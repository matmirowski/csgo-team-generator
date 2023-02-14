package pl.mateusz.csgoteamgenerator.ListFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.mateusz.csgoteamgenerator.R;

public class SniperFragment extends Fragment {

    public class CaptionedPlayersAdapter extends RecyclerView.Adapter<CaptionedPlayersAdapter.ViewHolder> {
        private String[] players;
        private Drawable[] drawables;

        public CaptionedPlayersAdapter(String[] players, Drawable[] drawables) {
            this.players = players;
            this.drawables = drawables;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return players.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final CardView cardView;

            public ViewHolder(CardView v) {
                super(v);
                cardView = v;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sniper, container, false);
    }
}