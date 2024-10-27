package com.garethevans.church.opensongtablet.export;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.customviews.MyMaterialTextView;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class OpenSongSetBundle {

    // This class deals with OpenSongSetBundle files (.ossb)
    // A bundle contains a set file (ending with .osts) in the root folder (MAIN)
    // The set file contains references to the file locations (sub folders)
    // All OpenSong formatted songs are given the extension .ost
    // Any songs that are PDFs/images get copied as is
    // The OpenSongSetBundle will be a zip file with the .ossb extension

    private final String TAG = "OpenSongSetBundle";
    private MainActivityInterface mainActivityInterface;
    private final String bundleExtension = ".ossb", setExtension = ".osts", songExtension = ".ost",
    processing_string;
    private Uri bundleUri;
    private boolean error = false;
    private boolean alive = true;
    private MyMaterialTextView progressText;
    private ZipEntry ze;
    private File setBundleFolder, justChordsBundleFolder, setFile;
    private ArrayList<File> songFiles, songFolders;
    private Uri setBundleUri, justChordsBundleUri;


    public OpenSongSetBundle(Context c, MyMaterialTextView progressText) {
        mainActivityInterface = (MainActivityInterface) c;
        this.progressText = progressText;
        processing_string = c.getString(R.string.processing);
        prepareSetBundleFolder();
    }

    public void zipFiles(String bundleFilename, ArrayList<Uri> uris, boolean ossb) {
        // The file uris are in the passed array list, so we just zip them
        alive = true;
        InputStream inputStream;
        byte[] tempBuff = new byte[1024];

        bundleUri = mainActivityInterface.getStorageAccess().getUriForItem("Export",
                "", bundleFilename + bundleExtension);
        mainActivityInterface.getStorageAccess().updateFileActivityLog(TAG +
                " Create OpenSongSetBundle/" + bundleFilename + bundleExtension + "  deleteOld=true");
        mainActivityInterface.getStorageAccess().lollipopCreateFileForOutputStream(true,
                bundleUri, null, "Export", "",
                bundleFilename + bundleExtension);

        if (ossb) {
            setBundleUri = bundleUri;
        } else {
            justChordsBundleUri = bundleUri;
        }

        OutputStream outputStream;
        ZipOutputStream zipOutputStream = null;
        try {
            outputStream = mainActivityInterface.getStorageAccess().getOutputStream(bundleUri);
            zipOutputStream = new ZipOutputStream(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        }
        ZipEntry ze;

        // Now deal with the files
        for (Uri uri : uris) {
            if (alive && uri!=null) {
                // Get the uri for this item
                inputStream = mainActivityInterface.getStorageAccess().getInputStream(uri);
                String thisFilename = mainActivityInterface.getStorageAccess().getFileNameFromUri(uri);
                ze = new ZipEntry(thisFilename);

                if (zipOutputStream != null) {
                    // Update the screen
                    mainActivityInterface.getMainHandler().post(() -> {
                        String message = processing_string + ": " + thisFilename;
                        if (progressText != null) {
                            progressText.setText(message);
                        }
                    });
                    try {
                        zipOutputStream.putNextEntry(ze);
                        if (!ze.isDirectory()) {
                            int len;
                            while ((len = inputStream.read(tempBuff)) > 0) {
                                zipOutputStream.write(tempBuff, 0, len);
                            }
                        }
                        zipOutputStream.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                        error = true;
                    }
                }

                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            if (zipOutputStream!=null) {
                zipOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        }
    }

    public void unzipFiles() {
        // Take the importUri and unzip the files in it
        // All songs will end with .osts, .ost, .pdf or (less likely) image files
        // The set will contain the correct location of the songs (subfolders)

        // Extract the files to our app storage location and deal with them from there
        prepareSetBundleFolder();
        alive = true;

        InputStream inputStream = mainActivityInterface.getStorageAccess().getInputStream(mainActivityInterface.getImportUri());
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));

        // Simply extract every file from the zip file
        try {
            byte[] buffer = new byte[1024];
            while ((ze = zipInputStream.getNextEntry()) != null) {
                if (!ze.isDirectory()) {
                    File zeFile = new File(ze.getName());
                    OutputStream outputStream = new FileOutputStream(zeFile);
                    int count;
                    try {
                        if (alive) {
                            while ((count = zipInputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, count);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            error = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Now get a note of the files
        songFiles = new ArrayList<>();
        songFolders = new ArrayList<>();
        setFile = null;

        Uri setFileUri = Uri.fromFile(setFile);
        mainActivityInterface.getSetActions().extractSetFile(setFileUri,true);

        File[] files = setBundleFolder.listFiles();
        if (files!=null) {
            for (File file : files) {
                if (file.getName().endsWith(setExtension)) {
                    setFile = file;
                    Log.d(TAG, "setFile:" + setFile.getName());
                } else if (file.getName().endsWith(songExtension) ||
                        mainActivityInterface.getStorageAccess().isIMGorPDF(file.getName())) {
                    songFiles.add(file);
                    Log.d(TAG, "songFile:" + file.getName());
                }
            }
        }

        if (setFile!=null) {
            // Let's clear our current set
            mainActivityInterface.getCurrentSet().initialiseTheSet();
            mainActivityInterface.getCurrentSet().setSetCurrent("");
            mainActivityInterface.getCurrentSet().setSetCurrentBeforeEdits("");
            mainActivityInterface.getCurrentSet().setSetCurrentLastName("");

            mainActivityInterface.getSetActions().extractSetFile(setFileUri,false);
        }
    }

    private void prepareSetBundleFolder() {
        setBundleFolder = mainActivityInterface.getStorageAccess().
                getAppSpecificFile("SetBundle", "openSong", null);

        justChordsBundleFolder = mainActivityInterface.getStorageAccess().
                getAppSpecificFile("SetBundle", "justChords", null);

        // Make sure the directory is empty
        File[] files = setBundleFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                Log.d(TAG, "deleted old file:" + file.delete());
            }
        }

        files = justChordsBundleFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                Log.d(TAG, "deleted old file:" + file.delete());
            }
        }
    }

    public Uri getSetBundleUri() {
        return setBundleUri;
    }

    public Uri getJustChordsBundleUri() {
        return justChordsBundleUri;
    }

    public void destroy() {
        alive = false;
    }

}
