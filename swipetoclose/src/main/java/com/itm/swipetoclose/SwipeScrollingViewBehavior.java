package com.itm.swipetoclose;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Alexey Sidorenko on 22-Jun-16.
 */
public class SwipeScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {
    private static final String TAG = "ScrollDebug";

    public SwipeScrollingViewBehavior() {
        super();
    }

    public SwipeScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
//        Log.d(TAG, "layoutDependsOn " + dependency);
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
  //      Log.d(TAG, "onDependentViewChanged " + dependency);
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean setTopAndBottomOffset(int offset) {
        Log.d(TAG, "setTopAndBottomOffset " + offset);
        return super.setTopAndBottomOffset(offset);
    }

    @Override
    public int getTopAndBottomOffset() {
        Log.d(TAG, "getTopAndBottomOffset ");
        return super.getTopAndBottomOffset();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
//        Log.d(TAG, "onStartNestedScroll " + child + "\n" + directTargetChild + "\n" + target);
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "onNestedScroll ");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        Log.d(TAG, "onNestedPreScroll ");
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

}
