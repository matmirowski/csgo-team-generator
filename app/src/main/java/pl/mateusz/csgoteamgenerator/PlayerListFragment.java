package pl.mateusz.csgoteamgenerator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.mateusz.csgoteamgenerator.ListFragments.AbstractRoleListFragment;
import pl.mateusz.csgoteamgenerator.ListFragments.IglFragment;
import pl.mateusz.csgoteamgenerator.ListFragments.RiflerFragment;
import pl.mateusz.csgoteamgenerator.ListFragments.SniperFragment;

public class PlayerListFragment extends Fragment {

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
    public void onStart() {
        super.onStart();
        Log.e("INFO", "STARTING PAGER");
        // setup viewpager
        RolePagerAdapter adapter = new RolePagerAdapter(getActivity().getSupportFragmentManager());
        ViewPager pager = getActivity().findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // setup tablayout
        TabLayout tabLayout = getActivity().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        // hide default toolbar and set new
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        Toolbar listToolbar = getActivity().findViewById(R.id.player_list_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(listToolbar);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                listToolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}