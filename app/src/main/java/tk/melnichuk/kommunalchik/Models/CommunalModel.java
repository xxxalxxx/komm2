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
import tk.melnichuk.kommunalchik.DataManagers.OptionsManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.CommunalTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.WaterRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.WaterTable;

/**
 * Created by al on 30.03.16.
 */
public class CommunalModel extends BaseModel {
    public static final int
    BILL_SIZE = 7,

    INDEX_CALC = 0,
    INDEX_RECALC = 1,
    INDEX_SUB = 2,
    INDEX_COMP = 3,
    INDEX_OVERPAY = 5,
    INDEX_ADDPAY = 6,
    INDEX_TOTAL = 4;


    @Override
    void calcMainTable(BigDecimal[] b) {
        b[INDEX_CALC] = b[INDEX_CALC].setScale(2, RoundingMode.HALF_UP);
        b[INDEX_RECALC] = b[INDEX_RECALC].setScale(2, RoundingMode.HALF_UP);
        b[INDEX_SUB] = b[INDEX_SUB].setScale(2, RoundingMode.HALF_UP);
        b[INDEX_COMP] = b[INDEX_COMP].setScale(2, RoundingMode.HALF_UP);
        b[INDEX_OVERPAY] = b[INDEX_OVERPAY].setScale(2, RoundingMode.HALF_UP);
        b[INDEX_ADDPAY] = b[INDEX_ADDPAY].setScale(2, RoundingMode.HALF_UP);


        if(b[INDEX_CALC].doubleValue()==0)
            b[INDEX_TOTAL] = b[INDEX_TOTAL].subtract(b[INDEX_SUB]).subtract(b[INDEX_COMP]);
        else b[INDEX_TOTAL] = b[INDEX_CALC].add(b[INDEX_RECALC]).subtract(b[INDEX_SUB]).subtract(b[INDEX_COMP]);

    }

    @Override
    BigDecimal[] getLastColumnItems() {
        return new BigDecimal[]{mMainTableData[INDEX_TOTAL], mMainTableData[INDEX_ADDPAY]};
    }

    long createMainTableInDb(SQLiteDatabase db, long billId, ArrayList<String> data){
        ContentValues cw = new ContentValues();
        cw.put(CommunalTable.COL_BILL_ID, billId );
        cw.put(CommunalTable.COL_CALC, data.get(INDEX_CALC));
        cw.put(CommunalTable.COL_RECALC, data.get(INDEX_RECALC));
        cw.put(CommunalTable.COL_SUB, data.get(INDEX_SUB));
        cw.put(CommunalTable.COL_COMP, data.get(INDEX_COMP));
        cw.put(CommunalTable.COL_OVERPAY, data.get(INDEX_OVERPAY));
        cw.put(CommunalTable.COL_ADDPAY, data.get(INDEX_ADDPAY));
        cw.put(CommunalTable.COL_TOTAL, data.get(INDEX_TOTAL));

        long modelId = db.insertOrThrow(CommunalTable.TABLE_NAME, null, cw);
        setLastModelId(modelId);
        return modelId;
    }


    public void updateMainTableInDb(SQLiteDatabase db, ArrayList<String>data, long billId){
        if(data == null || data.isEmpty()) return;

        long modelId = getModelId(db, CommunalTable.TABLE_NAME, CommunalTable.COL_ID, CommunalTable.COL_BILL_ID, String.valueOf(billId));

        if(modelId != -1){
            Log.d("_DBD", "upd segs not null bI:" + billId + " mI:" + modelId);

            setLastModelId(modelId);

            ContentValues cw = new ContentValues();
            cw.put(CommunalTable.COL_BILL_ID, billId );
            cw.put(CommunalTable.COL_CALC, data.get(INDEX_CALC));
            cw.put(CommunalTable.COL_RECALC, data.get(INDEX_RECALC));
            cw.put(CommunalTable.COL_SUB, data.get(INDEX_SUB));
            cw.put(CommunalTable.COL_COMP, data.get(INDEX_COMP));
            cw.put(CommunalTable.COL_OVERPAY, data.get(INDEX_OVERPAY));
            cw.put(CommunalTable.COL_ADDPAY, data.get(INDEX_ADDPAY));
            cw.put(CommunalTable.COL_TOTAL, data.get(INDEX_TOTAL));

            db.update(CommunalTable.TABLE_NAME, cw, CommunalTable.COL_ID + "=?", new String[]{String.valueOf(modelId)});
        }
    }


    @Override
    public void initInDb(SQLiteDatabase db, long billId,Context c){
        ArrayList<String> data = new ArrayList<>();

        for(int i=0;i<BILL_SIZE;++i){
            data.add(DEFAULT_INPUT);
        }

        long billTypeId = createMainTableInDb(db, billId, data);
        initSegmentsInDb(db, billTypeId, BillManager.INDEX_COMMUNAL);
    }


    @Override
    public ArrayList<String> getMainTableFromDb(SQLiteDatabase db, long billId) {
        ArrayList<String> data = new ArrayList<>();
        for(int i=0;i<BILL_SIZE;++i){
            data.add("");
        }

        Cursor c = db.query(
                CommunalTable.TABLE_NAME,
                new String[]{
                        CommunalTable.COL_ID,
                        CommunalTable.COL_CALC,
                        CommunalTable.COL_RECALC,
                        CommunalTable.COL_SUB,
                        CommunalTable.COL_COMP,
                        CommunalTable.COL_OVERPAY,
                        CommunalTable.COL_ADDPAY,
                        CommunalTable.COL_TOTAL
                },
                CommunalTable.COL_BILL_ID + "=?",
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

            data.set(INDEX_CALC, c.getString(c.getColumnIndex(CommunalTable.COL_CALC)));
            data.set(INDEX_RECALC, c.getString(c.getColumnIndex(CommunalTable.COL_RECALC)));
            data.set(INDEX_SUB, c.getString(c.getColumnIndex(CommunalTable.COL_SUB)));
            data.set(INDEX_COMP, c.getString(c.getColumnIndex(CommunalTable.COL_COMP)));
            data.set(INDEX_OVERPAY, c.getString(c.getColumnIndex(CommunalTable.COL_OVERPAY)));
            data.set(INDEX_ADDPAY, c.getString(c.getColumnIndex(CommunalTable.COL_ADDPAY)));
            data.set(INDEX_TOTAL, c.getString(c.getColumnIndex(CommunalTable.COL_TOTAL)));

        }

        return  data;
    }

    @Override
    public void deleteFromDb(SQLiteDatabase db, long billId) {

       int modelId = getModelId(db, CommunalTable.TABLE_NAME, CommunalTable.COL_ID, CommunalTable.COL_BILL_ID, String.valueOf(billId));

        if(modelId != -1){

            deleteSegmentsFromDb(db, modelId, BillManager.INDEX_COMMUNAL);
            db.delete(CommunalTable.TABLE_NAME, CommunalTable.COL_ID + "=?", new String[]{String.valueOf(modelId)});
        }
    }


    @Override
    public void addCalcedSegment(SQLiteDatabase db, long billId, ArrayList<String> segment) {

        Cursor c = db.query(
                CommunalTable.TABLE_NAME,
                new String[]{CommunalTable.COL_ID, CommunalTable.COL_TOTAL, CommunalTable.COL_ADDPAY},
                CommunalTable.COL_BILL_ID + "=?",
                new String[]{String.valueOf(billId)},
                null,
                null,
                null,
                "1");

        if(c.getCount() > 0) {
            c.moveToFirst();

            long modelId = c.getLong(c.getColumnIndex(CommunalTable.COL_ID));
            setLastModelId(modelId);

            String total =  c.getString(c.getColumnIndex(CommunalTable.COL_TOTAL));
            String addpay = c.getString(c.getColumnIndex(CommunalTable.COL_ADDPAY));

            ArrayList<String> newSegment = new ArrayList<>();
            for(String s : segment) newSegment.add(s);

            addCalcedItemsToSegment(newSegment, segment.get(2), new String[]{total, addpay});
            addSegment(db, modelId,BillManager.INDEX_COMMUNAL, newSegment);
        }
    }


}
