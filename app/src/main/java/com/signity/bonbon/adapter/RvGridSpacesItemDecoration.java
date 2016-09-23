package com.signity.bonbon.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by rajesh on 26/4/16.
 */
public class RvGridSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public RvGridSpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;

//        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildLayoutPosition(view) == 0) {
//        } else {
//            outRect.top = 0;
//        }
    }
}