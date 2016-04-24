package tk.melnichuk.kommunalchik.Models;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import jxl.write.WritableSheet;
import jxl.write.WriteException;
import tk.melnichuk.kommunalchik.DataManagers.BillManager;
import tk.melnichuk.kommunalchik.DataManagers.ExcelManager;
import tk.melnichuk.kommunalchik.DataManagers.OptionsManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.BillTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.WaterRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.WaterTable;
import tk.melnichuk.kommunalchik.Helpers.Utils;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 30.03.16.
 */
public class WaterModel extends BaseModel {
    int mType = 0;

    public static final int
        BILL_SIZE = 16,

        TYPE_COLD = 0,
        TYPE_WASTE = 1,
        TYPE_HOT = 2,

        COUNTER_NONE = 0,
        COUNTER_EXISTS = 1,

        INDEX_COUNTER = 9,
        INDEX_CURR = 0,
        INDEX_PREV = 1,
        INDEX_DIFF = 2,
        INDEX_RATE = 3,
        INDEX_CALC = 4,
        INDEX_RECALC = 5 ,
        INDEX_SUB = 6 ,
        INDEX_COMP = 7,
        INDEX_TOTAL = 8,
        INDEX_COUNTER_ROW_CALC = 10,
        INDEX_COUNTER_ROW_RECALC = 11,
        INDEX_COUNTER_ROW_SUB = 12,
        INDEX_COUNTER_ROW_COMP = 13,
        INDEX_COUNTER_ROW_TOTAL = 14,
        INDEX_SUM = 15;

    public WaterModel(int type) {
        mType = type;
    }

    @Override
    void calcMainTable(BigDecimal[] b) {

        boolean counterExists = b[INDEX_COUNTER].floatValue() == (float) COUNTER_EXISTS;

        BigDecimal curr = b[INDEX_CURR].setScale(2, RoundingMode.HALF_UP);
        BigDecimal  prev = b[INDEX_PREV].setScale(2, RoundingMode.HALF_UP);

        BigDecimal diff;

        if(curr.doubleValue()==0 && prev.doubleValue()==0 )
            diff = b[INDEX_DIFF].setScale(2, RoundingMode.HALF_UP);
        else
            diff = curr.subtract(prev);

        BigDecimal rate = b[INDEX_RATE].setScale(3, RoundingMode.HALF_UP);


        BigDecimal calc1;
        if(diff.doubleValue()==0 || rate.doubleValue()==0)
            calc1 =  b[INDEX_CALC].setScale(2, RoundingMode.HALF_UP);
        else calc1 = diff.multiply(rate).setScale(2, RoundingMode.HALF_UP);


        BigDecimal recalc1 = b[INDEX_RECALC].setScale(2, RoundingMode.HALF_UP);
        BigDecimal sub1 = b[INDEX_SUB].setScale(2, RoundingMode.HALF_UP);
        BigDecimal comp1 = b[INDEX_COMP].setScale(2, RoundingMode.HALF_UP);

        BigDecimal total1;

        if(calc1.doubleValue()==0)
            total1 = b[INDEX_TOTAL].add(recalc1).subtract(sub1).subtract(comp1);
        else total1 = calc1.add(recalc1).subtract(sub1).subtract(comp1);


        BigDecimal calc2 = b[INDEX_COUNTER_ROW_CALC].setScale(2, RoundingMode.HALF_UP);
        BigDecimal recalc2 = b[INDEX_COUNTER_ROW_RECALC].setScale(2, RoundingMode.HALF_UP);
        BigDecimal sub2 = b[INDEX_COUNTER_ROW_SUB].setScale(2, RoundingMode.HALF_UP);
        BigDecimal comp2 = b[INDEX_COUNTER_ROW_COMP].setScale(2, RoundingMode.HALF_UP);

        BigDecimal total2;
        if (calc2.doubleValue() == 0)
            total2 = b[INDEX_COUNTER_ROW_TOTAL].add(recalc2).subtract(sub2).subtract(comp2);
        else
            total2 = calc2.add(recalc2).subtract(sub2).subtract(comp2);


        BigDecimal total3;
        if( counterExists ) total3 = total2.add(total1);
        else total3 = total1;

        b[INDEX_CURR] = curr;
        b[INDEX_PREV ] = prev;
        b[INDEX_DIFF] = diff;
        b[INDEX_RATE] = rate;
        b[INDEX_CALC] = calc1;
        b[INDEX_RECALC] = recalc1;
        b[INDEX_SUB] = sub1;
        b[INDEX_COMP] = comp1;
        b[INDEX_TOTAL] = total1;
        b[INDEX_COUNTER_ROW_CALC] = calc2;
        b[INDEX_COUNTER_ROW_RECALC] = recalc2;
        b[INDEX_COUNTER_ROW_SUB] = sub2;
        b[INDEX_COUNTER_ROW_COMP] = comp2;
        b[INDEX_COUNTER_ROW_TOTAL] = total2;
        b[INDEX_SUM] = total3;

    }

    @Override
    BigDecimal[] getLastColumnItems() {
        return new BigDecimal[]{
                mMainTableData[INDEX_TOTAL],
                mMainTableData[INDEX_COUNTER_ROW_TOTAL],
                mMainTableData[INDEX_SUM]
        };
    }


    @Override
    public int addCellsExcelTable(int rowsOffset, WritableSheet ws, Resources res,
                                  ArrayList<String> mainTableData, ArrayList<ArrayList<String>> segmentsData) {

        if(mainTableData ==  null || mainTableData.isEmpty()) return 0;
        int row1 = rowsOffset + 2,
            row2 = row1 + 1,
            row3 = row2 + 1,
            row4 = row3 + 1;
        int sumRow = row4;
        try {

            Log.d("_EDB", "mtd:" + mainTableData.toString());
            Log.d("_EDB", "sd:" + segmentsData.toString());

            int counter = Integer.parseInt(mainTableData.get(INDEX_COUNTER));


            if (counter == COUNTER_NONE) {

                int indexRemoved = 4;
                if(segmentsData!=null && !segmentsData.isEmpty())
                    for(ArrayList<String> segment : segmentsData)
                        if(segment.size() > indexRemoved)
                            segment.remove(indexRemoved);


            } else {
                sumRow = row4 + 1;
                ExcelManager.addNumberToSheet(ws, 3, row4, res.getString(R.string.consumed));
                ExcelManager.addNumberToSheet(ws, 4, row4, mainTableData.get(INDEX_COUNTER_ROW_CALC));
                ExcelManager.addNumberToSheet(ws, 5, row4, mainTableData.get(INDEX_COUNTER_ROW_RECALC));
                ExcelManager.addNumberToSheet(ws, 6, row4, mainTableData.get(INDEX_COUNTER_ROW_SUB));
                ExcelManager.addNumberToSheet(ws, 7, row4, mainTableData.get(INDEX_COUNTER_ROW_COMP));
                ExcelManager.addNumberToSheet(ws, 8, row4, mainTableData.get(INDEX_COUNTER_ROW_TOTAL));
            }

            int titleId = mType == TYPE_COLD ? R.string.coldWater
                    : (mType == TYPE_WASTE ? R.string.wasteWater: R.string.hotWater);
            String title = res.getString(titleId);
            ExcelManager.addTitleToSheet(ws, 0, 8, rowsOffset, title);


            ExcelManager.addLabelToSheet(ws, 0, row1, res.getString(R.string.data));
            ws.mergeCells(0, row1, 2, row1);

            ExcelManager.addLabelToSheet(ws, 0, row2, res.getString(R.string.current));
            ExcelManager.addLabelToSheet(ws, 1, row2, res.getString(R.string.previous));
            ExcelManager.addLabelToSheet(ws, 2, row2, res.getString(R.string.difference));
            ExcelManager.addLabelToSheet(ws, 3, row2, res.getString(R.string.rate));
            ExcelManager.addLabelToSheet(ws, 4, row2, res.getString(R.string.calculated));
            ExcelManager.addLabelToSheet(ws, 5, row2, res.getString(R.string.recalculated));
            ExcelManager.addLabelToSheet(ws, 6, row2, res.getString(R.string.subsidy));
            ExcelManager.addLabelToSheet(ws, 7, row2, res.getString(R.string.compensation));
            ExcelManager.addLabelToSheet(ws, 8, row2, res.getString(R.string.sum));

            ExcelManager.addNumberToSheet(ws, 0, row3, mainTableData.get(INDEX_CURR));
            ExcelManager.addNumberToSheet(ws, 1, row3, mainTableData.get(INDEX_PREV));
            ExcelManager.addNumberToSheet(ws, 2, row3, mainTableData.get(INDEX_DIFF));
            ExcelManager.addNumberToSheet(ws, 3, row3, mainTableData.get(INDEX_RATE));
            ExcelManager.addNumberToSheet(ws, 4, row3, mainTableData.get(INDEX_CALC));
            ExcelManager.addNumberToSheet(ws, 5, row3, mainTableData.get(INDEX_RECALC));
            ExcelManager.addNumberToSheet(ws, 6, row3, mainTableData.get(INDEX_SUB));
            ExcelManager.addNumberToSheet(ws, 7, row3, mainTableData.get(INDEX_COMP));
            ExcelManager.addNumberToSheet(ws, 8, row3, mainTableData.get(INDEX_TOTAL));

            ExcelManager.addLabelToSheet(ws, 6, sumRow, res.getString(R.string.table_sum));
            ws.mergeCells(6, sumRow, 7, sumRow);

            ExcelManager.addNumberToSheet(ws, 8, sumRow, mainTableData.get(INDEX_SUM));

            ExcelManager.addSegmentsToExcelCellsArrayList(ws, res, segmentsData, row1, 9);
        } catch (WriteException e){
            Log.d("_EDB", "e gas:"+e.toString());
        }

        return sumRow - rowsOffset;
    }



    long createMainTableInDb(SQLiteDatabase db, long billId, ArrayList<String> data){
        ContentValues cw = new ContentValues();
        cw.put(WaterTable.COL_BILL_ID, billId );
        cw.put(WaterTable.COL_TYPE, mType);
        cw.put(WaterTable.COL_COUNTER, data.get(INDEX_COUNTER));
        cw.put(WaterTable.COL_CURR, data.get(INDEX_CURR));
        cw.put(WaterTable.COL_PREV, data.get(INDEX_PREV));
        cw.put(WaterTable.COL_DIFF, data.get(INDEX_DIFF));
        cw.put(WaterTable.COL_RATE, data.get(INDEX_RATE));
        cw.put(WaterTable.COL_SUM, data.get(INDEX_SUM));


        long id = db.insertOrThrow(WaterTable.TABLE_NAME, null, cw);

        cw.clear();

        cw.put(WaterRowTable.COL_WATER_BILL_ID, id);
        cw.put(WaterRowTable.COL_ROW_TYPE, WaterRowTable.ROW_TYPE_NORMAL);
        cw.put(WaterRowTable.COL_CALC, data.get(INDEX_CALC));
        cw.put(WaterRowTable.COL_RECALC, data.get(INDEX_RECALC));
        cw.put(WaterRowTable.COL_SUB, data.get(INDEX_SUB));
        cw.put(WaterRowTable.COL_COMP, data.get(INDEX_COMP));
        cw.put(WaterRowTable.COL_TOTAL, data.get(INDEX_TOTAL));

        db.insertOrThrow(WaterRowTable.TABLE_NAME, null, cw);
        cw.clear();

        cw.put(WaterRowTable.COL_WATER_BILL_ID, id);
        cw.put(WaterRowTable.COL_ROW_TYPE, WaterRowTable.ROW_TYPE_COUNTER);
        cw.put(WaterRowTable.COL_CALC, data.get(INDEX_COUNTER_ROW_CALC));
        cw.put(WaterRowTable.COL_RECALC, data.get(INDEX_COUNTER_ROW_RECALC));
        cw.put(WaterRowTable.COL_SUB, data.get(INDEX_COUNTER_ROW_SUB));
        cw.put(WaterRowTable.COL_COMP, data.get(INDEX_COUNTER_ROW_COMP));
        cw.put(WaterRowTable.COL_TOTAL, data.get(INDEX_COUNTER_ROW_TOTAL));

        db.insertOrThrow(WaterRowTable.TABLE_NAME, null, cw);
        Log.d("_DBD", "create table " + billId);
        return id;
    }

    public void updateMainTableInDb(SQLiteDatabase db, ArrayList<String>data, long billId){
        if(data == null || data.isEmpty()) return;

        Cursor c = db.query(
                WaterTable.TABLE_NAME,
                new String[]{WaterTable.COL_ID},
                WaterTable.COL_BILL_ID + "=? AND " + WaterTable.COL_TYPE + "=?",
                new String[]{String.valueOf(billId), String.valueOf(mType)},
                null,
                null,
                null,
                "1");
        if(c.getCount() > 0) {


            c.moveToFirst();
            long modelId = c.getLong(c.getColumnIndex(WaterTable.COL_ID));

            Log.d("_DBD", "upd segs not null bI:"+billId+" mI:"+modelId);
            String modelIdStr = String.valueOf(modelId);
            setLastModelId(modelId);

            ContentValues cw = new ContentValues();
            cw.put(WaterTable.COL_BILL_ID, billId );
            cw.put(WaterTable.COL_TYPE, mType);
            cw.put(WaterTable.COL_COUNTER, data.get(INDEX_COUNTER));
            cw.put(WaterTable.COL_CURR, data.get(INDEX_CURR));
            cw.put(WaterTable.COL_PREV, data.get(INDEX_PREV));
            cw.put(WaterTable.COL_DIFF, data.get(INDEX_DIFF));
            cw.put(WaterTable.COL_RATE, data.get(INDEX_RATE));
            cw.put(WaterTable.COL_SUM, data.get(INDEX_SUM));

            db.update(WaterTable.TABLE_NAME, cw, WaterTable.COL_ID + "=?", new String[]{modelIdStr});

            cw.clear();

            cw.put(WaterRowTable.COL_WATER_BILL_ID, modelId);
            cw.put(WaterRowTable.COL_ROW_TYPE, WaterRowTable.ROW_TYPE_NORMAL);
            cw.put(WaterRowTable.COL_CALC, data.get(INDEX_CALC));
            cw.put(WaterRowTable.COL_RECALC, data.get(INDEX_RECALC));
            cw.put(WaterRowTable.COL_SUB, data.get(INDEX_SUB));
            cw.put(WaterRowTable.COL_COMP, data.get(INDEX_COMP));
            cw.put(WaterRowTable.COL_TOTAL, data.get(INDEX_TOTAL));

            String waterRowWhereQuery =  WaterRowTable.COL_WATER_BILL_ID + "=? AND " + WaterRowTable.COL_ROW_TYPE + "=?";

            db.insertOrThrow(WaterRowTable.TABLE_NAME, null, cw);
            db.update(WaterRowTable.TABLE_NAME, cw,
                    waterRowWhereQuery,
                    new String[]{modelIdStr, String.valueOf(WaterRowTable.ROW_TYPE_NORMAL)});
            cw.clear();

            cw.put(WaterRowTable.COL_WATER_BILL_ID, modelId);
            cw.put(WaterRowTable.COL_ROW_TYPE, WaterRowTable.ROW_TYPE_COUNTER);
            cw.put(WaterRowTable.COL_CALC, data.get(INDEX_COUNTER_ROW_CALC));
            cw.put(WaterRowTable.COL_RECALC, data.get(INDEX_COUNTER_ROW_RECALC));
            cw.put(WaterRowTable.COL_SUB, data.get(INDEX_COUNTER_ROW_SUB));
            cw.put(WaterRowTable.COL_COMP, data.get(INDEX_COUNTER_ROW_COMP));
            cw.put(WaterRowTable.COL_TOTAL, data.get(INDEX_COUNTER_ROW_TOTAL));

            db.update(WaterRowTable.TABLE_NAME,cw,
                    waterRowWhereQuery,
                    new String[]{modelIdStr, String.valueOf(WaterRowTable.ROW_TYPE_COUNTER)});
        }




    }




    @Override
    public void initInDb(SQLiteDatabase db, long billId,Context c){
        ArrayList<String> data = new ArrayList<>();
        OptionsManager opts = new OptionsManager(c);
        opts.start();

        int counter;
        float rate;

        switch (mType){
            case TYPE_COLD:
                counter = opts.getColdWaterCounter();
                rate = opts.getColdWaterRate();
                break;
            case TYPE_WASTE:
                counter = opts.getWasteWaterCounter();
                rate = opts.getWasteWaterRate();
                break;
            default: //hot
                counter = opts.getHotWaterCounter();
                rate = opts.getHotWaterRate();
                break;
        }

        String counterStr = String.valueOf(counter);
        String rateStr = String.valueOf(rate);

        for(int i=0;i<BILL_SIZE;++i){
            if(i == INDEX_RATE) data.add(rateStr);
            else if (i == INDEX_COUNTER) data.add(counterStr);
            else data.add(DEFAULT_INPUT);
        }
        

        long billTypeId = createMainTableInDb(db,billId,data);
        initSegmentsInDb(db, billTypeId, BillManager.INDEX_COLD_WATER + mType);

        Log.d("_DBD","init "+ data.toString());


    }

    @Override
    public ArrayList<String> getMainTableFromDb(SQLiteDatabase db, long billId) {
        ArrayList<String> data = new ArrayList<>();
        for(int i=0;i<BILL_SIZE;++i){
            data.add("");
        }

        Cursor c = db.query(
                WaterTable.TABLE_NAME,
                new String[]{
                        WaterTable.COL_ID,
                        WaterTable.COL_COUNTER,
                        WaterTable.COL_CURR,
                        WaterTable.COL_PREV,
                        WaterTable.COL_DIFF,
                        WaterTable.COL_RATE,
                        WaterTable.COL_SUM
                },
                WaterTable.COL_BILL_ID + "=? AND " + WaterTable.COL_TYPE + "=?",
                new String[]{String.valueOf(billId), String.valueOf(mType)},
                null,
                null,
                null,
                "1"
        );

        Log.d("_DBD", "get " + billId);
        if(c.getCount() > 0){
            c.moveToFirst();
            Log.d("_DBD", "not null");
            long modelId = c.getLong(c.getColumnIndex(WaterTable.COL_ID));
            setLastModelId(modelId);

            data.set(INDEX_COUNTER, c.getString(c.getColumnIndex(WaterTable.COL_COUNTER)));
            data.set(INDEX_CURR, c.getString(c.getColumnIndex(WaterTable.COL_CURR)));
            data.set(INDEX_PREV, c.getString(c.getColumnIndex(WaterTable.COL_PREV)));
            data.set(INDEX_DIFF, c.getString(c.getColumnIndex(WaterTable.COL_DIFF)));
            data.set(INDEX_RATE, c.getString(c.getColumnIndex(WaterTable.COL_RATE)));
            data.set(INDEX_SUM, c.getString(c.getColumnIndex(WaterTable.COL_SUM)));

            c = db.query(
                WaterRowTable.TABLE_NAME,
                new String[]{
                        WaterRowTable.COL_ROW_TYPE,
                        WaterRowTable.COL_CALC,
                        WaterRowTable.COL_RECALC,
                        WaterRowTable.COL_SUB,
                        WaterRowTable.COL_COMP,
                        WaterRowTable.COL_TOTAL,
                },
                WaterRowTable.COL_WATER_BILL_ID + "=?",
                new String[]{String.valueOf(modelId)},
                null,
                null,
                null,
                "2"
            );

            if(c.getCount() > 0){
                Log.d("_DBD", "not null 2");
                while (c.moveToNext()){
                    int type = c.getInt(c.getColumnIndex(WaterRowTable.COL_ROW_TYPE));
                    if(type == WaterRowTable.ROW_TYPE_NORMAL) {
                        data.set(INDEX_CALC, c.getString(c.getColumnIndex(WaterRowTable.COL_CALC)));
                        data.set(INDEX_RECALC, c.getString(c.getColumnIndex(WaterRowTable.COL_RECALC)));
                        data.set(INDEX_SUB, c.getString(c.getColumnIndex(WaterRowTable.COL_SUB)));
                        data.set(INDEX_COMP, c.getString(c.getColumnIndex(WaterRowTable.COL_COMP)));
                        data.set(INDEX_TOTAL, c.getString(c.getColumnIndex(WaterRowTable.COL_TOTAL)));
                    } else {
                        data.set(INDEX_COUNTER_ROW_CALC, c.getString(c.getColumnIndex(WaterRowTable.COL_CALC)));
                        data.set(INDEX_COUNTER_ROW_RECALC, c.getString(c.getColumnIndex(WaterRowTable.COL_RECALC)));
                        data.set(INDEX_COUNTER_ROW_SUB, c.getString(c.getColumnIndex(WaterRowTable.COL_SUB)));
                        data.set(INDEX_COUNTER_ROW_COMP, c.getString(c.getColumnIndex(WaterRowTable.COL_COMP)));
                        data.set(INDEX_COUNTER_ROW_TOTAL, c.getString(c.getColumnIndex(WaterRowTable.COL_TOTAL)));
                    }
                }
            }
        }

        return  data;
    }



    @Override
    public void deleteFromDb(SQLiteDatabase db, long billId) {
    Log.d("_DBD","dfd1 " + billId);
        int modelId = getModelId(db, WaterTable.TABLE_NAME, WaterTable.COL_ID, WaterTable.COL_BILL_ID, String.valueOf(billId));
        Log.d("_DBD","dfd "+billId + " " + modelId);
        if(modelId != -1){
            String currIdStr = String.valueOf(modelId);
            deleteSegmentsFromDb(db,modelId, BillManager.INDEX_COLD_WATER + mType);
            db.delete(WaterRowTable.TABLE_NAME, WaterRowTable.COL_WATER_BILL_ID + "=?", new String[]{currIdStr});
            db.delete(WaterTable.TABLE_NAME, WaterTable.COL_ID + "=?", new String[]{currIdStr});
        }
    }

    @Override
    public void addCalcedSegment(SQLiteDatabase db, long billId, ArrayList<String> segment) {

        Cursor c = db.query(
                WaterTable.TABLE_NAME,
                new String[]{WaterTable.COL_ID, WaterTable.COL_SUM},
                WaterTable.COL_BILL_ID + "=? AND " + WaterTable.COL_TYPE + "=?",
                new String[]{String.valueOf(billId), String.valueOf(mType)},
                null,
                null,
                null,
                "1");

        if(c.getCount() > 0) {
            c.moveToFirst();

            long modelId = c.getLong(c.getColumnIndex(WaterTable.COL_ID));
            setLastModelId(modelId);

            String sum =  c.getString(c.getColumnIndex(WaterTable.COL_SUM));
            String totalNormalRow = "";
            String totalCounterRow = "";

            c = db.query(
                    WaterRowTable.TABLE_NAME,
                    new String[]{
                            WaterRowTable.COL_ROW_TYPE,
                            WaterRowTable.COL_TOTAL,
                    },
                    WaterRowTable.COL_WATER_BILL_ID + "=?",
                    new String[]{String.valueOf(modelId)},
                    null,
                    null,
                    null,
                    "2"
            );

            if(c.getCount() > 0){
                while(c.moveToNext()) {
                    int type = c.getInt(c.getColumnIndexOrThrow(WaterRowTable.COL_ROW_TYPE));
                    String total = c.getString(c.getColumnIndexOrThrow(WaterRowTable.COL_TOTAL));
                    if(type == WaterRowTable.ROW_TYPE_NORMAL) totalNormalRow = total;
                    else if(type == WaterRowTable.ROW_TYPE_COUNTER) totalCounterRow = total;
                }
            }

            ArrayList<String> newSegment = new ArrayList<>();
            for(String s : segment) newSegment.add(s);

            addCalcedItemsToSegment(newSegment,segment.get(2), new String[]{totalNormalRow, totalCounterRow, sum});
            Log.d("_DBD", "adding new segment:" + newSegment.toString() + " n:"+totalNormalRow + " c:"+totalCounterRow + " s:" + sum);
            addSegment(db, modelId, BillManager.INDEX_COLD_WATER + mType, newSegment);
        }
    }
}
