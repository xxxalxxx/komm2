package tk.melnichuk.kommunalchik.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by al on 27.03.16.
 */



public class ViewPagerHorizontalScrollView extends HorizontalScrollView {

    public int mX = 0;
    public int mDx = 0;

    public interface ScrollViewListener {
        void onScrollChanged(ViewPagerHorizontalScrollView scrollView,
                             int x, int y, int oldx, int oldy);
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
    }

    public ViewPagerHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollViewListener != null) {
            mScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
    
    
}
