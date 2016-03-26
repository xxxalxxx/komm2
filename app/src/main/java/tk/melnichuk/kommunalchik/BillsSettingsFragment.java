package tk.melnichuk.kommunalchik;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import tk.melnichuk.kommunalchik.DataManagers.OptionsManager;

/**
 * Created by al on 21.03.16.
 */
public class BillsSettingsFragment extends Fragment {

    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frag_bills_settings, container, false);

        final OptionsManager optionsManager = new OptionsManager(getActivity());


        optionsManager.start();

        setEditTextFromFloat(R.id.gas_rate_input, optionsManager.getGasRate());
        setSpinnerFromInt(R.id.gas_calc_by_input, optionsManager.getGasRateType());

        setEditTextFromFloat(R.id.cold_water_rate_input, optionsManager.getColdWaterRate());
        setSpinnerFromInt(R.id.cold_water_calc_by_input, optionsManager.getColdWaterCounter());

        setEditTextFromFloat(R.id.waste_water_rate_input, optionsManager.getWasteWaterRate());
        setSpinnerFromInt(R.id.waste_water_calc_by_input, optionsManager.getWasteWaterCounter());

        setEditTextFromFloat(R.id.hot_water_rate_input, optionsManager.getHotWaterRate());
        setSpinnerFromInt(R.id.hot_water_calc_by_input, optionsManager.getHotWaterCounter());

        setEditTextFromInt(R.id.electricity_step_sub_input, optionsManager.getElectricityStepSubsidy());
        setEditTextFromInt(R.id.electricity_step_step1_input, optionsManager.getElectricityStep1());
        setEditTextFromInt(R.id.electricity_step_step2_input, optionsManager.getElectricityStep2());

        setEditTextFromFloat(R.id.electricity_rate_sub_input, optionsManager.getElectricityRateSubsidy());
        setEditTextFromFloat(R.id.electricity_rate_step1_input, optionsManager.getElectricityRate1());
        setEditTextFromFloat(R.id.electricity_rate_step2_input, optionsManager.getElectricityRate2());
        setEditTextFromFloat(R.id.electricity_rate_step3_input, optionsManager.getElectricityRate3());

        setEditTextFromFloat(R.id.phone_tax_input, optionsManager.getTaxRate());
        setEditTextFromFloat(R.id.phone_rate_input, optionsManager.getPhoneRate());
        setEditTextFromFloat(R.id.phone_radio_input, optionsManager.getRadioRate());


        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        optionsManager.start();

                        optionsManager.setGasRate(getFloatFromEditText(R.id.gas_rate_input));
                        optionsManager.setGasRateType(getIntFromSpinner(R.id.gas_calc_by_input));

                        optionsManager.setColdWaterRate(getFloatFromEditText(R.id.cold_water_rate_input));
                        optionsManager.setColdWaterCounter(getIntFromSpinner(R.id.cold_water_calc_by_input));

                        optionsManager.setWasteWaterRate(getFloatFromEditText(R.id.waste_water_rate_input));
                        optionsManager.setWasteWaterCounter(getIntFromSpinner(R.id.waste_water_calc_by_input));

                        optionsManager.setHotWaterRate(getFloatFromEditText(R.id.hot_water_rate_input));
                        optionsManager.setHotWaterCounter(getIntFromSpinner(R.id.hot_water_calc_by_input));

                        optionsManager.setElectricityStepSubsidy(getIntFromEditText(R.id.electricity_step_sub_input));
                        optionsManager.setElectricityStep1(getIntFromEditText(R.id.electricity_step_step1_input));
                        optionsManager.setElectricityStep2(getIntFromEditText(R.id.electricity_step_step2_input));

                        optionsManager.setElectricityRateSubsidy(getFloatFromEditText(R.id.electricity_rate_sub_input));
                        optionsManager.setElectricityRate1(getFloatFromEditText(R.id.electricity_rate_step1_input));
                        optionsManager.setElectricityRate2(getFloatFromEditText(R.id.electricity_rate_step1_input));
                        optionsManager.setElectricityRate3(getFloatFromEditText(R.id.electricity_rate_step1_input));

                        optionsManager.setTaxRate(getFloatFromEditText(R.id.phone_tax_input));
                        optionsManager.setRadioRate(getFloatFromEditText(R.id.phone_radio_input));
                        optionsManager.setPhoneRate(getFloatFromEditText(R.id.phone_rate_input));

                        optionsManager.save();


                        Toast.makeText(getContext(), R.string.settings_bills_success, Toast.LENGTH_LONG).show();

                     //   MenuFragment menuFragment = new MenuFragment();
                       // final FragmentTransaction ft = getFragmentManager().beginTransaction();
                       // ft.replace(R.id.fragment_container, menuFragment, "SettingsFrag").commit();


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        FloatingActionButton fabSave = (FloatingActionButton) mView.findViewById(R.id.fab_save);

        fabSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(b.getContext());
                builder.setMessage(R.string.alert_dialog_bills_settings_save).setPositiveButton(R.string.alert_dialog_yes, dialogClickListener)
                        .setNegativeButton(R.string.alert_dialog_no, dialogClickListener).show();
            }

        });
        return mView;
    }


    String getStringFromEditText(int editTextId){
        String ret = ((EditText) mView.findViewById(editTextId)).getText().toString();
        return ret.isEmpty() ? "0" : ret;
    }

    float getFloatFromEditText(int editTextId){
        return Float.parseFloat(getStringFromEditText(editTextId));
    }


    int getIntFromEditText(int editTextId){
        return Integer.parseInt(getStringFromEditText(editTextId));
    }

    int getIntFromSpinner(int spinnerId){
        return ((Spinner) mView.findViewById(spinnerId)).getSelectedItemPosition();
    }

    void setEditTextFromFloat(int editTextId, float value){
        setEditTextFromString(editTextId, String.valueOf(value));
    }

    void setEditTextFromInt(int editTextId, int value){
        setEditTextFromString(editTextId, String.valueOf(value));
    }

    void setSpinnerFromInt(int spinnerId, int pos ) {
        ((Spinner)mView.findViewById(spinnerId)).setSelection( pos ,true);
    }

    void setEditTextFromString(int editTextId, String value){
        ((EditText) mView.findViewById(editTextId)).setText(value);
    }

}
