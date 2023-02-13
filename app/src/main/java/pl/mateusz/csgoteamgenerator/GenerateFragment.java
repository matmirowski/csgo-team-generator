package pl.mateusz.csgoteamgenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateFragment extends Fragment {
    private static final String liquipediaURL = "https://liquipedia.net/counterstrike/";
    private ImageView[] playerImageViews = new ImageView[5];
    private TextView[] playerTextViews = new TextView[5];

    private class TaskParams {
        String name;
        int index;

        public TaskParams(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }

    private class RandomNicknamesFromDatabaseTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getActivity());
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
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
                // 3 riflers are needed from cursor, so we are looking for them in loop
                String[] results = new String[5];
                results[0] = sniperCursor.getString(0);
                results[4] = iglCursor.getString(0);
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
                for (int i = 0; i < 5; i++) {
                    Log.d("INFO", "Generated playername: " + names[i]);
                    new PlayerImageURLTask().execute(new TaskParams(names[i], i));
                    playerTextViews[i].setText(names[i]);
                }
            } else {
                Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class PlayerImageURLTask extends AsyncTask<TaskParams, Void, String> {
        int index;

        @Override
        protected String doInBackground(TaskParams... params) {
            try {
                String playerProfileURL = liquipediaURL + params[0].name;
                index = params[0].index;
                Element doc = Jsoup
                        .connect(playerProfileURL)
                        .timeout(1000) //TODO test value
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/45.0.2454.101 Safari/537.36")
                        .get().head();

                String strMetaImg = doc.getElementsByTag("meta").get(12).toString();
                Log.d("INFO", "player meta: " + strMetaImg);
                int beginIndex = strMetaImg.indexOf("https");
                // if there is no player photo on liquipedia, default hltv photo is being used
                if (beginIndex == -1)
                    return "https://www.hltv.org/img/static/player/player_silhouette.png";
                int endIndex = strMetaImg.length() - 2;
                return strMetaImg.substring(beginIndex, endIndex);
            } catch (IOException e) {
                Log.e("EXC", "Error while getting image url from player profile", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String imageURL) {
            if (imageURL != null) {
                Log.d("INFO", "Index URL: " + index);
                new PlayerImageToDrawableTask().execute(new TaskParams(imageURL, index));
            } else {
                Toast.makeText(getActivity(), "Unable to load images", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class PlayerImageToDrawableTask extends AsyncTask<TaskParams, Void, Drawable> {
        int index;

        @Override
        protected Drawable doInBackground(TaskParams... params) {
            try {
                String url = params[0].name;
                index = params[0].index;
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
                playerImageViews[index].setImageDrawable(drawable);
            } else {
                Toast.makeText(getActivity(), "Unable to load images", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generate, container, false);
    }

    @Override
    public void onStart() {
        FragmentActivity rootActivity = getActivity();
        ImageButton generateButton = rootActivity.findViewById(R.id.button_generate);
        generateButton.setOnClickListener(e -> onClickGenerate());
        playerImageViews[0] = rootActivity.findViewById(R.id.image_player1);
        playerImageViews[1] = rootActivity.findViewById(R.id.image_player2);
        playerImageViews[2] = rootActivity.findViewById(R.id.image_player3);
        playerImageViews[3] = rootActivity.findViewById(R.id.image_player4);
        playerImageViews[4] = rootActivity.findViewById(R.id.image_player5);
        playerTextViews[0] = rootActivity.findViewById(R.id.text_nick1);
        playerTextViews[1] = rootActivity.findViewById(R.id.text_nick2);
        playerTextViews[2] = rootActivity.findViewById(R.id.text_nick3);
        playerTextViews[3] = rootActivity.findViewById(R.id.text_nick4);
        playerTextViews[4] = rootActivity.findViewById(R.id.text_nick5);
        super.onStart();
    }

    private void onClickGenerate() {
        new RandomNicknamesFromDatabaseTask().execute();
    }

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
}