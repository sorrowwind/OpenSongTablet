package com.garethevans.church.opensongtablet.appdata;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.BootupLogoBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

/*
This fragment is the first one that the main activity loads up.
It checks that we are clear to proceed - we have the required storage permissions and that we
have set it to a usable location.  If not, we get stuck on this page until we grant the permissions
and set the storage location.

When all is done, the user can click on the pulsating green start button and this will then move to
either the Performace or Presenter fragment.

Before it does that, it does a quick scan of the storage to make a very basic song menu available.
This will just contain the filenames and folders.  It does this by running the call found in the
MainActivity - the hub of the app!

The full indexing of the songs (lyrics, key, etc.) will be called on the MainActivity from either
the Performance/Presenter fragment.
*/

public class BootUpFragment extends Fragment {

    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "BootUpFragment";
    private String message, uriTreeString;
    private Uri uriTree;

    private BootupLogoBinding myView;
    private MainActivityInterface mainActivityInterface;
    private String deeplink_set_storage="", processing="", storage="", wait="", success="",
            mode_performance="", mainfoldername="", welcome="", set_string="", initialising="";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //mainActivityInterface.getShowCase().resetShowcase(getContext(),null);

        prepareStrings();

        myView = BootupLogoBinding.inflate(inflater, container, false);
        mainActivityInterface.setMode(mainActivityInterface.getPreferences().getMyPreferenceString("whichMode", mode_performance));

        mainActivityInterface.registerFragment(this,"BootUpFragment");

        mainActivityInterface.setSettingsOpen(false);
        mainActivityInterface.disableActionBarStuff(false);
        mainActivityInterface.hideActionBar();

        // Lock the navigation drawer and hide the actionbar and floating action button
        hideMenus();

        return myView.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.registerFragment(this,"BootUpFragment");
        if (mainActivityInterface.getWaitingOnBootUpFragment()) {
            startOrSetUp();
        }
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            deeplink_set_storage = getString(R.string.deeplink_set_storage);
            processing = getString(R.string.processing);
            set_string = getString(R.string.set_current);
            storage = getString(R.string.storage);
            wait = getString(R.string.wait);
            success = getString(R.string.success);
            mode_performance = getString(R.string.mode_performance);
            mainfoldername = getString(R.string.mainfoldername);
            welcome = getString(R.string.welcome);
            initialising = getString(R.string.initialising);
        }
    }
    private void hideMenus() {
        mainActivityInterface.hideActionButton(true);
        mainActivityInterface.lockDrawer(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivityInterface.registerFragment(null,"BootUpFragment");
        myView = null;
    }

    // Checks made before starting the app
    public void startOrSetUp() {
        if (storageIsCorrectlySet()) {
            if (!mainActivityInterface.getAlertChecks().showUpdateInfo() &&
                    mainActivityInterface.getPreferences().getMyPreferenceBoolean("indexSkipAllowed",false)) {
                try {
                    BootUpIndexBottomSheet bootUpIndexBottomSheet = new BootUpIndexBottomSheet(this);
                    bootUpIndexBottomSheet.show(mainActivityInterface.getMyFragmentManager(), "BootUpIndexing");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                startBootProcess(true,true);
            }
        } else {
            requireStorageCheck();
        }
    }
    private boolean storageIsCorrectlySet() {
        // Check that storage permission is granted and that it has been set and that it exists
        return (storagePermissionGranted() && storageLocationSet() && storageLocationValid());
    }
    private boolean storagePermissionGranted() {
        return mainActivityInterface.getAppPermissions().hasStoragePermissions();
    }
    private boolean storageLocationSet() {
        uriTreeString = mainActivityInterface.getPreferences().getMyPreferenceString("uriTree", "");
        return !uriTreeString.isEmpty();
    }
    private boolean storageLocationValid() {
        uriTree = Uri.parse(uriTreeString);
        return mainActivityInterface.getStorageAccess().uriTreeValid(uriTree);
    }

    private void requireStorageCheck() {
        // Either permission hasn't been granted, or it isn't set properly
        // Switch to the set storage fragment
        mainActivityInterface.setWhattodo("storageBad");
        if (getContext()!=null) {
            mainActivityInterface.navigateToFragment(deeplink_set_storage, 0);
        }
    }

    public void startBootProcess(boolean needIndex, boolean fullIndexRequired) {
        mainActivityInterface.getSongListBuildIndex().setIndexRequired(needIndex);
        mainActivityInterface.getSongListBuildIndex().setFullIndexRequired(fullIndexRequired);
        mainActivityInterface.getSongListBuildIndex().setCurrentlyIndexing(false);

        // Start the boot process
        if (getContext() != null) {
            mainActivityInterface.getThreadPoolExecutor().execute(() -> {
                // Tell the user we're initialising app
                if (getContext() != null) {
                    message = initialising;
                } else {
                    message = "Initialising";
                }
                updateMessage();
                mainActivityInterface.initialiseActivity();

                // Tell the user we're initialising the storage
                if (getContext() != null) {
                    message = processing + ": " + storage;
                } else {
                    message = "Processing: Storage";
                }
                updateMessage();

                // Get the last used song and folder.  If the song failed to load, reset to default
                setFolderAndSong();

                // Check for saved storage locations
                final String progress = mainActivityInterface.getStorageAccess().
                        createOrCheckRootFolders(uriTree);
                boolean foldersok = !progress.contains("Error");

                if (foldersok) {
                    // Build the basic song index by scanning the songs and creating a songIDs file
                    if (getContext() != null) {
                        message = processing + "\n" + wait;
                    } else {
                        message = "Processing\nWait....";
                    }
                    updateMessage();

                    mainActivityInterface.getStorageAccess().fixBadSongs();

                    if (needIndex) {
                        // Check for bad files
                        mainActivityInterface.getSongListBuildIndex().setIndexComplete(false);
                        mainActivityInterface.getPreferences().setMyPreferenceBoolean("indexSkipAllowed", false);
                        mainActivityInterface.quickSongMenuBuild();

                    } else {
                        mainActivityInterface.getSongListBuildIndex().setIndexComplete(true);
                    }

                    // Finished indexing
                    if (getContext() != null) {
                        message = success;
                    } else {
                        message = "Success";
                    }
                    if (myView != null) {
                        updateMessage();
                    }

                    // Increase the boot times for prompting a user to backup their songs
                    int runssincebackup = mainActivityInterface.getPreferences().getMyPreferenceInt("runssincebackup", 0);
                    int runssincebackupdismissed = mainActivityInterface.getPreferences().getMyPreferenceInt("runssincebackupdismissed", 0);
                    mainActivityInterface.getPreferences().setMyPreferenceInt("runssincebackup", runssincebackup + 1);
                    mainActivityInterface.getPreferences().setMyPreferenceInt("runssincebackupdismissed", runssincebackupdismissed + 1);

                    // Load in the setCurrent
                    message = set_string;
                    updateMessage();

                    mainActivityInterface.getSetActions().parseCurrentSet();

                    message = success;
                    updateMessage();

                    // Set up the rest of the main activity (on the main thread)
                    mainActivityInterface.getMainHandler().post(() -> {
                        mainActivityInterface.navHome();
                        mainActivityInterface.showActionBar();
                        mainActivityInterface.updateMargins();
                    });

                } else {
                    // There was a problem with the folders, so restart the app!
                    requireActivity().recreate();
                }
            });
        }
    }

    // If the fragment is still attached, display the update message
    private void updateMessage() {
        if (getActivity()!=null && getContext()!=null) {
            try {
                mainActivityInterface.getMainHandler().post(() -> {
                    if (myView!=null) {
                        myView.currentAction.setText(message);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Get the last used folder/filename or reset if it didn't load
    private void setFolderAndSong() {
        String temp_mainfoldername;
        String temp_welcome;
        if (getContext()!=null) {
            temp_mainfoldername = mainfoldername;
            temp_welcome = welcome;
        } else {
            temp_mainfoldername = "MAIN";
            temp_welcome = "Welcome to OpenSongApp";
        }
        mainActivityInterface.getSong().setFolder(mainActivityInterface.getPreferences().getMyPreferenceString("songFolder",
                temp_mainfoldername));

        mainActivityInterface.getSong().setFilename(mainActivityInterface.getPreferences().getMyPreferenceString("songFilename",
                temp_welcome));

        if (!mainActivityInterface.getPreferences().getMyPreferenceBoolean("songLoadSuccess",false)) {
            mainActivityInterface.getSong().setFolder(temp_mainfoldername);
            mainActivityInterface.getPreferences().setMyPreferenceString("songFolder",mainActivityInterface.getSong().getFolder());
            mainActivityInterface.getSong().setFilename("Welcome to OpenSongApp");
            mainActivityInterface.getPreferences().setMyPreferenceString("songFilename",temp_welcome);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myView = null;
        mainActivityInterface.registerFragment(null,"BootUpFragment");
    }

}