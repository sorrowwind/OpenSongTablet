package com.garethevans.church.opensongtablet.screensetup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.SettingsFontsPreviewBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

import java.util.ArrayList;

public class FontSetupPreviewFragment extends DialogFragment {

    private SettingsFontsPreviewBinding myView;
    private MainActivityInterface mainActivityInterface;

    private String sampleText, font_browse_string="", lorem_string="", deeplink_fonts_string="";
    private ArrayList<String> fontNames, alphaList;
    private final Handler handler = new Handler();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.updateToolbar(font_browse_string);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = SettingsFontsPreviewBinding.inflate(inflater,container,false);

        prepareStrings();

        sampleText = lorem_string;

        // Run the webview setup
        myView.webView.getSettings().setJavaScriptEnabled(true);
        myView.webView.getSettings().setDomStorageEnabled(true);
        myView.webView.setWebChromeClient(new WebChromeClient());
        myView.webView.addJavascriptInterface(new WebAppInterface(), "Android");
        setupWebView("a");

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            font_browse_string = getString(R.string.font_browse);
            lorem_string = getString(R.string.lorem);
            deeplink_fonts_string = getString(R.string.deeplink_fonts);
        }
    }

    private void setupWebView(String ab) {
        mainActivityInterface.getThreadPoolExecutor().execute(() -> {
            if (fontNames==null || fontNames.isEmpty()) {
                fontNames = mainActivityInterface.getMyFonts().getFontsFromGoogle();
                getAlphaList();
                handler.post(this::prepareAlphaList);
            }
            String content = preparePageContent(ab);
            handler.post(() -> prepareWebView(content));
        });
    }

    public void prepareWebView(String content) {
        if (myView != null && content != null) {
            myView.webView.loadDataWithBaseURL("", content, "text/html", "utf-8", null);
        }
    }

    public void prepareAlphaList() {
        if (myView!=null) {
            myView.sideIndex.removeAllViews();
            for (String ab : alphaList) {
                TextView textView = new TextView(getContext());
                textView.setTextSize(20.0f);
                textView.setText(ab);
                textView.setPadding(32, 32, 32, 32);
                textView.setOnClickListener(v -> setupWebView(ab));
                myView.sideIndex.addView(textView);
            }
        }
    }

    public class WebAppInterface {

        @JavascriptInterface
        public void processReturnValue(String fontname) {
            doSave(fontname);
        }
    }

    private void getAlphaList() {
        // Go through the list and get the unique alphabetical index
        alphaList = new ArrayList<>();
        for (String fontName:fontNames) {
            if (fontName!=null && !fontName.isEmpty() &&
                    !alphaList.contains(fontName.substring(0,1).toUpperCase(mainActivityInterface.getLocale()))) {
                alphaList.add(fontName.substring(0,1).toUpperCase(mainActivityInterface.getLocale()));
            }
        }
    }

    // Prepare the webpage based on alphabetical choices (button)
    private String preparePageContent(String ab) {

        StringBuilder links = new StringBuilder();
        StringBuilder rows = getStringBuilder(ab, links);

        return "<html>\n<head>\n" +
                "<script>\nfunction getMyFont(fnt) {\n" +
                "Android.processReturnValue(fnt);\n}\n</script>\n" +
                links + "\n" +
                "<style>\n" +
                "td {padding: 10px; text-align: left; border-bottom: 1px solid #ddd; font-size:18pt;}\n" +
                "tr:nth-child(even) {background-color: #f2f2f2;}\n" +
                "</style>" +
                "</head>\n<body>\n" +
                "<table>" + rows + "</table></body></html>";
    }

    private StringBuilder getStringBuilder(String ab, StringBuilder links) {
        StringBuilder rows = new StringBuilder();

        for (String fontName:fontNames) {
            if (fontName.toLowerCase(mainActivityInterface.getLocale()).startsWith(ab.toLowerCase(mainActivityInterface.getLocale()))) {
                links.append("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css2?family=")
                        .append(fontName.replaceAll("\\s", "+")).append("\">\n");
                rows.append("<tr onclick=\"getMyFont('").append(fontName.replaceAll("\\s", "+"))
                        .append("')\"><td>").append(fontName).append("</td><td style=\"font-family:'")
                        .append(fontName).append("';\">")
                        .append(sampleText).append("</td></tr>\n");
            }
        }
        return rows;
    }

    private void doSave(String fontName) {
        fontName = fontName.replace("+"," ");
        mainActivityInterface.getMyFonts().changeFont(mainActivityInterface.getWhattodo(),fontName,handler);
        mainActivityInterface.getThreadPoolExecutor().execute(() -> mainActivityInterface.getMainHandler().post(() -> {
            mainActivityInterface.popTheBackStack(R.id.fontSetupFragment, true);
            mainActivityInterface.navigateToFragment(deeplink_fonts_string, 0);
            dismiss();
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myView = null;
    }
}