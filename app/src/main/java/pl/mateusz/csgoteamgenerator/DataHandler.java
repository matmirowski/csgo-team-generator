package pl.mateusz.csgoteamgenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Random;

public class DataHandler {
    public static final String liquipediaURL = "https://liquipedia.net/counterstrike/";

    public static Player[] getPlayersFromDatabase(Role role, Context context) {
        SQLiteOpenHelper helper = new MyDatabaseHelper(context);
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query("PLAYERS",
                    new String[]{"NAME, IMAGESRC"}, "ROLE = ?",
                    new String[]{role.toString()},
                    null, null, null);
            if (!cursor.moveToFirst()) {
                Log.e("PLAYERLIST", "Can't move to first record in cursor");
                return null;
            }
            Player[] players = new Player[cursor.getCount()];
            int i = 0;
            do {
                players[i] = new Player(cursor.getString(0), null, i, cursor.getString(1));
                i++;
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return players;
        } catch (SQLiteException e) {
            Log.e("ERR", "Error while setting up adapter", e);
            return null;
        }
    }

    public static String getPlayerImageUrl(String name) {
        String playerProfileUrl = liquipediaURL + name;

        // site scraping using Jsoup
        Element doc = getSiteHtmlHead(playerProfileUrl);
        if (doc == null) {
            return null;
        }

        // get only element with image url (index 12 of meta tags)
        String strMetaImg = doc.getElementsByTag("meta").get(12).toString();
        Log.d("INFO", "player meta: " + strMetaImg);
        int beginIndex = strMetaImg.indexOf("https");

        // if there is no player photo on liquipedia, default hltv photo is being used
        if (beginIndex == -1)
            return "https://i.imgur.com/KQDl2wD.png";
        int endIndex = strMetaImg.length() - 2;
        return strMetaImg.substring(beginIndex, endIndex);
    }

    public static Element getSiteHtmlHead(String playerProfileUrl) {
        try {
            // get random number to have different user-agent header each connection
            Random random = new Random();
            int randomVersion = random.nextInt(1000);
            int randomVersionAfterDot = random.nextInt(100);
            int randomTimeout = random.nextInt(100) + 1000;
            String userAgentHead = "Safari/5.0 (Macintosh; Intel Mac OS X 10_11_1) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/46.0.2454.101 Safari/" + randomVersion + "." + randomVersionAfterDot;
            return Jsoup
                    .connect(playerProfileUrl)
                    .timeout(randomTimeout)
                    .userAgent(userAgentHead)
                    .get()
                    .head();
        } catch (IOException e) {
            Log.e("EXC", "Error while getting image url from player profile", e);
            return null;
        }
    }

}
