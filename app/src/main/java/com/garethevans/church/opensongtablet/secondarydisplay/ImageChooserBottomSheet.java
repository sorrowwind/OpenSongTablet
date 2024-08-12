package com.garethevans.church.opensongtablet.secondarydisplay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.BottomSheetImageChooseBinding;
import com.garethevans.church.opensongtablet.interfaces.DisplayInterface;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.screensetup.ChooseColorBottomSheet;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ImageChooserBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetImageChooseBinding myView;
    private MainActivityInterface mainActivityInterface;
    private DisplayInterface displayInterface;
    private String pickThis, mode_presenter_string="";
    private final Fragment callingFragment;
    private final String fragName;
    private int colorSelected, colorUnselected;


    public ImageChooserBottomSheet() {
        // Default constructor required to avoid re-instantiation failures
        // Just close the bottom sheet
        callingFragment = null;
        fragName = "";
        dismiss();
    }

    public ImageChooserBottomSheet(Fragment callingFragment, String fragName) {
        // Get a reference to the fragment that requested the image chooser so we can send update
        this.callingFragment = callingFragment;
        this.fragName = fragName;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
        displayInterface = (DisplayInterface) context;
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
        myView = BottomSheetImageChooseBinding.inflate(inflater,container,false);

        prepareStrings();

        myView.dialogHeading.setClose(this);

        // Update our preferences in case they have changed
        mainActivityInterface.getPresenterSettings().getImagePreferences();

        // Update with our chosen values
        pickThis = mainActivityInterface.getPresenterSettings().getBackgroundToUse();
        updatePreview(myView.singleColor,null, true);
        updatePreview(myView.image1,mainActivityInterface.getPresenterSettings().getBackgroundImage1(),false);
        updatePreview(myView.image2,mainActivityInterface.getPresenterSettings().getBackgroundImage2(),false);
        updatePreview(myView.video1,mainActivityInterface.getPresenterSettings().getBackgroundVideo1(),false);
        updatePreview(myView.video2,mainActivityInterface.getPresenterSettings().getBackgroundVideo2(),false);

        // Highlight the currently chosen image
        setSelectedBackgroundHighlight();

        // Set up listeners
        setListeners();

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            mode_presenter_string = getString(R.string.mode_presenter);
            colorSelected = ContextCompat.getColor(getContext(),R.color.colorSecondary);
            colorUnselected = Color.TRANSPARENT;

        }
    }
    private void updatePreview(ImageView view, Uri uri, boolean isColor) {
        if (getContext()!=null) {
            RequestOptions options = new RequestOptions().override(128, 72).centerInside();
            if (isColor) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.simple_rectangle);
                if (drawable != null) {
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable.mutate();
                    gradientDrawable.setColor(mainActivityInterface.getPresenterSettings().getBackgroundColor());
                    Glide.with(getContext()).load(gradientDrawable).apply(options).into(view);
                }
            } else {
                if (uri == null) {
                    Glide.with(getContext()).load(VectorDrawableCompat.create(getResources(), R.drawable.image, getContext().getTheme())).apply(options).into(view);
                } else {
                    Glide.with(getContext()).load(uri).apply(options).into(view);
                }
            }
        }
    }

    private void setListeners() {
        myView.singleColor.setOnClickListener(v -> updateChosenImage("color"));
        myView.image1.setOnClickListener(v -> updateChosenImage("img1"));
        myView.image2.setOnClickListener(v -> updateChosenImage("img2"));
        myView.video1.setOnClickListener(v -> updateChosenImage("vid1"));
        myView.video2.setOnClickListener(v -> updateChosenImage("vid2"));

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,
                    mainActivityInterface.getStorageAccess().getUriForItem("Backgrounds","",""));
        }
        intent.addFlags(mainActivityInterface.getStorageAccess().getAddPersistentReadUriFlags());

        myView.singleColor.setOnLongClickListener(view -> {
            if (callingFragment!=null) {
                ChooseColorBottomSheet chooseColorBottomSheet = new ChooseColorBottomSheet(callingFragment, fragName, "backgroundColor");
                chooseColorBottomSheet.show(mainActivityInterface.getMyFragmentManager(), "ChooseColorBottomSheet");
            }
            dismiss();
            return true;
        });
        myView.image1.setOnLongClickListener(view -> {
            pickThis = "img1";
            intent.setType("image/*");
            activityResultLauncher.launch(intent,null);
            return true;
        });
        myView.image2.setOnLongClickListener(view -> {
            pickThis = "img2";
            intent.setType("image/*");
            activityResultLauncher.launch(intent,null);
            return true;
        });
        myView.video1.setOnLongClickListener(view -> {
            pickThis = "vid1";
            intent.setType("video/*");
            activityResultLauncher.launch(intent,null);
            return true;
        });
        myView.video2.setOnLongClickListener(view -> {
            pickThis = "vid2";
            intent.setType("video/*");
            activityResultLauncher.launch(intent,null);
            return true;
        });

        // If on a Chromebook, allow right click context menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            myView.image1.setOnContextClickListener(v -> {
                pickThis = "img1";
                intent.setType("image/*");
                activityResultLauncher.launch(intent,null);
                return true;
            });
            myView.image2.setOnContextClickListener(v -> {
                pickThis = "img2";
                intent.setType("image/*");
                activityResultLauncher.launch(intent,null);
                return true;
            });
            myView.video1.setOnContextClickListener(v -> {
                pickThis = "vid1";
                intent.setType("video/*");
                activityResultLauncher.launch(intent,null);
                return true;
            });
            myView.video2.setOnContextClickListener(v -> {
                pickThis = "vid2";
                intent.setType("video/*");
                activityResultLauncher.launch(intent,null);
                return true;
            });
        }
    }

    private void updateChosenImage(String whichToUse) {
        pickThis = whichToUse;
        mainActivityInterface.getPreferences().setMyPreferenceString("backgroundToUse",whichToUse);
        setSelectedBackgroundHighlight();

        // Also, if we are in presenter mode, update the 'Settings' tab previews(
        if (mainActivityInterface.getMode().equals(mode_presenter_string) &&
                fragName.equals("presenterFragmentSettings") && callingFragment!=null) {
            mainActivityInterface.getPresenterSettings().getImagePreferences();
            mainActivityInterface.updateFragment(fragName,callingFragment,null);
        }
        displayInterface.updateDisplay("changeBackground");
        // Updating the fragment behind also calls a displayInterface update in the background
    }

    private void setSelectedBackgroundHighlight() {
        if (getContext()!=null) {
            colorSelected = ContextCompat.getColor(getContext(), R.color.colorSecondary);
        }
        colorUnselected = Color.TRANSPARENT;
        myView.colorBackground.setBackgroundColor(colorUnselected);
        myView.image1Background.setBackgroundColor(colorUnselected);
        myView.image2Background.setBackgroundColor(colorUnselected);
        myView.video1Background.setBackgroundColor(colorUnselected);
        myView.video2Background.setBackgroundColor(colorUnselected);

        switch (pickThis) {
            case "img1":
                myView.image1Background.setBackgroundColor(colorSelected);
                break;
            case "img2":
                myView.image2Background.setBackgroundColor(colorSelected);
                break;
            case "vid1":
                myView.video1Background.setBackgroundColor(colorSelected);
                break;
            case "vid2":
                myView.video2Background.setBackgroundColor(colorSelected);
                break;
            case "color":
                myView.colorBackground.setBackgroundColor(colorSelected);
                break;
        }
    }


    // Listener for file chooser
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        if (intent!=null) {
                            Uri uri = intent.getData();
                            if (uri!=null) {
                                // Get permissions!
                                if (getContext()!=null && !mainActivityInterface.getStorageAccess().fixUriToLocal(uri).startsWith("../OpenSong/")) {
                                    // Only need to take permission if it isn't in the OpenSong folder
                                    getContext().getContentResolver().takePersistableUriPermission(
                                            uri, mainActivityInterface.getStorageAccess().getTakePersistentReadUriFlags());
                                }

                                switch (pickThis) {
                                    case "img1":
                                        mainActivityInterface.getPresenterSettings().setBackgroundImage1(uri);
                                        mainActivityInterface.getPreferences().setMyPreferenceString("backgroundImage1",uri.toString());
                                        updatePreview(myView.image1, uri, false);
                                        myView.image1.performClick();
                                        break;
                                    case "img2":
                                        mainActivityInterface.getPresenterSettings().setBackgroundImage2(uri);
                                        mainActivityInterface.getPreferences().setMyPreferenceString("backgroundImage2",uri.toString());
                                        updatePreview(myView.image2, uri, false);
                                        myView.image2.performClick();
                                        break;
                                    case "vid1":
                                        mainActivityInterface.getPresenterSettings().setBackgroundVideo1(uri);
                                        mainActivityInterface.getPreferences().setMyPreferenceString("backgroundVideo1",uri.toString());
                                        updatePreview(myView.video1, uri, false);
                                        myView.video1.performClick();
                                        break;
                                    case "vid2":
                                        mainActivityInterface.getPresenterSettings().setBackgroundVideo2(uri);
                                        mainActivityInterface.getPreferences().setMyPreferenceString("backgroundVideo2",uri.toString());
                                        updatePreview(myView.video2, uri, false);
                                        myView.video2.performClick();
                                        break;
                                }
                            }
                        }
                    }
                }
            });
}
