package pl.mateusz.csgoteamgenerator;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /** Initial activity configuration and loading fragment with main content */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set default theme
        getApplication().setTheme(R.style.Theme_CSGOTeamGenerator);
        setContentView(R.layout.activity_main);

        // assign listener to navView
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // perform FragmentTransaction to one of fragments, depends on which was selected before
        // configuration change. If Activity is created for the first time, starts GenerateFragment
        Fragment contentFragment = new GenerateFragment();

        if (savedInstanceState != null) {
            String savedFragmentName = savedInstanceState.getString("saved_fragment_name");
            if (savedFragmentName.equals("PlayerListFragment"))
                contentFragment = new PlayerListFragment();
            else if (savedFragmentName.equals("PlayerAddFragment"))
                contentFragment = new PlayerAddFragment();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, contentFragment);
        transaction.commit();
    }

    /** Handles back press with open drawer */
    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /** Handles selecting fragment through the drawer, performs appropriate FragmentTransaction */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // if user clicked on currently checked item
        if (menuItem.isChecked()) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        int id = menuItem.getItemId();
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_generate_team:
                fragment = new GenerateFragment();
                break;
            case R.id.nav_player_list:
                fragment = new PlayerListFragment();
                break;
            case R.id.nav_add_player:
                fragment = new PlayerAddFragment();
                break;
            case R.id.nav_about:
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                String githubUrl = "https://github.com/matmirowski/csgo-team-generator";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(githubUrl));
                startActivity(intent);
                return false;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.setReorderingAllowed(true);
            transaction.addToBackStack(null);
            transaction.commit();
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    /** Saves name of current fragment that needs to be restored after activity recreating */
    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        String currentFragment;
        if (fragment instanceof GenerateFragment)
            currentFragment = "GenerateFragment";
        else if (fragment instanceof  PlayerListFragment)
            currentFragment = "PlayerListFragment";
        else
            currentFragment = "PlayerAddFragment";
        saveState.putString("saved_fragment_name", currentFragment);
        super.onSaveInstanceState(saveState);
    }
}