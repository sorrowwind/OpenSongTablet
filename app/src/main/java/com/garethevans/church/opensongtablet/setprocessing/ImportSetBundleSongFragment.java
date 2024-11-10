package com.garethevans.church.opensongtablet.setprocessing;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.garethevans.church.opensongtablet.databinding.SettingsSetBundledSongsBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

public class ImportSetBundleSongFragment extends Fragment {

    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "ImportSetBundleSetFragment";
    private SettingsSetBundledSongsBinding myView;
    private MainActivityInterface mainActivityInterface;
    private ImportSetItemAdapter importSetItemAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
        mainActivityInterface.getOpenSongSetBundle().setImportSetBundleSongFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivityInterface = null;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        myView = SettingsSetBundledSongsBinding.inflate(inflater, container, false);
        return myView.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        if (getContext()!=null) {
            importSetItemAdapter = new ImportSetItemAdapter(getContext());
            myView.includedSongs.setLayoutManager(new LinearLayoutManager(getContext()));
            myView.includedSongs.setAdapter(importSetItemAdapter);
        }
    }

    private void setupListeners() {
        myView.importSelectedSongs.setOnClickListener(view -> {
            if (importSetItemAdapter!=null) {
                myView.scrimImportBundle.setVisibility(View.VISIBLE);
                myView.progressText.setVisibility(View.VISIBLE);
                mainActivityInterface.getOpenSongSetBundle().setAlive(true);
                myView.importSelectedSongs.setEnabled(false);
                myView.importSelectedSongs.hide();
                mainActivityInterface.getThreadPoolExecutor().execute(() ->
                    importSetItemAdapter.importSelectedSongs(this,myView.progressText));
            } else {
                finishedImporting();
            }
        });
    }

    public void finishedImporting() {
        mainActivityInterface.getMainHandler().post(() -> {
            myView.scrimImportBundle.setVisibility(View.GONE);
            myView.progressText.setVisibility(View.GONE);
            mainActivityInterface.getOpenSongSetBundle().setAlive(false);
            myView.importSelectedSongs.setEnabled(true);
            myView.importSelectedSongs.show();
        });
    }
}
