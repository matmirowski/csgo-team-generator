package pl.mateusz.csgoteamgenerator.ListFragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import pl.mateusz.csgoteamgenerator.GenerateFragment;
import pl.mateusz.csgoteamgenerator.ImageSource;
import pl.mateusz.csgoteamgenerator.MyDatabaseHelper;
import pl.mateusz.csgoteamgenerator.Player;
import pl.mateusz.csgoteamgenerator.R;
import pl.mateusz.csgoteamgenerator.Role;

public class Sniper2Fragment extends Fragment {
    Player[] players;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        players = getPlayersFromDatabase(Role.Sniper);
        if (players == null) {
            Toast.makeText(getActivity(), "Can't access players from database", Toast.LENGTH_SHORT)
                    .show();
            return inflater.inflate(R.layout.fragment_sniper2, container, false);
        }
        RecyclerView recycler = (RecyclerView) inflater.inflate(R.layout.fragment_sniper2, container, false);
        PlayerListAdapter adapter = new PlayerListAdapter(players);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        return recycler;
    }


    private Player[] getPlayersFromDatabase(Role role) { //todo returns null if no player with role

        SQLiteOpenHelper helper = new MyDatabaseHelper(getActivity());
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