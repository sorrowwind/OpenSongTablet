package com.garethevans.church.opensongtablet.bootup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.BootupLogoBinding;
import com.garethevans.church.opensongtablet.filemanagement.StorageAccess;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.preferences.Preferences;
import com.garethevans.church.opensongtablet.preferences.StaticVariables;
import com.garethevans.church.opensongtablet.sqlite.CommonSQL;
import com.garethevans.church.opensongtablet.sqlite.NonOpenSongSQLiteHelper;
import com.garethevans.church.opensongtablet.sqlite.SQLiteHelper;

import java.util.ArrayList;

public class BootUpFragment extends Fragment {

    private Preferences preferences;
    private StorageAccess storageAccess;
    private SQLiteHelper sqLiteHelper;
    private NonOpenSongSQLiteHelper nonOpenSongSQLiteHelper;
    private CommonSQL commonSQL;
    private String initialising, message;
    private String uT;
    private Uri uriTree;

    private BootupLogoBinding myView;
    private MainActivityInterface mainActivityInterface;
    private Bundle bundle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityInterface.hideActionBar(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // TODO
        // REMOVE BEFORE RELEASE!!!!!
        //MaterialShowcaseView.resetAll(requireActivity());

        StaticVariables.homeFragment = false;  // Set to true for Performance/Stage/Presentation only

        bundle = savedInstanceState;

        myView = BootupLogoBinding.inflate(inflater, container, false);
        View root = myView.getRoot();

        // Initialise the helper classes
        initialiseHelpers();

        initialising = "Initialising: ";

        // Check we have the required storage permission
        startOrSetUp();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myView = null;
    }

    private void initialiseHelpers() {
        // Load the helper classes (preferences)
        preferences = new Preferences();
        storageAccess = new StorageAccess();
        sqLiteHelper = new SQLiteHelper(requireContext());
        nonOpenSongSQLiteHelper = new NonOpenSongSQLiteHelper(requireContext());
        commonSQL = new CommonSQL();
    }

    private void setFolderAndSong() {
        StaticVariables.whichSongFolder = preferences.getMyPreferenceString(getContext(), "whichSongFolder",
                getString(R.string.mainfoldername));
        StaticVariables.songfilename = preferences.getMyPreferenceString(getContext(), "songfilename",
                getString(R.string.welcome));

        // Check if we have used the app already, but the last song didn't load
        if (!preferences.getMyPreferenceBoolean(getContext(),"songLoadSuccess",false)) {
            StaticVariables.whichSongFolder = getString(R.string.mainfoldername);
            preferences.setMyPreferenceString(getContext(),"whichSongFolder",StaticVariables.whichSongFolder);
            StaticVariables.songfilename = "Welcome to OpenSongApp";
            preferences.setMyPreferenceString(getContext(),"songfilename",StaticVariables.songfilename);
        }
    }

    // Checks made before starting the app
    private void startOrSetUp() {
        if (storageIsCorrectlySet()) {
            startBootProcess();
        } else {
            requireStorageCheck();
        }
    }
    private boolean storagePermissionGranted() {
        return (getContext()!=null && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }
    private boolean storageLocationSet() {
        uT = preferences.getMyPreferenceString(getContext(),"uriTree","");
        return !uT.isEmpty();
    }
    private boolean storageLocationValid() {
        uriTree = Uri.parse(uT);
        return storageAccess.uriTreeValid(requireActivity(),uriTree);
    }
    private boolean storageIsCorrectlySet() {
        // Check that storage permission is granted and that it has been set and that it exists
        return (storagePermissionGranted() && storageLocationSet() && storageLocationValid());
    }

    private void requireStorageCheck() {
        // Either permission hasn't been granted, or it isn't set properly
        // Switch to the set storage fragment
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_boot, true)
                .build();
        NavHostFragment.findNavController(BootUpFragment.this)
                .navigate(R.id.action_nav_boot_to_nav_storage,bundle,navOptions);
    }

    private void startBootProcess() {
        // Start the boot process
        if (getContext() != null) {
            new Thread(() -> {
                // Set up the Typefaces
                if (getContext() != null) {
                    requireActivity().runOnUiThread(() -> {
                        message = initialising + getString(R.string.font_choose);
                        myView.currentAction.setText(message);
                    });
                }

                message = initialising + getString(R.string.storage);

                requireActivity().runOnUiThread(() -> {
                    // Tell the user we're initialising the storage
                    myView.currentAction.setText(message);
                });

                setFolderAndSong();

                // Check for saved storage locations
                final String progress = storageAccess.createOrCheckRootFolders(getContext(), uriTree, preferences);
                boolean foldersok = !progress.contains("Error");

                if (foldersok) {

                    // Get the songIds  these are basically folder/file pairs for proper indexing later
                    // These are stored to a temporary file in the app storage folder
                    ArrayList<String> songIds = new ArrayList<>();
                    try {
                        songIds = storageAccess.listSongs(getContext(), preferences);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int numSongs = songIds.size();
                    message = numSongs + " " + getString(R.string.processing) + "\n" + getString(R.string.wait);
                    Log.d("BootUpFragment", message);

                    requireActivity().runOnUiThread(() -> myView.currentAction.setText(message));

                    // Write a crude text file (line separated) with the song Ids (folder/file)
                    storageAccess.writeSongIDFile(getContext(), preferences, songIds);

                    // Try to create the basic databases
                    // Non persistent, created from storage at boot (to keep updated) used to references ALL files
                    sqLiteHelper.resetDatabase(getContext());
                    // Persistent containing details of PDF/Image files only.  Pull in to main database at boot
                    // Updated each time a file is created, deleted, moved.
                    // Also updated when feature data (pad, autoscroll, metronome, etc.) is updated for these files
                    nonOpenSongSQLiteHelper.initialise(getContext(), commonSQL, storageAccess, preferences);

                    // Add entries to the database that have songid, folder and filename fields
                    // This is the minimum that we need for the song menu.
                    // It can be upgraded asynchronously in StageMode/PresenterMode to include author/key
                    // Also will later include all the stuff for the search index as well
                    sqLiteHelper.insertFast(getContext(), commonSQL, storageAccess);

                    // Finished indexing
                    message = getString(R.string.success);
                    requireActivity().runOnUiThread(() -> myView.currentAction.setText(message));

                    StaticVariables.whichMode = preferences.getMyPreferenceString(getContext(), "whichMode", "Performance");

                    requireActivity().runOnUiThread(() -> mainActivityInterface.initialiseActivity());
                } else {
                    // There was a problem with the folders, so restart the app!
                    Log.d("BootUpFragment", "problem with folders");
                    requireActivity().recreate();
                }
            }).start();
        }
    }
}