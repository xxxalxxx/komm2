package tk.melnichuk.kommunalchik;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.frag_menu, container, false);

        Button btnNewBills = (Button) v.findViewById(R.id.btn_new_bills);
        btnNewBills.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.alert_dialog_bills_new_bill_warning)
                        .setPositiveButton(R.string.alert_dialog_yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BillsFragment billsFragment = new BillsFragment();

                                        billsFragment.setState(BillsFragment.STATE_NEW,BillsFragment.BILL_ID_NEW);
                                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.replace(R.id.fragment_container, billsFragment, "NewBillsFrag");
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                })
                        .setNegativeButton(R.string.alert_dialog_no, null).show();

            }

        });

        Button btnContinueBills = (Button) v.findViewById(R.id.btn_continue_bills);
        btnContinueBills.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                BillsFragment billsFragment = new BillsFragment();
              //  Bundle args = new Bundle();
                //args.putInt("state", BillsFragment.STATE_CONTINUED);
                //args.putInt("billId", BillsFragment.BILL_ID_CONTINUED);
               // billsFragment.setArguments(args);
                billsFragment.setState(BillsFragment.STATE_CONTINUED, BillsFragment.BILL_ID_CONTINUED);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, billsFragment, "NewBillsFrag");
                ft.addToBackStack(null);
                ft.commit();
            }

        });

        Button btnSettings = (Button) v.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                SettingsFragment settingsFrament = new SettingsFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, settingsFrament, "SettingsFrag");
                ft.addToBackStack(null);
                ft.commit();
            }

        });

        Button btnSavedBills = (Button) v.findViewById(R.id.btn_saved_bills);
        btnSavedBills.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {

                BillListFragment billListFragment = new BillListFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, billListFragment, "BillListFrag");
                ft.addToBackStack(null);
                ft.commit();
            }

        });


        Button btnExit = (Button) v.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                getActivity().onBackPressed();
            }

        });

        return v;

    }
}
