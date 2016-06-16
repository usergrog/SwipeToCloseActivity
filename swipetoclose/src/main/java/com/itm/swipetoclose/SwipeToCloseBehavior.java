package com.itm.swipetoclose;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alexey Sidorenko on 15-Jun-16.
 */
public class SwipeToCloseBehavior extends AppBarLayout.Behavior implements
        GestureDetector.OnGestureListener {
    private static final String TAG = SwipeToCloseBehavior.class.getSimpleName();
    private boolean mAllowBottom;
    private SwipeActionCallback mSwipeActionCallback;
    private View mTopView;
    private View mBottomView;
    private int mNewHeight;
    private AppBarLayout.OnOffsetChangedListener mOffsetChangedListener;
    private int mCurrentOffset;
    private Context mContext;
    private GestureDetectorCompat mDetector;
    private int mCloseMargin;

    public SwipeToCloseBehavior() {
        init();
    }

    public SwipeToCloseBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setupAttrs(context, attrs);
        init();
    }

    private void setupAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SwipeToCloseBehavior, 0, 0);
        try {
            mCloseMargin = a.getDimensionPixelSize(R.styleable.SwipeToCloseBehavior_layout_close_margin, 0);
            mAllowBottom = a.getBoolean(R.styleable.SwipeToCloseBehavior_layout_allow_bottom, true);
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
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        checkScrollTopView(dyUnconsumed);
        if (mAllowBottom) {
            checkScrollBottomView(dyUnconsumed);
        }
    }

    private boolean checkScrollBottomView(int distanceY) {
        if (distanceY > 0 && mBottomView != null) {
            ViewGroup.LayoutParams ll = mBottomView.getLayoutParams();
            mNewHeight = ll.height + distanceY;
            if (mNewHeight > 0 && mNewHeight <= mCloseMargin) {
                ll.height = mNewHeight;
                mBottomView.setLayoutParams(ll);
            } else if (mNewHeight > mCloseMargin && mContext instanceof Activity) {
                if (mSwipeActionCallback != null) {
                    mSwipeActionCallback.onUp();
                } else {
                    slideOut(true);
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkScrollTopView(int distanceY) {
        if (distanceY < 0 && mTopView != null && mCurrentOffset == 0) {
            ViewGroup.LayoutParams ll = mTopView.getLayoutParams();
            mNewHeight = ll.height - distanceY;
            if (mNewHeight > 0 && mNewHeight <= mCloseMargin) {
                ll.height = mNewHeight;
                mTopView.setLayoutParams(ll);
            } else if (mNewHeight > mCloseMargin && mContext instanceof Activity) {
                if (mSwipeActionCallback != null) {
                    mSwipeActionCallback.onDown();
                } else {
                    slideOut(false);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
        super.onStopNestedScroll(coordinatorLayout, abl, target);
        hideBothViews();
    }

    private void hideBothViews() {
        if (mNewHeight > 0) {
            ViewGroup.LayoutParams ll = mTopView.getLayoutParams();
            ll.height = 0;
            mTopView.setLayoutParams(ll);
            ll = mBottomView.getLayoutParams();
            ll.height = 0;
            mBottomView.setLayoutParams(ll);
            mNewHeight = 0;
        }
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout child, int layoutDirection) {
        boolean superLayout = super.onLayoutChild(parent, child, layoutDirection);
        // find top and bottom views
        if (mTopView == null) {
            mTopView = parent.getRootView().findViewWithTag("topView");
        }
        if (mBottomView == null) {
            mBottomView = parent.getRootView().findViewWithTag("bottomView");
        }
        return superLayout;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, AppBarLayout child, View dependency) {
        child.addOnOffsetChangedListener(mOffsetChangedListener);
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            hideBothViews();
            return super.onTouchEvent(parent, child, ev);
        } else {
            return this.mDetector.onTouchEvent(ev) || super.onTouchEvent(parent, child, ev);
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
        return checkScrollTopView((int) distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
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

    public void setSwipeActionCallback(SwipeActionCallback swipeActionCallback) {
        mSwipeActionCallback = swipeActionCallback;
    }

    public interface SwipeActionCallback {
        void onUp();

        void onDown();
    }
}

