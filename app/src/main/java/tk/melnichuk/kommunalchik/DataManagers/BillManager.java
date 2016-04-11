package tk.melnichuk.kommunalchik.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import tk.melnichuk.kommunalchik.BillsFragment;
import tk.melnichuk.kommunalchik.DataManagers.Tables.BillTable;
import tk.melnichuk.kommunalchik.Models.BaseModel;
import tk.melnichuk.kommunalchik.Models.CommunalModel;
import tk.melnichuk.kommunalchik.Models.ElectricityModel;
import tk.melnichuk.kommunalchik.Models.GasModel;
import tk.melnichuk.kommunalchik.Models.HeatingModel;
import tk.melnichuk.kommunalchik.Models.PhoneModel;
import tk.melnichuk.kommunalchik.Models.WaterModel;

/**
 * Created by al on 23.03.16.
 */
public class BillManager {

    BillsFragment mFragment;

    public BillManager(BillsFragment fragment){
        mFragment =fragment;

        initModels();
    }

    public BaseModel mModels[];

    public static final int
            NUM_BILL_TABLES = 8,

            INDEX_COMMUNAL = 0,
            INDEX_GAS = 1 ,
            INDEX_COLD_WATER = 2,
            INDEX_WASTE_WATER = 3,
            INDEX_HOT_WATER = 4,
            INDEX_HEATING = 5,
            INDEX_ELECTRICITY = 6,
            INDEX_PHONE = 7;

    private void initModels(){
        mModels = new BaseModel[NUM_BILL_TABLES];
        mModels[INDEX_COMMUNAL] = new CommunalModel();
        mModels[INDEX_GAS] = new GasModel();
        mModels[INDEX_COLD_WATER] = new WaterModel(WaterModel.TYPE_COLD);
        mModels[INDEX_WASTE_WATER] = new WaterModel(WaterModel.TYPE_WASTE);
        mModels[INDEX_HOT_WATER] = new WaterModel(WaterModel.TYPE_HOT);
        mModels[INDEX_HEATING] = new HeatingModel();
        mModels[INDEX_ELECTRICITY] = new ElectricityModel();
        mModels[INDEX_PHONE] = new PhoneModel();
    }

    private BillManager(){

    }

    public static BaseModel getModel(int modelIndex){
        if(modelIndex < 0 || modelIndex >= NUM_BILL_TABLES) return null;
       // return mModels[modelIndex];
        switch (modelIndex){
            case INDEX_COMMUNAL:
                return new CommunalModel();
            case INDEX_GAS:
                return new GasModel();
            case INDEX_COLD_WATER:
                return new WaterModel(WaterModel.TYPE_COLD);
            case INDEX_WASTE_WATER:
                return new WaterModel(WaterModel.TYPE_WASTE);
            case INDEX_HOT_WATER:
                return new WaterModel(WaterModel.TYPE_HOT);
            case INDEX_HEATING:
                return new HeatingModel();
            case INDEX_ELECTRICITY:
                return new ElectricityModel();
            case INDEX_PHONE:
                return new PhoneModel();
            default:
                return new BaseModel();
        }
    }

    public void getExcelArray(){
        //return row-cell representation of bills in dataholder for excel table
    }

    public void writeToSql(){

    }

    public void getFromSql(){

    }

    public void initDb(){

    }

    public long getTempTableIdByRelId(SQLiteDatabase db, long relId){

        Cursor c = db.query(
                BillTable.TABLE_NAME,
                new String[]{BillTable.COL_ID},
                BillTable.COL_RELATION + "=? AND " + BillTable.COL_STATUS + "=?",
                new String[]{String.valueOf(relId), String.valueOf(BillTable.STATUS_TEMP)},
                null,
                null,
                null,
                "1"
        );

        if(c.getCount() > 0) {
            c.moveToFirst();
            return c.getLong(c.getColumnIndexOrThrow(BillTable.COL_ID));
        }
        return -1;
    }

    public long initBillsInDb(SQLiteDatabase db, Context context, long relId){

        Log.d("_DBD", "bi3 " + relId);
        ContentValues cw = new ContentValues();
        cw.put(BillTable.COL_RELATION, relId);
        cw.put(BillTable.COL_STATUS, BillTable.STATUS_TEMP);
        cw.put(BillTable.COL_NAME, BillTable.NAME_TEMP);
        cw.put(BillTable.COL_DESC, "");
        cw.put(BillTable.COL_DATE, "0");

        long retBillId = db.insertOrThrow(BillTable.TABLE_NAME, null, cw);

        Log.d("_DBD", "bi2 " + retBillId);
        for (BaseModel m : mModels) {
            m.initInDb(db, retBillId, context);
        }

        return  retBillId;

    }

    public void deleteBillsFromDb(SQLiteDatabase db,long billId){

        db.delete(BillTable.TABLE_NAME, BillTable.COL_ID + "=?", new String[]{String.valueOf(billId)});

        for (BaseModel m : mModels) {
            m.deleteFromDb(db, billId);
        }
    }


    public long continueBill(long relId){
        if(mFragment == null) return -1;

        Context context = mFragment.getContext();
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        long retBillId = -1;
        db.beginTransaction();

        try {
            Cursor c = db.query(
                    BillTable.TABLE_NAME,
                    new String[]{BillTable.COL_ID},
                    BillTable.COL_RELATION + "=? AND " + BillTable.COL_STATUS + "=?",
                    new String[]{String.valueOf(relId), String.valueOf(BillTable.STATUS_TEMP)},
                    null,
                    null,
                    null,
                    "1"
            );

            if (c.getCount() > 0) {
                c.moveToFirst();
                retBillId = c.getInt(c.getColumnIndexOrThrow(BillTable.COL_ID));
                Log.d("_DBD", "bill id on continue " + retBillId);

            } else {
                //create new temp row
                Log.d("_DBD", "bi3 " + relId);
                ContentValues cw = new ContentValues();
                cw.put(BillTable.COL_RELATION, relId);
                cw.put(BillTable.COL_STATUS, BillTable.STATUS_TEMP);
                cw.put(BillTable.COL_NAME, BillTable.NAME_TEMP);
                cw.put(BillTable.COL_DESC, "");
                cw.put(BillTable.COL_DATE, "0");

                retBillId = db.insertOrThrow(BillTable.TABLE_NAME, null, cw);

                Log.d("_DBD", "bi2 " + retBillId);
                for (BaseModel m : mModels) {
                    m.initInDb(db, retBillId, context);
                }
            }

            db.setTransactionSuccessful();
            //create segments with global segments
        } catch (Exception e) {
            Log.d("_DBD","err " + e.toString());
        } finally {
            db.endTransaction();
            db.close();
            return retBillId;
        }
    }

    public long initNewBill(long relId){
        if(mFragment == null) return -1;
        Context context = mFragment.getContext();

        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        long retBillId = -1;

        db.beginTransaction();
        try {

            long billId = getTempTableIdByRelId(db, relId);
/*
            Cursor c = db.query(
                    BillTable.TABLE_NAME,
                    new String[]{BillTable.COL_ID},
                    BillTable.COL_RELATION + "=? AND " + BillTable.COL_STATUS + "=?",
                    new String[]{String.valueOf(relId), String.valueOf(BillTable.STATUS_TEMP)},
                    null,
                    null,
                    null,
                    "1"
            );
*/

            if(billId != -1) {

                deleteBillsFromDb(db,billId);

               /* db.delete(BillTable.TABLE_NAME, BillTable.COL_ID + "=?", new String[]{String.valueOf(billId)});

                for (BaseModel m : mModels) {

                    m.deleteFromDb(db, billId);
                }*/
            }

            /*
            if (c.getCount() > 0) {
                c.moveToFirst();
                int billId =  // c.getInt(c.getColumnIndexOrThrow(BillTable.COL_ID));

                Log.d("_DBD", "bi1 "+billId);

                db.delete(BillTable.TABLE_NAME, BillTable.COL_ID + "=?", new String[]{String.valueOf(billId)});

                for (BaseModel m : mModels) {

                    m.deleteFromDb(db, billId);
                }
                Log.d("_DBD", "del m ");
                //temp row exists, delete bill and related bill ids
            }
*/

            retBillId = initBillsInDb(db,context, relId);

            //create new temp row
          /*  Log.d("_DBD", "bi3 "+relId);
            ContentValues cw = new ContentValues();
            cw.put(BillTable.COL_RELATION, relId);
            cw.put(BillTable.COL_STATUS, BillTable.STATUS_TEMP);
            cw.put(BillTable.COL_NAME, BillTable.NAME_TEMP);
            cw.put(BillTable.COL_DESC, "");
            cw.put(BillTable.COL_DATE, "0");

            retBillId = db.insertOrThrow(BillTable.TABLE_NAME, null, cw);

            Log.d("_DBD", "bi2 "+retBillId);
            for (BaseModel m : mModels) {
                m.initInDb(db, retBillId, context);
            }*/

            //fill bill tables with default settings

            db.setTransactionSuccessful();
            //create segments with global segments
        } catch (Exception e) {
            Log.d("_DBD","err " + e.toString());
        } finally {
            db.endTransaction();
            db.close();
            return retBillId;
        }



    }



    public ArrayList<String> getMainTableFromDb(long billId, int currViewPagerItem){
        if(mFragment == null) return null;
        DbManager dbManager = new DbManager(mFragment.getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Log.d("_DBD",billId +"");
        ArrayList<String> ret = mModels[currViewPagerItem].getMainTableFromDb(db,billId);

        db.close();

        return  ret;

    }


    public ArrayList<ArrayList<String>> getSegmentsFromDb(int modelPos, long modelId){
        DbManager dbManager = new DbManager(mFragment.getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();

        ArrayList<ArrayList<String>> ret = mModels[modelPos].getSegmentsFromDb(db, modelId);

        db.close();
        Log.d("_DBD","getting segments:"+modelId);
        return ret;
    }

    public void updateTableData(ArrayList<String> mainTableData, ArrayList<ArrayList<String>> segmentsData, long billId, int currViewPagerItem){
        DbManager dbManager = new DbManager(mFragment.getContext());
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            mModels[currViewPagerItem].updateMainTableInDb(db, mainTableData, billId);
            long modelId = getLastModelId(currViewPagerItem);
            mModels[currViewPagerItem].updateSegmentsInDb(db, segmentsData, modelId);
            db.setTransactionSuccessful();
        } catch (Exception e){
            Log.d("_DBD","err upd " + e.toString());
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void updateModelInDb(){

    }

    public long getLastModelId(int modelPos){
        return mModels[modelPos].getLastModelId();
    }

}
