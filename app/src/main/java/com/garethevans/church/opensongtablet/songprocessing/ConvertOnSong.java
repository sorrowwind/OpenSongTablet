package com.garethevans.church.opensongtablet.songprocessing;

import android.content.Context;
import android.net.Uri;

import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

public class ConvertOnSong {

    // This is virtually the same as convertChoPro, but with a few extra tags
    // To simplify this, we will extract the specific OnSongStuff first and then pass it to convertChoPro

    // Declare the variables;
    private String title;
    private String author;
    private String key;
    private String capo;
    private String capoprint;
    private String copyright;
    private String ccli;
    private String tempo;
    private String time_sig;
    private String lyrics;
    private String midi;
    private String midiindex;
    private String duration;
    private String number;
    private String flow;
    private String theme;
    private String oldSongFileName;
    private String newSongFileName;
    private String songSubFolder;
    private String[] lines;
    private StringBuilder parsedLines;

    public Song convertTextToTags(Context c, MainActivityInterface mainActivityInterface, Uri uri, Song thisSong) {

        initialiseTheVariables();

        lyrics = thisSong.getLyrics();

        // Fix line breaks and slashes
        lyrics = mainActivityInterface.getProcessSong().fixLineBreaksAndSlashes(lyrics);

        // Fix specific OnSong tags
        lyrics = fixOnSongTags(lyrics);

        // Make tag lines common
        lyrics = mainActivityInterface.getConvertChoPro().makeTagsCommon(lyrics);

        // Fix content we recognise as OnSongTags
        lyrics = fixRecognisedContent(lyrics, mainActivityInterface);

        // Now that we have the basics in place, we will go back through the song and extract headings
        // We have to do this separately as [] were previously identifying chords, not tags.
        // Chords have now been extracted to chord lines
        lyrics = mainActivityInterface.getConvertChoPro().removeOtherTags(lyrics);

        // Get rid of multilple line breaks (max of 3 together)
        lyrics = mainActivityInterface.getConvertChoPro().getRidOfExtraLines(lyrics);

        // Add spaces to beginnings of lines that aren't comments, chords or tags
        lyrics = mainActivityInterface.getConvertChoPro().addSpacesToLines(lyrics);

        // Get the filename and subfolder (if any) that the original song was in by parsing the uri
        oldSongFileName = mainActivityInterface.getConvertChoPro().getOldSongFileName(uri);
        songSubFolder = mainActivityInterface.getConvertChoPro().getSongFolderLocation(mainActivityInterface, uri, oldSongFileName);

        // Prepare the new song filename
        newSongFileName = mainActivityInterface.getConvertChoPro().getNewSongFileName(mainActivityInterface, uri, title);

        // Set the correct values
        thisSong = setCorrectXMLValues(thisSong);

        // Now prepare the new songXML file
        String myNewXML = mainActivityInterface.getProcessSong().getXML(c,mainActivityInterface,thisSong);

        // Get a unique uri for the new song
        Uri newUri = mainActivityInterface.getConvertChoPro().getNewSongUri(c, mainActivityInterface, songSubFolder, newSongFileName);
        newSongFileName = newUri.getLastPathSegment();
        // Just in case it had _ appended due to name conflict.
        // Get rid of the rubbish...
        if (newSongFileName!=null && newSongFileName.contains("/")) {
            newSongFileName = newSongFileName.substring(newSongFileName.lastIndexOf("/"));
            newSongFileName = newSongFileName.replace("/","");
        }

        thisSong.setFilename(newSongFileName);

        // Now write the modified song
        mainActivityInterface.getConvertChoPro().writeTheImprovedSong(c, mainActivityInterface, thisSong, oldSongFileName, newSongFileName,
                songSubFolder, newUri, uri, myNewXML);

        // Add it to the database
        thisSong.setFilename(newSongFileName);
        thisSong.setTitle(title);
        thisSong.setAuthor(author);
        thisSong.setCopyright(copyright);
        thisSong.setKey(key);
        thisSong.setTimesig(time_sig);
        thisSong.setCcli(ccli);
        thisSong.setLyrics(lyrics);

        return thisSong;
    }

    private void initialiseTheVariables() {
        title = "";
        author = "";
        key = "";
        capo = "";
        capoprint = "";
        copyright = "";
        ccli = "";
        tempo = "";
        time_sig = "";
        oldSongFileName = "";
        newSongFileName = "";
        songSubFolder = "";
        lines = null;
        midi = "";
        midiindex = "";
        duration = "";
        number = "";
        flow = "";
        theme = "";

        parsedLines = new StringBuilder();
    }

    private String fixOnSongTags(String l) {
        l = l.replace("{artist :", "{artist:");
        l = l.replace("{a:", "{artist:");
        l = l.replace("{author :", "{author:");
        l = l.replace("{copyright :", "{copyright:");
        l = l.replace("{footer:", "{copyright:");
        l = l.replace("{footer :", "{copyright:");
        l = l.replace("{key :", "{key:");
        l = l.replace("{k:", "{key:");
        l = l.replace("{k :", "{key:");
        l = l.replace("{capo :", "{capo:");
        l = l.replace("{time :", "{time:");
        l = l.replace("{tempo :", "{tempo:");
        l = l.replace("{duration :", "{duration:");
        l = l.replace("{number :", "{number:");
        l = l.replace("{flow :", "{flow:");
        l = l.replace("{ccli :", "{ccli:");
        l = l.replace("{keywords :", "{keywords:");
        l = l.replace("{topic:", "{keywords:");
        l = l.replace("{topic :", "{keywords:");
        l = l.replace("{book :", "{book:");
        l = l.replace("{midi :", "{midi:");
        l = l.replace("{midi-index :", "{midi-index:");
        l = l.replace("{pitch :", "{pitch:");
        l = l.replace("{restrictions :", "{restrictions:");

        return l;
    }

    private String fixRecognisedContent(String l, MainActivityInterface mainActivityInterface) {
        // Break the filecontents into lines
        lines = l.split("\n");

        // IV - Handle tagless 1st and 2nd lines as Title and Artist
        if ((lines.length > 0) && (!lines[0].contains(":"))) {
            title = lines[0].trim();
            lines[0] = "";
        }
        if ((lines.length > 1) && (!lines[1].contains(":"))) {
            // IV - Change ';' to ',' - the separator used by CCLI
            author = lines[1].trim().replace(";",",");
            lines[1] = "";
        }

        // This will be the new lyrics lines
        parsedLines = new StringBuilder();
        for (String line : lines) {
            // Get rid of any extra whitespace
            line = line.trim();

            // Remove directive lines we don't need
            line = mainActivityInterface.getConvertChoPro().removeObsolete(line);

            if (line.contains("{title:") || line.contains("Title:")) {
                // Extract the title and empty the line (don't need to keep it)
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{title:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Title:");
                title = line.trim();
                line = "";

            } else if (line.contains("{artist:") || line.contains("Artist:") || line.contains("Author:")) {
                // Extract the author and empty the line (don't need to keep it)
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{artist:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Artist:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Author:");
                author = line.trim();
                line = "";

            } else if (line.contains("{copyright:") || line.contains("Copyright:") || line.contains("Footer:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{copyright:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Copyright:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Footer:");
                copyright = line.trim();
                line = "";

            } else if (line.contains("{subtitle:")) {
                // Extract the subtitles.  Add it back as a comment line
                String subtitle = mainActivityInterface.getConvertChoPro().removeTags(line, "{subtitle:");
                if (author.equals("")) {
                    author = subtitle;
                }
                if (copyright.equals("")) {
                    copyright = subtitle;
                }
                line = ";" + subtitle;

            } else if (line.contains("{ccli:") || line.contains("CCLI:")) {
                // Extract the ccli (not really a chordpro tag, but works for songselect and worship together
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{ccli:").trim();
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "CCLI:").trim();
                ccli = line.trim();
                line = "";

            } else if (line.contains("{key:") || line.contains("Key:")) {
                // Extract the key
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{key:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Key:");
                line = line.replace("[", "");
                line = line.replace("]", "");
                key = line.trim();
                line = "";

            } else if (line.contains("{capo:") || line.contains("Capo:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{capo:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Capo:");
                capo = line.trim();
                capoprint = "true";

            } else if (line.contains("{tempo:") || line.contains("Tempo:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{tempo:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Tempo:");
                tempo = line.trim();
                line = "";

            } else if (line.contains("{time:") || line.contains("Time:")) {
                // Extract the timesig
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{time:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Time:");
                time_sig = line.trim();
                line = "";

            } else if (line.contains("{duration:") || line.contains("Duration:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{duration:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Duration:");
                duration = line.trim();
                line = "";

            } else if (line.contains("{number:") || line.contains("Number:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{number:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Number:");
                number = line.trim();
                line = "";

            } else if (line.contains("{flow:") || line.contains("Flow:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{flow:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Flow:");
                flow = line.trim();
                line = "";

            } else if (line.contains("{keywords:") || line.contains("Keywords:") || line.contains("Topic:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{keywords:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Keywords:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "Topic:");
                theme = line.trim();
                line = "";

            } else if (line.contains("{midi:") || line.contains("MIDI:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{midi:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "MIDI:");
                midi = line.trim();
                line = "";

            } else if (line.contains("{midi-index:") || line.contains("MIDI-Index:")) {
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "{midi-index:");
                line = mainActivityInterface.getConvertChoPro().removeTags(line, "MIDI-Index:");
                midiindex = line.trim();
                line = "";

            } else if (line.startsWith("#")) {
                // Change lines that start with # into comment lines
                line = line.replaceFirst("#", ";");

            } else if (line.contains("{comments:") || line.contains("{comment:")) {
                // Change comment lines
                line = ";" + mainActivityInterface.getConvertChoPro().removeTags(line, "{comments:").trim();
                line = ";" + mainActivityInterface.getConvertChoPro().removeTags(line, "{comment:").trim();

            }

            // Fix guitar tab so it fits OpenSongApp formatting ;e |
            line = mainActivityInterface.getConvertChoPro().tryToFixTabLine(mainActivityInterface,line);

            if (line.startsWith(";;")) {
                line = line.replace(";;", ";");
            }

            // Now split lines with chords in them into two lines of chords then lyrics
            line = mainActivityInterface.getConvertChoPro().extractChordLines(line);

            line = line.trim() + "\n";
            parsedLines.append(line);

        }
        return parsedLines.toString();
    }

    private Song setCorrectXMLValues(Song thisSong) {
        if (title == null || title.isEmpty()) {
            thisSong.setTitle(newSongFileName);
        } else {
            thisSong.setTitle(title.trim());
        }
        thisSong.setAuthor(author.trim());
        thisSong.setCopyright(copyright.trim());
        thisSong.setTempo(tempo.trim());
        thisSong.setTimesig(time_sig.trim());
        thisSong.setCcli(ccli.trim());
        thisSong.setKey(key.trim());
        thisSong.setLyrics(lyrics.trim());
        thisSong.setCapo(capo.trim());
        thisSong.setCapoprint(capoprint.trim());
        thisSong.setMidi(midi.trim());
        thisSong.setMidiindex(midiindex.trim());
        thisSong.setAutoscrolllength(duration.trim());
        thisSong.setPresentationorder(flow.trim());
        thisSong.setHymnnum(number.trim());
        thisSong.setTheme(theme.trim());

        return thisSong;
    }

}