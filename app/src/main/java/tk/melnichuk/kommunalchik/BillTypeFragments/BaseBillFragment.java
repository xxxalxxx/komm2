package tk.melnichuk.kommunalchik.BillTypeFragments;

import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    int mSegmentLayoutResourceId = 0, mMainLayoutResourceId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainLayoutResourceId = getArguments().getInt("mainLayout");
        mSegmentLayoutResourceId = getArguments().getInt("segLayout");

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView =  inflater.inflate(mMainLayoutResourceId, container, false);

        mCustomKeyboard= new UnitTypesKeyboard(getActivity(), R.id.keyboardview, R.xml.keyboard_landscape );

        mCustomKeyboard.registerEditText(R.id.r2c1);
      //  resizeLayout();

        getActivity().findViewById(R.id.calc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "calc", Toast.LENGTH_LONG).show();
            }
        });

        getActivity().findViewById(R.id.fab_segment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "new segment", Toast.LENGTH_LONG).show();
            }
        });

        getActivity().findViewById(R.id.fab_segment_global).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "segmentS", Toast.LENGTH_LONG).show();
            }
        });

        getActivity().findViewById(R.id.fab_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "save", Toast.LENGTH_LONG).show();
            }
        });

        getActivity().findViewById(R.id.fab_excel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "excel", Toast.LENGTH_LONG).show();
            }
        });

        final ViewPager vp = (ViewPager) getActivity().findViewById(R.id.viewpager);
        Log.d("VVVVP", ""+vp);

        return mView;

    }

}
