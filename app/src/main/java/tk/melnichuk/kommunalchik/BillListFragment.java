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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tk.melnichuk.kommunalchik.DataManagers.BillManager;
import tk.melnichuk.kommunalchik.DataManagers.DbManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.BillTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentBillTypeTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentTable;
import tk.melnichuk.kommunalchik.Helpers.Utils;

/**
 * Created by al on 22.03.16.
 */
public class BillListFragment extends Fragment {
    View mView;
    ListView mListView;
    ImageButton mNavNext, mNavPrev;
    TextView mNavPage, mNoSegmentsText;
    BillListAdapter mBillListAdapter;
    private final int ITEMS_PER_PAGE = 6;
    private long mNumItems, mOffset = 0;
    ArrayList<BillListItem> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mOffset = savedInstanceState.getLong("offset");
            mNumItems = savedInstanceState.getLong("numItems");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        mData = new ArrayList<>();

        if(mView == null){
            mView = inflater.inflate(R.layout.frag_bill_list, container, false);

        }
        updateNumItems();

        updateBillsByOffsetAndLimit(ITEMS_PER_PAGE, mOffset);

        mBillListAdapter = new BillListAdapter(getContext(), mData, this);
        mListView = (ListView) mView.findViewById(R.id.bill_list);
        mListView.setAdapter(mBillListAdapter);

        mNavPage = (TextView) mView.findViewById(R.id.nav_page);
        mNoSegmentsText = (TextView) mView.findViewById(R.id.no_segments_text);
        mNavPrev = (ImageButton) mView.findViewById(R.id.nav_prev);
        mNavNext = (ImageButton) mView.findViewById(R.id.nav_next);

        updateNavPageText();

        mNavPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                long prevOffset = mOffset - ITEMS_PER_PAGE;
                if(prevOffset < 0) {
                    return;
                }
                mOffset = prevOffset;

                updateBillsByOffsetAndLimit(ITEMS_PER_PAGE, mOffset);
                updateNumItems();
                mBillListAdapter.notifyDataSetChanged();
                updateNavPageText();

            }
        });

        mNavNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                long nextOffset = mOffset + ITEMS_PER_PAGE;
                if(nextOffset >= mNumItems) {
                    return;
                }
                mOffset = nextOffset;
                updateBillsByOffsetAndLimit(ITEMS_PER_PAGE, mOffset);
                updateNumItems();
                mBillListAdapter.notifyDataSetChanged();
                updateNavPageText();


            }
        });


        return mView;
    }

    public class BillListItem {
        long mId;
        String mName, mDate;

        public BillListItem(long id, String name, String date){
            mId = id;
            mName = name;
            mDate = date;

        }
    };



    public void updateBillsByOffsetAndLimit(int limit, long offset){
        mData.clear();
        DbManager dbManager  = new DbManager(getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor c = db.query(
                BillTable.TABLE_NAME,  // The table to query
                new String[]{BillTable.COL_ID,BillTable.COL_NAME, BillTable.COL_DATE},                               // The columns to return
                BillTable.COL_STATUS + " =?",                                // The columns for the WHERE clause
                new String[] {String.valueOf(BillTable.STATUS_SAVED)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
            BillTable.COL_ID + " ASC",                    // The sort order
                offset + "," + limit
        );

        while(c.moveToNext()){
            long id = c.getLong(c.getColumnIndexOrThrow(BillTable.COL_ID));
            String name = c.getString(c.getColumnIndexOrThrow(BillTable.COL_NAME));
            String date = c.getString(c.getColumnIndexOrThrow(BillTable.COL_DATE));

            mData.add(new BillListItem(id,name,date));
        }

        db.close();
    }

    void updateNavPageText(){
        int numPages = (int) Math.ceil( (double) mNumItems/ITEMS_PER_PAGE );
        if(numPages == 0) {
            mNoSegmentsText.setVisibility(View.VISIBLE);
            mNavPage.setText(R.string.segment_list_no_pages);
            return;
        }
        mNoSegmentsText.setVisibility(View.GONE);

        long currPage =  (int) Math.ceil( (double) mOffset/ITEMS_PER_PAGE ) + 1;//mOffset/ITEMS_PER_PAGE + 1;

        String navText = currPage + "/" + numPages;
        mNavPage.setText(navText);
    }

    void updateNumItems(){
        DbManager dbManager = new DbManager(getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();
        mNumItems =  DatabaseUtils.queryNumEntries(db, BillTable.TABLE_NAME,
                BillTable.COL_STATUS + "=?",
                new String[]{String.valueOf(BillTable.STATUS_SAVED)});
    }

    void startFragmentUpdateTransaction(long id){
        BillsFragment billsFragment = new BillsFragment();
        billsFragment.setState(BillsFragment.STATE_SAVED, id);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, billsFragment, "NewBillsFrag");
        ft.addToBackStack(null);
        ft.commit();
    }

    void deleteBill(long billId){
        DbManager dbManager  = new DbManager(getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();
        db.beginTransaction();
        try{
            BillManager bm = new BillManager(this);
            bm.deleteBillsFromDb(db,billId);
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

        updateBillsByOffsetAndLimit(ITEMS_PER_PAGE, mOffset);
        mBillListAdapter.notifyDataSetChanged();
        updateNavPageText();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("offset", mOffset);
        outState.putLong("numItems", mNumItems);
    }

}
