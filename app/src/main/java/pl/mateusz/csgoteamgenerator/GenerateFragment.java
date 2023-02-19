package pl.mateusz.csgoteamgenerator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    //TODO round button corners?
    public static final String liquipediaURL = "https://liquipedia.net/counterstrike/";
    private final ImageView[] playerImageViews = new ImageView[5];
    private final TextView[] playerTextViews = new TextView[5];
    private final Drawable[] playerImageDrawables = new Drawable[5];
    private final String[] playerTemporaryNicknames = new String[5];
    private RequestBuilder<Drawable> onClickDrawable;
    private boolean generating = false;
    private boolean wasGenerated = false;
    private final Handler handler = new Handler();

    /**
     * Represents basic information about the player, used in AsyncTasks to pass information
     * about the player to another task.
     */
    private static class Player {
        String name;
        String url;
        int index;

        /**
         * Creates a player.
         * @param name nickname of a player
         * @param url url address of player's image (can be null)
         * @param index index of location, where player should appear (0 - sniper, 1-3 - rifler,
         *             4 - igl)
         */
        public Player(String name, String url, int index) {
            this.name = name;
            this.url = url;
            this.index = index;
        }
    }

    /**
     * Asynchronous task used to get random player for each spot in generated team from database.
     * 3 cursors are used in background thread, one for each role.
     * There aren't any parameters for background thread. Array of strings is being passed to
     * onPostExecute, which launches PlayerImageURLTask, when all nicknames are obtained.
     */
    private class RandomNicknamesFromDatabaseTask extends AsyncTask<Void, Void, String[]> {

        /**
         * @return String array with 5 random players from database
         * (index: 0 - sniper, 1-3 - riflers, 4 - igl)
         * Returns null if SQLiteException was thrown.
         */
        @Override
        protected String[] doInBackground(Void... voids) {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getActivity());
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                // cursors containing all players with specific role
                Cursor iglCursor = db.query("PLAYERS",
                        new String[]{"NAME"},
                        "ROLE = ?",
                        new String[] {"IGL"},
                        null, null, null);
                Cursor riflerCursor = db.query("PLAYERS",
                        new String[]{"NAME"},
                        "ROLE = ?",
                        new String[] {"Rifler"},
                        null, null, null);
                Cursor sniperCursor = db.query("PLAYERS",
                        new String[]{"NAME"},
                        "ROLE = ?",
                        new String[] {"Sniper"},
                        null, null, null);
                Random rand = new Random();

                // move cursors to random player from query
                iglCursor.moveToFirst();
                iglCursor.move(rand.nextInt(iglCursor.getCount()));
                sniperCursor.moveToFirst();
                sniperCursor.move(rand.nextInt(sniperCursor.getCount()));

                // temporary array containing sniper and igl
                String[] results = new String[5];
                results[0] = sniperCursor.getString(0);
                results[4] = iglCursor.getString(0);

                // we use method that gets random 3 riflers from cursor and puts them in an array
                // with igl and sniper (riflers are in indexes 1-3)
                String[] finalResults = getRandomRiflersFromCursor(riflerCursor, results);
                iglCursor.close();
                sniperCursor.close();
                riflerCursor.close();
                db.close();
                return finalResults;
            } catch (SQLiteException e) {
                Log.e("ERR", "Error while getting names from database", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] names) {
            if (names != null) {
                // "Niko" is a test case to avoid error while displaying first player
                new PlayerImageURLTask().execute(new Player("Niko", null, -1));
                for (int i = 0; i < 5; i++) {
                    Log.d("INFO", "Generated playername: " + names[i]);
                    new PlayerImageURLTask().execute(new Player(names[i], null, i));
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
        private int index;
        private String name;

        /**
         * @param params information about player
         * @return url of player image from liquipedia (or hltv default photo if there's no picture
         * on liquipedia)
         * Returns null if there was thrown an exception.
         */
        @Override
        protected String doInBackground(Player... params) {
            try {
                name = params[0].name;
                index = params[0].index;
                String playerProfileURL = liquipediaURL + name;

                // site scraping using Jsoup
                Element doc = Jsoup
                        .connect(playerProfileURL)
                        .timeout(1000)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/45.0.2454.101 Safari/537.36")
                        .get().head();

                // get only element with image url (index 12 of meta tags)
                String strMetaImg = doc.getElementsByTag("meta").get(12).toString();
                Log.d("INFO", "player meta: " + strMetaImg);
                int beginIndex = strMetaImg.indexOf("https");

                // if there is no player photo on liquipedia, default hltv photo is being used
                if (beginIndex == -1)
                    return "https://i.imgur.com/KQDl2wD.png";
                int endIndex = strMetaImg.length() - 2;
                return strMetaImg.substring(beginIndex, endIndex);
            } catch (IOException e) {
                Log.e("EXC", "Error while getting image url from player profile", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String imageURL) {
            if (index == -1) // test case to avoid error while first attempt
                return;
            if (imageURL != null) {
                Log.d("INFO", "Index URL: " + index);
                new PlayerImageToDrawableTask().execute(new Player(name, imageURL, index));
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
                String url = params[0].url;
                index = params[0].index;
                name = params[0].name;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onStart() {
        super.onStart();
        ImageView generateButton = getActivity().findViewById(R.id.button_generate);
        generateButton.setOnClickListener(e -> onClickGenerate());

        assignViewToArrays();
        setupToolbar();

        // set navigation bar color
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.bar));
        getActivity().getWindow().setStatusBarColor(Color.BLACK);

        //load animation of the generateButton
        onClickDrawable = Glide.with(getActivity()).load("https://i.imgur.com/SoP7eUI.gif");
    }

    /**
     * Invoked while generateButton was clicked
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
            ImageView generateButton = getActivity().findViewById(R.id.button_generate);
            generateButton.setImageResource(R.drawable.generate_static);

            // change player textviews and imageviews
            for (int i = 0; i < 5; i++) {
                playerTextViews[i].setText(playerTemporaryNicknames[i]);
                playerImageViews[i].setImageDrawable(playerImageDrawables[i]);
            }
        }
    };

    /**
     * Gets 3 random records from cursor containing all riflers.
     * Also checks if these records are individual (not repeated)
     * @param cursor cursor containing all riflers from database
     * @param results string array containing sniper on index 0 and igl on index 4
     * @return string array filled with all randomized players
     */
    private String[] getRandomRiflersFromCursor(Cursor cursor, String[] results) {
        Random rand = new Random();
        int correctPlayers = 0;
        int index = 1;
        while (correctPlayers < 3) {
            cursor.moveToFirst();
            cursor.move(rand.nextInt(cursor.getCount()));
            String tempName = cursor.getString(0);
            boolean isNameRepeated = false;
            for (String name : results) {
                if (tempName.equals(name)) {
                    isNameRepeated = true;
                    break;
                }
            }
            if (!isNameRepeated) {
                results[index] = tempName;
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
        toolbar.setTitle("Save Polish CS with your generated roster!");
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
                String shareText = "Check out my new team, which will save Polish CS:GO Scene!";
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
}