package tk.melnichuk.kommunalchik.BillTypeFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tk.melnichuk.kommunalchik.Models.WaterModel;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 30.03.16.
 */
public class WaterFragment extends BaseBillFragment {

    int mCurrSpinnerPos = 0;
    final static int HIDEABLE_SEGMENT_ID = R.id.text4;
    View[] mHideableRow;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int[] hideableValues = new int[]{R.id.r3c3,R.id.r3c4,R.id.r3c5,R.id.r3c6,R.id.r3c7,R.id.r3c8};
        mHideableRow = new View[hideableValues.length];

        for(int i=0;i<hideableValues.length;++i){
            mHideableRow[i] = view.findViewById(hideableValues[i]);
        }

        if(savedInstanceState != null) {
            mCurrSpinnerPos = savedInstanceState.getInt("spinnerPos",0);
        }

        Spinner spinner = (Spinner)view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                int visibility = position == WaterModel.COUNTER_NONE ? View.GONE : View.VISIBLE;
                setHideableRowVisility(visibility);
                setHideableSegmentsRowVisibility(visibility);
                mCurrSpinnerPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    @Override
    public ViewGroup addSegment() {
        ViewGroup newView = super.addSegment();
        newView.findViewById(HIDEABLE_SEGMENT_ID).setVisibility(mCurrSpinnerPos == WaterModel.COUNTER_NONE ? View.GONE : View.VISIBLE);
        return newView;
    }

    @Override
    public ViewGroup addSegment(ArrayList<String> segmentItem) {
        ViewGroup newView = super.addSegment(segmentItem);
        newView.findViewById(HIDEABLE_SEGMENT_ID).setVisibility(mCurrSpinnerPos == WaterModel.COUNTER_NONE ? View.GONE : View.VISIBLE);
        return newView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinnerPos", mCurrSpinnerPos);
    }

    void setHideableRowVisility(int visibility){
        if(mHideableRow != null){
            for(int i=0;i<mHideableRow.length;++i){
                mHideableRow[i].setVisibility(visibility);
            }
        }

    }

    void setHideableSegmentsRowVisibility(int visibility){
        if(mBillContainer == null || mSegmentContainer == null) return;

        int len = mSegmentContainer.getChildCount();
        for(int i=0;i<len;++i){
            ViewGroup segment = (ViewGroup) mSegmentContainer.getChildAt(i);
            segment.findViewById(HIDEABLE_SEGMENT_ID).setVisibility(visibility);
        }
    }


}
