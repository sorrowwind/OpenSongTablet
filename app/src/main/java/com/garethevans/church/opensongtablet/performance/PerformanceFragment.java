package com.garethevans.church.opensongtablet.performance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.abcnotation.ABCPopup;
import com.garethevans.church.opensongtablet.appdata.AlertInfoBottomSheet;
import com.garethevans.church.opensongtablet.controls.GestureListener;
import com.garethevans.church.opensongtablet.customslides.ImageSlideAdapter;
import com.garethevans.church.opensongtablet.customviews.RecyclerLayoutManager;
import com.garethevans.church.opensongtablet.databinding.ModePerformanceBinding;
import com.garethevans.church.opensongtablet.interfaces.ActionInterface;
import com.garethevans.church.opensongtablet.interfaces.DisplayInterface;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.nearby.NearbyAlertPopUp;
import com.garethevans.church.opensongtablet.pdf.PDFPageAdapter;
import com.garethevans.church.opensongtablet.songprocessing.Song;
import com.garethevans.church.opensongtablet.stage.StageSectionAdapter;
import com.garethevans.church.opensongtablet.stickynotes.StickyPopUp;

public class PerformanceFragment extends Fragment {

    @SuppressWarnings({"FieldCanBeLocal","unused"})
    private final String TAG = "PerformanceFragment";
    private StickyPopUp stickyPopUp;
    private NearbyAlertPopUp nearbyAlertPopUp;
    private ABCPopup abcPopup;
    private Bitmap.Config bmpFormat = Bitmap.Config.ARGB_8888;
    private MainActivityInterface mainActivityInterface;
    private ActionInterface actionInterface;
    private DisplayInterface displayInterface;
    private int swipeMinimumDistance;
    private int swipeMaxDistanceYError;
    private int swipeMinimumVelocity;
    private int availableWidth;
    private int availableHeight;
    private int widthBeforeScale;
    private int heightBeforeScale;
    private long doSongLoadStartTime;
    private final long doSongLoadQOSTime = 200;
    private int widthAfterScale;
    private int heightAfterScale;
    private boolean processingTestView;
    private boolean songChange;
    private boolean firstSongLoad = true;
    private boolean metronomeWasRunning;
    private float scaleFactor = 1.0f;
    private ModePerformanceBinding myView;
    private Animation animSlideIn, animSlideOut;
    private GestureDetector gestureDetector;
    private PDFPageAdapter pdfPageAdapter;
    private ImageSlideAdapter imageSlideAdapter;
    private StageSectionAdapter stageSectionAdapter;
    private RecyclerLayoutManager recyclerLayoutManager;
    private final Handler dealWithExtraStuffOnceSettledHandler = new Handler();
    private final Runnable dealWithExtraStuffOnceSettledRunnable = this::dealWithExtraStuffOnceSettled;

    private String mainfoldername="", mode_performance="", mode_presenter="", mode_stage="",
            not_allowed="", image_string="", nearby_large_file_string="", inline_set_string="";
    private int sendSongDelay = 0;
    @SuppressWarnings("FieldCanBeLocal")
    // GE - hidden this option, but reserving the right to reinstate even just for me
    private final int graceTime = 2000;
    private final Handler sendSongAfterDelayHandler = new Handler(),
        autoHideHighlighterHandler = new Handler();
    private final Runnable sendSongAfterDelayRunnable = () -> {
        // IV - The send is always called by the 'if' and will return true if a large file has been sent
        if (mainActivityInterface.getNearbyConnections().sendSongPayload()) {
            mainActivityInterface.getShowToast().doIt(nearby_large_file_string);
        }
        sendSongDelay = 3000;
    };
    private final Runnable autoHideHighlighterRunnable = new Runnable() {
        @Override
        public void run() {
            if (myView!=null) {
                myView.highlighterView.setVisibility(View.GONE);
            }
        }
    };

    private final Handler resetSendSongAfterDelayHandler = new Handler();
    private final Runnable resetSendSongAfterDelayRunnable = () -> {
        sendSongDelay = 0;
        mainActivityInterface.getNearbyConnections().setSendSongDelayActive(false);
    };

    // Attaching and destroying
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
        actionInterface = (ActionInterface) context;
        displayInterface = (DisplayInterface) context;
        mainActivityInterface.registerFragment(this, "Performance");
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareStrings();
        firstSongLoad = true;
        updateInlineSetSortTitles();
        displayInterface.checkDisplays();
        if (myView!=null && getContext()!=null) {
            myView.inlineSetList.initialisePreferences(getContext(), mainActivityInterface);
            updateInlineSetVisibility();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dealWithStickyNotes(false,true);
        if (mainActivityInterface.getToolbar()!=null) {
            mainActivityInterface.getToolbar().setPerformanceMode(false);
        }
        mainActivityInterface.registerFragment(null,"Performance");
    }
    @Override
    public void onDestroyView() {
        try {
            stickyPopUp.closeSticky();
            actionInterface.showSticky(false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            nearbyAlertPopUp.closeSticky();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            stickyPopUp.destroyPopup();
            nearbyAlertPopUp.destroyPopup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        myView = null;
        stickyPopUp = null;
        nearbyAlertPopUp = null;
        super.onDestroyView();
    }

    // The logic to start this fragment
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        myView = ModePerformanceBinding.inflate(inflater, container, false);
        prepareStrings();

        // Register this fragment
        mainActivityInterface.registerFragment(this,"Performance");
        mainActivityInterface.updateFragment("updateSongMenuSortTitles",this,null);

        // Initialise the helper classes that do the heavy lifting
        initialiseHelpers();

        // Initialise the recyclerview
        if (recyclerLayoutManager==null && getContext()!=null) {
            recyclerLayoutManager = new RecyclerLayoutManager(getContext());
            myView.recyclerView.setLayoutManager(recyclerLayoutManager);
        }
        myView.recyclerView.setItemAnimator(null);

        // Pass view references to the Autoscroll class
        mainActivityInterface.getAutoscroll().initialiseAutoscroll(myView.zoomLayout, myView.recyclerView);

        // Allow the song menu and page buttons
        mainActivityInterface.lockDrawer(false);
        mainActivityInterface.hideActionButton(false);
        //mainActivityInterface.expandActionButton();

        // Load in preferences
        loadPreferences();

        // Prepare the song menu (will be called again after indexing from the main activity index songs)
        if (mainActivityInterface.getSongListBuildIndex().getIndexRequired() &&
                !mainActivityInterface.getSongListBuildIndex().getCurrentlyIndexing()) {
            mainActivityInterface.fullIndex();
        }

        // Set listeners for the scroll/scale/gestures
        setGestureListeners();

        // Show the actionBar and hide it after a time if that's the user's preference
        mainActivityInterface.getToolbar().setPerformanceMode(true);
        mainActivityInterface.showActionBar();

        // MainActivity initialisation has firstRun set as true.
        // Check for connected displays now we have loaded preferences, etc
        if (mainActivityInterface.getFirstRun()) {
            // IV - Make sure second screen overlays are off
            mainActivityInterface.getPresenterSettings().setBlankscreenOn(false);
            displayInterface.updateDisplay("showBlankscreen");
            mainActivityInterface.getPresenterSettings().setBlackscreenOn(false);
            displayInterface.updateDisplay("showBlackscreen");
            displayInterface.checkDisplays();
            mainActivityInterface.setFirstRun(false);
        }

        // Tint the watermark to the text colour
        myView.waterMark.setColorFilter(mainActivityInterface.getMyThemeColors().getLyricsTextColor(),
                android.graphics.PorterDuff.Mode.SRC_IN);

        removeViews();

        if (mainActivityInterface.getWhattodo().equals("pendingLoadSet") &&
            mainActivityInterface.getCurrentSet().getCurrentSetSize()>0) {
            mainActivityInterface.setWhattodo("");
            // If we have chosen to open the first item (preference), do that, otherwise, look for the current song
            if (mainActivityInterface.getPreferences().getMyPreferenceBoolean("setLoadFirst",true)) {
                mainActivityInterface.getCurrentSet().setIndexSongInSet(0);
                mainActivityInterface.loadSongFromSet(0);
            } else {
                processingTestView = false;
                mainActivityInterface.loadSongFromSet(Math.max(mainActivityInterface.getCurrentSet().getIndexSongInSet(), 0));

            }
        } else {
            processingTestView = false;
            String songFolder = mainActivityInterface.getPreferences().getMyPreferenceString("songFolder",mainfoldername);
            String songFilename = mainActivityInterface.getPreferences().getMyPreferenceString("songFilename", "Welcome to OpenSongApp");
            mainActivityInterface.getSetActions().indexSongInSet(songFolder,songFilename,null);
            doSongLoad(songFolder, songFilename);
        }

        // Check if we need to show an alert
        if (mainActivityInterface.getAlertChecks().showPlayServicesAlert() ||
                mainActivityInterface.getAlertChecks().showBackup() ||
                mainActivityInterface.getAlertChecks().showUpdateInfo() ||
                mainActivityInterface.getAlertChecks().showBadSongMoved()) {
                    AlertInfoBottomSheet alertInfoBottomSheet = new AlertInfoBottomSheet();
                    alertInfoBottomSheet.show(mainActivityInterface.getMyFragmentManager(), "AlertInfoBottomSheet");
                    // When we close the alert, we check again
        }

        // Pass a reference of the zoom layout to the next/prev so we can stop fling scrolls
        mainActivityInterface.getDisplayPrevNext().setZoomLayout(myView.zoomLayout);

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            mainfoldername = getString(R.string.mainfoldername);
            mode_performance = getString(R.string.mode_performance);
            mode_presenter = getString(R.string.mode_presenter);
            mode_stage = getString(R.string.mode_stage);
            not_allowed = getString(R.string.not_allowed);
            image_string= getString(R.string.image);
            nearby_large_file_string = getString(R.string.nearby_large_file);
            inline_set_string = getString(R.string.set_inline_showcase);
        }
    }

    // Getting the preferences and helpers ready
    private void initialiseHelpers() {
        if (getContext() != null) {
            stickyPopUp = new StickyPopUp(getContext());
            nearbyAlertPopUp = new NearbyAlertPopUp(getContext());
            abcPopup = new ABCPopup(getContext());
            mainActivityInterface.getPerformanceGestures().setZoomLayout(myView.zoomLayout);
            mainActivityInterface.getPerformanceGestures().setRecyclerView(myView.recyclerView);
            //myView.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            myView.recyclerView.setLayoutManager(new RecyclerLayoutManager(getContext()));
            mainActivityInterface.getHotZones().initialiseHotZones(myView.hotZoneTopLeft, myView.hotZoneTopCenter, myView.hotZoneBottomCenter);
        }
    }
    private void loadPreferences() {
        mainActivityInterface.getProcessSong().updateProcessingPreferences();
        mainActivityInterface.getMyThemeColors().getDefaultColors();
        swipeMinimumDistance = mainActivityInterface.getPreferences().getMyPreferenceInt("swipeMinimumDistance", 250);
        swipeMaxDistanceYError = mainActivityInterface.getPreferences().getMyPreferenceInt("swipeMaxDistanceYError", 200);
        swipeMinimumVelocity = mainActivityInterface.getPreferences().getMyPreferenceInt("swipeMinimumVelocity", 600);
        if (mainActivityInterface.getMode().equals(mode_performance)) {
            myView.mypage.setBackgroundColor(mainActivityInterface.getMyThemeColors().getLyricsBackgroundColor());
            myView.waterMark.setVisibility(View.VISIBLE);
        } else if (mainActivityInterface.getMode().equals(mode_stage)) {
            // Stage Mode - sections have correct colour, but the background is different - set to background colour with a reduced alpha
            int newColor = mainActivityInterface.getMyThemeColors().adjustAlpha(mainActivityInterface.getMyThemeColors().getLyricsBackgroundColor(),0.9f);
            myView.mypage.setBackgroundColor(newColor);
        } else {
            // Presenter mode, just use primary color
            myView.mypage.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            myView.waterMark.setVisibility(View.GONE);
        }
        mainActivityInterface.updateOnScreenInfo("setpreferences");
        boolean allowPinchToZoom = mainActivityInterface.getPreferences().getMyPreferenceBoolean("allowPinchToZoom",true);
        myView.zoomLayout.setAllowPinchToZoom(allowPinchToZoom);
        myView.recyclerView.setAllowPinchToZoom(allowPinchToZoom);
        mainActivityInterface.getPresenterSettings().setLogoOn(false);
        displayInterface.updateDisplay("showLogo");
    }

    public void tryToImportIntent() {
        // We may have opened the app at this fragment by clicking on an openable file
        // Get the main activity to check and fix backstack to this as home if required
        mainActivityInterface.dealWithIntent(R.id.performanceFragment);
    }
    private void removeViews() {
        if (myView!=null) {
            try {
                mainActivityInterface.getSongSheetTitleLayout().removeAllViews();
                myView.songView.clearViews();
                myView.testPane.removeAllViews();
                myView.recyclerView.removeAllViews();
                myView.imageView.setImageDrawable(null);
                mainActivityInterface.setSectionViews(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Inline set
    public void orientationInlineSet(int orientation) {
        myView.inlineSetList.orientationChanged(orientation);
    }
    public void toggleInlineSet() {
        myView.inlineSetList.toggleInlineSet();
    }
    public void updateInlineSetVisibility() {
        if (myView!=null) {
            myView.inlineSetList.checkVisibility();
        }
    }
    public void notifyToClearInlineSet(int from, int count) {
        if (myView!=null) {
            myView.inlineSetList.notifyToClearInlineSet(from,count);
        }
    }
    public void notifyToInsertAllInlineSet() {
        if (myView!=null) {
            myView.inlineSetList.post(() -> myView.inlineSetList.notifyToInsertAllInlineSet());
        }
    }
    public void notifyInlineSetInserted() {
        if (myView!=null) {
            myView.inlineSetList.notifyInlineSetInserted();
        }
    }
    public void notifyInlineSetInserted(int position) {
        if (myView!=null) {
            try {
                myView.inlineSetList.notifyInlineSetInserted(position);
            } catch (Exception e) {
                Log.d(TAG, "Couldn't update inline set - might just not be shown currently");
            }
        }
    }
    public void notifyInlineSetRemoved(int position) {
        if (myView!=null) {
            myView.inlineSetList.notifyInlineSetRemoved(position);
        }
    }
    public void notifyInlineSetMove(int from, int to) {
        if (myView!=null) {
            try {
                myView.inlineSetList.notifyInlineSetMove(from, to);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void notifyInlineSetChanged(int position) {
        if (myView!=null) {
            mainActivityInterface.getMainHandler().post(() -> {
                if (myView!=null) {
                    myView.inlineSetList.notifyInlineSetChanged(position);
                    myView.inlineSetList.scrollToPosition(position);
                }
            });
        }
    }
    public void notifyInlineSetRangeChanged(int from, int count) {
        if (myView!=null) {
            myView.inlineSetList.notifyInlineSetRangeChanged(from,count);
        }
    }
    public void notifyInlineSetHighlight() {
        if (myView!=null) {
            try {
                myView.inlineSetList.notifyInlineSetHighlight();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void notifyInlineSetScrollToItem() {
        if (myView!=null) {
            myView.inlineSetList.notifyInlineSetScrollToItem();
        }
    }


    @SuppressWarnings("ConstantConditions")
    public void updateInlineSetSortTitles() {
        if (myView!=null && myView.inlineSetList!=null && myView.inlineSetList.getChildCount()<=0) {
            try {
                myView.inlineSetList.setUseTitle(mainActivityInterface.getPreferences().getMyPreferenceBoolean("songMenuSortTitles", true));
                myView.inlineSetList.post(() -> {
                    if (myView!=null) {
                        myView.inlineSetList.notifyInlineSetUpdated();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    // This stuff loads the song and prepares the views
    public void doSongLoad(String folder, String filename) {
        // IV - Set a boolean indicating song change
        songChange = !mainActivityInterface.getSong().getFilename().equals(filename) ||
                !mainActivityInterface.getSong().getFolder().equals(folder) ||
                firstSongLoad;
        mainActivityInterface.setHighlightChangeAllowed(true);

        boolean needToTryAgain = false;
        boolean needToPauseTryAgain = false;
        String keyInFilename;

        // If this is a key variation and it doesn't exist, we need to load the original
        if (mainActivityInterface.getVariations().getIsKeyVariation(folder,filename) &&
                !mainActivityInterface.getStorageAccess().uriExists(mainActivityInterface.getVariations().getKeyVariationUri(filename))) {
            String loadKey = mainActivityInterface.getSong().getKey();
            String[] newSongInfo = mainActivityInterface.getVariations().getPreVariationInfo(folder,filename,loadKey);
            folder = newSongInfo[0];
            filename = newSongInfo[1];
            keyInFilename = newSongInfo[2];
            mainActivityInterface.getSong().setKey(keyInFilename);
            // Try again and exit this first run
            needToTryAgain = true;
        }

        // If there is a delay processing the song, try again with a delay
        if (processingTestView && !needToTryAgain) {
            // Switch this off in 1 sec as there might have been a problem
            needToTryAgain = true;
            needToPauseTryAgain = true;
        }

        if (needToTryAgain) {
            String tryagainfolder = folder;
            String tryagainfilename = filename;
            firstSongLoad = true;
            if (needToPauseTryAgain) {
                mainActivityInterface.getMainHandler().postDelayed(() -> {
                    processingTestView = false;
                    doSongLoad(tryagainfolder, tryagainfilename);
                },1000);
            } else {
                mainActivityInterface.getMainHandler().post(() -> doSongLoad(tryagainfolder,tryagainfilename));
            }
            // End this run now
            return;
        }

        // We only load a song if there is a change of song file, or we manually force it, or receive from the host
        if (!processingTestView && myView!=null && (songChange || myView.inlineSetList.getForceReload()
                || mainActivityInterface.getTranspose().getForceReload() ||
            mainActivityInterface.getNearbyConnections().getForceReload()) || mainActivityInterface.getForceReload()) {

            // Clear any force reload flags
            firstSongLoad = false;
            mainActivityInterface.setForceReload(false);
            myView.inlineSetList.setForceReload(false);
            mainActivityInterface.getTranspose().setForceReload(false);
            mainActivityInterface.getNearbyConnections().setForceReload(false);

            mainActivityInterface.setHighlightChangeAllowed(false);

            try {
                doSongLoadStartTime = System.currentTimeMillis();
                mainActivityInterface.closeDrawer(true);
                mainActivityInterface.checkSetMenuItemHighlighted(mainActivityInterface.getCurrentSet().getPrevIndexSongInSet());

                // Make sure we only do this once (reset at the end of 'dealwithstuffafterready')
                if (!processingTestView) {
                    processingTestView = true;
                    // Loading the song is dealt with in this fragment as specific actions are required

                    // Remove capo
                    mainActivityInterface.updateOnScreenInfo("capoHide");

                    // IV - Deal with stop of metronome if we have changed song
                    metronomeWasRunning = mainActivityInterface.getMetronome().getIsRunning();
                    if (songChange && metronomeWasRunning) {
                        mainActivityInterface.getMetronome().stopMetronome();
                    }

                    // Stop any autoscroll if required, but not if it was activated
                    boolean autoScrollActivated = mainActivityInterface.getAutoscroll().getAutoscrollActivated();
                    mainActivityInterface.getAutoscroll().stopAutoscroll();
                    mainActivityInterface.getAutoscroll().setAutoscrollActivated(autoScrollActivated);

                    // Stop the highlighter autohide if required
                    autoHideHighlighterHandler.removeCallbacks(autoHideHighlighterRunnable);



                    String keyInSet = null;
                    boolean stillToCreateVariation;

                    if (mainActivityInterface.getCurrentSet().getIndexSongInSet()>-1) {
                        keyInSet = mainActivityInterface.getCurrentSet().getSetItemInfo(mainActivityInterface.getCurrentSet().getIndexSongInSet()).songkey;
                        // Compare with the indexed value
                        String keyInSong = mainActivityInterface.getSQLiteHelper().getKey(folder,filename);
                        if (keyInSong!=null && !keyInSong.isEmpty() && !keyInSong.equals(keyInSet)) {
                            // This is a key variation!
                            folder = mainActivityInterface.getVariations().getKeyVariationsFolder();
                            filename = mainActivityInterface.getVariations().getKeyVariationFilename(mainActivityInterface.getSong().getFolder(),mainActivityInterface.getSong().getFilename(),keyInSet);
                            // Check if the file needs to be created
                            Uri uriToCheck = mainActivityInterface.getVariations().getKeyVariationUri(filename);
                            stillToCreateVariation = !mainActivityInterface.getStorageAccess().uriExists(uriToCheck);
                        } else {
                            stillToCreateVariation = false;
                        }
                    } else {
                        stillToCreateVariation = false;
                    }

                    // During the load song call, the song is cleared
                    // However it first extracts the folder and filename we've just set
                    mainActivityInterface.getSong().setFolder(folder);
                    mainActivityInterface.getSong().setFilename(filename);

                    final boolean stillToCreateVariationFinal = stillToCreateVariation;
                    final String keyInSetFinal = keyInSet;
                    mainActivityInterface.getThreadPoolExecutor().execute(() -> {
                        // Prepare the slide out and in animations based on swipe direction
                        setupSlideOut();
                        setupSlideIn();

                        // Remove any sticky notes
                        actionInterface.showSticky(false, true);

                        // Now reset the song object (doesn't change what's already drawn on the screen)
                        Song songToUse;
                        if (stillToCreateVariationFinal) {
                            // If we are pointing to variation file check the file exists and if not, create it
                            boolean useExisting = true;
                            if (mainActivityInterface.getSong().getFolder().contains(mainActivityInterface.getVariations().getKeyVariationsFolder())) {
                                Uri uri = mainActivityInterface.getVariations().getKeyVariationUri(mainActivityInterface.getSong().getFilename());
                                if (!mainActivityInterface.getStorageAccess().uriExists(uri)) {
                                    String[] folderfilename = mainActivityInterface.getVariations().getPreVariationInfo(mainActivityInterface.getSong().getFolder(),mainActivityInterface.getSong().getFilename(),keyInSetFinal);
                                    mainActivityInterface.getSong().setFolder(folderfilename[0]);
                                    mainActivityInterface.getSong().setFilename(folderfilename[1]);
                                    useExisting = false;
                                }
                            }
                            if (useExisting) {
                                songToUse = mainActivityInterface.getLoadSong().doLoadSong(mainActivityInterface.getSong(), false);
                            } else {
                                songToUse = mainActivityInterface.getVariations().makeKeyVariation(mainActivityInterface.getLoadSong().doLoadSong(mainActivityInterface.getSong(), false), keyInSetFinal, true, true);
                            }
                        } else {
                            songToUse = mainActivityInterface.getLoadSong().doLoadSong(mainActivityInterface.getSong(),false);
                        }

                        // Set the main song with the loaded song, or the key variation song
                        mainActivityInterface.setSong(songToUse);

                        mainActivityInterface.getMainHandler().post(() -> {
                            if (myView != null) {
                                // Set the default color
                                myView.pageHolder.setBackgroundColor(mainActivityInterface.getMyThemeColors().getLyricsBackgroundColor());
                                // Update the toolbar with the song detail (null).
                                mainActivityInterface.updateToolbar(null);
                            }
                        });

                        // Clear any screenshot files
                        mainActivityInterface.setScreenshotFile(null);

                        // IV - Reset current values to 0
                        if (mainActivityInterface.getSong() != null &&
                                mainActivityInterface.getSong().getFiletype() != null &&
                                mainActivityInterface.getSong().getFiletype().equals("PDF")) {
                            mainActivityInterface.getSong().setPdfPageCurrent(0);
                        } else if (mainActivityInterface.getSong() != null) {
                            mainActivityInterface.getSong().setCurrentSection(0);
                            mainActivityInterface.getPresenterSettings().setCurrentSection(0);
                        }

                        if (mainActivityInterface.getNearbyConnections().hasValidConnections() &&
                                mainActivityInterface.getNearbyConnections().getIsHost()) {
                            // Only the first (with no delay) and last (with delay) of a long sequence of song changes is actually sent
                            // sendSongDelay will be 0 for the first song
                            // IV - Always empty then add to queue (known state)
                            mainActivityInterface.getNearbyConnections().setSendSongDelayActive(sendSongDelay != 0);
                            sendSongAfterDelayHandler.removeCallbacks(sendSongAfterDelayRunnable);
                            sendSongAfterDelayHandler.postDelayed(sendSongAfterDelayRunnable, sendSongDelay);
                            // IV - Always empty then add to queue (known state)
                            resetSendSongAfterDelayHandler.removeCallbacks(resetSendSongAfterDelayRunnable);
                            resetSendSongAfterDelayHandler.postDelayed(resetSendSongAfterDelayRunnable, 3500);
                        }

                        // Now slide out the song and after a delay start the next bit of the processing
                        if (myView != null) {
                            myView.recyclerView.post(() -> {
                                try {
                                    if (myView.recyclerView.getVisibility() == View.VISIBLE) {
                                        myView.recyclerView.startAnimation(animSlideOut);
                                    }
                                } catch (Exception e) {
                                    mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                                }
                            });
                            myView.pageHolder.post(() -> {
                                try {
                                    if (myView.pageHolder.getVisibility() == View.VISIBLE) {
                                        myView.pageHolder.startAnimation(animSlideOut);
                                    }
                                } catch (Exception e) {
                                    mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                                }
                            });
                        }
                        if (getContext() != null) {
                            // Scroll to the item in the set menus
                            mainActivityInterface.notifySetFragment("scrollTo", mainActivityInterface.getCurrentSet().getIndexSongInSet());

                            // Now continue with the song prep
                            mainActivityInterface.getMainHandler().postDelayed(this::prepareSongViews, 50 + getContext().getResources().getInteger(R.integer.slide_out_time));
                        }
                    });
                }

            } catch (Exception e) {
                mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
            }
        }
    }
    private void setupSlideOut() {
        // Set up the type of animate in
        if (mainActivityInterface.getDisplayPrevNext().getSwipeDirection().equals("R2L")) {
            animSlideOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
        } else {
            animSlideOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);
        }
    }
    private void setupSlideIn() {
        // Set up the type of animate in
        if (mainActivityInterface.getDisplayPrevNext().getSwipeDirection().equals("R2L")) {
            animSlideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
        } else {
            animSlideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
        }
    }
    private void prepareSongViews() {
        if (myView!=null) {
            // This is called on the UI thread above via the handler from mainLooper()
            // Reset the song views
            mainActivityInterface.setSectionViews(null);
            removeViews();

            // Make sure we are scrolled to the top of the views
            myView.recyclerView.scrollToTop();
            myView.zoomLayout.stopFlingScroll();
            myView.zoomLayout.doScrollTo(0,0);
            if (myView.recyclerView.getLayoutManager()!=null) {
                myView.recyclerView.getLayoutManager().scrollToPosition(0);
                try {
                    ((LinearLayoutManager) myView.recyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int[] screenSizes = mainActivityInterface.getDisplayMetrics();
            int screenWidth = screenSizes[0];
            int screenHeight = screenSizes[1];

            int[] viewPadding = mainActivityInterface.getViewMargins();

            availableWidth = screenWidth - viewPadding[0] - viewPadding[1] - myView.inlineSetList.getInlineSetWidth();
            availableHeight = screenHeight - viewPadding[2] - viewPadding[3];

            mainActivityInterface.setAvailableSizes(availableWidth,availableHeight);

            widthBeforeScale = 0;
            heightBeforeScale = 0;

            // Depending on what we are doing, create the content.
            // Options are
            // - PDF        PDF file. Use the recyclerView (not inside zoomLayout).  All sizes from image representations of pages
            // - IMG        Image file.  Use the imageView (inside pageHolder, inside zoomLayout).  All sizes from the bitmap file.
            // - **Image    Custom image slide (multiple images).  Same as PDF
            // - XML        OpenSong song.  Views need to be drawn and measured. Stage mode uses recyclerView, Performance, the pageHolder

            // Reset the recycler view to vertical by default
            if (recyclerLayoutManager!=null) {
                recyclerLayoutManager.setOrientation(RecyclerLayoutManager.VERTICAL);
            }

            // Set as not using pdfLandscape by default
            mainActivityInterface.getGestures().setPdfLandscapeView(false);

            if (mainActivityInterface.getSong() != null &&
                    mainActivityInterface.getSong().getFiletype() != null &&
                    mainActivityInterface.getSong().getFolder() != null) {

                if (mainActivityInterface.getSong().getFiletype().equals("PDF") &&
                        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    preparePDFView();

                } else if (mainActivityInterface.getSong().getFiletype().equals("IMG")) {
                    prepareIMGView();

                } else if (mainActivityInterface.getSong().getFolder().contains("**Image")) {
                    prepareSlideImageView();

                } else if (mainActivityInterface.getSong().getFiletype().equals("XML") ||
                        (mainActivityInterface.getSong().getFiletype().equals("PDF") &&
                                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP)) {
                    prepareXMLView();
                } else {
                    endProcessing();
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void preparePDFView() {
        // If we can't deal with a PDF (old Android), it will default to the info from the database as XML
        // We populate the recyclerView with the pages of the PDF
        myView.recyclerView.setVisibility(View.INVISIBLE);
        myView.pageHolder.setVisibility(View.GONE);
        myView.songView.setVisibility(View.GONE);
        myView.zoomLayout.setVisibility(View.GONE);

        if (mainActivityInterface.getPreferences().getMyPreferenceBoolean("pdfLandscapeView",true) &&
                mainActivityInterface.getPreferences().getMyPreferenceString("songAutoScale", "Y").equals("Y") &&
                mainActivityInterface.getOrientation() == Configuration.ORIENTATION_LANDSCAPE &&
                recyclerLayoutManager!=null) {
            recyclerLayoutManager.setOrientation(RecyclerLayoutManager.HORIZONTAL);
            // We can't allow swiping to move songs - set a record of this mode
            mainActivityInterface.getGestures().setPdfLandscapeView(true);
        } else if (recyclerLayoutManager!=null) {
            recyclerLayoutManager.setOrientation(RecyclerLayoutManager.VERTICAL);
            mainActivityInterface.getGestures().setPdfLandscapeView(false);
        }

        if (getContext()!=null) {
            pdfPageAdapter = new PDFPageAdapter(getContext(), mainActivityInterface, displayInterface,
                    availableWidth, availableHeight, myView.inlineSetList.getInlineSetWidth());
        }

        myView.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                myView.recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float scale = pdfPageAdapter.getPdfHorizontalScale();
                widthBeforeScale = pdfPageAdapter.getWidth();
                widthAfterScale = widthBeforeScale;
                heightBeforeScale = pdfPageAdapter.getHeight();
                heightAfterScale = heightBeforeScale;
                recyclerLayoutManager.setSizes(pdfPageAdapter.getWidths(), pdfPageAdapter.getHeights(), availableWidth, availableHeight,scale);
                myView.recyclerView.setPadding(myView.inlineSetList.getInlineSetWidth(),0,0,0);
                myView.recyclerView.setMaxScrollY(heightAfterScale - availableHeight);
                //IV - Reset zoom
                myView.recyclerView.toggleScale();


                // Do the slide in
                myView.recyclerView.setVisibility(View.VISIBLE);
                myView.recyclerView.startAnimation(animSlideIn);

                // Get a null screenshot
                getScreenshot(0, 0, 0);

                // Deal with song actions to run after display (highlighter, notes, etc)
                dealWithStuffAfterReady(true);
            }
        });
        myView.recyclerView.setAdapter(pdfPageAdapter);
    }
    private void prepareIMGView() {
        // We use the imageView inside the pageHolder, inside the zoomLayout
        myView.recyclerView.setVisibility(View.GONE);
        myView.pageHolder.setVisibility(View.INVISIBLE);
        myView.songView.setVisibility(View.GONE);
        myView.zoomLayout.setVisibility(View.VISIBLE);
        myView.imageView.setVisibility(View.VISIBLE);

        // Get a bmp from the image
        Bitmap bmp = mainActivityInterface.getProcessSong().getSongBitmap(mainActivityInterface.getSong().getFolder(),
                mainActivityInterface.getSong().getFilename());

        if (bmp!=null) {
            widthBeforeScale = bmp.getWidth();
            heightBeforeScale = bmp.getHeight();
        } else {
            widthBeforeScale = 1;
            heightBeforeScale = 1;
        }

        myView.zoomLayout.setPageSize(availableWidth, availableHeight);
        //IV - Reset zoom
        myView.zoomLayout.resetLayout();
        myView.zoomLayout.toggleScale();

        if (widthBeforeScale>0 && heightBeforeScale>0) {
            String scaleMethod = mainActivityInterface.getPreferences().getMyPreferenceString("songAutoScale", "W");
            switch (scaleMethod) {
                case "W":
                    scaleFactor = (float) availableWidth / (float) widthBeforeScale;
                    break;
                case "Y":
                    scaleFactor = Math.min((float) availableWidth / (float) widthBeforeScale, (float) availableHeight / (float) heightBeforeScale);
                    break;
                default:
                    scaleFactor = 1f;
            }
        } else {
            scaleFactor = 1f;
        }

        // Pass this scale factor to the zoom layout
        myView.zoomLayout.setCurrentScale(scaleFactor);

        widthAfterScale = (int) (widthBeforeScale*scaleFactor);
        heightAfterScale= (int) (heightBeforeScale*scaleFactor);

        myView.pageHolder.getLayoutParams().width = widthAfterScale;
        myView.pageHolder.getLayoutParams().height = heightAfterScale;
        myView.imageView.getLayoutParams().width = widthAfterScale;
        myView.imageView.getLayoutParams().height = heightAfterScale;

        RequestOptions requestOptions = new RequestOptions().override(widthAfterScale,heightAfterScale);
        if (getContext()!=null) {
            Glide.with(getContext()).load(bmp).apply(requestOptions).into(myView.imageView);
        }
        myView.zoomLayout.setSongSize(widthAfterScale, heightAfterScale + (int)(mainActivityInterface.getSongSheetTitleLayout().getHeight()*scaleFactor));

        // Slide in
        myView.pageHolder.post(() -> {
            if (myView!=null) {
                myView.pageHolder.setVisibility(View.VISIBLE);
                myView.pageHolder.startAnimation(animSlideIn);
            }
        });

        // Deal with song actions to run after display (highlighter, notes, etc)
        dealWithStuffAfterReady(true);

    }
    private void prepareSlideImageView() {
        // An image slide.  Use the recyclerView with a new arrayAdapter
        myView.pageHolder.setVisibility(View.GONE);
        myView.songView.setVisibility(View.GONE);
        myView.zoomLayout.setVisibility(View.GONE);
        myView.recyclerView.setVisibility(View.INVISIBLE);
        if (getContext()!=null) {
            imageSlideAdapter = new ImageSlideAdapter(getContext(), mainActivityInterface, displayInterface,
                    availableWidth, availableHeight);
        }

        // If we have a time for each slide, set the song duration
        if (mainActivityInterface.getSong().getUser1()!=null && !mainActivityInterface.getSong().getUser1().isEmpty()) {
            int time;
            try {
                time = Integer.parseInt(mainActivityInterface.getSong().getUser1());
            } catch (Exception e) {
                time = 0;
            }
            mainActivityInterface.getSong().setAutoscrolllength(String.valueOf((time*imageSlideAdapter.getItemCount())));
            mainActivityInterface.getSong().setAutoscrolldelay("0");
        }

        myView.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                myView.recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                widthBeforeScale = imageSlideAdapter.getWidth();
                widthAfterScale = widthBeforeScale;
                heightBeforeScale = imageSlideAdapter.getHeight();
                heightAfterScale = heightBeforeScale;
                recyclerLayoutManager.setSizes(imageSlideAdapter.getWidths(), imageSlideAdapter.getHeights(), availableWidth, availableHeight,1f);
                myView.recyclerView.setMaxScrollY(heightAfterScale - availableHeight);
                //IV - Reset zoom
                myView.recyclerView.toggleScale();

                // Slide in
                myView.recyclerView.setVisibility(View.VISIBLE);
                myView.recyclerView.startAnimation(animSlideIn);

                // Deal with song actions to run after display (highlighter, notes, etc)
                dealWithStuffAfterReady(true);

                // Get a null screenshot
                getScreenshot(0,0,0);

            }
        });
        myView.recyclerView.setAdapter(imageSlideAdapter);

    }
    private void prepareXMLView() {
        // If we are old Android and can't show a pdf, tell the user
        if (mainActivityInterface.getSong().getFiletype().equals("PDF") &&
                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            mainActivityInterface.getShowToast().doIt(not_allowed);
        }

        mainActivityInterface.setSectionViews(null);

        // Now prepare the song sections views so we can measure them for scaling using a view tree observer
        mainActivityInterface.setSectionViews(mainActivityInterface.getProcessSong().
                setSongInLayout(mainActivityInterface.getSong(), false, false));

        // Prepare the song sheet header if required, if not, make it null
        if (mainActivityInterface.getPreferences().getMyPreferenceBoolean("songSheet", false)) {
            mainActivityInterface.setSongSheetTitleLayout(mainActivityInterface.getSongSheetHeaders().getSongSheet(
                    mainActivityInterface.getSong(), mainActivityInterface.getProcessSong().getScaleComments(), mainActivityInterface.getMyThemeColors().getLyricsTextColor()));
        } else {
            mainActivityInterface.setSongSheetTitleLayout(null);
        }
        // We now have the views ready, we need to draw them so we can measure them
        // Start with the song sheet title/header
        // The other views are dealt with after this call

        // Run this as a post to the root view - otherwise views aren't necessarily fully ready for drawing
        myView.getRoot().post(this::setUpHeaderListener);
    }

    private void setUpHeaderListener() {
        try {
            // If we want headers, the header layout isn't null, so we can draw and listen
            // Add the view and wait for the vto return
            if (mainActivityInterface.getSongSheetTitleLayout() != null &&
                    !mainActivityInterface.getMode().equals(mode_presenter)) {
                // Check the header isn't already attached to a view
                if (mainActivityInterface.getSongSheetTitleLayout().getParent()!=null) {
                    ((ViewGroup) mainActivityInterface.getSongSheetTitleLayout().getParent()).removeAllViews();
                }
                if (myView!=null) {
                    myView.testPaneHeader.removeAllViews();
                }

                mainActivityInterface.getSongSheetTitleLayout().post(() -> {
                    if (myView!=null) {
                        try {
                            setUpTestViewListener();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                        }
                    }
                });
                try {
                    if (myView!=null) {
                        myView.testPaneHeader.addView(mainActivityInterface.getSongSheetTitleLayout());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                    setUpTestViewListener();
                }

            } else {
                // No song sheet title requested, so skip
                setUpTestViewListener();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
            // Error, so skip
            setUpTestViewListener();
        }
    }

    private void setUpTestViewListener() {
        if (myView!=null) {
            // Add the views and wait for the vto of each to finish
            myView.songView.clearViews();
            myView.testPane.removeAllViews();

            // We will only proceed once all of the views show true as being drawn
            boolean[] viewsDrawn = new boolean[mainActivityInterface.getSectionViews().size()];

            for (int v = 0; v < mainActivityInterface.getSectionViews().size(); v++) {
                final int viewNum = v;
                final View view = mainActivityInterface.getSectionViews().get(viewNum);

                // If views are attached to a parent, remove it from the parent
                if (view.getParent() != null) {
                    // Still attached - remove it
                    ((ViewGroup) view.getParent()).removeView(view);
                }

                // Set a post listener for the view
                view.post(() -> {
                    viewsDrawn[viewNum] = true;
                    // Check if the array is only true
                    boolean isReady = true;
                    for (boolean thisBoolean : viewsDrawn) {
                        if (!thisBoolean) {
                            // Not ready
                            isReady = false;
                            break;
                        }
                    }
                    if (isReady) {
                        songIsReadyToDisplay();
                    }
                });

                // Add the view.  The post above gets called once drawn
                myView.testPane.addView(view);
            }
        }
    }

    private void songIsReadyToDisplay() {
        if (myView!=null) {
            try {
                // Set the page holder to fullscreen for now
                myView.pageHolder.getLayoutParams().width = availableWidth;
                myView.pageHolder.getLayoutParams().height = availableHeight;
                myView.songSheetTitle.setVisibility(View.VISIBLE);

                // All views have now been drawn, so measure the arraylist views
                for (int x = 0; x < mainActivityInterface.getSectionViews().size(); x++) {
                    int width = mainActivityInterface.getSectionViews().get(x).getWidth();
                    int height = mainActivityInterface.getSectionViews().get(x).getHeight();
                    mainActivityInterface.addSectionSize(x, width, height);
                }

                myView.testPane.removeAllViews();

                // Decide which mode we are in to determine how the views are rendered
                if (mainActivityInterface.getMode().equals(mode_stage)) {
                    // We are in Stage mode so use the recyclerView
                    myView.recyclerView.setVisibility(View.INVISIBLE);
                    myView.pageHolder.setVisibility(View.GONE);
                    myView.songView.setVisibility(View.GONE);
                    myView.zoomLayout.setVisibility(View.GONE);
                    myView.highlighterView.setVisibility(View.GONE);
                    if (getContext() != null) {
                        stageSectionAdapter = new StageSectionAdapter(getContext(), mainActivityInterface,
                                displayInterface, myView.inlineSetList.getInlineSetWidth());
                    }

                    myView.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (myView!=null) {
                                myView.recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                widthBeforeScale = stageSectionAdapter.getWidth();
                                widthAfterScale = widthBeforeScale;
                                heightBeforeScale = stageSectionAdapter.getHeight();
                                heightAfterScale = heightBeforeScale;

                                recyclerLayoutManager.setSizes(stageSectionAdapter.getWidths(), stageSectionAdapter.getHeights(), availableWidth, availableHeight, 1f);
                                myView.recyclerView.setHasFixedSize(false);
                                myView.recyclerView.setMaxScrollY(heightAfterScale - availableHeight);
                                myView.recyclerView.setPadding(myView.inlineSetList.getInlineSetWidth(), 0, 0, 0);

                                endProcessing();

                                // Slide in
                                long QOSAdjustment = doSongLoadQOSTime - (System.currentTimeMillis() - doSongLoadStartTime);

                                myView.recyclerView.setVisibility(View.VISIBLE);
                                //IV - Reset zoom
                                myView.recyclerView.toggleScale();

                                mainActivityInterface.getMainHandler().postDelayed(() -> {
                                    myView.recyclerView.startAnimation(animSlideIn);

                                    dealWithStuffAfterReady(false);

                                    // Get a null screenshot
                                    getScreenshot(0, 0, 0);
                                }, Math.max(0, QOSAdjustment));
                            }
                        }
                    });
                    myView.recyclerView.post(() -> {
                        try {
                            myView.recyclerView.setAdapter(stageSectionAdapter);
                        } catch (Exception e) {
                            mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                            e.printStackTrace();
                        }
                    });


                } else {
                    // We are in Performance mode, so use the songView
                    myView.pageHolder.setVisibility(View.INVISIBLE);
                    myView.zoomLayout.setVisibility(View.VISIBLE);
                    myView.songView.setVisibility(View.VISIBLE);
                    myView.imageView.setVisibility(View.GONE);
                    myView.recyclerView.setVisibility(View.GONE);
                    myView.highlighterView.setVisibility(View.GONE);

                    myView.zoomLayout.setPageSize(availableWidth, availableHeight);
                    //IV - Reset zoom
                    myView.zoomLayout.resetLayout();
                    myView.zoomLayout.toggleScale();

                    myView.pageHolder.getLayoutParams().width = availableWidth;
                    myView.pageHolder.getLayoutParams().height = availableHeight;

                    float[] scaleInfo = mainActivityInterface.getProcessSong().addViewsToScreen(
                            mainActivityInterface.getSong(),
                            mainActivityInterface.getSectionViews(),
                            mainActivityInterface.getSectionWidths(), mainActivityInterface.getSectionHeights(),
                            myView.pageHolder, myView.songView, myView.songSheetTitle,
                            availableWidth, availableHeight, myView.songView.getCol1(), myView.songView.getCol2(),
                            myView.songView.getCol3(), false, getResources().getDisplayMetrics());

                    // Determine how many columns are scaled
                    heightAfterScale = 0;
                    if (scaleInfo[0] == 1) {
                    /*float[] {1,           // Number of columns
                    1    oneColumnScale,    // Overall best scale
                    2    col1_1Width,       // Column 1 max width
                    3    col1_1Height,      // Column 1 total height
                    4    sectionSpace}      // Section space per view except last column */
                        scaleFactor = scaleInfo[1];
                        widthBeforeScale = (int) scaleInfo[2];
                        heightBeforeScale = (int) scaleInfo[3];
                        widthAfterScale = (int) (widthBeforeScale * scaleFactor);
                        heightAfterScale = (int) (heightBeforeScale * scaleFactor);
                        myView.pageHolder.getLayoutParams().width = widthAfterScale;
                    } else if (scaleInfo[0] == 2) {
                    /*float[]{2,             // Number of columns
                    1    twoColumnScale,     // Overall best scale
                    2    columnBreak2,       // Break point
                    3    col1_2ScaleBest,    // Best col 1 scale
                    4    col1_2Width,        // Column 1 max width
                    5    col1_2Height,       // Column 1 total height
                    6    col2_2ScaleBest,    // Best col 2 scale
                    7    col2_2Width,        // Column 2 max width
                    8    col2_2Height,       // Column 2 total height
                    9    sectionSpace}       // Section space per view except last column */
                        scaleFactor = Math.max(scaleInfo[3], scaleInfo[6]);
                        widthBeforeScale = availableWidth;
                        heightBeforeScale = (int) Math.max(scaleInfo[5], scaleInfo[8]);
                        widthAfterScale = availableWidth;
                        heightAfterScale = (int) Math.max(scaleInfo[3] * scaleInfo[5], scaleInfo[6] * scaleInfo[8]);
                        myView.pageHolder.getLayoutParams().width = availableWidth;
                        myView.songView.getLayoutParams().width = availableWidth;
                    } else if (scaleInfo[0] == 3) {
                    /*float[]{3,             // Number of columns
                    1    threeColumnScale,   // Overall best scale
                    2    columnBreak3_a,     // Break point 1
                    3    columnBreak3_b,     // Break point 2
                    4    col1_3ScaleBest,    // Best col 1 scale
                    5    col1_3Width,        // Column 1 max width
                    6    col1_3Height,       // Column 1 total height
                    7    col2_3ScaleBest,    // Best col 2 scale
                    8    col2_3Width,        // Column 2 max width
                    9    col2_3Height,       // Column 2 total height
                    10   col3_3ScaleBest,    // Best col 3 scale
                    11   col3_3Width,        // Column 3 max width
                    12   col3_3Height,       // Column 3 total height
                    13   sectionSpace};      // Section space per view except last in column */
                        widthBeforeScale = availableWidth;
                        heightBeforeScale = (int) Math.max(scaleInfo[6], Math.max(scaleInfo[9], scaleInfo[12]));
                        widthAfterScale = availableWidth;
                        scaleFactor = Math.max(scaleInfo[4], Math.max(scaleInfo[7], scaleInfo[10]));
                        heightAfterScale = (int) Math.max(scaleInfo[4] * scaleInfo[6], Math.max(scaleInfo[7] * scaleInfo[9], scaleInfo[10] * scaleInfo[12]));
                        myView.pageHolder.getLayoutParams().width = availableWidth;
                        myView.songView.getLayoutParams().width = availableWidth;
                    }

                    heightAfterScale = heightAfterScale + mainActivityInterface.getSongSheetTitleLayout().getHeight();
                    myView.pageHolder.getLayoutParams().height = heightAfterScale;
                    myView.songView.getLayoutParams().height = heightAfterScale;

                    // Pass this scale factor to the zoom layout as the new minimum scale
                    myView.zoomLayout.setCurrentScale(scaleFactor);
                    myView.zoomLayout.setSongSize(widthAfterScale, heightAfterScale);

                    endProcessing();

                    // Slide in
                    long QOSAdjustment = doSongLoadQOSTime - (System.currentTimeMillis() - doSongLoadStartTime);

                    myView.zoomLayout.postDelayed(() -> {
                        try {
                            // The new song sizes were sent to the zoomLayout in ProcessSong
                            int topPadding = 0;

                            // IV - We need to request layout to get songsheet information added ahead of animate in. Odd!
                            if (myView != null) {
                                myView.pageHolder.requestLayout();
                                myView.pageHolder.setVisibility(View.VISIBLE);
                                myView.pageHolder.startAnimation(animSlideIn);

                                dealWithStuffAfterReady(false);

                                // Try to take a screenshot ready for any highlighter actions that may be called
                                getScreenshot(myView.pageHolder.getWidth(), myView.pageHolder.getHeight() - myView.songSheetTitle.getHeight(), topPadding);
                            }
                        } catch (Exception e) {
                            mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                            e.printStackTrace();
                        }
                    }, Math.max(0, QOSAdjustment));
                }

            } catch (Exception e) {
                mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                e.printStackTrace();
            }
        }
    }

    private void endProcessing () {
        // Set the load status to the song (used to enable nearby section change  listener)
        mainActivityInterface.getSong().setCurrentlyLoading(false);

        // Set the previous/next
        mainActivityInterface.getDisplayPrevNext().setPrevNext();

        // Release the processing lock
        processingTestView = false;
    }

    // This stuff deals with running song actions
    @SuppressWarnings("ConstantConditions")
    private void dealWithStuffAfterReady(boolean callEndProcessing) {

        if (callEndProcessing) {
            endProcessing();
        }

        // IV - Consume any later pending client section change received from Host (-ve value)
        if (mainActivityInterface.getNearbyConnections().hasValidConnections() &&
                !mainActivityInterface.getNearbyConnections().getIsHost()) {
            int hostPendingSection = mainActivityInterface.getNearbyConnections().getHostPendingSection();
            if (hostPendingSection != 0) {
                mainActivityInterface.getNearbyConnections().doSectionChange(hostPendingSection);
            }
            mainActivityInterface.getNearbyConnections().resetHostPendingSection();
        }

        mainActivityInterface.setHighlightChangeAllowed(true);

        // Update the secondary display (if present)
        displayInterface.updateDisplay("newSongLoaded");
        displayInterface.updateDisplay("setSongInfo");
        displayInterface.updateDisplay("initialiseInfoBarRequired");
        if (mainActivityInterface.getMode().equals(mode_stage) &&
                mainActivityInterface.getSong().getFiletype().equals("XML") &&
                !mainActivityInterface.getSong().getFolder().contains("**Image") &&
                !mainActivityInterface.getSong().getFolder().contains("**"+image_string)) {
            displayInterface.updateDisplay("setSongContent");
        } else if (!mainActivityInterface.getSong().getFiletype().equals("XML") ||
                mainActivityInterface.getSong().getFolder().contains("**Image")) {
            mainActivityInterface.getPresenterSettings().setCurrentSection(0);
            displayInterface.updateDisplay("showSection");
        } else {
            displayInterface.updateDisplay("setSongContent");
        }

        // Now deal with the highlighter file
        if (mainActivityInterface.getMode().equals(mode_performance)) {
            dealWithHighlighterFile(widthAfterScale, heightAfterScale);
        }

        // Load up the sticky notes if the user wants them
        dealWithStickyNotes(false, false);

        mainActivityInterface.moveToSongInSongMenu();

        // Run this only when the user has stopped on a song after 2s.
        // This is important for pad use - the pad will not change while the user rapidly changes songs.
        // This is important for rapid song changes - we only run autoscroll, metronome etc. for the last song.
        // For pads, once settled on a song the user has 2s grace to prep to play the song before cross fade.
        // A good time to change capo!
        dealWithExtraStuffOnceSettledHandler.removeCallbacks((dealWithExtraStuffOnceSettledRunnable));
        dealWithExtraStuffOnceSettledHandler.postDelayed(dealWithExtraStuffOnceSettledRunnable, graceTime);

        // If we are in a set, send that info to the inline set custom view to see if it should draw
        myView.inlineSetList.checkVisibility();
        mainActivityInterface.getMainHandler().postDelayed(() -> {
            if (myView!=null && myView.inlineSetList.getChildCount()>=0) {
                myView.inlineSetList.notifyInlineSetHighlight();
                // Showcase what this is
                if (myView.inlineSetList.getVisibility() == View.VISIBLE) {
                    // Just in case it is empty!
                    try {
                        if (myView != null) {
                            myView.inlineSetList.postDelayed(() -> {
                                if (myView!=null && myView.inlineSetList!=null) {
                                    try {
                                        mainActivityInterface.getShowCase().singleShowCase(
                                                (Activity) mainActivityInterface,
                                                myView.inlineSetList.getChildAt(0), null,
                                                inline_set_string, true, "inline_set");
                                    } catch (Exception e) {
                                        mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                                        e.printStackTrace();
                                    }
                                }
                            },800);
                        }
                    } catch (Exception e) {
                        mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                        e.printStackTrace();
                    }
                }
            }
        },800);
    }

    private void dealWithExtraStuffOnceSettled() {
        if (getContext()!=null && myView!=null) {
            mainActivityInterface.getHotZones().checkScrollButtonOn(myView.zoomLayout,myView.recyclerView);
            // Send the autoscroll information (if required)
            mainActivityInterface.getAutoscroll().initialiseSongAutoscroll(widthAfterScale, heightAfterScale, availableWidth, availableHeight);
            if (mainActivityInterface.getAutoscroll().getShouldAutostart()) {
                mainActivityInterface.getAutoscroll().startAutoscroll();
            }

            // Deal with auto start of metronome
            if (mainActivityInterface.getMetronome().getMetronomeAutoStart() &&
                    songChange && metronomeWasRunning) {
                mainActivityInterface.getMetronome().startMetronome();
            }

            // Deal with capo information (if required)
            mainActivityInterface.updateOnScreenInfo("capoShow");
            mainActivityInterface.dealWithCapo();

            // Start the pad (if the pads are activated and the pad is valid)
            mainActivityInterface.getPad().autoStartPad();

            // Update any midi commands (if any)
            if (mainActivityInterface.getBeatBuddy().getBeatBuddyAutoLookup() || mainActivityInterface.getMidi().getMidiSendAuto()) {

                int delay = 0;
                // Send BeatBuddy autosong if required
                if (mainActivityInterface.getBeatBuddy().getBeatBuddyAutoLookup()) {
                    delay = mainActivityInterface.getBeatBuddy().tryAutoSend(getContext(),mainActivityInterface,mainActivityInterface.getSong());
                }

                if (mainActivityInterface.getMidi().getMidiSendAuto()) {
                    mainActivityInterface.getMainHandler().postDelayed(() -> {
                        // These are addition to beatbuddy, so sent afterwards
                        mainActivityInterface.getMidi().buildSongMidiMessages();
                        mainActivityInterface.getMidi().sendSongMessages();
                    }, delay);
                }
            }

            // Check the set index
            mainActivityInterface.checkSetMenuItemHighlighted(mainActivityInterface.getCurrentSet().getIndexSongInSet());
            mainActivityInterface.notifySetFragment("scrollTo",mainActivityInterface.getCurrentSet().getIndexSongInSet());
            mainActivityInterface.getDisplayPrevNext().setPrevNext();


            // Update the view log usage
            mainActivityInterface.getStorageAccess().updateFileUsageLog(mainActivityInterface.getSong());

            // If we opened the app with and intent/file, check if we need to import
            tryToImportIntent();

            mainActivityInterface.updateOnScreenInfo("showhide");
        }
    }
    public void dealWithAbc(boolean forceShow, boolean hide) {
        if (hide) {
            if (abcPopup!=null) {
                abcPopup.closeScore();
            }
        } else {
            if ((mainActivityInterface != null && mainActivityInterface.getSong() != null &&
                    mainActivityInterface.getSong().getAbc() != null &&
                    !mainActivityInterface.getSong().getAbc().isEmpty() &&
                    mainActivityInterface.getPreferences().
                            getMyPreferenceBoolean("abcAuto", false)) || forceShow) {
                // This is called from the MainActivity when we clicked on the page button
                abcPopup.floatABC(myView.pageHolder, forceShow);
            }
        }
    }
    private void dealWithHighlighterFile(int w, int h) {
        try {
            if (getContext()!=null) {
                String highlighterFilename = mainActivityInterface.getProcessSong().
                        getHighlighterFilename(mainActivityInterface.getSong(),
                                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT,-1);
                Uri highlighterUri = mainActivityInterface.getStorageAccess().getUriForItem("Highlighter","",highlighterFilename);

                if (myView!=null && mainActivityInterface.getStorageAccess().uriExists(highlighterUri)) {
                    // Set the highlighter image view to match
                    myView.highlighterView.setVisibility(View.INVISIBLE);
                    // Once the view is ready at the required size, deal with it
                    ViewTreeObserver highlighterVTO = myView.highlighterView.getViewTreeObserver();
                    highlighterVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            try {
                                if (getContext() != null) {
                                    myView.highlighterView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    // Load in the bitmap with these dimensions
                                    // v5 used portrait and landscape views.  However, now if we only have one
                                    // column, we will always load the portrait view
                                    // landscape is now for columns
                                    Bitmap highlighterBitmap = mainActivityInterface.getProcessSong().
                                            getHighlighterFile(0, 0);

                                    if (highlighterBitmap != null &&
                                            mainActivityInterface.getPreferences().getMyPreferenceBoolean("drawingAutoDisplay", true)) {

                                        // If the bitmap doesn't match the view, scale it
                                        float bmpXScale = (float)w/(float)highlighterBitmap.getWidth();
                                        float bmpYScale = (float)h/(float)highlighterBitmap.getHeight();

                                        myView.highlighterView.setVisibility(View.VISIBLE);
                                        ViewGroup.LayoutParams rlp = myView.highlighterView.getLayoutParams();
                                        rlp.width = (int) ((float) w * bmpXScale);
                                        rlp.height = (int) ((float) h * bmpYScale);

                                        myView.highlighterView.setLayoutParams(rlp);
                                        RequestOptions requestOptions = new RequestOptions().centerInside().override(rlp.width, rlp.height);
                                        Glide.with(getContext()).load(highlighterBitmap).
                                                apply(requestOptions).
                                                into(myView.highlighterView);

                                        myView.highlighterView.setPivotX(0f);
                                        myView.highlighterView.setPivotY(0f);

                                        // Hide after a certain length of time
                                        int timetohide = mainActivityInterface.getPreferences().getMyPreferenceInt("timeToDisplayHighlighter", 0);
                                        if (timetohide != 0) {
                                            autoHideHighlighterHandler.postDelayed(
                                                    autoHideHighlighterRunnable, timetohide * 1000L);
                                        }
                                    } else {
                                        myView.highlighterView.post(() -> {
                                            if (myView != null) {
                                                try {
                                                    myView.highlighterView.setVisibility(View.GONE);
                                                } catch (Exception e) {
                                                    mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                    myView.highlighterView.post(() -> {
                        try {
                            myView.highlighterView.requestLayout();
                        } catch (Exception e) {
                            mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                            e.printStackTrace();
                        }
                    });
                } else if (myView!=null) {
                    myView.highlighterView.post(() -> {
                        if (myView!=null) {
                            try {
                                myView.highlighterView.setVisibility(View.GONE);
                            } catch (Exception e) {
                                mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());
            e.printStackTrace();
        }
    }
    private void getScreenshot(int w, int h, int topPadding) {
        if (!mainActivityInterface.getPreferences().
                getMyPreferenceString("songAutoScale","W").equals("N")
                && w!=0 && h!=0) {
            try {
                Bitmap bitmap = Bitmap.createBitmap(w, h + topPadding, bmpFormat);
                Canvas canvas = new Canvas(bitmap);
                if (myView != null) {
                    myView.songView.layout(0, topPadding, w, h + topPadding);
                    myView.songView.draw(canvas);
                    Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, topPadding, w, h);
                    bitmap.recycle();
                    mainActivityInterface.getThreadPoolExecutor().execute(() -> {
                        mainActivityInterface.setScreenshotFile(croppedBitmap);
                        croppedBitmap.recycle();
                    });
                }
            } catch (OutOfMemoryError e) {
                // Change the resolution of the bitmap to a lower option
                bmpFormat = Bitmap.Config.RGB_565;
                mainActivityInterface.setScreenshotFile(null);
                mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());

            } catch (Exception e) {
                e.printStackTrace();
                mainActivityInterface.setScreenshotFile(null);
                mainActivityInterface.getStorageAccess().updateCrashLog(e.toString());

            }
        }
    }
    public void toggleHighlighter() {
        if (mainActivityInterface.getMode().equals(mode_performance)) {
            if (myView.highlighterView.getVisibility() == View.VISIBLE) {
                myView.highlighterView.setVisibility(View.GONE);
            } else {
                myView.highlighterView.setVisibility(View.VISIBLE);
            }
        }
    }
    public void dealWithStickyNotes(boolean forceShow, boolean hide) {
        if (hide) {
            if (stickyPopUp!=null) {
                stickyPopUp.closeSticky();
            }
            if (abcPopup!=null) {
                abcPopup.closeScore();
            }
        } else {
            if ((mainActivityInterface != null && mainActivityInterface.getSong() != null &&
                    mainActivityInterface.getSong().getNotes() != null &&
                    !mainActivityInterface.getSong().getNotes().isEmpty() &&
                    mainActivityInterface.getPreferences().
                            getMyPreferenceBoolean("stickyAuto", true)) || forceShow) {
                // This is called from the MainActivity when we clicked on the page button
                if (myView!=null) {
                    stickyPopUp.floatSticky(myView.pageHolder, forceShow);
                }
            }
        }
    }

    public void showNearbyAlertPopUp(String message) {
        // Clear any existing popup
        nearbyAlertPopUp.destroyPopup();

        // Show the new one
        nearbyAlertPopUp.floatSticky(myView.pageHolder,message);
    }

    // The scale and gesture bits of the code
    @SuppressLint("ClickableViewAccessibility")
    private void setGestureListeners(){
        // get the gesture detector
        if (getContext()!=null) {
            gestureDetector = new GestureDetector(getContext(), new GestureListener(mainActivityInterface,
                    swipeMinimumDistance, swipeMaxDistanceYError, swipeMinimumVelocity));

            // Any interaction with the screen should trigger the display prev/next (if required)
            // It should also show the action bar
            myView.zoomLayout.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mainActivityInterface.getDisplayPrevNext().showAndHide();
                    mainActivityInterface.updateOnScreenInfo("showhide");
                    mainActivityInterface.showActionBar();
                    mainActivityInterface.getHotZones().checkScrollButtonOn(myView.zoomLayout,myView.recyclerView);
                    // Check for updating send nearby to
                    if (mainActivityInterface.getNearbyConnections().hasValidConnections() &&
                            mainActivityInterface.getNearbyConnections().getIsHost() &&
                            !mainActivityInterface.getPreferences().getMyPreferenceString("songAutoScale", "W").equals("Y")) {
                        // Get the scroll height
                        int height = myView.zoomLayout.getMaxScrollY();
                        // Get the scroll position
                        int scrollPos = myView.zoomLayout.getScrollY();
                        if (height > 0) {
                            mainActivityInterface.getNearbyConnections().sendScrollToPayload((float) scrollPos / (float) height);
                        }
                    }
                }
                return gestureDetector.onTouchEvent(motionEvent);
            });

            myView.recyclerView.setGestureDetector(gestureDetector);
        }
    }

    public void toggleScale() {
        // IV - View may no longer be valid
        if (myView!=null && myView.recyclerView.getVisibility() == View.VISIBLE) {
            // Resets the zoom
            myView.recyclerView.toggleScale();
        } else {
            if (myView != null) {
                // Toggles between different zooms
                myView.zoomLayout.toggleScale();
            }
        }
    }

    public void doNearbyScrollBy(float proportionScroll) {
        // We received from nearby host, so attempt to scroll by this proportion in the zoomLayout
        if (myView.zoomLayout.getVisibility()==View.VISIBLE &&
                !mainActivityInterface.getPreferences().getMyPreferenceString("songAutoScale","W").equals("Y")) {
            myView.zoomLayout.animateScrollBy(
                    Math.abs(proportionScroll),proportionScroll>0);
        } else if (myView.recyclerView.getVisibility()==View.VISIBLE) {
            int height = (int)(proportionScroll*myView.recyclerView.getHeight());
            myView.recyclerView.smoothScrollBy(0,height);
        }
    }

    public void doNearbyScrollTo(float proportionScroll) {
        // We received from nearby host, so attempt to scroll this position (as ratio of height)
        if (myView.zoomLayout.getVisibility()==View.VISIBLE &&
                !mainActivityInterface.getPreferences().getMyPreferenceString("songAutoScale","W").equals("Y")) {
            myView.zoomLayout.doScrollTo(0,(int)(myView.zoomLayout.getMaxScrollY()*proportionScroll));
        } else if (myView.recyclerView.getVisibility()==View.VISIBLE) {
            int height = (int)(proportionScroll*myView.recyclerView.getHeight());
            myView.recyclerView.scrollTo(0,height);
        }
    }

    public void updateSizes(int width, int height) {
        myView.zoomLayout.setSongSize(width,height);
    }

    // Received from MainActivity after a user clicked on a pdf page or a Stage Mode section
    public void performanceShowSection(int position) {
        // Scroll the recyclerView to the position as long as we aren't in an autoscroll
        if (!mainActivityInterface.getAutoscroll().getIsAutoscrolling()) {
            //myView.recyclerView.smoothScrollBy(0,500);

            // IV - Use a snap to top scroller if scrolling to the top of the screen
            if (mainActivityInterface.getPreferences().getMyPreferenceFloat("stageModeScale",0.8f) == 1.0f) {
                myView.recyclerView.smoothScrollTo(getContext(),recyclerLayoutManager, position);
            } else {
                myView.recyclerView.doSmoothScrollTo(recyclerLayoutManager, position);
            }
        }
        mainActivityInterface.getPresenterSettings().setCurrentSection(position);
        displayInterface.updateDisplay("showSection");
        mainActivityInterface.getHotZones().checkScrollButtonOn(myView.zoomLayout,myView.recyclerView);
    }

    // If a nearby host initiated a section change
    public void selectSection(int position) {
        if (mainActivityInterface.getSong().getFiletype().equals("PDF") &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pdfPageAdapter.sectionSelected(position);
        } else if (mainActivityInterface.getMode().equals(mode_stage)) {
            mainActivityInterface.getMainHandler().postDelayed(()-> {
                stageSectionAdapter.clickOnSection(position);
                performanceShowSection(position);
            },50);
        }
    }

    // Get the width of the song display (to check it fits)
    public int getSongWidth() {
        if (myView!=null) {
            return myView.songView.getWidth() - myView.songView.getPaddingStart();
        } else {
            return 0;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.clear();
        super.onSaveInstanceState(outState);
    }
}
