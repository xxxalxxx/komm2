package tk.melnichuk.kommunalchik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by al on 22.03.16.
 */
public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_settings, container, false);


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

                SegmentNewFragment segmentNewFragment = new SegmentNewFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, segmentNewFragment, "NewBillsFrag");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return v;
    }
}
