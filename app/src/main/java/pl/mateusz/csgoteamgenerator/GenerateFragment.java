package pl.mateusz.csgoteamgenerator;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

public class GenerateFragment extends Fragment {
    private final ImageView[] playerImageViews = new ImageView[5];
    private final TextView[] playerTextViews = new TextView[5];
    private final Drawable[] playerImageDrawables = new Drawable[5];
    private final String[] playerTemporaryNicknames = new String[5];
    private RequestBuilder<Drawable> onClickDrawable;
    private boolean generating = false;
    private boolean wasGenerated = false;
    private final Handler handler = new Handler();

    /**
     * Asynchronous task used to get random player for each spot in generated team from database.
     * 3 cursors are used in background thread, one for each role.
     * There aren't any parameters for background thread. Array of strings is being passed to
     * onPostExecute, which launches PlayerImageURLTask, when all nicknames are obtained.
     */
    private class RandomNicknamesFromDatabaseTask extends AsyncTask<Void, Void, Player[]> {

        /**
         * @return String array with 5 random players from database
         * (index: 0 - sniper, 1-3 - riflers, 4 - igl)
         * Returns null if SQLiteException was thrown.
         */
        @Override
        protected Player[] doInBackground(Void... voids) {
            Player[] allSnipers = DataHandler
                    .getPlayersFromDatabase(Role.Sniper, getActivity());
            Player[] allRiflers = DataHandler
                    .getPlayersFromDatabase(Role.Rifler, getActivity());
            Player[] allIgls = DataHandler
                    .getPlayersFromDatabase(Role.IGL, getActivity());

            if (allSnipers == null || allRiflers == null || allIgls == null) {
                Log.e("ERR", "No players found with one of the roles");
                return null;
            }

            // get random sniper and igl from arrays and put them in results array
            Random rand = new Random();
            Player[] results = new Player[5];
            results[0] = allSnipers[rand.nextInt(allSnipers.length)];
            results[0].setIndex(0);
            results[4] = allIgls[rand.nextInt(allIgls.length)];
            results[4].setIndex(4);

            // we use method that gets random 3 riflers from cursor and puts them in an array
            // with igl and sniper (riflers are in indexes 1-3)
            return getRandomRiflersFromCursor(allRiflers, results);
        }

        @Override
        protected void onPostExecute(Player[] players) {
            if (players != null) {

                // "Niko" is a test case to avoid error while displaying first player
                new PlayerImageURLTask().execute(new Player("Niko", null, -1,
                        "LIQUIPEDIA"));

                for (Player player : players) {
                    Log.d("INFO", "Generated playername: " + player.getName());
                    if (!player.getImageSource().equals(ImageSource.CUSTOM.toString()))
                        // image source is either LIQUIPEDIA or DEFAULT
                        new PlayerImageURLTask().execute(player);
                    else
                        // image source is custom
                        new PlayerImageCustomTask().execute(player);
                }
            } else {
                Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Asynchronous task used to get image URL from Liquipedia page of the player using Jsoup
     * library. It returns that URL, or if there is no player photo, returns default HLTV player
     * image URL.
     * onPostExecute launches PlayerImageToDrawableTask, which converts URL into drawable and puts
     * it in the ImageView.
     */
    private class PlayerImageURLTask extends AsyncTask<Player, Void, String> {
        private Player player;
        /**
         * @param params information about player
         * @return url of player image from liquipedia (or hltv default photo if there's no picture
         * on liquipedia)
         * Returns null if there was thrown an exception.
         */
        @Override
        protected String doInBackground(Player... params) {
            player = params[0];
            if (player.getImageSource().equals(ImageSource.DEFAULT.toString()))
                return "https://i.imgur.com/KQDl2wD.png";
            return DataHandler.getPlayerImageUrl(player.getName());
        }

        @Override
        protected void onPostExecute(String imageURL) {

            // test case to avoid error while first attempt
            if (player.getIndex() == -1)
                return;

            if (imageURL != null) {
                Log.d("INFO", "Index URL: " + player.getIndex());
                player.setUrl(imageURL);
                new PlayerImageToDrawableTask().execute(player);
            } else {
                Toast.makeText(getActivity(), "Unable to load images", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Asynchronous task used to convert image URL to Drawable using InputStream. Then it puts that
     * drawable in ImageView and puts nick of the player in TextView.
     */
    private class PlayerImageToDrawableTask extends AsyncTask<Player, Void, Drawable> {
        private int index;
        private String name;

        /**
         *
         * @param params - information about player containing image URL
         * @return drawable with player image (or null if thrown exception)
         */
        @Override
        protected Drawable doInBackground(Player... params) {
            try {
                String url = params[0].getUrl();
                index = params[0].getIndex();
                name = params[0].getName();
                InputStream is = (InputStream) new URL(url).getContent();
                return Drawable.createFromStream(is, "src name");
            } catch (Exception e) {
                Log.e("EXC", "Error while getting drawable from image url", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable != null) {
                Log.d("INFO", "Index IMG: " + index);

                // if player nickname contains ("_Polish_player), it needs to be cut
                if (name.endsWith("_(Polish_player)"))
                    name = name.substring(0, name.length() - 16);

                // assign drawable and name to temporary arrays
                playerImageDrawables[index] = drawable;
                playerTemporaryNicknames[index] = name;
            } else {
                Toast.makeText(getActivity(), "Unable to load images", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class PlayerImageCustomTask extends AsyncTask<Player, Void, Bitmap> {
        Player player;

        @Override
        protected Bitmap doInBackground(Player... players) {
            player = players[0];
            SQLiteOpenHelper helper = new MyDatabaseHelper(getActivity());
            try {
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor imageCursor = db.query("AVATARS",
                        new String[]{"IMAGE"},
                        "NAME = ?",
                        new String[]{player.getName()},
                        null, null, null);
                if (imageCursor.moveToFirst()) {
                    byte[] imageByteArray = imageCursor.getBlob(0);
                    imageCursor.close();
                    db.close();
                    return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                }
                return null;
            } catch (SQLiteException e) {
                Log.e("ERR", "Error while getting bitmap image from database", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                // convert bitmap to drawable
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                // assign image and name to arrays (that will be later shown in Views)
                playerImageDrawables[player.getIndex()] = drawable;
                playerTemporaryNicknames[player.getIndex()] = player.getName();
            } else {
                Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_generate, container, false);
    }

    /**
     * On start of the fragment:
     * - set onClickListener for generateButton
     * - fill arrays with ImageViews (with player photos) and TextViews (with nicknames)
     * - Setup toolbar
     * - Load button animation through Glide library
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            FragmentTransaction ft2 = getFragmentManager().beginTransaction();
            ft2.replace(R.id.fragment_container, new GenerateFragment());
            ft2.commit();
        }

        ImageView generateButton = getActivity().findViewById(R.id.button_generate);
        generateButton.setOnClickListener(e -> onClickGenerate());

        assignViewToArrays();
        setupToolbar();

        // set navigation bar color
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.bar));
        getActivity().getWindow().setStatusBarColor(Color.BLACK);

        //load animation of the generateButton
        onClickDrawable = Glide.with(getActivity()).load("https://i.imgur.com/SoP7eUI.gif");
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Invoked when generateButton was clicked
     */
    private void onClickGenerate() {
        wasGenerated = true;
        if (!generating) {
            ImageView generateButton = getActivity().findViewById(R.id.button_generate);
            onClickDrawable.into(generateButton);
            new RandomNicknamesFromDatabaseTask().execute();
            generating = true;
            handler.postDelayed(delayedShowPlayersRunnable, 6400);
        }
    }

    private final Runnable delayedShowPlayersRunnable = new Runnable() {
        @Override
        public void run() {
            generating = false;
            // change generateButton gif to static image
            try {
                ImageView generateButton = getActivity().findViewById(R.id.button_generate);
                generateButton.setImageResource(R.drawable.generate_static);

                // change player textviews and imageviews
                for (int i = 0; i < 5; i++) {
                    playerTextViews[i].setText(playerTemporaryNicknames[i]);
                    playerImageViews[i].setImageDrawable(playerImageDrawables[i]);
                }
            } catch (NullPointerException e) {
                Log.e("ERR", "Activity doesn't exist - can't display player images", e);
            }

        }
    };

    /**
     * Gets 3 random records from cursor containing all riflers.
     * Also checks if these records are individual (not repeated)
     * @param allRiflers array containing all riflers from database
     * @param results string array containing sniper on index 0 and igl on index 4
     * @return string array filled with all randomized players
     */
    private Player[] getRandomRiflersFromCursor(Player[] allRiflers, Player[] results) {
        Random rand = new Random();
        int correctPlayers = 0;
        int index = 1;
        while (correctPlayers < 3) {
            Player tempPlayer = allRiflers[rand.nextInt(allRiflers.length)];
            boolean isNameRepeated = false;
            for (Player player : results) {
                if (player != null && tempPlayer.getName().equals(player.getName())) {
                    isNameRepeated = true;
                    break;
                }
            }
            if (!isNameRepeated) {
                tempPlayer.setIndex(index);
                results[index] = tempPlayer;
                correctPlayers++;
                index++;
            }
        }
        return results;
    }

    private void assignViewToArrays() {
        FragmentActivity rootActivity = getActivity();
        playerImageViews[0] = rootActivity.findViewById(R.id.frame_layout_player1)
                .findViewById(R.id.image_player);
        playerImageViews[1] = rootActivity.findViewById(R.id.frame_layout_player2)
                .findViewById(R.id.image_player);
        playerImageViews[2] = rootActivity.findViewById(R.id.frame_layout_player3)
                .findViewById(R.id.image_player);
        playerImageViews[3] = rootActivity.findViewById(R.id.frame_layout_player4)
                .findViewById(R.id.image_player);
        playerImageViews[4] = rootActivity.findViewById(R.id.frame_layout_player5)
                .findViewById(R.id.image_player);
        playerTextViews[0] = rootActivity.findViewById(R.id.text_nick1);
        playerTextViews[1] = rootActivity.findViewById(R.id.text_nick2);
        playerTextViews[2] = rootActivity.findViewById(R.id.text_nick3);
        playerTextViews[3] = rootActivity.findViewById(R.id.text_nick4);
        playerTextViews[4] = rootActivity.findViewById(R.id.text_nick5);
    }

    private void setupToolbar() {
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            toolbar.setTitle("Save Polish CS with your generated roster!");
        else
            toolbar.setTitle("Generate your team!");
        toolbar.setBackgroundColor(getResources().getColor(R.color.appbar_color));
        toolbar.setTitleTextColor(Color.WHITE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.size() == 0) {
            Log.d("MENU", "Created new menu");
            inflater.inflate(R.menu.menu_toolbar_generate, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (generating) {
            Toast.makeText(getActivity(), "Can't share while generating!", Toast.LENGTH_SHORT)
                    .show();
            return super.onOptionsItemSelected(item);
        } else if (!wasGenerated) {
            Toast.makeText(getActivity(), "Need to generate team in order to share!",
                            Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareText = "Check out the team that will save Polish CS:GO Scene!";
                StringBuilder builder = new StringBuilder(shareText);
                for (int i = 0; i < 5; i++) {
                    builder.append("\n").append(playerTextViews[i].getText());
                }
                builder.append("\n").append("Generated via CSTeamRoulette");
                intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                Intent chosenIntent = Intent.createChooser(intent, "Share roster via...");
                startActivity(chosenIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("restarting", true);
        super.onSaveInstanceState(outState);
    }
}