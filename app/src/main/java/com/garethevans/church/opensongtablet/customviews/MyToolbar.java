package com.garethevans.church.opensongtablet.customviews;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.songprocessing.SongDetailsBottomSheet;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;


public class MyToolbar extends MaterialToolbar {

    // This holds references to the items in the Toolbar (except the battery)
    // Battery changes get sent via the mainactivityInterface
    // The toolbar is set as the supportActionbar received as actionBar, so can called via that as well

    private Context c;
    private MainActivityInterface mainActivityInterface;
    private ActionBar actionBar;
    private final FrameLayout batteryholder;
    private final TextView title;
    private final TextView author;
    private final TextView key;
    private final TextView capo;
    private final TextClock clock;
    private final ImageView setIcon, batteryimage;
    private final FrameLayout songandauthor;
    private final com.google.android.material.textview.MaterialTextView batterycharge;
    private Handler delayActionBarHide;
    private Runnable hideActionBarRunnable;
    private int actionBarHideTime = 1200, additionalTopPadding = 0;
    private float clockTextSize;
    private boolean clock24hFormat, clockOn, hideActionBar, clockSeconds, performanceMode;
    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "MyToolbar";
    private String capoString = "", keyString = "";

    // Set up the view and view items
    public void initialiseToolbar(Context c, ActionBar actionBar) {
        this.c = c;
        mainActivityInterface = (MainActivityInterface) c;
        this.actionBar = actionBar;
        delayActionBarHide = new Handler();
        hideActionBarRunnable = () -> {
            if (actionBar.isShowing() && !mainActivityInterface.needActionBar()) {
                actionBar.hide();
            }
        };
        updateClock();
    }
    public MyToolbar(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        View v = inflate(context, R.layout.view_toolbar_constraint, this);
        setIcon = v.findViewById(R.id.setIcon);
        title = v.findViewById(R.id.songtitle_ab);
        key = v.findViewById(R.id.songkey_ab);
        capo = v.findViewById(R.id.songcapo_ab);
        author = v.findViewById(R.id.songauthor_ab);
        batteryholder = v.findViewById(R.id.batteryholder);
        batteryimage = v.findViewById(R.id.batteryimage);
        batterycharge = v.findViewById(R.id.batterycharge);
        songandauthor = v.findViewById(R.id.songandauthor);
        clock = v.findViewById(R.id.digitalclock);

        batteryholder.setOnClickListener(v1 -> {
            mainActivityInterface.showActionBar();
            mainActivityInterface.navigateToFragment(c.getString(R.string.deeplink_actionbar), 0);
        });
    }

    // Deal with the preferences used for the actionbar
    private void updateActionBarPrefs() {
        clockTextSize = mainActivityInterface.getPreferences().getMyPreferenceFloat("clockTextSize",9.0f);
        clock24hFormat = mainActivityInterface.getPreferences().getMyPreferenceBoolean("clock24hFormat",true);
        clockOn = mainActivityInterface.getPreferences().getMyPreferenceBoolean("clockOn",true);
        clockSeconds = mainActivityInterface.getPreferences().getMyPreferenceBoolean("clockSeconds",false);
        hideActionBar = mainActivityInterface.getPreferences().getMyPreferenceBoolean("hideActionBar",false);
        actionBarHideTime = mainActivityInterface.getPreferences().getMyPreferenceInt("actionBarHideTime",1200);
    }
    public void updateActionBarSettings(String prefName, float value, boolean isvisible) {

        switch (prefName) {
            case "batteryDialOn":
                mainActivityInterface.getBatteryStatus().setBatteryDialOn(isvisible);
                break;
            case "batteryDialThickness":
                mainActivityInterface.getBatteryStatus().setBatteryDialThickness((int)value);
                mainActivityInterface.getBatteryStatus().setBatteryImage();
                break;
            case "batteryTextOn":
                mainActivityInterface.getBatteryStatus().setBatteryTextOn(isvisible);
                break;
            case "batteryTextSize":
                mainActivityInterface.getBatteryStatus().setBatteryTextSize(value);
                break;
            case "clockOn":
                clockOn = isvisible;
                if (!mainActivityInterface.getSettingsOpen()) {
                    hideView(clock, !isvisible);
                }
                break;
            case "clock24hFormat":
                clock24hFormat = isvisible;
                updateClock();
                break;
            case "clockSeconds":
                clockSeconds = isvisible;
                updateClock();
                break;
            case "clockTextSize":
                clock.setTextSize(value);
                break;
            case "songTitleSize":
                title.setTextSize(value);
                key.setTextSize(value);
                capo.setTextSize(value);
                break;
            case "songAuthorSize":
                author.setTextSize(value);
                break;
            case "hideActionBar":
                setHideActionBar(!isvisible);
                break;
            case "actionBarHideTime":
                actionBarHideTime = (int)value;
                break;
            case "showBatteryHolder":
                // GE The battery holder is now just the clickable area
                // This is called to stop clicks while changing the settings
                batteryholder.setVisibility(isvisible ? View.VISIBLE : View.GONE);
                break;
        }
    }

    public void setHideActionBar(boolean hideActionBar) {
        this.hideActionBar = hideActionBar;
    }
    public boolean getHideActionBar() {
        return hideActionBar;
    }

    // Update the text in the actionbar to either be a song info, or menu title
    public void setActionBar(String newtitle) {
        // If changing, reset help
        if (title!=null && !title.getText().equals(newtitle)) {
            // By default hide help (can be shown later)
            mainActivityInterface.updateToolbarHelp("");
        }

        if (newtitle == null) {
            // We are in the Performance/Stage mode
            float mainsize = mainActivityInterface.getPreferences().getMyPreferenceFloat("songTitleSize",13.0f);

            // If we are in a set, show the icon
            int positionInSet = mainActivityInterface.getSetActions().indexSongInSet(mainActivityInterface.getSong());
            if (positionInSet>-1) {
                // Check the set menu fragment to see if we need to highlight
                // This happens if we just loaded a song (not clicking on the set item in the menu)
                mainActivityInterface.checkSetMenuItemHighlighted(positionInSet);
                setIcon.setVisibility(View.VISIBLE);
                mainActivityInterface.getCurrentSet().setIndexSongInSet(positionInSet);
            } else {
                setIcon.setVisibility(View.GONE);
                mainActivityInterface.getCurrentSet().setIndexSongInSet(-1);
            }

            if (title != null && mainActivityInterface.getSong().getTitle() != null) {
                title.setTextSize(mainsize);
                String text = mainActivityInterface.getSong().getTitle();
                if (mainActivityInterface.getSong().getFolder().startsWith("*")) {
                    text = "*" + text;
                }
                title.setText(text);
                hideView(title, false);
            } else if (title!=null) {
                hideView(title, true);
            }
            if (author != null && mainActivityInterface.getSong().getAuthor() != null &&
                    !mainActivityInterface.getSong().getAuthor().isEmpty()) {
                author.setTextSize(mainActivityInterface.getPreferences().getMyPreferenceFloat("songAuthorSize",11.0f));
                author.setText(mainActivityInterface.getSong().getAuthor());
                hideView(author, false);
            } else if (author!=null) {
                hideView(author, true);
            }
            if (key != null && mainActivityInterface.getSong().getKey() != null &&
                    !mainActivityInterface.getSong().getKey().isEmpty()) {
                keyString = " (" + mainActivityInterface.getSong().getKey() + ")";
                key.setTextSize(mainsize);
                capo.setTextSize(mainsize);
                key.setText(keyString);
                hideView(key, false);
            } else if (key!=null) {
                hideView(key, true);
            }
            if (capo != null && mainActivityInterface.getSong().getCapo() !=null &&
                    !mainActivityInterface.getSong().getCapo().isEmpty()) {
                capoString = mainActivityInterface.getChordDisplayProcessing().getCapoPosition();
            } else {
                capoString = "";
            }

            if (!capoString.isEmpty() && !keyString.isEmpty()) {
                capoString += (" (" + mainActivityInterface.getTranspose().capoKeyTranspose() + ")").replace(" ()","");
            }
            String thisCapoString = "";
            if (!capoString.isEmpty()) {
                thisCapoString = " ["+capoString+"]";
            }
            if (thisCapoString.isEmpty() && capo!=null) {
                hideView(capo,true);
            } else if (capo!=null) {
                capo.setText(thisCapoString);
                hideView(capo,false);
            }

            if (title!=null) {
                title.setOnClickListener(v -> openDetails());
                title.setOnLongClickListener(view -> {
                    editSong();
                    return true;
                });
            }
            if (author!=null) {
                author.setOnClickListener(v -> openDetails());
                author.setOnLongClickListener(view -> {
                    editSong();
                    return true;
                });
            }
            if (key!=null) {
                key.setOnClickListener(v -> openDetails());
                key.setOnLongClickListener(view -> {
                    editSong();
                    return true;
                });
            }
            if (songandauthor!=null) {
                songandauthor.setOnClickListener(v -> openDetails());
                songandauthor.setOnLongClickListener(view -> {
                    editSong();
                    return true;
                });
            }

        } else {
            // We are in a different fragment, so hide the song info stuff
            setIcon.setVisibility(View.GONE);
            if (title != null) {
                title.setOnClickListener(null);
                title.setOnLongClickListener(null);
                title.setTextSize(18.0f);
                title.setText(newtitle);
                hideView(title, false);
                hideView(author, true);
                hideView(key, true);
                hideView(capo,true);
                songandauthor.setOnClickListener(null);
                songandauthor.setOnLongClickListener(null);
            }
        }
    }

    // The onscreen capo display can grab this rather than reprocessing it
    public String getCapoString() {
        return capoString;
    }

    // Clicking on the song title/author/etc. opens up the song details bottom sheet
    private void openDetails() {
        if (!mainActivityInterface.getSong().getTitle().equals("Welcome to OpenSongApp")) {
            SongDetailsBottomSheet songDetailsBottomSheet = new SongDetailsBottomSheet();
            songDetailsBottomSheet.show(mainActivityInterface.getMyFragmentManager(), "songDetailsBottomSheet");
        }
    }

    // Allow editing the song by long pressing on the title/author/etc. if not null
    private void editSong() {
        if (!mainActivityInterface.getSong().getTitle().equals("Welcome to OpenSongApp")) {
            mainActivityInterface.navigateToFragment(c.getString(R.string.deeplink_edit), 0);
        }
    }

    // This is used to show/hide various parts of the toolbar text (author, copyright, etc)
    public void hideView(View v, boolean hide) {
        if (v!=null) {
            v.setVisibility(hide ? View.GONE : View.VISIBLE);
        }
    }

    public void hideSongDetails(boolean hide) {
        hideView(title,hide);
        hideView(author,hide);
        hideView(key,hide);
        hideView(capo,hide);
    }

    // Set when entering/exiting performance mode as this is used to determine if we can autohide actionbar
    public void setPerformanceMode(boolean inPerformanceMode) {
        performanceMode = inPerformanceMode;
    }

    // Show/hide the actionbar
    public void showActionBar(boolean menuOpen) {
        // Remove any existing callbacks to hide the actionbar
        try {
            delayActionBarHide.removeCallbacks(hideActionBarRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Now show the action bar
        actionBar.show();

        // If we need to hide the actionbar again, set a runnable, as long as the menu isn't open
        if (hideActionBar && performanceMode && !menuOpen) {
            try {
                delayActionBarHide.postDelayed(hideActionBarRunnable, actionBarHideTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeCallBacks() {
        delayActionBarHide.removeCallbacks(hideActionBarRunnable);
    }

    // Flash on/off for metronome
    public void doFlash(int colorBar) {
        setBackground(new ColorDrawable(colorBar));
    }

    // Get the actionbar height - fakes a height of 0 if autohiding
    public int getActionBarHeight(boolean forceShown) {
        if (hideActionBar && performanceMode && !forceShown) {
            return mainActivityInterface.getWindowFlags().getCustomMarginTop();
        } else {
            if (getHeight()==0) {
                // Just in case there was a call before drawn
                return 168;
            } else {
                return getHeight();
            }
        }
    }

    public void setAdditionalTopPadding(int additionalTopPadding) {
        this.additionalTopPadding = additionalTopPadding;
    }
    public int getAdditionalTopPadding() {
        return additionalTopPadding;
    }

    public void updateClock() {
        // IV - Refresh from preferences as may have changed
        updateActionBarPrefs();
        mainActivityInterface.getTimeTools().setFormat(clock, mainActivityInterface.getSettingsOpen(),
                clockTextSize, clockOn, clock24hFormat, clockSeconds);
    }

    public void batteryholderVisibility(boolean visible, boolean clickable) {
        batteryholder.setVisibility(visible ? View.VISIBLE:View.GONE);
        batteryholder.setClickable(clickable);
        showClock(visible);
    }

    public ImageView getBatteryimage() {
        return batteryimage;
    }

    public MaterialTextView getBatterycharge() {
        return batterycharge;
    }

    public void showClock(boolean show) {
        // Only show if that is our preference
        clock.setTextSize(clockTextSize);
        clock.setVisibility(show && clockOn ? View.VISIBLE:View.GONE);
    }

    @Override
    public void setNavigationOnClickListener(OnClickListener listener) {
        super.setNavigationOnClickListener(listener);
    }

    @Override
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        super.setOnMenuItemClickListener(listener);
    }

    public boolean isHidden() {
        return isShown();
    }

}
