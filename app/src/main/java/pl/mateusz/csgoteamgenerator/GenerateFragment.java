package pl.mateusz.csgoteamgenerator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GenerateFragment extends Fragment {

    private class SnaxTask extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... url) {
            try {
                InputStream is = (InputStream) new URL(url[0]).getContent();
                return Drawable.createFromStream(is, "src name");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable != null) {
                ImageView snaxImg = getActivity().findViewById(R.id.image_player1);
                snaxImg.setImageDrawable(drawable);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generate, container, false);
    }

    @Override
    public void onStart() {
        ImageButton generateButton = getActivity().findViewById(R.id.button_generate);
        generateButton.setOnClickListener(e -> onClickGenerate());
        super.onStart();
    }

    private void onClickGenerate() {
        for (int i = 1; i <= 5; i++) {

        }
        String url = "https://liquipedia.net/commons/images/1/1a/Snax_Moche_XL_Esports_2019.jpg";
        new SnaxTask().execute(url);
    }

//    private long getTaskCount(long tasklist_Id) {
//        return DatabaseUtils.queryNumEntries(readableDatabase, TABLE_NAME);
//    }
}