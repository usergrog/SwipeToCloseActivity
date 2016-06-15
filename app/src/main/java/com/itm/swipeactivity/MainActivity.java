package com.itm.swipeactivity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();
    Toolbar mToolbar;
    View mRootView;
    private int mY;
    private int mHeight;
    private int mWidth;
    ImageView ivCollapsedCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        ivCollapsedCover = (ImageView) findViewById(R.id.ivCollapsedCover);

        Picasso.with(this).load("http://science-all.com/images/landscape/landscape-03.jpg").into(ivCollapsedCover);

        mRootView = findViewById(android.R.id.content);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mY = 0;
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        getSupportActionBar().setTitle("Title");
        startAnimation();

        final ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                Log.v("DEBUG", String.format("--- %2.2f", val));
                //mBinding.flProgress.setAlpha(val);
            }

        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                mBinding.flProgress.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();



    }

    private void startAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Interpolator interpolator;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    interpolator = AnimationUtils.loadInterpolator(MainActivity.this,
                            android.R.interpolator.linear_out_slow_in);
                } else {
                    interpolator = new DecelerateInterpolator();
                }

/*
                final ValueAnimator animator = ValueAnimator.ofInt(500, mEmptyView.getMeasuredHeight());
                animator.setInterpolator(interpolator);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int val = (Integer) animation.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = mEmptyView.getLayoutParams();
                        layoutParams.height= val;
                        mEmptyView.setLayoutParams(layoutParams);
//                        if (animation.getAnimatedValue().equals(desiredValue) && listener != null) {
//                            listener.onEntranceAnimationDone();
//                        }
                    }
                });

                animator.start();
*/
            }
        }, 100);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
//        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());

//            ViewGroup.LayoutParams layoutParams = mEmptyView.getLayoutParams();
//            layoutParams.height = layoutParams.height - (int) distanceY;
//            mEmptyView.setLayoutParams(layoutParams);

            mY = mY - (int) distanceY;
            mRootView.setY(mY);
            if (Math.abs(mY) > mHeight / 4){
                slideOut(mY < 0);
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }

    private void slideOut(boolean upAnimation) {
        finish();
        if (upAnimation) {
            overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
        } else{
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_down);
        }
    }

}
