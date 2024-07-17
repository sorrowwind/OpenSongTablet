package com.garethevans.church.opensongtablet.webserver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.setmenu.SetItemInfo;
import com.garethevans.church.opensongtablet.songprocessing.Song;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    private MainActivityInterface mainActivityInterface;
    private Context c;
    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "WebServer";
    @SuppressWarnings("FieldCanBeLocal")
    private final String nochords = "nochords/", songmenu = "songmenu/", songitem = "songitem/",
            setmenu = "setmenu/", setitem = "setitem/", hostsong = "hostsong/";
    private String ip;
    private boolean runWebServer, allowWebNavigation;
    private final String localFileSplit = ":____:";

    // Web server instantiation and closure
    public WebServer() {
        super(8080);
    }
    public void initialiseVariables(Context c) {
        this.c = c;
        mainActivityInterface = (MainActivityInterface) c;
        runWebServer = mainActivityInterface.getPreferences().getMyPreferenceBoolean("runWebServer",false);
        allowWebNavigation = mainActivityInterface.getPreferences().getMyPreferenceBoolean("allowWebNavigation",false);
        // If we have WIFI permissions, we can go ahead and get the required info and start the server if needed automatically
        if (mainActivityInterface.getAppPermissions().hasWebServerPermission()) {
            callRunWebServer();
        }

    }

    @SuppressLint("DefaultLocale")
    public void callRunWebServer() {
        getIP();
        try {
            if (runWebServer) {
                this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            } else {
                this.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopWebServer() {
        try {
            ip = null;
            this.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Deal with the server request for a webpage
    @Override
    public Response serve(IHTTPSession session) {
        // Session is what is sent by the client requesting a page from here
        // I've added the option of chords as a post parameter
        String pagerequest = session.getUri();
        String mimeType = MIME_HTML;
        String localFile = null;
        String webpage = "";
        boolean showchords = !pagerequest.contains(nochords);

        if (pagerequest.contains("_PDF/")) {
            // Serve a pdf image
            localFile = pagerequest.substring(pagerequest.indexOf("_PDF/")).replace("_PDF/","");
            mimeType = "application/pdf";
        } else if (pagerequest.contains("_JPG/")) {
            // Serve a jpeg image
            localFile = pagerequest.substring(pagerequest.indexOf("_JPG/")).replace("_JPG/","");
            mimeType = "image/jpeg";
        } else if (pagerequest.contains("_PNG/")) {
            // Serve a png image
            localFile = pagerequest.substring(pagerequest.indexOf("_PNG/")).replace("_PNG/", "");
            mimeType = "image/png";
        } else if (pagerequest.contains("_GIF/")) {
            // Serve a gif image
            localFile = pagerequest.substring(pagerequest.indexOf("_GIF/")).replace("_GIF/", "");
            mimeType = "image/gif";
        } else if (pagerequest.contains("_BMP/")) {
            // Serve a bmp image
            localFile = pagerequest.substring(pagerequest.indexOf("_BMP/")).replace("_BMP/", "");
            mimeType = "image/bmp";
        }

        if (localFile==null && allowWebNavigation) {
            if (pagerequest.contains(setmenu)) {
                // Get the current browsed song
                String currSong = getCurrSong(songitem, pagerequest);
                if (currSong.isEmpty()) {
                    currSong = getCurrSong(setitem, pagerequest);
                }
                webpage = createSetSongListHTML(true, currSong, showchords);

            } else if (pagerequest.contains(songmenu)) {
                // Get the current browsed song
                String currSong = getCurrSong(songitem, pagerequest);
                if (currSong.isEmpty()) {
                    currSong = getCurrSong(setitem, pagerequest);
                }
                webpage = createSetSongListHTML(false, currSong, showchords);

            } else if (pagerequest.contains(setitem)) {
                // We want to load a song in the set at the position afterwards
                pagerequest = pagerequest.replace(setitem, "");
                pagerequest = pagerequest.replaceAll("\\D", "");
                if (!pagerequest.isEmpty()) {
                    // Get the song to load
                    int setItemNum = Integer.parseInt(pagerequest);
                    SetItemInfo setItemInfo = mainActivityInterface.getCurrentSet().getSetItemInfo(setItemNum);
                    Song songForHTML = new Song();
                    songForHTML.setFolder(setItemInfo.songfolder);
                    songForHTML.setFilename(setItemInfo.songfilename);
                    songForHTML = mainActivityInterface.getLoadSong().doLoadSong(songForHTML, false);
                    webpage = getProcessedSongHTML(songForHTML, true, setItemNum, mainActivityInterface.getCurrentSet().getCurrentSetSize() - 1, setitem + setItemNum, showchords, false);
                }

            } else if (pagerequest.contains(songitem)) {
                // We want to load a song in the song menu at the position afterwards
                pagerequest = pagerequest.replace(songitem, "");
                pagerequest = pagerequest.replaceAll("\\D", "");
                if (!pagerequest.isEmpty()) {
                    // Get the song to load
                    int songItemNum = Integer.parseInt(pagerequest);
                    String folder = mainActivityInterface.getSongInMenu(songItemNum).getFolder();
                    String filename = mainActivityInterface.getSongInMenu(songItemNum).getFilename();
                    Song songForHTML = new Song();
                    songForHTML.setFolder(folder);
                    songForHTML.setFilename(filename);
                    songForHTML = mainActivityInterface.getLoadSong().doLoadSong(songForHTML, false);
                    webpage = getProcessedSongHTML(songForHTML, false, songItemNum, mainActivityInterface.getSongsInMenu().size(), songitem + songItemNum, showchords, false);
                }

            } else {
                // This is for /hostsong/ or the default splash screen for navigation mode
                int songItemNumber = mainActivityInterface.getPositionOfSongInMenu();
                webpage = getProcessedSongHTML(mainActivityInterface.getSong(), false, songItemNumber, mainActivityInterface.getSongsInMenu().size(), songitem + songItemNumber, showchords, true);

            }
        } else if (localFile == null) {
            // Just show the current song with no menu
            webpage = getProcessedSongHTML(mainActivityInterface.getSong(), false, 0, 0, songitem + "0", showchords, false);
        }

        mainActivityInterface.getStorageAccess().doStringWriteToFile("Settings","","web.html", webpage);

        if (localFile==null) {
            return newFixedLengthResponse(webpage);
        } else {
            String[] bits = localFile.split(localFileSplit);
            Uri localUri = mainActivityInterface.getStorageAccess().getUriForItem("Songs",bits[0],bits[1]);
            try {
                InputStream buffer = null;
                try {
                    buffer = mainActivityInterface.getStorageAccess().getInputStream(localUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return newFixedLengthResponse(Response.Status.OK, mimeType, buffer, -1 );

            } catch (Exception e) {
                e.printStackTrace();
                return newFixedLengthResponse(webpage);
            }
            //return newFixedLengthResponse(Response.Status.OK, mimeType, inputStream, -1);
        }
    }

    private String getCurrSong(String identifier, String from) {
        String text;
        if (from!=null && identifier!=null) {
            if (from.contains(identifier)) {
                text = from.substring(from.indexOf(identifier));
            } else {
                text = "";
            }
        } else {
            text = "";
        }
        if (!text.isEmpty() && !text.startsWith("/")) {
            text = "/" + text;
        }
        return text;
    }

    private int getCurrSongIndex(String from) {
        int index = 0;
        if (from.contains("/")) {
            String text = from.substring(from.lastIndexOf("/"));
            if (!text.isEmpty()) {
                text = text.replaceAll("\\D","");
                if (!text.isEmpty()) {
                    index = Integer.parseInt(text);
                }
            }
        }
        return index;
    }

    // Get IP address and QR code to match
    @SuppressLint("DefaultLocale")
    public String getIP() {
        if (ip==null || ip.isEmpty()) {
            try {
                WifiManager wifiMan = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInf = wifiMan.getConnectionInfo();
                int ipAddress = wifiInf.getIpAddress();
                ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "http://" + ip + ":8080/";
    }
    public Bitmap getIPQRCode() {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(getIP(), BarcodeFormat.QR_CODE, 800, 800);

            int w = bitMatrix.getWidth();
            int h = bitMatrix.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    pixels[y * w + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }


    // The preferences for running and options for web server
    public boolean getRunWebServer() {
        return runWebServer;
    }
    public void setRunWebServer(boolean runWebServer) {
        this.runWebServer = runWebServer;
        mainActivityInterface.getPreferences().setMyPreferenceBoolean("runWebServer",runWebServer);
        if (!runWebServer) {
            ip = null;
        } else {
            getIP();
        }
        callRunWebServer();
    }
    public boolean getAllowWebNavigation() {
        return allowWebNavigation;
    }
    public void setAllowWebNavigation(boolean allowWebNavigation) {
        this.allowWebNavigation = allowWebNavigation;
        mainActivityInterface.getPreferences().setMyPreferenceBoolean("allowWebNavigation",allowWebNavigation);
    }

    // The web page creation
    private String createSetSongListHTML(boolean setlist, String currSong, boolean showchords) {
        // Get the name of the current song
        int currSongIndex = getCurrSongIndex(currSong);
        int songMenuIndex = 0;
        int setMenuIndex = 0;
        if (!setlist && currSong.contains(songitem)) {
            songMenuIndex = currSongIndex;
        } else if (setlist && currSong.contains(setitem)) {
            setMenuIndex = currSongIndex;
        }

        // This uses the current set list to create a web page
        StringBuilder setSongListHTML = new StringBuilder();
        // Strings for webpage content building
        String base1 = "@import url('https://fonts.googleapis.com/css?family=";
        String base2 = "&swap=true');\n";
        setSongListHTML.append("<!DOCTYPE html>\n<html>\n")
                .append("<head>\n")
                .append("<style>\n")
                .append(base1)
                .append(mainActivityInterface.getMyFonts().getLyricFontName())
                .append(base2)
                .append(".page {color:")
                .append(String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsTextColor())))
                .append("; background-color:")
                .append(String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsBackgroundColor())))
                .append(";}\n")
                .append(".item {font-family:")
                .append(mainActivityInterface.getMyFonts().getLyricFontName())
                .append(", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; color:")
                .append(String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsTextColor())))
                .append("; padding:6px; font-size:10.0pt;}\n")
                .append(getMenuBarCSS())
                .append("body {width:100%; font-family:")
                .append(mainActivityInterface.getMyFonts().getLyricFontName())
                .append(", -apple-system, BlinkMacSystemFont, Lato, Arial, sans-serif; cursor: pointer; user-select: none;}\n")
                .append("</style>\n")
                .append("<script>\n")
                .append(getGlobalJSVariables(showchords,allowWebNavigation,currSongIndex,setlist,0,currSong,true, false))
                .append(getChordFunctionsJS())
                .append(getResizeJS())
                .append(getGoToSongJS())
                .append(getNavigateJS())
                .append("</script>\n")
                .append("</head>\n")
                .append("<meta name=\"viewport\" content=\"width=device-width\" id=\"viewport-meta\">\n")
                .append(getMenuBarHTML(true, !setlist, setlist))
                .append("<body class=\"page\" onload=\"javascript:measure()\">\n")
                .append("<div id=\"content\" style=\"width:fit-content; transform-origin: top left;\">\n");


        if (setlist) {
            // Now cycle through our set list and add a new div element for each one
            for (int x=0; x < mainActivityInterface.getCurrentSet().getCurrentSetSize(); x++) {
                String title = mainActivityInterface.getCurrentSet().getSetItemInfo(x).songtitle;
                String curritemId = "";
                if (x == setMenuIndex) {
                    curritemId = " id =\"currentItem\"";
                }
                setSongListHTML.append("<div").append(curritemId).append(" class=\"item\" onclick=\"javascript:getSpecificSong('set','")
                        .append(x).append("')\">").append(x+1).append(". ").append(title).append("</div>\n");
                setSongListHTML.append("<hr width=\"100%\"/>\n");
            }
        } else {
            // Now cycle through the song list and add a new div element for each one
            for (int x=0; x<mainActivityInterface.getSongsInMenu().size(); x++) {
                String title = mainActivityInterface.getSongsInMenu().get(x).getTitle();
                String curritemId = "";
                if (x == songMenuIndex) {
                    curritemId = " id =\"currentItem\"";
                }
                setSongListHTML.append("<div").append(curritemId).append(" class=\"item\" onclick=\"javascript:getSpecificSong('song','")
                        .append(x).append("')\">").append(title).append("</div>\n");
                setSongListHTML.append("<hr width=\"100%\"/>\n");
            }
        }

        setSongListHTML.append("</div>\n</body>\n</html>");

        return setSongListHTML.toString();
    }
    private String getProcessedSongHTML(Song songForHTML, boolean inset, int index, int max, String currSong, boolean showchords, boolean showhostsong) {
        mainActivityInterface.getProcessSong().processSongIntoSections(songForHTML,false);
        // IV - Initialise transpose capo key  - might be needed
        mainActivityInterface.getTranspose().capoKeyTranspose(songForHTML);

        String imgPDFSong = "";
        String fileExtension = songForHTML.getFilename().toLowerCase();
        if (fileExtension.contains(".")) {
            fileExtension = fileExtension.substring(fileExtension.lastIndexOf(".")).replace(".","");
        } else {
            fileExtension = "";
        }
        if (songForHTML.getFiletype().equals("IMG")) {
            if (fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {
                imgPDFSong = "<img src=\"_JPG/" + songForHTML.getFolder() + localFileSplit + songForHTML.getFilename() + "\" width=\"600\">";
            } else if (fileExtension.equals("png")) {
                imgPDFSong = "<img src=\"_PNG/" + songForHTML.getFolder() + localFileSplit + songForHTML.getFilename() + "\" width=\"600\">";
            }
        } else if (songForHTML.getFiletype().equals("PDF")) {
            imgPDFSong = "<object type=\"application/pdf\" data=\"_PDF/" + songForHTML.getFolder() + localFileSplit + songForHTML.getFilename() + "\" width=\"600px\" height=\"600px\">" +
                    "<p>" + c.getString(R.string.pdf_preview_not_allowed) + "</p>\n" +
                    "<a href=\"_PDF/" + songForHTML.getFolder() + localFileSplit + songForHTML.getFilename() + "\"/>" + c.getString(R.string.download) + " " + songForHTML.getFilename() + "</a></object>";
        }

        // Check to see if the song is in the users set even if clicked on from web song menu
        if (!inset) {
            Log.d(TAG,"WebServer looking for:"+songForHTML.getFolder()+"/"+songForHTML.getFilename());
            if (mainActivityInterface.getCurrentSet().getIndexSongInSet()>-1) {
                index = mainActivityInterface.getCurrentSet().getIndexSongInSet();
                inset = true;
                max = Math.max(mainActivityInterface.getCurrentSet().getCurrentSetSize() - 1,0);
            }
        }
        String songContent = "";
        StringBuilder stringBuilder = new StringBuilder();
        if (imgPDFSong.isEmpty()) {
            for (int sect = 0; sect < songForHTML.getPresoOrderSongSections().size(); sect++) {
                String section = songForHTML.getPresoOrderSongSections().get(sect);
                if (!section.isEmpty()) {
                    section = section.replace(mainActivityInterface.getProcessSong().columnbreak_string, "");
                    if (mainActivityInterface.getProcessSong().trimSections) {
                        // IV - End trim only as a section may start with a lyric line and have no header
                        section = ("¬" + section).trim().replace("¬", "");
                    }
                    // Add this section to the array (so it can be called later for presentation)
                    if (!section.trim().isEmpty()) {
                        // Now split by line, but keeping empty ones
                        String[] lines = section.split("\n", -1);
                        for (String line : lines) {
                            // IV - Do not process an empty group line or empty header line
                            if (!line.equals(mainActivityInterface.getProcessSong().groupline_string) && !line.equals("[]")) {
                                // Get the text stylings
                                String linetype = mainActivityInterface.getProcessSong().getLineType(line);
                                if (line.contains(mainActivityInterface.getProcessSong().groupline_string)) {
                                    // Has lyrics and chords
                                    stringBuilder.append(mainActivityInterface.getProcessSong().groupTableHTML(songForHTML, line));
                                } else {
                                    stringBuilder.append(mainActivityInterface.getProcessSong().lineTextHTML(songForHTML, linetype, line));
                                }
                            }
                        }
                    }
                }
            }
            songContent = mainActivityInterface.getSongSheetHeaders().getSongSheetTitleMainHTML(songForHTML) +
                    mainActivityInterface.getSongSheetHeaders().getSongSheetTitleExtrasHTML(songForHTML) +
                    stringBuilder;
        }

        return  "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                getHTMLFontImports() +
                ".page       {color:" + String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsTextColor())) +
                "; background-color:" + String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsBackgroundColor())) + ";}\n" +
                ".lyrictable {border-spacing:0; border-collapse: collapse; border:0px;}\n" +
                getMenuBarCSS() +
                "body        {width:100%;}\n" +
                "</style>\n" +
                "<script>\n" +
                getGlobalJSVariables(showchords,allowWebNavigation,index,inset,max,currSong,false,showhostsong) +
                getChordFunctionsJS() +
                getResizeJS() +
                getGoToSongJS() +
                getNavigateJS() +
                "</script>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" id=\"viewport-meta\">\n" +
                "</head>\n" +
                "<body class=\"page\" onload=\"javascript:measure()\">\n" +
                getMenuBarHTML(false, false, false) +
                "<div id=\"content\" style=\"width:fit-content; transform-origin: top left;\">\n" +
                imgPDFSong +
                songContent +
                "</div>\n</body>\n" +
                "</html>";
    }


    // Repeatable bits of code to save on duplication
    private String getHTMLFontImports() {
        // This prepares the import code for the top of the html file that locates the fonts from Google
        // If they've been downloaded already, they are cached on the device, so no need to redownload.
        String base1 = "@import url('https://fonts.googleapis.com/css?family=";
        String base2 = "&swap=true');\n";
        String importString = base1+mainActivityInterface.getMyFonts().getLyricFontName()+base2;
        importString += base1+mainActivityInterface.getMyFonts().getChordFontName()+base2;
        importString += ".menu {font-family:"+mainActivityInterface.getMyFonts().getLyricFontName()+", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; color:white; " +
                "font-size:14.0pt;}\n";
        importString += ".lyric {font-family:"+mainActivityInterface.getMyFonts().getLyricFontName()+", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; color:" +
                String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsTextColor())) + "; " +
                "padding: 0px; font-size:14.0pt; white-space:nowrap; width: fit-content;}\n";
        importString += ".chord {font-family:"+mainActivityInterface.getMyFonts().getChordFontName()+", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; color:" +
                String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsChordsColor())) + "; " +
                "padding: 0px; font-size:"+(14.0f*mainActivityInterface.getProcessSong().scaleChords)+"pt; white-space:nowrap;width: fit-content;}\n";
        importString += ".capo {font-family:"+mainActivityInterface.getMyFonts().getChordFontName()+", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; color:" +
                String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsCapoColor())) + "; " +
                "padding: 0px; font-size:"+(14.0f*mainActivityInterface.getProcessSong().scaleChords)+"pt; white-space:nowrap;width: fit-content;}\n";
        importString += ".titlemain {font-family:"+mainActivityInterface.getMyFonts().getLyricFontName()+", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; color:" +
                String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsTextColor())) + "; " +
                "padding: 0px; font-size:"+(14.0f*1.1f)+"pt; " +
                "text-decoration:underline;}\n";
        importString += ".titleextras {font-family:"+mainActivityInterface.getMyFonts().getLyricFontName()+", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; color:" +
                String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsTextColor())) + "; " +
                "padding: 0px; font-size:"+(14.0f*0.6f)+"pt; " +
                "text-decoration:none;}\n";
        importString += ".heading {font-family:"+mainActivityInterface.getMyFonts().getLyricFontName()+", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; color:" +
                String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsTextColor())) + "; " +
                "padding: 0px; font-size:"+(14.0f*mainActivityInterface.getProcessSong().scaleHeadings)+"pt; " +
                "text-decoration:underline;}\n";
        //importString += ".mono {font-family:"+mainActivityInterface.getMyFonts().getMonoFontName()+", 'Courier New', Courier, monospace; color:" +
        importString += ".mono {font-family:"+mainActivityInterface.getMyFonts().getMonoFontName()+", 'Courier New', monospace; color:" +
                String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsTextColor())) + "; " +
                "padding: 0px; font-size:"+(14.0f*mainActivityInterface.getProcessSong().scaleTabs)+"pt; " +
                "text-decoration:none;}\n";
        return importString;
    }

    private String getGlobalJSVariables(boolean showchords, boolean allowWebNavigation,
                                        int index, boolean inset, int maxitems, String currSong,
                                        boolean minSize, boolean showhostsong) {
        String showchordsline = "  var chords = true;\n";
        if (!showchords) {
            showchordsline = "  var chords = false;\n";
        }
        return  "  var contentWidth;\n" +
                "  var menuWidth;\n" +
                "  var menuscaleratio = 1;\n" +
                "  var showhostsong = " + showhostsong + ";\n" +
                showchordsline +
                "  var menusized = false;\n" +
                "  var allowWebNavigation="+allowWebNavigation+";\n" +
                "  var minSize=" + minSize + ";\n" +
                "  var maxSize=true;\n" +
                "  var splash=false;\n" +
                "  var serverAddress = \"" + getIP() + "\";\n" +
                "  var index="+index+";\n" +
                "  var inset="+inset+";\n" +
                "  var maxitems="+maxitems+";\n" +
                "  var currSong=\"" + currSong + "\";\n";
    }

    private String getChordFunctionsJS() {
        return "  function addNoChords() {" +
                "    var chordbit = \"\";\n" +
                "    if (!chords) {\n" +
                "      chordbit = \"" + nochords + "\";\n" +
                "    }\n" +
                "    return chordbit;\n" +
                "  }\n" +
                "  function toggleChords() {\n" +
                "    chords = !chords;\n" +
                "    getSong();\n" +
                "  }\n";
    }

    private String getResizeJS() {
        return "  function measure() {\n" +
                "    if (splash) {\n" +
                "      contentWidth = 512;\n" +
                "      menuWidth = 512;\n" +
                "    } else {\n" +
                "      contentWidth = document.getElementById(\"content\").clientWidth;\n" +
                "      menuWidth = document.getElementById(\"menu\").clientWidth;\n" +
                "    }\n" +
                "    if (allowWebNavigation && splash==false) {\n" +
                "      if (inset==true) {\n" +
                "        document.getElementById('set').style.backgroundColor = \"#294959\";\n" +
                "        document.getElementById('songs').style.backgroundColor = \"#232333\";\n" +
                "      } else if (inset==false) {\n" +
                "        document.getElementById('songs').style.backgroundColor = \"#294959\";\n" +
                "        document.getElementById('set').style.backgroundColor = \"#232333\";\n" +
                "      }\n" +
                "    }\n" +
                "    var chordlines = document.getElementsByClassName('chord');\n" +
                "    for (var i = 0; i < chordlines.length; i ++) {\n" +
                "       if (chords==false) {\n" +
                "           chordlines[i].style.display = 'none';\n" +
                "       } else {\n" +
                "           chordlines[i].style.display = 'table-cell';\n" +
                "       }\n" +
                "    }\n" +
                "    var capolines = document.getElementsByClassName('capo');\n" +
                "    for (var i = 0; i < capolines.length; i ++) {\n" +
                "       if (chords==false) {\n" +
                "           capolines[i].style.display = 'none';\n" +
                "       } else {\n" +
                "           capolines[i].style.display = 'table-cell';\n" +
                "       }\n" +
                "    }\n" +
                "    resize();\n" +
                "    window.location.href = \"#currentItem\";\n" +
                "  }\n" +
                "  function resize() {\n" +
                "    var viewportWidth = document.body.clientWidth - 24;\n" +
                "    var padding = document.body.style.padding;\n" +
                "    var scaleratio = 1;\n" +
                "    if (!menusized) {\n" +
                "       menuscaleratio = viewportWidth/menuWidth;\n" +
                "       scaleratio = viewportWidth/contentWidth;\n" +
                "       menusized = true;\n" +
                "    }\n" +
                "    if (menuscaleratio>2) {\n" +
                "        menuscaleratio = 2;\n" +
                "    }\n" +
                "    if (minSize && scaleratio<1) {\n" +
                "      scaleratio = 1;\n" +
                "    }\n" +
                "    if (maxSize && scaleratio>2) {\n" +
                "      scaleratio = 2;\n" +
                "    }\n" +
                "    var scaleSize = (scaleratio*100) + \"%\";\n" +
                "    var menuScale = (menuscaleratio*100) + \"%\";\n" +
                "    document.getElementById('menu').width = viewportWidth;\n" +
                "    document.getElementById('menu').style.transform = \"scale(\"+menuScale+\")\";\n" +
                "    document.getElementById('content').style.height = (document.getElementById('content').clientHeight * scaleratio)+\"px\";\n" +
                "    document.getElementById('content').style.transform = \"translate(0,\" + (document.getElementById('menu').clientHeight * menuscaleratio) + \"px) scale(\"+scaleSize+\")\";\n" +
                "    var newHeight = document.getElementById('content').clientHeight;\n" +
                "    var newWidth = viewportWidth;\n" +
                "    if (scaleratio>1) {\n" +
                "      newHeight = Math.round(newHeight / scaleratio);\n" +
                "      newWidth = Math.round(newWidth / scaleratio);\n" +
                "    }\n" +
                "    document.getElementById('content').style.height = \"\" + (newHeight) + \"px\";\n" +
                "    document.getElementById('content').style.width = \"\" + newWidth + \"px\";\n" +
                "    document.body.style.height = \"\" + (newHeight + document.getElementById('menu').clientHeight) + \"px\";\n" +
                "    document.body.style.width = \"\" + (newWidth) + \"px\";\n" +
                "  }\n" +
                "  function offsetAnchor() {\n" +
                "    if (location.hash.length !== 0) {\n" +
                "       window.scrollTo(window.scrollX, window.scrollY - (document.getElementById('menu').clientHeight) * menuscaleratio);\n" +
                "    }\n" +
                "    if (chords) {\n" +
                "      document.getElementById('chordbutton').style.textDecoration = \"none\";\n" +
                "    } else {\n" +
                "      document.getElementById('chordbutton').style.textDecoration = \"line-through\";\n" +
                "    }\n" +
                "  }\n" +
                "  window.addEventListener(\"hashchange\", offsetAnchor);\n" +
                "  window.setTimeout(offsetAnchor, 1); // The delay of 1 is arbitrary and may not always work right (although it did in my testing).\n\n";
    }


    private String getGoToSongJS() {
        return  "  function getSong() {\n" +
                "      if (showhostsong) {\n " +
                "        window.location.href = serverAddress + addNoChords() + currSong;\n" +
                "      } else if (inset) {\n" +
                "        window.location.href = serverAddress + addNoChords() + \"" + setitem + "\" + index;\n" +
                "      } else {\n" +
                "        window.location.href = serverAddress + addNoChords() + \"" + songitem + "\" + index;\n" +
                "      }\n" +
                "      showhostsong = false;\n" +
                "  }\n" +
                "  function getSpecificSong(how,newindex) {\n" +
                "      index = newindex;\n" +
                "      if (how==\"currSong\") {\n " +
                "        showhostsong = true;\n" +
                "      } else if (how==\"set\") {\n" +
                "        inset = true;\n" +
                "      } else {\n" +
                "        inset = false;\n" +
                "      }\n" +
                "      getSong();\n" +
                "  }\n";
    }
    private String getNavigateJS() {
        return  "  function songMenu() {\n" +
                "    window.location.href = serverAddress + addNoChords() + \"" + songmenu + "\" + currSong;\n" +
                "  }\n" +
                "  function setMenu() {\n" +
                "    window.location.href = serverAddress + addNoChords() + \"" + setmenu + "\" + currSong;\n" +
                "  }\n" +
                "  function back() {\n" +
                "    if (index>0) {\n" +
                "      if (inset) {\n" +
                "        getSpecificSong('set',index-1);\n" +
                "      } else {\n" +
                "        getSpecificSong('song',index-1);\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "  function forward() {\n" +
                "    if (index<maxitems) {\n" +
                "      if (inset) {\n" +
                "        getSpecificSong('set',index+1);\n" +
                "      } else {\n" +
                "        getSpecificSong('song',index+1);\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "  function hostSong() {\n" +
                "    var chordbit = \"\";\n" +
                "    if (chords) {\n" +
                "        chordbit = \"" + nochords + "\";\n" +
                "    }\n" +
                "    window.location.href = serverAddress + addNoChords() + \"" + hostsong + "\";\n" +
                "  }\n";
    }
    private String getMenuBarCSS() {
        if (allowWebNavigation) {
            return "#menu {position:fixed; padding: 0; top:0; overflow-x: scroll; white-space: nowrap; display:inline-block; transform-origin: top left; " +
                    "color:white; position:fixed; z-index:1; " +
                    "background-color:" + String.format("#%06X", (0xFFFFFF & mainActivityInterface.getMyThemeColors().getLyricsBackgroundColor())) + "; " +
                    "font-family:" + mainActivityInterface.getMyFonts().getLyricFontName() + ", -apple-system, BlinkMacSystemFont, Tahoma, Verdana, sans-serif; " +
                    "font-size:8pt;}\n" +
                    "a {margin-right:8px; padding:8px; float:left; display:inline-block; padding:8px; color:white; background-color:#294959; font-size:16pt;}\n" +
                    "a:link {color:white; text-decoration:none; font-size:8pt;}\n" +
                    "a:visited {color:white; text-decoration:none; font-size:8pt;}\n" +
                    "a:hover {color:white; text-decoration:none; font-size:8pt;}\n" +
                    "a:active {color:white; text-decoration:none; font-size:8pt;}\n";
        } else {
            return "";
        }
    }
    private String getMenuBarHTML(boolean hidearrows, boolean songmenu, boolean setmenu) {
        String text = "";
        if (allowWebNavigation) {
            String songmenuJS = "songMenu()";
            String setmenuJS = "setMenu()";
            if (songmenu) {
                songmenuJS = "javascript:getSpecificSong('currSong',0)";
            }
            if (setmenu) {
                setmenuJS = "javascript:getSpecificSong('currSong',0)";
            }
            text = "<span id=\"menu\">\n<a id=\"songs\" href=\"javascript:" + songmenuJS + "\">&nbsp; " + c.getString(R.string.songs) + "&nbsp; </a>\n" +
                    "<a id=\"set\" href=\"javascript:" + setmenuJS + "\">&nbsp; " + c.getString(R.string.set) + "&nbsp; </a>\n";
            if (hidearrows) {
                text += "</span>\n";
            } else {
                text += "<a href=\"javascript:hostSong()\">&nbsp; " + c.getString(R.string.web_server_host_song) + "&nbsp; </a>\n" +
                        "<a href=\"javascript:toggleChords()\">&nbsp; <span id=\"chordbutton\">" + c.getString(R.string.chords) + "</span>&nbsp; </a>\n" +
                        "<a href=\"javascript:back()\">&nbsp; &nbsp; &lt;&nbsp; &nbsp; </a>\n" +
                        "<a href=\"javascript:forward()\">&nbsp; &nbsp; &gt;&nbsp; &nbsp; </a>\n</span>\n";
            }
        }
        return text;
    }
}