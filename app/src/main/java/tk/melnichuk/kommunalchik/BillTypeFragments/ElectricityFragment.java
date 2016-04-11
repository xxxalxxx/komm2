package tk.melnichuk.kommunalchik.BillTypeFragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 30.03.16.
 */
public class ElectricityFragment extends BaseBillFragment {

    String mFrom, mTo, mUnits, mOver;
    EditText mStep1, mStep2;
    View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        mFrom  = getResources().getString(R.string.e_from);
        mTo    = getResources().getString(R.string.e_to);
        mUnits = getResources().getString(R.string.e_units);
        mOver  = getResources().getString(R.string.e_over);

        mStep1 = (EditText) view.findViewById(R.id.step1_input);
        mStep2 = (EditText) view.findViewById(R.id.step2_input);

        updateElectricityStep1(mView, mStep1.getText().toString());
        updateElectricityStep2(mView, mStep2.getText().toString());


        mStep1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("_CH", s.toString());
                updateElectricityStep1(view, s.toString());
            }
        });


        mStep2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateElectricityStep2(view, s.toString());
            }
        });


    }

    void updateElectricityStep1(View v,String s){
        if(s.isEmpty()) s = "0";
        String text = mFrom + ' ' + s + ' ' + mTo;
        ((TextView) v.findViewById(R.id.r4c0)).setText(text);
    }

    void updateElectricityStep2(View v,String s){
        if(s.isEmpty()) s = "0";
        String text = mOver + ' ' + s + ' ' + mUnits;
        ((TextView) v.findViewById(R.id.r5c0)).setText(text);

    }

    @Override
    public void calc() {
        super.calc();
        if(mView != null){
            if(mStep1 != null)
                updateElectricityStep1(mView, mStep1.getText().toString());
            if(mStep2 != null)
                updateElectricityStep2(mView, mStep2.getText().toString());
        }

    }
}
