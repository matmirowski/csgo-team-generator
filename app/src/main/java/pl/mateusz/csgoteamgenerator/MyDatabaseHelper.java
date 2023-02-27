package pl.mateusz.csgoteamgenerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "CSRoulette_Database"; //TODO CHANGE IT

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
        if (oldVersion < 1) { // players in tier1 teams
            // g2
            addPlayer(db, "HuNter-", Role.Rifler);
            addPlayer(db, "NiKo", Role.Rifler);
            addPlayer(db, "M0NESY", Role.Sniper);
            addPlayer(db, "Jks", Role.Rifler);
            addPlayer(db, "HooXi", Role.IGL);
            // heroic
            addPlayer(db, "CadiaN", Role.IGL);
            addPlayer(db, "Stavn", Role.Rifler);
            addPlayer(db, "TeSeS", Role.Rifler);
            addPlayer(db, "Sjuush", Role.Rifler);
            addPlayer(db, "Jabbi", Role.Rifler);
            // liquid
            addPlayer(db, "EliGE", Role.Rifler);
            addPlayer(db, "YEKINDAR", Role.Rifler);
            addPlayer(db, "NAF", Role.Rifler);
            addPlayer(db, "oSee", Role.Sniper);
            addPlayer(db, "Nitr0", Role.IGL);
            // faze clan
            addPlayer(db, "Karrigan", Role.IGL);
            addPlayer(db, "Rain", Role.Rifler);
            addPlayer(db, "Broky", Role.Sniper);
            addPlayer(db, "Twistzz", Role.Rifler);
            addPlayer(db, "Ropz", Role.Rifler);
            // navi
            addPlayer(db, "S1mple", Role.Sniper);
            addPlayer(db, "ElectroNic", Role.IGL);
            addPlayer(db, "Perfecto", Role.Rifler);
            addPlayer(db, "B1t", Role.Rifler);
            addPlayer(db, "Npl", Role.Rifler);
            // vitality
            addPlayer(db, "ApEX", Role.IGL);
            addPlayer(db, "ZywOo", Role.Sniper);
            addPlayer(db, "Dupreeh", Role.Rifler);
            addPlayer(db, "Magisk", Role.Rifler);
            addPlayer(db, "Spinx", Role.Rifler);
            // Outsiders
            addPlayer(db, "Jame", Role.Sniper);
            addPlayer(db, "Qikert", Role.Rifler);
            addPlayer(db, "FL1T", Role.Rifler);
            addPlayer(db, "Fame", Role.Rifler);
            addPlayer(db, "KaiR0N-", Role.Rifler);
            // OG
            addPlayer(db, "FlameZ", Role.Rifler);
            addPlayer(db, "F1KU", Role.Rifler);
            addPlayer(db, "NEOFRAG", Role.Rifler);
            addPlayer(db, "Degster", Role.Sniper);
            addPlayer(db, "Nexa", Role.IGL);
            // fnatic
            addPlayer(db, "KRIMZ", Role.Rifler);
            addPlayer(db, "mezii", Role.IGL);
            addPlayer(db, "Nicoodoz", Role.Sniper);
            addPlayer(db, "RoeJ", Role.Rifler);
            addPlayer(db, "FASHR", Role.Rifler);
            // complexity
            addPlayer(db, "JT_(South_African_player)", Role.IGL);
            addPlayer(db, "FaNg", Role.Rifler);
            addPlayer(db, "Floppy", Role.Rifler);
            addPlayer(db, "Grim", Role.Rifler);
            addPlayer(db, "Hallzerk", Role.Sniper);
            // mouz
            addPlayer(db, "Frozen", Role.Rifler);
            addPlayer(db, "Dexter", Role.IGL);
            addPlayer(db, "Torzsi", Role.Sniper);
            addPlayer(db, "JDC", Role.Rifler);
            addPlayer(db, "XertioN", Role.Rifler);
            // spirit
            addPlayer(db, "Chopper", Role.IGL);
            addPlayer(db, "Magixx", Role.Rifler);
            addPlayer(db, "Patsi", Role.Rifler);
            addPlayer(db, "S1ren", Role.Rifler);
            addPlayer(db, "W0nderful", Role.Sniper);
            // cloud9
            addPlayer(db, "HObbit", Role.Rifler);
            addPlayer(db, "Nafany", Role.IGL);
            addPlayer(db, "Sh1ro", Role.Sniper);
            addPlayer(db, "Ax1Le", Role.Rifler);
            addPlayer(db, "Buster", Role.Rifler);
            // furia
            addPlayer(db, "ArT", Role.IGL);
            addPlayer(db, "Saffee", Role.Sniper);
            addPlayer(db, "Drop", Role.Rifler);
            addPlayer(db, "KSCERATO", Role.Rifler);
            addPlayer(db, "Yuurih", Role.Rifler);
            // astralis
            addPlayer(db, "Xyp9x", Role.Rifler);
            addPlayer(db, "Dev1ce", Role.Sniper);
            addPlayer(db, "Gla1ve", Role.IGL);
            addPlayer(db, "BlameF", Role.Rifler);
            addPlayer(db, "Buzz", Role.Rifler);
            // nip
            addPlayer(db, "Aleksib", Role.IGL);
            addPlayer(db, "REZ", Role.Rifler);
            addPlayer(db, "Brollan", Role.Rifler);
            addPlayer(db, "Headtr1ck", Role.Sniper);
            addPlayer(db, "K0nfig", Role.Rifler);
            // big
            addPlayer(db, "TabseN", Role.IGL);
            addPlayer(db, "SyrsoN", Role.Sniper);
            addPlayer(db, "FaveN", Role.Rifler);
            addPlayer(db, "Krimbo", Role.Rifler);
            addPlayer(db, "K1to", Role.Rifler);
            // ence
            addPlayer(db, "Snappi", Role.IGL);
            addPlayer(db, "Dycha", Role.Rifler);
            addPlayer(db, "Maden", Role.Rifler);
            addPlayer(db, "SunPayus", Role.Sniper);
            addPlayer(db, "NertZ", Role.Rifler);
            // eternal
            addPlayer(db, "XANTARES", Role.Rifler);
            addPlayer(db, "Calyx", Role.Rifler);
            addPlayer(db, "ImoRR", Role.Sniper);
            addPlayer(db, "Xfl0ud", Role.Rifler);
            addPlayer(db, "MAJ3R", Role.IGL);
        }
        if (oldVersion < 2) { // other players
            addPlayer(db, "Hades_(Polish_player)", Role.Sniper);
            addPlayer(db, "Refrezh", Role.Rifler);
            addPlayer(db, "Siuhy", Role.IGL);
            addPlayer(db, "AcoR", Role.Sniper);
            addPlayer(db, "Snax", Role.Rifler);
            addPlayer(db, "FalleN", Role.Sniper);
            addPlayer(db, "Coldzera", Role.Rifler);
            addPlayer(db, "KennyS", Role.Sniper);
            addPlayer(db, "Hampus", Role.Rifler);
            addPlayer(db, "Bubzkji", Role.Rifler);
            addPlayer(db, "Shox", Role.Rifler);
            addPlayer(db, "Boombl4", Role.IGL);
            addPlayer(db, "ANNIHILATION", Role.Sniper);
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE AVATARS(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "IMAGE BLOB)");
        }
    }

    /**
     * Adds player to database with default ImageSource (liquipedia)
     * @param db SQLite database
     * @param name player's name
     * @param role player's role
     */
    public void addPlayer(SQLiteDatabase db, String name, Role role) {
        // IMAGESRC column is default = "LIQUIPEDIA"
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("ROLE", role.toString());
        values.put("IMAGESRC", "LIQUIPEDIA");
        db.insert("PLAYERS", null, values);
    }

    /**
     * Adds player to database
     * @param db SQLite database
     * @param name player's name
     * @param role player's role
     * @param imgSrc player's image source
     */
    public void addPlayer(SQLiteDatabase db, String name, Role role, ImageSource imgSrc) {
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("ROLE", role.toString());
        values.put("IMAGESRC", imgSrc.toString());
        db.insert("PLAYERS", null, values);
        Log.d("DATABASE", "Added player to database: " + name + " role: " +
                role.toString() + " img: " + imgSrc.toString());
    }

    /**
     * Adds picture of the player to AVATARS table in database (saves it as byte array)
     * @param db SQLite database
     * @param name player's name
     * @param img player's picture as byte array
     */
    public void addAvatar(SQLiteDatabase db, String name, byte[] img) {
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("IMAGE", img);
        db.insert("AVATARS", null, values);
        Log.d("DATABASE", "Added image to database: " + name);
    }

    /**
     * Removes player from database (and his avatar if exists)
     * @param db SQLite databse
     * @param name player's name
     * @param imgSrc player's image source
     */
    public void removePlayer(SQLiteDatabase db, String name, String imgSrc) {
        if (imgSrc.equals("CUSTOM")) {
            db.delete("AVATARS", "NAME = ?", new String[]{name});
        }
        db.delete("PLAYERS", "NAME = ?", new String[]{name});
        Log.d("DATABASE", "Removed player: " + name);
    }
}
