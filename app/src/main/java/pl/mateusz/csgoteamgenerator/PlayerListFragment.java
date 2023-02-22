package pl.mateusz.csgoteamgenerator;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.mateusz.csgoteamgenerator.ListFragments.IglFragment;
import pl.mateusz.csgoteamgenerator.ListFragments.RiflerFragment;
import pl.mateusz.csgoteamgenerator.ListFragments.SniperFragment;

public class PlayerListFragment extends Fragment {
    private int currentColor;
    private boolean currentFragmentVisible;

    private class RolePagerAdapter extends FragmentStatePagerAdapter {

        public RolePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SniperFragment();
                case 1:
                    return new RiflerFragment();
                case 2:
                    return new IglFragment();
            }
            return null;
        }

        @Override
        public int getCount() { // 3 pages
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getActivity().getResources().getText(R.string.sniper_tab);
                case 1:
                    return getActivity().getResources().getText(R.string.riflers_tab);
                case 2:
                    return getActivity().getResources().getText(R.string.igls_tab);
            }
            return null;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            FragmentTransaction ft2 = getFragmentManager().beginTransaction();
            ft2.replace(R.id.fragment_container, new PlayerListFragment());
            ft2.commit();
        }

        Log.e("INFO", "STARTING PAGER");
        currentFragmentVisible = true;

        // set theme
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.sniper));
        getActivity().getWindow().setStatusBarColor(Color.BLACK);
        currentColor = getResources().getColor(R.color.sniper);

        // setup viewpager
        RolePagerAdapter adapter = new RolePagerAdapter(getActivity().getSupportFragmentManager());
        ViewPager pager = getActivity().findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // set fab onClickListener
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_player_list);
        fab.setOnClickListener(e -> onClickAddPlayer());

        // setup tablayout
        TabLayout tabLayout = getActivity().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.sniper));

        setupLayout();

        // set pager onPageChangeListener to change theme depending on current ViewPager page
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {}

            @Override
            public void onPageScrollStateChanged(int position) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        getActivity().getWindow().setNavigationBarColor(getResources()
                                .getColor(R.color.sniper));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.sniper));
                        currentColor = getResources().getColor(R.color.sniper);
                        break;
                    case 1:
                        getActivity().getWindow().setNavigationBarColor(getResources()
                                .getColor(R.color.rifler));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.rifler));
                        currentColor = getResources().getColor(R.color.rifler);
                        break;
                    case 2:
                        getActivity().getWindow().setNavigationBarColor(getResources()
                                .getColor(R.color.igl));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.igl));
                        currentColor = getResources().getColor(R.color.igl);
                        break;
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        currentFragmentVisible = false;
    }

    private void onClickAddPlayer() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.fragment_container, new PlayerAddFragment());
        ft.addToBackStack(null);
        ft.commit();

        // select item in drawer
        NavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_add_player);
    }

    private void setupLayout() {
        // hide default toolbar and set new with drawerlayout setup
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }
        Toolbar listToolbar = getActivity().findViewById(R.id.player_list_toolbar);
        listToolbar.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        listToolbar.setTitle("List of best Polish players");
        ((AppCompatActivity) getActivity()).setSupportActionBar(listToolbar);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                listToolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer) {
            //TODO try to decrease delay
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (currentFragmentVisible)
                    getActivity().getWindow().setNavigationBarColor(Color.BLACK);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (currentFragmentVisible)
                    getActivity().getWindow().setNavigationBarColor(currentColor);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("restarting", true);
        super.onSaveInstanceState(outState);
    }

}