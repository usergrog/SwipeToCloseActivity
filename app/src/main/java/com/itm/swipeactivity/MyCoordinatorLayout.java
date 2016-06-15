package com.itm.swipeactivity;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Alexey Sidorenko on 08-Jun-16.
 */
public class MyCoordinatorLayout extends CoordinatorLayout {

    public interface ScrollListener{
        void onScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed);
    }

    private static final String TAG = MyCoordinatorLayout.class.getSimpleName();
    private ScrollListener mScrollListener;


    public MyCoordinatorLayout(Context context) {
        super(context);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.v(TAG, "dyConsumed " + dyConsumed  + " dyUnconsumed " + dyUnconsumed);
        if (mScrollListener != null){
            mScrollListener.onScroll(target,dxConsumed,dyConsumed, dxUnconsumed, dxUnconsumed);
        }
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.v(TAG, "MotionEvent " + ev.toString());

        return super.onTouchEvent(ev);
    }

    public void setScrollListener(ScrollListener scrollListener) {
        mScrollListener = scrollListener;
    }
}
