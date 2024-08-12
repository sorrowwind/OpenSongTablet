package com.garethevans.church.opensongtablet.pads;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.customviews.MyMaterialEditText;
import com.garethevans.church.opensongtablet.databinding.SettingsPadsCustomBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

public class CustomPadsFragment extends Fragment {

    private MainActivityInterface mainActivityInterface;
    private SettingsPadsCustomBinding myView;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private final String TAG = "CustomPadsFragment";
    private MyMaterialEditText myMaterialEditText;
    private String prefName, prefValue, pad_string = "", custom_string = "", website_pad_string = "",
            pad_auto_string = "";
    private String webAddress;

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.updateToolbar(pad_string + " (" + custom_string + ")");
        mainActivityInterface.updateToolbarHelp(webAddress);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = SettingsPadsCustomBinding.inflate(inflater, container, false);

        prepareStrings();

        webAddress = website_pad_string;

        // Set up the file launcher listener
        setupLauncher();

        // Set up the user preferences and listeners
        setupPadOptions();

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext() != null) {
            pad_string = getString(R.string.pad);
            custom_string = getString(R.string.custom);
            website_pad_string = getString(R.string.website_pad);
            pad_auto_string = getString(R.string.pad_auto);
        }
    }

    @SuppressLint("WrongConstant")
    private void setupLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Handle the returned Uri
            String text;
            if (result != null && result.getData() != null && result.getData().getData() != null && getContext() != null) {
                Uri uri = result.getData().getData();
                Log.d(TAG, "uri=" + uri);

                // If this is a localised (i.e. inside OpenSong folder), we don't need to take the permissions
                // There is a limit of 128-512 permissions allowed (depending on Android version).
                prefValue = mainActivityInterface.getStorageAccess().fixUriToLocal(uri);
                text = prefValue;
                if (!prefValue.contains("../OpenSong/") && getActivity() != null) {
                    ContentResolver resolver = getActivity().getContentResolver();
                    resolver.takePersistableUriPermission(uri, mainActivityInterface.getStorageAccess().getTakePersistentReadUriFlags());
                }

            } else {
                prefValue = "";
                text = pad_auto_string;
            }
            mainActivityInterface.getPreferences().setMyPreferenceString(prefName, prefValue);
            myMaterialEditText.setText(text.replace("../OpenSong/Pads/",""));
        });
    }

    private void setupPadOptions() {
        setPref(myView.padAb, "customPadAb");
        setPref(myView.padA, "customPadA");
        setPref(myView.padBb, "customPadBb");
        setPref(myView.padB, "customPadB");
        setPref(myView.padC, "customPadC");
        setPref(myView.padDb, "customPadDb");
        setPref(myView.padD, "customPadD");
        setPref(myView.padEb, "customPadEb");
        setPref(myView.padE, "customPadE");
        setPref(myView.padF, "customPadF");
        setPref(myView.padGb, "customPadGb");
        setPref(myView.padG, "customPadG");
        setPref(myView.padAbm, "customPadAbm");
        setPref(myView.padAm, "customPadAm");
        setPref(myView.padBbm, "customPadBbm");
        setPref(myView.padBm, "customPadBm");
        setPref(myView.padCm, "customPadCm");
        setPref(myView.padDbm, "customPadDbm");
        setPref(myView.padDm, "customPadDm");
        setPref(myView.padEbm, "customPadEbm");
        setPref(myView.padEm, "customPadEm");
        setPref(myView.padFm, "customPadFm");
        setPref(myView.padGbm, "customPadGbm");
        setPref(myView.padGm, "customPadGm");
    }

    private void setPref(MyMaterialEditText myMaterialEditText, String prefName) {
        String pref = mainActivityInterface.getPreferences().getMyPreferenceString(prefName, "");
        if (pref == null || pref.isEmpty() || pref.equals("auto")) {
            pref = pad_auto_string;
        }
        myMaterialEditText.setText(pref.replace("../OpenSong/Pads/",""));
        myMaterialEditText.setFocusable(false);
        final String prefVal = pref;
        myMaterialEditText.setOnClickListener(view -> selectFile(myMaterialEditText, prefName, prefVal));
    }

    private void selectFile(MyMaterialEditText myMaterialEditText, String prefName, String prefValue) {
        // Set the value to auto.  If the user cancel, this becomes the new value
        this.myMaterialEditText = myMaterialEditText;
        this.prefName = prefName;
        this.prefValue = prefValue;
        this.myMaterialEditText.setText(pad_auto_string);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,
                    mainActivityInterface.getStorageAccess().getUriForItem("Pads","",""));
        }
        intent.addFlags(mainActivityInterface.getStorageAccess().getAddPersistentReadUriFlags());
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activityResultLauncher.unregister();
        myView = null;
    }
}
