package pl.mateusz.csgoteamgenerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "test5"; //TODO

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PLAYERS(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT," +
                "ROLE TEXT)");
        updateDatabase(db, 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 1) { // players with photos
            addRecord(db, "Sidney", Role.IGL);
            addRecord(db, "phr", Role.IGL);
            addRecord(db, "Oskarish", Role.IGL);
            addRecord(db, "SZPERO", Role.IGL);
            addRecord(db, "Snax", Role.Rifler);
            addRecord(db, "Furlan", Role.Rifler);
            addRecord(db, "MICHU", Role.Rifler);
            addRecord(db, "GruBy", Role.Rifler);
            addRecord(db, "Byali", Role.Rifler);
            addRecord(db, "KEi", Role.Rifler);
            addRecord(db, "Innocent", Role.Rifler);
            addRecord(db, "Vegi", Role.Rifler);
            addRecord(db, "Sobol", Role.Rifler);
            addRecord(db, "Leman", Role.Rifler);
            addRecord(db, "Goofy", Role.Rifler);
            addRecord(db, "TOAO", Role.Rifler);
            addRecord(db, "Mouz_(Polish_player)", Role.Rifler);
            addRecord(db, "Rallen", Role.Rifler);
            addRecord(db, "Reatz", Role.Rifler);
            addRecord(db, "Demho", Role.Rifler);
            addRecord(db, "KRaSnaL", Role.Rifler);
            addRecord(db, "TaZ", Role.Rifler);
            addRecord(db, "Snatchie", Role.Sniper);
            addRecord(db, "TudsoN", Role.Sniper);
            addRecord(db, "MINISE", Role.Sniper);
            addRecord(db, "MhL", Role.Sniper);
            addRecord(db, "Hades_(Polish_player)", Role.Sniper);
        }
        if (oldVersion < 2) { // players with no photo on liquipedia
            addRecord(db, "Mynio", Role.IGL);
            addRecord(db, "SNx", Role.IGL);
            addRecord(db, "LunAtic", Role.IGL);
            addRecord(db, "Markoś", Role.Sniper);
            addRecord(db, "M4tthi", Role.Sniper);
            addRecord(db, "MASKED", Role.Sniper);
            addRecord(db, "Layner", Role.Sniper);
            addRecord(db, "Fr3nd", Role.Sniper);
            addRecord(db, "Kylar", Role.Rifler);
            addRecord(db, "Reiko", Role.Rifler);
            addRecord(db, "Jedqr", Role.Rifler);
            addRecord(db, "Ponczek", Role.Rifler);
            addRecord(db, "Bnox", Role.Rifler);
            addRecord(db, "ZaNNN", Role.Rifler);
            addRecord(db, "Iso", Role.Rifler);
            addRecord(db, "Casey", Role.Rifler);
            addRecord(db, "OLIMP", Role.Rifler);
            addRecord(db, "Darko", Role.Rifler);
            addRecord(db, "Prism", Role.Rifler);
            addRecord(db, "Enzo", Role.Rifler);
        }
        if (oldVersion < 3) { // new column containing image source
            db.execSQL("ALTER TABLE PLAYERS ADD IMAGESRC TEXT NOT NULL DEFAULT 'LIQUIPEDIA'");
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE AVATARS(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "IMAGE BLOB)");
        }
    }

    public void addRecord(SQLiteDatabase db, String name, Role role) {
        // IMAGESRC column is default = "LIQUIPEDIA"
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("ROLE", role.toString());
        db.insert("PLAYERS", null, values);
    }

    public void addRecord(SQLiteDatabase db, String name, Role role, ImageSource imgSrc) {
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
}
