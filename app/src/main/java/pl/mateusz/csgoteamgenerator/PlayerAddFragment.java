package pl.mateusz.csgoteamgenerator;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//todo after onClick fab in playerlist check role checkbox
public class PlayerAddFragment extends Fragment {
    private EditText nickEditText;
    private RadioGroup roleGroup;
    private Switch liquipediaSwitch;
    private Switch defaultSwitch;
    private TextView liquipediaHint;
    private Button liquipediaCheckButton;
    private AppCompatCheckBox liquipediaSuccessCheckbox;
    private FloatingActionButton fab;
    private ImageView playerImageView;
    private Bitmap playerImageBitmap;
    private Button uploadImageButton;
    private Boolean photoSelected;
    private TextView nickCaption;

    private class CheckCorrectNicknameTask extends AsyncTask<String, Void, Boolean> {
        private String message = "";
        private String name;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                name = params[0];
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
            if (name.equals("ILLa")) { // test case initiated onStart to avoid Jsoup errors
                return;
            }
            if (success) {
                liquipediaSuccessCheckbox.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
                liquipediaSuccessCheckbox.setChecked(true);
                nickEditText.setFocusable(false);
                nickCaption.setText(getString(R.string.locked));
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

        // assign views to variables
        nickEditText = getActivity().findViewById(R.id.add_player_nick_text);
        roleGroup = getActivity().findViewById(R.id.role_group);
        liquipediaSwitch = getActivity().findViewById(R.id.add_player_liquipedia_switch);
        defaultSwitch = getActivity().findViewById(R.id.add_player_default_switch);
        liquipediaHint = getActivity().findViewById(R.id.add_player_liquipedia_hint);
        liquipediaCheckButton = getActivity().findViewById(R.id.add_player_liquipedia_check_button);
        liquipediaSuccessCheckbox = getActivity()
                .findViewById(R.id.add_player_liquipedia_success_checkbox);
        fab = getActivity().findViewById(R.id.add_player_fab);
        playerImageView = getActivity().findViewById(R.id.add_player_image);
        uploadImageButton = getActivity().findViewById(R.id.add_player_upload_button);
        nickCaption = getActivity().findViewById(R.id.add_player_nick_caption);

        // theme and toolbar
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.appbar_color));
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.appbar_color));
        setupToolbar();

        // assign onClickListeners
        liquipediaSwitch.setOnClickListener(e -> onLiquipediaSourceClick());
        defaultSwitch.setOnClickListener(e -> onDefaultSourceClick());
        liquipediaCheckButton.setOnClickListener(e -> onLiquipediaCheckClick());
        fab.setOnClickListener(e -> onAddPlayerClick());
        uploadImageButton.setOnClickListener(e -> onUploadImageClick());

        // test case to avoid Jsoup errors
        new CheckCorrectNicknameTask().execute("ILLa");
    }

    private void setupToolbar() {
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }
        Toolbar toolbar = getActivity().findViewById(R.id.add_player_toolbar);
        toolbar.setTitle("Add new wholesome player");
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
        if (liquipediaSwitch.isChecked()) {
            // Liquipedia image source
            liquipediaHint.setVisibility(View.VISIBLE);
            liquipediaCheckButton.setVisibility(View.VISIBLE);
            liquipediaSuccessCheckbox.setVisibility(View.VISIBLE);
            defaultSwitch.setVisibility(View.GONE);
            playerImageView.setVisibility(View.GONE);
            uploadImageButton.setVisibility(View.GONE);
        } else {
            // Custom image source
            liquipediaHint.setVisibility(View.GONE);
            liquipediaCheckButton.setVisibility(View.GONE);
            liquipediaSuccessCheckbox.setChecked(false);
            liquipediaSuccessCheckbox.setVisibility(View.GONE);
            defaultSwitch.setVisibility(View.VISIBLE);
            playerImageView.setVisibility(View.VISIBLE);
            uploadImageButton.setVisibility(View.VISIBLE);
            nickEditText.setFocusableInTouchMode(true);
            nickCaption.setText(getString(R.string.nickname));
        }
    }

    private void onDefaultSourceClick() {
        if (defaultSwitch.isChecked()) {
            // default image source
            playerImageView.setVisibility(View.GONE);
            uploadImageButton.setVisibility(View.GONE);
        } else {
            // custom image source
            playerImageView.setVisibility(View.VISIBLE);
            uploadImageButton.setVisibility(View.VISIBLE);
        }
    }

    private void onLiquipediaCheckClick() {
        if (!liquipediaSuccessCheckbox.isChecked()) {
            String name = nickEditText.getText().toString();
            if (name.equals("")) {
                Toast.makeText(getActivity(), "Please enter nickname", Toast.LENGTH_SHORT).show();
            } else {
                new CheckCorrectNicknameTask().execute(name);
            }
        }
    }

    private void onAddPlayerClick() {
        String name = nickEditText.getText().toString();
        if (name.equals("")) {
            // if name is empty
            Toast.makeText(getActivity(), "Please enter nickname", Toast.LENGTH_SHORT).show();
            return;
        }
        if (liquipediaSwitch.isChecked() && !liquipediaSuccessCheckbox.isChecked()) {
            // if user choose liquipedia but nickname isnt checked yet
            Toast.makeText(getActivity(), "Please check if nickname is correct first",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!liquipediaSwitch.isChecked() && !defaultSwitch.isChecked() && !photoSelected) {
            // if user didn't select any photo from gallery
            Toast.makeText(getActivity(), "Please select a image first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // get role
        int roleId = roleGroup.getCheckedRadioButtonId();
        Role role;
        switch (roleId) {
            case R.id.sniper_radio:
                role = Role.Sniper;
                break;
            case R.id.rifler_radio:
                role = Role.Rifler;
                break;
            case R.id.igl_radio:
                role = Role.IGL;
                break;
            default:
                Toast.makeText(getActivity(), "Please choose role first", Toast.LENGTH_SHORT)
                        .show();
                return;
        }

        // get image source
        ImageSource imgSource;
        if (liquipediaSwitch.isChecked())
            imgSource = ImageSource.LIQUIPEDIA;
        else if (defaultSwitch.isChecked())
            imgSource = ImageSource.DEFAULT;
        else
            imgSource = ImageSource.CUSTOM;

        // add record
        try { //TODO async
            MyDatabaseHelper helper = new MyDatabaseHelper(getActivity());
            SQLiteDatabase db = helper.getWritableDatabase();
            helper.addPlayer(db, name, role, imgSource);

            // save player image if imagesource is custom
            if (imgSource == ImageSource.CUSTOM) {
                if (playerImageBitmap == null) {
                    throw new IOException("Image bitmap is null");
                }
                byte[] imageAsByteArray = getBitmapAsByteArray(playerImageBitmap);
                if (imageAsByteArray != null) {
                    helper.addAvatar(db, name, imageAsByteArray);
                }
            }

            // display Snackbar
            String snackbarText = "Player added successfully";
            Snackbar addPlayerSnackbar = Snackbar.make(getActivity().findViewById(R.id.drawer_layout),
                    snackbarText, Snackbar.LENGTH_LONG);
            addPlayerSnackbar.setAction("UNDO", e -> {
                    helper.removePlayer(db, name, imgSource);
                    Toast.makeText(getActivity(), "Player removed!", Toast.LENGTH_SHORT).show();
            });
            // close db after Snackbar is closed
            addPlayerSnackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    db.close();
                }
            });
            addPlayerSnackbar.show();

            // move to PlayerListFragment
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new PlayerListFragment());
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
            // select appropriate item in drawer
            NavigationView navView = getActivity().findViewById(R.id.nav_view);
            navView.setCheckedItem(R.id.nav_player_list);
        } catch (SQLiteException e) {
            Log.e("ERR", "Error while putting player into database", e);
            Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("ERR", "Error while putting player into database - null bitmap", e);
            Toast.makeText(getActivity(), "Image unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void onUploadImageClick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && data != null) {
            try {
                Uri selectedImage = data.getData();
                InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                playerImageView.setImageBitmap(selectedBitmap);
                playerImageBitmap = selectedBitmap;
                photoSelected = true;
            } catch (FileNotFoundException e) {
                Toast.makeText(getActivity(), "Image not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            Log.e("ERR", "Can't convert bitmap to byte array", e);
            return null;
        }
    }
}