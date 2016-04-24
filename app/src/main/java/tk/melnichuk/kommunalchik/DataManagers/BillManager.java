package tk.melnichuk.kommunalchik.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
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

    Fragment mFragment;

    public BillManager(Fragment fragment){
        mFragment = fragment;

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

    public static final int[] modelIndices = {
            INDEX_COMMUNAL,
            INDEX_GAS ,
            INDEX_COLD_WATER,
            INDEX_WASTE_WATER,
            INDEX_HOT_WATER,
            INDEX_HEATING,
            INDEX_ELECTRICITY,
            INDEX_PHONE
    };

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



    public long initNewBill(long relId){
        if(mFragment == null) return -1;
        Context context = mFragment.getContext();
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        long retBillId = -1;

        db.beginTransaction();
        try {
            long billId = getTempTableIdByRelId(db, relId,BillTable.STATUS_TEMP);

            if(billId != -1) {
                deleteBillsFromDb(db,billId);
            }
            retBillId = initBillsInDb(db,context, relId, BillTable.STATUS_TEMP);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("_DBD","err " + e.toString());
        } finally {
            db.endTransaction();
            db.close();
            return retBillId;
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
            long billId =  getTempTableIdByRelId(db, relId,BillTable.STATUS_TEMP);

            if(billId != -1){
                retBillId = billId;
                Log.d("_DBD", "bill id on continue " + retBillId);
            } else {
                retBillId = initBillsInDb(db,context, relId, BillTable.STATUS_TEMP);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("_DBD","err " + e.toString());
        } finally {
            db.endTransaction();
            db.close();
            return retBillId;
        }
    }

    public long initSavedBill(long relId){
        if(mFragment == null) return -1;
        Context context = mFragment.getContext();

        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        long retBillId = -1;

        db.beginTransaction();
        try {
            long billId = getTempTableIdByRelId(db, relId, BillTable.STATUS_TEMP_FROM_SAVED);

            if(billId != -1) {
                deleteBillsFromDb(db,billId);
            }

            retBillId = initBillsInDb(db,context, relId, BillTable.STATUS_TEMP_FROM_SAVED);


            for(int i=0;i<mModels.length;++i){
                ArrayList<String> mainTableData = mModels[i].getMainTableFromDb(db,relId);
                long modelId = mModels[i].getLastModelId();
                ArrayList<ArrayList<String>> segmentsData = mModels[i].getSegmentsFromDb(db,modelId,i);


                mModels[i].initInDb(db, retBillId, context);

                if(mainTableData != null) {
                    Log.d("_SDB", mainTableData.toString());
                    mModels[i].updateMainTableInDb(db, mainTableData, retBillId);
                }

                if(segmentsData != null) {
                    long savedModelId = mModels[i].getLastModelId();
                    mModels[i].updateSegmentsInDb(db,segmentsData,savedModelId,i);
                    Log.d("_SDB", segmentsData.toString());
                }

            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("_DBD","err initSavedBill()" + e.toString());
        } finally {
            db.endTransaction();
            db.close();
            return retBillId;
        }
    }

    public void writeBillsToExcel(long billId){

        Context context = mFragment.getContext();
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();

        String name = "", date = "";
        String thisId    = "A." + BillTable.COL_ID;
        String relId     = "A." + BillTable.COL_RELATION;
        String otherId   = "B." + BillTable.COL_ID;
        String otherName = "B." + BillTable.COL_NAME;
        String otherDate = "B." + BillTable.COL_DATE;

        Cursor c = db.query(
                BillTable.TABLE_NAME + " AS A INNER JOIN " + BillTable.TABLE_NAME + " AS B ON " + relId + "=" + otherId,
                new String[]{otherName, otherDate},
                thisId + "=?",
                new String[]{String.valueOf(billId)},
                null,
                null,
                null,
                "1");

        if(c.getCount() > 0){
            c.moveToFirst();
            name = c.getString(0);
            String dateStr = c.getString(1);

            Log.d("_EDB", "d:"+name + " d:"+ dateStr);

            date = dateStr.replace("/","_");
        }

        long time = System.currentTimeMillis();
        Log.d("_EDB", "n:"+name +" d:"+date +" t:"+ time);


        String fileName = name+"_"+date+"_"+time;

        ExcelManager em = new ExcelManager();
        WritableSheet ws = em.begin(fileName);

        if(ws != null) {

            int offset = 0;
            Resources res = context.getResources();

            for (int i = 0; i < mModels.length; ++i) {
                ArrayList<String> mainTableData = mModels[i].getMainTableFromDb(db, billId);
                long modelId = mModels[i].getLastModelId();
                ArrayList<ArrayList<String>> segmentsData = mModels[i].getSegmentsFromDb(db, modelId, i);
                offset += (mModels[i].addCellsExcelTable(offset, ws, res, mainTableData, segmentsData) + 3);
            }

            em.end();
        }
    }



    public long getTempTableIdByRelId(SQLiteDatabase db, long relId, int status){

        Cursor c = db.query(
                BillTable.TABLE_NAME,
                new String[]{BillTable.COL_ID},
                BillTable.COL_RELATION + "=? AND " + BillTable.COL_STATUS + "=?",
                new String[]{String.valueOf(relId), String.valueOf(status)},
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

    public long initBillsInDb(SQLiteDatabase db, Context context, long relId, int status){

        ContentValues cw = new ContentValues();
        cw.put(BillTable.COL_RELATION, relId);
        cw.put(BillTable.COL_STATUS, status);
        cw.put(BillTable.COL_NAME, BillTable.NAME_TEMP);
        cw.put(BillTable.COL_DESC, "");
        cw.put(BillTable.COL_DATE, "0");

        long retBillId = db.insertOrThrow(BillTable.TABLE_NAME, null, cw);

        for (BaseModel m : mModels) {
            m.initInDb(db, retBillId, context);
        }

        return  retBillId;
    }

    public long createSavedBill(String name, String date, long billId){
        long retRelId = -1;
        if(name == null || date == null) return retRelId;
        Context context = mFragment.getContext();
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cw = new ContentValues();
            cw.put(BillTable.COL_RELATION, BillTable.RELATION_NONE);
            cw.put(BillTable.COL_STATUS, BillTable.STATUS_SAVED);
            cw.put(BillTable.COL_NAME, name);
            cw.put(BillTable.COL_DESC, "");
            cw.put(BillTable.COL_DATE, date);
            long savedBillId = db.insertOrThrow(BillTable.TABLE_NAME,null,cw);

            for(int i=0;i<mModels.length;++i){
                ArrayList<String> mainTableData = mModels[i].getMainTableFromDb(db,billId);
                long modelId = mModels[i].getLastModelId();
                ArrayList<ArrayList<String>> segmentsData = mModels[i].getSegmentsFromDb(db,modelId,i);


                mModels[i].initInDb(db, savedBillId, context);

                if(mainTableData != null) {
                    Log.d("_SDB", mainTableData.toString());
                    mModels[i].updateMainTableInDb(db, mainTableData, savedBillId);
                }

                if(segmentsData != null) {
                    long savedModelId = mModels[i].getLastModelId();
                    mModels[i].updateSegmentsInDb(db,segmentsData,savedModelId,i);
                    Log.d("_SDB", segmentsData.toString());
                }
            }

            cw.clear();
            cw.put(BillTable.COL_RELATION, savedBillId);

            db.update(BillTable.TABLE_NAME, cw, BillTable.COL_ID + "=?", new String[]{String.valueOf(billId)});
            retRelId = savedBillId;

            db.setTransactionSuccessful();
        } catch (Exception e){
            Log.d("_SDB", "crt err " + e.toString() );
        } finally {
            db.endTransaction();
            db.close();
            return retRelId;
        }
    }


    public long updateSavedBill(String name, String date, long billId, long relId){
        long retRelId = -1;
        if(name == null || date == null) return retRelId;
        Context context = mFragment.getContext();
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cw = new ContentValues();
            cw.put(BillTable.COL_NAME, name);
            cw.put(BillTable.COL_DATE, date);
            db.update(BillTable.TABLE_NAME, cw, BillTable.COL_ID + "=?", new String[]{String.valueOf(relId)});

            for(int i=0;i<mModels.length;++i){
                ArrayList<String> mainTableData = mModels[i].getMainTableFromDb(db,billId);
                long modelId = mModels[i].getLastModelId();
                ArrayList<ArrayList<String>> segmentsData = mModels[i].getSegmentsFromDb(db,modelId,i);

                if(mainTableData != null) {
                    Log.d("_SDB", mainTableData.toString());
                    mModels[i].updateMainTableInDb(db, mainTableData, relId);
                }
                if(segmentsData != null) {
                    long savedModelId = mModels[i].getLastModelId();
                    mModels[i].updateSegmentsInDb(db,segmentsData,savedModelId,i);
                    Log.d("_SDB", segmentsData.toString());
                }

            }
            retRelId = relId;

            db.setTransactionSuccessful();
        } catch (Exception e){
            Log.d("_DBD", "crt err " + e.toString() );
        } finally {
            db.endTransaction();
            db.close();
            return retRelId;
        }
    }

    public void deleteBillsFromDb(SQLiteDatabase db,long billId){

        db.delete(BillTable.TABLE_NAME, BillTable.COL_ID + "=?", new String[]{String.valueOf(billId)});

        for (BaseModel m : mModels) {
            m.deleteFromDb(db, billId);
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

        ArrayList<ArrayList<String>> ret = mModels[modelPos].getSegmentsFromDb(db, modelId, modelPos);

        db.close();
        Log.d("_DBD", "getting segments:" + modelId + " model pos:" + modelPos);
        return ret;
    }

    public void updateTableData(ArrayList<String> mainTableData, ArrayList<ArrayList<String>> segmentsData, long billId, int currViewPagerItem){
        DbManager dbManager = new DbManager(mFragment.getContext());
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            mModels[currViewPagerItem].updateMainTableInDb(db, mainTableData, billId);
            long modelId = getLastModelId(currViewPagerItem);
            mModels[currViewPagerItem].updateSegmentsInDb(db, segmentsData, modelId, currViewPagerItem);
            db.setTransactionSuccessful();
        } catch (Exception e){
            Log.d("_DBD","err upd " + e.toString());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public long getLastModelId(int modelPos){
        return mModels[modelPos].getLastModelId();
    }

    public void addCommonCalcedSegments(ArrayList<Integer> segmentIds, ArrayList<String> segment, long billId){
        if(segmentIds == null || segmentIds.isEmpty()) return;
        DbManager dbManager = new DbManager(mFragment.getContext());
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            for(Integer i : segmentIds){
                mModels[i].addCalcedSegment(db,billId, segment);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("_DBD", "common seg exception " + e.toString());
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public Cursor getRelatedBillCursor(long relId){

        DbManager dbManager = new DbManager(mFragment.getContext());
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor c =  db.query(
                BillTable.TABLE_NAME,
                null,
                BillTable.COL_ID + "=?",
                new String[]{String.valueOf(relId)},
                null,
                null,
                null,
                "1");

        c.moveToFirst();
        return c;

    }

}
