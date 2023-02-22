package pl.mateusz.csgoteamgenerator.ListFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class IglFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get all players with specified role
        Player[] players = DataHandler.getPlayersFromDatabase(Role.IGL, getActivity());

        // if there is no players with such role, then we just inflate default layout //todo
        if (players == null) {
            Toast.makeText(getActivity(), "Can't access players from database", Toast.LENGTH_SHORT)
                    .show();
            return inflater.inflate(R.layout.fragment_player_list_role, container, false);
        }
        RecyclerView recycler = (RecyclerView) inflater.inflate(R.layout.fragment_player_list_role,
                container, false);
        PlayerListAdapter adapter = new PlayerListAdapter(players, getActivity());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        recycler.setBackground(getResources().getDrawable(R.drawable.listback_igl));
        return recycler;
    }
}