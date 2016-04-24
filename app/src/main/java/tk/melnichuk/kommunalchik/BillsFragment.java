package tk.melnichuk.kommunalchik;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tk.melnichuk.kommunalchik.BillTypeFragments.BaseBillFragment;
import tk.melnichuk.kommunalchik.BillTypeFragments.ElectricityFragment;
import tk.melnichuk.kommunalchik.BillTypeFragments.GasFragment;
import tk.melnichuk.kommunalchik.BillTypeFragments.WaterFragment;
import tk.melnichuk.kommunalchik.CustomViews.FixedSpeedScroller;
import tk.melnichuk.kommunalchik.CustomViews.SlidingTabLayout;
import tk.melnichuk.kommunalchik.CustomViews.UnitTypesKeyboard;
import tk.melnichuk.kommunalchik.CustomViews.ViewPagerHorizontalScrollView;
import tk.melnichuk.kommunalchik.DataManagers.BillManager;
import tk.melnichuk.kommunalchik.DataManagers.OptionsManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.BillTable;
import tk.melnichuk.kommunalchik.Helpers.HeightAnimation;

/**
 * Created by al on 21.03.16.
 */
public class BillsFragment extends Fragment {

    public static final int
        STATE_NEW = 0, STATE_CONTINUED = 1, STATE_SAVED = 2,
        BILL_ID_NEW = 0, BILL_ID_CONTINUED = 0;
    public int mState;
    public boolean mDoRestoreBill = false;
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
        Log.d("_LDB", "create");
        if(savedInstanceState != null) {
            mPrevHeight = savedInstanceState.getInt("prevViewPagerHeight",0);
            mPrevPos = savedInstanceState.getInt("viewPagerPos",0);
            mBillId = savedInstanceState.getLong("billId", 0);
            mRelId = savedInstanceState.getLong("relId", 0);
            mState = savedInstanceState.getInt("state",0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_bills, container, false);

        mBillManager = new BillManager(this);
        switch (mState){
            case STATE_CONTINUED:

                OptionsManager opts = new OptionsManager(getContext());
                opts.start();
                mRelId = opts.getSavedRelId();
                Log.d("_LC","cont:"+ mRelId + "");
                mBillId = mBillManager.continueBill(mRelId);
                mState = STATE_CONTINUED;
                break;
            case STATE_NEW:

                mBillId = mBillManager.initNewBill(mRelId);
                mState = STATE_CONTINUED;
                //create new temp table, set default rate values from settings and segments
                break;
            case STATE_SAVED:
                mBillId = mBillManager.initSavedBill(mRelId);
                ((MainActivity)getActivity()).mShowSavedBillExitMessage = true;
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        view.findViewById(R.id.calc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null && mCurrBill != null) {
                    mCurrBill.calc();
                }
            }
        });


        view.findViewById(R.id.fab_segment_global).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "segmentS", Toast.LENGTH_LONG).show();
                mDoRestoreBill = true;

                SegmentFragment segmentFragment = new SegmentFragment();
                segmentFragment.setState(SegmentFragment.STATE_COMMON);
                segmentFragment.setBillId(mBillId);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();

                ft.replace(R.id.fragment_container, segmentFragment, "NewBillsFrag");
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        view.findViewById(R.id.fab_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater(savedInstanceState).inflate(R.layout.alert_dialog_save, null);
                adb.setView(dialogView);

                final DatePicker dp = (DatePicker) dialogView.findViewById(R.id.date_picker);
                final EditText nameEditText  = (EditText) dialogView.findViewById(R.id.name);
                final RadioGroup rg = (RadioGroup)  dialogView.findViewById(R.id.radio_group);
                final RadioButton rbNew =  (RadioButton) dialogView.findViewById(R.id.btn_new_bill);
                final RadioButton rbCurr =  (RadioButton) dialogView.findViewById(R.id.btn_curr_bill);


                Log.d("_REL", mRelId + "");
                if(mRelId == 0) {
                    dialogView.findViewById(R.id.btn_curr_bill).setVisibility(View.GONE);
                    rg.check(R.id.btn_new_bill);
                } else {
                    rg.check(R.id.btn_curr_bill);
                    Cursor c = mBillManager.getRelatedBillCursor(mRelId);

                    String name = c.getString(c.getColumnIndexOrThrow(BillTable.COL_NAME));
                    String date = c.getString(c.getColumnIndexOrThrow(BillTable.COL_DATE));
                    String[] dateVals = date.split("/");
                    Log.d("_REL","restore:"+ date);
                    dp.updateDate(
                            Integer.valueOf(dateVals[2]),
                            Integer.valueOf(dateVals[1]) - 1,// map [1,12] -> [0,11]
                            Integer.valueOf(dateVals[0])
                    );
                    nameEditText.setText(name);
                }

                adb.setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0;i<rg.getChildCount();++i){
                            View child = rg.getChildAt(i);
                            if(child instanceof RadioButton){
                                RadioButton radioButtonChild = (RadioButton) child;
                                if(radioButtonChild.isChecked()) {
                                    String name = nameEditText.getText().toString();
                                    if(name.trim().isEmpty()){
                                        Toast.makeText(getActivity(), R.string.err_new_bill_name_empty, Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    updateFragmentInDb(mCurrBill);

                                    Calendar cal = Calendar.getInstance();
                                    cal.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                                    Date newDate = cal.getTime();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String date = dateFormat.format(newDate);


                                    long resultRelId;
                                    if(radioButtonChild == rbNew) {
                                        resultRelId = mBillManager.createSavedBill(name, date,mBillId);
                                        if(resultRelId != -1) mRelId = resultRelId;
                                    } else if(radioButtonChild == rbCurr){
                                        resultRelId = mBillManager.updateSavedBill(name, date, mBillId, mRelId);
                                        if(resultRelId != -1) mRelId = resultRelId;
                                    }
                                }

                            }
                        }
                    }
                })
               .setNegativeButton(R.string.alert_dialog_no, null)
               .show();



                Toast.makeText(getActivity(), "save", Toast.LENGTH_LONG).show();
            }
        });

        view.findViewById(R.id.fab_excel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "excel", Toast.LENGTH_LONG).show();
                updateFragmentInDb(mCurrBill);
                mBillManager.writeBillsToExcel(mBillId);
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
        outState.putBoolean("doRestoreBill", mDoRestoreBill);




    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("_LC", "stop");
        updateFragmentInDb(mCurrBill);


        if(mState == STATE_CONTINUED) {
            Log.d("_LC", "rel id:"+mRelId);
            OptionsManager opts = new OptionsManager(getContext());
            opts.start();
            opts.setSavedRelId(mRelId);
            opts.save();
        }


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

            boolean pageChanged =  false;
            if (mCurrBill != object) {
                pageChanged = true;
                Log.d("_VPD", " IN ");
                final BaseBillFragment prevBill = mCurrBill;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                      updateFragmentInDb(prevBill);

                    }
                }).start();

                mCurrBill = ((BaseBillFragment) object);
                updateFragmentFromDb(mCurrBill,position);
            }
            mPrevPos = position;


            if(!pageChanged && object != null && mDoRestoreBill){
                updateFragmentFromDb((BaseBillFragment)object,position);
                mDoRestoreBill = false;
            }

        }
    }



    void updateFragmentFromDb(BaseBillFragment bill, int position){
        ArrayList<String> mtd = mBillManager.getMainTableFromDb(mBillId,position);
        long lastModelId = mBillManager.getLastModelId(position);
        ArrayList<ArrayList<String>> segments = mBillManager.getSegmentsFromDb(position, lastModelId);
        Log.d("_DBD", "pos curr:" + mViewPager.getCurrentItem() + " currPos2:" + position + " prevPos:" + mPrevPos + " modelId:"+lastModelId);

        if(mtd != null){
            Log.d("_DBD","out "+ mtd.toString());
            bill.fillMainTableData(mtd);

        }

        if(segments != null) {
            Log.d("_DBD","seg out "+ segments.toString());
            bill.fillSegmentsDataFromDb(segments);
        }
    }

    void updateFragmentInDb(BaseBillFragment bill){
        if(bill == null) return;
        ArrayList<String> mainTableData = bill.getMainTableData();
        ArrayList<ArrayList<String>> segmentsData = bill.getFullSegmentsData();

        if(mainTableData != null && segmentsData != null){
            Log.d("_DBD", " NOT NULL ");
            Log.d("_DBD", mainTableData.toString());
            Log.d("_DBD", segmentsData.toString());
            if(mBillManager != null)
            mBillManager.updateTableData(mainTableData, segmentsData, mBillId, mPrevPos);

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
