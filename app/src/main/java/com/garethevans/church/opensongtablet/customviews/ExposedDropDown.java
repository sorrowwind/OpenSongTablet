package com.garethevans.church.opensongtablet.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;

public class ExposedDropDown extends TextInputLayout {

    private AutoCompleteTextView autoCompleteTextView;
    private TextInputLayout textInputLayout;
    private final String TAG = "ExposedDropDown";
    private Context c;
    private final int delay = 50;
    private boolean largePopups;
    private ArrayList<String> arrayList = null;

    @SuppressLint("ClickableViewAccessibility")
    public ExposedDropDown(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.c = context;

        inflate(context, R.layout.view_exposed_dropdown,this);

        identifyViews();

        int[] set = new int[]{android.R.attr.text, android.R.attr.hint};
        TypedArray a = context.obtainStyledAttributes(attrs, set);

        CharSequence text = a.getText(0);
        CharSequence hint = a.getText(1);

        autoCompleteTextView.setSingleLine(true);
        // The popup background is set in styles, but it seems to require programmatic setting!
        autoCompleteTextView.setDropDownBackgroundResource(R.drawable.popup_bg);
        if (text!=null) {
            autoCompleteTextView.setText(text);
        }
        if (hint!=null) {
            textInputLayout.setHint(hint);
        }
        textInputLayout.setBoxBackgroundColor(getResources().getColor(R.color.transparent));
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setPadding(0,0,0,0);
        a.recycle();
        autoCompleteTextView.setOnTouchListener(new MyTouchListener());
        textInputLayout.setEndIconOnClickListener(v -> {
            autoCompleteTextView.post(() -> {
            setPopupSize();
            if (autoCompleteTextView.isPopupShowing()) {
                autoCompleteTextView.dismissDropDown();
            } else {
                autoCompleteTextView.dismissDropDown();
                // Delay the showing..
                autoCompleteTextView.postDelayed(() -> {
                    autoCompleteTextView.showDropDown();
                    keepPosition();
                },delay);
            }
            });
        });
    }

    private class MyTouchListener implements OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction()==MotionEvent.ACTION_DOWN ||
            event.getAction()==MotionEvent.ACTION_BUTTON_PRESS) {
                setPopupSize();
            } else if (event.getAction()==MotionEvent.ACTION_UP ||
            event.getAction()==MotionEvent.ACTION_BUTTON_RELEASE) {
                autoCompleteTextView.post(() -> {
                    if (autoCompleteTextView.isPopupShowing()) {
                        autoCompleteTextView.dismissDropDown();
                    } else {
                        autoCompleteTextView.dismissDropDown();
                        // Delay the showing..
                        autoCompleteTextView.postDelayed(() -> {
                            autoCompleteTextView.showDropDown();
                            keepPosition();
                        },delay);
                    }
                });
            }
            return true;
        }
    }

    private void keepPosition() {
        Log.d(TAG,"keepPosition()   arrayList:"+arrayList);
        if (arrayList!=null && arrayList.size()>0) {
            Log.d(TAG,"arrayList.size():"+arrayList.size());
            String selectedValue = getText().toString();
            int position = arrayList.indexOf(selectedValue);
            Log.d(TAG,"selectedValue:"+selectedValue+"  position:"+position);
            autoCompleteTextView.setListSelection(arrayList.indexOf(getText().toString()));
        }
    }

    private void identifyViews() {
        autoCompleteTextView = findViewById(R.id.textView);
        textInputLayout = findViewById(R.id.textLayout);

        autoCompleteTextView.setId(View.generateViewId());
        textInputLayout.setId(View.generateViewId());
    }

    public Editable getText() {
        // Check for null
        if (autoCompleteTextView.getText()==null) {
            autoCompleteTextView.setText("");
        }
        return autoCompleteTextView.getText();
    }
    public CharSequence getHint() {
        if (textInputLayout.getHint()==null) {
            textInputLayout.setHint("");
        }
        return textInputLayout.getHint();
    }

    public void setText(String text) {
        autoCompleteTextView.setText(text);
        autoCompleteTextView.clearFocus();
    }
    public void setHint(String hint) {
        textInputLayout.setHint(hint);
        textInputLayout.clearFocus();
    }
    public void setAdapter(ExposedDropDownArrayAdapter arrayAdapter) {
        autoCompleteTextView.setAdapter(arrayAdapter);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        autoCompleteTextView.addTextChangedListener(textWatcher);
    }

    public void setArray(Context c, String[] stringArray) {
        this.c = c;
        largePopups = ((MainActivityInterface) c).getPreferences().getMyPreferenceBoolean("largePopups",true);
        arrayList = new ArrayList<>();
        Collections.addAll(arrayList, stringArray);
    }

    public void setArray(Context c, ArrayList<String> objects) {
        this.c = c;
        largePopups = ((MainActivityInterface) c).getPreferences().getMyPreferenceBoolean("largePopups",true);
        arrayList = objects;
    }

    public void setPopupSize() {
        MainActivityInterface mainActivityInterface = (MainActivityInterface) c;
        if (largePopups) {
            try {
                if (autoCompleteTextView != null) {
                    // Get the location of the popup position and negatively offset this minus the toolbar height
                    int[] location = new int[2];
                    autoCompleteTextView.getLocationOnScreen(location);
                    int height = mainActivityInterface.getDisplayMetrics()[1] - (mainActivityInterface.getToolbar().getActionBarHeight(false) * 2);
                    int y = location[1];
                    autoCompleteTextView.setDropDownVerticalOffset(-y);
                    autoCompleteTextView.setDropDownHeight(height);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                autoCompleteTextView.setDropDownVerticalOffset(-autoCompleteTextView.getHeight());
                int newHeight = (int) ((float) getContext().getResources().getDimension(R.dimen.exposed_dropdown_height));
                autoCompleteTextView.setDropDownHeight(newHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
