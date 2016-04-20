package tk.melnichuk.kommunalchik;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class BillListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<BillListFragment.BillListItem> mData;
    BillListFragment mBillListFragment;
    private static LayoutInflater mInflater = null;

    public BillListAdapter(Context context, ArrayList<BillListFragment.BillListItem> data, BillListFragment segmentListFragment) {
        mContext = context;
        mData = data;
        mBillListFragment = segmentListFragment;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View vi = convertView == null ? mInflater.inflate(R.layout.bill_list_item, null) : convertView;

        TextView name = (TextView) vi.findViewById(R.id.name_value);
        TextView date = (TextView) vi.findViewById(R.id.date_value);

        name.setText(mData.get(position).mName);
        date.setText(mData.get(position).mDate);

        vi.findViewById(R.id.delete_bill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.alert_dialog_bills_delete).setPositiveButton(R.string.alert_dialog_yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long id = mData.get(position).mId;
                            mBillListFragment.deleteBill(id);
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_no, null).show();
            }
        });

        vi.findViewById(R.id.update_bill).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                long id = mData.get(position).mId;
                mBillListFragment.startFragmentUpdateTransaction(id);
            }
        });

        return vi;
    }
}