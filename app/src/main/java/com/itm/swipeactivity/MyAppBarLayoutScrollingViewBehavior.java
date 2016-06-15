package com.itm.swipeactivity;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Alexey Sidorenko on 15-Jun-16.
 */
public class MyAppBarLayoutScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {
    private static final String TAG = MyAppBarLayoutScrollingViewBehavior.class.getSimpleName();
    private static final String VIEW_TAG = "overScrollScale";
    private View mRootView;
    private int mInitialHeight;
    private int mStartY;

    public MyAppBarLayoutScrollingViewBehavior() {
    }

    public MyAppBarLayoutScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        Log.v(TAG, "layoutDependsOn " + child);
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        Log.v(TAG, "onDependentViewChanged " + child + " " + dependency);
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        Log.v(TAG, "onLayoutChild " + child);
        boolean superLayout = super.onLayoutChild(parent, child, layoutDirection);
        if (mRootView == null) {
            mRootView = parent.getRootView();
        }
        return superLayout;
    }


/*
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
//        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        Log.v(TAG, "newHeight dy " + dy + " " + mRootView.getY());

        if (mRootView != null) {
            int newY = mStartY - dy;
            if (newY > 0 && newY <= 150) {
                Log.v(TAG, "newHeight " + newY);
                mRootView.setY(newY);
                consumed[1] = dy;
            }
        }
    }
*/

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.v(TAG, "dyConsumed " + dyConsumed + " dyUnconsumed " + dyUnconsumed + " " + target.getClass().getSimpleName()
                + " " + child.getClass().getSimpleName());
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
/*
        if (mRootView != null) {
            mStartY = (int) mRootView.getY();
        }
*/
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}
