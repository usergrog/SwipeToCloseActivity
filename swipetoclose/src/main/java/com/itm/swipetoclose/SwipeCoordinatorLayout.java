package com.itm.swipetoclose;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Alexey Sidorenko on 22-Jun-16.
 */
public class SwipeCoordinatorLayout extends android.support.design.widget.CoordinatorLayout implements
        GestureDetector.OnGestureListener {
    private static final String TAG = SwipeCoordinatorLayout.class.getSimpleName();
    private final Context mContext;
    private View mTopView;
    private int mCurHeight;
    private int mCloseMargin;
    private SwipeActionCallback mSwipeActionCallback;
    private AppBarLayout.OnOffsetChangedListener mOffsetChangedListener;
    private int mCurrentOffset;
    private AppBarLayout mAppBarLayout;
    private GestureDetectorCompat mDetector;
    private int mPreviousDy = 0;

    public SwipeCoordinatorLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SwipeCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setupAttrs(context, attrs);
        init();
    }

    public SwipeCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setupAttrs(context, attrs);
        init();
    }

    private void setupAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SwipeToCloseBehavior, 0, 0);
        try {
            mCloseMargin = a.getDimensionPixelSize(R.styleable.SwipeCoordinatorLayout_layout_close_margin, 0);
        } finally {
            a.recycle();
        }
    }


    private void init() {
        // will save current offset for app bar layout
        mOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mCurrentOffset = verticalOffset;
            }
        };

        // app bar doesn't trigger Behavior methods except onTouch....
        // will use gesture detector for handling scrolling
        mDetector = new GestureDetectorCompat(mContext, this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            hideBothViews();
            return super.onTouchEvent(ev);
        } else {
            return this.mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
        }
    }

    private void slideOut(boolean upAnimation) {
        if (mContext instanceof Activity) {
            Activity activity = ((Activity) mContext);
            activity.finish();
            if (upAnimation) {
                activity.overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
            } else {
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_down);
            }

        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        // we need call super.onStartNestedScroll, it handles nested scrolling for example for AppBarLayout
        boolean result = super.onStartNestedScroll(child, target, nestedScrollAxes);
        return true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if ((target instanceof NestedScrollView &&
                target.getScrollY() > 0) || mCurrentOffset != 0) {
            super.onNestedPreScroll(target, dx, dy, consumed);
        } else if (consumed[1] == 0) {
            if (mTopView != null) {
                handleTopScroll(dy, consumed);
            }
        }
    }

    private void handleTopScroll(int dy, int[] consumed) {
        int delta = (mPreviousDy + dy) / 2;
        mPreviousDy = dy;
        ViewGroup.LayoutParams ll = mTopView.getLayoutParams();
        mCurHeight = ll.height - delta;
        if (mCurHeight > 0 && mCurHeight <= mCloseMargin) {
            ll.height = mCurHeight;
            mTopView.setLayoutParams(ll);
            Log.v(TAG, "mCurHeight " + mCurHeight);
            consumed[1] = dy;
        } else if (mCurHeight > mCloseMargin) {
            if (mSwipeActionCallback != null) {
                mSwipeActionCallback.onDown();
            } else {
                slideOut(false);
            }
        } else if (mCurHeight < 0) {
            mCurHeight = 0;
        }
    }

    @Override
    public void onLayoutChild(View child, int layoutDirection) {
        // find top and bottom views
        if (mTopView == null) {
            mTopView = getRootView().findViewWithTag("topView");
        }
        if (mAppBarLayout == null) {
            mAppBarLayout = (AppBarLayout) getRootView().findViewWithTag("appBarLayout");
            mAppBarLayout.addOnOffsetChangedListener(mOffsetChangedListener);
        }
        super.onLayoutChild(child, layoutDirection);
    }

    @Override
    public void onStopNestedScroll(View target) {
        super.onStopNestedScroll(target);
        hideBothViews();
    }

    private void hideBothViews() {
        if (mCurHeight > 0) {
            final ViewGroup.LayoutParams llTop = mTopView.getLayoutParams();

            final ValueAnimator animator = ValueAnimator.ofInt(mCurHeight, 0);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(300);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int val = (Integer) animation.getAnimatedValue();
                    if (llTop.height > 0) {
                        llTop.height = val;
                        mTopView.setLayoutParams(llTop);
                    }
                }

            });
            animator.start();
            mCurHeight = 0;
            mPreviousDy = 0;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int[] consumed = new int[2];
        Log.v(TAG, "mCurrentOffset " + mCurrentOffset);
        if (mCurrentOffset == 0 || mCurHeight > 0) {
            handleTopScroll((int) distanceY, consumed);
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface SwipeActionCallback {
        void onUp();

        void onDown();
    }

    public void setSwipeActionCallback(SwipeActionCallback swipeActionCallback) {
        mSwipeActionCallback = swipeActionCallback;
    }
}
