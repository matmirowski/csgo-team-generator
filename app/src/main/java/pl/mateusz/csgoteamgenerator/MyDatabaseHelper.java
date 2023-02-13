package pl.mateusz.csgoteamgenerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = null; //TODO

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
        switch (oldVersion) {
            case 0:
                //addRecord(db, "Mynio", Role.IGL);
                addRecord(db, "Sidney", Role.IGL);
                addRecord(db, "Snax", Role.Rifler);
                addRecord(db, "Furlan", Role.Rifler);
                addRecord(db, "Oskarish", Role.Rifler);
                addRecord(db, "phr", Role.Rifler);
                addRecord(db, "MICHU", Role.Rifler);
                addRecord(db, "GruBy", Role.Rifler);
                addRecord(db, "Snatchie", Role.Sniper);
                addRecord(db, "Markoś", Role.Sniper);
        }
    }

    private void addRecord(SQLiteDatabase db, String name, Role role) {
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("ROLE", role.toString());
        db.insert("PLAYERS", null, values);
    }
}
