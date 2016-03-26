package tk.melnichuk.kommunalchik;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tk.melnichuk.kommunalchik.DataManagers.DbManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.BaseTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentBillTypeTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentTable;

/**
 * Created by al on 22.03.16.
 */
public class SegmentListFragment extends Fragment {
    View mView;
    ListView mListView;
    Button mNavNext, mNavPrev;
    TextView mNavPage;
    SegmentListAdapter mSegmentListAdapter;
    private final int ITEMS_PER_PAGE = 3;
    private long mNumItems, mOffset;
    ArrayList<SegmentListItem> mData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);


        mData = new ArrayList<>();
        if(savedInstanceState != null){
            mView = inflater.inflate(R.layout.frag_segment_list, container, false);
            mOffset = savedInstanceState.getLong("offset");
            mNumItems = savedInstanceState.getLong("numItems");
        } else if(mView == null){
            mView = inflater.inflate(R.layout.frag_segment_list, container, false);
            mOffset = 0;
            updateNumItems();
        }


        updateSegmentsByOffsetAndLimit(ITEMS_PER_PAGE, mOffset);


        mSegmentListAdapter = new SegmentListAdapter(getContext(), mData, this);
        mListView = (ListView) mView.findViewById(R.id.segment_list);
        mListView.setAdapter(mSegmentListAdapter);

        mNavPage = (TextView) mView.findViewById(R.id.nav_page);
        mNavPrev = (Button) mView.findViewById(R.id.nav_prev);
        mNavNext = (Button) mView.findViewById(R.id.nav_next);

        updateNavPageText();

        FloatingActionButton fabAdd = (FloatingActionButton) mView.findViewById(R.id.fab_segment_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                SegmentFragment segmentNewFragment = new SegmentFragment();
                segmentNewFragment.setState(SegmentFragment.STATE_CREATE);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, segmentNewFragment, "NewBillsFrag");
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        mNavPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                if(mOffset - ITEMS_PER_PAGE < 0) {
                    return;
                }
                mOffset -= ITEMS_PER_PAGE;

                updateSegmentsByOffsetAndLimit(ITEMS_PER_PAGE,mOffset);
                updateNumItems();
                mSegmentListAdapter.notifyDataSetChanged();
                updateNavPageText();

            }
        });

        mNavNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                if(mOffset + ITEMS_PER_PAGE >= mNumItems) {
                    return;
                }
                mOffset += ITEMS_PER_PAGE;
                updateSegmentsByOffsetAndLimit(ITEMS_PER_PAGE,mOffset);
                updateNumItems();
                mSegmentListAdapter.notifyDataSetChanged();
                updateNavPageText();


            }
        });


        return mView;
    }

    public class SegmentListItem {
        String mId, mName, mUnit,mValue;

        public SegmentListItem(String id, String name, String unit, String value){
            mId = id;
            mName = name;
            mUnit = unit;
            mValue = value;
        }
    };



    public void updateSegmentsByOffsetAndLimit(int limit, long offset){
        mData.clear();
        DbManager dbManager  = new DbManager(getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();


        Cursor c = db.query(
                SegmentTable.TABLE_NAME,  // The table to query
                new String[]{SegmentTable.COL_ID,SegmentTable.COL_NAME, SegmentTable.COL_UNIT, SegmentTable.COL_VALUE},                               // The columns to return
                SegmentTable.COL_TYPE + " = ?",                                // The columns for the WHERE clause
                new String[] {String.valueOf(SegmentTable.TYPE_GLOBAL)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                SegmentTable.COL_ID + SegmentTable.ORDER_ASC,                                // The sort order
                offset + "," + limit
        );

        while(c.moveToNext()){
            String id = c.getString(c.getColumnIndexOrThrow(SegmentTable.COL_ID));
            String name = c.getString(c.getColumnIndexOrThrow(SegmentTable.COL_NAME));
            String value = c.getString(c.getColumnIndexOrThrow(SegmentTable.COL_VALUE));

            int unitInt = c.getInt(c.getColumnIndexOrThrow(SegmentTable.COL_UNIT));
            String unit = Utils.getUnitValue(getContext(), unitInt);

            mData.add(new SegmentListItem(id,name,unit,value));
        }

        db.close();
    }

    void updateNavPageText(){
        long currPage =  (int) Math.ceil( (double) mOffset/ITEMS_PER_PAGE ) + 1;//mOffset/ITEMS_PER_PAGE + 1;

        int numPages = (int) Math.ceil( (double) mNumItems/ITEMS_PER_PAGE );
        String navText = currPage + "/" + numPages;
        mNavPage.setText(navText);
    }

    void updateNumItems(){
        DbManager dbManager = new DbManager(getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();
        mNumItems =  DatabaseUtils.queryNumEntries(db, SegmentTable.TABLE_NAME);
    }

    void startFragmentUpdateTransaction(String id){

        SegmentFragment segmentNewFragment = new SegmentFragment();
        segmentNewFragment.setState(SegmentFragment.STATE_UPDATE);
        segmentNewFragment.setSegmentId(id);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, segmentNewFragment, "NewBillsFrag");
        ft.addToBackStack(null);
        ft.commit();
    }

    void deleteSegment(String segmentId){
        DbManager dbManager  = new DbManager(getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();
        db.beginTransaction();
        try{
            db.delete(SegmentTable.TABLE_NAME, SegmentTable.COL_ID + " =?", new String[]{segmentId});
            db.delete(SegmentBillTypeTable.TABLE_NAME, SegmentBillTypeTable.COL_SEGMENT_ID + " =?", new String[]{segmentId});

            db.setTransactionSuccessful();
        } catch(Exception e){
            Log.e("SQL_ERR", e.toString());
        } finally {
            db.endTransaction();
            db.close();
        }

        updateNumItems();

        if(mOffset >= mNumItems ) {
            mOffset-= ITEMS_PER_PAGE;
            if(mOffset < 0) {
                mOffset = 0;
            }

        }

        updateSegmentsByOffsetAndLimit(ITEMS_PER_PAGE, mOffset);
        mSegmentListAdapter.notifyDataSetChanged();
        updateNavPageText();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("offset", mOffset);
        outState.putLong("numItems",mNumItems);

    }

}
