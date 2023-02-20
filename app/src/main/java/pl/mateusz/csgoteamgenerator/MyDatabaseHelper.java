package pl.mateusz.csgoteamgenerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "test7"; //TODO

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PLAYERS(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT," +
                "ROLE TEXT, " +
                "IMAGESRC TEXT)");
        updateDatabase(db, 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 1) { // players with photos
            addPlayer(db, "Sidney", Role.IGL);
            addPlayer(db, "phr", Role.IGL);
            addPlayer(db, "Oskarish", Role.IGL);
            addPlayer(db, "SZPERO", Role.IGL);
            addPlayer(db, "Snax", Role.Rifler);
            addPlayer(db, "Furlan", Role.Rifler);
            addPlayer(db, "MICHU", Role.Rifler);
            addPlayer(db, "GruBy", Role.Rifler);
            addPlayer(db, "Byali", Role.Rifler);
            addPlayer(db, "KEi", Role.Rifler);
            addPlayer(db, "Innocent", Role.Rifler);
            addPlayer(db, "Vegi", Role.Rifler);
            addPlayer(db, "Sobol", Role.Rifler);
            addPlayer(db, "Leman", Role.Rifler);
            addPlayer(db, "Goofy", Role.Rifler);
            addPlayer(db, "TOAO", Role.Rifler);
            addPlayer(db, "Mouz_(Polish_player)", Role.Rifler);
            addPlayer(db, "Rallen", Role.Rifler);
            addPlayer(db, "Reatz", Role.Rifler);
            addPlayer(db, "Demho", Role.Rifler);
            addPlayer(db, "KRaSnaL", Role.Rifler);
            addPlayer(db, "TaZ", Role.Rifler);
            addPlayer(db, "Snatchie", Role.Sniper);
            addPlayer(db, "TudsoN", Role.Sniper);
            addPlayer(db, "MINISE", Role.Sniper);
            addPlayer(db, "MhL", Role.Sniper);
            addPlayer(db, "Hades_(Polish_player)", Role.Sniper);
        }
        if (oldVersion < 2) { // players with no photo on liquipedia
            addPlayer(db, "Mynio", Role.IGL);
            addPlayer(db, "SNx", Role.IGL);
            addPlayer(db, "LunAtic", Role.IGL);
            addPlayer(db, "Markoś", Role.Sniper);
            addPlayer(db, "M4tthi", Role.Sniper);
            addPlayer(db, "MASKED", Role.Sniper);
            addPlayer(db, "Layner", Role.Sniper);
            addPlayer(db, "Fr3nd", Role.Sniper);
            addPlayer(db, "Kylar", Role.Rifler);
            addPlayer(db, "Reiko", Role.Rifler);
            addPlayer(db, "Jedqr", Role.Rifler);
            addPlayer(db, "Ponczek", Role.Rifler);
            addPlayer(db, "Bnox", Role.Rifler);
            addPlayer(db, "ZaNNN", Role.Rifler);
            addPlayer(db, "Iso", Role.Rifler);
            addPlayer(db, "Casey", Role.Rifler);
            addPlayer(db, "OLIMP", Role.Rifler);
            addPlayer(db, "Darko", Role.Rifler);
            addPlayer(db, "Prism", Role.Rifler);
            addPlayer(db, "Enzo", Role.Rifler);
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE AVATARS(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "IMAGE BLOB)");
        }
    }

    public void addPlayer(SQLiteDatabase db, String name, Role role) {
        // IMAGESRC column is default = "LIQUIPEDIA"
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("ROLE", role.toString());
        values.put("IMAGESRC", "LIQUIPEDIA");
        db.insert("PLAYERS", null, values);
    }

    public void addPlayer(SQLiteDatabase db, String name, Role role, ImageSource imgSrc) {
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("ROLE", role.toString());
        values.put("IMAGESRC", imgSrc.toString());
        db.insert("PLAYERS", null, values);
        Log.d("DATABASE", "Added player to database: " + name + " role: " +
                role.toString() + " img: " + imgSrc.toString());
    }

    public void addAvatar(SQLiteDatabase db, String name, byte[] img) {
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("IMAGE", img);
        db.insert("AVATARS", null, values);
        Log.d("DATABASE", "Added image to database: " + name);
    }

    public void removePlayer(SQLiteDatabase db, String name, ImageSource imgSrc) {
        if (imgSrc == ImageSource.CUSTOM) {
            db.delete("AVATARS", "NAME = ?", new String[]{name});
        }
        db.delete("PLAYERS", "NAME = ?", new String[]{name});
        Log.d("DATABASE", "Removed player: " + name);
    }
}
