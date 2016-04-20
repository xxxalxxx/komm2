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
import tk.melnichuk.kommunalchik.DataManagers.Tables.ElectricityRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.ElectricityTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.PhoneRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.PhoneTable;

/**
 * Created by al on 30.03.16.
 */
public class PhoneModel extends BaseModel {
    public static final int
        BILL_SIZE = 10,

        NUM_PHONE_ROWS = 3,

        INDEX_TAX = 0,

        INDEX_PHONE_NO_TAX = 1,
        INDEX_PHONE_TAX = 2,
        INDEX_PHONE_TOTAL = 3,

        INDEX_RADIO_NO_TAX = 4,
        INDEX_RADIO_TAX = 5,
        INDEX_RADIO_TOTAL = 6,

        INDEX_SUM_NO_TAX = 7,
        INDEX_SUM_TAX = 8,
        INDEX_SUM_TOTAL = 9;

    @Override
    void calcMainTable(BigDecimal[] b) {

        BigDecimal noTax1,withTax1,total1,
                noTax2, withTax2,total2,
                noTax3,withTax3,total3;

        final BigDecimal TAX = b[INDEX_TAX].divide(new BigDecimal(100));

        noTax1 = b[INDEX_PHONE_NO_TAX].setScale(2, RoundingMode.HALF_UP);
        withTax1 = noTax1.multiply(TAX).setScale(2, RoundingMode.HALF_UP);
        if(noTax1.doubleValue()==0 || withTax1.doubleValue()==0)
            total1 = b[INDEX_PHONE_TOTAL].setScale(2, RoundingMode.HALF_UP);
        else total1 = noTax1.add(withTax1);

        noTax2 = b[INDEX_RADIO_NO_TAX].setScale(2, RoundingMode.HALF_UP);
        withTax2 = noTax2.multiply(TAX).setScale(2, RoundingMode.HALF_UP);
        if(noTax2.doubleValue()==0 || withTax2.doubleValue()==0)
            total2 = b[INDEX_RADIO_TOTAL].setScale(2, RoundingMode.HALF_UP);
        else total2 = noTax2.add(withTax2);

        noTax3 = noTax1.add(noTax2);
        withTax3 = withTax1.add(withTax2);
        if(total1.doubleValue()!=0 || total2.doubleValue()!=0)
            total3 = total1.add(total2);
        else if(noTax3.add(withTax3).doubleValue()!=0)
            total3 = noTax3.add(withTax3);
        else total3 = b[INDEX_SUM_TOTAL].setScale(2, RoundingMode.HALF_UP);

        b[INDEX_PHONE_NO_TAX] = noTax1;
        b[INDEX_PHONE_TAX] = withTax1;
        b[INDEX_PHONE_TOTAL] = total1;

        b[INDEX_RADIO_NO_TAX] = noTax2;
        b[INDEX_RADIO_TAX] = withTax2;
        b[INDEX_RADIO_TOTAL] = total2;

        b[INDEX_SUM_NO_TAX] = noTax3;
        b[INDEX_SUM_TAX] = withTax3;
        b[INDEX_SUM_TOTAL] = total3;

    }

    @Override
    BigDecimal[] getLastColumnItems() {
        return new BigDecimal[]{
                mMainTableData[INDEX_PHONE_TOTAL],
                mMainTableData[INDEX_RADIO_TOTAL],
                mMainTableData[INDEX_SUM_TOTAL]
        };
    }


    long createMainTableInDb(SQLiteDatabase db, long billId, ArrayList<String> data){


        ContentValues cw = new ContentValues();
        cw.put(PhoneTable.COL_BILL_ID, billId );
        cw.put(PhoneTable.COL_TAX, data.get(INDEX_TAX));

        long id = db.insertOrThrow(PhoneTable.TABLE_NAME, null, cw);
        setLastModelId(id);

        int[] preTaxIndices = {INDEX_PHONE_NO_TAX,INDEX_RADIO_NO_TAX, INDEX_SUM_NO_TAX};


        for(int i = 0; i < NUM_PHONE_ROWS; ++i){
            cw.clear();
            cw.put(PhoneRowTable.COL_PHONE_BILL_ID, id);
            cw.put(PhoneRowTable.COL_TYPE, i);
            cw.put(PhoneRowTable.COL_PRETAX, data.get(preTaxIndices[i]));
            cw.put(PhoneRowTable.COL_TAX,  data.get(preTaxIndices[i] + 1));
            cw.put(PhoneRowTable.COL_TOTAL, data.get(preTaxIndices[i] + 2));

            db.insertOrThrow(PhoneRowTable.TABLE_NAME, null, cw);
        }

        Log.d("_DBD", "create table " + billId);

        return id;
    }




    public void updateMainTableInDb(SQLiteDatabase db, ArrayList<String>data, long billId){
        if(data == null || data.isEmpty()) return;


        long modelId = getModelId(db, PhoneTable.TABLE_NAME, PhoneTable.COL_ID, PhoneTable.COL_BILL_ID, String.valueOf(billId));
        Log.d("_DBD", "upd segs not null bI:" + billId + " mI:" + modelId + " d:" + data.toString());



        if(modelId != -1) {
            setLastModelId(modelId);

            String modelIdStr = String.valueOf(modelId);
            ContentValues cw = new ContentValues();
            cw.put(PhoneTable.COL_BILL_ID, billId );
            cw.put(PhoneTable.COL_TAX, data.get(INDEX_TAX));

            db.update(PhoneTable.TABLE_NAME, cw, PhoneTable.COL_ID + "=?", new String[]{modelIdStr});

            int[] preTaxIndices = {INDEX_PHONE_NO_TAX,INDEX_RADIO_NO_TAX, INDEX_SUM_NO_TAX};
            String phoneRowWhereString = PhoneRowTable.COL_PHONE_BILL_ID + "=? AND " + PhoneRowTable.COL_TYPE + "=?";

            for(int i = 0; i < NUM_PHONE_ROWS; ++i){
                cw.clear();
                cw.put(PhoneRowTable.COL_PHONE_BILL_ID, modelId);
                cw.put(PhoneRowTable.COL_TYPE, i);
                cw.put(PhoneRowTable.COL_PRETAX, data.get(preTaxIndices[i]));
                cw.put(PhoneRowTable.COL_TAX,  data.get(preTaxIndices[i] + 1));
                cw.put(PhoneRowTable.COL_TOTAL, data.get(preTaxIndices[i] + 2));

                db.update(PhoneRowTable.TABLE_NAME, cw, phoneRowWhereString, new String[]{modelIdStr, String.valueOf(i)});
            }
        }
    }



    @Override
    public void initInDb(SQLiteDatabase db, long billId, Context c){
        ArrayList<String> data = new ArrayList<>();
        OptionsManager opts = new OptionsManager(c);
        opts.start();


        for(int i=0;i<BILL_SIZE;++i){
            switch (i){
                case INDEX_TAX:
                    data.add( String.valueOf(opts.getTaxRate()) );
                    break;
                case INDEX_PHONE_NO_TAX:
                    data.add( String.valueOf(opts.getPhoneRate()) );
                    break;
                case INDEX_RADIO_NO_TAX:
                    data.add( String.valueOf(opts.getRadioRate()) );
                    break;
                default:
                    data.add(DEFAULT_INPUT);
            }
        }


        long billTypeId = createMainTableInDb(db, billId, data);
        initSegmentsInDb(db, billTypeId, BillManager.INDEX_PHONE);

        Log.d("_DBD", "init " + data.toString());


    }




    @Override
    public ArrayList<String> getMainTableFromDb(SQLiteDatabase db, long billId) {
        ArrayList<String> data = new ArrayList<>();
        for(int i=0;i<BILL_SIZE;++i){
            data.add("");
        }

        Cursor c = db.query(
                PhoneTable.TABLE_NAME,
                null,
                PhoneTable.COL_BILL_ID + "=?",
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
            long modelId = c.getLong(c.getColumnIndex(PhoneTable.COL_ID));
            setLastModelId(modelId);

            data.set(INDEX_TAX, c.getString(c.getColumnIndex(PhoneTable.COL_TAX)));

            c = db.query(
                PhoneRowTable.TABLE_NAME,
                null,
                    PhoneRowTable.COL_PHONE_BILL_ID + "=?",
                new String[]{String.valueOf(modelId)},
                null,
                null,
                PhoneRowTable.COL_TYPE + " ASC",
                String.valueOf(NUM_PHONE_ROWS)
            );

            if(c.getCount() > 0){
                while (c.moveToNext()){

                    int type = c.getInt(c.getColumnIndex(PhoneRowTable.COL_TYPE));

                    String preTax = c.getString(c.getColumnIndex(PhoneRowTable.COL_PRETAX));
                    String tax = c.getString(c.getColumnIndex(PhoneRowTable.COL_TAX));
                    String total = c.getString(c.getColumnIndex(PhoneRowTable.COL_TOTAL));

                    switch (type){
                        case PhoneRowTable.TYPE_PHONE:
                            data.set(INDEX_PHONE_NO_TAX, preTax);
                            data.set(INDEX_PHONE_TAX, tax);
                            data.set(INDEX_PHONE_TOTAL, total);
                            break;
                        case PhoneRowTable.TYPE_RADIO:
                            data.set(INDEX_RADIO_NO_TAX, preTax);
                            data.set(INDEX_RADIO_TAX,tax);
                            data.set(INDEX_RADIO_TOTAL, total);
                            break;
                        case PhoneRowTable.TYPE_TOTAL:
                            data.set(INDEX_SUM_NO_TAX, preTax);
                            data.set(INDEX_SUM_TAX, tax);
                            data.set(INDEX_SUM_TOTAL, total);
                            break;
                    }
                }
            }
        }

        return  data;
    }

    @Override
    public void deleteFromDb(SQLiteDatabase db, long billId) {
        int modelId = getModelId(db, PhoneTable.TABLE_NAME, PhoneTable.COL_ID, PhoneTable.COL_BILL_ID, String.valueOf(billId));
        Log.d("_DBD","dfd "+billId + " " + modelId);
        if(modelId != -1){
            String currIdStr = String.valueOf(modelId);
            deleteSegmentsFromDb(db, modelId, BillManager.INDEX_PHONE);
            db.delete(PhoneRowTable.TABLE_NAME, PhoneRowTable.COL_PHONE_BILL_ID + "=?", new String[]{currIdStr});
            db.delete(PhoneTable.TABLE_NAME, PhoneTable.COL_ID + "=?", new String[]{currIdStr});
        }
    }




    @Override
    public void addCalcedSegment(SQLiteDatabase db, long billId, ArrayList<String> segment) {

        long modelId = getModelId(db, PhoneTable.TABLE_NAME, PhoneTable.COL_ID, PhoneTable.COL_BILL_ID, String.valueOf(billId));
        if(modelId != -1) {

            setLastModelId(modelId);

            Cursor c = db.query(
                    PhoneRowTable.TABLE_NAME,
                    new String[]{PhoneRowTable.COL_TOTAL},
                    PhoneRowTable.COL_PHONE_BILL_ID + "=?",
                    new String[]{String.valueOf(modelId)},
                    null,
                    null,
                    PhoneRowTable.COL_TYPE + " ASC",
                    String.valueOf(NUM_PHONE_ROWS)
            );

            String[] items = new String[NUM_PHONE_ROWS];

            if(c.getCount() > 0){
                int i = 0;
                while(c.moveToNext()) {
                    items[i] = c.getString(c.getColumnIndexOrThrow(PhoneRowTable.COL_TOTAL));
                    ++i;
                }
            }

            ArrayList<String> newSegment = new ArrayList<>();
            for(String s : segment) newSegment.add(s);

            addCalcedItemsToSegment(newSegment, segment.get(2), items);

            addSegment(db, modelId, BillManager.INDEX_PHONE, newSegment);
        }
    }
}
