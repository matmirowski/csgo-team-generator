package pl.mateusz.csgoteamgenerator.ListFragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import pl.mateusz.csgoteamgenerator.MyDatabaseHelper;
import pl.mateusz.csgoteamgenerator.R;
import pl.mateusz.csgoteamgenerator.Role;

public class Sniper2Fragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] names = getNamesFromDatabase(Role.Sniper);
        RecyclerView recycler = (RecyclerView) inflater.inflate(R.layout.fragment_sniper2, container, false);
//        return inflater.inflate(R.layout.fragment_sniper2, container, false);
        PlayerListAdapter adapter = new PlayerListAdapter(names);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        return recycler;
    }

    private String[] getNamesFromDatabase(Role role) { //todo returns null if no player with role

        SQLiteOpenHelper helper = new MyDatabaseHelper(getActivity());
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query("PLAYERS",
                    new String[]{"NAME"}, "ROLE = ?",
                    new String[]{role.toString()},
                    null, null, null);
            if (!cursor.moveToFirst()) {
                Log.e("PLAYERLIST", "Can't move to first record in cursor");
                return null;
            }
            String[] names = new String[cursor.getCount()];
            int i = 0;
            do {
                names[i] = cursor.getString(0);
                i++;
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return names;
        } catch (SQLiteException e) {
            Log.e("ERR", "Error while setting up adapter", e);
            return null;
        }
    }

}