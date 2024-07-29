package com.garethevans.church.opensongtablet.bible;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.customviews.ExposedDropDown;
import com.garethevans.church.opensongtablet.customviews.ExposedDropDownArrayAdapter;
import com.garethevans.church.opensongtablet.databinding.BottomSheetBibleOfflineBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class BibleOfflineBottomSheet extends BottomSheetDialogFragment {

    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "BibleOfflineBottom";

    private MainActivityInterface mainActivityInterface;
    private BottomSheetBibleOfflineBinding myView;
    private Bible bible;
    private String bibleText = "", website_bible_offline_string="", scripture_string="",
            added_to_set_string="", mode_presenter_string;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            FrameLayout bottomSheet = ((BottomSheetDialog) dialog1).findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setDraggable(false);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = BottomSheetBibleOfflineBinding.inflate(inflater, container, false);

        prepareStrings();

        myView.dialogHeader.setClose(this);
        myView.dialogHeader.setWebHelp(mainActivityInterface,website_bible_offline_string);

        // Set up helpers()
        setupHelpers();

        // Set up the views
        setupViews();

        // Set the listeners
        setupListeners();

        // Initialise the exposed dropdown for the file
        // Other dropdowns become available once others are chosen
        // Most heavy lifting is done by the bible class
        initialiseBible();

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            website_bible_offline_string = getString(R.string.website_bible_offline);
            scripture_string = getString(R.string.scripture);
            added_to_set_string = getString(R.string.added_to_set);
            mode_presenter_string = getString(R.string.mode_presenter);
        }
    }
    private void setupHelpers() {
        bible = mainActivityInterface.getBible();
    }

    private void setupViews() {
        mainActivityInterface.getProcessSong().editBoxToMultiline(myView.content);
        myView.lineLength.setValue(bible.getLineLength());
        myView.lineLength.setHint(String.valueOf(bible.getLineLength()));
        myView.linesPerSlide.setValue(bible.getLinesPerSlide());
        myView.linesPerSlide.setHint(String.valueOf(bible.getLinesPerSlide()));
        myView.verseNumbers.setChecked(bible.getShowVerseNumbers());
    }

    private void setupListeners() {
        myView.bible.addTextChangedListener(new MyTextWatcher("bible"));
        myView.book.addTextChangedListener(new MyTextWatcher("book"));
        myView.chapter.addTextChangedListener(new MyTextWatcher("chapter"));
        myView.verseFrom.addTextChangedListener(new MyTextWatcher("verseFrom"));
        myView.verseTo.addTextChangedListener(new MyTextWatcher("verseTo"));
        myView.verseNumbers.setOnCheckedChangeListener((compoundButton, b) -> {
            bible.setShowVerseNumbers(b);
            stretchText();
            if (myView.content.getText()==null || myView.content.getText().toString().isEmpty()) {
                myView.addToSet.setVisibility(View.GONE);
            } else {
                myView.addToSet.setVisibility(View.VISIBLE);
            }
        });
        myView.lineLength.addOnChangeListener((slider, value, fromUser) -> {
            bible.setLineLength((int)value);
            myView.lineLength.setHint(String.valueOf((int)value));
            stretchText();
        });
        myView.linesPerSlide.addOnChangeListener((slider, value, fromUser) -> {
            bible.setLinesPerSlide((int)value);
            myView.linesPerSlide.setHint(String.valueOf((int)value));
            stretchText();
        });
        myView.addToSet.setOnClickListener(v -> {
            // Build an array list to add to a custom slide
            ArrayList<String> scripture = new ArrayList<>();
            // The first item identifies the arraylist as a scripture
            scripture.add("scripture");
            // Now add the title
            String verse;
            if (bible.getBibleVerseFrom().equals(bible.getBibleVerseTo())) {
                verse = bible.getBibleVerseFrom();
            } else {
                verse = bible.getBibleVerseFrom() + "-" + bible.getBibleVerseTo();
            }
            scripture.add(bible.getBibleBook() + " " + bible.getBibleChapter() + ":" + verse);
            // Now the text
            scripture.add(myView.content.getText().toString());
            // Now the translation
            scripture.add(bible.getTranslation());

            // Add to the set
            mainActivityInterface.getCustomSlide().buildCustomSlide(scripture);
            mainActivityInterface.getCustomSlide().addItemToSet(false);
            mainActivityInterface.getShowToast().doItBottomSheet(scripture_string+" "+added_to_set_string,myView.getRoot());
            if (!mainActivityInterface.getMode().equals(mode_presenter_string)) {
                mainActivityInterface.navHome();
            }
            dismiss();
        });
    }

    private void initialiseBible() {
        mainActivityInterface.getThreadPoolExecutor().execute(() -> {
            bible.buildDefaultBibleBooks();
            mainActivityInterface.getMainHandler().post(() -> myView.progressBar.setVisibility(View.VISIBLE));
            bible.buildBibleFiles();
            mainActivityInterface.getMainHandler().post(() -> {
                updateExposedDropDown(myView.bible, bible.getBibleFiles(), bible.getBibleFile());
                myView.progressBar.setVisibility(View.GONE);
            });
        });
    }

    private class MyTextWatcher implements TextWatcher {
        private final String which;

        MyTextWatcher(String which) {
            this.which = which;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (which) {
                case "bible":
                    mainActivityInterface.getThreadPoolExecutor().execute(() -> {
                        String chosen = getTextFromView(myView.bible);
                        if (chosen !=null && !chosen.isEmpty()) {
                            mainActivityInterface.getMainHandler().post(() -> myView.progressBar.setVisibility(View.VISIBLE));
                            bible.setBibleFile(chosen);
                            bible.buildBibleBooks();
                            mainActivityInterface.getMainHandler().post(() -> {
                                updateExposedDropDown(myView.book,bible.getBibleBooks(), bible.getBibleBook());
                                myView.progressBar.setVisibility(View.GONE);
                            });
                        }
                    });
                    break;
                case "book":
                    mainActivityInterface.getThreadPoolExecutor().execute(() -> {
                        String chosen = getTextFromView(myView.book);
                        if (chosen !=null && !chosen.isEmpty()) {
                            mainActivityInterface.getMainHandler().post(() -> myView.progressBar.setVisibility(View.VISIBLE));
                            bible.setBibleBook(chosen);
                            bible.setBibleChapter("1");
                            bible.setBibleVerseFrom("1");
                            bible.setBibleVerseTo("1");
                            bible.buildBibleChapters();
                            mainActivityInterface.getMainHandler().post(() -> {
                                updateExposedDropDown(myView.chapter,bible.getBibleChapters(), bible.getBibleChapter());
                                myView.progressBar.setVisibility(View.GONE);
                            });
                        }
                    });
                    break;
                case "chapter":
                    mainActivityInterface.getThreadPoolExecutor().execute(() -> {
                        String chosen = getTextFromView(myView.chapter);
                        if (chosen !=null && !chosen.isEmpty()) {
                            mainActivityInterface.getMainHandler().post(() -> myView.progressBar.setVisibility(View.VISIBLE));
                            bible.setBibleChapter(chosen);
                            bible.setBibleVerseFrom("1");
                            bible.setBibleVerseTo("1");
                            bible.buildBibleVerses();
                            mainActivityInterface.getMainHandler().post(() -> {
                                updateExposedDropDown(myView.verseFrom,bible.getBibleVerses(), bible.getBibleVerseFrom());
                                updateExposedDropDown(myView.verseTo,bible.getBibleVerses(), bible.getBibleVerseTo());
                                myView.verseTo.setText("1");
                                myView.progressBar.setVisibility(View.GONE);
                            });
                        }
                    });
                    break;
                case "verseFrom":
                    mainActivityInterface.getThreadPoolExecutor().execute(() -> {
                        String chosen = getTextFromView(myView.verseFrom);
                        if (chosen !=null && !chosen.isEmpty()) {
                            mainActivityInterface.getMainHandler().post(() -> myView.progressBar.setVisibility(View.VISIBLE));
                            bible.setBibleVerseFrom(chosen);
                            mainActivityInterface.getMainHandler().post(() -> {
                                // Check the 'to' value is the same or bigger than this
                                if (myView.verseTo.getText()!=null && (myView.verseTo.getText().toString().isEmpty() || rangeIsIncorrect(myView.verseFrom, chosen, false))) {
                                    myView.verseTo.setText(chosen);
                                }
                                bibleText = bible.getBibleText();
                                stretchText();
                                if (myView.content.getText()==null || myView.content.getText().toString().isEmpty()) {
                                    myView.addToSet.setVisibility(View.GONE);
                                } else {
                                    myView.addToSet.setVisibility(View.VISIBLE);
                                }
                                myView.progressBar.setVisibility(View.GONE);
                            });
                        }
                    });
                    break;
                case "verseTo":
                    mainActivityInterface.getThreadPoolExecutor().execute(() -> {
                        String chosen = getTextFromView(myView.verseTo);
                        if (chosen !=null && !chosen.isEmpty()) {
                            mainActivityInterface.getMainHandler().post(() -> myView.progressBar.setVisibility(View.VISIBLE));
                            bible.setBibleVerseTo(chosen);
                            mainActivityInterface.getMainHandler().post(() -> {
                                // Check the 'from' value is the same or smaller than this
                                if (myView.verseFrom.getText()!=null && (myView.verseFrom.getText().toString().isEmpty() || rangeIsIncorrect(myView.verseFrom, chosen, true))) {
                                    myView.verseFrom.setText(chosen);
                                }
                                bibleText = bible.getBibleText();
                                stretchText();
                                if (myView.content.getText()==null || myView.content.getText().toString().isEmpty()) {
                                    myView.addToSet.setVisibility(View.GONE);
                                } else {
                                    myView.addToSet.setVisibility(View.VISIBLE);
                                }
                                myView.progressBar.setVisibility(View.GONE);
                            });
                        }
                    });
                    break;
            }
        }
    }

    private String getTextFromView(ExposedDropDown exposedDropDown) {
        if (exposedDropDown.getText()!=null) {
            try {
                return exposedDropDown.getText().toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private void updateExposedDropDown(ExposedDropDown exposedDropDown, ArrayList<String> values, String defaultValue) {
        exposedDropDown.setAdapter(null);
        if (getContext()!=null) {
            ExposedDropDownArrayAdapter exposedDropDownArrayAdapter = new ExposedDropDownArrayAdapter(getContext(), exposedDropDown, R.layout.view_exposed_dropdown_item, values);
            exposedDropDown.setAdapter(exposedDropDownArrayAdapter);
        }
        if (defaultValue!=null && !defaultValue.isEmpty()) {
            exposedDropDown.setText(defaultValue);
        }
    }

    private boolean rangeIsIncorrect(ExposedDropDown exposedDropDown, String chosen, boolean isGreaterThan) {
        int value = 1;
        if (exposedDropDown.getText()!=null) {
            try {
                value = Integer.parseInt(myView.verseTo.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isGreaterThan) {
            return value > getIntFromText(chosen);
        } else {
            return value < getIntFromText(chosen);
        }
    }
    private int getIntFromText(String text) {
        if (text!=null) {
            try {
                return Integer.parseInt(text);
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
        } else {
            return 1;
        }
    }

    private void stretchText() {
        mainActivityInterface.getProcessSong().splitTextByMaxChars(myView.content,bibleText,bible.getLineLength(),bible.getLinesPerSlide(), bible.getShowVerseNumbers());
    }
}
