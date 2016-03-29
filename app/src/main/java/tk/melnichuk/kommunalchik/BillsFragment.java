package tk.melnichuk.kommunalchik;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import tk.melnichuk.kommunalchik.BillTypeFragments.BaseBillFragment;
import tk.melnichuk.kommunalchik.CustomViews.FixedSpeedScroller;
import tk.melnichuk.kommunalchik.CustomViews.SlidingTabLayout;
import tk.melnichuk.kommunalchik.CustomViews.ViewPagerHorizontalScrollView;
import tk.melnichuk.kommunalchik.Helpers.HeightAnimation;

/**
 * Created by al on 21.03.16.
 */
public class BillsFragment extends Fragment {

    public static final int STATE_NEW = 0, STATE_CONTINUED = 1, STATE_FROM_DATABASE = 2;
    public int mState;


    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private int mPrevPos = 0, mPrevHeight = 0;
    private int mTableBottomOffsetDp = 0;
    public static int[] mMipmapResourceNames = new int[]{
            R.mipmap.communal,
            R.mipmap.gas,
            R.mipmap.cold_water,
            R.mipmap.waste_water,
            R.mipmap.hot_water,
            R.mipmap.heating,
            R.mipmap.electricity,
            R.mipmap.phone
    };
    public int[] mColors;
    public static int[] mBillNames = new int[]{
            R.string.communal,
            R.string.gas,
            R.string.coldWater,
            R.string.wasteWater,
            R.string.hotWater,
            R.string.heating,
            R.string.electricity,
            R.string.phone
    };

    public static int[] mBillLayouts = new int[]{
            R.layout.table_communal,
            R.layout.table_gas,
            R.layout.table_water,
            R.layout.table_water,
            R.layout.table_water,
            R.layout.table_heating,
            R.layout.table_electricity,
            R.layout.table_phone,
    };

    public static int[] mSegmentLayouts = new int[]{
            R.layout.segment_communal,
            R.layout.segment_default,
            R.layout.segment_default,
            R.layout.segment_default,
            R.layout.segment_default,
            R.layout.segment_heating,
            R.layout.segment_electricity,
            R.layout.segment_default,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mPrevHeight = savedInstanceState.getInt("prevViewPagerHeight",0);
            mPrevPos = savedInstanceState.getInt("viewPagerPos",0);
        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.frag_bills, container, false);
        mColors = getColors();

        final float scale = getContext().getResources().getDisplayMetrics().density;
        mTableBottomOffsetDp = (int) (10 * scale + 0.5f);


        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new BillsPagerAdapter(getChildFragmentManager()));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {

                animateViewPagerPageChange(position);
                mPrevPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabViewLayoutId(R.layout.bills_menu_icon);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(getColors());

      //  resizeLayout(p);
        animateViewPagerPageChange(mPrevPos);
       // mViewPager.onPageSelected(0);




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
        @Override
        public Fragment getItem(final int position) {

            BaseBillFragment bbf = new BaseBillFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("segLayout",mSegmentLayouts[position]);
            bundle.putInt("mainLayout",mBillLayouts[position]);
            bbf.setArguments(bundle);

            return bbf;
        }

    }

    void animateViewPagerPageChange(final int position){

                    /*   ObjectAnimator animation = ObjectAnimator.ofFloat(v, "rotationX", 0.0f, 0f);
                animation.setDuration(1000);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());*/

        final TextView v = (TextView) getActivity().findViewById(R.id.bill_title);
        v.setText(mBillNames[position]);
        Log.d("_SCROLLER", " IN "+ position);

        resizeLayout(position);

        new Thread(new Runnable()
        {
            public void run()
            {


                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                TextView textView = new TextView(getContext());
                textView.setText(mBillNames[mViewPager.getCurrentItem()]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, v.getTextSize());
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.AT_MOST);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                textView.measure(widthMeasureSpec, heightMeasureSpec);

                boolean doAnimateHeight = v.getHeight() != textView.getMeasuredHeight();
                final HeightAnimation heightAnim = doAnimateHeight ? new HeightAnimation(v, v.getHeight(), textView.getMeasuredHeight()) : null;
                if(doAnimateHeight) {
                    heightAnim.setDuration(500);
                }


                v.post(new Runnable() {
                    public void run() {
                      //  int pos = position;
                        Log.d("_SCROLLER", position + "");

                        if(heightAnim != null)
                            v.startAnimation(heightAnim);
                    }
                });
            }
        }).start();

        final ObjectAnimator animator = ObjectAnimator.ofInt(v, "backgroundColor", mColors[mPrevPos],mColors[position]);
        animator.setDuration(500);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }


    private void resizeLayout(final int position){

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) return;

                final ViewPagerHorizontalScrollView rl = new ViewPagerHorizontalScrollView(getContext());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
                rl.setLayoutParams(lp);
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.AT_MOST);
                final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

                if(mViewPager != null) {
                    final RelativeLayout.LayoutParams vplp = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ViewPagerHorizontalScrollView rll = (ViewPagerHorizontalScrollView) getLayoutInflater(new Bundle()).inflate(BillsFragment.mBillLayouts[position], rl, false);
                            rll.measure(widthMeasureSpec, heightMeasureSpec);
                            int h = rll.getMeasuredHeight() + mTableBottomOffsetDp;

                            if (h > 0) {

                                Log.d("_RLL", h + " " + vplp.height);
                                if (mPrevHeight == 0) mPrevHeight = vplp.height;
                                vplp.height = h;

                                if (mPrevHeight != h) {
                                    HeightAnimation heightAnim = new HeightAnimation(mViewPager, mPrevHeight, h);
                                    heightAnim.setDuration(500);
                                    mViewPager.startAnimation(heightAnim);
                                    //   mViewPager.setLayoutParams(vplp);
                                    mPrevHeight = h;

                                }

                            }

                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mViewPager != null)
        outState.putInt("viewPagerPos",mViewPager.getCurrentItem());
        outState.putInt("prevViewPagerHeight", mPrevHeight);
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

}
