package com.garethevans.church.opensongtablet.songprocessing;

import android.content.Context;

import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

public class ConvertJustChords {

    // This is used to convert between OpenSong and JustChords format
    private final String TAG = "ConvertJustSong";
    private final Context c;
    private final MainActivityInterface mainActivityInterface;

    public ConvertJustChords(Context c) {
        this.c = c;
        mainActivityInterface = (MainActivityInterface) c;
    }

    public String convertOpenSongToJustChords(Song song) {
        // TODO
        return "This will eventually be the justChords conversion - returning text only";
    }

    public void convertJustChordsToOpenSong(Song song) {

    }
}
