package com.garethevans.church.opensongtablet.setprocessing;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.garethevans.church.opensongtablet.songmenu.SongListAdapter;

public class ImportBundleViewPagerAdapter extends FragmentStateAdapter {

    SongListAdapter.AdapterCallback callback;

    public final Fragment[] menuFragments = {new ImportSetBundleSetFragment(), new ImportSetBundleSongFragment()};
    private int openMenu = 1;

    public ImportBundleViewPagerAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        openMenu = 1;
        return menuFragments[position];
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public boolean isSetMenu() {
        return openMenu==1;
    }
    public boolean isSongMenu() {
        return openMenu==2;
    }
}
