package pl.mateusz.csgoteamgenerator.ListFragments;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import pl.mateusz.csgoteamgenerator.DataHandler;
import pl.mateusz.csgoteamgenerator.Player;
import pl.mateusz.csgoteamgenerator.R;
import pl.mateusz.csgoteamgenerator.Role;

public abstract class AbstractRoleFragment extends Fragment {
    protected View initialSetup(Role role, LayoutInflater inflater, ViewGroup container) {
        // get all players with specified role
        Player[] players = DataHandler.getPlayersFromDatabase(role, getActivity());

        // setup recycler
        RecyclerView recycler = (RecyclerView) inflater.inflate(R.layout.fragment_player_list_role,
                container, false);
        PlayerListAdapter adapter = new PlayerListAdapter(players, getActivity());
        recycler.setAdapter(adapter);

        // amount of images in one row depends on orientation
        int orientation = getActivity().getResources().getConfiguration().orientation;
        int imagesInOneRow;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            imagesInOneRow = 2;
        else
            imagesInOneRow = 5;
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), imagesInOneRow));

        Drawable backgroundDrawable = null;
        switch (role) {
            case Sniper:
                backgroundDrawable = getResources().getDrawable(R.drawable.listback_sniper);
                break;
            case Rifler:
                backgroundDrawable = getResources().getDrawable(R.drawable.listback_rifler);
                break;
            case IGL:
                backgroundDrawable = getResources().getDrawable(R.drawable.listback_igl);
                break;
        }
        recycler.setBackground(backgroundDrawable);

        // if there is no players with such role, then we just inflate default layout //todo
        if (players == null) {
            Toast.makeText(getActivity(), "Can't access players from database", Toast.LENGTH_SHORT)
                    .show();
            return inflater.inflate(R.layout.fragment_player_list_role, container, false);
        }

        return recycler;
    }
}
