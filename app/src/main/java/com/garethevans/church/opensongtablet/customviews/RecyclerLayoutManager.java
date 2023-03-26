package com.garethevans.church.opensongtablet.customviews;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerLayoutManager extends LinearLayoutManager {

    // map of child adapter position to its height.
    private ArrayList<Integer> childSizes = new ArrayList<>();
    private int size, scrolledY;
    private int screenSize;
    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "RecyclerLayoutMan";

    public RecyclerLayoutManager(Context context) {
        super(context);
    }

    public void setSizes(ArrayList<Float> floatSizes, int screenSize) {
        float total = 0;
        childSizes = new ArrayList<>();
        for (float val:floatSizes) {
            total += val;
            childSizes.add((int)val);
            Log.d(TAG,"size added:"+val);
        }
        size = (int)total;
        this.screenSize = screenSize;
    }

    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        return size;
    }

    @Override
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        return screenSize;
    }

    @Override
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        if (getChildCount()==0) {
            return 0;
        } else {
            View firstChild = getChildAt(0);
            if (firstChild!=null) {
                int firstChildPosition = getPosition(firstChild);
                scrolledY = (int)-firstChild.getY();
                for (int i=0;i<firstChildPosition;i++) {
                    if (i<childSizes.size()) {
                        scrolledY += childSizes.get(i);
                    }
                }
            }
        }
        return 0;
    }

    public ArrayList<Integer> getChildSizes() {
        return childSizes;
    }

    public int getScrollY() {
        return scrolledY;
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return super.computeScrollVectorForPosition(targetPosition);
    }

    public int getTop(int position) {
        // Get the top of the required position
        int y=0;
        for (int i=0; i<position; i++) {
            y += childSizes.get(i);
        }
        Log.d(TAG,"y="+y);
        return y;
    }
}
