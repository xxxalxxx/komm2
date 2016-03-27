package tk.melnichuk.kommunalchik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tk.melnichuk.kommunalchik.BillTypeFragments.BaseBillFragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_bills, container, false);
        mMipmapResourceNames = getMipmapResourceNames();
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new BillsPagerAdapter(getChildFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabViewLayoutId(R.layout.bills_menu_icon);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(getColors());

    }

    public class BillsPagerAdapter extends FragmentPagerAdapter {


        public BillsPagerAdapter(FragmentManager fm) {
            super(fm);
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

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
      //  @Override
    //    public boolean isViewFromObject(View view, Object o) {
      //      return o == view;
       // }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        //@Override
    //    public CharSequence getPageTitle(int position) {
      //      return "Item " + (position + 1);
      //  }
        // END_INCLUDE (pageradapter_getpagetitle)

        @Override
        public Fragment getItem(int position) {
            return new BaseBillFragment();
        }

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */


       /* @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.temp_view,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            // Retrieve a TextView from the inflated View, and update it's text
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText(String.valueOf(position + 1));

            // Return the View
            return view;
        }*/



        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        /*@Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }*/

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
