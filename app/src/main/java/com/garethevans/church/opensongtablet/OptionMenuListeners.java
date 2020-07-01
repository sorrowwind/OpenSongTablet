package com.garethevans.church.opensongtablet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.SalutDevice;

public class OptionMenuListeners extends AppCompatActivity {

    public interface MyInterface {
        void openFragment();
        void openMyDrawers(String which);
        void closeMyDrawers(String which);
        void refreshActionBar();
        void loadSong();
        void prepareSongMenu();
        void rebuildSearchIndex();
        void callIntent(String what, Intent intent);
        void prepareOptionMenu();
        void removeSongFromSet(int val);
        void splashScreen();
        void showActionBar();
        void hideActionBar();
        void useCamera();
        void doDownload(String file);
        void connectHDMI();
        void takeScreenShot();
        void prepareLearnAutoScroll();
        void stopAutoScroll();
        void killPad();
        void stopMetronome();
        void doExport();
        void updateExtraInfoColorsAndSizes(String s);
        void selectAFileUri(String s);
        void profileWork(String s);
    }

    private static MyInterface mListener;

    private static FragmentManager fm;
    private static float textSize = 14.0f;

    static LinearLayout prepareOptionMenu(Context c, FragmentManager fragman) {
        mListener = (MyInterface) c;
        fm = fragman;
        LinearLayout menu;
        switch (StaticVariables.whichOptionMenu) {
            case "MAIN":
            default:
                menu = createMainMenu(c);
                break;

            case "SET":
                menu = createSetMenu(c);
                break;

            case "SONG":
                menu = createSongMenu(c);
                break;

            case "SONGDISPLAY":
                menu = createSongDisplayMenu(c);
                break;

            case "SONGFEATURES":
                menu = createSongFeaturesMenu(c);
                break;

            case "PROFILE":
                menu = createProfileMenu(c);
                break;

            case "FIND":
                menu = createFindSongsMenu(c);
                break;

            case "CHORDS":
                menu = createChordsMenu(c);
                break;

            case "DISPLAY":
                menu = createDisplayMenu(c);
                break;

            case "CONNECT":
                menu = createConnectMenu(c);
                break;

            case "MIDI":
                menu = createMidiMenu(c);
                break;

            case "MODE":
                menu = createModeMenu(c);
                break;

            case "STORAGE":
                menu = createStorageMenu(c);
                break;

            case "GESTURES":
                menu = createGesturesMenu(c);
                break;

            case "AUTOSCROLL":
                menu = createAutoscrollMenu(c);
                break;

            case "PAD":
                menu = createPadMenu(c);
                break;

            case "METRONOME":
                menu = createMetronomeMenu(c);
                break;

            case "CCLI":
                menu = createCCLIMenu(c);
                break;

            case "OTHER":
                menu = createOtherMenu(c);
                break;

        }
        if (mListener != null) {
            mListener.refreshActionBar();
        }
        return menu;
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createMainMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option, null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createSetMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_set, null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createSongMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_song, null);
        } else {
            return null;
        }
    }

    @SuppressWarnings("InflateParams")
    private static LinearLayout createSongDisplayMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.option_songdisplay, null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createSongFeaturesMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.option_songfeatures, null);
        } else {
            return null;
        }
    }

    @SuppressWarnings("InflateParams")
    private static LinearLayout createProfileMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_profile,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createChordsMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_chords,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createDisplayMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_display,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createStorageMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_storage,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createMidiMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_midi,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createFindSongsMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_findsongs,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createConnectMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_connections,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createModeMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_modes,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createGesturesMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_gestures,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createAutoscrollMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_autoscroll,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createPadMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_pad,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createMetronomeMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_metronome,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createCCLIMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_ccli,null);
        } else {
            return null;
        }
    }

    @SuppressLint("InflateParams")
    private static LinearLayout createOtherMenu(Context c) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            return (LinearLayout) inflater.inflate(R.layout.popup_option_other,null);
        } else {
            return null;
        }
    }

    static void optionListeners(View v, Context c, Preferences preferences, StorageAccess storageAccess) {

        textSize = preferences.getMyPreferenceFloat(c,"songMenuAlphaIndexSize",14.0f);

        // Decide which listeners we need based on the menu
        switch (StaticVariables.whichOptionMenu) {
            case "MAIN":
            default:
                mainOptionListener(v,c);
                break;

            case "SET":
                setOptionListener(v, c, preferences, storageAccess);
                break;

            case "SONG":
                songOptionListener(v,c,preferences);
                break;

            case "SONGDISPLAY":
                songDisplayOptionListener(v,c);
                break;

            case "SONGFEATURES":
                songFeaturesOptionListener(v,c);
                break;

            case "PROFILE":
                profileOptionListener(v,c);
                break;

            case "CHORDS":
                chordOptionListener(v, c, storageAccess, preferences);
                break;

            case "DISPLAY":
                displayOptionListener(v,c);
                break;

            case "FIND":
                findSongsOptionListener(v,c);
                break;

            case "STORAGE":
                storageOptionListener(v, c, preferences);
                break;

            case "CONNECT":
                connectOptionListener(v,c);
                break;

            case "MIDI":
                midiOptionListener(v,c,preferences);
                break;

            case "MODE":
                modeOptionListener(v,c,preferences);
                break;

            case "GESTURES":
                gestureOptionListener(v,c,preferences);
                break;

            case "AUTOSCROLL":
                autoscrollOptionListener(v,c,preferences);
                break;

            case "PAD":
                padOptionListener(v,c,preferences);
                break;

            case "METRONOME":
                metronomeOptionListener(v,c, preferences);
                break;

            case "CCLI":
                ccliOptionListener(v,c,preferences);
                break;

            case "OTHER":
                otherOptionListener(v, c, preferences);
                break;
        }
    }

    private static void setMenuTextView(TextView t, String text) {
        t.setTextSize(textSize+2.0f);
        t.setText(text.toUpperCase(StaticVariables.locale));
    }

    private static void setTextButtons(Button b, String text) {
        b.setTextSize(textSize);
        b.setText(text.toUpperCase(StaticVariables.locale));
    }
    private static void setTextTextView(TextView t, String text) {
        t.setTextSize(textSize);
        t.setText(text.toUpperCase(StaticVariables.locale));
    }

    private static void mainOptionListener(View v, final Context c) {
        mListener = (MyInterface) c;
        // Identify the buttons
        TextView optionTitle = v.findViewById(R.id.optionTitle);
        Button menuSongDisplayButton = v.findViewById(R.id.menuSongDisplayButton);
        Button menuSongFeaturesButton = v.findViewById(R.id.menuSongFeaturesButton);
        Button menuSetButton = v.findViewById(R.id.menuSetButton);
        Button menuSongButton = v.findViewById(R.id.menuSongButton);
        Button menuProfileButton = v.findViewById(R.id.menuProfileButton);
        Button menuChordsButton = v.findViewById(R.id.menuChordsButton);
        Button menuDisplayButton = v.findViewById(R.id.menuDisplayButton);
        Button menuGesturesButton = v.findViewById(R.id.menuGesturesButton);
        Button menuConnectButton = v.findViewById(R.id.menuConnectButton);
        Button menuModeButton = v.findViewById(R.id.menuModeButton);
        Button menuMidiButton = v.findViewById(R.id.menuMidiButton);
        Button menuFindSongsButton = v.findViewById(R.id.menuFindSongsButton);
        Button menuStorageButton = v.findViewById(R.id.menuStorageButton);
        Button menuPadButton = v.findViewById(R.id.menuPadButton);
        Button menuAutoScrollButton = v.findViewById(R.id.menuAutoScrollButton);
        Button menuMetronomeButton = v.findViewById(R.id.menuMetronomeButton);
        Button menuCCLIButton = v.findViewById(R.id.menuCCLIButton);
        Button menuOtherButton = v.findViewById(R.id.menuOtherButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        setMenuTextView(optionTitle,c.getString(R.string.options));
        setTextButtons(menuSongDisplayButton,c.getString(R.string.song_display));
        setTextButtons(menuSongFeaturesButton,c.getString(R.string.song_features));
        setTextButtons(menuSetButton,c.getString(R.string.set));
        setTextButtons(menuSongButton,c.getString(R.string.song));
        setTextButtons(menuProfileButton,c.getString(R.string.profile));
        setTextButtons(menuChordsButton,c.getString(R.string.chords));
        setTextButtons(menuDisplayButton,c.getString(R.string.display));
        setTextButtons(menuGesturesButton,c.getString(R.string.gesturesandmenus));
        setTextButtons(menuConnectButton,c.getString(R.string.connections_connect));
        setTextButtons(menuMidiButton,c.getString(R.string.midi));
        setTextButtons(menuModeButton,c.getString(R.string.choose_app_mode));
        setTextButtons(menuFindSongsButton,c.getString(R.string.findnewsongs));
        setTextButtons(menuStorageButton,c.getString(R.string.storage));
        setTextButtons(menuPadButton,c.getString(R.string.pad));
        setTextButtons(menuAutoScrollButton,c.getString(R.string.autoscroll));
        setTextButtons(menuMetronomeButton,c.getString(R.string.metronome));
        setTextButtons(menuCCLIButton,c.getString(R.string.edit_song_ccli));
        setTextButtons(menuOtherButton,c.getString(R.string.other));

        // Only allow connection menu for JellyBean+
        menuConnectButton.setVisibility(View.VISIBLE);

        // Only allow MIDI menu for Marshmallow+ and if it is available
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            menuMidiButton.setVisibility(View.VISIBLE);
        } else {
            menuMidiButton.setVisibility(View.GONE);
        }

        // Set the listeners
        menuSongDisplayButton.setOnClickListener(new MenuNavigateListener("SONGDISPLAY"));
        menuSongFeaturesButton.setOnClickListener(new MenuNavigateListener("SONGFEATURES"));
        menuSetButton.setOnClickListener(new MenuNavigateListener("SET"));
        menuSongButton.setOnClickListener(new MenuNavigateListener("SONG"));
        menuProfileButton.setOnClickListener(new MenuNavigateListener("PROFILE"));
        menuChordsButton.setOnClickListener(new MenuNavigateListener("CHORDS"));
        menuDisplayButton.setOnClickListener(new MenuNavigateListener("DISPLAY"));
        menuGesturesButton.setOnClickListener(new MenuNavigateListener("GESTURES"));
        menuFindSongsButton.setOnClickListener(new MenuNavigateListener("FIND"));
        menuStorageButton.setOnClickListener(new MenuNavigateListener("STORAGE"));
        if (c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            menuMidiButton.setOnClickListener(new MenuNavigateListener("MIDI"));
        } else {
            menuMidiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StaticVariables.myToastMessage = "MIDI - " + c.getString(R.string.nothighenoughapi);
                    ShowToast.showToast(c);
                }
            });
        }
        menuConnectButton.setOnClickListener(new MenuNavigateListener("CONNECT"));
        menuModeButton.setOnClickListener(new MenuNavigateListener("MODE"));
        menuAutoScrollButton.setOnClickListener(new MenuNavigateListener("AUTOSCROLL"));
        menuPadButton.setOnClickListener(new MenuNavigateListener("PAD"));
        menuMetronomeButton.setOnClickListener(new MenuNavigateListener("METRONOME"));
        menuCCLIButton.setOnClickListener(new MenuNavigateListener("CCLI"));
        menuOtherButton.setOnClickListener(new MenuNavigateListener("OTHER"));
        closeOptionsFAB.setOnClickListener(new FABCloseListener());
    }

    private static void setOptionListener(View v, final Context c, final Preferences preferences, final StorageAccess storageAccess) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuUp = v.findViewById(R.id.setMenuTitle);
        Button setLoadButton = v.findViewById(R.id.setLoadButton);
        Button setSaveButton = v.findViewById(R.id.setSaveButton);
        Button setNewButton = v.findViewById(R.id.setNewButton);
        Button setDeleteButton = v.findViewById(R.id.setDeleteButton);
        Button setOrganiseButton = v.findViewById(R.id.setOrganiseButton);
        Button setImportButton = v.findViewById(R.id.setImportButton);
        Button setExportButton = v.findViewById(R.id.setExportButton);
        Button setCustomButton = v.findViewById(R.id.setCustomButton);
        Button setVariationButton = v.findViewById(R.id.setVariationButton);
        Button setEditButton = v.findViewById(R.id.setEditButton);
        SwitchCompat showSetTickBoxInSongMenu = v.findViewById(R.id.showSetTickBoxInSongMenu);
        //LinearLayout setLinearLayout = v.findViewById(R.id.setLinearLayout);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        setMenuTextView(menuUp,c.getString(R.string.set));
        setTextButtons(setLoadButton,c.getString(R.string.load));
        setTextButtons(setSaveButton,c.getString(R.string.save));
        setTextButtons(setNewButton,c.getString(R.string.set_new));
        setTextButtons(setDeleteButton,c.getString(R.string.delete));
        setTextButtons(setOrganiseButton,c.getString(R.string.managesets));
        setTextButtons(setImportButton,c.getString(R.string.importnewset));
        setTextButtons(setExportButton,c.getString(R.string.export));
        setTextButtons(setCustomButton,c.getString(R.string.add_custom_slide));
        setTextButtons(setVariationButton,c.getString(R.string.customise_set_item));
        setTextButtons(setEditButton,c.getString(R.string.edit));
        setTextButtons(showSetTickBoxInSongMenu,c.getString(R.string.setquickcheck));

        showSetTickBoxInSongMenu.setChecked(preferences.getMyPreferenceBoolean(c,"songMenuSetTicksShow",true));

        // Set the button listeners
        menuUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        setLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "loadset";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        setSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lastSetName = preferences.getMyPreferenceString(c,"setCurrentLastName","");
                Uri settosave = storageAccess.getUriForItem(c, preferences, "Sets", "", lastSetName);
                if (lastSetName==null || lastSetName.equals("")) {
                    StaticVariables.whattodo = "saveset";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                } else if (storageAccess.uriExists(c, settosave)) {
                    // Load the are you sure prompt
                    StaticVariables.whattodo = "saveset";
                    String setnamenice = lastSetName.replace("__"," / ");
                    String message = c.getResources().getString(R.string.save) + " \'" + setnamenice + "\"?";
                    StaticVariables.myToastMessage = message;
                    try {
                        DialogFragment newFragment = PopUpAreYouSureFragment.newInstance(message);
                        newFragment.show(fm,message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    StaticVariables.whattodo = "saveset";
                    if (mListener != null) {
                        mListener.openFragment();
                    }
                }
            }
        });

        setNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "clearset";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        setOrganiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "managesets";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        setDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "deleteset";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        setImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticVariables.whattodo = "doimportset";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.selectAFileUri(c.getString(R.string.importnewset));
                }
            }
        });

        setExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "exportset";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        setCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "customcreate";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        setVariationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "setitemvariation";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        setEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "editset";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        showSetTickBoxInSongMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"songMenuSetTicksShow",b);
                if (mListener!=null) {
                    mListener.prepareSongMenu();
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });

        // Add the set list to the menu
        /*if (StaticVariables.mSetList!=null) {
            for (int x = 0; x< StaticVariables.mSetList.length; x++) {
                TextView tv = new TextView(c);
                tv.setText(StaticVariables.mSetList[x]);
                tv.setTextColor(0xffffffff);
                tv.setTextSize(16.0f);
                tv.setPadding(16,16,16,16);
                LinearLayout.LayoutParams tvp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                tvp.setMargins(40,40,40,40);
                tv.setLayoutParams(tvp);
                final int val = x;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            StaticVariables.setView = true;
                            FullscreenActivity.pdfPageCurrent = 0;
                            FullscreenActivity.linkclicked = StaticVariables.mSetList[val];
                            StaticVariables.indexSongInSet = val;
                            SetActions setActions = new SetActions();
                            setActions.songIndexClickInSet();
                            setActions.getSongFileAndFolder(c);
                            preferences.setMyPreferenceString(c,"whichSongFolder",StaticVariables.whichSongFolder);
                            preferences.setMyPreferenceString(c, "songfilename",StaticVariables.songfilename);
                            if (mListener != null) {
                                mListener.closeMyDrawers("option");
                                mListener.loadSong();
                            }
                        } catch (Exception e) {
                            Log.d("OptionMenuListeners", "Something went wrong with the set item");
                            e.printStackTrace();
                        }
                    }
                });
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        try {
                            FullscreenActivity.linkclicked = StaticVariables.mSetList[val];
                            if (mListener != null) {
                                mListener.removeSongFromSet(val);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
                setLinearLayout.addView(tv);
            }
        }*/

    }

    private static void songOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuUp = v.findViewById(R.id.songMenuTitle);
        Button songEditButton = v.findViewById(R.id.songEditButton);
        Button songDuplicateButton = v. findViewById(R.id.songDuplicateButton);
        Button songPadButton = v.findViewById(R.id.songPadButton);
        Button songAutoScrollButton = v.findViewById(R.id.songAutoScrollButton);
        Button songMetronomeButton = v.findViewById(R.id.songMetronomeButton);
        Button songStickyButton = v.findViewById(R.id.songStickyButton);
        Button songDrawingButton = v.findViewById(R.id.songDrawingButton);
        Button songChordsButton = v.findViewById(R.id.songChordsButton);
        Button songScoreButton = v.findViewById(R.id.songScoreButton);
        Button songOnYouTubeButton = v.findViewById(R.id.songOnYouTubeButton);
        Button songOnWebButton = v.findViewById(R.id.songOnWebButton);
        Button songLinksButton = v.findViewById(R.id.songLinksButton);
        Button songRenameButton = v.findViewById(R.id.songRenameButton);
        Button songNewButton = v.findViewById(R.id.songNewButton);
        Button songDeleteButton = v.findViewById(R.id.songDeleteButton);
        Button songExportButton = v.findViewById(R.id.songExportButton);
        Button songImportButton = v.findViewById(R.id.songImportButton);
        final SwitchCompat songPresentationOrderButton = v.findViewById(R.id.songPresentationOrderButton);
        SwitchCompat songKeepMultiLineCompactButton = v.findViewById(R.id.songKeepMultiLineCompactButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        setMenuTextView(menuUp,c.getString(R.string.song));
        setTextButtons(songPadButton,c.getString(R.string.pad));
        setTextButtons(songAutoScrollButton,c.getString(R.string.autoscroll));
        setTextButtons(songMetronomeButton,c.getString(R.string.metronome));
        setTextButtons(songChordsButton,c.getString(R.string.chords));
        setTextButtons(songLinksButton,c.getString(R.string.link));
        setTextButtons(songDuplicateButton,c.getString(R.string.duplicate));
        setTextButtons(songEditButton,c.getString(R.string.edit));
        setTextButtons(songStickyButton,c.getString(R.string.stickynotes_edit));
        setTextButtons(songDrawingButton,c.getString(R.string.highlight));
        setTextButtons(songScoreButton,c.getString(R.string.music_score));
        setTextButtons(songOnYouTubeButton,c.getString(R.string.youtube));
        setTextButtons(songOnWebButton,c.getString(R.string.websearch));
        setTextButtons(songRenameButton,c.getString(R.string.rename));
        setTextButtons(songNewButton,c.getString(R.string.new_something));
        setTextButtons(songDeleteButton,c.getString(R.string.delete));
        setTextButtons(songImportButton,c.getString(R.string.importnewsong));
        setTextButtons(songExportButton,c.getString(R.string.export));
        setTextButtons(songPresentationOrderButton,c.getString(R.string.edit_song_presentation));
        setTextButtons(songKeepMultiLineCompactButton,c.getString(R.string.keepmultiline));

        // Hide the drawing option unless we are in performance mode
        if (StaticVariables.whichMode.equals("Performance")) {
            songDrawingButton.setVisibility(View.VISIBLE);
        } else {
            songDrawingButton.setVisibility(View.GONE);
        }

        // Set the switches up based on preferences
        songPresentationOrderButton.setChecked(preferences.getMyPreferenceBoolean(c,"usePresentationOrder",false));
        songKeepMultiLineCompactButton.setChecked(preferences.getMyPreferenceBoolean(c,"multiLineVerseKeepCompact",false));

        // Set the button listeners
        menuUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        songDuplicateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "duplicate";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        songPadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_pad";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });

        songAutoScrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_autoscroll";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });

        songMetronomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_metronome";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });

        songChordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_chords";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });

        songLinksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_links";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });

        songEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "editsong";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                } else {
                    StaticVariables.myToastMessage = c.getString(R.string.not_allowed);
                    ShowToast.showToast(c);
                }
            }
        });

        songStickyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "editnotes";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                } else {
                    StaticVariables.myToastMessage = c.getString(R.string.not_allowed);
                    ShowToast.showToast(c);
                }
            }
        });

        songDrawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "drawnotes";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    // Take a snapshot of the songwindow
                    mListener.takeScreenShot();
                    if (FullscreenActivity.bmScreen!=null) {
                        mListener.openFragment();
                    } else {
                        Log.d("OptionMenuListeners", "screenshot is null");
                    }
                }
            }
        });

        songScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "abcnotation_edit";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        songOnYouTubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "youtube";
                if (mListener != null) {
                    Intent youtube = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/results?search_query=" + StaticVariables.mTitle + "+" + StaticVariables.mAuthor));
                    mListener.callIntent("web", youtube);
                    mListener.closeMyDrawers("option");
                }
            }
        });

        songOnWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "websearch";
                if (mListener!=null) {
                    Intent web = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/search?q=" + StaticVariables.mTitle + "+" + StaticVariables.mAuthor));
                    mListener.callIntent("web", web);
                    mListener.closeMyDrawers("option");
                }
            }
        });

        songRenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "renamesong";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        songNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "createsong";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        songDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "deletesong";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        songImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticVariables.whattodo = "doimport";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.selectAFileUri(c.getString(R.string.importnewsong));
                }
            }
        });

        songExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "customise_exportsong";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                } else {
                    StaticVariables.myToastMessage = c.getString(R.string.not_allowed);
                    ShowToast.showToast(c);
                }
            }
        });

        songPresentationOrderButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"usePresentationOrder",b);
                if (FullscreenActivity.isSong) {
                    if (mListener != null) {
                        mListener.loadSong();
                    }
                }
            }
        });

        songKeepMultiLineCompactButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"multiLineVerseKeepCompact",b);
                if (FullscreenActivity.isSong) {
                    if (mListener != null) {
                        mListener.loadSong();
                    }
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });

    }

    private static void songDisplayOptionListener(View v, final Context c) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuUp = v.findViewById(R.id.songDisplayMenuTitle);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);
        Button songThemeButton = v.findViewById(R.id.songThemeButton);
        Button songFontButton = v.findViewById(R.id.songFontButton);
        Button songAutoScaleButton = v.findViewById(R.id.songAutoScaleButton);
        Button songLyricsButton = v.findViewById(R.id.songLyricsButton);
        Button songChordsButton = v.findViewById(R.id.songChordsButton);
        Button songPageButtonsButton = v.findViewById(R.id.songPageButtonsButton);
        Button songOther = v.findViewById(R.id.songOther);

        // Capitalise all the text by locale
        setMenuTextView(menuUp,c.getString(R.string.song_display));
        setTextButtons(songThemeButton,c.getString(R.string.choose_theme));
        setTextButtons(songFontButton, c.getString(R.string.choose_fonts));
        setTextButtons(songAutoScaleButton, c.getString(R.string.autoscale_toggle));
        setTextButtons(songLyricsButton, c.getString(R.string.lyrics_settings));
        setTextButtons(songChordsButton, c.getString(R.string.chord_settings));
        setTextButtons(songPageButtonsButton, c.getString(R.string.pagebuttons));
        setTextButtons(songOther, c.getString(R.string.other));

        // Set the button listeners
        menuUp.setOnClickListener(new MenuNavigateListener("MAIN"));
        closeOptionsFAB.setOnClickListener(new FABCloseListener());
        songThemeButton.setOnClickListener(new OpenFragmentButtonListener("changetheme","option"));
        songFontButton.setOnClickListener(new OpenFragmentButtonListener("changefonts","option"));
        songAutoScaleButton.setOnClickListener(new OpenFragmentButtonListener("autoscale","option"));
        songLyricsButton.setOnClickListener(new OpenFragmentButtonListener("lyricssettings","option"));
        songChordsButton.setOnClickListener(new OpenFragmentButtonListener("chordsettings","option"));
        songPageButtonsButton.setOnClickListener(new OpenFragmentButtonListener("pagebuttons","option"));
        songOther.setOnClickListener(new OpenFragmentButtonListener("displayother","option"));
    }

    private static void songFeaturesOptionListener(View v, final Context c) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuUp = v.findViewById(R.id.optionFeatureTitle);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);
        Button featureTransposeButton = v.findViewById(R.id.featureTransposeButton);
        Button featurePadButton = v.findViewById(R.id.featurePadButton);
        Button featureAutoScrollButton = v.findViewById(R.id.featureAutoScrollButton);
        Button featureMetronomeButton = v.findViewById(R.id.featureMetronomeButton);
        Button featureStickyButton = v.findViewById(R.id.featureStickyButton);
        Button featureDrawingButton = v.findViewById(R.id.featureDrawingButton);
        Button featureChordsButton = v.findViewById(R.id.featureChordsButton);
        Button featureScoreButton = v.findViewById(R.id.featureScoreButton);
        Button featureLinksButton = v.findViewById(R.id.featureLinksButton);
        Button featureOnYouTubeButton = v.findViewById(R.id.featureOnYouTubeButton);
        Button featureOnWebButton = v.findViewById(R.id.featureOnWebButton);

        // Capitalise all the text by locale
        setMenuTextView(menuUp,c.getString(R.string.song_display));
        setTextButtons(featureTransposeButton,c.getString(R.string.transpose));
        setTextButtons(featurePadButton,c.getString(R.string.pad));
        setTextButtons(featureAutoScrollButton,c.getString(R.string.autoscroll));
        setTextButtons(featureMetronomeButton,c.getString(R.string.metronome));
        setTextButtons(featureStickyButton,c.getString(R.string.stickynotes));
        setTextButtons(featureDrawingButton,c.getString(R.string.highlight));
        setTextButtons(featureChordsButton,c.getString(R.string.chords));
        setTextButtons(featureScoreButton,c.getString(R.string.music_score));
        setTextButtons(featureLinksButton,c.getString(R.string.link));
        setTextButtons(featureOnYouTubeButton,c.getString(R.string.youtube));
        setTextButtons(featureOnWebButton,c.getString(R.string.websearch));

        // Set the button listeners
        menuUp.setOnClickListener(new MenuNavigateListener("MAIN"));
        closeOptionsFAB.setOnClickListener(new FABCloseListener());
        featureTransposeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "transpose";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                } else {
                    StaticVariables.myToastMessage = c.getString(R.string.not_allowed);
                    ShowToast.showToast(c);
                }
            }
        });
        featureTransposeButton.setOnClickListener(new OpenFragmentButtonListener("transpose","option"));

        // TODO Add the rest of the button actions to OpenFragment.  Should create popups from current options menus
        featurePadButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureAutoScrollButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureMetronomeButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureStickyButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureDrawingButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureChordsButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureScoreButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureLinksButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureOnYouTubeButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));
        featureOnWebButton.setOnClickListener(new OpenFragmentButtonListener("featurePad","option"));

    }

    private static void chordOptionListener(View v, final Context c, final StorageAccess storageAccess, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.chordsMenuTitle);
        Button chordsButton = v.findViewById(R.id.chordsButton);
        Button chordsTransposeButton = v.findViewById(R.id.chordsTransposeButton);
        Button chordsSharpButton = v.findViewById(R.id.chordsSharpButton);
        Button chordsFlatButton = v.findViewById(R.id.chordsFlatButton);
        SwitchCompat chordsToggleSwitch = v.findViewById(R.id.chordsToggleSwitch);
        SwitchCompat chordsLyricsToggleSwitch = v.findViewById(R.id.chordsLyricsToggleSwitch);
        final SwitchCompat chordsCapoToggleSwitch = v.findViewById(R.id.chordsCapoToggleSwitch);
        final SwitchCompat chordsNativeAndCapoToggleSwitch = v.findViewById(R.id.chordsNativeAndCapoToggleSwitch);
        final SwitchCompat capoAsNumeralsToggleSwitch = v.findViewById(R.id.capoAsNumeralsToggleSwitch);
        SwitchCompat switchCapoTextSize = v.findViewById(R.id.switchCapoTextSize);
        Button chordsFormatButton = v.findViewById(R.id.chordsFormatButton);
        Button chordsConvertButton = v.findViewById(R.id.chordsConvertButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Get text for b button by adjusting the b button to use the unicode character instead
        String newflat = c.getString(R.string.chords_flat).replace(" b "," \u266d ");
        String newsharp = c.getString(R.string.chords_sharp).replace(" # "," \u266f ");

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.chords).toUpperCase(StaticVariables.locale));
        setTextButtons(chordsButton,c.getString(R.string.chords));
        setTextButtons(chordsTransposeButton,c.getString(R.string.transpose));
        chordsSharpButton.setTransformationMethod(null);
        chordsFlatButton.setTransformationMethod(null);
        setTextButtons(chordsSharpButton,newsharp);
        setTextButtons(chordsFlatButton,newflat);
        setTextButtons(chordsToggleSwitch,c.getString(R.string.showchords));
        setTextButtons(chordsLyricsToggleSwitch,c.getString(R.string.showlyrics));
        setTextButtons(chordsCapoToggleSwitch,c.getString(R.string.showcapo));
        setTextButtons(chordsNativeAndCapoToggleSwitch,c.getString(R.string.capo_toggle_bothcapo));
        setTextButtons(capoAsNumeralsToggleSwitch,c.getString(R.string.capo_style));
        setTextButtons(switchCapoTextSize,c.getString(R.string.size));
        setTextButtons(chordsFormatButton,c.getString(R.string.choose_chordformat));
        setTextButtons(chordsConvertButton,c.getString(R.string.chord_convert));

        // Set the switches up based on preferences
        if (preferences.getMyPreferenceBoolean(c,"displayChords",true)) {
            chordsToggleSwitch.setChecked(true);
        } else {
            chordsToggleSwitch.setChecked(false);
            chordsCapoToggleSwitch.setEnabled(false);
            chordsNativeAndCapoToggleSwitch.setEnabled(false);
        }

        switchCapoTextSize.setChecked(preferences.getMyPreferenceBoolean(c,"capoLargeFontInfoBar",true));

        chordsLyricsToggleSwitch.setChecked(preferences.getMyPreferenceBoolean(c,"displayLyrics",true));
        boolean capochordsbuttonenabled = preferences.getMyPreferenceBoolean(c,"displayChords",true);
        chordsCapoToggleSwitch.setChecked(preferences.getMyPreferenceBoolean(c,"displayCapoChords",true));
        chordsCapoToggleSwitch.setEnabled(capochordsbuttonenabled);
        if (!capochordsbuttonenabled) {
            chordsCapoToggleSwitch.setAlpha(0.4f);
        }

        boolean nativeandcapobuttonenabled = preferences.getMyPreferenceBoolean(c,"displayChords",true) &&
                capochordsbuttonenabled;
        chordsNativeAndCapoToggleSwitch.setChecked(preferences.getMyPreferenceBoolean(c,"displayCapoAndNativeChords",false));
        chordsNativeAndCapoToggleSwitch.setEnabled(nativeandcapobuttonenabled);
        if (!nativeandcapobuttonenabled) {
            chordsNativeAndCapoToggleSwitch.setAlpha(0.4f);
        }
        capoAsNumeralsToggleSwitch.setChecked(preferences.getMyPreferenceBoolean(c,"capoInfoAsNumerals",false));

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        chordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_chords";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });

        chordsTransposeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "transpose";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                } else {
                    StaticVariables.myToastMessage = c.getString(R.string.not_allowed);
                    ShowToast.showToast(c);
                }
            }
        });

        chordsSharpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transpose transpose = new Transpose();
                StaticVariables.whattodo = "transpose";
                if (FullscreenActivity.isPDF) {
                    // Can't do this action on a pdf!
                    StaticVariables.myToastMessage = c.getResources().getString(R.string.pdf_functionnotavailable);
                    ShowToast.showToast(c);
                } else if (!FullscreenActivity.isSong) {
                    // Editing a slide / note / scripture / image
                    StaticVariables.myToastMessage = c.getResources().getString(R.string.not_allowed);
                    ShowToast.showToast(c);
                } else {
                    StaticVariables.transposeDirection = "0";
                    transpose.checkChordFormat(c,preferences);
                    if (preferences.getMyPreferenceBoolean(c,"chordFormatUsePreferred",true)) {
                        StaticVariables.detectedChordFormat = preferences.getMyPreferenceInt(c,"chordFormat",1);
                    }
                    try {
                        transpose.doTranspose(c,storageAccess, preferences, true, false, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mListener!=null) {
                        mListener.loadSong();
                    }
                }
            }
        });

        chordsFlatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transpose transpose = new Transpose();
                StaticVariables.whattodo = "transpose";
                if (FullscreenActivity.isPDF) {
                    // Can't do this action on a pdf!
                    StaticVariables.myToastMessage = c.getResources().getString(R.string.pdf_functionnotavailable);
                    ShowToast.showToast(c);
                } else if (!FullscreenActivity.isSong) {
                    // Editing a slide / note / scripture / image
                    StaticVariables.myToastMessage = c.getResources().getString(R.string.not_allowed);
                    ShowToast.showToast(c);
                } else {
                    StaticVariables.transposeDirection = "0";
                    transpose.checkChordFormat(c,preferences);
                    if (preferences.getMyPreferenceBoolean(c,"chordFormatUsePreferred",true)) {
                        StaticVariables.detectedChordFormat = preferences.getMyPreferenceInt(c,"chordFormat",1);
                    }
                    try {
                        transpose.doTranspose(c, storageAccess, preferences, false, true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mListener!=null) {
                        mListener.loadSong();
                    }
                }
            }
        });

        chordsToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"displayChords",b);
                chordsCapoToggleSwitch.setEnabled(b);
                if (!b) {
                    chordsCapoToggleSwitch.setAlpha(0.4f);
                } else {
                    chordsCapoToggleSwitch.setAlpha(1.0f);
                }

                boolean nativeandcapobuttonenabled = preferences.getMyPreferenceBoolean(c,"displayCapoChords",true) && b;
                chordsNativeAndCapoToggleSwitch.setEnabled(nativeandcapobuttonenabled);
                if (!nativeandcapobuttonenabled) {
                    chordsNativeAndCapoToggleSwitch.setAlpha(0.4f);
                } else {
                    chordsNativeAndCapoToggleSwitch.setAlpha(1.0f);
                }

                if (mListener!=null) {
                    mListener.loadSong();
                }
            }
        });

        chordsLyricsToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"displayLyrics",b);
                if (mListener!=null) {
                    mListener.loadSong();
                }
            }
        });

        chordsCapoToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"displayCapoChords",b);
                boolean nativeandcapobuttonenabled = preferences.getMyPreferenceBoolean(c,"displayChords",true) && b;
                chordsNativeAndCapoToggleSwitch.setEnabled(nativeandcapobuttonenabled);
                if (!nativeandcapobuttonenabled) {
                    chordsNativeAndCapoToggleSwitch.setAlpha(0.4f);
                } else {
                    chordsNativeAndCapoToggleSwitch.setAlpha(1.0f);
                }
                if (mListener!=null) {
                    mListener.loadSong();
                }
            }
        });

        chordsNativeAndCapoToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"displayCapoAndNativeChords",b);
                if (mListener!=null) {
                    mListener.loadSong();
                }
            }
        });

        capoAsNumeralsToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"capoInfoAsNumerals",b);
                if (mListener!=null) {
                    mListener.loadSong();
                }
            }
        });
        chordsFormatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "choosechordformat";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        switchCapoTextSize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"capoLargeFontInfoBar",b);
                if (mListener!=null) {
                    mListener.updateExtraInfoColorsAndSizes("capo");
                }
            }
        });
        chordsConvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transpose transpose = new Transpose();
                if (FullscreenActivity.isPDF) {
                    // Can't do this action on a pdf!
                    StaticVariables.myToastMessage = c.getResources().getString(R.string.pdf_functionnotavailable);
                    ShowToast.showToast(c);
                } else if (!FullscreenActivity.isSong) {
                    // Editing a slide / note / scripture / image
                    StaticVariables.myToastMessage = c.getResources().getString(R.string.not_allowed);
                    ShowToast.showToast(c);
                } else {
                    transpose.convertChords(c,storageAccess,preferences);
                }
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.loadSong();
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });
    }

    private static void profileOptionListener(View v, final Context c) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionProfileTitle);
        Button profileLoadButton = v.findViewById(R.id.profileLoadButton);
        Button profileSaveButton = v.findViewById(R.id.profileSaveButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.profile).toUpperCase(StaticVariables.locale));
        setTextButtons(profileLoadButton,c.getString(R.string.load));
        setTextButtons(profileSaveButton,c.getString(R.string.save));

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });
        profileLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.profileWork("load");
                }
            }
        });
        profileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener!=null) {
                    mListener.profileWork("save");
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });
    }

    private static void displayOptionListener(View v, final Context c) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionDisplayTitle);
        Button displayThemeButton = v.findViewById(R.id.displayThemeButton);
        Button displayAutoScaleButton = v.findViewById(R.id.displayAutoScaleButton);
        Button displayFontButton = v.findViewById(R.id.displayFontButton);
        Button displayButtonsButton = v.findViewById(R.id.displayButtonsButton);
        Button displayPopUpsButton = v.findViewById(R.id.displayPopUpsButton);
        Button displayInfoButton = v.findViewById(R.id.displayInfoButton);
        Button displayActionBarButton = v.findViewById(R.id.displayActionBarButton);
        Button displayConnectedDisplayButton = v.findViewById(R.id.displayConnectedDisplayButton);
        Button displayHDMIButton = v.findViewById(R.id.displayHDMIButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.display).toUpperCase(StaticVariables.locale));
        setTextButtons(displayThemeButton,c.getString(R.string.choose_theme));
        setTextButtons(displayAutoScaleButton,c.getString(R.string.autoscale_toggle));
        setTextButtons(displayFontButton,c.getString(R.string.choose_fonts));
        setTextButtons(displayButtonsButton,c.getString(R.string.pagebuttons));
        setTextButtons(displayPopUpsButton,c.getString(R.string.display_popups));
        setTextButtons(displayInfoButton,c.getString(R.string.extra));
        setTextButtons(displayActionBarButton,c.getString(R.string.actionbar));
        setTextButtons(displayConnectedDisplayButton,c.getString(R.string.connected_display));
        setTextButtons(displayHDMIButton,c.getString(R.string.hdmi));

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        displayThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "changetheme";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        displayAutoScaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "autoscale";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        displayFontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "changefonts";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        displayButtonsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "pagebuttons";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        displayPopUpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "popupsettings";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        displayInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "extra";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        displayActionBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "actionbarinfo";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        displayConnectedDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null && (FullscreenActivity.isPresenting || FullscreenActivity.isHDMIConnected)) {
                    StaticVariables.whattodo = "connecteddisplay";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                } else {
                    StaticVariables.myToastMessage = view.getContext().getString(R.string.nodisplays);
                    ShowToast.showToast(view.getContext());
                }
            }
        });

        displayHDMIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.myToastMessage = view.getContext().getString(R.string.connections_searching);
                    ShowToast.showToast(view.getContext());
                    StaticVariables.whattodo = "hdmi";
                    mListener.connectHDMI();
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });

    }

    private static void findSongsOptionListener(View v, final Context c) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.findSongMenuTitle);
        Button ugSearchButton = v.findViewById(R.id.ugSearchButton);
        Button chordieSearchButton = v.findViewById(R.id.chordieSearchButton);
        Button songselectSearchButton = v.findViewById(R.id.songselectSearchButton);
        Button worshiptogetherSearchButton = v.findViewById(R.id.worshiptogetherSearchButton);
        Button worshipreadySearchButton = v.findViewById(R.id.worshipreadySearchButton);
        Button ukutabsSearchButton = v.findViewById(R.id.ukutabsSearchButton);
        Button holychordsSearchButton = v.findViewById(R.id.holychordsSearchButton);
        Button bandDownloadButton = v.findViewById(R.id.bandDownloadButton);
        Button churchDownloadButton = v.findViewById(R.id.churchDownloadButton);
        Button songImportButton = v.findViewById(R.id.songImportButton);
        Button cameraButton = v.findViewById(R.id.cameraButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        worshipreadySearchButton.setVisibility(View.GONE);

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.findnewsongs).toUpperCase(StaticVariables.locale));
        setTextButtons(ugSearchButton,c.getString(R.string.ultimateguitarsearch));
        setTextButtons(chordieSearchButton,c.getString(R.string.chordiesearch));
        setTextButtons(songselectSearchButton,c.getString(R.string.songselect)+ " " + c.getString(R.string.subscription));
        setTextButtons(worshiptogetherSearchButton,c.getString(R.string.worshiptogether)+ " " + c.getString(R.string.subscription));
        setTextButtons(worshipreadySearchButton,c.getString(R.string.worshipready)+ " " + c.getString(R.string.subscription));
        setTextButtons(ukutabsSearchButton,c.getString(R.string.ukutabs));
        setTextButtons(holychordsSearchButton,c.getString(R.string.holychords));
        setTextButtons(bandDownloadButton,c.getString(R.string.my_band));
        setTextButtons(churchDownloadButton,c.getString(R.string.my_church));
        setTextButtons(songImportButton,c.getString(R.string.importnewsong));
        setTextButtons(cameraButton,c.getString(R.string.camera));

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        ugSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "ultimate-guitar";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        chordieSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "chordie";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        worshiptogetherSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "worshiptogether";
                if (mListener != null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        worshipreadySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "worshipready";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        songselectSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "songselect";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        ukutabsSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "ukutabs";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        holychordsSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticVariables.whattodo = "holychords";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        bandDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "download_band";
                StaticVariables.myToastMessage = c.getString(R.string.wait);
                ShowToast.showToast(c);
                if (mListener!=null) {
                    mListener.doDownload("https://drive.google.com/uc?export=download&id=0B-GbNhnY_O_leDR5bFFjRVVxVjA");
                    mListener.closeMyDrawers("option");
                }
            }
        });
        churchDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "download_church";
                StaticVariables.myToastMessage = c.getString(R.string.wait);
                ShowToast.showToast(c);
                if (mListener!=null) {
                    mListener.doDownload("https://drive.google.com/uc?export=download&id=0B-GbNhnY_O_lbVY3VVVOMkc5OGM");
                    mListener.closeMyDrawers("option");
                }
            }
        });
        songImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticVariables.whattodo = "doimport";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.selectAFileUri(c.getString(R.string.importnewsong));
                }
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener!=null) {
                    mListener.useCamera();
                }
           }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });

    }

    private static void storageOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionStorageTitle);
        Button storageNewFolderButton = v.findViewById(R.id.storageNewFolderButton);
        Button storageEditButton = v.findViewById(R.id.storageEditButton);
        Button storageManageButton = v.findViewById(R.id.storageManageButton);
        Button exportSongListButton = v.findViewById(R.id.exportSongListButton);
        Button storageImportOSBButton = v.findViewById(R.id.storageImportOSBButton);
        Button storageExportOSBButton = v.findViewById(R.id.storageExportOSBButton);
        Button storageImportOnSongButton = v.findViewById(R.id.storageImportOnSongButton);
        Button storageSongMenuButton = v.findViewById(R.id.storageSongMenuButton);
        Button storageDatabaseButton = v.findViewById(R.id.storageDatabaseButton);
        Button storageLogButton = v.findViewById(R.id.storageLogButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.storage).toUpperCase(StaticVariables.locale));
        setTextButtons(storageNewFolderButton,c.getString(R.string.folder_new));
        setTextButtons(storageEditButton,c.getString(R.string.folder_rename));
        setTextButtons(storageManageButton,c.getString(R.string.storage_choose));
        setTextButtons(exportSongListButton,c.getString(R.string.exportsongdirectory));
        setTextButtons(storageImportOSBButton,c.getString(R.string.backup_import));
        setTextButtons(storageExportOSBButton,c.getString(R.string.backup_export));
        setTextButtons(storageImportOnSongButton,c.getString(R.string.import_onsong_choose));
        setTextButtons(storageSongMenuButton,c.getString(R.string.refreshsongs));
        setTextButtons(storageDatabaseButton,c.getString(R.string.search_rebuild));
        setTextButtons(storageLogButton,c.getString(R.string.search_log));

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        storageNewFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "newfolder";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        storageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "editfoldername";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        storageManageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    preferences.setMyPreferenceInt(c, "lastUsedVersion", 0);
                    mListener.closeMyDrawers("option");
                    mListener.splashScreen();
                }
            }
        });

        exportSongListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "exportsonglist";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        storageImportOSBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "processimportosb";
                    mListener.selectAFileUri(c.getString(R.string.backup_import));
                    mListener.closeMyDrawers("option");
                }
            }
        });

        storageExportOSBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "exportosb";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        storageImportOnSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "importos";
                    mListener.closeMyDrawers("option");
                    mListener.selectAFileUri(c.getString(R.string.import_onsong_choose));
                }
            }
        });

        storageSongMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.prepareSongMenu();
                    mListener.closeMyDrawers("option");
                    mListener.openMyDrawers("song");
                    mListener.closeMyDrawers("song_delayed");
                }
            }
        });

        storageDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.rebuildSearchIndex();
                }
            }
        });

        storageLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "errorlog";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });
    }

    private static void connectOptionListener(View v, final Context c) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuUp = v.findViewById(R.id.connectionsMenuTitle);

        // We keep a static reference to these in the FullscreenActivity
        FullscreenActivity.hostButton = v.findViewById(R.id.connectionsHostButton);
        FullscreenActivity.clientButton = v.findViewById(R.id.connectionsGuestButton);
        SwitchCompat connectionsReceiveHostFile = v.findViewById(R.id.connectionsReceiveHostFile);
        FullscreenActivity.connectionsLog = v.findViewById(R.id.options_connections_log);

        if (FullscreenActivity.salutLog==null || FullscreenActivity.salutLog.equals("")) {
            FullscreenActivity.salutLog = c.getResources().getString(R.string.connections_log) + "\n\n";
        }
        setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);

        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Get host/slave text
        if (FullscreenActivity.hostButtonText==null || FullscreenActivity.hostButtonText.equals("")) {
            FullscreenActivity.hostButtonText = c.getResources().getString(R.string.connections_service_start).toUpperCase(StaticVariables.locale);
        }
        if (FullscreenActivity.clientButtonText==null || FullscreenActivity.clientButtonText.equals("")) {
            FullscreenActivity.clientButtonText = c.getResources().getString(R.string.connections_discover).toUpperCase(StaticVariables.locale);
        }

        // Capitalise all the text by locale
        menuUp.setText(c.getString(R.string.connections_connect).toUpperCase(StaticVariables.locale));
        setTextButtons(connectionsReceiveHostFile,c.getString(R.string.connections_receive_host));
        setTextButtons(FullscreenActivity.hostButton,FullscreenActivity.hostButtonText);
        setTextButtons(FullscreenActivity.clientButton,FullscreenActivity.clientButtonText);
        connectionsReceiveHostFile.setChecked(FullscreenActivity.receiveHostFiles);

        // Set the button listeners
        menuUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });
        connectionsReceiveHostFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FullscreenActivity.receiveHostFiles = b;
            }
        });
        FullscreenActivity.connectionsLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullscreenActivity.salutLog = c.getResources().getString(R.string.connections_log) + "\n\n";
                setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
            }
        });
        FullscreenActivity.hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupNetwork(c);
            }
        });
        FullscreenActivity.clientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverServices(c);
            }
        });
        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });

    }

    private static void midiOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuUp = v.findViewById(R.id.midiMenuTitle);

        Button midiBluetooth = v.findViewById(R.id.midiBluetooth);
        Button midiUSB = v.findViewById(R.id.midiUSB);
        Button midiCommands = v.findViewById(R.id.midiCommands);
        Button midiSend = v.findViewById(R.id.midiSend);
        SwitchCompat midiAuto = v.findViewById(R.id.midiAuto);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        menuUp.setText(c.getString(R.string.midi).toUpperCase(StaticVariables.locale));
        setTextButtons(midiBluetooth,c.getString(R.string.midi_bluetooth));
        setTextButtons(midiUSB,c.getString(R.string.midi_usb));
        setTextButtons(midiCommands,c.getString(R.string.midi_commands));
        setTextButtons(midiSend,c.getString(R.string.midi_send));
        setTextButtons(midiAuto,c.getString(R.string.midi_auto));

        // Set the default
        midiAuto.setChecked(preferences.getMyPreferenceBoolean(c,"midiSendAuto",false));

        // Set the button listeners
        menuUp.setOnClickListener(new MenuNavigateListener("MAIN"));
        midiBluetooth.setOnClickListener(new OpenFragmentButtonListener("bluetoothmidi","option"));
        midiUSB.setOnClickListener(new OpenFragmentButtonListener("usbmidi","option"));
        midiCommands.setOnClickListener(new OpenFragmentButtonListener("midicommands","option"));
        midiSend.setOnClickListener(new OpenFragmentButtonListener("showmidicommands","option"));
        midiAuto.setOnCheckedChangeListener(new SwitchBooleanSaveListener(c,preferences,"midiSendAuto"));
        closeOptionsFAB.setOnClickListener(new FABCloseListener());
    }

    private static void setupNetwork(final Context c) {
        Log.d("OptionMenuListener","FullscreenActivity.network="+FullscreenActivity.network);
        if (FullscreenActivity.network!=null) {
            Log.d("OptionMenuListener", "FullscreenActivity.network.isRunningAsHost=" + FullscreenActivity.network.isRunningAsHost);
        }

        if (FullscreenActivity.network!=null && !FullscreenActivity.network.isRunningAsHost) {
            try {
                FullscreenActivity.network.startNetworkService(new SalutDeviceCallback() {
                    @Override
                    public void call(SalutDevice salutDevice) {
                        StaticVariables.myToastMessage = salutDevice.readableName + " - " +
                                c.getResources().getString(R.string.connections_success);
                        FullscreenActivity.salutLog += "\n" + StaticVariables.myToastMessage;
                        setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
                        ShowToast.showToast(c);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            FullscreenActivity.hostButtonText = c.getResources().getString(R.string.connections_service_stop).toUpperCase(StaticVariables.locale);
            setTextButtons(FullscreenActivity.hostButton,FullscreenActivity.hostButtonText);
            FullscreenActivity.clientButton.setAlpha(0.5f);
            FullscreenActivity.clientButton.setClickable(false);
            StaticVariables.myToastMessage = c.getResources().getString(R.string.connections_broadcast) +
                    " " + FullscreenActivity.mBluetoothName;
            FullscreenActivity.salutLog += "\n" + StaticVariables.myToastMessage;
            setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
            ShowToast.showToast(c);
        } else {
            try {
                if (FullscreenActivity.network!=null) {
                    FullscreenActivity.network.stopNetworkService(false);
                }
                FullscreenActivity.hostButtonText = c.getResources().getString(R.string.connections_service_start).toUpperCase(StaticVariables.locale);
                setTextButtons(FullscreenActivity.hostButton,FullscreenActivity.hostButtonText);
                FullscreenActivity.salutLog += "\n" + c.getResources().getString(R.string.connections_service_stop);
                setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
                FullscreenActivity.clientButton.setAlpha(1f);
                FullscreenActivity.clientButton.setClickable(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void discoverServices(final Context c) {
        if(FullscreenActivity.network!=null && !FullscreenActivity.network.isRunningAsHost && !FullscreenActivity.network.isDiscovering) {
            try {
                FullscreenActivity.network.discoverNetworkServices(new SalutCallback() {
                    @Override
                    public void call() {
                        SalutDevice hostname = FullscreenActivity.network.foundDevices.get(0);
                        StaticVariables.myToastMessage = c.getResources().getString(R.string.connections_host) +
                                " " + hostname.readableName;
                        FullscreenActivity.salutLog += "\n" + StaticVariables.myToastMessage;
                        setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
                        ShowToast.showToast(c);
                        registerWithHost(c,hostname);
                    }
                }, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FullscreenActivity.salutLog += "\n" + c.getResources().getString(R.string.connections_searching);
            setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
            FullscreenActivity.clientButtonText = c.getResources().getString(R.string.connections_discover_stop).toUpperCase(StaticVariables.locale);
            setTextButtons(FullscreenActivity.clientButton,FullscreenActivity.clientButtonText);
            FullscreenActivity.hostButton.setAlpha(0.5f);
            FullscreenActivity.hostButton.setClickable(false);
        } else {
            FullscreenActivity.salutLog += "\n" +c.getResources().getString(R.string.connections_discover_stop);
            setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
            try {
                FullscreenActivity.network.stopServiceDiscovery(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FullscreenActivity.clientButtonText = c.getResources().getString(R.string.connections_discover).toUpperCase(StaticVariables.locale);
            setTextButtons(FullscreenActivity.clientButton,FullscreenActivity.clientButtonText);
            FullscreenActivity.hostButton.setAlpha(1f);
            FullscreenActivity.hostButton.setClickable(true);
        }

    }

    private static void registerWithHost(final Context c, final SalutDevice possibleHost) {
        try {
            Log.d("OptionMenu","possibleHost="+possibleHost);
            FullscreenActivity.network.registerWithHost(possibleHost, new SalutCallback() {
                @Override
                public void call() {
                    StaticVariables.myToastMessage = c.getResources().getString(R.string.connections_connected) +
                            " " + possibleHost.readableName;
                    FullscreenActivity.salutLog += "\n" + StaticVariables.myToastMessage;
                    setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
                    ShowToast.showToast(c);
                    FullscreenActivity.clientButtonText = (c.getResources().getString(R.string.connections_disconnect) +
                            " " + possibleHost.readableName).toUpperCase(StaticVariables.locale);
                    setTextButtons(FullscreenActivity.clientButton,FullscreenActivity.clientButtonText);

                }
            }, new SalutCallback() {
                @Override
                public void call() {
                    StaticVariables.myToastMessage = possibleHost.readableName + ": " +
                            c.getResources().getString(R.string.connections_failure);
                    FullscreenActivity.salutLog += "\n" + StaticVariables.myToastMessage;
                    setTextTextView(FullscreenActivity.connectionsLog,FullscreenActivity.salutLog);
                    ShowToast.showToast(c);
                    try {
                        FullscreenActivity.network.stopServiceDiscovery(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FullscreenActivity.clientButtonText = c.getResources().getString(R.string.connections_discover).toUpperCase(StaticVariables.locale);
                    setTextButtons(FullscreenActivity.clientButton,FullscreenActivity.clientButtonText);
                    FullscreenActivity.hostButton.setAlpha(1f);
                    FullscreenActivity.hostButton.setClickable(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void modeOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuUp = v.findViewById(R.id.modeMenuTitle);
        Button modePerformanceButton = v.findViewById(R.id.modePerformanceButton);
        Button modeStageButton = v.findViewById(R.id.modeStageButton);
        Button modePresentationButton = v.findViewById(R.id.modePresentationButton);
        CheckBox performanceCheck = v.findViewById(R.id.performanceCheck);
        CheckBox stageCheck = v.findViewById(R.id.stageCheck);
        CheckBox presentationCheck = v.findViewById(R.id.presentationCheck);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        menuUp.setText(c.getString(R.string.choose_app_mode).toUpperCase(StaticVariables.locale));
        setTextButtons(modePerformanceButton,c.getString(R.string.performancemode));
        setTextButtons(modeStageButton,c.getString(R.string.stagemode));
        setTextButtons(modePresentationButton,c.getString(R.string.presentermode));
        performanceCheck.setVisibility(View.GONE);
        stageCheck.setVisibility(View.GONE);
        presentationCheck.setVisibility(View.GONE);

        // Disable the current mode
        switch (StaticVariables.whichMode) {
            case "Performance":
                setAppModeButtons(modePerformanceButton,modeStageButton,modePresentationButton,performanceCheck);
                break;

            case "Stage":
                setAppModeButtons(modeStageButton, modePerformanceButton,modePresentationButton,stageCheck);
                break;

            case "Presentation":
                setAppModeButtons(modePresentationButton, modePerformanceButton,modeStageButton,presentationCheck);
                break;
        }
        // Set the button listeners
        menuUp.setOnClickListener(new MenuNavigateListener("MAIN"));
        modePerformanceButton.setOnClickListener(new ModeChangeListener(c,preferences,"Performance"));
        modeStageButton.setOnClickListener(new ModeChangeListener(c,preferences,"Stage"));
        modePresentationButton.setOnClickListener(new ModeChangeListener(c,preferences,"Presentation"));
        closeOptionsFAB.setOnClickListener(new FABCloseListener());
    }

    private static void setAppModeButtons(Button activeButton, Button available1, Button available2, CheckBox checkVisible) {
        activeButton.setEnabled(false);
        available1.setEnabled(true);
        available2.setEnabled(true);
        checkVisible.setVisibility(View.VISIBLE);
    }

    private static void gestureOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionGestureTitle);
        Button gesturesPedalButton = v.findViewById(R.id.gesturesPedalButton);
        Button gesturesPageButton = v.findViewById(R.id.gesturesPageButton);
        Button gesturesCustomButton = v.findViewById(R.id.gesturesCustomButton);
        Button gesturesMenuOptions = v.findViewById(R.id.gesturesMenuOptions);
        Button gesturesScrollButton = v.findViewById(R.id.gesturesScrollButton);
        SwitchCompat displayMenuToggleSwitch = v.findViewById(R.id.displayMenuToggleSwitch);
        Button gesturesSongSwipeButton = v.findViewById(R.id.gesturesSongSwipeButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.gesturesandmenus).toUpperCase(StaticVariables.locale));
        setTextButtons(gesturesPedalButton,c.getString(R.string.footpedal));
        setTextButtons(gesturesPageButton,c.getString(R.string.quicklaunch_title));
        setTextButtons(gesturesCustomButton,c.getString(R.string.custom_gestures));
        setTextButtons(gesturesMenuOptions,c.getString(R.string.menu_settings));
        setTextButtons(gesturesScrollButton,c.getString(R.string.scrollbuttons));
        setTextButtons(displayMenuToggleSwitch,c.getString(R.string.hide_actionbar));
        setTextButtons(gesturesSongSwipeButton,c.getString(R.string.swipe));

        // Set the switches up based on preferences
        displayMenuToggleSwitch.setChecked(preferences.getMyPreferenceBoolean(c,"hideActionBar",false));

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        gesturesPedalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "footpedal";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        gesturesPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "quicklaunch";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        gesturesCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "gestures";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        gesturesMenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "menuoptions";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        gesturesScrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "scrollsettings";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        gesturesSongSwipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "swipesettings";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });
        displayMenuToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"hideActionBar",b);
                if (mListener!=null) {
                    if (b) {
                        mListener.hideActionBar();
                    } else {
                        mListener.showActionBar();
                    }
                    mListener.loadSong();
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });

    }

    private static void autoscrollOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionAutoScrollTitle);
        Button autoScrollButton = v.findViewById(R.id.autoScrollButton);
        Button autoScrollTimeDefaultsButton = v.findViewById(R.id.autoScrollTimeDefaultsButton);
        Button autoScrollLearnButton = v.findViewById(R.id.autoScrollLearnButton);
        SwitchCompat switchTimerSize = v.findViewById(R.id.switchTimerSize);
        SwitchCompat autoScrollStartButton = v.findViewById(R.id.autoScrollStartButton);
        SwitchCompat autoscrollActivatedSwitch = v.findViewById(R.id.autoscrollActivatedSwitch);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.autoscroll).toUpperCase(StaticVariables.locale));
        setTextButtons(autoScrollButton,c.getString(R.string.autoscroll));
        setTextButtons(autoScrollTimeDefaultsButton,c.getString(R.string.default_autoscroll));
        setTextButtons(autoScrollStartButton,c.getString(R.string.autostart_autoscroll));
        setTextButtons(autoscrollActivatedSwitch,c.getString(R.string.activated));
        setTextButtons(switchTimerSize,c.getString(R.string.timer_size));
        setTextButtons(autoScrollLearnButton,c.getString(R.string.timer_learn));

        // Set the switches up based on preferences
        autoScrollStartButton.setChecked(preferences.getMyPreferenceBoolean(c,"autoscrollAutoStart",false));
        autoscrollActivatedSwitch.setChecked(StaticVariables.clickedOnAutoScrollStart);

        switchTimerSize.setChecked(preferences.getMyPreferenceBoolean(c,"autoscrollLargeFontInfoBar",true));

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        autoScrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_autoscroll";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });
        autoscrollActivatedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                StaticVariables.clickedOnAutoScrollStart = b;
                if (!b && mListener!=null) {
                    mListener.stopAutoScroll();
                }
            }
        });
        autoScrollTimeDefaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "autoscrolldefaults";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        autoScrollStartButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"autoscrollAutoStart",b);
            }
        });

        autoScrollLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.prepareLearnAutoScroll();
                }
            }
        });
        switchTimerSize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"autoscrollLargeFontInfoBar",b);
                if (mListener!=null) {
                    mListener.updateExtraInfoColorsAndSizes("autoscroll");
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });
    }

    private static void padOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionPadTitle);
        Button padButton = v.findViewById(R.id.padButton);
        Button padCrossFadeButton = v.findViewById(R.id.padCrossFadeButton);
        Button padCustomButton = v.findViewById(R.id.padCustomButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);
        SwitchCompat switchTimerSize = v.findViewById(R.id.switchTimerSize);
        SwitchCompat padStartButton = v.findViewById(R.id.padStartButton);
        SwitchCompat padActivatedSwitch = v.findViewById(R.id.padActivatedSwitch);


        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.pad).toUpperCase(StaticVariables.locale));
        setTextButtons(padButton,c.getString(R.string.pad));
        setTextButtons(padStartButton,c.getString(R.string.autostartpad));
        setTextButtons(padCustomButton,c.getString(R.string.custom));
        setTextButtons(padCrossFadeButton,c.getString(R.string.crossfade_time));
        setTextButtons(padActivatedSwitch,c.getString(R.string.activated));
        setTextButtons(switchTimerSize,c.getString(R.string.timer_size));

        // Set the switch
        switchTimerSize.setChecked(preferences.getMyPreferenceBoolean(c,"padLargeFontInfoBar",true));

        padStartButton.setChecked(preferences.getMyPreferenceBoolean(c,"padAutoStart",false));
        padActivatedSwitch.setChecked(StaticVariables.clickedOnPadStart);
        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        padButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_pad";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });

        padStartButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"padAutoStart",b);
            }
        });
        padCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    StaticVariables.whattodo = "custompads";
                    mListener.openFragment();
                }
            }
        });
        padCrossFadeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "crossfade";
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });
        switchTimerSize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"autoscrollLargeFontInfoBar",b);
                if (mListener!=null) {
                    mListener.updateExtraInfoColorsAndSizes("pad");
                }
            }
        });
        padActivatedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                StaticVariables.clickedOnPadStart = b;
                if (!b && mListener!=null) {
                    mListener.killPad();
                }
            }
        });

    }

    private static void metronomeOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionMetronomeTitle);
        Button metronomeButton = v.findViewById(R.id.metronomeButton);
        Button metronomeLengthButton = v.findViewById(R.id.metronomeLengthButton);
        SwitchCompat metronomeStartButton = v.findViewById(R.id.metronomeStartButton);
        SwitchCompat metronomeActivatedSwitch = v.findViewById(R.id.metronomeActivatedSwitch);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        int val = preferences.getMyPreferenceInt(c,"metronomeLength",0);
        String str;
        if (val==0) {
            str = c.getString(R.string.metronome_duration) + ": " + c.getString(R.string.continuous);
        } else {
            str = c.getString(R.string.metronome_duration) + ": "+val;
        }

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.metronome).toUpperCase(StaticVariables.locale));
        setTextButtons(metronomeButton,c.getString(R.string.metronome));
        setTextButtons(metronomeLengthButton,str);
        setTextButtons(metronomeActivatedSwitch,c.getString(R.string.activated));
        setTextButtons(metronomeStartButton,c.getString(R.string.autostartmetronome));

        // Set the switches up based on preferences
        metronomeStartButton.setChecked(preferences.getMyPreferenceBoolean(c,"metronomeAutoStart",false));
        metronomeActivatedSwitch.setChecked(StaticVariables.clickedOnMetronomeStart);

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        metronomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.isSong) {
                    StaticVariables.whattodo = "page_metronome";
                    if (mListener != null) {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                    }
                }
            }
        });
        metronomeLengthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "page_metronome";
                if (mListener!=null) {
                    try {
                        mListener.closeMyDrawers("option");
                        mListener.openFragment();
                        mListener.prepareOptionMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        metronomeStartButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.setMyPreferenceBoolean(c,"metronomeAutoStart",b);
            }
        });

        metronomeActivatedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                StaticVariables.clickedOnMetronomeStart = b;
                if (!b && mListener!=null) {
                    if (StaticVariables.metronomeonoff.equals("on")) {
                        mListener.stopMetronome();
                    }
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });
    }

    private static void ccliOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionCCLITitle);
        Button ccliChurchButton = v.findViewById(R.id.ccliChurchButton);
        Button ccliLicenceButton = v.findViewById(R.id.ccliLicenceButton);
        SwitchCompat ccliAutoButton = v.findViewById(R.id.ccliAutoButton);
        Button ccliViewButton = v.findViewById(R.id.ccliViewButton);
        Button ccliExportButton = v.findViewById(R.id.ccliExportButton);
        Button ccliResetButton = v.findViewById(R.id.ccliResetButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        String mcname = preferences.getMyPreferenceString(c,"ccliChurchName","");
        String mcnum  = preferences.getMyPreferenceString(c,"ccliLicence","");
        if (!mcname.isEmpty()) {
            mcname = "\n"+mcname;
        }
        if (!mcnum.isEmpty()) {
            mcnum = "\n"+mcnum;
        }
        String cname = c.getString(R.string.ccli_church).toUpperCase(StaticVariables.locale) + mcname;
        String clice = c.getString(R.string.ccli_licence).toUpperCase(StaticVariables.locale) + mcnum;

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.edit_song_ccli).toUpperCase(StaticVariables.locale));
        setTextButtons(ccliChurchButton,cname);
        setTextButtons(ccliLicenceButton,clice);
        setTextButtons(ccliAutoButton,c.getString(R.string.ccli_automatic));
        setTextButtons(ccliViewButton,c.getString(R.string.ccli_view));
        setTextButtons(ccliExportButton,c.getString(R.string.export));
        setTextButtons(ccliResetButton,c.getString(R.string.ccli_reset));

        // Set the switches up based on preferences
        ccliAutoButton.setChecked(preferences.getMyPreferenceBoolean(c,"ccliAutomaticLogging",false));

        // Set the button listeners
        menuup.setOnClickListener(new MenuNavigateListener("MAIN"));
        ccliAutoButton.setOnCheckedChangeListener(new SwitchBooleanSaveListener(c,preferences,"ccliAutomaticLogging"));
        ccliChurchButton.setOnClickListener(new OpenFragmentButtonListener("ccli_church",""));
        ccliLicenceButton.setOnClickListener(new OpenFragmentButtonListener("ccli_licence",""));
        ccliViewButton.setOnClickListener(new OpenFragmentButtonListener("ccli_view",""));
        ccliExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whattodo = "ccli_export";
                if (mListener!=null) {
                    mListener.doExport();
                }
            }
        });
        ccliResetButton.setOnClickListener(new OpenFragmentButtonListener("ccli_reset",""));
        closeOptionsFAB.setOnClickListener(new FABCloseListener());
    }

    private static void otherOptionListener(View v, final Context c, final Preferences preferences) {
        mListener = (MyInterface) c;

        // Identify the buttons
        TextView menuup = v.findViewById(R.id.optionOtherTitle);
        Button otherHelpButton = v.findViewById(R.id.otherHelpButton);
        Button otherTweetButton = v.findViewById(R.id.otherTweetButton);
        Button otherLanguageButton = v.findViewById(R.id.otherLanguageButton);
        Button otherStartButton = v.findViewById(R.id.otherStartButton);
        Button otherRateButton = v.findViewById(R.id.otherRateButton);
        Button otherPayPalButton = v.findViewById(R.id.otherPayPalButton);
        Button otherEmailButton = v.findViewById(R.id.otherEmailButton);
        FloatingActionButton closeOptionsFAB = v.findViewById(R.id.closeOptionsFAB);

        // Capitalise all the text by locale
        menuup.setText(c.getString(R.string.other).toUpperCase(StaticVariables.locale));
        setTextButtons(otherHelpButton,c.getString(R.string.help));
        setTextButtons(otherTweetButton,c.getString(R.string.twitteruser));
        setTextButtons(otherLanguageButton,c.getString(R.string.language));
        setTextButtons(otherStartButton,c.getString(R.string.start_screen));
        setTextButtons(otherRateButton,c.getString(R.string.rate));
        setTextButtons(otherEmailButton,c.getString(R.string.forum));
        setTextButtons(otherPayPalButton,c.getString(R.string.paypal));

        // Set the button listeners
        menuup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVariables.whichOptionMenu = "MAIN";
                if (mListener!=null) {
                    mListener.prepareOptionMenu();
                }
            }
        });

        otherHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.opensongapp.com/user-guide";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.callIntent("web",i);
                }
            }
        });

        otherTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.callIntent("twitter",null);
                }
            }
        });

        otherEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.opensongapp.com/forum";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.callIntent("web",i);
                }
            }
        });
        otherLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    StaticVariables.whattodo = "language";
                    mListener.closeMyDrawers("option");
                    mListener.openFragment();
                }
            }
        });

        otherStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    // Set the last used version to 1
                    // Setting to 0 is now only for fresh installs
                    preferences.setMyPreferenceInt(c, "lastUsedVersion", 1);
                    mListener.closeMyDrawers("option");
                    mListener.splashScreen();
                }
            }
        });

        otherRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    // Rate this app
                    String appPackage = c.getPackageName();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                    mListener.closeMyDrawers("option");
                    mListener.callIntent("web", i);
                }
            }
        });

        otherPayPalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    // PayPal.Me
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/opensongapp"));
                    mListener.closeMyDrawers("option");
                    mListener.callIntent("web", i);
                }
            }
        });

        closeOptionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                }
            }
        });
    }

    public static void updateMenuVersionNumber(Context c, TextView showVersion) {
        // Update the app version in the menu
        PackageInfo pinfo;
        int versionNumber = 0;
        String versionName = "?";
        try {
            pinfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            versionNumber = pinfo.versionCode;
            versionName = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }

        if (!versionName.equals("?") && versionNumber > 0) {
            String temptext = "V" + versionName + " (" + versionNumber + ")";
            if (showVersion != null) {
                showVersion.setVisibility(View.VISIBLE);
                showVersion.setText(temptext);
            }
        } else {
            if (showVersion != null) {
                showVersion.setVisibility(View.GONE);
            }
        }
    }

    private static class OpenFragmentButtonListener implements View.OnClickListener {
        String whattodo;
        String closedrawer;

        OpenFragmentButtonListener(String whattodo, String closedrawer) {
            this.whattodo = whattodo;
            this.closedrawer = closedrawer;
        }

        @Override
        public void onClick(View v) {
            if (mListener!=null) {
                StaticVariables.whattodo = whattodo;
                if (closedrawer!=null && !closedrawer.isEmpty()) {
                    mListener.closeMyDrawers(closedrawer);
                }
                mListener.openFragment();
            }
        }
    }

    private static class MenuNavigateListener implements View.OnClickListener {
        String menu;

        MenuNavigateListener(String menu) {
            this.menu = menu;
        }

        @Override
        public void onClick(View v) {
            StaticVariables.whichOptionMenu = menu;
            if (mListener!=null) {
                mListener.prepareOptionMenu();
            }
        }
    }

    private static class FABCloseListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mListener!=null) {
                mListener.closeMyDrawers("option");
            }
        }
    }

    private static class ModeChangeListener implements View.OnClickListener {
        Preferences preferences;
        String mode;
        Context c;

        ModeChangeListener(Context c, Preferences prefs, String mode) {
            this.c = c;
            this.preferences = prefs;
            this.mode = mode;
        }

        @Override
        public void onClick(View v) {
            if (!StaticVariables.whichMode.equals(mode)) {
                // Switch to mode
                StaticVariables.whichMode = mode;
                preferences.setMyPreferenceString(c,"whichMode",mode);
                Intent newmode = new Intent();
                switch (mode) {
                    case "Performance":
                    case "Stage":
                    default:
                        newmode.setClass(c, StageMode.class);
                        break;

                    case "Presentation":
                    case "Presenter":
                        newmode.setClass(c,PresenterMode.class);
                }
                if (mListener!=null) {
                    mListener.closeMyDrawers("option");
                    mListener.callIntent("activity", newmode);
                }
            }
        }
    }

    private static class SwitchBooleanSaveListener implements CompoundButton.OnCheckedChangeListener {

        Context c;
        Preferences preferences;
        String prefName;

        SwitchBooleanSaveListener(Context ctx, Preferences prefs, String pName) {
            this.c = ctx;
            this.preferences = prefs;
            this.prefName = pName;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            preferences.setMyPreferenceBoolean(c,prefName,isChecked);
        }
    }

}