package tk.melnichuk.kommunalchik;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tk.melnichuk.kommunalchik.DataManagers.DataHolder;
import tk.melnichuk.kommunalchik.DataManagers.DbManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentBillTypeTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentTable;

/**
 * Created by al on 22.03.16.
 */
public class SegmentFragment extends Fragment{
    String testVar = "ttt";
    public final static int STATE_CREATE = 0, STATE_UPDATE = 1, STATE_COMMON = 2;
    public final static int MODE_DECIMAL = 0, MODE_PERCENT = 1, MODE_FRACTION = 2;
    private int mCurrentMode, mState = -1;
    String mSegmentId = "-1";

    View mView;
    TextView mUnderlineDecimal;
    TextView mUnderlinePercent;
    TextView mUnderlineFraction;

    EditText mInputDecimal;
    EditText mInputPercent;

    EditText mInputFractionNumerator;
    EditText mInputFractionDenominator;
    TextView mFractionDivide;

    TextView mTitle;
    TextView mInputName;

    TextView[] mModes;

    ViewGroup mCheckBoxLayout;

    DataHolder mDataHolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        if(mView != null) testVar = "aaa";
        Log.d("TESTVAR",testVar);
        mView = inflater.inflate(R.layout.frag_segment, container, false);

        mTitle = (TextView) mView.findViewById(R.id.title);
        mInputName = (TextView) mView.findViewById(R.id.name);

        mUnderlineDecimal = (TextView) mView.findViewById(R.id.underline_decimal);
        mUnderlinePercent = (TextView) mView.findViewById(R.id.underline_percent);
        mUnderlineFraction = (TextView) mView.findViewById(R.id.underline_fraction);

        mInputDecimal = (EditText) mView.findViewById(R.id.decimal_input);
        mInputPercent = (EditText) mView.findViewById(R.id.percent_input);
        mInputFractionNumerator = (EditText) mView.findViewById(R.id.numerator);
        mInputFractionDenominator = (EditText) mView.findViewById(R.id.denominator);
        mFractionDivide = (TextView) mView.findViewById(R.id.divide);

        mCheckBoxLayout = (ViewGroup) mView.findViewById(R.id.checkbox_layout);

        mModes = new TextView[]{
            (TextView) mView.findViewById(R.id.decimal_text),
            (TextView) mView.findViewById(R.id.percent_text),
            (TextView) mView.findViewById(R.id.fraction_text)

        };

        mModes[MODE_DECIMAL].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                setUnderlineVisibilities(View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                setInputVisibilities(View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                mCurrentMode = MODE_DECIMAL;
            }
        });

        mModes[MODE_PERCENT].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                setUnderlineVisibilities(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                setInputVisibilities(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                mCurrentMode = MODE_PERCENT;
            }
        });

        mModes[MODE_FRACTION].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                setUnderlineVisibilities(View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                setInputVisibilities(View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                mCurrentMode = MODE_FRACTION;
            }
        });


        if (savedInstanceState != null) {
            mCurrentMode = savedInstanceState.getInt("mode");
            mState = savedInstanceState.getInt("state");
            mSegmentId = String.valueOf(savedInstanceState.getInt("segmentId", -1));
        } else {
            mCurrentMode = 0;
        }



        if(mState == STATE_UPDATE) {
            //if segment is being updated - set text and current mode values from db
            DbManager dbManager  = new DbManager(getContext());
            SQLiteDatabase db = dbManager.getReadableDatabase();
            Cursor c = db.query(
                    SegmentTable.TABLE_NAME,
                    new String[]{SegmentTable.COL_NAME, SegmentTable.COL_UNIT, SegmentTable.COL_VALUE},
                    SegmentTable.COL_ID + " =?",
                    new String[]{mSegmentId},
                    null,
                    null,
                    null,
                    "1"
            );
            if(c.moveToFirst()){

                String name = c.getString(c.getColumnIndexOrThrow(SegmentTable.COL_NAME));
                int unit = c.getInt(c.getColumnIndexOrThrow(SegmentTable.COL_UNIT));
                String value = c.getString(c.getColumnIndexOrThrow(SegmentTable.COL_VALUE));

                mInputName.setText(name);

                switch (unit){
                    case MODE_DECIMAL:
                        mInputDecimal.setText(value);
                        break;
                    case MODE_PERCENT:
                        mInputPercent.setText(value);
                        break;
                    case MODE_FRACTION:
                        String[] parts = value.split("/");
                        mInputFractionNumerator.setText(parts[0]);
                        mInputFractionDenominator.setText(parts[1]);
                }
                //change state of initial mode on firse create
                //use changed mode value otherwise
                if(savedInstanceState == null){
                    mCurrentMode = unit;
                }

            }
            int numBillTypes = mCheckBoxLayout.getChildCount();
            Integer[] billsTypes = new Integer[numBillTypes];
            c = db.query(
                    SegmentBillTypeTable.TABLE_NAME,
                    new String[]{SegmentBillTypeTable.COL_TYPE},
                    SegmentBillTypeTable.COL_SEGMENT_ID + " = ?",
                    new String[]{mSegmentId},
                    null,
                    null,
                    null
            );



            while(c.moveToNext()){
                int segmentId = c.getInt(c.getColumnIndexOrThrow(SegmentBillTypeTable.COL_TYPE));
                billsTypes[segmentId] = new Integer(segmentId);

            }

            db.close();

            for(int i=0;i<numBillTypes;++i){
                View child = mCheckBoxLayout.getChildAt(i);
                if(child instanceof CheckBox && billsTypes[i] == null){
                    ((CheckBox) child).setChecked(false);
                }
            }

        }

        int titleRes = mCurrentMode == STATE_CREATE ? R.string.segment_title_create
                : (mCurrentMode == STATE_UPDATE ? R.string.segment_title_update : R.string.segment_title_common );
        mTitle.setText(titleRes);


        mModes[mCurrentMode].performClick();


        mView.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                getFragmentManager().popBackStackImmediate();
              //  getActivity().onBackPressed();
            }
        });

        mView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                String segmentName = mInputName.getText().toString();//((TextView) mView.findViewById(R.id.name)).getText().toString();
                if(segmentName.isEmpty()) {
                   Toast.makeText(getContext(), R.string.err_new_segment_name_empty, Toast.LENGTH_LONG).show();
                   return;
                }

                String num = "";
                String denom = "1";
                String val = "";

                switch (mCurrentMode){
                    case MODE_DECIMAL:
                        num = mInputDecimal.getText().toString();
                        val = num;
                        break;
                    case MODE_PERCENT:
                        num = mInputPercent.getText().toString();
                        val = num;
                        break;
                    case MODE_FRACTION:
                        num = mInputFractionNumerator.getText().toString();
                        denom = mInputFractionDenominator.getText().toString();
                        val = num + "/" + denom;
                        break;
                }

                if(num.isEmpty()){
                    Toast.makeText(getContext(), R.string.err_new_segment_empty_num, Toast.LENGTH_LONG).show();
                    return;
                }

                if(denom.isEmpty()){
                    Toast.makeText(getContext(), R.string.err_new_segment_empty_denom, Toast.LENGTH_LONG).show();
                    return;
                }

                if(Float.parseFloat(denom) == 0.0) {
                    Toast.makeText(getContext(), R.string.err_new_segment_zero_denom, Toast.LENGTH_LONG).show();
                    return;
                }

              //  ViewGroup checkboxLayout = //(ViewGroup) mView.findViewById(R.id.checkbox_layout);
                int len = mCheckBoxLayout.getChildCount();

                if(mState != STATE_COMMON){
                    DbManager dbManager  = new DbManager(getContext());
                    SQLiteDatabase db = dbManager.getWritableDatabase();

                    db.beginTransaction();
                    try {

                        ContentValues cw = new ContentValues();
                        cw.put(SegmentTable.COL_BILL_ID, 0 );
                        cw.put(SegmentTable.COL_TYPE, SegmentTable.TYPE_GLOBAL);
                        cw.put(SegmentTable.COL_NAME,segmentName);
                        cw.put(SegmentTable.COL_UNIT, mCurrentMode);
                        cw.put(SegmentTable.COL_VALUE, val);

                        long segmentTableId;
                        if(mState == STATE_UPDATE) {

                            //remove segment bill list
                            db.delete(SegmentBillTypeTable.TABLE_NAME,SegmentBillTypeTable.COL_SEGMENT_ID + "= ?", new String[]{mSegmentId});
                            //update segment
                            segmentTableId = Long.parseLong(mSegmentId);
                            db.update(SegmentTable.TABLE_NAME, cw, SegmentTable.COL_ID + "= ?", new String[]{mSegmentId});

                        } else {
                            segmentTableId = db.insertOrThrow(SegmentTable.TABLE_NAME, null, cw);
                        }
                        cw.clear();

                        int billType = 0;
                        for(int i=0;i<len;++i) {
                            View child = mCheckBoxLayout.getChildAt(i);
                            if(child instanceof CheckBox) {
                                CheckBox cb = (CheckBox) child;
                                if(cb.isChecked()){
                                    cw.put(SegmentBillTypeTable.COL_SEGMENT_ID, segmentTableId);
                                    cw.put(SegmentBillTypeTable.COL_TYPE, billType );
                                    db.insertOrThrow(SegmentBillTypeTable.TABLE_NAME, null, cw);
                                }
                                ++billType;
                            }
                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        Log.e("SQL_ERR", e.toString());
                        //Error in between database transaction
                    } finally {
                        db.endTransaction();
                        db.close();
                    }




                } else {

                }

                int strRes = mState == STATE_UPDATE ? R.string.segment_update_success : R.string.segment_create_success;
                Toast.makeText(getContext(), strRes, Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();

            }
        });
        return mView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("state", mState);
        outState.putInt("mode", mCurrentMode);
        outState.putInt("segmentId", Integer.valueOf(mSegmentId));
    }

    void setUnderlineVisibilities(int decimalVisibility, int percentVisibility, int fractionVisibility)
    {
        mUnderlineDecimal.setVisibility(decimalVisibility);
        mUnderlinePercent.setVisibility(percentVisibility);
        mUnderlineFraction.setVisibility(fractionVisibility);
    }

    void setInputVisibilities(int decimalVisibility, int percentVisibility, int fractionVisibility){
        mInputDecimal.setVisibility(decimalVisibility);
        mInputPercent.setVisibility(percentVisibility);
        mInputFractionNumerator.setVisibility(fractionVisibility);
        mInputFractionDenominator.setVisibility(fractionVisibility);
        mFractionDivide.setVisibility(fractionVisibility);
    }



    public void setState(int state){
        mState = state;
    }

    public void setSegmentId(String segmentId){
        mSegmentId = segmentId;
    }

    public void setCommon(DataHolder dh) {
        //TODO: save dataholder on recreation of window
        mDataHolder = dh;

        mState = STATE_COMMON;
    }


}
