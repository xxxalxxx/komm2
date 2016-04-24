package tk.melnichuk.kommunalchik.BillTypeFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import tk.melnichuk.kommunalchik.CustomViews.UnitTypesEditText;
import tk.melnichuk.kommunalchik.CustomViews.UnitTypesKeyboard;
import tk.melnichuk.kommunalchik.DataManagers.BillManager;
import tk.melnichuk.kommunalchik.MainActivity;
import tk.melnichuk.kommunalchik.Models.BaseModel;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 26.03.16.
 */
public class BaseBillFragment extends Fragment {

    static final int INDEX_SEGMENT_INPUT_START = 4, INDEX_SEGMENT_UNIT_TYPES_INPUT = 3, INDEX_SEGMENT_INPUT_FROM_DB_START = 3;
    ArrayList<String> mMainTableData = null;
    ArrayList<ArrayList<String>> mSegmentsData = null;
    View mView;
    int mSegmentLayoutResourceId = 0, mMainLayoutResourceId = 0;
    int mNumSegmentItems = 0;
    int mDummySegmentNameCounter = 1;
    public int mModelId = 0;
    String mDummySegmentNameValue;
    public LinearLayout mSegmentContainer;
    public RelativeLayout mBillContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mMainLayoutResourceId = args.getInt("mainLayout");
        mSegmentLayoutResourceId = args.getInt("segLayout");
        mModelId = args.getInt("modelId");

        if(savedInstanceState != null){
            mDummySegmentNameCounter = savedInstanceState.getInt("dummySegmentNameCounter",0);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        mView =  inflater.inflate(mMainLayoutResourceId, container, false);
        mBillContainer = (RelativeLayout) mView.findViewById(R.id.table_container);
        mSegmentContainer = (LinearLayout) mView.findViewById(R.id.segments_container);


        if(savedInstanceState != null) {
            mNumSegmentItems = savedInstanceState.getInt("numSegmentItems",0);
        } else {
            calcNumSegmentViews();
        }

        mDummySegmentNameValue = getResources().getString(R.string.dummy_segment_name_value);

        ImageButton addSegmentButton = (ImageButton) mView.findViewById(R.id.button);
        addSegmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "calc_btn", Toast.LENGTH_LONG).show();
                addSegment();
            }
        });

        Log.d("_VINF","on create view");

        return mView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mMainTableData != null){
            fillMainTableData(mMainTableData);
            mMainTableData = null;
        }

        if(mSegmentsData != null){
            fillSegmentsDataFromDb(mSegmentsData);
            mSegmentsData = null;
        }
    }

    public ViewGroup addSegment(){
        Log.d("_SSS", "add seg simple");
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(getContext()).inflate(
                mSegmentLayoutResourceId, mSegmentContainer, false);
        mSegmentContainer.addView(newView);

        ((TextView) newView.findViewById(android.R.id.text1)).setText(mDummySegmentNameValue + " " + mDummySegmentNameCounter);
        ++mDummySegmentNameCounter;

        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity != null && mainActivity.mKeyboard != null) {
            UnitTypesEditText et = (UnitTypesEditText) newView.findViewById(R.id.text2);
            mainActivity.mKeyboard.registerEditText(et);
        }

        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSegmentContainer.removeView(newView);
            }
        });
        return newView;
    }


    public ViewGroup addSegment(ArrayList<String> segmentItem){

        final ViewGroup newView = (ViewGroup) LayoutInflater.from(getContext()).inflate(
                mSegmentLayoutResourceId, mSegmentContainer, false);
        mSegmentContainer.addView(newView);

        ((TextView) newView.getChildAt(0)).setText(segmentItem.get(0));

        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity != null && mainActivity.mKeyboard != null) {

            UnitTypesEditText et = (UnitTypesEditText) newView.getChildAt(3);
            et.setUnitTypeFromIdStr(segmentItem.get(1));
            et.setText(segmentItem.get(2));
            ((TextView)newView.getChildAt(2)).setText(et.getLabel());
            Log.d("_DBD", "_label " +  et.getLabel() + " " + segmentItem.toString() );
            mainActivity.mKeyboard.registerEditText(et);

        }
        int numViewChildren = newView.getChildCount();
        int interfaceSegmentsLeft = numViewChildren - INDEX_SEGMENT_INPUT_START;
        int arrayListSegmentsLeft = segmentItem.size() - INDEX_SEGMENT_INPUT_FROM_DB_START;
        if(interfaceSegmentsLeft > 0 && arrayListSegmentsLeft > 0){
            int len =  Math.min(interfaceSegmentsLeft,arrayListSegmentsLeft);
            int  j = INDEX_SEGMENT_INPUT_START, k = INDEX_SEGMENT_INPUT_FROM_DB_START;
            for(int i=0;
                i<len;
                ++i, ++j, ++k){

                Log.d("_DBD", "j:" + j + " segInterfacelen:" + newView.getChildCount() + " k:" + k + " segitemlen:" + segmentItem.size());

                ((TextView)newView.getChildAt(j)).setText(segmentItem.get(k));
            }

            if(j < numViewChildren)
                for(int i=j;i< numViewChildren;++i)
                    ((TextView)newView.getChildAt(i)).setText("0");


        }

        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSegmentContainer.removeView(newView);
            }
        });
        return newView;
    }

    public void fillMainTableData(ArrayList<String> mainTableData){
        if(mBillContainer == null || mSegmentContainer == null) {
            mMainTableData = mainTableData;
            return;
        }
        int it = 0;
        int len = mBillContainer.getChildCount();
        for(int i=0;i<len;++i){
            View child = mBillContainer.getChildAt(i);
            if(child instanceof EditText) {
                ((EditText) child).setText( mainTableData.get(it++) );
            } else if(child instanceof Spinner){
                ((Spinner)child).setSelection(Integer.valueOf(mainTableData.get(it)));
                ++it;
            }
        }
    }

    public void fillSegmentsData(ArrayList<ArrayList<String>> segmentsData){
        if(mBillContainer == null || mSegmentContainer == null){
            return;
        }

        int len = mSegmentContainer.getChildCount();

        for(int i=0;i<len;++i){
            ViewGroup segment = (ViewGroup) mSegmentContainer.getChildAt(i);
            ArrayList<String> dataSegment = segmentsData.get(i);
            int k = 1;
            for(int j=INDEX_SEGMENT_INPUT_START;j<segment.getChildCount();++j){
                ((TextView)segment.getChildAt(j)).setText(dataSegment.get(k));
                ++k;
            }
        }
    }


    public void fillSegmentsDataFromDb(final ArrayList<ArrayList<String>> segmentsData){
        if(mBillContainer == null || mSegmentContainer == null) {
            mSegmentsData = segmentsData;
            return;
        }
        Log.d("_SEGDAT", "" + segmentsData.toString());
           // addSegment(segmentsData.get(i));
        mSegmentContainer.removeAllViews();
        for(int i=0;i<segmentsData.size();++i){
            final int pos = i;
            mSegmentContainer.post(new Runnable() {
                @Override
                public void run() {
                    addSegment(segmentsData.get(pos));
                }
            });
        }
    }

    public ArrayList<String> getMainTableData(){

        if(mBillContainer == null) return null;

        ArrayList<String> mainInputs = new ArrayList<>();
        int len = mBillContainer.getChildCount();
        for(int i=0;i<len;++i){
            View child = mBillContainer.getChildAt(i);
            if(child instanceof EditText){
                String text =  ((EditText) child).getText().toString();
                mainInputs.add(text.isEmpty() ? "0" : text);
            } else if(child instanceof Spinner){
                int pos = ((Spinner) child).getSelectedItemPosition();
                mainInputs.add( String.valueOf(pos) );
            }
        }

        String result = "[";
        for(int i =0;i<mainInputs.size();++i){
            result+= ("i:" + i + " v:" + mainInputs.get(i));
            if(i != mainInputs.size()-1)
            result+=", ";
        }

        result += "]";
        Log.d("_CALC", "1 " + result + "len " + len);

        return mainInputs;
    }

    public ArrayList<ArrayList<String>> getFullSegmentsData(){
        if(mBillContainer == null || mSegmentContainer == null) return null;

        int len = mSegmentContainer.getChildCount();

        ArrayList<ArrayList<String>> segments = new ArrayList<>();
        for(int i=0;i<len;++i) {
            ViewGroup segment = (ViewGroup) mSegmentContainer.getChildAt(i);
            if(segment != null) {
                int numSegmentItems = segment.getChildCount();
                ArrayList<String> segmentItems = new ArrayList<>();

                UnitTypesEditText unitTypesEditText = (UnitTypesEditText) segment.getChildAt(INDEX_SEGMENT_UNIT_TYPES_INPUT);
                segmentItems.add( ((TextView)segment.getChildAt(0)).getText().toString() );
                segmentItems.add(String.valueOf(unitTypesEditText.getUnitTypeId()));
                segmentItems.add(unitTypesEditText.getText().toString());

                for(int j=INDEX_SEGMENT_INPUT_START;j<numSegmentItems;++j){

                    View segmentItem = segment.getChildAt(j);

                    String text = ((TextView) segmentItem).getText().toString();
                    if(text.isEmpty()) text = "0";
                    segmentItems.add(text);
                }

                segments.add(segmentItems);
            }
        }
        return segments;
    }



    public ArrayList<ArrayList<String>> getSegmentsData(){
        if(mBillContainer == null || mSegmentContainer == null) return null;

        int len = mSegmentContainer.getChildCount();

        ArrayList<ArrayList<String>> segments = new ArrayList<>();
        for(int i=0;i<len;++i){
            ViewGroup segment = (ViewGroup) mSegmentContainer.getChildAt(i);

            if(segment != null) {
                int numSegmentItems = segment.getChildCount();
                ArrayList<String> segmentItems = new ArrayList<>();
                String unitTypesInputVal = ((UnitTypesEditText)segment.getChildAt(INDEX_SEGMENT_UNIT_TYPES_INPUT)).getDecimalVal();
                segmentItems.add(unitTypesInputVal);
                for(int j=INDEX_SEGMENT_INPUT_START;j<numSegmentItems;++j){
                    View segmentItem = segment.getChildAt(j);
                    String text = ((TextView) segmentItem).getText().toString();
                    if(text.isEmpty()) text = "0";
                    segmentItems.add(text);
                }
                segments.add(segmentItems);
            }
        }
        Log.d("_CALC","2 " + segments.toString() + " len" + len);
        return segments;
    }

    void calcNumSegmentViews(){

        RelativeLayout rll = (RelativeLayout) getLayoutInflater(new Bundle()).inflate(mSegmentLayoutResourceId, null, false);
        if(rll != null) {
            mNumSegmentItems = rll.getChildCount();
            Log.d("_CHC",rll.getChildCount() +"");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("numSegmentItems", mNumSegmentItems);
        outState.putInt("dummySegmentNameCounter", mDummySegmentNameCounter);
        Log.d("_SV", "saving state");
    }

    public void calc(){
        BaseModel model = BillManager.getModel(mModelId);
        ArrayList<String> mainData = model.getCalcedMainTableData(getMainTableData());
        ArrayList<ArrayList<String>> segmentsData = model.getCalcedSegmentsData(getSegmentsData());
        Log.d("_CALC","3 " + segmentsData.toString() );
        fillMainTableData(mainData);
        fillSegmentsData(segmentsData);
    }

}
