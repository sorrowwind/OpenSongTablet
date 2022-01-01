package com.garethevans.church.opensongtablet.screensetup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.songprocessing.SongDetailsBottomSheet;

public class AppActionBar {

    private final String TAG = "AppActionBar";
    private final ActionBar actionBar;
    private final TextView title;
    private final TextView author;
    private final TextView key;
    private final TextView capo;
    private final ImageView batteryDial;
    private final TextView batteryText;
    private final TextView clock;
    private final BatteryStatus batteryStatus;
    private final Handler delayactionBarHide;
    private final Runnable hideActionBarRunnable;
    private final int autoHideTime = 1200;

    private boolean hideActionBar;
    private boolean performanceMode;

    public AppActionBar(ActionBar actionBar, BatteryStatus batteryStatus, TextView title, TextView author, TextView key, TextView capo, ImageView batteryDial,
                        TextView batteryText, TextView clock, boolean hideActionBar) {
        if (batteryStatus == null) {
            this.batteryStatus = new BatteryStatus();
        } else {
            this.batteryStatus = batteryStatus;
        }
        this.actionBar = actionBar;
        this.title = title;
        this.author = author;
        this.key = key;
        this.capo = capo;
        this.batteryDial = batteryDial;
        this.batteryText = batteryText;
        this.clock = clock;
        this.hideActionBar = hideActionBar;
        delayactionBarHide = new Handler();
        hideActionBarRunnable = () -> {
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.hide();
            }
        };
    }

    public void setHideActionBar(boolean hideActionBar) {
        this.hideActionBar = hideActionBar;
    }
    public boolean getHideActionBar() {
        return hideActionBar;
    }
    public void setActionBar(Context c, MainActivityInterface mainActivityInterface, String newtitle) {
        if (newtitle == null) {
            // We are in the Performance/Stage mode
            float mainsize = mainActivityInterface.getPreferences().getMyPreferenceFloat(c,"songTitleSize",13.0f);

            if (title != null && mainActivityInterface.getSong().getTitle() != null) {
                title.setTextSize(mainsize);
                title.setText(mainActivityInterface.getSong().getTitle());
            }
            if (author != null && mainActivityInterface.getSong().getAuthor() != null &&
                    !mainActivityInterface.getSong().getAuthor().isEmpty()) {
                author.setTextSize(mainActivityInterface.getPreferences().getMyPreferenceFloat(c,"songAuthorSize",11.0f));
                author.setText(mainActivityInterface.getSong().getAuthor());
                hideView(author, false);
            } else {
                hideView(author, true);
            }
            if (key != null && mainActivityInterface.getSong().getKey() != null &&
                    !mainActivityInterface.getSong().getKey().isEmpty()) {
                String k = " (" + mainActivityInterface.getSong().getKey() + ")";
                key.setTextSize(mainsize);
                capo.setTextSize(mainsize);
                key.setText(k);
                hideView(key, false);
            } else {
                hideView(key, true);
            }
            if (title!=null) {
                title.setOnClickListener(v -> openDetails(mainActivityInterface));
                title.setOnLongClickListener(view -> {
                    editSong(mainActivityInterface);
                    return true;
                });
            }
            if (author!=null) {
                author.setOnClickListener(v -> openDetails(mainActivityInterface));
                author.setOnLongClickListener(view -> {
                    editSong(mainActivityInterface);
                    return true;
                });
            }
            if (key!=null) {
                key.setOnClickListener(v -> openDetails(mainActivityInterface));
                key.setOnLongClickListener(view -> {
                    editSong(mainActivityInterface);
                    return true;
                });
            }
        } else {
            // We are in a different fragment, so don't hide the song info stuff
            actionBar.show();
            if (title != null) {
                title.setTextSize(22.0f);
                title.setText(newtitle);
                hideView(author, true);
                hideView(key, true);
            }
        }
    }

    private void openDetails(MainActivityInterface mainActivityInterface) {
        if (!mainActivityInterface.getSong().getTitle().equals("Welcome to OpenSongApp")) {
            SongDetailsBottomSheet songDetailsBottomSheet = new SongDetailsBottomSheet();
            songDetailsBottomSheet.show(mainActivityInterface.getMyFragmentManager(), "songDetailsBottomSheet");
        }
    }
    private void editSong(MainActivityInterface mainActivityInterface) {
        if (!mainActivityInterface.getSong().getTitle().equals("Welcome to OpenSongApp")) {
            mainActivityInterface.navigateToFragment("opensongapp://settings/edit", 0);
        }
    }

    public void setActionBarCapo(TextView capo, String string) {
        capo.setText(string);
    }

    public void updateActionBarSettings(Context c, MainActivityInterface mainActivityInterface,
                                        String prefName, int intval, float floatval, boolean isvisible) {
        switch (prefName) {
            case "batteryDialOn":
                hideView(batteryDial,!isvisible);
                break;
            case "batteryDialThickness":
                batteryStatus.setBatteryImage(c,batteryDial,actionBar.getHeight(),(int) (batteryStatus.getBatteryStatus(c) * 100.0f),intval);
                break;
            case "batteryTextOn":
                hideView(batteryText,!isvisible);
                break;
            case "batteryTextSize":
                batteryText.setTextSize(floatval);
                break;
            case "clockOn":
                hideView(clock,!isvisible);
                break;
            case "clock24hFormat":
                batteryStatus.updateClock(mainActivityInterface,clock,mainActivityInterface.getPreferences().getMyPreferenceFloat(c,"clockTextSize",9.0f),
                        clock.getVisibility()==View.VISIBLE,isvisible);
                break;
            case "clockTextSize":
                clock.setTextSize(floatval);
                break;
            case "songTitleSize":
                title.setTextSize(floatval);
                key.setTextSize(floatval);
                capo.setTextSize(floatval);
                break;
            case "songAuthorSize":
                author.setTextSize(floatval);
                break;
            case "hideActionBar":
                setHideActionBar(!isvisible);
                break;
        }
    }

    private void hideView(View v, boolean hide) {
        if (hide) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

    // Action bar stuff
    public void toggleActionBar(boolean wasScrolling, boolean scrollButton,
                                boolean menusActive) {
        try {
            delayactionBarHide.removeCallbacks(hideActionBarRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (actionBar != null) {
            if (wasScrolling || scrollButton) {
                if (hideActionBar && !menusActive) {
                    actionBar.hide();
                }
            } else if (!menusActive) {
                if (actionBar.isShowing() && hideActionBar) {
                    delayactionBarHide.postDelayed(hideActionBarRunnable, 500);
                } else {
                    actionBar.show();
                    // Set a runnable to hide it after 3 seconds
                    if (hideActionBar) {
                        delayactionBarHide.postDelayed(hideActionBarRunnable, autoHideTime);
                    }
                }
            }
        }
    }


    // Set when entering/exiting performance mode
    public void setPerformanceMode(boolean inPerformanceMode) {
        performanceMode = inPerformanceMode;
    }

    // Show/hide the actionbar
    public void showActionBar(boolean menuOpen) {
        // Show the ActionBar based on the user preferences
        // If we are in performance mode (boolean set when opening/closing PerformanceFragment)
        // The we can autohide if the user preferences state that's what is wanted
        // If we are not in performance mode, we don't set a runnable to autohide them
        try {
            delayactionBarHide.removeCallbacks(hideActionBarRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (actionBar != null) {
            actionBar.show();
        }

        if (hideActionBar && performanceMode && !menuOpen) {
            try {
                delayactionBarHide.postDelayed(hideActionBarRunnable, autoHideTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeCallBacks() {
        delayactionBarHide.removeCallbacks(hideActionBarRunnable);
    }

    // Flash on/off for metronome
    public void doFlash(int colorBar) {
        actionBar.setBackgroundDrawable(new ColorDrawable(colorBar));
    }

    // Get the actionbar height - fakes a height of 0 if autohiding
    public int getActionBarHeight() {
        if (hideActionBar && performanceMode) {
            return 0;
        } else {
            return actionBar.getHeight();
        }
    }
}