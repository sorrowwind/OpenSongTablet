package com.garethevans.church.opensongtablet.filemanagement;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;
import static com.google.android.material.snackbar.Snackbar.make;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.animation.CustomAnimation;
import com.garethevans.church.opensongtablet.databinding.StorageChooseBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/*
This fragment is used to set the storage location for the app.  It deals with the permissions for
using the external storage and allows the user to use the built in picker to choose the location
For Lollipop+, this is a URI TREE, for KitKat, it is just a location.
The app stores this (and permissions granted for later use of any files/folders attached to this location.

It is called under the following conditions:
* the first boot if the storage has never been set
* normal booting if there is an issue with the currently set storage
* the user has specified that they want to change the storage location
*/

public class SetStorageLocationFragment extends Fragment {

    private MainActivityInterface mainActivityInterface;
    private Uri uriTree, uriTreeHome;
    private ArrayList<String> locations;
    private File folder;

    ActivityResultLauncher<Intent> folderChooser;
    ActivityResultLauncher<String> storagePermission;

    private StorageChooseBinding myView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }
    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.hideActionBar(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        myView = StorageChooseBinding.inflate(inflater, container, false);

        // Set up the views
        initialiseViews();

        // Set up intent listeners for permissions and folder choices
        setUpIntentListeners();

        // Check preferences for storage (if they exist)
        getUriTreeFromPreferences();

        // If we are sent here from the kitkat chooser, deal with that
        kitKatDealWithUri();

        // Show the current location
        showStorageLocation();

        // Check we have the required storage permission
        // If we have it, this will update the text, if not it will ask for permission
        checkStatus();

        return myView.getRoot();
    }

    private void initialiseViews() {
        // Lock the menu and hide the actionbar and action button
        mainActivityInterface.lockDrawer(true);
        mainActivityInterface.hideActionButton(true);

        // Set up the storage location currently set in an edit box that acts like a button only
        myView.progressText.findViewById(R.id.editText).setFocusable(false);
        myView.progressText.findViewById(R.id.editText).setClickable(true);
        ((TextInputEditText)myView.progressText.findViewById(R.id.editText)).setMaxLines(4);
        myView.progressText.findViewById(R.id.editText).
                setOnClickListener(t -> myView.setStorage.performClick());

        // Set the listeners for the buttons
        myView.infoButton.setOnClickListener(v -> {
            BottomSheetDialogFragment dialog = new SetStorageBottomSheet();
            dialog.show(requireActivity().getSupportFragmentManager(),"SetStorageBottomSheet");
        });
        myView.setStorage.setOnClickListener(v -> chooseStorageLocation());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Hide the previous location button as we can't use it without full manage storage control
            // Don't want the hassle of requesting this.
            myView.findStorage.setVisibility(View.GONE);
        } else {
            myView.findStorage.setOnClickListener(v -> startSearch());
        }
        myView.startApp.setOnClickListener(v -> goToSongs());
    }

    private void setUpIntentListeners() {
        folderChooser = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                        // This is only called when using lollipop or later
                        lollipopDealWithUri(result.getData());

                        // Save the location uriTree and uriTreeHome
                        saveUriLocation();

                        // Update the storage text
                        showStorageLocation();

                        // See if we can show the start button yet
                        checkStatus();
                    }
                });

        storagePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted.
                        checkStatus();
                        showStorageLocation();
                    }
                });
    }

    // Deal with the storage location set/chosen
    private void getUriTreeFromPreferences() {
        /*
        The user could have specified the actual OpenSong folder (if it already existed),
        or the location to create the OpenSong folder in on first run.  uriTree is the one we have
        permission for and could be either.  If it is the parent folder, uriTreeHome becomes the
        actual child OpenSong folder.  If it is the OpenSong folder, uriTree = uriTreeHome
        Also it could be null!
        */

        String uriTree_String = mainActivityInterface.getPreferences().getMyPreferenceString(requireContext(), "uriTree", "");

        if (uriTree_String!=null && !uriTree_String.equals("")) {
            if (mainActivityInterface.getStorageAccess().lollipopOrLater()) {
                uriTree = Uri.parse(uriTree_String);
            } else {
                uriTree = Uri.fromFile(new File(uriTree_String));
            }
        } else {
            uriTree = null;
            uriTreeHome = null;
        }
        if (uriTree!=null && uriTreeHome==null) {
            uriTreeHome = mainActivityInterface.getStorageAccess().homeFolder(requireActivity(), uriTree, mainActivityInterface);
        }
    }

    private void warningCheck() {
        // If the user tries to set the app storage to OpenSong/Songs/ warn them!
        if (myView.progressText.findViewById(R.id.editText) != null &&
                ((TextInputEditText)myView.progressText.findViewById(R.id.editText)).getText()!=null &&
                Objects.requireNonNull(((TextInputEditText) myView.progressText.findViewById(R.id.editText)).getText()).toString().contains("OpenSong/Songs/")) {
            Snackbar snackbar = make(requireActivity().findViewById(R.id.drawer_layout), R.string.storage_warning,
                    LENGTH_INDEFINITE).setAction(android.R.string.ok, view -> {
            });
            View snackbarView = snackbar.getView();
            TextView snackTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            snackTextView.setMaxLines(4);
            snackbar.show();
        }
    }
    private void saveUriLocation() {
        if (uriTree!=null) {
            // Save the preferences
            // If we are using KitKat, we have to parse the storage
            String uriTree_String = uriTree.toString();
            String uriTreeHome_String = uriTreeHome.toString();
            uriTree_String = uriTree_String.replace("file://","");
            uriTreeHome_String = uriTreeHome_String.replace("file://","");
            mainActivityInterface.getPreferences().setMyPreferenceString(requireContext(), "uriTree", uriTree_String);
            mainActivityInterface.getPreferences().setMyPreferenceString(requireContext(), "uriTreeHome", uriTreeHome_String);
        } else {
            mainActivityInterface.getPreferences().setMyPreferenceString(requireContext(), "uriTree", "");
            mainActivityInterface.getPreferences().setMyPreferenceString(requireContext(), "uriTreeHome", "");
        }
    }

    // Stuff to search for previous installation locations
    private void startSearch() {
        // Deactivate the stuff we shouldn't click on while it is being prepared
        setEnabledOrDisabled(false);

        // Initialise the available storage locations
        locations = new ArrayList<>();
        findLocations();
    }
    private void findLocations() {
        // Run in a new thread
        // This is only for KitKat up to Oreo.  Not allowed for newer versions without
        // Manage Storage permissions, which is overkill!
        new Thread(() -> {
            // Go through the directories recursively and add them to an arraylist
            folder = new File("/storage");
            walkFiles(folder);

            folder = Environment.getExternalStorageDirectory();
            walkFiles(folder);

            // Set up the file list, as long as the user wasn't bored and closed the window!
            requireActivity().runOnUiThread(() -> {
                if (locations != null) {
                    // Hide the  and reenable stuff
                    setEnabledOrDisabled(true);

                    if (locations.size() < 1) {
                        // No previous installations found
                        myView.previousStorageHeading.setVisibility(View.VISIBLE);
                        myView.previousStorageTextView.setVisibility(View.GONE);
                        myView.previousStorageLocations.setVisibility(View.GONE);
                        myView.previousStorageLocations.removeAllViews();
                    } else {
                        myView.previousStorageHeading.setVisibility(View.GONE);
                        myView.previousStorageTextView.setText(getString(R.string.existing_found));
                        myView.previousStorageTextView.setVisibility(View.VISIBLE);
                        myView.previousStorageLocations.setVisibility(View.VISIBLE);
                        myView.previousStorageLocations.removeAllViews();
                        StringBuilder check = new StringBuilder();
                        // Add the locations to the textview
                        for (int x = 0; x < locations.size(); x++) {
                            if (!check.toString().contains("¬" + locations.get(x) + "¬")) {
                                check.append("¬").append(locations.get(x)).append("¬");
                                TextView tv = new TextView(requireContext());
                                tv.setText(locations.get(x));
                                myView.previousStorageLocations.addView(tv);
                            }
                        }
                    }
                }
            });

        }).start();
    }
    private void walkFiles(File root) {

        if (root!=null && root.exists() && root.isDirectory()) {
            File[] list = root.listFiles();
            if (list != null) {
                for (File f : list) {
                    if (f.isDirectory()) {
                        String where = f.getAbsolutePath();
                        String extra;
                        displayWhere(where);

                         if (!where.contains(".estrongs") && !where.contains("com.ttxapps") && where.endsWith("/OpenSong/Songs")) {
                             int count = mainActivityInterface.getStorageAccess().songCountAtLocation(f);
                            extra = count + " Songs";
                            // Found one and it isn't in eStrongs recycle folder or the dropsync temp files!
                            // IV - Add  a leading ¬ and remove trailing /Songs
                            where = "¬" + where.substring(0, where.length() - 6);
                            // IV - For paths identified as Internal storage (more patterns may be needed) remove the parent folder
                            where = where.
                                    replace("¬/storage/sdcard0/", "/").
                                    replace("¬/storage/emulated/0/", "/").
                                    replace("¬/storage/emulated/legacy/", "/").
                                    replace("¬/storage/self/primary/", "/");
                            if (where.startsWith("¬")) {
                                // IV - Handle other paths as 'External'
                                where = where.substring(10);
                                extra = extra + ", " + this.getResources().getString(R.string.storage_ext) + " " + where.substring(0, where.indexOf("/"));
                                where = where.substring(where.indexOf("/"));
                            }
                            where = "(" + extra + "): " + where;
                            locations.add(where);
                        }
                        folder = f;
                        walkFiles(f);
                    }
                }
            }
        }
    }
    private void displayWhere(String msg) {
        final String str = msg;
        requireActivity().runOnUiThread(() -> myView.previousStorageTextView.setText(str));
    }

    // Deal with allowing or hiding the start button
    private void checkStatus() {
        if (isStorageGranted() && isStorageSet() && isStorageValid()) {
            myView.firstRun.setVisibility(View.GONE);
            myView.startApp.setVisibility(View.VISIBLE);
            pulseButton(myView.startApp);
            myView.setStorage.clearAnimation();
        } else {
            myView.firstRun.setVisibility(View.VISIBLE);
            myView.startApp.clearAnimation();
            myView.startApp.setVisibility(View.GONE);
            myView.setStorage.setVisibility(View.VISIBLE);
            pulseButton(myView.setStorage);
        }
    }
    private void setEnabledOrDisabled(boolean what) {
        myView.startApp.setEnabled(what);
        myView.setStorage.setEnabled(what);
        myView.findStorage.setEnabled(what);

        if (!what) {
            myView.previousStorageTextView.setVisibility(View.VISIBLE);
        } else {
            myView.previousStorageTextView.setVisibility(View.GONE);
        }
    }
    private void goToSongs() {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.setStorageLocationFragment, false)
                .build();
        NavHostFragment.findNavController(this).navigate(R.id.bootUpFragment, null, navOptions);
    }
    private void pulseButton(View v) {
        CustomAnimation ca = new CustomAnimation();
        ca.pulse(requireActivity(), v);
    }

    // Below are some checks that will be called to see if we are good to go
    // Firstly check if we have granted the storage permission.  This is the first check
    private void checkStoragePermission() {
        if (isStorageGranted()) {
            // Permission has been granted, so set the storage location
            showStorageLocation();

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Permission hasn't been allowed and we are due to explain why
            showStorageRationale();
            requestStorage();

        } else {
            // Storage permission has not been granted.  Launch the request to allow it
            requestStorage();
        }
    }
    private boolean isStorageGranted() {
        return ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void showStorageRationale() {
        try {
            Snackbar.make(myView.mainpage, R.string.storage_rationale,
                    LENGTH_INDEFINITE).setAction(android.R.string.ok, view -> storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Checks for the storage being ok to proceed
    private boolean isStorageSet() {
        return (uriTreeHome!=null && uriTree!=null && !uriTreeHome.toString().isEmpty() && !uriTree.toString().isEmpty());
    }
    private boolean isStorageValid() {
        return (isStorageSet() && mainActivityInterface.getStorageAccess().uriTreeValid(getContext(),uriTree));
    }
    private void notWriteable() {
        uriTree = null;
        uriTreeHome = null;
        mainActivityInterface.getShowToast().doIt(requireActivity(), getString(R.string.storage_notwritable));
        showStorageLocation();
    }

    // Now deal with getting a suitable storage location
    private void requestStorage() {
        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    private void chooseStorageLocation() {
        if (isStorageGranted()) {
            Intent intent;
            if (mainActivityInterface.getStorageAccess().lollipopOrLater()) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // IV - 'Commented in' this extra to try to always show internal and sd card storage
                intent.putExtra("android.content.extra.SHOW_ADVANCED", true);

                folderChooser.launch(intent);

            } else {
                mainActivityInterface.navigateToFragment(null,R.id.kitKatFolderChoose);

            }
        } else {
            checkStoragePermission();
        }
    }

    private void kitKatDealWithUri() {
        if (mainActivityInterface.getWhattodo()!=null &&
                mainActivityInterface.getWhattodo().startsWith("kitkat:")) {
            String location = mainActivityInterface.getWhattodo().replace("kitkat:","");
            if (!location.equals("null") && !location.isEmpty()) {
                File kitkatlocation = new File(mainActivityInterface.getWhattodo().replace("kitkat:", ""));
                mainActivityInterface.setWhattodo(null);
                uriTree = Uri.fromFile(kitkatlocation);
                uriTreeHome = mainActivityInterface.getStorageAccess().homeFolder(requireContext(), uriTree, mainActivityInterface);

                // Save the location uriTree and uriTreeHome
                saveUriLocation();
                showStorageLocation();
                checkStatus();
            }
            mainActivityInterface.setWhattodo(null);
        }
    }
    private void lollipopDealWithUri(Intent resultData) {
        // This is the newer version for Lollipop+ This is preferred!
        if (resultData != null) {
            uriTree = resultData.getData();
        } else {
            uriTree = null;
        }
        if (uriTree != null) {
            requireActivity().getContentResolver().takePersistableUriPermission(uriTree,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        uriTreeHome = mainActivityInterface.getStorageAccess().homeFolder(requireActivity(), uriTree, mainActivityInterface);
        if (uriTree == null || uriTreeHome == null) {
            notWriteable();
        }
        if (uriTree != null) {
            checkStatus();
        }
    }

    // Here we deal with displaying (and processing) the chosen storage location
    private void showStorageLocation() {
        uriTreeHome = mainActivityInterface.getStorageAccess().fixBadStorage(uriTreeHome);
        if (uriTreeHome==null) {
            uriTree = null;
            saveUriLocation();
            ((TextInputEditText)myView.progressText.findViewById(R.id.editText)).setText("");
        } else {
            String[] niceLocation = mainActivityInterface.getStorageAccess().niceUriTree(getContext(), mainActivityInterface, uriTreeHome);
            String outputText = niceLocation[1] + "\n" + niceLocation[0];
            ((TextInputEditText)myView.progressText.findViewById(R.id.editText)).setText(outputText);
            warningCheck();
        }
        checkStatus();
    }

}