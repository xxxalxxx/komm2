package tk.melnichuk.kommunalchik;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import tk.melnichuk.kommunalchik.BillTypeFragments.BaseBillFragment;
import tk.melnichuk.kommunalchik.CustomViews.FixedSpeedScroller;
import tk.melnichuk.kommunalchik.CustomViews.SlidingTabLayout;

/**
 * Created by al on 21.03.16.
 */
public class BillsFragment extends Fragment {

    public static final int STATE_NEW = 0, STATE_CONTINUED = 1, STATE_FROM_DATABASE = 2;
    public int mState;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    public int[] mMipmapResourceNames;

    public int mLandScapeHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.frag_bills, container, false);
        mMipmapResourceNames = getMipmapResourceNames();

      //  v.findViewById(R.id.main_container).setMinimumHeight(11000);

       // v.findViewById(R.id.main_container_inner).setMinimumHeight(mLandScapeHeight);
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new BillsPagerAdapter(getChildFragmentManager()));

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
            // scroller.setFixedDuration(5000);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabViewLayoutId(R.layout.bills_menu_icon);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(getColors());

       // mViewPager.setMinimumHeight(mLandScapeHeight);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mLandScapeHeight = Math.min(metrics.widthPixels,metrics.heightPixels);

        Log.d("MMET", "h " + metrics.heightPixels + "  w" + metrics.widthPixels);
        Log.d("SLDIM", "h " + view.findViewById(R.id.fab_save).getHeight());




    }

    public class BillsPagerAdapter extends FragmentStatePagerAdapter {
        Field mScroller;
        FixedSpeedScroller mFixedSpeedScroller;

        public void setDuration(int coeff) {

            try {

                FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), new DecelerateInterpolator());
                scroller.setFixedDuration(coeff);
                mScroller.set(mViewPager,scroller);

            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }

        }

        public BillsPagerAdapter(FragmentManager fm) {
            super(fm);

            try {
                mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mFixedSpeedScroller =  new FixedSpeedScroller(mViewPager.getContext(), new DecelerateInterpolator());
            } catch (NoSuchFieldException e){}

        }

        public int getMipMapResourceName(int pos) {
            return mMipmapResourceNames[pos];
        }
        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item " + (position + 1);
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        @Override
        public Fragment getItem(int position) {
            return new BaseBillFragment();
        }

    }


    int[] getColors() {
        return new int[] {
            ContextCompat.getColor(getContext(), R.color.communal_color) ,
            ContextCompat.getColor(getContext(), R.color.gas_color),
            ContextCompat.getColor(getContext(), R.color.cold_water_color),
            ContextCompat.getColor(getContext(), R.color.waste_water_color),
            ContextCompat.getColor(getContext(), R.color.hot_water_color),
            ContextCompat.getColor(getContext(), R.color.heating_color),
            ContextCompat.getColor(getContext(), R.color.electricity_color),
            ContextCompat.getColor(getContext(), R.color.phone_color)
        };
    }

    int[] getMipmapResourceNames() {
        return new int[] {
                R.mipmap.communal,
                R.mipmap.gas,
                R.mipmap.cold_water,
                R.mipmap.waste_water,
                R.mipmap.hot_water,
                R.mipmap.heating,
                R.mipmap.electricity,
                R.mipmap.phone
        };
    }

}
