package com.garethevans.church.opensongtablet.screensetup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.garethevans.church.opensongtablet.R;
import com.google.android.material.textview.MaterialTextView;

public class ShowToast {

    private final View anchor;
    private final String TAG = "ShowToast";
    private final PopupWindow popupWindow;
    private final MaterialTextView textToast;
    private Handler handlerShow;
    private Handler handlerHide;
    private Runnable runnableShow;
    private long messageEndTime = 0;
    private Runnable runnableHide = new Runnable() {
        @Override
        public void run() {
            try {
                popupWindow.dismiss();
            } catch (Exception e) {
                Log.d(TAG,"Couldn't dismiss popupWindow");
            }
        }
    };

    public ShowToast(Context c, View anchor) {
        this.anchor = anchor;
        popupWindow = new PopupWindow(c);
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.view_toast,null,false);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(false);
        popupWindow.setBackgroundDrawable(null);
        textToast = view.findViewById(R.id.textToast);
    }

    public void doIt(final String message) {
        try {
            if (message != null && !message.isEmpty()) {
                // Toasts with custom layouts are deprecated and look ugly!
                // Use a more customisable popup window

                // If a message is already showing, then wait
                long delayTime;
                long currTime = System.currentTimeMillis();
                if (currTime > messageEndTime) {
                    // Good to go now
                    delayTime = 0;
                    messageEndTime = currTime + 2000;
                } else {
                    delayTime = messageEndTime - currTime + 500;
                }

                runnableShow = () -> {
                    if (textToast!=null && popupWindow!=null) {
                        try {
                            textToast.setText(message);
                            popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);
                            messageEndTime = System.currentTimeMillis() + 2000;
                            handlerHide = new Handler(Looper.getMainLooper());
                            handlerHide.postDelayed(runnableHide, 2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handlerShow = new Handler(Looper.getMainLooper());
                handlerShow.postDelayed(runnableShow, delayTime);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        if (handlerShow!=null) {
            handlerShow.removeCallbacks(runnableShow);
        }
        runnableShow = null;
        if (handlerHide!=null) {
            handlerHide.removeCallbacks(runnableHide);
        }
        runnableHide = null;
    }
}
