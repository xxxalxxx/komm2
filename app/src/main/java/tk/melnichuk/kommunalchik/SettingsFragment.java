package tk.melnichuk.kommunalchik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by al on 22.03.16.
 */
public class SettingsFragment extends Fragment {

    SegmentListFragment mSegmentListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_settings, container, false);
    /*    if(savedInstanceState != null){
            mSegmentListFragment = (SegmentListFragment)getFragmentManager().getFragment(savedInstanceState, "segmentListFragment");
        } else {
            mSegmentListFragment = new SegmentListFragment();
        }
*/

        LinearLayout billsLayout = (LinearLayout) v.findViewById(R.id.bills_layout);
        billsLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                BillsSettingsFragment billsSettingsFragment = new BillsSettingsFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, billsSettingsFragment, "NewBillsFrag");
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        LinearLayout segmentsLayout = (LinearLayout) v.findViewById(R.id.segments_layout);
        segmentsLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                SegmentListFragment segmentListFragment = new SegmentListFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, segmentListFragment, "NewBillsFrag");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return v;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //if (mSegmentListFragment.isAdded()) {
        //    getFragmentManager().putFragment(outState, "segmentListFragment", mSegmentListFragment);
       // }
    }
}
