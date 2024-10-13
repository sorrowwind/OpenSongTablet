package com.garethevans.church.opensongtablet.songprocessing;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.customviews.ExposedDropDownArrayAdapter;
import com.garethevans.church.opensongtablet.databinding.EditSongFeaturesBinding;
import com.garethevans.church.opensongtablet.interfaces.EditSongFragmentInterface;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

import java.util.ArrayList;

public class EditSongFragmentFeatures extends Fragment {

    private EditSongFeaturesBinding myView;
    private MainActivityInterface mainActivityInterface;
    private EditSongFragmentInterface editSongFragmentInterface;
    private String whichLink = "audio";
    private String pad_auto_string="";
    private String link_audio_string="";
    private String off_string="";
    private String tempo_string="";
    private String bpm_string="";
    private String link_youtube_string="";
    private String link_web_string="";
    private String link_file_string="";
    private String custom_string="";
    private String link_string="";
    private String online_search_string="";
    private String use_default_string = "";
    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "EditSongFeatures";
    private String[] key_choice_string={};
    private ArrayList<String> instruments = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
        editSongFragmentInterface = (EditSongFragmentInterface) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.getThreadPoolExecutor().execute(() -> {
            prepareStrings();

            // Set up the values
            setupValues();

            // Set up the listeners
            setupListeners();

        });
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = EditSongFeaturesBinding.inflate(inflater, container, false);

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            key_choice_string = getResources().getStringArray(R.array.key_choice);
            // Fix the default keys to the user preferred chord format B>H, #>is, b>es, etc.
            key_choice_string = mainActivityInterface.getTranspose().fixTempKeys(key_choice_string);

            pad_auto_string = getString(R.string.pad_auto);
            link_audio_string = getString(R.string.link_audio);
            off_string = getString(R.string.off);
            tempo_string = getString(R.string.tempo);
            bpm_string = getString(R.string.bpm);
            link_youtube_string = getString(R.string.link_youtube);
            link_web_string = getString(R.string.link_web);
            link_file_string = getString(R.string.link_file);
            custom_string = getString(R.string.custom);
            link_string = getString(R.string.link);
            String search_string = getString(R.string.search);
            String online_string = getString(R.string.online);
            online_search_string = search_string +" ("+ online_string +")";
            use_default_string = getString(R.string.use_default);
            instruments = mainActivityInterface.getChordDisplayProcessing().getSongInstruments();
        }
    }
    private void setupValues() {
        if (mainActivityInterface.getTempSong()==null) {
            mainActivityInterface.setTempSong(mainActivityInterface.getSong());
        }

        // The key
        mainActivityInterface.getTranspose().checkOriginalKeySet(mainActivityInterface.getTempSong());
        if (getContext()!=null) {
            mainActivityInterface.getMainHandler().post(() -> {
                ExposedDropDownArrayAdapter keyArrayAdapter1 = new ExposedDropDownArrayAdapter(getContext(),
                        myView.key, R.layout.view_exposed_dropdown_item, key_choice_string);
                myView.key.setAdapter(keyArrayAdapter1);
                myView.key.setText(mainActivityInterface.getTranspose().getFixedKey(mainActivityInterface.getTempSong().getKey()));
                ExposedDropDownArrayAdapter keyArrayAdapter2 = new ExposedDropDownArrayAdapter(getContext(),
                        myView.originalkey, R.layout.view_exposed_dropdown_item, key_choice_string);
                myView.originalkey.setAdapter(keyArrayAdapter2);
                myView.originalkey.setText(mainActivityInterface.getTranspose().getFixedKey(mainActivityInterface.getTempSong().getKeyOriginal()));
            });
        }
        mainActivityInterface.getMainHandler().post(() -> myView.searchOnline.setText(online_search_string));

        // The capo
        setupCapo();

        // Check for overrides
        if (mainActivityInterface.getProcessSong().getHasAbcOffOverride(mainActivityInterface.getTempSong())) {
            myView.overrideAbcSlider.setSliderPos(2);
        } else if (mainActivityInterface.getProcessSong().getHasAbcOnOverride(mainActivityInterface.getTempSong())) {
            myView.overrideAbcSlider.setSliderPos(1);
        } else {
            myView.overrideAbcSlider.setSliderPos(0);
        }

        // The pad file
        ArrayList<String> padfiles = new ArrayList<>();
        padfiles.add(pad_auto_string);
        padfiles.add(link_audio_string);
        padfiles.add(off_string);
        if (getContext()!=null) {
            mainActivityInterface.getMainHandler().post(() -> {
                ExposedDropDownArrayAdapter padArrayAdapter = new ExposedDropDownArrayAdapter(getContext(),
                        myView.pad, R.layout.view_exposed_dropdown_item, padfiles);
                myView.pad.setAdapter(padArrayAdapter);
                myView.pad.setText(niceTextFromPref(mainActivityInterface.getTempSong().getPadfile()));
            });
        }
        if (mainActivityInterface.getTempSong().getPadfile() == null ||
                mainActivityInterface.getTempSong().getPadfile().isEmpty()) {
            mainActivityInterface.getTempSong().setPadfile("auto");
        }

        // The loop
        if (mainActivityInterface.getTempSong().getPadloop()!=null) {
            mainActivityInterface.getMainHandler().post(() -> myView.loop.setChecked(mainActivityInterface.getTempSong().getPadloop().equals("true")));
        } else {
            mainActivityInterface.getMainHandler().post(() -> myView.loop.setChecked(false));
        }

        // The tempo
        ArrayList<String> tempos = new ArrayList<>();
        tempos.add("");
        for (int x = 40; x < 300; x++) {
            tempos.add(String.valueOf(x));
        }
        if (getContext()!=null) {
            mainActivityInterface.getMainHandler().post(() -> {
                ExposedDropDownArrayAdapter tempoArrayAdapter = new ExposedDropDownArrayAdapter(getContext(),
                        myView.tempo, R.layout.view_exposed_dropdown_item, tempos);
                myView.tempo.setAdapter(tempoArrayAdapter);
                myView.tempo.setHint(tempo_string + " ("+bpm_string+")");
                myView.tempo.setText(mainActivityInterface.getTempSong().getTempo());
                mainActivityInterface.getMetronome().initialiseTapTempo(myView.tapTempo,myView.timesig,null,null,myView.tempo);
            });
        }

        // The timesig
        ArrayList<String> timesigs = new ArrayList<>();
        for (int divisions = 1; divisions <= 16; divisions++) {
            if (divisions == 1 || divisions == 2 || divisions == 4 || divisions == 8 || divisions == 16) {
                for (int beats = 1; beats <= 16; beats++) {
                    timesigs.add(beats + "/" + divisions);
                }
            }
        }
        if (getContext()!=null) {
            mainActivityInterface.getMainHandler().post(() -> {
                ExposedDropDownArrayAdapter timesigArrayAdapter = new ExposedDropDownArrayAdapter(getContext(),
                        myView.timesig, R.layout.view_exposed_dropdown_item, timesigs);
                myView.timesig.setAdapter(timesigArrayAdapter);
                myView.timesig.setText(mainActivityInterface.getTempSong().getTimesig());
            });
        }

        // The preferred instrument for the song
        if (getContext()!=null) {
            mainActivityInterface.getMainHandler().post(() -> {
                // Add the default text temporarily
                instruments.add(0, use_default_string);
                ExposedDropDownArrayAdapter instrumentsAdapter = new ExposedDropDownArrayAdapter(getContext(), myView.preferredInstrument, R.layout.view_exposed_dropdown_item, instruments);
                myView.preferredInstrument.setAdapter(instrumentsAdapter);
                String chosen = mainActivityInterface.getChordDisplayProcessing().getSongInstrumentNice(mainActivityInterface.getTempSong().getPreferredInstrument());
                myView.preferredInstrument.setText(chosen);
                // Now take the default out of the arraylist
                instruments.remove(0);
            });
        }

        // Duration and delay
        if (mainActivityInterface.getTempSong().getAutoscrolllength()==null ||
                mainActivityInterface.getTempSong().getAutoscrolllength().isEmpty()) {
            mainActivityInterface.getTempSong().setAutoscrolllength("0");
        }
        int[] timeVals = mainActivityInterface.getTimeTools().getMinsSecsFromSecs(Integer.parseInt(mainActivityInterface.getTempSong().getAutoscrolllength()));

        mainActivityInterface.getMainHandler().post(() -> {
            myView.durationMins.setInputType(InputType.TYPE_CLASS_NUMBER);
            myView.durationMins.setDigits("0123456789");
            myView.durationMins.setText(String.valueOf(timeVals[0]));
            myView.durationSecs.setInputType(InputType.TYPE_CLASS_NUMBER);
            myView.durationSecs.setDigits("0123456789");
            myView.durationSecs.setText(String.valueOf(timeVals[1]));
            myView.delay.setInputType(InputType.TYPE_CLASS_NUMBER);
            myView.delay.setDigits("0123456789");
            myView.delay.setText(mainActivityInterface.getTempSong().getAutoscrolldelay());

        // The midi, abc and customchords
            myView.midi.setText(mainActivityInterface.getTempSong().getMidi());
            mainActivityInterface.getProcessSong().editBoxToMultiline(myView.midi);
            myView.abc.setText(mainActivityInterface.getTempSong().getAbc());
            mainActivityInterface.getProcessSong().editBoxToMultiline(myView.abc);
            myView.customChords.setText(mainActivityInterface.getTempSong().getCustomchords());
            mainActivityInterface.getProcessSong().editBoxToMultiline(myView.customChords);
        });
        checkLines();

        // The links
        ArrayList<String> linkOptions = new ArrayList<>();
        linkOptions.add(link_audio_string);
        linkOptions.add(link_youtube_string);
        linkOptions.add(link_web_string);
        linkOptions.add(link_file_string);
        if (getContext()!=null) {
            mainActivityInterface.getMainHandler().post(() -> {
                ExposedDropDownArrayAdapter linkArrayAdapter = new ExposedDropDownArrayAdapter(getContext(),
                        myView.linkType, R.layout.view_exposed_dropdown_item, linkOptions);
                myView.linkType.setAdapter(linkArrayAdapter);
                myView.linkType.setText(link_audio_string);
            });
        }
        setLink();

        // Resize the bottom padding to the soft keyboard height or half the screen height for the soft keyboard (workaround)
        mainActivityInterface.getMainHandler().post(() -> mainActivityInterface.getWindowFlags().adjustViewPadding(mainActivityInterface,myView.resizeForKeyboardLayout));
    }

    private void setupCapo() {
        ArrayList<String> capos = new ArrayList<>();
        if (mainActivityInterface.getTempSong()!=null) {
            capos.add("");
            String songkey = "";
            String origkey = "";
            if (mainActivityInterface.getTempSong().getKey()!=null) {
                songkey = mainActivityInterface.getTempSong().getKey();
            }
            if (mainActivityInterface.getTempSong().getKeyOriginal()!=null) {
                origkey = mainActivityInterface.getTempSong().getKeyOriginal();
            }
            if ((songkey == null || songkey.isEmpty()) && origkey != null && !origkey.isEmpty()) {
                songkey = origkey;
                mainActivityInterface.getTempSong().setKey(origkey);
                String finalOrigkey = origkey;
                mainActivityInterface.getMainHandler().post(() -> myView.key.setText(finalOrigkey));

            }
            if ((origkey == null || origkey.isEmpty()) && songkey != null && !songkey.isEmpty()) {
                origkey = songkey;
                mainActivityInterface.getTempSong().setKeyOriginal(origkey);
                String finalSongkey = songkey;
                mainActivityInterface.getMainHandler().post(() -> myView.originalkey.setText(mainActivityInterface.getTranspose().getFixedKey(finalSongkey)));
            }

            for (int x = 1; x < 12; x++) {
                // If we have a ket set, work out the capo key
                String capokey = "";
                if (songkey != null && !songkey.isEmpty()) {
                    capokey = " (" + mainActivityInterface.getTranspose().transposeChordForCapo(x, "." + songkey) + ")";
                }
                capos.add(x + capokey.replace(".", ""));
            }

            if (getContext() != null) {
                mainActivityInterface.getMainHandler().post(() -> {
                    ExposedDropDownArrayAdapter capoArrayAdapter = new ExposedDropDownArrayAdapter(getContext(),
                            myView.capo, R.layout.view_exposed_dropdown_item, capos);
                    myView.capo.setAdapter(capoArrayAdapter);
                });
            }

            String songcapo = mainActivityInterface.getTempSong().getCapo();
            if (songcapo == null) {
                songcapo = "";
            }

            Log.d(TAG, "songcapo:" + songcapo + "  songkey:" + songkey);
            if (songkey != null && !songkey.isEmpty() && !songcapo.isEmpty()) {
                songcapo = songcapo.replaceAll("\\D", "").trim();
                if (!songcapo.isEmpty()) {
                    songcapo += " (" + mainActivityInterface.getTranspose().transposeChordForCapo(Integer.parseInt(songcapo), "." + songkey) + ")";
                }
            }

            String finalSongcapo = songcapo;
            mainActivityInterface.getMainHandler().post(() -> myView.capo.setText(finalSongcapo.replace(".", "")));
        }
    }

    private void setLink() {
        String linkvalue;
        switch (whichLink) {
            case "audio":
            default:
                linkvalue = mainActivityInterface.getTempSong().getLinkaudio();
                break;
            case "youtube":
                linkvalue = mainActivityInterface.getTempSong().getLinkyoutube();
                break;
            case "web":
                linkvalue = mainActivityInterface.getTempSong().getLinkweb();
                break;
            case "other":
                linkvalue = mainActivityInterface.getTempSong().getLinkother();
                break;
        }
        mainActivityInterface.getMainHandler().post(() -> myView.linkValue.setText(linkvalue));
    }

    private void editLink(String value) {
        switch (whichLink) {
            case "audio":
            default:
                mainActivityInterface.getTempSong().setLinkaudio(value);
                break;
            case "youtube":
                mainActivityInterface.getTempSong().setLinkyoutube(value);
                break;
            case "web":
                mainActivityInterface.getTempSong().setLinkweb(value);
                break;
            case "other":
                mainActivityInterface.getTempSong().setLinkother(value);
                break;
        }
    }

    private void checkLines() {
        mainActivityInterface.getMainHandler().post(() -> {
            mainActivityInterface.getProcessSong().stretchEditBoxToLines(myView.midi, 2);
            mainActivityInterface.getProcessSong().stretchEditBoxToLines(myView.abc, 2);
        });
    }
    private void setupListeners() {
        mainActivityInterface.getMainHandler().post(() -> {
            myView.searchOnline.setOnClickListener(view -> {
                GetBPMBottomSheet getBPMBottomSheet = new GetBPMBottomSheet(EditSongFragmentFeatures.this);
                getBPMBottomSheet.show(mainActivityInterface.getMyFragmentManager(), "GetBPMBottomSheet");
            });
            // The simple text only fields
            myView.key.addTextChangedListener(new MyTextWatcher("key"));
            myView.originalkey.addTextChangedListener(new MyTextWatcher("originalkey"));
            myView.capo.addTextChangedListener(new MyTextWatcher("capo"));
            myView.pad.addTextChangedListener(new MyTextWatcher("pad"));
            myView.loop.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    mainActivityInterface.getTempSong().setPadloop("true");
                } else {
                    mainActivityInterface.getTempSong().setPadloop("false");
                }
            });
            myView.tempo.addTextChangedListener(new MyTextWatcher("tempo"));
            myView.timesig.addTextChangedListener(new MyTextWatcher("timesig"));
            myView.durationMins.addTextChangedListener(new MyTextWatcher("durationMins"));
            myView.durationSecs.addTextChangedListener(new MyTextWatcher("durationSecs"));
            myView.delay.addTextChangedListener(new MyTextWatcher("delay"));
            myView.midi.addTextChangedListener(new MyTextWatcher("midi"));
            myView.abc.addTextChangedListener(new MyTextWatcher("abc"));
            myView.customChords.addTextChangedListener(new MyTextWatcher("customchords"));
            myView.linkType.addTextChangedListener(new MyTextWatcher("linktype"));
            myView.linkValue.addTextChangedListener(new MyTextWatcher("linkvalue"));

            myView.tapTempo.setOnClickListener(button -> mainActivityInterface.getMetronome().tapTempo());

            // The sticky notes override
            myView.overrideAbcSlider.addOnChangeListener((slider, value, fromUser) -> {
                // All options should clear existing override value
                mainActivityInterface.getProcessSong().removeAbcOverrides(mainActivityInterface.getTempSong(), true);
                // Get rid of any existing sticky_off values
                mainActivityInterface.getProcessSong().removeAbcOverrides(mainActivityInterface.getTempSong(),false);

                if (value==1) {
                    // Add the abc_on override
                    mainActivityInterface.getProcessSong().addAbcOverride(
                            mainActivityInterface.getTempSong(),true);

                } else if (value==2) {
                    // Add the abc_off override
                    mainActivityInterface.getProcessSong().addAbcOverride(
                            mainActivityInterface.getTempSong(), false);
                }
                myView.overrideAbcSlider.updateAlphas();
            });

            myView.preferredInstrument.addTextChangedListener(new MyTextWatcher("preferredinstrument"));
            // Scroll listener
            myView.featuresScrollView.setExtendedFabToAnimate(editSongFragmentInterface.getSaveButton());
        });
    }

    private String niceTextFromPref(String prefText) {
        switch (prefText) {
            default:
                return "";
            case "auto":
                return pad_auto_string;
            case "link":
                return link_audio_string;
            case "custom":
                return custom_string;
        }
    }

    private class MyTextWatcher implements TextWatcher {

        String what;

        MyTextWatcher(String what) {
            this.what = what;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (what) {
                case "key":
                    mainActivityInterface.getTempSong().setKey(mainActivityInterface.getTranspose().getOriginalFixedKey(editable.toString()));
                    setupCapo();
                    break;
                case "originalkey":
                    mainActivityInterface.getTempSong().setKeyOriginal(mainActivityInterface.getTranspose().getOriginalFixedKey(editable.toString()));
                    break;
                case "capo":
                    // Get rid of any new key text (e.g. convert '1 (D)' to '1')
                    String fixedcapo = editable.toString().replaceAll("\\D","").trim();
                    mainActivityInterface.getTempSong().setCapo(fixedcapo);
                    break;
                case "pad":
                    // We need to save the English short text in the preferences
                    mainActivityInterface.getTempSong().setPadfile(shortText(editable.toString()));
                    break;
                case "tempo":
                    mainActivityInterface.getTempSong().setTempo(editable.toString());
                    break;
                case "timesig":
                    mainActivityInterface.getTempSong().setTimesig(editable.toString());
                    break;
                case "durationMins":
                case "durationSecs":
                    updateTime();
                    break;
                case "delay":
                    mainActivityInterface.getTempSong().setAutoscrolldelay(editable.toString());
                    break;
                case "midi":
                    mainActivityInterface.getTempSong().setMidi(editable.toString());
                    break;
                case "abc":
                    mainActivityInterface.getTempSong().setAbc(editable.toString());
                    break;
                case "customchords":
                    mainActivityInterface.getTempSong().setCustomChords(editable.toString());
                    break;
                case "linktype":
                    if (editable.toString().equals(link_audio_string)) {
                        whichLink = "audio";
                    } else if (editable.toString().equals(link_youtube_string)) {
                        whichLink = "youtube";
                    } else if (editable.toString().equals(link_web_string)) {
                        whichLink = "web";
                    } else if (editable.toString().equals(link_file_string)) {
                        whichLink = "other";
                    }
                    setLink();
                    break;
                case "linkvalue":
                    editLink(editable.toString());
                    break;
                case "preferredinstrument":
                    updatePreferredInstrument(editable.toString());
                    break;
            }
        }
    }

    private void updateTime() {
        // Because secs could be over 60, get the total, reformat and update
        String minsText;
        String secsText;
        if (myView.durationMins.getText()==null ||
        myView.durationMins.getText().toString().isEmpty()) {
            minsText = "0";
        } else {
            minsText = myView.durationMins.getText().toString();
        }
        if (myView.durationSecs.getText()==null ||
                myView.durationSecs.getText().toString().isEmpty()) {
            secsText = "0";
        } else {
            secsText = myView.durationSecs.getText().toString();
        }
        int mins = Integer.parseInt(minsText);
        int secs = Integer.parseInt(secsText);
        int total = mainActivityInterface.getTimeTools().totalSecs(mins,secs);
        mainActivityInterface.getTempSong().setAutoscrolllength(String.valueOf(total));
    }
    private String shortText(String niceText) {
        if (niceText.equals(off_string)) {
            return "off";
        } else if (niceText.equals(link_string)) {
            return "link";
        } else if (niceText.equals(pad_auto_string)) {
            return "auto";
        } else {
            return "";
        }
    }


    // From the online search bottom sheet
    public void updateKey(String foundKey) {
        myView.key.setText(foundKey);
    }
    public void updateTempo(int tempo) {
        myView.tempo.setText(String.valueOf(tempo));
    }

    public void updateDuration(int mins, int secs) {
        myView.durationMins.setText(String.valueOf(mins));
        myView.durationSecs.setText(String.valueOf(secs));
    }

    private void updatePreferredInstrument(String option) {
        String preferredInstrument;
        if (option.equals(use_default_string)) {
            // Set to blank as it isn't required to be written into the xml
            preferredInstrument = "";
        } else {
            preferredInstrument = mainActivityInterface.getChordDisplayProcessing().getPrefFromInstrument(option);
        }
        mainActivityInterface.getTempSong().setPreferredInstrument(preferredInstrument);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myView = null;
    }
}
