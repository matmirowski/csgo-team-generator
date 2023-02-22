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
import pl.mateusz.csgoteamgenerator.DataHandler;
import pl.mateusz.csgoteamgenerator.GenerateFragment;
import pl.mateusz.csgoteamgenerator.MyDatabaseHelper;
import pl.mateusz.csgoteamgenerator.Player;
import pl.mateusz.csgoteamgenerator.R;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private final Player[] players;
    private final Activity activity;

    public PlayerListAdapter(Player[] players, Activity activity) {
        this.players = players;
        this.activity = activity;
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
        ImageView imageView = cv.findViewById(R.id.card_image);
        Player player = players[i];
        text.setText(player.getName());

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

    private static class TaskData {
        String name;
        ImageView imageView;

        public TaskData(String name, ImageView imageView) {
            this.name = name;
            this.imageView = imageView;
        }
    }

    private class AssignUrlImageToPlayerImageTask extends AsyncTask<TaskData, Void, String> {
        private ImageView imageView;

        @Override
        protected String doInBackground(TaskData... taskData) {
            imageView = taskData[0].imageView;
            return DataHandler.getPlayerImageUrl(taskData[0].name);
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (imageUrl != null) {
                Glide.with(activity)
                        .load(imageUrl)
                        .into(imageView);
            }
        }
    }

    private class AssignCustomImageToPlayerImageTask extends AsyncTask<TaskData, Void, Drawable> {
        private ImageView imageView;

        @Override
        protected Drawable doInBackground(TaskData... taskData) {
            String name = taskData[0].name;
            imageView = taskData[0].imageView;
            MyDatabaseHelper helper = new MyDatabaseHelper(activity);
            try {
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor cursor = db.query("AVATARS",
                        new String[]{"IMAGE"},
                        "NAME = ?",
                        new String[]{name},
                        null, null, null);
                if (!cursor.moveToFirst())
                    return null;
                byte[] imageByteArray = cursor.getBlob(0);
                cursor.close();
                db.close();
                return new BitmapDrawable(BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length));
            } catch (SQLiteException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
            }
        }
    }
}