package com.garethevans.church.opensongtablet.controls;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

import java.util.ArrayList;

public class PedalActions {

    // Actions triggered here are sent to the PerformanceGestures to be acted upon there (one place!)

    // IV - code supporting intentional page turns when using pedal for next/previous.
    // IV - 'Are you sure?' is displayed and the user must stop, wait and can repeat the action to continue after 2 seconds (an intentional action)
    // IV - After continue there is a 10s grace period where further pedal use is not tested.  Any pedal 'page' or 'scroll' use extends a further 10s grace period.

    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "PedalActions";
    private final Context c;
    private final MainActivityInterface mainActivityInterface;
    private ArrayList<String> actions, actionCodes;
    private PedalsFragment pedalsFragment;

    // 8 buttons, but ignoring item 0
    private final int[] pedalCode = new int[9];
    private final String[] pedalMidi = new String[9];
    private final String[] pedalShortPressAction = new String[9];
    private final String[] pedalLongPressAction = new String[9];
    public Boolean[] pedalDown = new Boolean[9];
    public Long[] pedalDownTime = new Long[9];
    public Boolean[] pedalWasLongPressed = new Boolean[9];

    public final int[] defPedalCodes = new int[]{-1,21,22,19,20,92,93,-1,-1};
    public final String[] defPedalMidis = new String[]{"","C3","D3","E3","F3","G3","A3","B3","C4"};
    public final String[] defShortActions = new String[]{"","prev","next","up","down","","","",""};
    public final String[] defLongActions  = new String[] {"", "songmenu", "set", "", "", "", "", "", ""};
    private int airTurnLongPressTime, repeatModeCount, repeatModeTime, currentRepeatCount = 0,
            testPedalKeycode = -1;
    private boolean beatBuddyDownRegistered = false, airTurnMode, repeatMode, midiAsPedal,
            pedalScrollBeforeMove, pedalShowWarningBeforeMove, warningActive, warningGracePeriod,
            pedalIgnorePrevNext, testing = false, testPedalDown, testPedalWasLongPressed,
            handlerChecking, blockLongPress, actionUpTriggered = false, checkRepeatShortPressUp=true;

    public void setTesting(PedalsFragment pedalsFragment, boolean testing) {
        this.testing = testing;
        this.pedalsFragment = pedalsFragment;
    }
    private long testPedalDownTime = 0;
    private String testPedalMidi = "", testDesiredAction = "";
    private final Handler repeatHandlerCheck= new Handler(Looper.getMainLooper()), blockLongPressHandler = new Handler(Looper.getMainLooper()),
            warningWaitHandler = new Handler(Looper.getMainLooper()), warningGracePeriodHandler = new Handler(Looper.getMainLooper()),
            checkRepeatShortPressUpHandler = new Handler(Looper.getMainLooper());
    private final Runnable repeatRunnableCheck = () -> {
        if (!testing && currentRepeatCount >= repeatModeCount) {
            handlerChecking = true;
            doRepeatDetectionUp(testPedalKeycode, testPedalMidi, testDesiredAction);
            handlerChecking = false;
        }
    };
    private final Runnable blockLongPressRunnable = () -> blockLongPress = false;
    private final Runnable warningWaitRunnable = () -> pedalIgnorePrevNext = false;
    private final Runnable warningGracePeriodRunnable = () -> {
        warningGracePeriod = false;
        warningActive = false;
    };
    private final Runnable checkRepeatShortPressUpRunnable = () -> {
        checkRepeatShortPressUp = false;
        doRepeatDetectionUp(testPedalKeycode,testPedalMidi,testDesiredAction);

    };

    @SuppressWarnings("FieldCanBeLocal")
    private final int warningWaitTime = 2000, warningGraceTime = 10000;

    public PedalActions(Context c) {
        this.c = c;
        mainActivityInterface = (MainActivityInterface) c;
        setUpPedalActions();
    }

    public void setUpPedalActions() {
        setActions();
        setPrefs();
    }

    public ArrayList<String> getActions() {
        return actions;
    }
    public ArrayList<String> getActionCodes() {
        return actionCodes;
    }

    private void setActions() {
        actions = new ArrayList<>();
        actionCodes = new ArrayList<>();

        String startstop = " (" + c.getString(R.string.start) + " / " + c.getString(R.string.stop) + ")";
        String showhide = " (" + c.getString(R.string.show) + " / " + c.getString(R.string.hide) + ")";
        String onoff = " (" + c.getString(R.string.on)+" / "+c.getString(R.string.off) + ")";
        String settings = " " + c.getString(R.string.settings);
        String autoscroll = c.getString(R.string.autoscroll) + startstop;
        String pad = c.getString(R.string.pad) + startstop;
        String metronome = c.getString(R.string.metronome) + startstop;

        addString("","");

        // Set actions
        addString("set",c.getString(R.string.set_current) + showhide);
        addString("inlineset",c.getString(R.string.set_inline) + showhide);
        addString("inlinesetsettings",c.getString(R.string.set_inline)+ settings);
        addString("addtoset",c.getString(R.string.add_song_to_set));
        addString("addtosetvariation",c.getString(R.string.variation_make));
        addString("exportset",c.getString(R.string.export_current_set));

        addString("","");

        // Song actions
        addString("pad",pad);
        addString("padsettings",c.getString(R.string.pad)+settings);
        addString("metronome",metronome);
        addString("metronomesettings",c.getString(R.string.metronome)+settings);
        addString("autoscroll",autoscroll);
        addString("autoscrollsettings",c.getString(R.string.autoscroll)+settings);
        addString("inc_autoscroll_speed",c.getString(R.string.inc_autoscroll_speed));
        addString("dec_autoscroll_speed",c.getString(R.string.dec_autoscroll_speed));
        addString("toggle_autoscroll_pause",c.getString(R.string.autoscroll_pause));
        addString("pad_autoscroll",pad + " & " + c.getString(R.string.autoscroll));
        addString("pad_metronome",pad + " & " + c.getString(R.string.metronome));
        addString("autoscroll_metronome",autoscroll + " & " + c.getString(R.string.metronome));
        addString("pad_autoscroll_metronome",pad + " & " + c.getString(R.string.autoscroll) + " & " + c.getString(R.string.metronome));
        addString("editsong",c.getString(R.string.edit));
        addString("share_song",c.getString(R.string.export)+" "+c.getString(R.string.song));
        addString("importoptions",c.getString(R.string.import_main));
        addString("importonline",c.getString(R.string.import_basic)+" "+c.getString(R.string.online_services));
        addString("refreshsong",c.getString(R.string.refresh_song));
        addString("","");

        // Song navigation
        addString("songmenu",c.getString(R.string.show_songs) + showhide);
        addString("scrolldown",c.getString(R.string.scroll_down));
        addString("scrollup",c.getString(R.string.scroll_up));
        addString("next",c.getString(R.string.next));
        addString("prev",c.getString(R.string.previous));
        addString("randomsong",c.getString(R.string.random_song));

        addString("","");

        // Chords
        addString("transpose",c.getString(R.string.transpose));
        addString("transposesettings",c.getString(R.string.chord_settings));
        addString("chordfingerings",c.getString(R.string.chord_fingering)+showhide);
        addString("customchords",c.getString(R.string.custom_chords));
        addString("chordsettings",c.getString(R.string.chord_settings));

        addString("","");

        // Song information
        addString("link",c.getString(R.string.link));
        addString("stickynotes",c.getString(R.string.song_notes)+showhide);
        addString("stickynotessettings",c.getString(R.string.song_notes_edit));
        addString("highlight",c.getString(R.string.highlight)+showhide);
        addString("highlightedit",c.getString(R.string.highlight_info));
        addString("abc",c.getString(R.string.music_score));
        addString("abcedit",c.getString(R.string.music_score_info));

        addString("","");

        // Display
        addString("profiles",c.getString(R.string.profile));
        addString("showchords",c.getString(R.string.show_chords));
        addString("showcapo",c.getString(R.string.show_capo));
        addString("showlyrics",c.getString(R.string.show_lyrics));
        addString("theme",c.getString(R.string.theme_choose));
        addString("togglescale",c.getString(R.string.scale_auto));
        addString("autoscalesettings",c.getString(R.string.scaling_info));
        addString("pdfpage",c.getString(R.string.select_page));
        addString("invertpdf",c.getString(R.string.invert_PDF));
        addString("fonts",c.getString(R.string.font_choose));
        addString("showlogo",c.getString(R.string.show_logo) + " (" + c.getString(R.string.connected_display) + ")");

        addString("","");

        // Controls
        addString("nearby",c.getString(R.string.connections_discover));
        addString("nearbysettings",c.getString(R.string.connections_connect)+settings);
        addString("gestures",c.getString(R.string.custom_gestures));
        addString("pedals",c.getString(R.string.pedal)+settings);
        addString("midi",c.getString(R.string.midi_send));
        addString("midiboard",c.getString(R.string.midi_board));
        addString("midisettings",c.getString(R.string.midi)+settings);
        addString("midisend",c.getString(R.string.midi_auto)+onoff);
        addString("beatbuddy",c.getString(R.string.beat_buddy)+settings);
        addString("beatbuddystart",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.start));
        addString("beatbuddystop",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.stop));
        addString("beatbuddypause",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.pause));
        addString("beatbuddyaccent",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.accent));
        addString("beatbuddyfill",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.fill));
        addString("beatbuddytrans1",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.transition)+" 1");
        addString("beatbuddytrans2",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.transition)+" 2");
        addString("beatbuddytrans3",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.transition)+" 3");
        addString("beatbuddytransnext",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.transition)+" "+c.getString(R.string.next));
        addString("beatbuddytransprev",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.transition)+" "+c.getString(R.string.previous));
        addString("beatbuddytransexit",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.transition)+" "+c.getString(R.string.exit));
        addString("beatbuddyxtrans1",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.exclusive)+" "+c.getString(R.string.transition)+" 1");
        addString("beatbuddyxtrans2",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.exclusive)+" "+c.getString(R.string.transition)+" 2");
        addString("beatbuddyxtrans3",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.exclusive)+" "+c.getString(R.string.transition)+" 3");
        addString("beatbuddyxtransnext",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.exclusive)+" "+c.getString(R.string.transition)+" "+c.getString(R.string.next));
        addString("beatbuddyxtransprev",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.exclusive)+" "+c.getString(R.string.transition)+" "+c.getString(R.string.previous));
        addString("beatbuddyxtransexit",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.exclusive)+" "+c.getString(R.string.transition)+" "+c.getString(R.string.exit));
        addString("beatbuddyhalf",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.half_time));
        addString("beatbuddyhalfexit",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.half_time)+" "+c.getString(R.string.exit));
        addString("beatbuddydouble",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.double_time));
        addString("beatbuddydoubleexit",c.getString(R.string.beat_buddy)+" "+c.getString(R.string.double_time)+" "+c.getString(R.string.exit));
        addString("beatbuddyvolup",c.getString(R.string.beat_buddy)+ " " + c.getString(R.string.volume) + " +");
        addString("beatbuddyvoldown",c.getString(R.string.beat_buddy)+ " " + c.getString(R.string.volume) + " -");
        addString("beatbuddyvolhpup",c.getString(R.string.beat_buddy)+ " " + c.getString(R.string.volume_headphone) + " +");
        addString("beatbuddyvolhpdown",c.getString(R.string.beat_buddy)+ " " + c.getString(R.string.volume_headphone) + " -");
        addString("midiaction1",c.getString(R.string.midi_action)+" "+1);
        addString("midiaction2",c.getString(R.string.midi_action)+" "+2);
        addString("midiaction3",c.getString(R.string.midi_action)+" "+3);
        addString("midiaction4",c.getString(R.string.midi_action)+" "+4);
        addString("midiaction5",c.getString(R.string.midi_action)+" "+5);
        addString("midiaction6",c.getString(R.string.midi_action)+" "+6);
        addString("midiaction7",c.getString(R.string.midi_action)+" "+7);
        addString("midiaction8",c.getString(R.string.midi_action)+" "+8);
        addString("sysexstart",c.getString(R.string.midi_sysex)+" "+c.getString(R.string.start));
        addString("sysexstop", c.getString(R.string.midi_sysex)+" "+c.getString(R.string.stop));

        addString("","");

        // Nearby messages
        addString("nearbymessage1",c.getString(R.string.nearby_message)+" "+1);
        addString("nearbymessage2",c.getString(R.string.nearby_message)+" "+2);
        addString("nearbymessage3",c.getString(R.string.nearby_message)+" "+3);
        addString("nearbymessage4",c.getString(R.string.nearby_message)+" "+4);
        addString("nearbymessage5",c.getString(R.string.nearby_message)+" "+5);
        addString("nearbymessage6",c.getString(R.string.nearby_message)+" "+6);
        addString("nearbymessage7",c.getString(R.string.nearby_message)+" "+7);
        addString("nearbymessage8",c.getString(R.string.nearby_message)+" "+8);

        addString("","");

        // Utilities
        addString("soundlevel",c.getString(R.string.sound_level_meter));
        addString("tuner",c.getString(R.string.tuner));
        addString("bible",c.getString(R.string.bible_verse));

        addString("","");

        // Exit
        addString("exit",c.getString(R.string.exit) + " " + c.getString(R.string.app_name));
    }
    private void addString(String id, String val) {
        actionCodes.add(id);
        actions.add(val);
    }
    public void setPrefs() {
        for (int w=1; w<=8; w++) {
            pedalCode[w] = mainActivityInterface.getPreferences().getMyPreferenceInt("pedal"+w+"Code", defPedalCodes[w]);
            pedalMidi[w] = mainActivityInterface.getPreferences().getMyPreferenceString("pedal"+w+"Midi",defPedalMidis[w]);
            pedalShortPressAction[w] = mainActivityInterface.getPreferences().getMyPreferenceString("pedal"+w+"ShortPressAction",defShortActions[w]);
            pedalLongPressAction[w] = mainActivityInterface.getPreferences().getMyPreferenceString("pedal"+w+"LongPressAction",defLongActions[w]);
        }
        airTurnMode = mainActivityInterface.getPreferences().getMyPreferenceBoolean("airTurnMode", false);
        repeatMode = mainActivityInterface.getPreferences().getMyPreferenceBoolean("repeatMode",false);
        repeatModeCount = mainActivityInterface.getPreferences().getMyPreferenceInt("repeatModeCount",5);
        repeatModeTime = mainActivityInterface.getPreferences().getMyPreferenceInt("repeatModeTime",1000);
        airTurnLongPressTime = mainActivityInterface.getPreferences().getMyPreferenceInt("airTurnLongPressTime", 1000);
        pedalScrollBeforeMove = mainActivityInterface.getPreferences().getMyPreferenceBoolean("pedalScrollBeforeMove",true);
        pedalShowWarningBeforeMove = mainActivityInterface.getPreferences().getMyPreferenceBoolean("pedalShowWarningBeforeMove",false);
        midiAsPedal = mainActivityInterface.getPreferences().getMyPreferenceBoolean("midiAsPedal", false);
    }

    public void commonEventDown(int keyCode, String keyMidi) {
        // We only check commands if this is a BeatBuddy transistion or we are in AirTurnMode
        int pedal = getButtonNumber(keyCode, keyMidi);
        String desiredAction = getDesiredAction(true, pedal);
        if (desiredAction!=null && !beatBuddyDownRegistered && (desiredAction.startsWith("beatbuddytrans") ||
                desiredAction.startsWith("beatbuddyxtrans")) &&
                !desiredAction.contains("exit")) {
            // Send this command now
            beatBuddyDownRegistered = true;
            whichEventTriggered(true,keyCode,keyMidi);
        } else if (!airTurnMode && !repeatMode && desiredAction!=null && desiredAction.startsWith("midiaction")) {
            // Midi action so do it now
            actionUpTriggered = true;
            whichEventTriggered(true,keyCode,keyMidi);
        } else if (airTurnMode && (keyMidi==null || keyMidi.isEmpty())) {
            // Using AirTurnMode for keyboard pedal, deal with this separately, otherwise, do nothing
            doAirTurnDetectionDown(keyCode,keyMidi,desiredAction);
        }
        // repeatMode ignores action down as it uses action up
    }

    public void commonEventUp(int keyCode, String keyMidi) {
        //Log.d(TAG,"commonEventUp:"+keyCode);
        //Log.d(TAG,"keyMidi:"+keyMidi+"  repeatMode:"+repeatMode);
        // If we already triggered the action with key down, skip
        if (actionUpTriggered) {
            actionUpTriggered = false;
        } else {
            // Using AirTurnMode or repeatMod for keyboard pedal
            // Deal with this separately, otherwise, send the action
            int pedal;
            String desiredAction ="";
            if (!testing) {
                pedal = getButtonNumber(keyCode, keyMidi);
                desiredAction = getDesiredAction(true, pedal);
            }

            Log.d(TAG,"testing:"+testing);
            Log.d(TAG,"desiredAction:"+desiredAction);
            if (testing && keyCode!=0) {
                // We don't want to action anything other than test for repeatMode for long press
                doRepeatDetectionUp(keyCode,"","");

            } else if (desiredAction != null && (desiredAction.startsWith("beatbuddytrans") ||
                    desiredAction.startsWith("beatbuddyxtrans")) &&
                    !desiredAction.contains("exit")) {
                // Send this command now
                beatBuddyDownRegistered = false;
                whichEventTriggered(false, keyCode, keyMidi);
            } else if (airTurnMode && (keyMidi == null || keyMidi.isEmpty())) {
                doAirTurnDetectionUp(keyCode);
            } else if (repeatMode && (keyMidi == null || keyMidi.isEmpty())) {
                Log.d(TAG, "doRepeatDetectionUp repeatMode:"+ true);
                doRepeatDetectionUp(keyCode, keyMidi, desiredAction);
            } else {
                whichEventTriggered(true, keyCode, keyMidi);
            }
        }
    }

    public void commonEventLong(int keyCode, String keyMidi) {
        if (!beatBuddyDownRegistered) {
            whichEventTriggered(false, keyCode, keyMidi);
        }
    }

    public void whichEventTriggered(boolean shortpress, int keyCode, String keyMidi) {
        int pedal = getButtonNumber(keyCode, keyMidi);
        String desiredAction = getDesiredAction(shortpress, pedal);
        if (desiredAction == null) {
            desiredAction = "";
        }

        Log.d(TAG,"whichEventTriggered()");
        // IV - code supporting intentional page turns when using pedal for next/previous.
        // IV - 'Are you sure?' is displayed and the user must stop, wait and can repeat the action to continue after 2 seconds (an intentional action)
        // IV - After continue there is a 10s grace period where further pedal use is not tested.  Any pedal 'page' or 'scroll' use extends a further 10s grace period.
        // IV - Handlers for confirmation of page change when using pedal
        // Decide if we are allowed to move, or we are in a warning before move phase due to pedalShowWarningBeforeMove
        // If we can scroll, no need to warn yet

        if (pedalShowWarningBeforeMove &&
                ((desiredAction.equals("prev") && !mainActivityInterface.getPerformanceGestures().canScroll(false))
                || (desiredAction.equals("next")) && !mainActivityInterface.getPerformanceGestures().canScroll(true))) {
            if (!warningActive && !warningGracePeriod) {
                // Set the warning to active and display it
                warningActive = true;
                mainActivityInterface.getShowToast().doIt(c.getString(R.string.pedal_warning));

                // Set the system to ignore previous and next for the next 2 seconds (warning time)
                pedalIgnorePrevNext = true;
                warningWaitHandler.removeCallbacks(warningWaitRunnable);
                warningWaitHandler.postDelayed(warningWaitRunnable, warningWaitTime);

            } else if (pedalIgnorePrevNext) {
                // The user has clicked again within the warning time, so reset the warning time
                warningWaitHandler.removeCallbacks(warningWaitRunnable);
                warningWaitHandler.postDelayed(warningWaitRunnable, warningWaitTime);

            } else {
                // The warning time is over and we can allow moving to prev/next
                // Set the gracePeriod of 10 seconds to allow moving without warnings
                warningGracePeriod = true;
                try {
                    warningGracePeriodHandler.removeCallbacks(warningGracePeriodRunnable);
                    warningGracePeriodHandler.postDelayed(warningGracePeriodRunnable, warningGraceTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If we are moving up/down, we can cancel any pedal warnings
        if (pedalShowWarningBeforeMove && (desiredAction.equals("up") || desiredAction.equals("down"))) {
            warningActive = false;
            pedalIgnorePrevNext = false;
            try {
                warningWaitHandler.removeCallbacks(warningWaitRunnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // Convert extra actions based on if song menu is open/closed
        // Also check for warning
        switch (desiredAction) {
            case "prev":
            case "previous":
                desiredAction = "prev";
                // If the menu is open, scroll up
                if (mainActivityInterface.getMenuOpen()) {
                    desiredAction = "scrollmenuup";
                } else if (!pedalIgnorePrevNext) {
                    warningActive = false;
                } else {
                    desiredAction = "";
                }
                break;
            case "next":
                // If the menu is open, scroll down
                if (mainActivityInterface.getMenuOpen()) {
                    desiredAction = "scrollmenudown";
                } else if (!pedalIgnorePrevNext) {
                    warningActive = false;
                } else {
                    desiredAction = "";
                }
                break;
            case "down":
                // If the menu is open, scroll down
                if (mainActivityInterface.getMenuOpen()) {
                    desiredAction = "scrollmenudown";
                }
                break;
            case "up":
                // If the menu is open, scroll up
                if (mainActivityInterface.getMenuOpen()) {
                    desiredAction = "scrollmenuup";
                }
                break;
        }

        // Now deal with the desired action.  isLongPress is for page buttons (not to do with pedal)
        mainActivityInterface.getPerformanceGestures().doAction(desiredAction,false);
    }

    public int getButtonNumber(int keyCode, String keyMidi) {
        int pedal = 0;
        if (keyMidi != null) {
            for (int w = 1; w <= 8; w++) {
                if (pedalMidi[w].equals(keyMidi)) {
                    pedal = w;
                }
            }
        } else {
            for (int w = 1; w <= 8; w++) {
                if (pedalCode[w]==keyCode) {
                    pedal = w;
                }
            }
        }
        return pedal;
    }
    private String getDesiredAction(boolean shortpress, int pedal) {
        if (shortpress) {
            return pedalShortPressAction[pedal];
        } else {
            return pedalLongPressAction[pedal];
        }
    }

    // Getters and setters
    public boolean getPedalScrollBeforeMove() {
        return pedalScrollBeforeMove;
    }
    public boolean getPedalShowWarningBeforeMove() {
        return pedalShowWarningBeforeMove;
    }

    public int getPedalCode(int which) {
        return pedalCode[which];
    }
    public String getMidiCode(int which) {
        return pedalMidi[which];
    }
    public boolean getMidiAsPedal() {
        return midiAsPedal;
    }
    public String getPedalShortPressAction(int which) {
        return pedalShortPressAction[which];
    }
    public String getPedalLongPressAction(int which) {
        return pedalLongPressAction[which];
    }

    public void setPreferences(String which, boolean bool) {
        switch (which) {
            case "pedalScrollBeforeMove":
                this.pedalScrollBeforeMove = bool;
                break;
            case "pedalShowWarningBeforeMove":
                this.pedalShowWarningBeforeMove = bool;
                break;
            case "airTurnMode":
                this.airTurnMode = bool;
                break;
            case "repeatMode":
                this.repeatMode = bool;
                break;
            case "midiAsPedal":
                this.midiAsPedal = bool;
                break;
        }
        // Save the preference
        mainActivityInterface.getPreferences().setMyPreferenceBoolean(which, bool);
    }
    public void setPreferences(String which, int val) {
        switch (which) {
            case "airTurnLongPressTime":
                airTurnLongPressTime = val;
                break;
            case "repeatModeTime":
                repeatModeTime = val;
                break;
            case "repeatModeCount":
                repeatModeCount = val;
                break;
        }
        // Save the preference
        mainActivityInterface.getPreferences().setMyPreferenceInt(which, val);
    }
    public void setPedalCode(int which, int newCode) {
        pedalCode[which] = newCode;
        mainActivityInterface.getPreferences().setMyPreferenceInt("pedal"+which+"Code",newCode);
    }
    public void setMidiCode(int which, String newCode) {
        pedalMidi[which] = newCode;
        mainActivityInterface.getPreferences().setMyPreferenceString("pedal"+which+"Midi",newCode);
    }
    public void setPedalPreference(int which, boolean shortPress, String action) {
        String pref = "pedal"+which;
        if (shortPress) {
            pedalShortPressAction[which] = action;
            pref = pref + "ShortPressAction";
        } else {
            pedalLongPressAction[which] = action;
            pref = pref + "LongPressAction";
        }
        // Save the preference
        mainActivityInterface.getPreferences().setMyPreferenceString(pref, action);
    }
    public void setMidiAsPedal(boolean midiAsPedal) {
        this.midiAsPedal = midiAsPedal;
    }
    private int getPedalFromKeyCode(int keyCode) {
        // Go through the pedal codes and return the matching event
        int pedal = 0;
        for (int ped=1; ped<pedalCode.length; ped++) {
            if (pedalCode[ped] == keyCode) {
                pedal = ped;
                break;
            }
        }
        return pedal;
    }



    /* AirTurn pedals send repeated key down information a time apart (keyrepeattime)
    For this mode, we only act on keyUp for short presses
    To differentiate between long and short presses, we need to do the following:
        onKeyDown > Set a boolean that the key is down
                    Get a note of the system time for this pedal down
                    Because this can be sent multiple times, do nothing if key is already down
                    Check the time to see if longPress time has elapsed
                    If it has, record as a longPress action and do the action

        onKeyUp   > if we weren't registered as a longPress action, then send the keyCode up
                    if we were registered as a longPress, reset the keyDown so we can listen again
     */
    private void doAirTurnDetectionDown(int keyCode, String keyMidi, String desiredAction) {
        // Figure out which pedal is being pressed
        int keyPedalNum = getPedalFromKeyCode(keyCode);
        boolean isKeyPedal = keyPedalNum > 0;

        // Only proceed if we know which pedal
        if (testing || isKeyPedal) {
            boolean pedalIsDown;
            boolean longTimeHasPassed;
            boolean notAlreadyLongPressed;

            if (testing) {
                if (!testPedalDown) {
                    // Set this pedal as being pressed for the first time
                    testPedalDown = true;
                    // Set the system time
                    testPedalDownTime = System.currentTimeMillis();
                }
                pedalIsDown = testPedalDown;
                longTimeHasPassed = testPedalDownTime!=0 && System.currentTimeMillis() > (testPedalDownTime+airTurnLongPressTime);
                notAlreadyLongPressed = testPedalWasLongPressed;

            } else {
                // Check the status of this pedal
                if (pedalDown[keyPedalNum] == null || !pedalDown[keyPedalNum]) {
                    // Set this pedal as being pressed for the first time
                    pedalDown[keyPedalNum] = true;
                    // Set the system time
                    pedalDownTime[keyPedalNum] = System.currentTimeMillis();
                }
                pedalIsDown = pedalDown[keyPedalNum] != null && pedalDown[keyPedalNum];
                longTimeHasPassed = pedalDownTime[keyPedalNum]!=null &&
                        pedalDownTime[keyPedalNum]!=0 &&
                        System.currentTimeMillis() > (pedalDownTime[keyPedalNum]+airTurnLongPressTime);
                notAlreadyLongPressed = pedalWasLongPressed[keyPedalNum] == null || !pedalWasLongPressed[keyPedalNum];
            }

            // Check if the pedal is down and longPress time has elapsed and isn't already registered
            if (pedalIsDown && longTimeHasPassed && notAlreadyLongPressed) {
                // Register this as a new long press.  This stops the ACTION_UP being run too
                if (!testing) {
                    pedalWasLongPressed[keyPedalNum] = true;
                }

                if (testing && pedalsFragment!=null) {
                    // Testing, so update the long press mode
                    testPedalWasLongPressed = false;
                    testPedalDown = false;
                    testPedalDownTime = 0;
                    pedalsFragment.setLongPressMode("airturn");
                } else if (!testing) {
                    // Do the long press action
                    commonEventLong(keyCode, null);
                }

            } else if (desiredAction!=null && desiredAction.startsWith("midiaction")) {
                actionUpTriggered = true;
                whichEventTriggered(true,keyCode,keyMidi);
            }
        }
    }
    private void doAirTurnDetectionUp(int keyCode) {
        // Figure out which pedal is being pressed
        int keyPedalNum = getPedalFromKeyCode(keyCode);
        boolean isKeyPedal = keyPedalNum > 0;

        // Only proceed if we know which pedal
        if (isKeyPedal) {
            if (pedalWasLongPressed[keyPedalNum] != null && pedalWasLongPressed[keyPedalNum]) {
                // This pedal was registered as a long press, do nothing other than reset it
                Log.d(TAG, "Long press happened already, do nothing");
            } else {
                // Not a long press, so action the shortPress
                whichEventTriggered(true, keyCode, null);
            }
            pedalDown[keyPedalNum] = false;
            pedalDownTime[keyPedalNum] = 0L; // This means not valid time;
            pedalWasLongPressed[keyPedalNum] = false;
        }
    }
    public boolean getAirTurnMode() {
        return airTurnMode;
    }
    public int getAirTurnLongPressTime() {
        return airTurnLongPressTime;
    }


    // repeatMode for long press
    // This mode has repeated key down and key up events.
    // To register a long press detection in this mode, the first key up time is noted
    // Each following key up time is counted for the user pref repeatModeTime (def:200ms)
    // After the repeatModeTime has elapsed if the count exceeds the repeatModeCount pref long press is triggered
    // If we don't meet this threshold, the short press is triggered.

    // TODO not working yet as no check on single tap

    private void doRepeatDetectionUp(int keyCode, String keyMidi, String desiredAction) {
        // Figure out which pedal is being pressed
        int keyPedalNum = getPedalFromKeyCode(keyCode);
        testPedalKeycode = keyPedalNum;

        //Log.d(TAG,"doRepeatDetectionUp");
        // Remove callbacks to any checks scheduled
        repeatHandlerCheck.removeCallbacks(repeatRunnableCheck);

        // Only proceed if we know which pedal or we are testing
        if (keyPedalNum > 0 || testing) {

            boolean pedalIsDown;
            boolean longTimeHasPassed;
            boolean notAlreadyLongPressed;

            // Check the status of this pedal
            if (testing) {
                if (!testPedalDown) {
                    // Set this pedal as being pressed for the first time
                    testPedalDown = true;
                    // Set the system time
                    testPedalDownTime = System.currentTimeMillis();
                }
                pedalIsDown = testPedalDown;
                longTimeHasPassed = testPedalDownTime!=0 &&
                        System.currentTimeMillis() > (testPedalDownTime+repeatModeTime);
                notAlreadyLongPressed = !testPedalWasLongPressed;

            } else {
                if (pedalDown[keyPedalNum] == null || !pedalDown[keyPedalNum]) {
                    // Set this pedal as being pressed for the first time
                    pedalDown[keyPedalNum] = true;
                    testPedalDown = true;
                    // Set the system time
                    pedalDownTime[keyPedalNum] = System.currentTimeMillis();
                }
                pedalIsDown = pedalDown[keyPedalNum] != null && pedalDown[keyPedalNum];

                // Set the test values to the actual values as we will use them on the delayed check
                testPedalDownTime = pedalDownTime[keyPedalNum];
                testPedalKeycode = keyCode;
                testDesiredAction = desiredAction;
                testPedalMidi = keyMidi;
                testPedalDown = pedalIsDown;

                longTimeHasPassed = pedalDownTime[keyPedalNum]!=null &&
                        pedalDownTime[keyPedalNum]!=0 &&
                        System.currentTimeMillis() > (pedalDownTime[keyPedalNum]+repeatModeTime);
                notAlreadyLongPressed = pedalWasLongPressed[keyPedalNum] == null || !pedalWasLongPressed[keyPedalNum];
            }

            //Log.d(TAG,"pedalIsDown:"+pedalIsDown+"  longTimeHasPassed:"+longTimeHasPassed+"  notAlreadyLongPressed:"+notAlreadyLongPressed+"  currentCount:"+currentRepeatCount);
            // Check if the pedal is down and longPress time has elapsed and isn't already registered
            if (pedalIsDown && longTimeHasPassed && notAlreadyLongPressed && currentRepeatCount >= repeatModeCount) {
                // The time has elapsed and we have enough keyups to trigger the long press
                // Reset the counter and clear any delayed handler checks
                currentRepeatCount = 0;

                //Log.d(TAG,"blockLongPress:"+blockLongPress);
                if (testing && pedalsFragment!=null) {
                    // Testing, so update the long press mode
                    testPedalWasLongPressed = false;
                    testPedalDown = false;
                    testPedalDownTime = 0;
                    pedalsFragment.setLongPressMode("repeat");

                } else if (!testing && !blockLongPress) {
                    pedalWasLongPressed[keyPedalNum] = false;
                    pedalDown[keyPedalNum] = false;
                    pedalDownTime[keyPedalNum] = 0L;

                    // Stop the long press being detected for a second
                    blockLongPress = true;
                    blockLongPressHandler.postDelayed(blockLongPressRunnable,1000);
                    // Do the long press action
                    commonEventLong(keyCode, null);
                    pedalWasLongPressed[keyPedalNum] = false;

                }

            } else if (pedalIsDown && longTimeHasPassed && notAlreadyLongPressed) {
                // We didn't receive enough key up commands in the timescale.  Reset the counter and do a short press
                // Reset the counters
                currentRepeatCount = 0;
                testPedalWasLongPressed = false;
                testPedalDown = false;
                testPedalDownTime = 0;
                checkRepeatShortPressUp = true;

                if (!testing) {
                    pedalDown[keyPedalNum] = false;
                    pedalDownTime[keyPedalNum] = 0L;
                    pedalWasLongPressed[keyPedalNum] = false;
                    whichEventTriggered(true, keyCode, null);
                }

            } else if (pedalIsDown && !longTimeHasPassed && (desiredAction==null || !desiredAction.startsWith("midiaction"))) {
                //Log.d(TAG,"checkRepeatShortPressUp:"+checkRepeatShortPressUp);
                if (checkRepeatShortPressUp) {
                    checkRepeatShortPressUpHandler.postDelayed(checkRepeatShortPressUpRunnable,500);

                } else if (!handlerChecking) {
                    // The time hasn't passed so just increase the counter
                    currentRepeatCount++;

                    // Add a runnable to check in a few ms
                    repeatHandlerCheck.postDelayed(repeatRunnableCheck, 500);

                }

            } else if (!testing && desiredAction!=null && desiredAction.startsWith("midiaction")) {
                actionUpTriggered = true;
                pedalDown[keyPedalNum] = false;
                pedalDownTime[keyPedalNum] = 0L;
                pedalWasLongPressed[keyPedalNum] = false;
                whichEventTriggered(true,keyCode,keyMidi);
            }
        }
    }
    public boolean getRepeatMode() {
        return repeatMode;
    }
    public int getRepeatModeTime() {
        return repeatModeTime;
    }
    public int getRepeatModeCount() {
        return repeatModeCount;
    }
}
