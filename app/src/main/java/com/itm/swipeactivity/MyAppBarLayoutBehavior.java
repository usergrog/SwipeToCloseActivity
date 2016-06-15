package com.itm.swipeactivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

/**
 * Created by Alexey Sidorenko on 15-Jun-16.
 */
public class MyAppBarLayoutBehavior extends AppBarLayout.Behavior {
    private View mTargetView;

    private static final String TAG = MyAppBarLayoutBehavior.class.getSimpleName();
    private int mStartY;
    private int mNewHeight;
    private AppBarLayout.OnOffsetChangedListener mOffsetChangedListener;
    private int mCurrentOffset;
    private Context mContext;
    private int mWidth;
    private int mHeight;

    public MyAppBarLayoutBehavior() {
        init();
    }

    public MyAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mCurrentOffset = verticalOffset;
            }
        };

        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

    }

    @Override
    protected void layoutChild(CoordinatorLayout parent, AppBarLayout child, int layoutDirection) {
        super.layoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        mStartY = 0;
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        Log.v(TAG, "onNestedPreScroll " + dy);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        Log.v(TAG, "dyConsumed " + dyConsumed + " dyUnconsumed " + dyUnconsumed + " " + target.getClass().getSimpleName()
                + " " + child.getClass().getSimpleName() + " mCurrentOffset " + mCurrentOffset);

        if (dyUnconsumed < 0 && mTargetView != null && mCurrentOffset == 0) {
            ViewGroup.LayoutParams ll = mTargetView.getLayoutParams();
            mNewHeight = ll.height - dyUnconsumed;
            if (mNewHeight > 0 && mNewHeight <= 50) {
                Log.v(TAG, "newHeight " + mNewHeight);
                ll.height = mNewHeight;
                mTargetView.setLayoutParams(ll);
            } else if (mNewHeight > 50 && mContext instanceof Activity) {
                startExitAnimation();
            }
        }
    }

    private void startExitAnimation() {
        final ValueAnimator animator = ValueAnimator.ofInt(50, mHeight / 2);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams ll = mTargetView.getLayoutParams();
                ll.height = val;
                mTargetView.setLayoutParams(ll);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ((Activity) mContext).finish();
            }
        });
        animator.start();
    }


    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
        super.onStopNestedScroll(coordinatorLayout, abl, target);
        if (mTargetView != null && mNewHeight > 0) {
            ViewGroup.LayoutParams ll = mTargetView.getLayoutParams();
            ll.height = 0;
            mNewHeight = 0;
            mTargetView.setLayoutParams(ll);
        }
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout child, int layoutDirection) {
        boolean superLayout = super.onLayoutChild(parent, child, layoutDirection);
        if (mTargetView == null) {
            mTargetView = parent.getRootView().findViewWithTag("testTag");
        }
        return superLayout;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, AppBarLayout child, View dependency) {
        child.addOnOffsetChangedListener(mOffsetChangedListener);
        return super.layoutDependsOn(parent, child, dependency);
    }
}
