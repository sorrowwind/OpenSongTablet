package com.garethevans.church.opensongtablet.songprocessing;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.InputStream;

public class ConvertWord {

    // Try to convert content of Word douments into text

    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "ConvertWord";
    private final MainActivityInterface mainActivityInterface;

    public ConvertWord(Context c) {
        mainActivityInterface = (MainActivityInterface) c;
    }

    public String convertDocxToText(Uri uri, String filename) {
        // Because we need to have a file rather than a uri, make a temp version
        String text = "";
        try {
            if (uri!=null && filename!=null) {
                DocumentConverter converter = new DocumentConverter();

                Result<String> resultHTML = null;
                // We could just extract text, but using HTML we can get headings
                try (InputStream inputStream1 = mainActivityInterface.getStorageAccess().getInputStream(uri)) {
                    resultHTML = converter.convertToHtml(inputStream1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG,"resultHTML:"+resultHTML);
                if (resultHTML!=null) {
                    text = resultHTML.getValue();
                    text = text.replace("<h1>", "[").replace("</h1>", "]");
                    text = text.replace("<h2>", "[").replace("</h2>", "]");
                    text = text.replace("<h3>", "[").replace("</h3>", "]");
                    text = text.replace("<p>", "\n").replace("</p>", "\n");
                    text = text.replace("<br>", "\n").replace("</br>", "\n");

                    text = mainActivityInterface.getProcessSong().removeHTMLTags(text);
                    String[] lines = text.split("\n");
                    StringBuilder stringBuilder = new StringBuilder();

                    // Firstly just trim out blank spaces from lines and add them back up
                    for (String line : lines) {
                        line = line.trim();
                        stringBuilder.append(line).append("\n");
                    }

                    // Next remove multiple line breaks and replace with blank section headers
                    text = stringBuilder.toString().replace("\n\n\n","[ ]");
                    text = stringBuilder.toString().replace("\n\n","\n");
                    Log.d(TAG,"text:"+text);
                    text = mainActivityInterface.getConvertTextSong().convertText(text);
                } else {
                    text = "";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mainActivityInterface.getStorageAccess().updateFileActivityLog(e.toString());
        }
        return text;
    }
}
