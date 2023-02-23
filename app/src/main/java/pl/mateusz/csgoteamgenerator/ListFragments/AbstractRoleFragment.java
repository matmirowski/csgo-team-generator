package pl.mateusz.csgoteamgenerator.ListFragments;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import pl.mateusz.csgoteamgenerator.DataHandler;
import pl.mateusz.csgoteamgenerator.MyDatabaseHelper;
import pl.mateusz.csgoteamgenerator.Player;
import pl.mateusz.csgoteamgenerator.R;
import pl.mateusz.csgoteamgenerator.Role;

public abstract class AbstractRoleFragment extends Fragment {

    /**
     * Initial setup of the fragment, invoked in all child fragments (SniperFragment etc)
     * @param role role of players displayed in List
     * @param inflater layoutInflater of the layout
     * @param container container holding current layout
     * @return RecyclerView with added adapters etc
     */
    protected View initialSetup(Role role, LayoutInflater inflater, ViewGroup container) {
        // get all players with specified role
        Player[] players = DataHandler.getPlayersFromDatabase(role, getActivity());

        // setup recycler
        RecyclerView recycler = (RecyclerView) inflater.inflate(R.layout.fragment_player_list_role,
                container, false);
        PlayerListAdapter adapter = new PlayerListAdapter(players, getActivity());
        setAdapterOnClickListener(adapter, role);
        recycler.setAdapter(adapter);

        // amount of images in one row depends on orientation
        int orientation = getActivity().getResources().getConfiguration().orientation;
        int imagesInOneRow;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            imagesInOneRow = 2;
        else
            imagesInOneRow = 5;
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), imagesInOneRow));

        // set background
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

    /**
     * Sets adapter onClickListener in order to be able to remove player from playerlist.
     * Shows AlertDialog to confirm action.
     * If user confirms action, removes player from database, array in adapter and from currently
     * shown recyclerview.
     * @param adapter adapter of used RecyclerView
     */
    protected void setAdapterOnClickListener(PlayerListAdapter adapter, Role role) {
        adapter.setListener((position, playerName, imageSource) -> {

            // AlertDialog's onClickListener
            DialogInterface.OnClickListener dialogClickListener = (DialogInterface dialog, int button) -> {

                // check if there are enough players
                int playerCount = adapter.getItemCount();
                if ((role == Role.Sniper && playerCount == 1) ||
                        (role == Role.Rifler && playerCount == 3) ||
                        (role == Role.IGL && playerCount == 1)) {
                    Toast.makeText(getActivity(), "Error: Not enough players left",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (button == DialogInterface.BUTTON_POSITIVE) {
                    //Yes button clicked
                    boolean success = DataHandler
                            .removePlayer(playerName, imageSource, getActivity());
                    if (success) {
                        adapter.removeItem(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                        Toast.makeText(getActivity(), "Player removed", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(getActivity(), "Database unavailable",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

            // creating AlertDialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("This player will be removed")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        });
    }
}
