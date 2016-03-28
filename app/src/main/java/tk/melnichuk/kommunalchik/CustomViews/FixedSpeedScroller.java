package tk.melnichuk.kommunalchik.CustomViews;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by al on 27.03.16.
 */
public class FixedSpeedScroller extends Scroller {


    private final int DURATION_SPEED = 250;
    private int mDuration = DURATION_SPEED;
    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    public void setFixedDuration(int coeff) {
        mDuration = coeff * DURATION_SPEED;
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
        if(mDuration > DURATION_SPEED) mDuration = DURATION_SPEED;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
        if(mDuration > DURATION_SPEED) mDuration = DURATION_SPEED;
    }

}