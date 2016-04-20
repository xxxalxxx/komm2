package tk.melnichuk.kommunalchik.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import tk.melnichuk.kommunalchik.DataManagers.BillManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.CommunalTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.HeatingTable;

/**
 * Created by al on 30.03.16.
 */
public class HeatingModel extends BaseModel {
    public static final int
        BILL_SIZE = 8,
        INDEX_CALC = 0,
        INDEX_RECALC = 1,
        INDEX_SUB = 2,
        INDEX_COMP = 3,
        INDEX_TOTAL = 4,
        INDEX_OVERPAY = 5,
        INDEX_ADDPAY = 6,
        INDEX_SUM = 7;

    @Override
    void calcMainTable(BigDecimal[] b) {

        BigDecimal calc = b[INDEX_CALC].setScale(2, RoundingMode.HALF_UP);
        BigDecimal recalc= b[INDEX_RECALC].setScale(2, RoundingMode.HALF_UP);
        BigDecimal sub = b[INDEX_SUB].setScale(2, RoundingMode.HALF_UP);
        BigDecimal comp = b[INDEX_COMP].setScale(2, RoundingMode.HALF_UP);

        BigDecimal total1;
        if(calc.doubleValue()==0)
            total1 = b[INDEX_TOTAL].subtract(sub).subtract(comp).setScale(2, RoundingMode.HALF_UP);
        else total1 = calc.add(recalc ).subtract(sub).subtract(comp).setScale(2, RoundingMode.HALF_UP);

        BigDecimal overpay = b[INDEX_OVERPAY].setScale(2, RoundingMode.HALF_UP);
        BigDecimal addpay = b[INDEX_ADDPAY].setScale(2, RoundingMode.HALF_UP);
        BigDecimal total2 = total1.add(addpay);

        b[INDEX_CALC] = calc;
        b[INDEX_RECALC] = recalc;
        b[INDEX_SUB ] = sub;
        b[INDEX_COMP ] = comp;
        b[INDEX_TOTAL ] = total1;
        b[INDEX_OVERPAY] = overpay;
        b[INDEX_ADDPAY] = addpay;
        b[INDEX_SUM] = total2;
    }

    @Override
    BigDecimal[] getLastColumnItems() {
        return new BigDecimal[]{
                mMainTableData[INDEX_TOTAL],
                mMainTableData[INDEX_ADDPAY],
                mMainTableData[INDEX_SUM]
        };
    }


    long createMainTableInDb(SQLiteDatabase db, long billId, ArrayList<String> data){

        ContentValues cw = new ContentValues();
        cw.put(HeatingTable.COL_BILL_ID, billId );
        cw.put(HeatingTable.COL_CALC, data.get(INDEX_CALC));
        cw.put(HeatingTable.COL_RECALC, data.get(INDEX_RECALC));
        cw.put(HeatingTable.COL_SUB, data.get(INDEX_SUB));
        cw.put(HeatingTable.COL_COMP, data.get(INDEX_COMP));
        cw.put(HeatingTable.COL_OVERPAY, data.get(INDEX_OVERPAY));
        cw.put(HeatingTable.COL_ADDPAY, data.get(INDEX_ADDPAY));
        cw.put(HeatingTable.COL_TOTAL, data.get(INDEX_TOTAL));
        cw.put(HeatingTable.COL_SUM, data.get(INDEX_SUM));

        long modelId = db.insertOrThrow(HeatingTable.TABLE_NAME, null, cw);
        setLastModelId(modelId);
        return modelId;
    }


    public void updateMainTableInDb(SQLiteDatabase db, ArrayList<String>data, long billId){
        if(data == null || data.isEmpty()) return;

        long modelId = getModelId(db, HeatingTable.TABLE_NAME, HeatingTable.COL_ID, HeatingTable.COL_BILL_ID, String.valueOf(billId));

        if(modelId != -1){
            Log.d("_DBD", "upd segs not null bI:" + billId + " mI:" + modelId);

            setLastModelId(modelId);

            ContentValues cw = new ContentValues();
            cw.put(HeatingTable.COL_BILL_ID, billId );
            cw.put(HeatingTable.COL_CALC, data.get(INDEX_CALC));
            cw.put(HeatingTable.COL_RECALC, data.get(INDEX_RECALC));
            cw.put(HeatingTable.COL_SUB, data.get(INDEX_SUB));
            cw.put(HeatingTable.COL_COMP, data.get(INDEX_COMP));
            cw.put(HeatingTable.COL_OVERPAY, data.get(INDEX_OVERPAY));
            cw.put(HeatingTable.COL_ADDPAY, data.get(INDEX_ADDPAY));
            cw.put(HeatingTable.COL_TOTAL, data.get(INDEX_TOTAL));
            cw.put(HeatingTable.COL_SUM, data.get(INDEX_SUM));

            db.update(HeatingTable.TABLE_NAME, cw, HeatingTable.COL_ID + "=?", new String[]{String.valueOf(modelId)});
        }
    }

    @Override
    public void initInDb(SQLiteDatabase db, long billId,Context c){
        ArrayList<String> data = new ArrayList<>();

        for(int i=0;i<BILL_SIZE;++i){
            data.add(DEFAULT_INPUT);
        }

        long billTypeId = createMainTableInDb(db, billId, data);
        initSegmentsInDb(db, billTypeId, BillManager.INDEX_HEATING);
    }


    @Override
    public ArrayList<String> getMainTableFromDb(SQLiteDatabase db, long billId) {
        ArrayList<String> data = new ArrayList<>();
        for(int i=0;i<BILL_SIZE;++i){
            data.add("");
        }

        Cursor c = db.query(
                HeatingTable.TABLE_NAME,
                null,
                HeatingTable.COL_BILL_ID + "=?",
                new String[]{String.valueOf(billId)},
                null,
                null,
                null,
                "1"
        );

        Log.d("_DBD", "get " + billId);
        if(c.getCount() > 0){
            c.moveToFirst();
            Log.d("_DBD", "not null");
            long modelId = c.getLong(c.getColumnIndex(CommunalTable.COL_ID));
            setLastModelId(modelId);

            data.set(INDEX_CALC, c.getString(c.getColumnIndex(HeatingTable.COL_CALC)));
            data.set(INDEX_RECALC, c.getString(c.getColumnIndex(HeatingTable.COL_RECALC)));
            data.set(INDEX_SUB, c.getString(c.getColumnIndex(HeatingTable.COL_SUB)));
            data.set(INDEX_COMP, c.getString(c.getColumnIndex(HeatingTable.COL_COMP)));
            data.set(INDEX_OVERPAY, c.getString(c.getColumnIndex(HeatingTable.COL_OVERPAY)));
            data.set(INDEX_ADDPAY, c.getString(c.getColumnIndex(HeatingTable.COL_ADDPAY)));
            data.set(INDEX_TOTAL, c.getString(c.getColumnIndex(HeatingTable.COL_TOTAL)));
            data.set(INDEX_SUM, c.getString(c.getColumnIndex(HeatingTable.COL_SUM)));

        }

        return  data;
    }



    @Override
    public void deleteFromDb(SQLiteDatabase db, long billId) {

        int modelId = getModelId(db, HeatingTable.TABLE_NAME, HeatingTable.COL_ID, HeatingTable.COL_BILL_ID, String.valueOf(billId));

        if(modelId != -1){

            deleteSegmentsFromDb(db, modelId, BillManager.INDEX_HEATING);
            db.delete(HeatingTable.TABLE_NAME, HeatingTable.COL_ID + "=?", new String[]{String.valueOf(modelId)});
        }
    }


    @Override
    public void addCalcedSegment(SQLiteDatabase db, long billId, ArrayList<String> segment) {

        Cursor c = db.query(
                HeatingTable.TABLE_NAME,
                new String[]{HeatingTable.COL_ID, HeatingTable.COL_TOTAL, HeatingTable.COL_ADDPAY ,HeatingTable.COL_SUM},
                HeatingTable.COL_BILL_ID + "=?",
                new String[]{String.valueOf(billId)},
                null,
                null,
                null,
                "1");

        if(c.getCount() > 0) {
            c.moveToFirst();

            long modelId = c.getLong(c.getColumnIndex(HeatingTable.COL_ID));
            setLastModelId(modelId);

            String total =  c.getString(c.getColumnIndex(HeatingTable.COL_TOTAL));
            String addpay = c.getString(c.getColumnIndex(HeatingTable.COL_ADDPAY));
            String sum =  c.getString(c.getColumnIndex(HeatingTable.COL_SUM));

            ArrayList<String> newSegment = new ArrayList<>();
            for(String s : segment) newSegment.add(s);

            addCalcedItemsToSegment(newSegment, segment.get(2), new String[]{total, addpay, sum});
            addSegment(db, modelId, BillManager.INDEX_HEATING, newSegment);
        }
    }


}
