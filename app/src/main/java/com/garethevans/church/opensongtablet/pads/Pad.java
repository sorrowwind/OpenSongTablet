package com.garethevans.church.opensongtablet.pads;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Pad {
    private final String TAG = "Pad";
    public boolean orientationChanged;
    public boolean pad1Pause, pad2Pause;
    public int padInQuickFade;
    private final Context c;
    private final MainActivityInterface mainActivityInterface;
    private MediaPlayer pad1, pad2;
    private boolean pad1Fading, pad2Fading;
    private int currentOrientation;
    private float pad1VolL, pad1VolR, pad2VolL, pad2VolR;
    private Timer pad1FadeTimer, pad2FadeTimer;
    private TimerTask pad1FadeTimerTask, pad2FadeTimerTask;
    private final Handler pad1FadeTimerHandler = new Handler();
    private final Handler pad2FadeTimerHandler = new Handler();
    private final LinearLayout pad;
    private final MaterialTextView padTime;
    private final MaterialTextView padTotalTime;
    private int padLength;
    private float pad1VolDrop, pad2VolDrop;
    private boolean padsActivated = false;
    private CharSequence padPauseTime;
    private Timer padPlayTimer;
    private TimerTask padPlayTimerTask;
    private final Handler padPlayTimerHandler = new Handler();

    public Pad(Context c, LinearLayout pad) {
        this.c = c;
        mainActivityInterface = (MainActivityInterface) c;
        this.pad = pad;
        padTime = pad.findViewById(R.id.padTime);
        padTotalTime = pad.findViewById(R.id.padTotalTime);
    }

    public void startPad() {
        Log.d("TAG","managePads StartPad");
        // IV - managePads will fade all running pads
        // managePads will start the new pad if/when a pad player is free
        padsActivated = true;
        managePads();
    }

    public void stopPad() {
        Log.d(TAG,"managePads StopPad");
        padsActivated = false;
        // IV - managePads fades all running pads
        managePads();
        stopPadPlay();
    }

    private void stopAndReset(int padNum) {
        switch (padNum) {
            case 1:
                if (pad1 != null) {
                    try {
                        pad1Pause = false;
                        pad1.reset();
                        pad1.release();
                        pad1 = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (pad2 != null) {
                    try {
                        pad2Pause = false;
                        pad2.reset();
                        pad2.release();
                        pad2 = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private String keyToFlat(String key) {
        return key.replace("A#","Bb").replace("C#","Db")
                .replace("D#","Eb").replace("F#","Gb")
                .replace("G#","Ab");
    }

    public boolean isAutoPad() {
        String padFile = mainActivityInterface.getSong().getPadfile();
        if (padFile == null) {
            padFile = "";
        }
        String key = mainActivityInterface.getSong().getKey();
        if (key == null) {
            key = "";
        }
        return (padFile.isEmpty() || padFile.equals("auto") || padFile.equals(c.getString(R.string.pad_auto))) && !key.isEmpty();
    }

    public boolean isCustomAutoPad() {
        String key = mainActivityInterface.getSong().getKey();
        if (key == null) {
            key = "";
        }
        String customPad = "";
        if (!key.isEmpty()) {
            customPad = mainActivityInterface.getPreferences().getMyPreferenceString("customPad" + keyToFlat(key) ,"");
        }
        return isAutoPad() && customPad!=null && !customPad.isEmpty();
    }

    public boolean isLinkAudio() {
        String padFile = mainActivityInterface.getSong().getPadfile();
        if (padFile == null) {
            padFile = "";
        }
        String linkAudio = mainActivityInterface.getSong().getLinkaudio();
        return (padFile.equals("link") || padFile.equals(c.getString(R.string.link_audio))) &&
                linkAudio!=null && !linkAudio.isEmpty();
    }

    public Uri getPadUri() {
        Uri padUri = null;
        if (isCustomAutoPad()) {
            padUri = mainActivityInterface.getStorageAccess().fixLocalisedUri(
                    mainActivityInterface.getPreferences().getMyPreferenceString(
                            "customPad"+keyToFlat(mainActivityInterface.getSong().getKey()),""));
        } else if (isLinkAudio()) {
            padUri = mainActivityInterface.getStorageAccess().fixLocalisedUri(
                    mainActivityInterface.getSong().getLinkaudio());
        }
        // If none of the above, we assume an auto pad if the key has been set.
        // Since autopads are raw asset files, we use assetfiledescriptor instead.
        // A null padUri, will trigger that
        return padUri;
    }

    private boolean isPadValid(Uri padUri) {
        return mainActivityInterface.getStorageAccess().uriExists(padUri);
    }

    private void loadAndStart(int padNum) {
        Uri padUri = getPadUri();
        String padFile = mainActivityInterface.getSong().getPadfile();
        if (padFile == null) {
            padFile = "";
        }
        String key = mainActivityInterface.getSong().getKey();
        if (key == null) {
            key = "";
        }

        // If the padUri is null, we likely need a default autopad assuming the key is set
        AssetFileDescriptor assetFileDescriptor = null;
        if (padUri==null && !key.isEmpty()) {
            assetFileDescriptor = getAssetPad(key);
        }

        // Decide if pad should loop
        boolean padLoop = mainActivityInterface.getSong().getPadloop().equals("true");

        // Decide if the pad is valid
        boolean padValid = (assetFileDescriptor!=null || isPadValid(padUri)) &&
                !padFile.equals("off");

        // Prepare any error message
        if (!padValid) {
            if (key.isEmpty()) {
                mainActivityInterface.getShowToast().doIt(c.getString(R.string.pad_key_error));
            } else if (isCustomAutoPad()) {
                mainActivityInterface.getShowToast().doIt(c.getString(R.string.pad_file_error));
            } else if (isLinkAudio()) {
                mainActivityInterface.getShowToast().doIt(c.getString(R.string.pad_custom_pad_error));
            } else if (padFile.equals("off")) {
                mainActivityInterface.getShowToast().doIt(c.getString(R.string.pad_off));
            }
            stopPadPlay();
        }

        if (padValid) {
            switch (padNum) {
                case 1:
                    pad1 = new MediaPlayer();
                    pad1.setOnCompletionListener(mediaPlayer -> {
                        stopAndReset(1);
                        stopPadPlay();
                    });
                    pad1.setOnPreparedListener(mediaPlayer -> doPlay(1));
                    if (assetFileDescriptor != null) {
                        try {
                            pad1.setDataSource(assetFileDescriptor.getFileDescriptor(),
                                    assetFileDescriptor.getStartOffset(),
                                    assetFileDescriptor.getLength());
                            assetFileDescriptor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (padUri != null) {
                        try {
                            pad1.setDataSource(c, padUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    pad1.setLooping(padLoop);
                    pad1.prepareAsync();
                    pad1.setOnErrorListener((mp, what, extra) -> {
                        // IV - Could not prepare pad - try again!
                        Log.d(TAG, "managePads pad1 Fail in prepare of MediaPlayer! Try again.");
                        if (padsActivated) {
                            stopAndReset(1);
                            loadAndStart(1);
                        }
                        return false;
                    });
                    break;
                case 2:
                    pad2 = new MediaPlayer();
                    pad2.setOnCompletionListener(mediaPlayer -> {
                        stopAndReset(2);
                        stopPadPlay();
                    });
                    pad2.setOnPreparedListener(mediaPlayer -> doPlay(2));
                    if (assetFileDescriptor != null) {
                        try {
                            pad2.setDataSource(assetFileDescriptor.getFileDescriptor(),
                                    assetFileDescriptor.getStartOffset(),
                                    assetFileDescriptor.getLength());
                            assetFileDescriptor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (padUri != null) {
                        try {
                            pad2.setDataSource(c, padUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    pad2.setLooping(padLoop);
                    pad2.prepareAsync();
                    pad2.setOnErrorListener((mp, what, extra) -> {
                        // IV - Could not prepare pad - try again!
                        Log.d(TAG, "managePads pad2 Fail in prepare of MediaPlayer! Try again.");
                        if (padsActivated) {
                            stopAndReset(2);
                            loadAndStart(2);
                        }
                        return false;
                    });
                    break;
            }
        }
    }
    private void managePads() {
        Log.d(TAG, "managePads ------");
        Log.d(TAG, "managePads padActivated " + padsActivated);
        Log.d(TAG, "managePads pad1 is active '" + (pad1 != null) + "'. pad1 is fading '" + pad1Fading + "'.");
        Log.d(TAG, "managePads pad2 is active '" + (pad2 != null) + "'. pad2 is fading '" + pad2Fading + "'.");

        // IV - Fade any running pads
        if ((pad1 != null) || (pad2 != null)) {
            final int fadeTime = mainActivityInterface.getPreferences().getMyPreferenceInt("padCrossFadeTime", 8000);

            if (pad1 != null && !pad1Fading) {
                Log.d(TAG, "managePads Pad1 Fading");
                final float padVol = mainActivityInterface.getPreferences().getMyPreferenceFloat("padVol", 1.0f);
                switch (mainActivityInterface.getPreferences().getMyPreferenceString("padPan", "C")) {
                    case "L":
                        pad1VolL = padVol;
                        pad1VolR = 0.0f;
                        break;
                    case "R":
                        pad1VolL = 0.0f;
                        pad1VolR = padVol;
                        break;
                    default:
                        pad1VolL = padVol;
                        pad1VolR = padVol;
                }

                // How much to drop the vol by each step
                pad1VolDrop = padVol / 50;

                pad1FadeTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        pad1FadeTimerHandler.post(() -> {
                            if (!(pad1VolL == 0 && pad1VolR == 0)) {
                                // IV - When fading
                                pad1Fading = true;
                                if (padInQuickFade == 1) {
                                    Log.d(TAG, "managePads Pad1 Quick fade initiated");
                                    pad1VolDrop = pad1VolDrop * 3;
                                    padInQuickFade = -1;
                                }
                                pad1VolL = newVol(pad1VolL, pad1VolDrop);
                                pad1VolR = newVol(pad1VolR, pad1VolDrop);
                                setVolume(1, pad1VolL, pad1VolR);
                            } else {
                                // IV - When faded
                                stopAndReset(1);
                                pad1Fading = false;
                                Log.d(TAG, "managePads Pad1 Stopped " + padInQuickFade + " " + padsActivated);
                                if (padInQuickFade == -1) {
                                    if (padsActivated) {
                                        Log.d(TAG, "managePads Pad1 start requested after quick fade");
                                        loadAndStart(1);
                                    }
                                    padInQuickFade = 0;
                                }
                                Log.d(TAG,"managePads Pad1 endFadeTimer called");
                                new Handler().post(() -> endFadeTimer(1));
                            }
                        });
                    }
                };
                pad1FadeTimer = new Timer();
                if (pad1Pause) {
                    pad1FadeTimer.scheduleAtFixedRate(pad1FadeTimerTask, 0, 1000 / 50);
                } else {
                    pad1FadeTimer.scheduleAtFixedRate(pad1FadeTimerTask, 0, fadeTime / 50);
                }
                // IV - Needed here
                pad1Fading = true;
            }

            if (pad2 != null && !pad2Fading) {
                Log.d(TAG, "managePads Pad2 Fading");
                final float padVol = mainActivityInterface.getPreferences().getMyPreferenceFloat("padVol", 1.0f);
                switch (mainActivityInterface.getPreferences().getMyPreferenceString("padPan", "C")) {
                    case "L":
                        pad2VolL = padVol;
                        pad2VolR = 0.0f;
                        break;
                    case "R":
                        pad2VolL = 0.0f;
                        pad2VolR = padVol;
                        break;
                    default:
                        pad2VolL = padVol;
                        pad2VolR = padVol;
                }

                // How much to drop the vol by each step
                pad2VolDrop = padVol / 50;

                pad2FadeTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        pad2FadeTimerHandler.post(() -> {
                            if (!(pad2VolL == 0 && pad2VolR == 0f)) {
                                // IV - When fading
                                pad2Fading = true;
                                if (padInQuickFade == 2) {
                                    Log.d(TAG, "managePads pad2 Quick fade initiated");
                                    pad2VolDrop = pad2VolDrop * 3;
                                    padInQuickFade = -2;
                                }
                                pad2VolL = newVol(pad2VolL, pad2VolDrop);
                                pad2VolR = newVol(pad2VolR, pad2VolDrop);
                                setVolume(2, pad2VolL, pad2VolR);
                            } else {
                                // IV - When faded
                                stopAndReset(2);
                                pad2Fading = false;
                                Log.d(TAG, "managePads pad2 Stopped " + padInQuickFade + " " + padsActivated);
                                if (padInQuickFade == -2) {
                                    if (padsActivated) {
                                        Log.d(TAG, "managePads pad2 start requested after quick fade");
                                        loadAndStart(2);
                                    }
                                    padInQuickFade = 0;
                                }
                                Log.d(TAG,"managePads pad2 endFadeTimer called");
                                new Handler().post(() -> endFadeTimer(2));
                            }
                        });
                    }
                };
                pad2FadeTimer = new Timer();
                if (pad2Pause) {
                    pad2FadeTimer.scheduleAtFixedRate(pad2FadeTimerTask, 0, 1000 / 50);
                } else {
                    pad2FadeTimer.scheduleAtFixedRate(pad2FadeTimerTask, 0, fadeTime / 50);
                }
                // IV - Needed here
                pad2Fading = true;
            }
        }

        // IV - If both pads are fading set the quietest one to 'quick fade'
        if (pad1Fading && pad2Fading) {
            padInQuickFade = 1 + (Math.max(pad1VolL, pad1VolR) > Math.max(pad2VolL, pad2VolR) ? 1 : 0);
        }
        Log.d(TAG, ("managePads pad" + padInQuickFade + " Quick fading").replace("pad0 Quick f","No Quick f"));

        if (padsActivated && (pad1 == null || pad2 == null)) {
            if (pad1 == null) {
                pad1Fading = false;
                Log.d(TAG, "managePads Pad1 Start requested as free for use.");
                loadAndStart(1);
            } else {
                pad2Fading = false;
                Log.d(TAG, "managePads Pad2 Start requested as free for use");
                loadAndStart(2);
            }
        }
    }

    private AssetFileDescriptor getAssetPad(String key) {
        // Using the built in pad
        key = key.replace("Ab","G#");
        key = key.replace("Bb","A#");
        key = key.replace("Db","C#");
        key = key.replace("Eb","D#");
        key = key.replace("Gb","F#");
        key = key.replace("#","sharp");
        key = key.toLowerCase(Locale.ROOT);

        int path = c.getResources().getIdentifier(key.toLowerCase(Locale.ROOT), "raw", c.getPackageName());
        return c.getResources().openRawResourceFd(path);
    }
    private void endFadeTimer(int padNum) {
        switch (padNum) {
            case 1:
                if (pad1FadeTimerTask!=null) {
                    pad1FadeTimerTask.cancel();
                    pad1FadeTimerTask = null;
                }
                if (pad1FadeTimer != null) {
                    pad1FadeTimer.cancel();
                    pad1FadeTimer.purge();
                }
                pad1Fading = false;
                break;
            case 2:
                if (pad2FadeTimerTask!=null) {
                    pad2FadeTimerTask.cancel();
                    pad2FadeTimerTask = null;
                }
                if (pad2FadeTimer != null) {
                    pad2FadeTimer.cancel();
                    pad2FadeTimer.purge();
                }
                pad2Fading = false;
                break;
        }
    }
    public void setVolume(int padNum, float volL, float volR) {
        switch (padNum) {
            case 1:
                if (pad1!=null) {
                    try {
                        pad1.setVolume(volL, volR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (pad2!=null) {
                    try {
                        pad2.setVolume(volL, volR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }
    private float newVol(float currVol, float dropVol) {
        currVol = currVol - dropVol;
        if (currVol < 0) {
            return 0.0f;
        } else {
            return currVol;
        }
    }
    private void doPlay(int padNum) {
        Log.d(TAG,"managePads doPlay Pad" + padNum);
        switch (padNum) {
            case 1:
                pad1Fading = false;
                pad1Pause = false;
                pad1.start();
                padLength = (int)(pad1.getDuration()/1000f);
                break;
            case 2:
                pad2Fading = false;
                pad2Pause = false;
                pad2.start();
                padLength = (int)(pad2.getDuration()/1000f);
                break;
        }
        Log.d(TAG,"managePads padLength " + padLength);
        pad.setOnClickListener(v -> playStopOrPause(padNum));
        pad.setOnLongClickListener(v -> longClick(padNum));

        // IV - We setup the on-screen timer display
        padTime.setText("0:00");
        padTotalTime.setText(" / " + mainActivityInterface.getTimeTools().timeFormatFixer(padLength));
        padTotalTime.setVisibility(View.VISIBLE);
        pad.setVisibility(View.VISIBLE);

        // IV - Schedule a new timer when the timer not already running
        if (padPlayTimerTask == null) {
            padPlayTimerTask = new TimerTask() {
                @Override
                public void run() {
                    padPlayTimerHandler.post(() -> {
                        // IV - If stopping
                        if (padTotalTime.getText().equals("Stopping")) {
                            if ((pad1 == null || !pad1.isPlaying()) && (pad2 == null || !pad2.isPlaying())) {
                                stopPadPlayTimer();
                            }
                        } else {
                            // IV - If paused
                            if (pad1Pause || pad2Pause) {
                                padTime.setText(padPauseTime);
                                if (padTime.getCurrentTextColor() == Color.TRANSPARENT) {
                                    padTime.setTextColor(Color.WHITE);
                                } else {
                                    padTime.setTextColor(Color.TRANSPARENT);
                                }
                                // IV - If running normally
                            } else {
                                String text = "0:00";
                                try {
                                    if (pad1 != null && pad1.isPlaying() && !pad1Fading) {
                                        text = mainActivityInterface.getTimeTools().timeFormatFixer((int) (pad1.getCurrentPosition() / 1000f));
                                    }
                                    if (pad2 != null && pad2.isPlaying() && !pad2Fading) {
                                        text = mainActivityInterface.getTimeTools().timeFormatFixer((int) (pad2.getCurrentPosition() / 1000f));
                                    }
                                } catch (Exception e) {
                                    // Nothing to do
                                }
                                padTime.setText(text);
                                padTime.setTextColor(Color.WHITE);
                            }
                        }
                    });
                }
            };
            padPlayTimer = new Timer();
            padPlayTimer.scheduleAtFixedRate(padPlayTimerTask, 1000, 1000);
        }
        mainActivityInterface.updateOnScreenInfo("showhide");
    }

    // Get info about the pads
    public boolean isPadPlaying() {
        return (pad1!=null && pad1.isPlaying() && !pad1Fading) || (pad2!=null && pad2.isPlaying() && !pad2Fading);
    }
    public boolean isPadPrepared() {
        return padTime.getText()!=null && !padTime.getText().toString().isEmpty() &&
                ((pad1!=null && pad1.getDuration()>0 && (pad1.isPlaying() || pad1Pause)) ||
                (pad2!=null && pad2.getDuration()>0 && (pad2.isPlaying() || pad2Pause)));
    }
    public void autoStartPad() {
        if (mainActivityInterface.getPreferences().getMyPreferenceBoolean("padAutoStart",false) && padsActivated) {
            startPad();
        }
    }
    private void playStopOrPause(int padNum) {
        switch (padNum) {
            case 1:
                if (pad1!=null && pad1.isPlaying() && pad1Fading) {
                    // Just stop the pad
                    stopAndReset(1);
                    padsActivated = false;
                    pad1Pause = false;
                } else if (pad1!=null && pad1.isPlaying() && !pad1Fading) {
                    // Pause the pad
                    pad1.pause();
                    pad1Pause = true;
                    padPauseTime = padTime.getText();
                } else if (pad1!=null && !pad1Fading) {
                    pad1.start();
                    pad1Pause = false;
                }
                break;
            case 2:
                pad2Pause = false;
                if (pad2!=null && pad2.isPlaying() && pad2Fading) {
                    // Just stop the pad
                    stopAndReset(2);
                    padsActivated = false;
                    pad1Pause = false;
                } else if (pad2!=null && pad2.isPlaying() && !pad2Fading) {
                    // Pause the pad
                    pad2.pause();
                    pad2Pause = true;
                    padPauseTime = padTime.getText();
                } else if (pad2!=null && !pad2Fading) {
                    pad2.start();
                    pad1Pause = false;
                }
                break;
        }
    }

    public void panicStop() {
        // Emergency stop all pads - no fade
        stopAndReset(1);
        stopAndReset(2);
        stopPadPlayTimer();
        padsActivated = false;
    }

    // Orientation changes
    public void setOrientationChanged(boolean orientationChanged) {
        this.orientationChanged = orientationChanged;
    }
    public boolean getOrientationChanged() {
        return orientationChanged;
    }
    public void setCurrentOrientation(int currentOrientation) {
        this.currentOrientation = currentOrientation;
    }
    public int getCurrentOrientation() {
        return currentOrientation;
    }

    private boolean longClick(int padNum) {
        stopAndReset(padNum);
        stopPadPlayTimer();
        return true;
    }

    public void stopPadPlayTimer() {
        if (padPlayTimerTask != null) {
            padPlayTimerTask.cancel();
            padPlayTimerTask = null;
        }
        if (padPlayTimer != null) {
            padPlayTimer.cancel();
            padPlayTimer.purge();
        }
        padTime.setText("");
        padTotalTime.setText("");
        pad.setVisibility(View.GONE);
    }
    
    private void stopPadPlay() {
        // IV - Configure padPlayTimer for stopping
        padTime.setText("");
        padTotalTime.setVisibility(View.GONE);
        padTotalTime.setText("Stopping");
    }
}