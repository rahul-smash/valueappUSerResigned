package com.signity.bonbon.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by Rajinder on 29/9/15.
 */
//http://stackoverflow.com/questions/17490899/using-androids-slidingpanelayout-with-viewpager

/**
 * SlidingPaneLayout that, if closed, checks if children can scroll before it intercepts
 * touch events.  This allows it to contain horizontally scrollable children without
 * intercepting all of their touches.
 * <p/>
 * To handle cases where the user is scrolled very far to the right, but should still be
 * able to open the pane without the need to scroll all the way back to the start, this
 * view also adds edge touch detection, so it will intercept edge swipes to open the pane.
 */
public class PagerEnabledSlidingPaneLayout extends SlidingPaneLayout {

    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mEdgeSlop;

    public PagerEnabledSlidingPaneLayout(Context context) {
        this(context, null);
    }

    public PagerEnabledSlidingPaneLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerEnabledSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ViewConfiguration config = ViewConfiguration.get(context);
        mEdgeSlop = config.getScaledEdgeSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = ev.getX();
                mInitialMotionY = ev.getY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                // The user should always be able to "close" the pane, so we only check
                // for child scrollability if the pane is currently closed.
                if (mInitialMotionX > mEdgeSlop && mInitialMotionY > mInitialMotionX && !isOpen() || canScroll(this, false,
                        Math.round(x - mInitialMotionX), Math.round(x), Math.round(y))) {

                    // How do we set super.mIsUnableToDrag = true?

                    // send the parent a cancel event
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onInterceptTouchEvent(cancelEvent);

//                    return false;
                }
            }
        }

        return super.onInterceptTouchEvent(ev);
//        return false;
    }


    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
    /* special case ViewPagers, which don't properly implement the scrolling interface */
        return checkV && (ViewCompat.canScrollHorizontally(v, -dx) ||
                ((v instanceof ViewPager) && canViewPagerScrollHorizontally((ViewPager) v, -dx)));
    }

    boolean canViewPagerScrollHorizontally(ViewPager p, int dx) {
        return !(dx < 0 && p.getCurrentItem() <= 0 ||
                0 < dx && p.getAdapter().getCount() - 1 <= p.getCurrentItem());
    }
}