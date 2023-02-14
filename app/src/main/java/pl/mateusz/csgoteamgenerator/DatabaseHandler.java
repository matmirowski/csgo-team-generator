package pl.mateusz.csgoteamgenerator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DatabaseHandler {
    private static final String liquipediaURL = "https://liquipedia.net/counterstrike/";

    public static String[] getAllRolePlayerNames(Role role, FragmentActivity activity) {
        String[] strRole = {role.toString()};
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(activity);
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            // cursors containing all players with specific role
            Cursor roleCursor = db.query("PLAYERS",
                    new String[]{"NAME"},
                    "ROLE = ?",
                    strRole,
                    null, null, null);
            roleCursor.moveToFirst();
            int index = 0;
            String[] names = new String[roleCursor.getCount()];
            while (true) {
                boolean shouldContinue = roleCursor.moveToNext();
                if (!shouldContinue)
                    break;
                names[index] = roleCursor.getString(0);
            }
            roleCursor.close();
            db.close();
            return names;
        } catch (SQLiteException e) {
            Log.e("ERR", "DatabaseHandler: Error while getting all players " +
                    "from specific role", e);
            return null;
        }
    }

    public static Drawable getDrawableFromNickname(String name) {
        String url = getURLFromNickName(name);
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (IOException e) {
            return null;
        }
    }

    private static String getURLFromNickName(String name) {
        try {
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
                return "https://www.hltv.org/img/static/player/player_silhouette.png";
            int endIndex = strMetaImg.length() - 2;
            return strMetaImg.substring(beginIndex, endIndex);
        } catch (IOException e) {
            Log.e("EXC", "Error while getting image url from player profile", e);
            return null;
        }
    }

}
