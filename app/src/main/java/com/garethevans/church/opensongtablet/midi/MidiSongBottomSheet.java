package com.garethevans.church.opensongtablet.midi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.BottomSheetSongMidiBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class MidiSongBottomSheet extends BottomSheetDialogFragment {

    private MainActivityInterface mainActivityInterface;
    private BottomSheetSongMidiBinding myView;
    private ArrayList<MidiInfo> midiInfos;
    private LinearLayoutManager llm;
    private MidiMessagesAdapter midiMessagesAdapter;
    private final String TAG = "MidiSongBottomSheet";
    private String website_midi_song_string="", nothing_selected_string="";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            FrameLayout bottomSheet = ((BottomSheetDialog) dialog1).findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setDraggable(false);
            }
        });
        return dialog;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        myView = BottomSheetSongMidiBinding.inflate(inflater,container,false);

        prepareStrings();

        myView.dialogHeading.setClose(this);
        myView.dialogHeading.setWebHelp(mainActivityInterface,website_midi_song_string);

        // Get the midi device
        getMidiDeviceName();

        // Get the preference
        myView.autoSendMidi.setChecked(mainActivityInterface.getMidi().getMidiSendAuto());

        // Get the song midi commands
        setupAdapter();
        buildList();

        //getMidiCommands();

        // Set the listeners
        setupListeners();

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            website_midi_song_string = getString(R.string.website_midi_song);
            nothing_selected_string = getString(R.string.nothing_selected);
        }
    }
    private void getMidiDeviceName() {
        String name = mainActivityInterface.getMidi().getMidiDeviceName();
        if (name==null || name.isEmpty()) {
            myView.midiDevice.setHint(nothing_selected_string);
        } else {
            myView.midiDevice.setHint(mainActivityInterface.getMidi().getMidiDeviceName());
        }
    }

    private void setupAdapter() {
        if (getContext()!=null) {
            midiMessagesAdapter = new MidiMessagesAdapter(getContext());
            midiMessagesAdapter.setFromSongMessages(false);
             llm = new LinearLayoutManager(getContext());
            llm.setOrientation(RecyclerView.VERTICAL);
            myView.recyclerView.post(() -> {
                myView.recyclerView.setLayoutManager(llm);
                myView.recyclerView.setAdapter(midiMessagesAdapter);
            });
        }
    }

    public void updateSongMessages(String newMessages) {
        mainActivityInterface.getSong().setMidi(newMessages.trim());
        mainActivityInterface.getSaveSong().updateSong(mainActivityInterface.getSong(),false);
        buildList();
    }

    private void buildList() {
        midiInfos = new ArrayList<>();
        if (mainActivityInterface.getSong().getMidi()!=null) {
            String[] bits = mainActivityInterface.getSong().getMidi().trim().split("\n");
            Log.d(TAG, "bits.length: " + bits.length);
            for (String command : bits) {
                if (command != null && !command.isEmpty() && getActivity() != null) {
                    // Get a human readable version of the midi code
                    Log.d(TAG, "command: " + command);
                    String readable = mainActivityInterface.getMidi().getReadableStringFromHex(command);
                    MidiInfo midiInfo = new MidiInfo();
                    midiInfo.midiCommand = command;
                    midiInfo.readableCommand = readable;
                    midiInfos.add(midiInfo);
                }
            }
        }

        myView.recyclerView.post(() -> {
            Log.d(TAG, "Sending update size:"+midiInfos.size());

            midiMessagesAdapter.updateMidiInfos(midiInfos);
            myView.recyclerView.setAdapter(midiMessagesAdapter);
            myView.recyclerView.setVisibility(View.VISIBLE);
            myView.recyclerView.invalidate();
        });
    }

    private void setupListeners() {
       myView.songMidiEdit.setOnClickListener(v -> {
            MidiShortHandBottomSheet midiShortHandBottomSheet = new MidiShortHandBottomSheet(null,MidiSongBottomSheet.this,"MidiSongBottomSheet",null,mainActivityInterface.getSong().getMidi());
            midiShortHandBottomSheet.show(mainActivityInterface.getMyFragmentManager(),"MidiShortHandBottomSheet");
        });
       myView.autoSendMidi.setOnCheckedChangeListener((compoundButton, b) -> mainActivityInterface.getMidi().setMidiSendAuto(b));
    }
}
