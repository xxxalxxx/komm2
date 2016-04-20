package tk.melnichuk.kommunalchik.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

/**
 * Created by al on 27.03.16.
 */



public class ViewPagerHorizontalScrollView extends HorizontalScrollView {

    public int mX = 0;
    public int mDx = 0;
    int mScrollEnablingDist = 0;
    int mScrollPosX = 0;
    int mDisplayWidth = 0;

    public interface ScrollViewListener {
        void onScrollChanged(ViewPagerHorizontalScrollView scrollView,
                             int x, int y, int oldx, int oldy);
    }

    private boolean mScrollable = true;

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(750);
                    mScrollable = true;
                } catch (InterruptedException e) {

                }

            }
        }).start();
    }

    public boolean isScrollable() {
        return mScrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                mDx = mX - (int)ev.getX();
                return super.onTouchEvent(ev);
            case MotionEvent.ACTION_DOWN:
                mX = (int) ev.getX();

/*
                if(!mScrollable && Math.abs(mScrollPosX - mX) > mScrollEnablingDist ) {
                    mScrollable = true;
                }*/
                return super.onTouchEvent(ev);
                //return mScrollable ? super.onTouchEvent(ev) : mScrollable;
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
       // if (!mScrollable) return false;
        //else
        return super.onInterceptTouchEvent(ev);
    }


    public void setTouchX(int x){
        mX = x;
    }

    public void setTouchDx(int x){
        mDx = mX - x;
    }

    private ScrollViewListener mScrollViewListener = null;
    public ViewPagerHorizontalScrollView(Context context) {
        super(context);
    }

    public ViewPagerHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScrollEnablingDistance();
    }

    public ViewPagerHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScrollEnablingDistance();
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }

    private void setScrollEnablingDistance(){
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDisplayWidth = metrics.widthPixels;
        mScrollEnablingDist = mDisplayWidth/2;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        new Thread(new Runnable() {
            @Override
            public void run() {
                View view = (View) getChildAt(getChildCount() - 1);
                int diffEnd = (view.getRight() - (getWidth() + getScrollX()));
                int diffStart = view.getLeft() - getScrollX();
                // if diff is zero, then the bottom has been reached
                if(!isScrollable()) return;
                if(diffEnd == 0) {
                    mScrollPosX = mDisplayWidth;
                    setScrollingEnabled(false);
                } else if(diffStart == 0) {
                    mScrollPosX = getScrollX();
                    setScrollingEnabled(false);
                }

            }
        }).start();

    }
    
    
}
