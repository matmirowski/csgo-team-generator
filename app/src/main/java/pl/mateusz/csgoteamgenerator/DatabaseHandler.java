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
import java.net.MalformedURLException;
import java.net.URL;

public class DatabaseHandler {

    public static Player[] getPlayersFromDatabase(Role role, Context context) { //todo returns null if no player with role

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

}
