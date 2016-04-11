package tk.melnichuk.kommunalchik;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

import tk.melnichuk.kommunalchik.BillTypeFragments.BaseBillFragment;
import tk.melnichuk.kommunalchik.BillTypeFragments.ElectricityFragment;
import tk.melnichuk.kommunalchik.BillTypeFragments.GasFragment;
import tk.melnichuk.kommunalchik.BillTypeFragments.WaterFragment;
import tk.melnichuk.kommunalchik.CustomViews.FixedSpeedScroller;
import tk.melnichuk.kommunalchik.CustomViews.SlidingTabLayout;
import tk.melnichuk.kommunalchik.CustomViews.UnitTypesKeyboard;
import tk.melnichuk.kommunalchik.CustomViews.ViewPagerHorizontalScrollView;
import tk.melnichuk.kommunalchik.DataManagers.BillManager;
import tk.melnichuk.kommunalchik.Helpers.HeightAnimation;

/**
 * Created by al on 21.03.16.
 */
public class BillsFragment extends Fragment {

    public static final int STATE_NEW = 0, STATE_CONTINUED = 1, STATE_FROM_DATABASE = 2,
        BILL_ID_NEW = 0, BILL_ID_CONTINUED = 0;
    public int mState;

    public long mBillId, mRelId;
    BillManager mBillManager;

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

    public BaseBillFragment mCurrBill;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mPrevHeight = savedInstanceState.getInt("prevViewPagerHeight",0);
            mPrevPos = savedInstanceState.getInt("viewPagerPos",0);
            mBillId = savedInstanceState.getLong("billId", 0);
            mRelId = savedInstanceState.getLong("relId", 0);
            mState = savedInstanceState.getInt("state",0);
            //restore dataholder state

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_bills, container, false);

        //Bundle b = getArguments();
      //  mState = b.getInt("state");
       // mBillId = b.getInt("billId");
        mBillManager = new BillManager(this);
        switch (mState){
            case STATE_CONTINUED:
                mBillId = mBillManager.continueBill(mRelId);
                mState = STATE_CONTINUED;
                break;
            case STATE_NEW:

                mBillId = mBillManager.initNewBill(mRelId);
                mState = STATE_CONTINUED;
                //create new temp table, set default rate values from settings and segments
                break;

            case STATE_FROM_DATABASE:
                break;
        }


        mColors = getColors();

        final float scale = getContext().getResources().getDisplayMetrics().density;
        mTableBottomOffsetDp = (int) (10 * scale + 0.5f);


        return v;
    }


    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new BillsPagerAdapter(getChildFragmentManager()));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {

                animateViewPagerPageChange(position);
               // mPrevPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        view.findViewById(R.id.calc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null && mCurrBill != null) {
                  //  mCurrBill.getMainTableData();
                   // mCurrBill.getSegmentsData();
                    mCurrBill.calc();
                }
            }
        });

        view.findViewById(R.id.fab_segment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "new segment", Toast.LENGTH_LONG).show();
            }
        });

        view.findViewById(R.id.fab_segment_global).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "segmentS", Toast.LENGTH_LONG).show();
            }
        });

        view.findViewById(R.id.fab_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

                View dialogView = getLayoutInflater(savedInstanceState).inflate(R.layout.alert_dialog_save, null);
                adb.setView(dialogView);

                final DatePicker dp = (DatePicker) dialogView.findViewById(R.id.datePicker);
                final EditText et  = (EditText) dialogView.findViewById(R.id.name);

                dialogView.findViewById(R.id.btn_new_bill).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "n", Toast.LENGTH_LONG).show();
                        //dp.setVisibility(View.VISIBLE);
                        //et.setVisibility(View.VISIBLE);
                    }
                });

                dialogView.findViewById(R.id.btn_curr_bill).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "c", Toast.LENGTH_LONG).show();
                        //dp.setVisibility(View.INVISIBLE);
                        //et.setVisibility(View.INVISIBLE);
                    }
                });


                adb.setPositiveButton(R.string.alert_dialog_yes, null)
                   .setNegativeButton(R.string.alert_dialog_no, null)
                   .show();



                Toast.makeText(getActivity(), "save", Toast.LENGTH_LONG).show();
            }
        });

        view.findViewById(R.id.fab_excel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "excel", Toast.LENGTH_LONG).show();
            }
        });


        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
            mScroller.set(mViewPager, scroller);
        }
        catch (NoSuchFieldException e) {}
        catch (IllegalArgumentException e) {}
        catch (IllegalAccessException e) {}

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabViewLayoutId(R.layout.bills_menu_icon);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(getColors());

        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity != null)
        mainActivity.mKeyboard = new UnitTypesKeyboard(mainActivity, R.id.keyboardview, mainActivity.isLandscapeOrientation() ? R.xml.keyboard_landscape : R.xml.keyboard_portrait );

        animateViewPagerPageChange(mPrevPos);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mViewPager != null)
            outState.putInt("viewPagerPos",mViewPager.getCurrentItem());
        outState.putInt("prevViewPagerHeight", mPrevHeight);
        outState.putLong("billId", mBillId);
        outState.putLong("relId", mRelId);
        outState.putInt("state", mState);
    }


    public class BillsPagerAdapter extends FragmentStatePagerAdapter {
        Field mScroller;
        FixedSpeedScroller mFixedSpeedScroller;

        public void setDuration(int coeff) {

            try {
                FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), new DecelerateInterpolator());
                scroller.setFixedDuration(coeff);
                mScroller.set(mViewPager,scroller);

            }
            catch (IllegalArgumentException e) {}
            catch (IllegalAccessException e) {}

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
            return BillManager.NUM_BILL_TABLES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item " + (position + 1);
        }
        @Override
        public Fragment getItem(int position) {
            if(mViewPager != null)
            Log.d("__POX", position +" curr " + mViewPager.getCurrentItem());
            BaseBillFragment bbf;
            Bundle bundle = new Bundle();

            switch (position){
                case BillManager.INDEX_GAS:
                    bbf = new GasFragment();
                    break;
                case BillManager.INDEX_COLD_WATER:
                case BillManager.INDEX_WASTE_WATER:
                case BillManager.INDEX_HOT_WATER:
                    bbf = new WaterFragment();
                    break;
                case BillManager.INDEX_ELECTRICITY:
                    bbf = new ElectricityFragment();
                    break;
                default:
                    bbf = new BaseBillFragment();
            }



            bundle.putInt("segLayout",mSegmentLayouts[position]);
            bundle.putInt("mainLayout", mBillLayouts[position]);
            bundle.putInt("modelId",position);
            bbf.setArguments(bundle);
           // mCurrBill = bbf;
            return bbf;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, final int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d("_CURRBILL", " IN ");
            if (mCurrBill != object) {
                final BaseBillFragment prevBill = mCurrBill;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (prevBill == null) return;
                        ArrayList<String> mainTableData = prevBill.getMainTableData();
                        ArrayList<ArrayList<String>> segmentsData = prevBill.getFullSegmentsData();

                        if(mainTableData != null && segmentsData != null){
                            Log.d("_DBD", " NOT NULL ");
                            Log.d("_DBD", mainTableData.toString());
                            Log.d("_DBD", segmentsData.toString());

                            mBillManager.updateTableData(mainTableData, segmentsData, mBillId, mPrevPos);

                        }
                    }
                }).start();

                mCurrBill = ((BaseBillFragment) object);
                ArrayList<String> mtd = mBillManager.getMainTableFromDb(mBillId,position);
                long lastModelId = mBillManager.getLastModelId(position);
                ArrayList<ArrayList<String>> segments = mBillManager.getSegmentsFromDb(position, lastModelId);
                Log.d("_DBD", "pos curr:" + mViewPager.getCurrentItem() + " currPos2:" + position + " prevPos:" + mPrevPos + " modelId:"+lastModelId);
                if(mCurrBill.mBillContainer == null) {

                }
                if(mtd != null){
                    Log.d("_DBD","out "+ mtd.toString());
                    mCurrBill.fillMainTableData(mtd);

                }

                if(segments != null) {
                    Log.d("_DBD","seg out "+ segments.toString());
                    mCurrBill.fillSegmentsDataFromDb(segments);
                }
            }
            mPrevPos = position;
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
                                mPrevHeight = h;

                            }
                        }
                        }
                    });
                }
            }
        }).start();
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

    void setState(int state, long relId){

        mState = state;
        mRelId = relId;
    }



}
