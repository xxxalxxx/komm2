package tk.melnichuk.kommunalchik.BillTypeFragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import tk.melnichuk.kommunalchik.Models.GasModel;
import tk.melnichuk.kommunalchik.Models.WaterModel;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 30.03.16.
 */
public class GasFragment extends BaseBillFragment {

    int mCurrSpinnerPos = 0;
    View[] mHideableRow;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int[] hideableValues = new int[]{R.id.r1c2,R.id.r2c2,R.id.r3c2,R.id.r2c3,R.id.r3c3,R.id.r2c4,R.id.r3c4};
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
                int visibility = position == GasModel.CALC_BY_RATE ? View.GONE : View.VISIBLE;
                setHideableRowVisility(visibility);
                mCurrSpinnerPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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


}
