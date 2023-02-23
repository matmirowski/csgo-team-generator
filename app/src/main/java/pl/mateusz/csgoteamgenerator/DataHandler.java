package pl.mateusz.csgoteamgenerator;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Random;

public class DataHandler {
    /** Liquipedia player profile URL used to scrape website to get player image url*/
    public static final String liquipediaURL = "https://liquipedia.net/counterstrike/";

    /**
     * Finds all players from database that have the same role as stated in params
     * @param role role of players that we want to get from database
     * @param context context of application
     * @return array of all Player objects with role stated in params
     */
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

            // create Player instances and put them in array
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

    /**
     * Finds url of player's image from Liquipedia profile page. Uses Jsoup library to scrape site.
     * @param name name of the player
     * @return url of player's image
     */
    public static String getPlayerImageUrl(String name) {
        String playerProfileUrl = liquipediaURL + name;

        // site scraping using Jsoup to get html head
        Element head = getSiteHtmlHead(playerProfileUrl);
        if (head == null) {
            return null;
        }

        // get only element with image url (index 12 of meta tags)
        String strMetaImg = head.getElementsByTag("meta").get(12).toString();
        Log.d("INFO", "player meta: " + strMetaImg);
        int beginIndex = strMetaImg.indexOf("https");

        // if there is no player photo on liquipedia, default hltv photo is being used
        if (beginIndex == -1)
            return "https://i.imgur.com/KQDl2wD.png";
        int endIndex = strMetaImg.length() - 2;
        return strMetaImg.substring(beginIndex, endIndex);
    }

    /**
     * Finds HTML head of player's Liquipedia player profile using Jsoup library
     * @param playerProfileUrl url of player's profile
     * @return HTML head of player's profile
     */
    public static Element getSiteHtmlHead(String playerProfileUrl) {
        try {
            // random numbers to have different user-agent header each connection to avoid ban
            Random random = new Random();
            int randomVersion = random.nextInt(1000);
            int randomVersionAfterDot = random.nextInt(100);
            // random timeout to avoid ban
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

    public static Bitmap getPlayerImageAsBitmap(Player player, Activity activity) {
        SQLiteOpenHelper helper = new MyDatabaseHelper(activity);
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

    /**
     * Converts bitmap to byte array
     * @param bitmap bitmap to be converted to byte array
     * @return byte array that represents bitmap
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            Log.e("ERR", "Can't convert bitmap to byte array", e);
            return null;
        }
    }

    public static boolean removePlayer(String playerName, String imageSource, Activity activity) {
        MyDatabaseHelper helper = new MyDatabaseHelper(activity);
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            helper.removePlayer(db, playerName, imageSource);
            db.close();
            return true;
        } catch (SQLiteException e) {
            Log.e("DataHandler", "Can't remove player");
            return false;
        }
    }
}
