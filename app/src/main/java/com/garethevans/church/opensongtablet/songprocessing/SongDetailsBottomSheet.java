package com.garethevans.church.opensongtablet.songprocessing;

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

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.BottomSheetSongDetailsBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SongDetailsBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetSongDetailsBinding myView;
    private MainActivityInterface mainActivityInterface;
    private final String TAG = "SongDetailsBottomSheet";

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
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = BottomSheetSongDetailsBinding.inflate(inflater, container, false);

        myView.dialogHeading.setText(getString(R.string.information));
        myView.dialogHeading.setClose(this);

        // Set up views
        setupViews();

        // Set up listeners
        setupListeners();

        return myView.getRoot();
    }

    private void setupViews() {
        if (mainActivityInterface.getSong().getFiletype().equals("PDF") ||
                mainActivityInterface.getSong().getFiletype().equals("IMG")) {
            myView.textExtract.setVisibility(View.VISIBLE);
        } else {
            myView.textExtract.setVisibility(View.GONE);
        }
        myView.title.setHint(mainActivityInterface.getSong().getTitle());
        myView.author.setHint(mainActivityInterface.getSong().getAuthor());
        myView.key.setHint(mainActivityInterface.getSong().getKey());
        myView.copyright.setHint(mainActivityInterface.getSong().getCopyright());
        myView.ccli.setHint(mainActivityInterface.getSong().getCcli());
        myView.presentationOrder.setHint(mainActivityInterface.getSong().getPresentationorder());
        myView.hymnnum.setHint(mainActivityInterface.getSong().getHymnnum());
        myView.notes.setHint(mainActivityInterface.getSong().getNotes());
        myView.lyrics.setHint(mainActivityInterface.getSong().getLyrics());
    }

    private void setupListeners() {
        myView.textExtract.setOnClickListener(v -> Log.d(TAG,"Start OCR"));
        myView.edit.setOnClickListener(v -> {
            mainActivityInterface.navigateToFragment("opensongapp://settings/edit",0);
            dismiss();
        });
    }
}