package com.garethevans.church.opensongtablet.utilities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.SettingsUtilitiesBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

public class UtilitiesMenuFragment extends Fragment {

    private MainActivityInterface mainActivityInterface;
    private SettingsUtilitiesBinding myView;
    private String beatBuddy_string = "", utilities_string="", aeros_string="",
            deeplink_database_utilities="";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.updateToolbar(utilities_string);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        myView = SettingsUtilitiesBinding.inflate(inflater,container,false);

        prepareStrings();

        // Set up listeners
        setupListeners();

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            utilities_string = getString(R.string.utilities);
            beatBuddy_string = getString(R.string.deeplink_beatbuddy_options);
            aeros_string = getString(R.string.deeplink_aeros);
            deeplink_database_utilities = getString(R.string.deeplink_database_utilities);
        }
    }

    private void setupListeners() {
        myView.soundMeter.setOnClickListener(v -> {
            SoundLevelBottomSheet soundLevelBottomSheet = new SoundLevelBottomSheet();
            soundLevelBottomSheet.show(mainActivityInterface.getMyFragmentManager(),"soundLevelBottomSheet");
        });
        myView.tuner.setOnClickListener(v -> {
            TunerBottomSheet tunerBottomSheet = new TunerBottomSheet();
            tunerBottomSheet.show(mainActivityInterface.getMyFragmentManager(),"tunerBottomSheet");
        });
        myView.beatBuddy.setOnClickListener(v -> mainActivityInterface.navigateToFragment(beatBuddy_string,0));
        myView.aeros.setOnClickListener(v -> mainActivityInterface.navigateToFragment(aeros_string,0));
        myView.databaseOptions.setOnClickListener(v -> mainActivityInterface.navigateToFragment(deeplink_database_utilities,0));
    }

}
