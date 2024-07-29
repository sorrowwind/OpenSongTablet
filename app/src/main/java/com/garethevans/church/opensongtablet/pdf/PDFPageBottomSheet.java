package com.garethevans.church.opensongtablet.pdf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.garethevans.church.opensongtablet.databinding.BottomSheetPdfPageBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PDFPageBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetPdfPageBinding myView;
    private MainActivityInterface mainActivityInterface;

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
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = BottomSheetPdfPageBinding.inflate(inflater, container, false);
        myView.dialogHeader.setClose(this);

        // Set up views
        setupViews();

        // Set up listeners
        setupListeners();

        return myView.getRoot();
    }

    private void setupViews() {
        int isPDFVisible;
        int isNotPDFVisible;
        if (mainActivityInterface.getSong().getFiletype().equals("PDF")) {
            isPDFVisible = View.VISIBLE;
            isNotPDFVisible = View.GONE;
            myView.pageSlider.setValueFrom(1);
            if (mainActivityInterface.getSong().getPdfPageCount()>0 &&
                    mainActivityInterface.getSong().getPdfPageCurrent()<=0) {
                mainActivityInterface.getSong().setPdfPageCurrent(0);
            }
            myView.pageSlider.setValueTo(mainActivityInterface.getSong().getPdfPageCount());
            myView.pageSlider.setValue(mainActivityInterface.getSong().getPdfPageCurrent()+1);
            String text = String.valueOf((mainActivityInterface.getSong().getPdfPageCurrent()+1));
            myView.pageNumber.setText(text);
        } else {
            isPDFVisible = View.GONE;
            isNotPDFVisible = View.VISIBLE;
        }
        myView.pagesNotavailable.setVisibility(isNotPDFVisible);
        myView.pageSlider.setVisibility(isPDFVisible);
        myView.pageNumber.setVisibility(isPDFVisible);
        myView.nextPage.setVisibility(isPDFVisible);
        myView.previousPage.setVisibility(isPDFVisible);
        checkButtonEnable(false);
    }

    private void setupListeners() {
        myView.previousPage.setOnClickListener(v -> {
            if (mainActivityInterface.getSong().getPdfPageCurrent()>0) {
                mainActivityInterface.getDisplayPrevNext().setSwipeDirection("L2R");
                mainActivityInterface.getSong().setPdfPageCurrent(mainActivityInterface.getSong().getPdfPageCurrent()-1);
            }
            myView.pageSlider.setValue(Integer.parseInt(myView.pageNumber.getText().toString()));
            checkButtonEnable(true);
            myView.pageSlider.setValue(Integer.parseInt(myView.pageNumber.getText().toString()));
        });
        myView.nextPage.setOnClickListener(v -> {
            if (mainActivityInterface.getSong().getPdfPageCurrent()<mainActivityInterface.getSong().getPdfPageCount()-1) {
                mainActivityInterface.getDisplayPrevNext().setSwipeDirection("R2L");
                mainActivityInterface.getSong().setPdfPageCurrent(mainActivityInterface.getSong().getPdfPageCurrent()+1);
            }
            checkButtonEnable(true);
            myView.pageSlider.setValue(Integer.parseInt(myView.pageNumber.getText().toString()));
        });
        myView.pageSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                if (mainActivityInterface.getSong().getPdfPageCurrent() + 1> value) {
                    mainActivityInterface.getDisplayPrevNext().setSwipeDirection("L2R");
                } else {
                    mainActivityInterface.getDisplayPrevNext().setSwipeDirection("R2L");
                }
                mainActivityInterface.getSong().setPdfPageCurrent((int) value-1);

                checkButtonEnable(true);
            }
        });
    }

    private void checkButtonEnable(boolean hasChanged) {
        myView.previousPage.setEnabled(mainActivityInterface.getSong().getPdfPageCurrent() > 1);
        myView.nextPage.setEnabled(mainActivityInterface.getSong().getPdfPageCurrent() < mainActivityInterface.getSong().getPdfPageCount()-1);
        if (hasChanged) {
            // Update the page number text
            String text = String.valueOf((mainActivityInterface.getSong().getPdfPageCurrent()+1));
            myView.pageNumber.setText(text);
            mainActivityInterface.pdfScrollToPage(mainActivityInterface.getSong().getPdfPageCurrent());
        }
    }
}
