package tk.melnichuk.kommunalchik.DataManagers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by al on 23.03.16.
 */
public class OptionsManager {

    private static String PREFS_NAME = "kommunalchik_prefs";
    private Context mContext;
    SharedPreferences mSharedPrefs;
    SharedPreferences.Editor mEditor;

    private static float
            DEFAULT_GAS_RATE,
            DEFAULT_COLD_WATER_RATE,
            DEFAULT_WASTE_WATER_RATE,
            DEFAULT_HOT_WATER_RATE,
            DEFAULT_ELECTRICITY_RATE_SUBSIDY,
            DEFAULT_ELECTRICITY_RATE_1,
            DEFAULT_ELECTRICITY_RATE_2,
            DEFAULT_ELECTRICITY_RATE_3,
            DEFAULT_TAX_RATE,
            DEFAULT_RADIO_RATE,
            DEFAULT_PHONE_RATE;

    private static int
            DEFAULT_GAS_RATE_TYPE,
            DEFAULT_COLD_WATER_COUNTER,
            DEFAULT_WASTE_WATER_COUNTER,
            DEFAULT_HOT_WATER_COUNTER,
            DEFAULT_ELECTRICITY_STEP_SUBSIDY,
            DEFAULT_ELECTRICITY_STEP_1,
            DEFAULT_ELECTRICITY_STEP_2,

            DEFAULT_TEMP_BILL_TABLE_ID = -1;

    private static String
            STR_GAS_RATE = "gas_rate",
            STR_COLD_WATER_RATE = "cold_water_rate",
            STR_HOT_WATER_RATE = "hot_water_rate",
            STR_WASTE_WATER_RATE = "waste_water_rate",
            STR_ELECTRICITY_RATE_SUBSIDY = "electricity_rate_subsidy",
            STR_ELECTRICITY_RATE_1 = "electricity_rate_1",
            STR_ELECTRICITY_RATE_2 = "electricity_rate_2",
            STR_ELECTRICITY_RATE_3 = "electricity_rate_3",
            STR_RADIO_RATE = "radio_rate",
            STR_PHONE_RATE = "phone_rate",
            STR_TAX_RATE = "tax_rate",

            STR_GAS_RATE_TYPE = "gas_rate_type",
            STR_COLD_WATER_COUNTER = "cold_water_counter",
            STR_WASTE_WATER_COUNTER = "waste_water_counter",
            STR_HOT_WATER_COUNTER = "hot_water_counter",
            STR_ELECTRICITY_STEP_SUBSIDY = "electricity_step_subsidy",
            STR_ELECTRICITY_STEP_1 =  "electricity_step_1",
            STR_ELECTRICITY_STEP_2 =  "electricity_step_2",


    STR_TEMP_BILL_TABLE_ID = "tempBillTableId",
    STR_SAVED_TEMP_BILL_TABLE_ID = "savedTempBillTableId";


    static {
        DEFAULT_GAS_RATE = DEFAULT_COLD_WATER_RATE = DEFAULT_WASTE_WATER_RATE = DEFAULT_HOT_WATER_RATE =
        DEFAULT_ELECTRICITY_RATE_SUBSIDY = DEFAULT_ELECTRICITY_RATE_1 = DEFAULT_ELECTRICITY_RATE_2 =  DEFAULT_ELECTRICITY_RATE_3 =
        DEFAULT_RADIO_RATE = DEFAULT_PHONE_RATE = 0.0f;


        DEFAULT_GAS_RATE_TYPE = DEFAULT_COLD_WATER_COUNTER = DEFAULT_WASTE_WATER_COUNTER =
        DEFAULT_HOT_WATER_COUNTER = DEFAULT_ELECTRICITY_STEP_SUBSIDY  = 0;

        DEFAULT_ELECTRICITY_STEP_1 = 100;
        DEFAULT_ELECTRICITY_STEP_2 = 600;
        DEFAULT_TAX_RATE = 20.0f;
    }


    public OptionsManager(Context context){
        mContext = context;
    }


    //now that was kind of boring

    public float getGasRate(){
        return mSharedPrefs.getFloat(STR_GAS_RATE, DEFAULT_GAS_RATE);
    }

    public float getColdWaterRate(){
        return mSharedPrefs.getFloat(STR_COLD_WATER_RATE, DEFAULT_COLD_WATER_RATE);
    }

    public float getWasteWaterRate(){
        return mSharedPrefs.getFloat(STR_WASTE_WATER_RATE, DEFAULT_WASTE_WATER_RATE);
    }

    public float getHotWaterRate(){
        return mSharedPrefs.getFloat(STR_HOT_WATER_RATE, DEFAULT_HOT_WATER_RATE);
    }

    public float getElectricityRateSubsidy(){
        return mSharedPrefs.getFloat(STR_ELECTRICITY_RATE_SUBSIDY, DEFAULT_ELECTRICITY_RATE_SUBSIDY);
    }

    public float getElectricityRate1(){
        return mSharedPrefs.getFloat(STR_ELECTRICITY_RATE_1, DEFAULT_ELECTRICITY_RATE_1);
    }

    public float getElectricityRate2(){
        return mSharedPrefs.getFloat(STR_ELECTRICITY_RATE_2, DEFAULT_ELECTRICITY_RATE_2);
    }

    public float getElectricityRate3(){
        return mSharedPrefs.getFloat(STR_ELECTRICITY_RATE_3, DEFAULT_ELECTRICITY_RATE_3);
    }
    public float getTaxRate(){
        return mSharedPrefs.getFloat(STR_TAX_RATE, DEFAULT_TAX_RATE);
    }

    public float getPhoneRate(){
        return mSharedPrefs.getFloat(STR_PHONE_RATE, DEFAULT_PHONE_RATE);
    }

    public float getRadioRate(){
        return mSharedPrefs.getFloat(STR_RADIO_RATE, DEFAULT_RADIO_RATE);
    }

    public int getGasRateType(){
        return mSharedPrefs.getInt(STR_GAS_RATE_TYPE, DEFAULT_GAS_RATE_TYPE);
    }


    public int getColdWaterCounter(){
        return mSharedPrefs.getInt(STR_COLD_WATER_COUNTER, DEFAULT_COLD_WATER_COUNTER);
    }

    public int getHotWaterCounter(){
        return mSharedPrefs.getInt(STR_HOT_WATER_COUNTER, DEFAULT_HOT_WATER_COUNTER);
    }

    public int getWasteWaterCounter(){
        return mSharedPrefs.getInt(STR_WASTE_WATER_COUNTER, DEFAULT_WASTE_WATER_COUNTER);
    }

    public int getElectricityStepSubsidy(){
        return mSharedPrefs.getInt(STR_ELECTRICITY_STEP_SUBSIDY, DEFAULT_ELECTRICITY_STEP_SUBSIDY);
    }

    public int getElectricityStep1(){
        return mSharedPrefs.getInt(STR_ELECTRICITY_STEP_1, DEFAULT_ELECTRICITY_STEP_1);
    }

    public int getElectricityStep2(){
        return mSharedPrefs.getInt(STR_ELECTRICITY_STEP_2, DEFAULT_ELECTRICITY_STEP_2);
    }

    public int getTempBillTableId(int id){
        return mSharedPrefs.getInt(STR_TEMP_BILL_TABLE_ID, DEFAULT_TEMP_BILL_TABLE_ID);
    }





    public void setGasRate(float gr){
        mEditor.putFloat(STR_GAS_RATE, gr);
    }

    public void setColdWaterRate(float cwr){
        mEditor.putFloat(STR_COLD_WATER_RATE, cwr);
    }

    public void setWasteWaterRate(float wwr){
        mEditor.putFloat(STR_WASTE_WATER_RATE, wwr);
    }

    public void setHotWaterRate(float hwr){
        mEditor.putFloat(STR_HOT_WATER_RATE, hwr);
    }


    public void setElectricityRateSubsidy(float ers){
        mEditor.putFloat(STR_ELECTRICITY_RATE_SUBSIDY, ers);
    }

    public void setElectricityRate1(float er1){
        mEditor.putFloat(STR_ELECTRICITY_RATE_1, er1);
    }

    public void setElectricityRate2(float er2){
        mEditor.putFloat(STR_ELECTRICITY_RATE_2, er2);
    }

    public void setElectricityRate3(float er3){
        mEditor.putFloat(STR_ELECTRICITY_RATE_3, er3);
    }

    public void setTaxRate(float tr){
        mEditor.putFloat(STR_TAX_RATE, tr);
    }

    public void setPhoneRate(float pr){
        mEditor.putFloat(STR_PHONE_RATE, pr);
    }

    public void setRadioRate(float rr){
        mEditor.putFloat(STR_RADIO_RATE, rr);
    }


    public void setGasRateType(int grt){
        mEditor.putInt(STR_GAS_RATE_TYPE, grt);
    }

    public void setColdWaterCounter(int cwc){
        mEditor.putInt(STR_COLD_WATER_COUNTER, cwc);
    }

    public void setWasteWaterCounter(int wwc){
        mEditor.putInt(STR_WASTE_WATER_COUNTER, wwc);
    }

    public void setHotWaterCounter(int hwc){
        mEditor.putInt(STR_HOT_WATER_COUNTER, hwc);
    }

    public void setElectricityStepSubsidy(int ess){
        mEditor.putInt(STR_ELECTRICITY_STEP_SUBSIDY, ess);
    }

    public void setElectricityStep1(int es1){
        mEditor.putInt(STR_ELECTRICITY_STEP_1, es1);
    }

    public void setElectricityStep2(int es2){
        mEditor.putInt(STR_ELECTRICITY_STEP_2, es2);
    }

    public void setTempBillTableId(int id){
        mEditor.putInt(STR_TEMP_BILL_TABLE_ID, id);
    }

    public void start() {
        mSharedPrefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPrefs.edit();
    }

    public void save(){
        mEditor.commit();
    }
}
