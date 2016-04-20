package tk.melnichuk.kommunalchik.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tk.melnichuk.kommunalchik.BillTypeFragments.BaseBillFragment;
import tk.melnichuk.kommunalchik.DataManagers.BillManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.CommunalTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentBillTypeTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentTable;
import tk.melnichuk.kommunalchik.Helpers.Utils;

/**
 * Created by al on 26.03.16.
 */
public class BaseModel {

    BigDecimal[] mMainTableData;
    long mLastModelId = -1;

    public static final String DEFAULT_INPUT = "0";

    public BaseModel(){
      //  mMainTableItems = new String[getNumMainTableItems()];
    }

    BigDecimal[] getBigDecimalArray(ArrayList<String> in){
        BigDecimal[] b = new BigDecimal[in.size()];
        for(int i=0;i<in.size();++i){
            b[i] = new BigDecimal(in.get(i));
        }
        return b;
    }

    ArrayList<String> getStringArrayList(BigDecimal[] in){
        ArrayList<String> s = new ArrayList<>();
        for(int i=0;i<in.length;++i){
            s.add(Utils.getZeroStrippedString(in[i]));
        }
        return s;
    }

    void calcMainTable(BigDecimal[] b){}


    //getExcelArrayFromDataManager
    void getExcelArray(){}

    public ArrayList<String> getCalcedMainTableData(ArrayList<String> mainTableData){
        if(mainTableData == null) return null;
        mMainTableData = getBigDecimalArray(mainTableData);
        calcMainTable(mMainTableData);
        return getStringArrayList(mMainTableData);
    }

    public ArrayList<ArrayList<String>> getCalcedSegmentsData(ArrayList<ArrayList<String>> segmentsData){
        if(mMainTableData == null) return null;
        if(segmentsData.size() > 0){
            BigDecimal[] lastColumnItems = getLastColumnItems();
            for(int i=0;i<segmentsData.size();++i){
                ArrayList<String> segment = segmentsData.get(i);
                BigDecimal segmentDecimalFraction = new BigDecimal(segment.get(0) );
                for(int j=0;j<lastColumnItems.length;++j) {
                    String val = Utils.getZeroStrippedString(segmentDecimalFraction.multiply(lastColumnItems[j]).setScale(2, RoundingMode.HALF_UP));
                    segment.set(j + 1, val);
                }
            }
        }

        return segmentsData;

    }

    BigDecimal[] getLastColumnItems(){
        return null;
    }

    public ArrayList<String> getMainTableFromDb(SQLiteDatabase db, long billId){

        return null;
    }

    public ArrayList<ArrayList<String>> getSegmentsFromDb(SQLiteDatabase db, long modelId, long modelType) {

        String segId = SegmentTable.TABLE_NAME + "." + SegmentTable.COL_ID;
        String segBillId = SegmentTable.TABLE_NAME + "."+ SegmentTable.COL_BILL_ID;
        String segBillType = SegmentTable.TABLE_NAME  + "." + SegmentTable.COL_BILL_TYPE;
        String segName = SegmentTable.TABLE_NAME + "." + SegmentTable.COL_NAME;
        String segUnit = SegmentTable.TABLE_NAME + "." + SegmentTable.COL_UNIT;
        String segVal = SegmentTable.TABLE_NAME + "." + SegmentTable.COL_VALUE;
        String segInput = SegmentRowTable.TABLE_NAME + "." + SegmentRowTable.COL_VALUE;
        String segOrder =  SegmentRowTable.TABLE_NAME + "." + SegmentRowTable.COL_POSITION;
        String segOtherId =  SegmentRowTable.TABLE_NAME + "." + SegmentRowTable.COL_SEGMENT_ID;

        Cursor c = db.query(
                SegmentTable.TABLE_NAME + " LEFT JOIN " + SegmentRowTable.TABLE_NAME + " ON " + segId + " = " + segOtherId,
                new String[]{segId, segName,segUnit,segVal, segInput, segOtherId },
                segBillId + "=? AND " + segBillType + "=?",
                new String[]{String.valueOf(modelId), String.valueOf(modelType)},
                null,
                null,
                segOtherId + " ASC,"+ segOrder + " ASC"
        );

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        int len = c.getCount();
        Log.d("_DBD","seg ln " + len + " id" + modelId + " type:"+modelType);
        if(len == 0) return data;

        c.moveToFirst();
        int id = c.getInt(c.getColumnIndex(segId));//c.getInt(c.getColumnIndex(segOtherId));
        int prevId = id;

        ArrayList<String> segment = new ArrayList<>();

        segment.add(c.getString(1));
        segment.add(c.getString(2));
        segment.add(c.getString(3));
        String input = c.getString(4);
        if(input ==  null || input.equals("null")) input = "0";
        segment.add(input);

        Log.d("_DBD", "S " + segment.toString() + " id " + id + " prevId " + prevId);

        if(len > 1) {
            while (c.moveToNext()) {

                id = c.getInt(c.getColumnIndex(segId));
                if (id != prevId) {
                    data.add(segment);
                    segment = new ArrayList<>();
                }
                Log.d("_DBD", "S1 " +segment.size());
                if (segment.isEmpty()) {
                    segment.add(c.getString(1));
                    segment.add(c.getString(2));
                    segment.add(c.getString(3));
                }

                input = c.getString(4);
                if(input ==  null || input.equals("null")) input = "0";
                segment.add(input);
                Log.d("_DBD", "S " + segment.toString() + " id " + id + " prevId " + prevId);
                prevId = id;

            }
        }

        data.add(segment);
        return data;
    }

    public void deleteFromDb(SQLiteDatabase db, long billId){


    }

    public void initInDb(SQLiteDatabase db, long billId, Context c){}

    void initSegmentsInDb(SQLiteDatabase db, long modelId, int modelType){

        Log.d("_DBD","seg init " + modelType);
        String segId = SegmentTable.TABLE_NAME + "." + SegmentTable.COL_ID;
        String segIdRel = SegmentBillTypeTable.TABLE_NAME +"."+SegmentBillTypeTable.COL_SEGMENT_ID;
        String segType = SegmentTable.TABLE_NAME + "." + SegmentTable.COL_TYPE;
        String segName =  SegmentTable.TABLE_NAME + "." + SegmentTable.COL_NAME;
        String segUnit = SegmentTable.TABLE_NAME + "." + SegmentTable.COL_UNIT;
        String segVal = SegmentTable.TABLE_NAME + "." + SegmentTable.COL_VALUE;
        String segBillType = SegmentBillTypeTable.TABLE_NAME + "." + SegmentBillTypeTable.COL_TYPE;

        Cursor c = db.query(
            SegmentTable.TABLE_NAME + " INNER JOIN " + SegmentBillTypeTable.TABLE_NAME + " ON " +
            segId + " = " + segIdRel,
            new String[]{ segType, segName, segUnit, segVal, segBillType},
                segType + "=? AND " + segBillType + "=?",
            new String[]{SegmentTable.TYPE_GLOBAL + "", modelType + ""},
            null,
            null,
            null
        );

        if(c.getCount() > 0) {

            while (c.moveToNext()) {
                ContentValues cw = new ContentValues();
                cw.put(SegmentTable.COL_BILL_ID, modelId + "");
                cw.put(SegmentTable.COL_BILL_TYPE, c.getString(c.getColumnIndexOrThrow(segBillType)) );
                cw.put(SegmentTable.COL_TYPE, SegmentTable.TYPE_LOCAL +"");
                cw.put(SegmentTable.COL_NAME, c.getString(c.getColumnIndexOrThrow(segName)));
                cw.put(SegmentTable.COL_UNIT, c.getInt(c.getColumnIndexOrThrow(segUnit)));
                cw.put(SegmentTable.COL_VALUE,c.getString(c.getColumnIndexOrThrow(segVal)));

                db.insertOrThrow(SegmentTable.TABLE_NAME, null, cw);
            }
        }
    }

    public void updateMainTableInDb(SQLiteDatabase db, ArrayList<String> data, long billId){}

    public void updateSegmentsInDb(SQLiteDatabase db, ArrayList<ArrayList<String>> data, long modelId, long modelType){
        if(data == null) return;
        deleteSegmentsFromDb(db, modelId, modelType);
        if(data.isEmpty()) return;

        for(int i=0;i<data.size();++i){
            addSegment(db,modelId,modelType,data.get(i));
        }
    }

    void addSegment(SQLiteDatabase db, long modelId,long modelType, ArrayList<String> segment){
        String name  = segment.get(0);
        String unit  = segment.get(1);
        String value = segment.get(2);

        ContentValues cw = new ContentValues();
        cw.put(SegmentTable.COL_BILL_ID, modelId);
        cw.put(SegmentTable.COL_BILL_TYPE, modelType);
        cw.put(SegmentTable.COL_NAME, name);
        cw.put(SegmentTable.COL_TYPE, SegmentTable.TYPE_LOCAL);
        cw.put(SegmentTable.COL_UNIT, unit);
        cw.put(SegmentTable.COL_VALUE, value);

        long segmentId = db.insertOrThrow(SegmentTable.TABLE_NAME,null, cw);
        int itStart = 3;
        int len = segment.size();

        if(len > itStart){
            for(int j=itStart;j<len;++j){
                cw.clear();

                cw.put(SegmentRowTable.COL_SEGMENT_ID, segmentId);
                cw.put(SegmentRowTable.COL_POSITION, j - itStart);
                cw.put(SegmentRowTable.COL_VALUE, segment.get(j));

                db.insertOrThrow(SegmentRowTable.TABLE_NAME, null, cw);

                Log.d("_DBD","seg insert "+ cw.toString() +  " model type:"+modelType );
            }
        }
    }

    void deleteSegmentsFromDb(SQLiteDatabase db, long modelId, long modelType) {
        Log.d("_DBD","seg delete "+ modelId +  " model type:"+modelType );
        db.execSQL("DELETE FROM " + SegmentRowTable.TABLE_NAME +
                        " WHERE " + SegmentRowTable.COL_SEGMENT_ID +
                        " IN (SELECT " + SegmentTable.COL_ID +
                        " FROM " + SegmentTable.TABLE_NAME +
                        " WHERE " + SegmentTable.COL_BILL_ID + " = " + modelId + " AND " + SegmentTable.COL_BILL_TYPE + "=" + modelType + " )"
        );
        db.delete(SegmentTable.TABLE_NAME,
                SegmentTable.COL_BILL_ID + "=? AND "+ SegmentTable.COL_TYPE + "=? AND " + SegmentTable.COL_BILL_TYPE + "=?",
                new String[]{
                    String.valueOf(modelId),
                    String.valueOf(SegmentTable.TYPE_LOCAL),
                    String.valueOf(modelType)
                });

    }

    int getModelId(SQLiteDatabase db, String tableName,String colId,String colBillId, String valBillId){
        Cursor c = db.query(
                tableName,
                new String[]{colId},
                colBillId + "=?",
                new String[]{valBillId},
                null,
                null,
                null,
                "1");
        if(c.getCount() > 0) {
            c.moveToFirst();
            return c.getInt(c.getColumnIndexOrThrow(colId));
        }
        return -1;

    }


    public long getLastModelId(){
        return mLastModelId;
    }

    public void setLastModelId(long modelId){
        mLastModelId = modelId;
    }

    public void addCalcedSegment(SQLiteDatabase db, long billId, ArrayList<String> segment){}

    void addCalcedItemsToSegment(ArrayList<String> newSegment, String multiplier ,String[] items){

        if(newSegment != null){
            Log.d("_SDB", newSegment.toString() + " m:"+multiplier + " ");
            BigDecimal decMultipier = Utils.getParsedBigDecimal(multiplier);
            for(String item : items){
                BigDecimal decItem =  Utils.getParsedBigDecimal(item);
                BigDecimal decResult = decMultipier.multiply(decItem);
                decResult = decResult.setScale(2, RoundingMode.HALF_UP);
                newSegment.add( Utils.getZeroStrippedString(decResult) );
            }
        }
    }

}
