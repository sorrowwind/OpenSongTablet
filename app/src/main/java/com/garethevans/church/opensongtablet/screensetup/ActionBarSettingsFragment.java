package com.garethevans.church.opensongtablet.screensetup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.SettingsActionbarBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ActionBarSettingsFragment extends Fragment {

    private SettingsActionbarBinding myView;
    private MainActivityInterface mainActivityInterface;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = SettingsActionbarBinding.inflate(inflater,container,false);

        mainActivityInterface.updateToolbar(getString(R.string.actionbar_display));

        // Set up preferences and view settings
        setupPreferences();

        return myView.getRoot();
    }

    private void setupPreferences() {
        // The song title and author
        myView.titleTextSize.setHintTextSize(mainActivityInterface.getPreferences().getMyPreferenceFloat(requireContext(),"songTitleSize",13.0f));
        myView.authorTextSize.setHintTextSize(mainActivityInterface.getPreferences().getMyPreferenceFloat(requireContext(),"songAuthorSize",11.0f));

        // The sliders
        float titleTextSize = checkMin(mainActivityInterface.getPreferences().getMyPreferenceFloat(requireContext(),"songTitleSize", 13),6);
        float authorTextSize = checkMin(mainActivityInterface.getPreferences().getMyPreferenceFloat(requireContext(),"songAuthorSize", 11),6);
        float batteryTextSize = checkMin(mainActivityInterface.getPreferences().getMyPreferenceFloat(requireContext(),"batteryTextSize", 9),6);
        float timeTextSize = checkMin(mainActivityInterface.getPreferences().getMyPreferenceFloat(requireContext(),"clockTextSize", 9),6);
        int batteryDialSize = (int)checkMin(mainActivityInterface.getPreferences().getMyPreferenceInt(requireContext(),"batteryDialThickness", 4),1);

        myView.titleTextSize.setValue(titleTextSize);
        myView.titleTextSize.setHint(timeTextSize+"sp");
        myView.titleTextSize.setLabelFormatter(value -> ((int)value)+"sp");
        myView.titleTextSize.setHintTextSize(titleTextSize);
        myView.authorTextSize.setValue(authorTextSize);
        myView.authorTextSize.setHint(authorTextSize+"sp");
        myView.authorTextSize.setLabelFormatter(value -> ((int)value)+"sp");
        myView.authorTextSize.setHintTextSize(authorTextSize);
        myView.batteryDialSize.setValue((int)batteryDialSize);
        myView.batteryDialSize.setLabelFormatter(value -> ((int)value)+"px");
        myView.batteryTextSize.setValue(batteryTextSize);
        myView.batteryTextSize.setLabelFormatter(value -> ((int)value)+"px");

        // The switches
        myView.autohideActionBar.setChecked(mainActivityInterface.getPreferences().getMyPreferenceBoolean(requireContext(),"hideActionBar",false));
        showOrHideView(mainActivityInterface.getPreferences().getMyPreferenceBoolean(requireContext(),"batteryDialOn",true),
                true,myView.batteryDialOnOff,myView.batteryDialSize);
        showOrHideView(mainActivityInterface.getPreferences().getMyPreferenceBoolean(requireContext(),"batteryTextOn",true),
                true,myView.batteryTextOnOff,myView.batteryDialSize);
        showOrHideView(mainActivityInterface.getPreferences().getMyPreferenceBoolean(requireContext(),"clockOn",true),
                true,myView.clockTextOnOff,myView.timeLayout);
        myView.clock24hrOnOff.setChecked(mainActivityInterface.getPreferences().getMyPreferenceBoolean(requireContext(),"clock24hFormat",true));
        
        // The listeners
        myView.titleTextSize.addOnChangeListener(new MyOnChangeListener("songTitleSize",true));
        myView.authorTextSize.addOnChangeListener(new MyOnChangeListener("songAuthorSize",true));
        myView.batteryDialSize.addOnChangeListener(new MyOnChangeListener("batteryDialThickness",true));
        myView.batteryTextSize.addOnChangeListener(new MyOnChangeListener("batteryTextSize",true));
        myView.timeTextSize.addOnChangeListener(new MyOnChangeListener("clockTextSize",true));
        myView.titleTextSize.addOnSliderTouchListener(new MyOnSliderTouch("songTitleSize",true));
        myView.authorTextSize.addOnSliderTouchListener(new MyOnSliderTouch("songAuthorSize",true));
        myView.batteryDialSize.addOnSliderTouchListener(new MyOnSliderTouch("batteryDialThickness",true));
        myView.batteryTextSize.addOnSliderTouchListener(new MyOnSliderTouch("batteryTextSize",true));
        myView.timeTextSize.addOnSliderTouchListener(new MyOnSliderTouch("clockTextSize",true));

        myView.autohideActionBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateActionBar("hideActionBar",-1,0.0f,!isChecked);
            mainActivityInterface.getPreferences().setMyPreferenceBoolean(requireContext(),"hideActionBar",isChecked);
        });
        myView.batteryDialOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showOrHideView(isChecked,false ,myView.batteryDialOnOff, myView.batteryDialSize);
            updateActionBar("batteryDialOn",-1,0.0f,isChecked);
            mainActivityInterface.getPreferences().setMyPreferenceBoolean(requireContext(),"batteryDialOn",isChecked);
        });
        myView.batteryTextOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showOrHideView(isChecked,false ,myView.batteryTextOnOff, myView.batteryTextSize);
            updateActionBar("batteryTextOn",-1,0.0f,isChecked);
            mainActivityInterface.getPreferences().setMyPreferenceBoolean(requireContext(),"batteryTextOn",isChecked);
        });
        myView.clockTextOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showOrHideView(isChecked,false ,myView.clockTextOnOff, myView.timeLayout);
            updateActionBar("clockOn",-1,0.0f,isChecked);
            mainActivityInterface.getPreferences().setMyPreferenceBoolean(requireContext(),"clockOn",isChecked);
        });
        myView.clock24hrOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateActionBar("clock24hFormat",-1,0.0f,isChecked);
            mainActivityInterface.getPreferences().setMyPreferenceBoolean(requireContext(),"clock24hFormat",isChecked);
        });
    }

    private void showOrHideView(boolean show, boolean setSwitch, SwitchMaterial switchMaterial, LinearLayout linearLayout) {
        if (show) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
        if (setSwitch) {
            switchMaterial.setChecked(show);
        }
    }

    private float checkMin (float value, float min) {
        return Math.max(value,min);
    }

    private class MyOnChangeListener implements Slider.OnChangeListener {

        private final boolean isfloat;
        private final String prefName;

        MyOnChangeListener(String prefName, boolean isfloat) {
            this.prefName = prefName;
            this.isfloat = isfloat;
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            if (isfloat) {
                if (prefName.equals("songTitleSize")) {
                    myView.titleTextSize.setHint((int)value + "sp");
                    myView.titleTextSize.setHintTextSize(value);
                } else if (prefName.equals("songAuthorSize")) {
                    myView.authorTextSize.setHint((int)value + "sp");
                    myView.authorTextSize.setHintTextSize(value);
                } else {
                    updateActionBar(prefName, -1, value, false);
                }
            } else {
                updateActionBar(prefName, (int)value, 0.0f, false);
            }
        }
    }
    private class MyOnSliderTouch implements Slider.OnSliderTouchListener {

        private final String prefName;
        private final boolean isfloat;

        MyOnSliderTouch(String prefName, boolean isfloat) {
            this.prefName = prefName;
            this.isfloat = isfloat;
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) { }

        @SuppressLint("RestrictedApi")
        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {
            // Save the preference
            if (isfloat) {
                mainActivityInterface.getPreferences().setMyPreferenceFloat(requireContext(),prefName, slider.getValue());
            } else {
                mainActivityInterface.getPreferences().setMyPreferenceInt(requireContext(),prefName, (int)slider.getValue());
            }
        }
    }

    private void updateActionBar(String prefName, int intval, float floatval, boolean isvisible) {
        mainActivityInterface.updateActionBarSettings(prefName, intval, floatval, isvisible);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myView = null;
    }
}
