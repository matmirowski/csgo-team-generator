package pl.mateusz.csgoteamgenerator.ListFragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import pl.mateusz.csgoteamgenerator.R;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private final String[] names;
    //TODO images

    public PlayerListAdapter(String[] names) {
        this.names = names;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    @NonNull
    @Override
    public PlayerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cv = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_layout, viewGroup, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerListAdapter.ViewHolder viewHolder, int i) {
        CardView cv = viewHolder.cardView;
        TextView text = cv.findViewById(R.id.card_name);
        text.setText(names[i]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }
}