package tk.melnichuk.kommunalchik;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class SegmentListAdapter extends BaseAdapter {

    Context mContext;
    String[] data;
    ArrayList<SegmentListFragment.SegmentListItem> mData;
    SegmentListFragment mSegmentListFragment;
    private static LayoutInflater mInflater = null;

    public SegmentListAdapter(Context context, ArrayList<SegmentListFragment.SegmentListItem> data, SegmentListFragment segmentListFragment) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mData = data;
        mSegmentListFragment = segmentListFragment;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View vi = convertView == null ? mInflater.inflate(R.layout.segment_list_item, null) : convertView;

        TextView name = (TextView) vi.findViewById(R.id.name_value);
        TextView unit = (TextView) vi.findViewById(R.id.unit_value);
        TextView value = (TextView) vi.findViewById(R.id.value_value);

        name.setText(mData.get(position).mName);
        unit.setText(mData.get(position).mUnit);
        value.setText(mData.get(position).mValue);

        vi.findViewById(R.id.delete_segment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.alert_dialog_segments_delete).setPositiveButton(R.string.alert_dialog_yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String id = mData.get(position).mId;
                            mSegmentListFragment.deleteSegment(id);
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_no, null).show();
            }
        });

        vi.findViewById(R.id.update_segment).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                String id = mData.get(position).mId;
                mSegmentListFragment.startFragmentUpdateTransaction(id);
            }
        });

        return vi;
    }
}