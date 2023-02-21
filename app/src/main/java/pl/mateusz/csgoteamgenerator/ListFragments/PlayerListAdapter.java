package pl.mateusz.csgoteamgenerator.ListFragments;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import lombok.Getter;
import pl.mateusz.csgoteamgenerator.GenerateFragment;
import pl.mateusz.csgoteamgenerator.ImageSource;
import pl.mateusz.csgoteamgenerator.Player;
import pl.mateusz.csgoteamgenerator.R;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private final Player[] players;
    //TODO images

    public PlayerListAdapter(Player[] players) {
        this.players = players;
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
            new AssignImageToPlayerImageTask().execute(new TaskData(players[i].getName(), imageView));
        }
        else if (player.getImageSource().equals("DEFAULT")) {
            imageView.setImageDrawable(cv.getResources().getDrawable(R.drawable.default_player));
        }
        else {
            // custom
        }
    }

    @Override
    public int getItemCount() {
        return players.length;
    }

    @Getter
    private static class TaskData {
        String name;
        ImageView imageView;

        public TaskData(String name, ImageView imageView) {
            this.name = name;
            this.imageView = imageView;
        }
    }

    private class AssignImageToPlayerImageTask extends AsyncTask<TaskData, Void, Drawable> {
        private ImageView imageView;

        @Override
        protected Drawable doInBackground(TaskData... taskData) {
            try {
                imageView = taskData[0].imageView;
                String playerProfileURL = GenerateFragment.liquipediaURL + taskData[0].name;

                // site scraping using Jsoup
                Element doc = Jsoup
                        .connect(playerProfileURL)
                        .timeout(1000)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/45.0.2454.101 Safari/537.36")
                        .get().head();

                // get only element with image url (index 12 of meta tags)
                String strMetaImg = doc.getElementsByTag("meta").get(12).toString();
                Log.d("INFO", "player meta: " + strMetaImg);
                int beginIndex = strMetaImg.indexOf("https");

                // if there is no player photo on liquipedia, default hltv photo is being used
                String imageUrl;
                if (beginIndex == -1)
                    return null;
                else {
                    int endIndex = strMetaImg.length() - 2;
                    imageUrl = strMetaImg.substring(beginIndex, endIndex);
                }
                Log.d("IMAGEURL", imageUrl);

                // get drawable from url
                InputStream is = (InputStream) new URL(imageUrl).getContent();
                return Drawable.createFromStream(is, "src name");
            } catch (IOException e) {
                Log.e("EXC", "Error while getting image url from player profile", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable image) {
            if (image != null) {
                imageView.setImageDrawable(image);
            }
        }
    }
}