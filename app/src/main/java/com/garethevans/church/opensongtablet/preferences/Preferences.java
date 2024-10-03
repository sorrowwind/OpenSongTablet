package com.garethevans.church.opensongtablet.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences extends Activity {

    public Preferences(Context c) {
        this.c = c;
    }
    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "Preferences";

    // This is the way that preferences will be stored
    private SharedPreferences sharedPref;
    private final Context c;

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    // Get the saved preference values
    public String getMyPreferenceString(String prefname, String fallback) {
        // Return a string from saved preference
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            try {
                return sharedPref.getString(prefname, fallback);
            } catch (Exception e) {
                return fallback;
            }
        } else {
            return fallback;
        }
    }
    public int getMyPreferenceInt(String prefname, int fallback) {
        // Return an int from saved preference
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            try {
                return sharedPref.getInt(prefname, fallback);
            } catch (Exception e) {
                return fallback;
            }
        } else {
            return fallback;
        }
    }
    public long getMyPreferenceLong(String prefname, long fallback) {
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            try {
                return sharedPref.getLong(prefname, fallback);
            } catch (Exception e) {
                return fallback;
            }
        } else {
            return fallback;
        }
    }
    public float getMyPreferenceFloat(String prefname, float fallback) {
        // Return a float from saved preferences
        if (c!=null && prefname!=null) {
                sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            try {
                return sharedPref.getFloat(prefname, fallback);
            } catch (Exception e) {
                return fallback;
            }
        } else {
            return fallback;
        }
    }
    public boolean getMyPreferenceBoolean(String prefname, boolean fallback) {
        // Return a boolean from saved preference
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            try {
                return sharedPref.getBoolean(prefname, fallback);
            } catch (Exception e) {
                return fallback;
            }
        } else {
            return fallback;
        }
    }

    // Set the preference values
    public void setMyPreferenceString(String prefname, String value) {
        // Identify the preferences
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            sharedPref.edit().putString(prefname, value).apply();
        }
    }
    public void setMyPreferenceInt(String prefname, int value) {
        // Identify the preferences
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            sharedPref.edit().putInt(prefname, value).apply();
        }
    }
    public void setMyPreferenceLong(String prefname, long value) {
        // Identify the preferences
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            sharedPref.edit().putLong(prefname, value).apply();
        }
    }
    public void setMyPreferenceFloat(String prefname, float value) {
        // Identify the preferences
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            sharedPref.edit().putFloat(prefname, value).apply();
        }
    }
    public void setMyPreferenceBoolean(String prefname, boolean value) {
        // Identify the preferences
        if (c!=null && prefname!=null) {
            sharedPref = c.getSharedPreferences("CurrentPreferences", Context.MODE_PRIVATE);
            sharedPref.edit().putBoolean(prefname, value).apply();
        }
    }

    // The ints used in the app
    public int getFinalInt(String which) {
         int value = -1;
        switch (which) {
            case "REQUEST_FILE_CHOOSER":
                value = 5500;
                break;
            case "REQUEST_OSB_FILE":
                value = 5501;
                break;
            case "REQUEST_IOS_FILE":
                value = 5502;
                break;
            case "REQUEST_CAMERA_CODE":
                value = 5503;
                break;
        }
        return value;
    }





    // Below is an alphabetical list of all the user preferences stored in the app!
    //
    // Variable name                    Type        What
    // abcAuto                          boolean     Should abc notation automatically be shown? (def:false)
    // abcAutoTranspose                 boolean     Should the abc notation output be transposed to match the song key (def:true)
    // abcPopupWidth                    float       The percentage of the screen width for the abc score popup (def:0.95f)
    // abcTranspose                     int         The visual transposing of abc notation
    // abcXPosition                     int         The x position of the popup sticky note (def: -1 which means figure it out first)
    // abcYPosition                     int         The y position of the popup sticky note (def: -1 which means figure it out first)
    // abcZoom                          int         How much the staff width is reduced thus zooming in.  Calculated by width/zoom (def:2)
    // addSectionBox   *NOTYET*         boolean     Draw a box around sections when in Performance mode (def:false)
    // addSectionSpace                  boolean     Should a spacing line be added between sections to improve readability (def:true)
    // aerosChannel                     int         The MIDI channel for the Aeros loop pedal (def:1);
    // airTurnLongPressTime             int         If using airTurnMode, how long should an action_down be held for to simulate longPress (def:1000)
    // airTurnMode                      boolean     Should autorepeat onKeyUp (multiple from keyRepeatCount variable) be converted to longKeyPress actions for AirTurn pedals (def:false)
    // allowPinchToZoom                 boolean     Can the song be pinch zoomed in Performance mode? (def:true)
    // appTheme                         String      The theme to use (dark, light, custom1, custom2) (def:dark)
    // autoscrollAutoStart              boolean     Should autoscroll start on page load (needs to be started manually the first time) (def:false)
    // autoscrollDefaultSongLength      int         The default length of a song to use for autoscroll (def:180)
    // autoscrollDefaultSongPreDelay    int         The default length of the predelay to use with autoscroll (default:20)
    // autoscrollDefaultMaxPreDelay     int         The default max of the autoscroll predelay slider (default:30)
    // autoscrollLargeFontInfoBar       boolean     The text size of the floating autoscroll info bar (default:true = 20.0f.  false = 14.0f)
    // autoscrollPreDelayCountdown      boolean     Should the autoscroll pre-delay timer countdown rather than count up (def: false)
    // autoscrollUseDefaultTime         boolean     If not time has been set for the song, should we use the default when starting (def:true)
    // autoshowMusicScore               boolean     If ABC notation should automatically display (when available) on song load (def:false)
    // backgroundImage1                 String      The uri of the background image 1 for presentations (def: bg.png)
    // backgroundImage2                 String      The uri of the background image 2 for presentations (def: bg.png)
    // backgroundVideo1                 String      The uri of the background video 1 for presentations (def:"")
    // backgroundVideo2                 String      The uri of the background video 2 for presentations (def:"")
    // backgroundToUse                  String      Which background are we using (img1, img2, vid1, vid2, color) (def:img1)
    // backgroundColor                  int         Solid colour for presentation background (def: colorPrimary)
    // backgroundTypeToUse              String      Is the background an image or a video (def:image)
    // batteryDialOn                    boolean     Should the battery circle be shown in the action bar (def:true)
    // batteryDialThickness             int         The thickness of the battery dial in the action bar (def:4)
    // batteryTextOn                    boolean     Should the battery percentage text be shown in the action bar (def:true)
    // batteryTextSize                  float       The size of the battery text (def:9.0f)
    // beatBuddyAerosMode               boolean     Should we use Aeros compatibility mode (limit folders to 127) (def:true)
    // beatBuddyAutoLookup              boolean     Should the current song title be looked up in the database and send the MIDI song code automatically? (def:false)
    // beatBuddyChannel                 int         The MIDI channel for BeatBuddy commands (song messages and gestures) (def:1)
    // beatBuddyIncludeDrumKit          boolean     Should drum kit messages be saved with song (def:false)
    // beatBuddyIncludeSong             boolean     Should song messages be saved with song (def:false)
    // beatBuddyIncludeTempo            boolean     Should tempo messages be saved with song (def:false)
    // beatBuddyIncludeHPVolume         boolean     Should headphone volume messages be saved with song (def:false)
    // beatBuddyIncludeVolume           boolean     Should volume messages be saved with song (def:false)
    // beatBuddyUseImported             boolean     Should we be searching the imported BeatBuddy songs/drums (def:false)
    // beatBuddyVolume                  int         The volume for the BeatBuddy (def:100)
    // beqtBuddyHPVolume                int         The volume for the BeatBuddy heaphones (def:100)
    // bibleCurrentFile                 String      The last used local bible XML file (def:"")
    // blockShadow                      boolean     Should second screen text be displayed on block shadowed text boxes.  The color/alpha is set the same as presoShadowColor(def:false)
    // blockShadowAlpha                 float       The alpha of the blockShadow behind Stage/Presenter text (def:0.5f)
    // capoInfoAsNumerals               boolean     Should the capo info bar use Roman numerals (def:false)
    // capoLargeFontInfoBar             boolean     The text size of the floating capo info bar (def:true is 20.0f false is 14.0f)
    // castRotation                     float       The rotation of the cast display 0, 90, 180, 270.  (def:0.0f)
    // ccliAutomaticLogging             boolean     Should we automatically write to the ccli log (def:false)
    // ccliChurchName                   String      The name of the church for CCLI logging (def:"")
    // ccliLicence                      String      The CCLI licence number (def:"")
    // chordFormat                      int         My preferred chord format (def:1=normal, 2=Bb->B and B->H, 3=same as 2, but with is/es/as. 4=doremi, 5=nashvillenumber 6=nashvillenumeral) (def:1)
    // chordFormatAutoChange            boolean     When a song is loaded and use preferred is switched on, should the song try to auto change (not overwrite) (def:false);
    // chordFormatUsePreferred          boolean     When transposing, should we assume we are using preferred chord format (def:true)
    // chordInstrument                  String      The default instrument for showing chords (def:g)
    // chosenstorage                    String      The uri of the document tree (Storage Access Framework) (def:null)
    // clock24hFormat                   boolean     Should the clock be shown in 24hr format (def:true)
    // clockOn                          boolean     Should the clock be shown in the action bar (def:true)
    // clockSeconds                     boolean     Should the actionbar clock include seconds (def:false)
    // clockTextSize                    float       The size of the clock font (def:9.0f)
    // curlyBrackets                    boolean     Treat text inside curly brackets as performance comments and are hidden during presentation (def:true)
    // curlyBracketsDevice              boolean     Hide text inside curly brackets on performance/stage devices too
    // custom1_invertPDF                boolean     Should the PDF colours be inverted (def:true)
    // custom1_lyricsBackgroundColor    int         The color for the lyrics background in the custom1 theme
    // custom1_lyricsBridgeColor        int         The color for the background for the bridge in the custom1 theme
    // custom1_lyricsCapoColor          int         The color for the capo text in the custom1 theme
    // custom1_lyricsChordsColor        int         The color for the chords text in the custom1 theme
    // custom1_lyricsChorusColor        int         The color for the background for the chorus in the custom1 theme
    // custom1_lyricsCommentColor       int         The color for the background for the comment in the custom1 theme
    // custom1_lyricsCustomColor        int         The color for the background for the custom section in the custom1 theme
    // custom1_lyricsPreChorusColor     int         The color for the background for the prechorus in the custom1 theme
    // custom1_lyricsTagColor           int         The color for the background for the tag in the custom1 theme
    // custom1_lyricsTextColor          int         The color for the lyrics text in the custom1 theme
    // custom1_lyricsVerseColor         int         The color for the background for the verse in the custom1 theme,
    // custom1_presoFontColor           int         The color for the presentation text in the custom1 theme
    // custom2_presoChordColor          int         The color for the presentation chords in the custom1 theme
    // custom1_presoShadowColor         int         The color for the presentation text shadow in the custom1 theme
    // custom1_presoInfoColor           int         The color for the presentation info text in the custom1 theme
    // custom1_presoAlertColor          int         The color for the presentation alert text in the custom1 theme
    // custom1_presoCapoColor           int         The color for the presentation capo text in the custom1 theme
    // custom1_metronomeColor           int         The color for the metronome background in the custom1 theme
    // custom1_pageButtonsColor         int         The color for the page buttons info text in the custom1 theme
    // custom1_stickyTextColor          int         The color for the sticky note text info text in the custom1 theme
    // custom1_stickyBackgroundColor    int         The color for the sticky note background info text in the custom1 theme
    // custom1_extraInfoTextColor       int         The color for the extra info text in the custom1 theme
    // custom1_extraInfoBgColor         int         The color for the extra info background in the custom1 theme
    // custom1_highlightChordColor      int         The color to highlight chords (i.e. background). (def:tranparent/0x00000000)
    // custom1_highlightHeadingColor    int         The color to highlight headings (i.e. background). (def:tranparent/0x00000000)
    // custom1_hotZoneColor             int         The color to tint the hot zones (def: transparent/0x00000000);
    // custom2_invertPDF                boolean     Should the PDF colours be inverted (def:false)
    // custom2_lyricsBackgroundColor    int         The color for the lyrics background in the custom2 theme
    // custom2_lyricsBridgeColor        int         The color for the background for the bridge in the custom2 theme
    // custom2_lyricsCapoColor          int         The color for the capo text in the custom2 theme
    // custom2_lyricsChordsColor        int         The color for the chords text in the custom2 theme
    // custom2_lyricsChorusColor        int         The color for the background for the chorus in the custom2 theme
    // custom2_lyricsCommentColor       int         The color for the background for the comment in the custom2 theme
    // custom2_lyricsCustomColor        int         The color for the background for the custom section in the custom2 theme
    // custom2_lyricsPreChorusColor     int         The color for the background for the prechorus in the custom2 theme
    // custom2_lyricsTagColor           int         The color for the background for the tag in the custom2 theme
    // custom2_lyricsTextColor          int         The color for the lyrics text in the custom2 theme
    // custom2_lyricsVerseColor         int         The color for the background for the verse in the custom2 theme,
    // custom2_presoFontColor           int         The color for the presentation text in the custom2 theme
    // custom2_presoChordColor          int         The color for the presentation chords in the custom2 theme
    // custom2_presoShadowColor         int         The color for the presentation text shadow in the custom2 theme
    // custom2_presoInfoColor           int         The color for the presentation info text in the custom2 theme
    // custom2_presoAlertColor          int         The color for the presentation alert text in the custom2 theme
    // custom2_presoCapoColor           int         The color for the presentation capo text in the custom2 theme
    // custom2_metronomeColor           int         The color for the metronome background in the custom2 theme
    // custom2_pageButtonsColor         int         The color for the page buttons info text in the custom2 theme
    // custom2_stickyTextColor          int         The color for the sticky note text info text in the custom2 theme
    // custom2_stickyBackgroundColor    int         The color for the sticky note background info text in the custom2 theme
    // custom2_extraInfoTextColor       int         The color for the extra info text in the custom2 theme
    // custom2_extraInfoBgColor         int         The color for the extra info background in the custom2 theme
    // custom2_highlightChordColor      int         The color to highlight chords (i.e. background). (def:tranparent/0x00000000)
    // custom2_highlightHeadingColor    int         The color to highlight headings (i.e. background). (def:tranparent/0x00000000)
    // custom2_hotZoneColor             int         The color to tint the hot zones (def: transparent/0x00000000);
    // customLogo                       String      The uri of the user logo for presentations (def:"")
    // customLogoSize                   float       Size of the custom logo (% of screen) (def:0.5f)
    // customPadAb                      String      Custom pad uri for the key specified
    // customPadA                       String      Custom pad uri for the key specified
    // customPadBb                      String      Custom pad uri for the key specified
    // customPadB                       String      Custom pad uri for the key specified
    // customPadC                       String      Custom pad uri for the key specified
    // customPadDb                      String      Custom pad uri for the key specified
    // customPadD                       String      Custom pad uri for the key specified
    // customPadEb                      String      Custom pad uri for the key specified
    // customPadE                       String      Custom pad uri for the key specified
    // customPadF                       String      Custom pad uri for the key specified
    // customPadGb                      String      Custom pad uri for the key specified
    // customPadG                       String      Custom pad uri for the key specified
    // customPadAbm                     String      Custom pad uri for the key specified
    // customPadAm                      String      Custom pad uri for the key specified
    // customPadBbm                     String      Custom pad uri for the key specified
    // customPadBm                      String      Custom pad uri for the key specified
    // customPadCm                      String      Custom pad uri for the key specified
    // customPadDbm                     String      Custom pad uri for the key specified
    // customPadDm                      String      Custom pad uri for the key specified
    // customPadEbm                     String      Custom pad uri for the key specified
    // customPadEm                      String      Custom pad uri for the key specified
    // customPadFm                      String      Custom pad uri for the key specified
    // customPadGbm                     String      Custom pad uri for the key specified
    // customPadGm                      String      Custom pad uri for the key specified
    // dark_invertPDF                   boolean     Should the PDF colours be inverted (def:true)
    // dark_lyricsBackgroundColor       int         The color for the lyrics background in the dark theme
    // dark_lyricsBridgeColor           int         The color for the background for the bridge in the dark theme
    // dark_lyricsCapoColor             int         The color for the capo text in the dark theme
    // dark_lyricsChordsColor           int         The color for the chords text in the dark theme
    // dark_lyricsChorusColor           int         The color for the background for the chorus in the dark theme
    // dark_lyricsCommentColor          int         The color for the background for the comment in the dark theme
    // dark_lyricsCustomColor           int         The color for the background for the custom section in the dark theme
    // dark_lyricsPreChorusColor        int         The color for the background for the prechorus in the dark theme
    // dark_lyricsTagColor              int         The color for the background for the tag in the dark theme
    // dark_lyricsTextColor             int         The color for the lyrics text in the dark theme
    // dark_lyricsVerseColor            int         The color for the background for the verse in the dark theme,
    // dark_presoFontColor              int         The color for the presentation text in the dark theme
    // dark_presoChordColor             int         The color for the presentation chords in the dark theme
    // dark_presoShadowColor            int         The color for the presentation text shadow in the dark theme
    // dark_presoInfoColor              int         The color for the presentation info text in the dark theme
    // dark_presoAlertColor             int         The color for the presentation alert text in the dark theme
    // dark_presoCapoColor              int         The color for the presentation capo text in the dark theme
    // dark_metronomeColor              int         The color for the metronome background in the dark theme
    // dark_pageButtonsColor            int         The color for the page buttons info text in the dark theme
    // dark_stickyTextColor             int         The color for the sticky note text info text in the dark theme
    // dark_stickyBackgroundColor       int         The color for the sticky note background info text in the dark theme
    // dark_extraInfoTextColor          int         The color for the extra info text in the dark theme
    // dark_extraInfoBgColor            int         The color for the extra info background in the dark theme
    // dark_highlightChordColor         int         The color to highlight chords (i.e. background). (def:tranparent/0x00000000)
    // dark_highlightHeadingColor       int         The color to highlight headings (i.e. background). (def:tranparent/0x00000000)
    // dark_hotZoneColor                int         The color to tint the hot zones (def: transparent/0x00000000);
    // databaseLastUpdate               long        The timestamp the song folder was last indexed (def:-1)
    // defaultPresentationText          boolean     Should the 'Words and music by' and 'Used by permission' be included (def:true)
    // deviceId                         String      The device name for Nearby Connections (def:Bluetooth name/device manufacturer+model)
    // displayCapoChords                boolean     Should capo chords be shown (def:true)
    // displayCapoAndNativeChords       boolean     Should both chords be shown at once (def:false)
    // displayChords                    boolean     Decides if chords should be shown (def:true)
    // displayChordDiagrams             boolean     Should chords be shown as diagrams showing the fingering in the song window (def:false)
    // displayLyrics                    boolean     Decides if lyrics should be shown (def:true)
    // displayBoldChordsHeadings        boolean     Should the chords and headings be shown in a bold font (def:false)
    // displayBoldChorus    x            boolean     Should the chorus be displayed as bold (def:false)
    // download_wifi_only               boolean     Only allow download over WiFi (no mobile data) (def:true)
    // drawingAutoDisplay               boolean     Should the highlighter drawings be shown on page load (def:true)
    // drawingEraserSize                int         The default size of the eraser (def:20)
    // drawingHighlighterColor          int         The color of the highlighter (StaticVariables.highlighteryellow)
    // drawingHighlighterSize           int         The default size of the highlighter (def:20)
    // drawingPenColor                  int         The colour of the pen (def:StaticVariables.black)
    // drawingPenSize                   int         The default size of the pen (def:20)
    // drawingTool                      String      The current drawing tool (def:hghlighter)
    // editAsChordPro                   boolean     Should the song edit window be ChordPro format (def:false)
    // editTextSize                     float       The size of the song edit text font (8-24) (def: 14)
    // exportOpenSongAppSet             boolean     Should we export .osts file (def:false)
    // exportOpenSongApp                boolean     Should we export .ost file (def:false)
    // exportOpenSongSet                boolean     Should we export desktop xml file (def:true)
    // exportOpenSongTextSet            boolean     Should we export .txt file (def:false)
    // exportCurrentFormat              boolean     Should we export songs in their standard existing format: OpenSong, pdf, or image - set export option (def:true)
    // exportDesktop                    boolean     Should we export desktop xml file (def:true)
    // exportSetSongs                   boolean     Should we include the set songs with the set export (def:true)
    // exportText                       boolean     Should we export .txt file (def:false)
    // exportChordPro                   boolean     Should we export .chopro file (def:false)
    // exportOnSong                     boolean     Should we export .onsong file (def:false)
    // exportImage                      boolean     Should we export .png file (def:false)
    // exportMergedText                 boolean     Should we export merged .txt file for all songs (def:false);
    // exportPDF                        boolean     Should we export .pdf file (def:false)
    // exportPNG                        boolean     Should we export .png file (def:false)
    // exportSetPDF                     boolean     Should we export the set as .pdf file (def:false)
    // exportScreenshot                 boolean     Should we export the song as a screenshot (WYSIWYG) .png file (def:false)
    // filterSections                   boolean     Should we enable filter by section tags (def:false)
    // filterShow                       boolean     If filtering should only show current filters rather than hide (def:false)
    // filterText                       String      Filter text split by line breaks
    // fontSize                         float       The non-scaled font size (def:20.0f)
    // fontSizeMax                      float       The max font size (def:50.0f)
    // fontSizeMin                      float       The min font size (def:8.0f)
    // fontChord                        String      The name of the font used for the chords.  From fonts.google.com (def:lato)
    // fontLyric                        String      The name of the font used for the lyrics.  From fonts.google.com (def:lato)
    // fontPreso                        String      The name of the font used for the preso.  From fonts.google.com (def:lato)
    // fontPresoInfo                    String      The name of the font used for the presoinfo.  From fonts.google.com (def:lato)
    // fontSizePreso                    float       The non-scale presentation font size (def:14.0f)
    // fontSizePresoMax                 float       The maximum autoscaled font size (def:40.0f)
    // fontSticky                       String      The name of the font used for the sticky notes.  From fonts.google.com (def:lato)
    // forceColumns                     boolean     When full autoscale is on and a song has !-- page breaks, use these to force column breaks (def:true)
    // forcePDFSinglePage               boolean     When exporting PDF songs or printing, should the song be scaled to fit on a single page (def:false)
    // gestureDoubleTap                 String      The action for double tapping on the song screen (def:editsong)
    // gestureLongPress                 String      The action for long pressing on the song screen (def:addtoset)
    // gestureNavigation                boolean     Should the gesture bar be overlaid on top of the display (def:false)
    // graceTime                        boolean     Should the default 2 second grace time after song load be respected (def:true)
    // hardwareAcceleration             boolean     Use hardware acceleration for graphics normally on by default, but can cause glitches (def:true)
    // hideActionBar                    boolean     Should the action bar auto hide (def:false)
    // hideActionBarTime                int         How long should the action bar be visible for before hiding (def:1200)
    // hideInfoBar                      boolean     Should the info bar (Presenter mode only) autohide after initial display + minimum time (def: false)
    // hideLyricsBox                    boolean     Should we hide the box around the lyrics (def:false)
    // hotZoneBottomCenterShort         String      The action for short pressing on this hot zone (def:scrolldown)
    // hotZoneBottomCenterLong          String      The action for short pressing on this hot zone (def:"")
    // hotZoneTopCenterShort            String      The action for short pressing on this hot zone (def:scrollup)
    // hotZoneTopCenterLong             String      The action for short pressing on this hot zone (def:"")
    // hotZoneTopLeftShort              String      The action for short pressing on this hot zone (def:"")
    // hotZoneTopLeftLong               String      The action for short pressing on this hot zone (def:"")
    // ignorePlayServicesWarning        boolean     Has the user ticked the box to not show this again (def:false);
    // indexSkipAllowed                 boolean     Can we skip indexing (only allowed if it has been completed) (def:false)
    // inlineSet                        boolean     Should we show the inline setlist (def:true)
    // inlineSetPresenter               boolean     Should we show the inline setlist for Presenter mode (def:true)
    // inlineSetWidth                   float       The width (as a percentage of screen width) of the inline setlist (def:0.15f)
    // inlineSetWidthPresenter          float       The width (as a percentage of screen width) of the inline setlist in Presenter Mode (def:0.3f)
    // inlineSetTextSize                float       The text size for the inline set (def:14f)
    // inlineSetTextSizePresenter       float       The text size for the inline set for Presenter mode (def:14f)
    // intentAlreadyDealtWith           boolean     A note of if we have dealt with the intent to avoid multiple calls.  Reset to false on destroy (def: false)
    // language                         String      The locale set in the menu (def:en)
    // largePopups                      boolean     Should the drop down exposed popups be big (def:true)
    // lastUsedVersion                  int         The app version number the last time the app ran (def:0)
    // light_invertPDF                  boolean     Should the PDF colours be inverted (def:false)
    // light_lyricsBackgroundColor      int         The color for the lyrics background in the light theme
    // light_lyricsBridgeColor          int         The color for the background for the bridge in the light theme
    // light_lyricsCapoColor            int         The color for the capo text in the light theme
    // light_lyricsChordsColor          int         The color for the chords text in the light theme
    // light_lyricsChorusColor          int         The color for the background for the chorus in the light theme
    // light_lyricsCommentColor         int         The color for the background for the comment in the light theme
    // light_lyricsCustomColor          int         The color for the background for the custom section in the light theme
    // light_lyricsPreChorusColor       int         The color for the background for the prechorus in the light theme
    // light_lyricsTagColor             int         The color for the background for the tag in the light theme
    // light_lyricsTextColor            int         The color for the lyrics text in the light theme
    // light_lyricsVerseColor           int         The color for the background for the verse in the light theme,
    // light_presoFontColor             int         The color for the presentation text in the light theme
    // light_presoChordColor            int         The color for the presentation chords in the light theme
    // light_presoShadowColor           int         The color for the presentation text shadow in the light theme
    // light_presoInfoColor             int         The color for the presentation info text in the light theme
    // light_presoAlertColor            int         The color for the presentation alert text in the light theme
    // light_presoCapoColor             int         The color for the presentation capo text in the light theme
    // light_metronomeColor             int         The color for the metronome background in the light theme
    // light_pageButtonsColor           int         The color for the page buttons info text in the light theme
    // light_stickyTextColor            int         The color for the sticky note text info text in the light theme
    // light_stickyBackgroundColor      int         The color for the sticky note background info text in the light theme
    // light_extraInfoTextColor         int         The color for the extra info text in the light theme
    // light_extraInfoBgColor           int         The color for the extra info background in the light theme
    // light_highlightChordColor        int         The color to highlight chords (i.e. background). (def:tranparent/0x00000000)
    // light_highlightHeadingColor      int         The color to highlight headings (i.e. background). (def:tranparent/0x00000000)
    // light_hotZoneColor               int         The color to tint the hot zones (def: transparent/0x00000000);
    // lineSpacing                      float       The line spacing trim value to use (def:0.1f)
    // marginToolbarLeft                int         Any additional padding to the left of the actionbar content (def:0)
    // marginToolbarRight               int         Any additional padding to the right of the actionbar content (def:0)
    // maxPDFScaling                    float       An option to adjust the output size of PDFs from default size (values 0.4-1.5 def:0.75f)
    // menuSize                         int         The width of the side menus (min 100 max 400) (def:250)
    // metronomeAudio                   boolean     Should the metronome use audio playback of tick/tocks (def:true)
    // metronomeAutoStart               boolean     Should the metronome autostart with song (after manually starting first time) (def:false)
    // metronomeLength                  int         Number of bars the metronome stays on for (0=indefinitely) (def:0)
    // metronomeSyncWithBeatBuddy       boolean     Should the metronome start and stop sync with the BeatBuddy (def:false)
    // metronomePan                     String      The panning of the metronome sound L, C, R (def:C)
    // metronomeTickSound               String      The reference to the filename in the assets/metronome for the tick (def:digital_high)
    // metronomeTickVol                 float       The volume of the metronome tick (def:0.8f)
    // metronomeTockSound               String      The reference to the filename in the assets/metronome for the tick (def:digital_low)
    // metronomeTockVol                 float       The volume of the metronome tick (def:0.6f)
    // metronomeShowVisual              boolean     Should the metronome be visual (flash action bar) (def:false)
    // midiAsPedal                      boolean     Should the midi device trigger pedal commands (def:false)
    // midiAction1                      String      Midi message allocated to midiAction1 assignable to action/pedal (def:0x99 0x24 0x64) - bass drum
    // midiAction2                      String      Midi message allocated to midiAction2 assignable to action/pedal (def:0x99 0x26 0x64) - snare drum
    // midiAction3                      String      Midi message allocated to midiAction3 assignable to action/pedal (def:0x99 0x2A 0x64) - closed hat
    // midiAction4                      String      Midi message allocated to midiAction4 assignable to action/pedal (def:0x99 0x2E 0x64) - open hat
    // midiAction5                      String      Midi message allocated to midiAction5 assignable to action/pedal (def:0x99 0x30 0x64) - tom 1
    // midiAction6                      String      Midi message allocated to midiAction6 assignable to action/pedal (def:0x99 0x2F 0x64) - tom 2
    // midiAction7                      String      Midi message allocated to midiAction7 assignable to action/pedal (def:0x99 0x2B 0x64) - tom 3
    // midiAction8                      String      Midi message allocated to midiAction8 assignable to action/pedal (def:0x99 0x37 0x64) - crash
    // midiBoard                        int     Which MIDI board is selected (def:1)
    // midiBoard{1-3}Title              String      The title of MIDI board 1-3 (def:"")
    // midiBoard{1-3}Button{1-8}Name    String      The name of the MIDI buttons 1-8 on the boards 1-3 (def:"")
    // midiBoard{1-3}Button{1-8}MIDI    String      The MIDI code of the MIDI buttons 1-8 on the boards 1-3 (def:"")
    // midiBoard{1-3}Slider{1-2}Name    String      The name of the MIDI sliders 1-2 on the boards 1-3 (def:"")
    // midiBoard{1-3}Slider{1-2}Channel int         The MIDI channel of the MIDI sliders 1-2 on the boards 1-3 (def:0)
    // midiBoard{1-3}Slider{1-2}CC      int         The CC number of the MIDI sliders 1-2 on the boards 1-3 (def:0)
    // midiBoard{1-3}Slider{1-2}Value   int         The value of the MIDI sliders 1-2 on the boards 1-3 (def:0)
    // midiSendAuto                     boolean     Should the midi info in the song be sent on song load automatically (def:false)
    // multiLineVerseKeepCompact        boolean     Should multiline verses be kept compact (def:false)
    // navBarKeepSpace                  boolean     Should the app avoid writing in the bottom navbar space (def:false)
    // nearbyHostMenuOnly               boolean     Should the host only listen for clients when the nearby menu is open (def:false)
    // nearbyHostPassthrough            boolean     Allows a host to pass on messages received from other hosts rather than blocking (def:true);
    // nearbyKeepHostFiles              boolean     Keep files received from host (will overwrite your songs!) (def:false)
    // nearbyMatchToPDFSong             boolean     If the received song (same filename) exists as a pdf (filename.pdf), then load that rather than the xml song (def:false)
    // nearbyMessage1...8               String      Message that can be assigned to an action to send to connected clients (def:"")
    // nearbyMessageMIDIAction          boolean     Should the matching nearbyMessage{1-8} be sent to connected devices along with midiAction{1-8} (def:true)
    // nearbyMessageSticky              boolean     If a message is sent to connected (nearby) devices, should it use a sticky note (if not, it is an alert) (def:false)
    // nearbyPreferredHost              boolean     If we set ourselves as a host, this will be the next start on boot option (def:false)
    // nearbyReceiveHostAutoscroll      boolean     Should we listen (as a client) for the host starting/stopping autoscroll? (def:true)
    // nearbyReceiveHostFiles           boolean     If we are a client, do we want to receive a temporary copy of the host's version of the songs? (def:true)
    // nearbyReceiveHostScroll          boolean     If we are a client, do we listen for the host changing scroll position (def:true)
    // nearbyReceiveHostSongSections    boolean     If we are a client, do we listen for the host changing sections (def:true)
    // nearbyStartOnBoot                boolean     Should the nearby connection start on boot (using host if previously set, or default as client) (def:false)
    // nearbyStrategy                   String      Which strategy to use for Nearby connections: cluster, star, single (def:cluster)
    // nearbyTemporaryAdvertise         boolean     When acting as a Nearby host, should we only advertise for 10s (def:false)
    // nextInSet                        boolean     Should the next song in the set be shown (def:true)
    // onscreenAutoscrollHide           boolean     Performance/Stage autoscroll info text at the top - should it autohide after a delay (def:true)
    // onscreenCapoHide                 boolean     Performance/Stage capo info text at the top - should it autohide after a delay (def:true)
    // onscreenPadHide                  boolean     Performance/Stage pad info text at the top - should it autohide after a delay (def:true)
    // padAutoStart                     boolean     Should the pad autostart with song (after manually starting first time) (def:false)
    // padCrossFadeTime                 int         The time in ms used to fade out a pad.  Set in the PopUpCrossFade fragment (def:8000)
    // padLargeFontInfoBar              boolean     The text size of the floating pad info bar (def:true is 20.0f false is 14.0f)
    // padPan                           String      The panning of the pad (L, C or R) (def:C)
    // padVol                           float       The volume of the pad (def:1.0f)
    // pageButton1...                   String      The action for page button 1,2,3... (def for 1=inlineset, 2=transpose, the rest empty)
    // pageButtonShow1...               boolean     Should the button be visible. User can temporarily hide (or change the num)
    // pageButtonMini                   boolean     Should the page buttons be mini (def:false)
    // pdfSize                          String      The size for printing PDF (def:A4)
    // pdfTheme                         String      The theme to use when exporting PDF files or printing (def:default)
    // pedal1Code                       int         The keyboard int code assigned to pedal 1 (default is 21 - left arrow)
    // pedal1Midi                       String      The midi note assigned to pedal 1 (default:C3)
    // pedal1LongPressAction            String      The action called when pedal 1 is long pressed (default is songmenu)
    // pedal1ShortPressAction           String      The action called when pedal 1 is short pressed (default is prev)
    // pedal2Code                       int         The keyboard int code assigned to pedal 2 (default is 22 - right arrow)
    // pedal2Midi                       String      The midi note assigned to pedal 1 (default:C3)
    // pedal2LongPressAction            String      The action called when pedal 2 is long pressed (default is set)
    // pedal2ShortPressAction           String      The action called when pedal 2 is short pressed (default is next)
    // pedal3Code                       int         The keyboard int code assigned to pedal 3 (default is 19 - up arrow)
    // pedal3Midi                       String      The midi note assigned to pedal 1 (default:C3)
    // pedal3LongPressAction            String      The action called when pedal 3 is long pressed (default is "")
    // pedal3ShortPressAction           String      The action called when pedal 3 is short pressed (default is "")
    // pedal4Code                       int         The keyboard int code assigned to pedal 4 (default is 20 - down arrow)
    // pedal4Midi                       String      The midi note assigned to pedal 1 (default:C3)
    // pedal4LongPressAction            String      The action called when pedal 4 is long pressed (default is "")
    // pedal4ShortPressAction           String      The action called when pedal 4 is short pressed (default is "")
    // pedal5Code                       int         The keyboard int code assigned to pedal 5 (default is 92 - page up)
    // pedal5Midi                       String      The midi note assigned to pedal 1 (default:C3)
    // pedal5LongPressAction            String      The action called when pedal 5 is long pressed (default is "")
    // pedal5ShortPressAction           String      The action called when pedal 5 is short pressed (default is "")
    // pedal6Code                       int         The keyboard int code assigned to pedal 6 (default is 93 - page down)
    // pedal6Midi                       String      The midi note assigned to pedal 1 (default:C3)
    // pedal6LongPressAction            String      The action called when pedal 6 is long pressed (default is "")
    // pedal6ShortPressAction           String      The action called when pedal 6 is short pressed (default is "")
    // pedal7Code                       int         The keyboard int code assigned to pedal 7 (default is -1 = nothing)
    // pedal7Midi                       String      The midi note assigned to pedal 1 (default:C3)
    // pedal7LongPressAction            String      The action called when pedal 7 is long pressed (default is "")
    // pedal7ShortPressAction           String      The action called when pedal 7 is short pressed (default is "")
    // pedal8Code                       int         The keyboard int code assigned to pedal 8 (default is -1 = nothing)
    // pedal8Midi                       String      The midi note assigned to pedal 1 (default:C3)
    // pedal8LongPressAction            String      The action called when pedal 8 is long pressed (default is "")
    // pedal8ShortPressAction           String      The action called when pedal 8 is short pressed (default is "")
    // pedalScrollBeforeMove            boolean     Should the prev/next pedal buttons try to scroll first (makes 2 pedals into 4) (def:true)
    // pedalShowWarningBeforeMove       boolean     Should an 'are you sure' toast warning be shown before moving to next item in the set (def:false)
    // popupAlpha                       float       The opacity of the popup windows (def:0.8f)
    // popupDim                         float       The darkness of the main window when the popup is open (def:0.8f)
    // popupPosition                    String      The position of the popups (tl, tc, tr, l, c, r, bl, bc, br) (def:c)
    // popupScale                       float       The size of the popup relative to the page size (def:0.7f)
    // prefKey_Ab                       boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:true)
    // prefKey_Bb                       boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:true)
    // prefKey_Db                       boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:false)
    // prefKey_Eb                       boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:true)
    // prefKey_Gb                       boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:false)
    // prefKey_Abm                      boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:false)
    // prefKey_Bbm                      boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:true)
    // prefKey_Dbm                      boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:false)
    // prefKey_Ebm                      boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:true)
    // prefKey_Gbm                      boolean     Prefer the key using flats if true (otherwise prefer the alternative key using sharps) (def:false)
    // presoAlertText                   String      The text for the alert in Presentation mode (def:"")
    // presoAlertTextSize               float       The size of the alert text in Presentation mode (def:12.0f)
    // presoAuthorTextSize              float       The size of the author text in Presentation mode (def:12.0f)
    // presoAutoScale                   boolean     Should the presenter window use autoscale for text (def:true)
    // presoAutoUpdateProjector         boolean     Should the projector be updated automatically in PresenterMode when something changes (def:true)
    // presoBackgroundAlpha             float       The alpha value for the presentation background (def:1.0f)
    // presoClock24h                    boolean     Should the presentation clock be in 24hr format (def:true)
    // presoClockSeconds                boolean     Should the presentation clock include seconds (def:true)
    // presoClockSize                   float       The size of the clock on the presentation window (def:12.0f);
    // presoCopyrightTextSize           float       The size of the copyright text in Presentation mode (def:12.0f)
    // presoInfoAlign                   int         The align gravity of the info in presentation mode (Gravity.END)
    // presoLyricsAlign                 int         The align gravity of the lyrics in presentation mode (Gravity.HORIZONTAL_CENTER)
    // presoLyricsVAlign                int         The vertical align gravity of the lyrics in presentation mode (Gravity.TOP)
    // presoLyricsBold                  boolean     Should the presentation lyrics be displayed in bold (def:false)
    // presoShowChords                  boolean     Should chords be shown in the presentation window (def:false)
    // presoShowClock                   boolean     Should a clock be shown on the presentation window (def:false)
    // presoTitleTextSize               float       The size of the title text in Presentation mode (def:14.0f)
    // presoTransitionTime              int         The time for transitions between items in presenter mode (ms) (def:800)
    // presoXMargin                     int         The margin for the X axis on the presentation window (def:0)
    // presoYMargin                     int         The margin for the Y axis on the presentation window (def:0)
    // prevInSet                        boolean     Should the previous song in the set be shown (def:false)
    // prevNextTextButtons              boolean     Should the previous / next buttons be ExtendedFABs with text rather than FABs (def:true)
    // prevNextHide                     boolean     Should the previous / next buttons be hidden after timeout (def:true)
    // prevNextSongMenu                 boolean     Should previous and next songs be shown when not in a set from song menu (def:false)
    // profileName                      String      The last loaded or saved profile name (def:"")
    // refAHz                           int         The preferred frequency of A4 in Hz for the tuner (def:440).
    // repeatMode                       boolean     Should the pedal long press use repeat mode (multiple key up commands) (def:false)
    // repeatModeCount                  int         How many key up commands need to have happened in 500ms (def:5)
    // repeatModeTime                   int         The time to listen for the count of key up commands (def:200)
    // runWebServer                     boolean     Should a web server displaying the song be automatically started on boot (def:false)
    // runssincebackup                  int         The number of times the app has opened without backup (prompt the user after 10) (def:0)
    // runssincebackupdismissed         int         A rolling counter 0-10 on how many boots since dismissing the alert (def:0)
    // scaleChords                      float       The scale factor for chords relative to the lyrics (def:0.8f)
    // scaleComments                    float       The scale factor for comments relative to the lyrics (def:0.8f)
    // scaleHeadings                    float       The scale factor for headings relative to the lyrics (def:0.6f)
    // scaleTabs                        float       The scale factor for tabs relative to the lyrics (def:0.8f)
    // scrollDistance                   float       The percentage of the screen that is scrolled using the scroll buttons/pedals (def:0.7f)
    // scrollSpeed                      int         How quick should the scroll animation be (def:1500)
    // searchFolder                     boolean     Should the folder be included in the search (def:true)
    // searchFolderChosen               String      What is the currently selected folder
    // setCurrent                       String      The current set (each item enclosed in $**_folder/song_**$) - gets parsed on loading app (def:"")
    // setCurrentBeforeEdits            String      The current set before edits.  Used as a comparison to decide save action(def:"")
    // setCurrentLastName               String      The last name used when saving or loading a set (def:"")
    // setLoadFirst                     boolean     Should the first item in a set be called when loading (def:true)
    // setsSortOrder                    String      Which order to sort the set list (def:az - other options za, newest, oldest);
    // songAuthorSize                   float       The size of the song author text in the action bar (def:11.0f)
    // songAutoScale                    String      Choice of autoscale mode (Y)es, (W)idth only or (N)one (def:W)
    // songAutoScaleColumnMaximise      boolean     When autoscale is on full and columns are used, should each column scale independently to maximise font size (def:true)
    // songAutoScaleOverrideFull        boolean     If the app can override full autoscale if the font is too small (def:true)
    // songAutoScaleOverrideWidth       boolean     If the app can override width autoscale if the font is too small (def:false)
    // songFilename                     String      The name of the current song file (def:"")
    // songListSearchByFolder           boolean     Should we search in the song list using a custom folder (def:true)
    // songListSearchByFolderValue    String        The folder filter chosen to search in (def:"")
    // songListSearchByArtist           boolean     Should we search in the song list using a custom artist (def:false)
    // songListSearchByKey              boolean     Should we search in the song list using a custom key (def:false)
    // songListSearchByTag              boolean     Should we search in the song list using a custom folder (def:false)
    // songListSearchByTitle            boolean     Should we search in the song list using a custom title (def:false)
    // songListSearchByFilter           boolean     Should we search in the song list using a custom filter (def:false)
    // songLoadSuccess                  boolean     Indicates if the song loaded correctly (won't load a song next time if it crashed) (def:false)
    // songMenuAlphaIndexLevel2         boolean     Should the alphabetical index have a level 2 filter (clicking on it displays two letters) (def:false)
    // songMenuAlphaIndexShow           boolean     Should we show the alphabetical index in the song menu (def:true)
    // songMenuAlphaIndexSize           float       The text size for the alphabetical index in the song menu (def:12.0f)
    // songMenuItemSize                 float       The text size for the song menu items (def:14.0f)
    // songMenuSubItemSizeAuthor        float       The text size for the song menu item sub title for the author (def:12.0f)
    // songMenuSubItemSizeFile          float       The text size for the song menu item sub title for the file location (def:12.0f)
    // songMenuSetTicksShow             boolean     Should we show the ticks identifying song is in the set in the song menu (def:true)
    // songSheet                        boolean     Should the extra song info be shown at the top like a printed songsheet? (def:false)
    // songTitleSize                    float       The size of the song title text in the action bar (def:13.0f)
    // songMenuSortTitles               boolean     If the song menu should sort (and display) by title rather than filename (def:true)
    // soundMeterRange                  int         The volume range of the sound meter (def:400)
    // stageModeScale                   float       The max height of each stage mode section (to allow next section to peek at bottom) (def:0.80f)
    // stickyAuto                       boolean     Should sticky notes be shown automatically (def:true)
    // stickyTextSize                   float       The text size for the popup sticky note (def:14f)
    // stickyWidth                      int         The width of popup sticky notes (def:400)
    // stickyXPosition                  int         The x position of the popup sticky note (def: -1 which means figure it out first)
    // stickyYPosition                  int         The y position of the popup sticky note (def: -1 which means figure it out first)
    // swipeForMenus                    boolean     Can we swipe the menus in or out (def:true)
    // swipeForSongs                    boolean     Can we swipe to move between song items (def:true)
    // swipeMinimumDistance             int         The minimum distance for a swipe to be registered (dp) (def:250)
    // swipeMinimumVelocity             int         The minimum speed for a swipe to be registered (dp/s) (def:600)
    // swipeMaxDistanceYError           int         The maximum Y movement in a swipe allowed for it to be recognised (def:200)
    // timeToDisplayHighlighter         int         The time to show highlighter notes for before hiding (def=0 means keep on) ms
    // timeToDisplaySticky              int         The time to show sticky notes for before hiding (def=0 means keep on)
    // trimSections                     boolean     Should whitespace be removed from song sections (def:true)
    // trimLines                        boolean     Should the lines be trimmed (using the lineSpacing) value (def:false)
    // trimWordSpacing                  boolean     Should multiple spaces be trimmed between words (e.g. 'This    is   a    sentence. What   happens?'>'This is a sentence.  What happens?' (def:true)
    // tunerCents                       int         How many cents the note has to be within to be in tune (def:2)
    // uriTree                          String      A string representation of the user root location (may be the OpenSong folder or its parent) (def:"")
    // uriTreeHome                      String      A string representation of the user home location (The OpenSong folder) (def:"")
    // usePresentationOrder             boolean     Should the song be parsed into the specified presentation order (def:false)
    // webViewDesktop                   boolean     Should a desktop site be requested for UG online import (def:false)
    // whichSetCategory                 String      Which set category are we browsing (category___setname) (def:c.getString(R.string.mainfoldername))
    // whichMode                        String      Which app mode - Stage, Performance, Presentation (def:Performance)
    //

}