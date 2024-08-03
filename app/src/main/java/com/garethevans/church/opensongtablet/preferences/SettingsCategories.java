package com.garethevans.church.opensongtablet.preferences;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.appdata.InformationBottomSheet;
import com.garethevans.church.opensongtablet.databinding.SettingsCategoriesBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.textview.MaterialTextView;

public class SettingsCategories extends Fragment {

    private SettingsCategoriesBinding myView;
    private MainActivityInterface mainActivityInterface;
    ActivityResultLauncher<String[]> nearbyConnectionsPermission;
    ActivityResultLauncher<String[]> webserverPermission;
    private String settings_string="", mode_presenter_string="", presenter_mode_string="",
            mode_stage_string="", stage_mode_string="", performance_mode_string="",
            play_services_error_string="", midi_description_string="", not_available_string="",
            location_string="", permissions_refused_string="", wait_string="", nearby_string="";

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.updateToolbar(settings_string);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mainActivityInterface = (MainActivityInterface) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        myView = SettingsCategoriesBinding.inflate(inflater, container, false);

        prepareStrings();

        // Hide the features not available to this device
        hideUnavailable();

        // Set the mode in the button
        setModeText();

        // Set up the permission launcher for Nearby
        setPermissions();

        // Set listeners
        setListeners();

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            settings_string = getString(R.string.settings);
            mode_presenter_string = getString(R.string.mode_presenter);
            presenter_mode_string = getString(R.string.presenter_mode);
            mode_stage_string = getString(R.string.mode_stage);
            stage_mode_string = getString(R.string.stage_mode);
            performance_mode_string = getString(R.string.performance_mode);
            play_services_error_string = getString(R.string.play_services_error);
            midi_description_string = getString(R.string.midi_description);
            not_available_string = getString(R.string.not_available);
            location_string = getString(R.string.location);
            nearby_string = getString(R.string.nearby_devices);
            permissions_refused_string = getString(R.string.permissions_refused);
            wait_string = getString(R.string.index_songs_wait);
        }
    }
    private void hideUnavailable() {
        // If the user doesn't have Google API availability, they can't use the connect feature
        if (getContext()!=null) {
            setPlayEnabled(mainActivityInterface.getAlertChecks().getHasPlayServices());

            // If they don't have midi functionality, remove this
            setMidiEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI));
        } else {
            setPlayEnabled(false);
            setMidiEnabled(false);
        }
    }

    private void setModeText() {
        String mode;
        String getMode = mainActivityInterface.getMode();

        if (getMode.equals(mode_presenter_string)) {
            mode = presenter_mode_string;
        } else if (getMode.equals(mode_stage_string)) {
            mode = stage_mode_string;
        } else {
            mode = performance_mode_string;
        }
        myView.modeButton.setHint(mode);
    }

    private void setPlayEnabled(boolean enabled) {
        myView.connectButton.setEnabled(enabled);
        myView.connectLine.setEnabled(enabled);
        if (!enabled) {
            myView.connectButton.setHint(play_services_error_string);
        }
    }

    private void setMidiEnabled(boolean enabled) {
        String message;
        if (enabled) {
            message = midi_description_string;
        } else {
            message = not_available_string;
        }
        myView.midiButton.setEnabled(enabled);
        myView.midiButton.setHint(message);
    }

    private void setPermissions() {
        nearbyConnectionsPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (mainActivityInterface.getAppPermissions().hasGooglePlay() &&
                    mainActivityInterface.getAppPermissions().hasNearbyPermissions()) {
                mainActivityInterface.navigateToFragment(null, R.id.nearbyConnectionsFragment);

            } else {
                // notify user
                String permission_needed;
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
                    permission_needed = nearby_string;
                } else {
                    permission_needed = location_string;
                }
                InformationBottomSheet informationBottomSheet = new InformationBottomSheet(permission_needed,
                        permissions_refused_string, settings_string, "appPrefs");
                informationBottomSheet.show(mainActivityInterface.getMyFragmentManager(), "InformationBottomSheet");
            }
        });
        webserverPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (mainActivityInterface.getAppPermissions().hasWebServerPermission()) {
                mainActivityInterface.navigateToFragment(null, R.id.webServerFragment);
            }
        });
    }

    private void setListeners() {
        myView.storageButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.storage_graph));
        myView.displayButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.display_graph));
        myView.actionsButton.setOnClickListener(v -> {
            if (mainActivityInterface.getSongListBuildIndex().getIndexComplete()) {
                mainActivityInterface.navigateToFragment(null, R.id.actions_graph);
            } else {
                String progressText = "";
                if (mainActivityInterface.getSongMenuFragment()!=null) {
                    MaterialTextView progressView = mainActivityInterface.getSongMenuFragment().getProgressText();
                    if (progressView!=null && progressView.getText()!=null) {
                        progressText = " " + progressView.getText().toString();
                    }
                }
                mainActivityInterface.getShowToast().doIt(wait_string + progressText);
            }
        });
        myView.setActionsButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.set_graph));
        myView.gesturesButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.control_graph));
        myView.connectButton.setOnClickListener(v -> {
            // Check we have the required permissions and if so the launcher navigates to the connect fragment
            mainActivityInterface.setWhattodo("nearby");
            nearbyConnectionsPermission.launch(mainActivityInterface.getAppPermissions().getNearbyPermissions());
            mainActivityInterface.getStorageAccess().updateFileActivityLog(mainActivityInterface.getAppPermissions().getPermissionsLog());
            mainActivityInterface.getAppPermissions().resetPermissionsLog();
        });
        myView.webServerButton.setOnClickListener(v -> {
            // Check for permissions once we get to the page
            mainActivityInterface.navigateToFragment(null, R.id.webServerFragment);
        });
        myView.modeButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.modeFragment));
        myView.midiButton.setOnClickListener(v -> {
            // This button is only available if we are running Marshmallow or later
            mainActivityInterface.navigateToFragment(null, R.id.midi_graph);
        });
        myView.profilesButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.profileFragment));
        myView.ccliButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.settingsCCLI));
        myView.utilitiesButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.utilities_graph));
        myView.aboutButton.setOnClickListener(v -> mainActivityInterface.navigateToFragment(null, R.id.about_graph));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myView = null;
    }
}