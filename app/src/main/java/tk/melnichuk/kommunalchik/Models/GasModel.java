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
import tk.melnichuk.kommunalchik.DataManagers.ExcelCell;
import tk.melnichuk.kommunalchik.DataManagers.ExcelManager;
import tk.melnichuk.kommunalchik.DataManagers.OptionsManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.CommunalTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.GasTable;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 30.03.16.
 */
public class GasModel extends BaseModel {
    public static final int
        BILL_SIZE = 12,

        CALC_BY_RATE = 0,
        CALC_BY_COUNTER = 1,

        INDEX_CALC_BY = 2,
        INDEX_AREA = 0,
        INDEX_REGISTERED = 1,
        INDEX_CURR = 3,
        INDEX_PREV = 4,
        INDEX_DIFF = 5,
        INDEX_RATE = 6,
        INDEX_CALC = 7,
        INDEX_SUB  = 8,
        INDEX_ADDPAY = 10,
        INDEX_TOTAL = 9,
        INDEX_SUM = 11;

    @Override
    void calcMainTable(BigDecimal[] b) {

        boolean counterExists = b[INDEX_CALC_BY].floatValue() == (float) CALC_BY_COUNTER;

        BigDecimal area = b[INDEX_AREA].setScale(2, RoundingMode.HALF_UP);
        BigDecimal registered = b[INDEX_REGISTERED].setScale(2, RoundingMode.HALF_UP);
        BigDecimal curr = b[INDEX_CURR].setScale(2, RoundingMode.HALF_UP);
        BigDecimal prev = b[INDEX_PREV].setScale(2, RoundingMode.HALF_UP);
        BigDecimal diff = b[INDEX_DIFF].setScale(2, RoundingMode.HALF_UP);
        BigDecimal rate = b[INDEX_RATE].setScale(3, RoundingMode.HALF_UP);
        BigDecimal calc;

        if(counterExists){
            if(!(curr.doubleValue()==0 && prev.doubleValue()==0) )
                diff = curr.subtract(prev);

            if(diff.doubleValue()==0 || rate.doubleValue()==0)
                calc =  b[INDEX_CALC].setScale(2, RoundingMode.HALF_UP);
            else calc = diff.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        } else {
            if(registered.doubleValue()==0 || rate.doubleValue()==0)
                calc =  b[INDEX_CALC].setScale(2, RoundingMode.HALF_UP);
            else calc = registered.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal sub = b[INDEX_SUB].setScale(2, RoundingMode.HALF_UP);
        BigDecimal total1;

        if (calc.doubleValue() == 0)
            total1 = b[INDEX_TOTAL].subtract(sub);
        else
            total1 = calc.subtract(sub);

        BigDecimal addpay = b[INDEX_ADDPAY].setScale(2, RoundingMode.HALF_UP);
        BigDecimal total2;
        if(addpay.doubleValue()==0 && total1.doubleValue()==0)
            total2 = b[INDEX_SUM].setScale(2, RoundingMode.HALF_UP);
        else
            total2 = total1.add(addpay);

        b[INDEX_AREA] = area;
        b[INDEX_REGISTERED] = registered;
        b[INDEX_CURR] = curr;
        b[INDEX_PREV] = prev;
        b[INDEX_DIFF] = diff;
        b[INDEX_CALC] = calc;
        b[INDEX_SUB] = sub;
        b[INDEX_RATE] = rate;
        b[INDEX_ADDPAY] = addpay;
        b[INDEX_TOTAL] = total1;
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


    @Override
    public int addCellsExcelTable(int rowsOffset, WritableSheet ws, Resources res,
                                  ArrayList<String> mainTableData, ArrayList<ArrayList<String>> segmentsData) {

        if(mainTableData ==  null || mainTableData.isEmpty()) return 0;
        int row1 = rowsOffset + 2,
            row2 = row1 + 1,
            row3 = row2 + 1,
            row4 = row3 + 1,
            row5 = row4 + 1;
        try {

            Log.d("_EDB", "mtd:" + mainTableData.toString());
            Log.d("_EDB", "sd:" + segmentsData.toString());

            int calcBy = Integer.parseInt(mainTableData.get(INDEX_CALC_BY));
            int offset = 0;
            String calcByString = res.getString(R.string.gas_calc_by);
            calcByString += " " + res.getStringArray(R.array.counter_indicator_gas)[calcBy];
            if (calcBy == CALC_BY_COUNTER) {
                offset = 3;


                ExcelManager.addLabelToSheet(ws, 2, row1, res.getString(R.string.data));
                ws.mergeCells(2, row1, 4, row1);

                ExcelManager.addLabelToSheet(ws, 2, row2, res.getString(R.string.current));
                ExcelManager.addLabelToSheet(ws, 3, row2, res.getString(R.string.previous));
                ExcelManager.addLabelToSheet(ws, 4, row2, res.getString(R.string.difference));

                ExcelManager.addNumberToSheet(ws, 2, row3, mainTableData.get(INDEX_CURR));
                ExcelManager.addNumberToSheet(ws, 3, row3, mainTableData.get(INDEX_PREV));
                ExcelManager.addNumberToSheet(ws, 4, row3, mainTableData.get(INDEX_DIFF));

            }

            ExcelManager.addTitleToSheet(ws, 0, 5, rowsOffset, res.getString(R.string.gas));

            ExcelManager.addLabelToSheet(ws, 0, row1, res.getString(R.string.area));
            ExcelManager.addLabelToSheet(ws, 1, row1, res.getString(R.string.registered));

            ExcelManager.addLabelToSheet(ws, 0, row2, mainTableData.get(INDEX_AREA));
            ExcelManager.addNumberToSheet(ws, 1, row2, mainTableData.get(INDEX_REGISTERED));

            ExcelManager.addLabelToSheet(ws, 0, row3, calcByString);
            ws.mergeCells(0, row3, 1, row3);

            ExcelManager.addLabelToSheet(ws, 2 + offset, row2, res.getString(R.string.rate));
            ExcelManager.addLabelToSheet(ws, 3 + offset, row2, res.getString(R.string.calculated));
            ExcelManager.addLabelToSheet(ws, 4 + offset, row2, res.getString(R.string.subsidy));
            ExcelManager.addLabelToSheet(ws, 5 + offset, row2, res.getString(R.string.sum));

            ExcelManager.addNumberToSheet(ws, 2 + offset, row3, mainTableData.get(INDEX_RATE));
            ExcelManager.addNumberToSheet(ws, 3 + offset, row3, mainTableData.get(INDEX_CALC));
            ExcelManager.addNumberToSheet(ws, 4 + offset, row3, mainTableData.get(INDEX_SUB));
            ExcelManager.addNumberToSheet(ws, 5 + offset, row3, mainTableData.get(INDEX_TOTAL));


            ExcelManager.addLabelToSheet(ws, 4 + offset, row4, res.getString(R.string.additional_payment));
            ExcelManager.addNumberToSheet(ws,5 + offset, row4, mainTableData.get(INDEX_ADDPAY));

            ExcelManager.addLabelToSheet(ws, 4 + offset, row5, res.getString(R.string.table_group_sum));
            ExcelManager.addNumberToSheet(ws,5 + offset, row5, mainTableData.get(INDEX_SUM));

            ExcelManager.addSegmentsToExcelCellsArrayList(ws, res, segmentsData, row1, 6 + offset);
        } catch (WriteException e){
            Log.d("_EDB", "e gas:"+e.toString());
        }



        return row5 - rowsOffset;
    }


    long createMainTableInDb(SQLiteDatabase db, long billId, ArrayList<String> data){

        ContentValues cw = new ContentValues();
        cw.put(GasTable.COL_BILL_ID, billId );
        cw.put(GasTable.COL_CALC_BY, data.get(INDEX_CALC_BY));
        cw.put(GasTable.COL_AREA, data.get(INDEX_AREA));
        cw.put(GasTable.COL_REGISTERED, data.get(INDEX_REGISTERED));
        cw.put(GasTable.COL_CURR, data.get(INDEX_CURR));
        cw.put(GasTable.COL_PREV, data.get(INDEX_PREV));
        cw.put(GasTable.COL_DIFF, data.get(INDEX_DIFF));
        cw.put(GasTable.COL_CALC, data.get(INDEX_CALC));
        cw.put(GasTable.COL_SUB, data.get(INDEX_SUB));
        cw.put(GasTable.COL_RATE, data.get(INDEX_RATE));
        cw.put(GasTable.COL_ADDPAY, data.get(INDEX_ADDPAY));
        cw.put(GasTable.COL_TOTAL, data.get(INDEX_TOTAL));
        cw.put(GasTable.COL_SUM, data.get(INDEX_SUM));

        long modelId = db.insertOrThrow(GasTable.TABLE_NAME, null, cw);
        setLastModelId(modelId);
        return modelId;
    }


    public void updateMainTableInDb(SQLiteDatabase db, ArrayList<String>data, long billId){
        if(data == null || data.isEmpty()) return;

        long modelId = getModelId(db, GasTable.TABLE_NAME, GasTable.COL_ID, GasTable.COL_BILL_ID, String.valueOf(billId));

        if(modelId != -1){
            Log.d("_DBD", "upd segs not null bI:" + billId + " mI:" + modelId);

            setLastModelId(modelId);

            ContentValues cw = new ContentValues();
            cw.put(GasTable.COL_BILL_ID, billId );
            cw.put(GasTable.COL_CALC_BY, data.get(INDEX_CALC_BY));
            cw.put(GasTable.COL_AREA, data.get(INDEX_AREA));
            cw.put(GasTable.COL_REGISTERED, data.get(INDEX_REGISTERED));
            cw.put(GasTable.COL_CURR, data.get(INDEX_CURR));
            cw.put(GasTable.COL_PREV, data.get(INDEX_PREV));
            cw.put(GasTable.COL_DIFF, data.get(INDEX_DIFF));
            cw.put(GasTable.COL_CALC, data.get(INDEX_CALC));
            cw.put(GasTable.COL_SUB, data.get(INDEX_SUB));
            cw.put(GasTable.COL_RATE, data.get(INDEX_RATE));
            cw.put(GasTable.COL_ADDPAY, data.get(INDEX_ADDPAY));
            cw.put(GasTable.COL_TOTAL, data.get(INDEX_TOTAL));
            cw.put(GasTable.COL_SUM, data.get(INDEX_SUM));


            db.update(GasTable.TABLE_NAME, cw, GasTable.COL_ID + "=?", new String[]{String.valueOf(modelId)});
        }
    }

    @Override
    public void initInDb(SQLiteDatabase db, long billId,Context c){
        ArrayList<String> data = new ArrayList<>();

        OptionsManager opts = new OptionsManager(c);
        opts.start();

        for(int i=0;i<BILL_SIZE;++i){
            switch (i) {
                case INDEX_CALC_BY:
                    data.add(opts.getGasRateType() + "");
                    break;
                case INDEX_RATE:
                    data.add(opts.getGasRate() + "");
                    break;
                default:
                data.add(DEFAULT_INPUT);
            }
        }

        long billTypeId = createMainTableInDb(db, billId, data);
        initSegmentsInDb(db, billTypeId, BillManager.INDEX_GAS);
    }

    @Override
    public ArrayList<String> getMainTableFromDb(SQLiteDatabase db, long billId) {

        ArrayList<String> data = new ArrayList<>();
        for(int i=0;i<BILL_SIZE;++i){
            data.add("");
        }

        Cursor c = db.query(
                GasTable.TABLE_NAME,
                null,
                GasTable.COL_BILL_ID + "=?",
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
            long modelId = c.getLong(c.getColumnIndex(GasTable.COL_ID));
            setLastModelId(modelId);

            data.set(INDEX_CALC_BY, c.getString(c.getColumnIndex(GasTable.COL_CALC_BY)));
            data.set(INDEX_AREA, c.getString(c.getColumnIndex(GasTable.COL_AREA)));
            data.set(INDEX_REGISTERED, c.getString(c.getColumnIndex(GasTable.COL_REGISTERED)));
            data.set(INDEX_CURR, c.getString(c.getColumnIndex(GasTable.COL_CURR)));
            data.set(INDEX_PREV, c.getString(c.getColumnIndex(GasTable.COL_PREV)));
            data.set(INDEX_DIFF, c.getString(c.getColumnIndex(GasTable.COL_DIFF)));
            data.set(INDEX_RATE, c.getString(c.getColumnIndex(GasTable.COL_RATE)));
            data.set(INDEX_CALC, c.getString(c.getColumnIndex(GasTable.COL_CALC)));
            data.set(INDEX_SUB, c.getString(c.getColumnIndex(GasTable.COL_SUB)));
            data.set(INDEX_ADDPAY, c.getString(c.getColumnIndex(GasTable.COL_ADDPAY)));
            data.set(INDEX_TOTAL, c.getString(c.getColumnIndex(GasTable.COL_TOTAL)));
            data.set(INDEX_SUM, c.getString(c.getColumnIndex(GasTable.COL_SUM)));

        }

        return  data;
    }

    @Override
    public void deleteFromDb(SQLiteDatabase db, long billId) {

        int modelId = getModelId(db, GasTable.TABLE_NAME, GasTable.COL_ID, GasTable.COL_BILL_ID, String.valueOf(billId));

        if(modelId != -1){
            deleteSegmentsFromDb(db, modelId, BillManager.INDEX_GAS);
            db.delete(GasTable.TABLE_NAME, GasTable.COL_ID + "=?", new String[]{String.valueOf(modelId)});
        }
    }


    @Override
    public void addCalcedSegment(SQLiteDatabase db, long billId, ArrayList<String> segment) {

        Cursor c = db.query(
                GasTable.TABLE_NAME,
                new String[]{GasTable.COL_ID, GasTable.COL_TOTAL, GasTable.COL_ADDPAY, GasTable.COL_SUM},
                GasTable.COL_BILL_ID + "=?",
                new String[]{String.valueOf(billId)},
                null,
                null,
                null,
                "1");

        if(c.getCount() > 0) {
            c.moveToFirst();

            long modelId = c.getLong(c.getColumnIndex(GasTable.COL_ID));
            setLastModelId(modelId);

            String total =  c.getString(c.getColumnIndex(GasTable.COL_TOTAL));
            String addpay = c.getString(c.getColumnIndex(GasTable.COL_ADDPAY));
            String sum = c.getString(c.getColumnIndex(GasTable.COL_SUM));

            ArrayList<String> newSegment = new ArrayList<>();
            for(String s : segment) newSegment.add(s);

            addCalcedItemsToSegment(newSegment, segment.get(2), new String[]{total, addpay, sum});
            addSegment(db, modelId, BillManager.INDEX_GAS, newSegment);
        }
    }
}
