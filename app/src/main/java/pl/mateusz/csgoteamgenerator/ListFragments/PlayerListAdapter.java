package pl.mateusz.csgoteamgenerator.ListFragments;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import pl.mateusz.csgoteamgenerator.DataHandler;
import pl.mateusz.csgoteamgenerator.GenerateFragment;
import pl.mateusz.csgoteamgenerator.MyDatabaseHelper;
import pl.mateusz.csgoteamgenerator.Player;
import pl.mateusz.csgoteamgenerator.R;

/**
 * Adapter used in RecyclerViews in all RoleFragments such as SniperFragment etc
 */
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    /** All players with fragment's role */
    private Player[] players;

    /** App's activity */
    private final Activity activity;

    /** Listener that allows communication with Fragment and setting onClickListener*/
    @Setter
    private Listener listener;

    interface Listener {
        void onClick(int position, String playerName, String imageSource);
    }

    public PlayerListAdapter(Player[] players, Activity activity) {
        this.players = players;
        this.activity = activity;

        // test case to avoid Jsoup error
        new AssignUrlImageToPlayerImageTask().execute(new TaskData("Snax", null));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    /**
     * Removes player with index position from array
     * @param position position of player in RecyclerView and array
     */
    public void removeItem(int position) {
        Player[] newPlayers = new Player[players.length - 1];
        for (int i = 0, j = 0; i < players.length; i++) {
            if (i != position) {
                newPlayers[j] = players[i];
                j++;
            }
        }
        players = newPlayers;
    }

    @NonNull
    @Override
    public PlayerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cv = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_layout, viewGroup, false);
        return new ViewHolder(cv);
    }

    /**
     * Invoked when need to bind data to viewholder. Initially all CardViews have default player
     * photo. Then through AsyncTasks Liquipedia's images and custom images are being
     * put in the ImageViews.
     */
    @Override
    public void onBindViewHolder(@NonNull PlayerListAdapter.ViewHolder viewHolder, int i) {
        CardView cv = viewHolder.cardView;
        TextView text = cv.findViewById(R.id.card_name);
        ImageView imageView = cv.findViewById(R.id.card_image);
        Player player = players[i];
        text.setText(player.getName());

        // set listener
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onClick(viewHolder.getAdapterPosition(), player.getName(),
                            player.getImageSource());
            }
        });

        // set image
        if (player.getImageSource().equals("LIQUIPEDIA")) {
            new AssignUrlImageToPlayerImageTask().execute(new TaskData(player.getName(), imageView));
        }
        else if (player.getImageSource().equals("DEFAULT")) {
            imageView.setImageDrawable(cv.getResources().getDrawable(R.drawable.default_player2));
        }
        else {
            new AssignCustomImageToPlayerImageTask().execute(new TaskData(player.getName(), imageView));
        }
    }

    @Override
    public int getItemCount() {
        return players.length;
    }

    // Implementation of these two methods: getItemViewType and getItemId fixes bug with recycler
    // displaying wrong images with slight performance loss
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Contains data used in AsyncTasks
     */
    private static class TaskData {
        String name;
        ImageView imageView;

        public TaskData(String name, ImageView imageView) {
            this.name = name;
            this.imageView = imageView;
        }
    }

    /**
     * Asynchronous task that gets player profile image from Liquipedia and then loads it into
     * ImageView in a card through Glide library.
     */
    private class AssignUrlImageToPlayerImageTask extends AsyncTask<TaskData, Void, String> {
        private ImageView imageView;

        @Override
        protected String doInBackground(TaskData... taskData) {
            imageView = taskData[0].imageView;
            return DataHandler.getPlayerImageUrl(taskData[0].name);
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (imageUrl != null && !imageUrl.equals("https://i.imgur.com/KQDl2wD.png")
                    && !activity.isDestroyed() && imageView != null) {
                // url is not null and url isn't default player url
                Log.d("LIST-IMAGEURL", imageUrl);
                Glide.with(activity)
                        .load(imageUrl)
                        .into(imageView);
            }
        }
    }

    /**
     * Asynchronous task that finds player custom image in database and then puts it into ImageView
     */
    private class AssignCustomImageToPlayerImageTask extends AsyncTask<TaskData, Void, Drawable> {
        private ImageView imageView;

        @Override
        protected Drawable doInBackground(TaskData... taskData) {
            String name = taskData[0].name;
            imageView = taskData[0].imageView;
            Bitmap playerImage = DataHandler.getPlayerImageAsBitmap(
                    new Player(name, null, 0, null), activity);
            return new BitmapDrawable(activity.getResources(), playerImage);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable != null && !activity.isDestroyed()) {
                imageView.setImageDrawable(drawable);
            }
        }
    }
}