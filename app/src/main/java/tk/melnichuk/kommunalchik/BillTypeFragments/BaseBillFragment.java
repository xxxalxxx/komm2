package tk.melnichuk.kommunalchik.BillTypeFragments;

import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import tk.melnichuk.kommunalchik.BillsFragment;
import tk.melnichuk.kommunalchik.CustomViews.UnitTypesKeyboard;
import tk.melnichuk.kommunalchik.CustomViews.ViewPagerHorizontalScrollView;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 26.03.16.
 */
public class BaseBillFragment extends Fragment {
    View mView;
    UnitTypesKeyboard mCustomKeyboard;
    BillsFragment mBillsFragment;
    int mSegmentViewId = 0;
    /*BaseBillFragment(BillsFragment billsFragment) {
        mBillsFragment = billsFragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView =  inflater.inflate(R.layout.table_electricity, container, false);

        mCustomKeyboard= new UnitTypesKeyboard(getActivity(), R.id.keyboardview, R.xml.keyboard_portrait );

        mCustomKeyboard.registerEditText(R.id.r2c1);


        getActivity().findViewById(R.id.calc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "123", Toast.LENGTH_LONG).show();
            }
        });

        final ViewPager vp = (ViewPager) getActivity().findViewById(R.id.viewpager);
        Log.d("VVVVP", ""+vp);
/*
        final ViewPagerHorizontalScrollView scrollView = (ViewPagerHorizontalScrollView) mView;
        scrollView.setScrollViewListener(new ViewPagerHorizontalScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ViewPagerHorizontalScrollView scrollView, int x, int y, int oldx, int oldy) {
                if(vp == null) return;
                View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
                if(view == null) return;
                try {
                    int scrollX = scrollView.getScrollX();
                    int viewLeft = view.getLeft();
                    int diffEnd = view.getRight() - (scrollView.getWidth() + scrollX) + viewLeft;
                    int diffStart = viewLeft - scrollX;
                    int dx = scrollView.mDx;
                    int currVpItem = vp.getCurrentItem();
                    Log.d("CURRITEM", currVpItem + "");
                    // if diff is zero, then the bottom has been reached
                    if (diffEnd == 0 && dx > 150 && currVpItem < vp.getChildCount() - 1) {
                        Toast.makeText(getActivity(), "end", Toast.LENGTH_SHORT).show();
                        vp.setCurrentItem(currVpItem + 1, true);
                        scrollView.mDx = scrollView.mX = 0;
                        // do stuff
                    }

                    if (diffStart == 0 && dx < -150 && currVpItem > 0) {
                        Toast.makeText(getActivity(), "start", Toast.LENGTH_SHORT).show();
                        vp.setCurrentItem(currVpItem - 1, true);
                        scrollView.mDx = scrollView.mX = 0;
                    }
                    Log.d("SCRLV", diffStart + " dx:" + dx + " ddx:" + scrollView.mDx);

                }catch (Exception e){}

            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(scrollView == null) return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        scrollView.setTouchX((int)event.getX());
                        break;
                    case MotionEvent.ACTION_UP:
                        scrollView.setTouchDx((int)event.getX());
                        break;

                    default:
                        break;
                }
                return false;
            }
        });*/

        return mView;
    }

    

    public void setSegmentViewId(int segmentViewResourceId){
        mSegmentViewId = segmentViewResourceId;
    }

}
