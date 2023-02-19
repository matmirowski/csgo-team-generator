package pl.mateusz.csgoteamgenerator;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class PlayerAddFragment extends Fragment {

    private class CheckCorrectNicknameTask extends AsyncTask<String, Void, Boolean> {
        private String message = "";

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String name = params[0];
                String playerProfileURL = GenerateFragment.liquipediaURL + name;
                // site scraping using Jsoup
                Element doc = Jsoup
                        .connect(playerProfileURL)
                        .timeout(1000)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/45.0.2454.101 Safari/537.36")
                        .get().head();
                String scriptTag = doc.getElementsByTag("script").get(0).toString();
                if (scriptTag.contains("Disambiguation pages")) {
                    message = "Invalid nickname: Too many players under this URL!";
                    Log.e("CHECK", message);
                    return false;
                }
                message = "Nickname is correct!";
                return true;
            } catch (HttpStatusException e) {
                message = "Invalid nickname: There is no such player!";
                Log.e("CHECK", message, e);
                return false;
            } catch (IOException e) {
                message = "Liquipedia site unavailable! Please try again";
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                CheckBox successCheckbox = getActivity().findViewById(R.id.add_player_liquipedia_success_checkbox);
                Button checkButton = getActivity().findViewById(R.id.add_player_liquipedia_check_button);
                successCheckbox.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
                successCheckbox.setChecked(true);
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_add, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // theme and toolbar
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.appbar_color));
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.appbar_color));
        setupToolbar();

        // switch onclick
        Switch liquipediaSourceSwitch = getActivity().findViewById(R.id.add_player_liquipedia_switch);
        liquipediaSourceSwitch.setOnClickListener(e -> onLiquipediaSourceClick());

        // checkButton onclick
        Button liquipediaCheckButton = getActivity().findViewById(R.id.add_player_liquipedia_check_button);
        liquipediaCheckButton.setOnClickListener(e -> onLiquipediaCheckClick());
    }

    private void setupToolbar() {
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }
        Toolbar toolbar = getActivity().findViewById(R.id.add_player_toolbar);
        toolbar.setTitle("Add new wholesome player");
//        toolbar.setBackgroundColor(getResources().getColor(R.color.appbar_color));
        toolbar.setTitleTextColor(Color.WHITE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void onLiquipediaSourceClick() {
        Switch liquipediaSourceSwitch = getActivity().findViewById(R.id.add_player_liquipedia_switch);
        Switch defaultImageSwitch = getActivity().findViewById(R.id.add_player_default_switch);
        TextView liquipediaHintTextView = getActivity().findViewById(R.id.add_player_liquipedia_hint);
        Button liquipediaCheckButton = getActivity().findViewById(R.id.add_player_liquipedia_check_button);
        CheckBox liquipediaSuccessCheckbox = getActivity().findViewById(R.id.add_player_liquipedia_success_checkbox);

        if (liquipediaSourceSwitch.isChecked()) {
            // Liquipedia image source
            liquipediaHintTextView.setVisibility(View.VISIBLE);
            liquipediaCheckButton.setVisibility(View.VISIBLE);
            liquipediaSuccessCheckbox.setVisibility(View.VISIBLE);
            defaultImageSwitch.setVisibility(View.GONE);
        } else {
            // Custom image source
            liquipediaHintTextView.setVisibility(View.GONE);
            liquipediaCheckButton.setVisibility(View.GONE);
            liquipediaSuccessCheckbox.setVisibility(View.GONE);
            defaultImageSwitch.setVisibility(View.VISIBLE);
        }
    }

    private void onLiquipediaCheckClick() {
        EditText nickEditText = getActivity().findViewById(R.id.add_player_nick_text);
        String name = nickEditText.getText().toString();
        if (name.equals("")) {
            Toast.makeText(getActivity(), "Please enter nickname", Toast.LENGTH_SHORT).show();
        } else {
            new CheckCorrectNicknameTask().execute(name);
        }
    }
}