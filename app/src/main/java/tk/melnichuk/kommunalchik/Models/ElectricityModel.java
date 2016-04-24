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
import tk.melnichuk.kommunalchik.DataManagers.Tables.ElectricityRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.ElectricityTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.WaterRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.WaterTable;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 30.03.16.
 */
public class ElectricityModel extends BaseModel {
    public static final int
        BILL_SIZE = 18,
        NUM_ELECTRICITY_ROWS = 4,

        INDEX_CURR = 0,
        INDEX_PREV = 1,
        INDEX_DIFF = 2,

        INDEX_DIFF_STEP_SUB = 3,
        INDEX_RATE_STEP_SUB = 4,
        INDEX_TOTAL_STEP_SUB = 5,

        INDEX_STEP_1 = 6,
        INDEX_DIFF_STEP_1 = 7,
        INDEX_RATE_STEP_1 = 8,
        INDEX_TOTAL_STEP_1 = 9,

        INDEX_STEP_2 = 10,
        INDEX_DIFF_STEP_2 = 11,
        INDEX_RATE_STEP_2 = 12,
        INDEX_TOTAL_STEP_2 = 13,


        INDEX_DIFF_STEP_3 = 14,
        INDEX_RATE_STEP_3 = 15,
        INDEX_TOTAL_STEP_3 = 16,

        INDEX_SUM = 17;

    @Override
    void calcMainTable(BigDecimal[] b) {
        BigDecimal curr = b[INDEX_CURR].setScale(4, RoundingMode.HALF_UP);//integer input type,no need for scale
        BigDecimal prev = b[INDEX_PREV].setScale(4, RoundingMode.HALF_UP);

        BigDecimal diff;
        if(curr.doubleValue()==0 && prev.doubleValue()==0)
            diff = b[INDEX_DIFF].setScale(0, RoundingMode.HALF_UP);
        else  diff = curr.subtract(prev);

        double diffDouble = diff.doubleValue();
        BigDecimal step1 = b[INDEX_STEP_1].setScale(0, RoundingMode.HALF_UP);
        BigDecimal step2 = b[INDEX_STEP_2].setScale(0, RoundingMode.HALF_UP);

        if(step1.subtract(step2).doubleValue() > 0) {
            BigDecimal temp = step1;
            step1 = step2;
            step2 = temp;
        }

        BigDecimal diff1 = b[INDEX_DIFF_STEP_SUB].setScale(0, RoundingMode.HALF_UP);
        BigDecimal rate1 = b[INDEX_RATE_STEP_SUB].setScale(4, RoundingMode.HALF_UP);
        BigDecimal total1;
        if(diff1.doubleValue()==0 || rate1.doubleValue()==0)
            total1 = b[INDEX_TOTAL_STEP_SUB].setScale(2, RoundingMode.HALF_UP);
        else total1 = diff1.multiply(rate1).setScale(2, RoundingMode.HALF_UP);

        BigDecimal diff2;
        if(step1.doubleValue() <= 0.0) diff2 = b[INDEX_DIFF_STEP_1].setScale(0, RoundingMode.HALF_UP);
        else if(diffDouble <= step1.doubleValue() ) diff2 = diff;
        else diff2 = step1;

            BigDecimal rate2 = b[INDEX_RATE_STEP_1].setScale(4, RoundingMode.HALF_UP);

        BigDecimal total2;
        if(diff2.doubleValue()==0 || rate2.doubleValue()==0)
            total2 = b[INDEX_TOTAL_STEP_1].setScale(2, RoundingMode.HALF_UP);
        else total2 = diff2.multiply(rate2).setScale(2, RoundingMode.HALF_UP);

        BigDecimal diff3;
        BigDecimal midStepDiff = diff.subtract(step1);



        if(midStepDiff.doubleValue() < 0) diff3 =  BigDecimal.ZERO;
        else if(midStepDiff.doubleValue() >= step2.doubleValue() ) diff3 = step2;
        else diff3 = midStepDiff;

        BigDecimal rate3 = b[INDEX_RATE_STEP_2].setScale(4, RoundingMode.HALF_UP);
        BigDecimal total3;
        if(diff3.doubleValue()==0 || rate3.doubleValue()==0)
            total3 = b[INDEX_TOTAL_STEP_2].setScale(2, RoundingMode.HALF_UP);
        else total3 = diff3.multiply(rate3).setScale(2, RoundingMode.HALF_UP);

        BigDecimal diff4;
        BigDecimal maxStepDiff = diff.subtract(step2).subtract(step1);
        //Log.d("_DIFF", maxStepDiff.toString());

        if(maxStepDiff.doubleValue() > 0) diff4 = maxStepDiff;
        else diff4 =  BigDecimal.ZERO;
        BigDecimal rate4 = b[INDEX_RATE_STEP_3].setScale(4, RoundingMode.HALF_UP);
        BigDecimal total4;
        if(diff4.doubleValue()==0 || rate4.doubleValue()==0)
            total4 = b[INDEX_TOTAL_STEP_3].setScale(2, RoundingMode.HALF_UP);
        else total4 = diff4.multiply(rate4).setScale(2, RoundingMode.HALF_UP);

        BigDecimal total5 = total2.subtract(total1).add(total3).add(total4);
        Log.d("_DIFF", total5.toString());


        b[INDEX_CURR] = curr;
        b[INDEX_PREV] = prev;
        b[INDEX_DIFF] = diff;

        b[INDEX_DIFF_STEP_SUB ] = diff1;
        b[INDEX_RATE_STEP_SUB] = rate1;
        b[INDEX_TOTAL_STEP_SUB] = total1;

        b[INDEX_STEP_1] = step1;
        b[INDEX_DIFF_STEP_1] = diff2;
        b[INDEX_RATE_STEP_1] = rate2;
        b[INDEX_TOTAL_STEP_1] = total2;

        b[INDEX_STEP_2 ] = step2;
        b[INDEX_DIFF_STEP_2] = diff3;
        b[INDEX_RATE_STEP_2] = rate3;
        b[INDEX_TOTAL_STEP_2] = total3;

        b[INDEX_DIFF_STEP_3] = diff4;
        b[INDEX_RATE_STEP_3] = rate4;
        b[INDEX_TOTAL_STEP_3] = total4;

        b[INDEX_SUM] = total5;

    }

    @Override
    BigDecimal[] getLastColumnItems() {
        return new BigDecimal[]{
                mMainTableData[INDEX_TOTAL_STEP_SUB],
                mMainTableData[INDEX_TOTAL_STEP_1],
                mMainTableData[INDEX_TOTAL_STEP_2],
                mMainTableData[INDEX_TOTAL_STEP_3],
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
            row4 = row3 + 1,
            row5 = row4 + 1,
            row6 = row5 + 1,
            row7 = row6 + 1;

        try {
            Log.d("_EDB", "mtd:" + mainTableData.toString());
            Log.d("_EDB", "sd:" + segmentsData.toString());

            String units = res.getString(R.string.e_units);
            String from = res.getString(R.string.e_from);
            String over = res.getString(R.string.e_over);
            String to = res.getString(R.string.e_to);
            String step1Val = mainTableData.get(INDEX_STEP_1);
            String step2Val = mainTableData.get(INDEX_STEP_2);

            String step1 = to + " " + step1Val + " " + units;
            String step2 = from + " " + step1Val + " " + to + " " + step2Val + " " + units;
            String step3 = over + " " + step2Val + " " + units;

            ExcelManager.addTitleToSheet(ws, 0, 4, rowsOffset, res.getString(R.string.electricity));

            ExcelManager.addLabelToSheet(ws, 0, row1, res.getString(R.string.e_current));
            ExcelManager.addLabelToSheet(ws, 1, row1, res.getString(R.string.e_previous));
            ExcelManager.addLabelToSheet(ws, 2, row1, res.getString(R.string.e_consumed));

            ExcelManager.addNumberToSheet(ws, 0, row2, mainTableData.get(INDEX_CURR));
            ExcelManager.addNumberToSheet(ws, 1, row2, mainTableData.get(INDEX_PREV));
            ExcelManager.addNumberToSheet(ws, 2, row2, mainTableData.get(INDEX_DIFF));
            ExcelManager.addLabelToSheet(ws, 3, row2, res.getString(R.string.e_rate));
            ExcelManager.addLabelToSheet(ws, 4, row2, res.getString(R.string.e_sum));

            addElectricityRow(ws, row3, res.getString(R.string.e_subsidy), INDEX_DIFF_STEP_SUB, mainTableData);
            addElectricityRow(ws, row4, step1, INDEX_DIFF_STEP_1, mainTableData);
            addElectricityRow(ws, row5, step2, INDEX_DIFF_STEP_2, mainTableData);
            addElectricityRow(ws, row6, step3, INDEX_DIFF_STEP_3, mainTableData);

            ExcelManager.addLabelToSheet(ws, 2, row7, res.getString(R.string.e_total_sum));
            ws.mergeCells(2, row7, 3, row7);
            ExcelManager.addNumberToSheet(ws, 4, row7, mainTableData.get(INDEX_SUM));

            ExcelManager.addSegmentsToExcelCellsArrayList(ws, res, segmentsData, row1, 5);

            /*
            ExcelManager.addLabelToSheet(ws, 0, row3, res.getString(R.string.e_subsidy));
            ws.mergeCells(0, row3, 1, row3);
            ExcelManager.addNumberToSheet(ws, 2, row3, mainTableData.get(INDEX_DIFF_STEP_SUB));
            ExcelManager.addNumberToSheet(ws, 3, row3, mainTableData.get(INDEX_RATE_STEP_SUB));
            ExcelManager.addNumberToSheet(ws, 4, row3, mainTableData.get(INDEX_TOTAL_STEP_SUB));


            ExcelManager.addLabelToSheet(ws, 0, row4, step1);
            ws.mergeCells(0, row4, 1, row4);
            ExcelManager.addNumberToSheet(ws, 2, row4, mainTableData.get(INDEX_DIFF_STEP_1));
            ExcelManager.addNumberToSheet(ws, 3, row4, mainTableData.get(INDEX_RATE_STEP_1));
            ExcelManager.addNumberToSheet(ws, 4, row4, mainTableData.get(INDEX_TOTAL_STEP_1));

            ExcelManager.addLabelToSheet(ws, 0, row5, step2);
            ws.mergeCells(0, row5, 1, row5);
            ExcelManager.addNumberToSheet(ws, 2, row5, mainTableData.get(INDEX_DIFF_STEP_2));
            ExcelManager.addNumberToSheet(ws, 3, row5, mainTableData.get(INDEX_RATE_STEP_2));
            ExcelManager.addNumberToSheet(ws, 4, row5, mainTableData.get(INDEX_TOTAL_STEP_2));

            ExcelManager.addLabelToSheet(ws, 0, row6, step3);
            ws.mergeCells(0, row6, 1, row6);
            ExcelManager.addNumberToSheet(ws, 2, row6, mainTableData.get(INDEX_DIFF_STEP_3));
            ExcelManager.addNumberToSheet(ws, 3, row6, mainTableData.get(INDEX_RATE_STEP_3));
            ExcelManager.addNumberToSheet(ws, 4, row6, mainTableData.get(INDEX_TOTAL_STEP_3));
            */


        } catch (WriteException e){
            Log.d("_EDB", "e gas:"+e.toString());
        }

        return row7 - rowsOffset;
    }

    void addElectricityRow(WritableSheet ws, int row, String stepStr, int indexDiff, ArrayList<String> mainTableData){

        try {
            ExcelManager.addLabelToSheet(ws, 0, row, stepStr);
            ws.mergeCells(0, row, 1, row);
            ExcelManager.addNumberToSheet(ws, 2, row, mainTableData.get(indexDiff));
            ExcelManager.addNumberToSheet(ws, 3, row, mainTableData.get(indexDiff + 1));
            ExcelManager.addNumberToSheet(ws, 4, row, mainTableData.get(indexDiff + 2));

        } catch (WriteException e) {
            e.printStackTrace();
        }

    }


    long createMainTableInDb(SQLiteDatabase db, long billId, ArrayList<String> data){
        ContentValues cw = new ContentValues();
        cw.put(ElectricityTable.COL_BILL_ID, billId );
        cw.put(ElectricityTable.COL_CURR, data.get(INDEX_CURR));
        cw.put(ElectricityTable.COL_PREV, data.get(INDEX_PREV));
        cw.put(ElectricityTable.COL_DIFF, data.get(INDEX_DIFF));
        cw.put(ElectricityTable.COL_SUM, data.get(INDEX_SUM));


        long id = db.insertOrThrow(ElectricityTable.TABLE_NAME, null, cw);
        setLastModelId(id);
        //cw.clear();

        int[] diffIndices = new int[]{INDEX_DIFF_STEP_SUB, INDEX_DIFF_STEP_1, INDEX_DIFF_STEP_2, INDEX_DIFF_STEP_3};
        for(int i = 0; i < NUM_ELECTRICITY_ROWS; ++i){
            cw.clear();
            cw.put(ElectricityRowTable.COL_ELECTRICITY_BILL_ID, id);
            cw.put(ElectricityRowTable.COL_TYPE, i);
            String step = (i == ElectricityRowTable.STEP_SUBSIDY || i == ElectricityRowTable.STEP_3 ) ? DEFAULT_INPUT : data.get(diffIndices[i] - 1);
            cw.put(ElectricityRowTable.COL_STEP, step );
            cw.put(ElectricityRowTable.COL_DIFF, data.get(diffIndices[i]));
            cw.put(ElectricityRowTable.COL_RATE,  data.get(diffIndices[i] + 1));
            cw.put(ElectricityRowTable.COL_TOTAL, data.get(diffIndices[i] + 2));

            db.insertOrThrow(ElectricityRowTable.TABLE_NAME, null, cw);
        }

        Log.d("_DBD", "create table " + billId);
        return id;
    }



    public void updateMainTableInDb(SQLiteDatabase db, ArrayList<String>data, long billId){
        if(data == null || data.isEmpty()) return;

        long modelId = getModelId(db, ElectricityTable.TABLE_NAME, ElectricityTable.COL_ID, ElectricityTable.COL_BILL_ID, String.valueOf(billId));
        Log.d("_DBD", "upd segs not null bI:"+billId+" mI:"+modelId + " d:"+data.toString());

        setLastModelId(modelId);

        if(modelId != -1) {
            String modelIdStr = String.valueOf(modelId);
            ContentValues cw = new ContentValues();
            cw.put(ElectricityTable.COL_BILL_ID, billId );
            cw.put(ElectricityTable.COL_CURR, data.get(INDEX_CURR));
            cw.put(ElectricityTable.COL_PREV, data.get(INDEX_PREV));
            cw.put(ElectricityTable.COL_DIFF, data.get(INDEX_DIFF));
            cw.put(ElectricityTable.COL_SUM, data.get(INDEX_SUM));

            db.update(ElectricityTable.TABLE_NAME, cw, ElectricityTable.COL_ID + "=?", new String[]{modelIdStr});

            int[] diffIndices = new int[]{INDEX_DIFF_STEP_SUB, INDEX_DIFF_STEP_1, INDEX_DIFF_STEP_2, INDEX_DIFF_STEP_3};
            String electricityRowWhereString = ElectricityRowTable.COL_ELECTRICITY_BILL_ID + "=? AND " + ElectricityRowTable.COL_TYPE + "=?";

            for(int i = 0; i < NUM_ELECTRICITY_ROWS; ++i){
                cw.clear();
                cw.put(ElectricityRowTable.COL_ELECTRICITY_BILL_ID, modelIdStr);
                String step = (i == ElectricityRowTable.STEP_SUBSIDY || i == ElectricityRowTable.STEP_3 ) ? DEFAULT_INPUT : data.get(diffIndices[i] - 1);
                cw.put(ElectricityRowTable.COL_STEP, step );
                cw.put(ElectricityRowTable.COL_DIFF, data.get(diffIndices[i]));
                cw.put(ElectricityRowTable.COL_RATE, data.get(diffIndices[i] + 1));
                cw.put(ElectricityRowTable.COL_TOTAL, data.get(diffIndices[i] + 2));

                db.update(ElectricityRowTable.TABLE_NAME, cw,  electricityRowWhereString, new String[]{modelIdStr, String.valueOf(i)});
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
                case INDEX_DIFF_STEP_SUB:
                    data.add( String.valueOf(opts.getElectricityStepSubsidy()) );
                    data.add( String.valueOf(opts.getElectricityRateSubsidy()) );
                    i+=1;
                    continue;
                case INDEX_STEP_1:
                    data.add( String.valueOf(opts.getElectricityStep1()) );
                    data.add(DEFAULT_INPUT);
                    data.add( String.valueOf(opts.getElectricityRate1()) );
                    i+=2;
                    continue;
                case INDEX_STEP_2:
                    data.add( String.valueOf(opts.getElectricityStep2()) );
                    data.add(DEFAULT_INPUT);
                    data.add( String.valueOf(opts.getElectricityRate2()) );
                    i+=2;
                    continue;
                case INDEX_RATE_STEP_3:
                    data.add( String.valueOf(opts.getElectricityRate3()) );
                default:
                    data.add(DEFAULT_INPUT);
            }
        }


        long billTypeId = createMainTableInDb(db, billId, data);
        initSegmentsInDb(db, billTypeId, BillManager.INDEX_ELECTRICITY);

        Log.d("_DBD", "init " + data.toString());
    }



    @Override
    public ArrayList<String> getMainTableFromDb(SQLiteDatabase db, long billId) {
        ArrayList<String> data = new ArrayList<>();
        for(int i=0;i<BILL_SIZE;++i){
            data.add("");
        }

        Cursor c = db.query(
                ElectricityTable.TABLE_NAME,
                new String[]{
                        ElectricityTable.COL_ID,
                        ElectricityTable.COL_BILL_ID,
                        ElectricityTable.COL_CURR,
                        ElectricityTable.COL_PREV,
                        ElectricityTable.COL_DIFF,
                        ElectricityTable.COL_SUM
                },
                ElectricityTable.COL_BILL_ID + "=?",
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
            long modelId = c.getLong(c.getColumnIndex(ElectricityTable.COL_ID));
            setLastModelId(modelId);

            data.set(INDEX_CURR, c.getString(c.getColumnIndex(ElectricityTable.COL_CURR)));
            data.set(INDEX_PREV, c.getString(c.getColumnIndex(ElectricityTable.COL_PREV)));
            data.set(INDEX_DIFF, c.getString(c.getColumnIndex(ElectricityTable.COL_DIFF)));
            data.set(INDEX_SUM, c.getString(c.getColumnIndex(ElectricityTable.COL_SUM)));

            c = db.query(
                    ElectricityRowTable.TABLE_NAME,
                    new String[]{
                            ElectricityRowTable.COL_TYPE,
                            ElectricityRowTable.COL_STEP,
                            ElectricityRowTable.COL_DIFF,
                            ElectricityRowTable.COL_RATE,
                            ElectricityRowTable.COL_TOTAL
                    },
                    ElectricityRowTable.COL_ELECTRICITY_BILL_ID + "=?",
                    new String[]{String.valueOf(modelId)},
                    null,
                    null,
                    ElectricityRowTable.COL_TYPE + " ASC",
                    String.valueOf(NUM_ELECTRICITY_ROWS)
            );

            if(c.getCount() > 0){
                while (c.moveToNext()){

                    int type = c.getInt(c.getColumnIndex(ElectricityRowTable.COL_TYPE));
                    switch (type){
                        case ElectricityRowTable.STEP_SUBSIDY:
                            data.set(INDEX_DIFF_STEP_SUB, c.getString(c.getColumnIndex(ElectricityRowTable.COL_DIFF)));
                            data.set(INDEX_RATE_STEP_SUB, c.getString(c.getColumnIndex(ElectricityRowTable.COL_RATE)));
                            data.set(INDEX_TOTAL_STEP_SUB, c.getString(c.getColumnIndex(ElectricityRowTable.COL_TOTAL)));
                            break;
                        case ElectricityRowTable.STEP_1:
                            data.set(INDEX_STEP_1, c.getString(c.getColumnIndex(ElectricityRowTable.COL_STEP)));
                            data.set(INDEX_DIFF_STEP_1, c.getString(c.getColumnIndex(ElectricityRowTable.COL_DIFF)));
                            data.set(INDEX_RATE_STEP_1, c.getString(c.getColumnIndex(ElectricityRowTable.COL_RATE)));
                            data.set(INDEX_TOTAL_STEP_1, c.getString(c.getColumnIndex(ElectricityRowTable.COL_TOTAL)));
                            break;
                        case ElectricityRowTable.STEP_2:
                            data.set(INDEX_STEP_2, c.getString(c.getColumnIndex(ElectricityRowTable.COL_STEP)));
                            data.set(INDEX_DIFF_STEP_2, c.getString(c.getColumnIndex(ElectricityRowTable.COL_DIFF)));
                            data.set(INDEX_RATE_STEP_2, c.getString(c.getColumnIndex(ElectricityRowTable.COL_RATE)));
                            data.set(INDEX_TOTAL_STEP_2, c.getString(c.getColumnIndex(ElectricityRowTable.COL_TOTAL)));
                            break;
                        case ElectricityRowTable.STEP_3:
                            data.set(INDEX_DIFF_STEP_3, c.getString(c.getColumnIndex(ElectricityRowTable.COL_DIFF)));
                            data.set(INDEX_RATE_STEP_3, c.getString(c.getColumnIndex(ElectricityRowTable.COL_RATE)));
                            data.set(INDEX_TOTAL_STEP_3, c.getString(c.getColumnIndex(ElectricityRowTable.COL_TOTAL)));
                            break;
                    }
                }
            }
        }

        return  data;
    }


    @Override
    public void deleteFromDb(SQLiteDatabase db, long billId) {
        int modelId = getModelId(db, ElectricityTable.TABLE_NAME, ElectricityTable.COL_ID, ElectricityTable.COL_BILL_ID, String.valueOf(billId));
        Log.d("_DBD","dfd "+billId + " " + modelId);
        if(modelId != -1){
            String currIdStr = String.valueOf(modelId);
            deleteSegmentsFromDb(db, modelId, BillManager.INDEX_ELECTRICITY);
            db.delete(ElectricityRowTable.TABLE_NAME, ElectricityRowTable.COL_ELECTRICITY_BILL_ID + "=?", new String[]{currIdStr});
            db.delete(ElectricityTable.TABLE_NAME, ElectricityTable.COL_ID + "=?", new String[]{currIdStr});
        }
    }


    @Override
    public void addCalcedSegment(SQLiteDatabase db, long billId, ArrayList<String> segment) {

        Cursor c = db.query(
                ElectricityTable.TABLE_NAME,
                new String[]{ElectricityTable.COL_ID, ElectricityTable.COL_SUM},
                ElectricityTable.COL_BILL_ID + "=?",
                new String[]{String.valueOf(billId)},
                null,
                null,
                null,
                "1");

        if(c.getCount() > 0) {
            c.moveToFirst();

            long modelId = c.getLong(c.getColumnIndex(ElectricityTable.COL_ID));
            setLastModelId(modelId);

            String sum =  c.getString(c.getColumnIndex(ElectricityTable.COL_SUM));
            String[] items = new String[NUM_ELECTRICITY_ROWS + 1]; // all rows + sum row
            items[NUM_ELECTRICITY_ROWS] = sum;

            c = db.query(
                    ElectricityRowTable.TABLE_NAME,
                    new String[]{ElectricityRowTable.COL_TOTAL},
                    ElectricityRowTable.COL_ELECTRICITY_BILL_ID + "=?",
                    new String[]{String.valueOf(modelId)},
                    null,
                    null,
                    ElectricityRowTable.COL_TYPE + " ASC",
                    String.valueOf(NUM_ELECTRICITY_ROWS)
            );

            if(c.getCount() > 0){
                int i = 0;
                while(c.moveToNext()) {
                    items[i] = c.getString(c.getColumnIndexOrThrow(ElectricityRowTable.COL_TOTAL));
                    ++i;
                }
            }

            ArrayList<String> newSegment = new ArrayList<>();
            for(String s : segment) newSegment.add(s);

            addCalcedItemsToSegment(newSegment, segment.get(2), items);
            Log.d("_DBD", "adding new segment:" + newSegment.toString() + " s:" + sum);
            addSegment(db, modelId,BillManager.INDEX_ELECTRICITY, newSegment);
        }
    }


}
