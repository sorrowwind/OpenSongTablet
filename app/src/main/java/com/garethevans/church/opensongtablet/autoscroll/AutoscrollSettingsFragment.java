package com.garethevans.church.opensongtablet.autoscroll;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.SettingsAutoscrollBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

public class AutoscrollSettingsFragment extends Fragment {

    private SettingsAutoscrollBinding myView;
    private MainActivityInterface mainActivityInterface;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = SettingsAutoscrollBinding.inflate(inflater, container, false);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Set up the views
        setupViews();

        // Set up the listeners
        setupListeners();

        return myView.getRoot();
    }

    private void setupViews() {
        // Get the defaults from preferences
        myView.defaultDuration.setText(mainActivityInterface.getPreferences().getMyPreferenceInt(requireContext(),
                "autoscrollDefaultSongLength", 180)+"");
        myView.defaultDelay.setText(mainActivityInterface.getPreferences().getMyPreferenceInt(requireContext(),
                "autoscrollDefaultSongPreDelay", 20)+"");
        if (mainActivityInterface.getPreferences().getMyPreferenceBoolean(requireContext(),
                "autoscrollUseDefaultTime", true)) {
            myView.autoscrollDefault.setChecked(true);
        } else {
            myView.autoscrollPrompt.setChecked(true);
        }
        myView.autostartAutoscroll.setChecked(mainActivityInterface.getPreferences().getMyPreferenceBoolean(requireContext(),
                "autoscrollAutoStart", false));
        // Get song values
        myView.songDuration.setText(mainActivityInterface.getSong().getAutoscrolllength());
        myView.songDelay.setText(mainActivityInterface.getSong().getAutoscrolldelay());
        // Check audio link file
        mainActivityInterface.getAutoscroll().checkLinkAudio(requireContext(),mainActivityInterface,
                myView.linkAudio, myView.songDuration, myView.songDelay,getStringToInt(myView.songDelay.getText().toString()));

    }

    private void setupListeners() {
        myView.songDuration.addTextChangedListener(new MyTextWatcher("songDuration"));
        myView.songDelay.addTextChangedListener(new MyTextWatcher("songDelay"));
        myView.defaultDuration.addTextChangedListener(new MyTextWatcher("defaultDuration"));
        myView.defaultDelay.addTextChangedListener(new MyTextWatcher("defaultDelay"));
        myView.autostartAutoscroll.setOnCheckedChangeListener((compoundButton, b) -> mainActivityInterface.getPreferences().setMyPreferenceBoolean(requireContext(),"autoscrollAutoStart", b));
        myView.autoscrollPrompt.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                mainActivityInterface.getPreferences().setMyPreferenceBoolean(requireContext(),
                        "autoscrollUseDefaultTime",b);
            }
        });
        myView.autoscrollDefault.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mainActivityInterface.getPreferences().setMyPreferenceBoolean(requireContext(),
                        "autoscrollUseDefaultTime",b);
            }
        });
        myView.learnAutoscroll.setOnClickListener(v -> learnAutoscroll());
        myView.nestedScrollView.setFabToAnimate(myView.startStopAutoscroll);

        myView.startStopAutoscroll.setOnClickListener(v -> startStopAutoscroll());
    }

    private class MyTextWatcher implements TextWatcher {

        private final String which;

        MyTextWatcher(String which) {
            this.which = which;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            // These text boxes can only be numbers
            int song_delay = getStringToInt(myView.songDelay.getText().toString());
            int song_duration = getStringToInt(myView.songDuration.getText().toString());
            int default_duration = getStringToInt(myView.defaultDuration.getText().toString());
            int default_delay = getStringToInt(myView.defaultDelay.getText().toString());

            switch (which) {
                case "defaultDuration":
                    if (default_duration<default_delay) {
                        myView.defaultDelay.setText(default_duration+"");
                        mainActivityInterface.getPreferences().setMyPreferenceInt(requireContext(),
                                "autoscrollDefaultSongPreDelay", default_duration);
                    }
                    mainActivityInterface.getPreferences().setMyPreferenceInt(requireContext(),
                            "autoscrollDefaultSongLength", default_duration);
                    break;
                case "defaultDelay":
                    if (default_delay>default_duration) {
                        myView.defaultDuration.setText(default_delay+"");
                        mainActivityInterface.getPreferences().setMyPreferenceInt(requireContext(),
                                "autoscrollDefaultSongLength", default_delay);
                    }
                    mainActivityInterface.getPreferences().setMyPreferenceInt(requireContext(),
                            "autoscrollDefaultSongPreDelay", default_delay);
                    break;
                case "songDuration":
                    if (song_duration<song_delay) {
                        myView.songDelay.setText(song_duration+"");
                        mainActivityInterface.getSong().setAutoscrolldelay(song_duration+"");
                    }
                    mainActivityInterface.getSong().setAutoscrolllength(song_duration+"");
                    mainActivityInterface.getSaveSong().updateSong(requireContext(),mainActivityInterface);
                    break;
                case "songDelay":
                    if (song_delay>song_duration) {
                        myView.songDuration.setText(song_delay+"");
                        mainActivityInterface.getSong().setAutoscrolllength(song_delay+"");
                    }
                    mainActivityInterface.getSong().setAutoscrolldelay(song_delay+"");
                    mainActivityInterface.getSaveSong().updateSong(requireContext(),mainActivityInterface);
                    break;
            }
        }
    }
    private int getStringToInt(String string) {
        // A chance to check the value is a number.  If not return 0;
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return 0;
        }
    }

    private void learnAutoscroll() {
        // This sends an action to the performance mode to start the process
    }

    private void startStopAutoscroll() {
        if (mainActivityInterface.getAutoscroll().getIsAutoscrolling()) {
            mainActivityInterface.getAutoscroll().stopAutoscroll();
            myView.startStopAutoscroll.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                    R.drawable.ic_play_white_36dp));
        } else {
            mainActivityInterface.getAutoscroll().startAutoscroll();
            myView.startStopAutoscroll.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                    R.drawable.ic_stop_white_36dp));
        }
    }

}
