package tk.melnichuk.kommunalchik.DataManagers;

import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import tk.melnichuk.kommunalchik.Helpers.Utils;

/**
 * Created by al on 21.04.16.
 */
public class ExcelManager {
    public static final String EXCEL_SUFFIX = ".xls";
    public static final String FOLDER_NAME = "Kommunalchik";

    public void init(){

        File f = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    private WritableWorkbook mWritableWorkbook = null;

    public  ExcelManager(){}
    public WritableSheet begin(String fileName){
        mWritableWorkbook = getNewWritableWorkBookInstance(fileName);
        return getNewWritableSheetInstance(mWritableWorkbook, fileName);
    }

    public void end(){
        if(mWritableWorkbook != null){

            try {
                mWritableWorkbook.write();
                mWritableWorkbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }


    public static WritableWorkbook getNewWritableWorkBookInstance(String fileName){
        String dir  = Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME;
        String name = fileName + EXCEL_SUFFIX;
        File file = new File(dir,name);
        WritableWorkbook ww = null;
        try {
            ww = Workbook.createWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
        }

        return ww;
    }

    public static WritableSheet getNewWritableSheetInstance(WritableWorkbook ww,String fileName){
        return  ww.createSheet(fileName, 0);
    }

    public static Number addNumberToSheet(WritableSheet ws, int cell, int row, String value){

        Number out = getNumber(cell,row,value);
        WritableCellFormat formatNum = new WritableCellFormat();
        try {
            ws.addCell(out);
            formatNum.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
            //formatNum.setBackground(color);
            out.setCellFormat(formatNum);
        } catch (WriteException e) {
            e.printStackTrace();
        }

        return out;

    }


    public static Label addLabelToSheet(WritableSheet ws, int cell, int row, String value){

        Label out = getLabel(cell, row, value);
        WritableCellFormat formatNum = new WritableCellFormat();
        try {
            ws.addCell(out);
            formatNum.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
            //formatNum.setBackground(color);
            out.setCellFormat(formatNum);
        } catch (WriteException e) {
            e.printStackTrace();
        }

        return out;
    }


    public static Number getNumber(int cell, int row, String value){
        double num;
        try{
            num = Double.parseDouble(value);

        } catch (NumberFormatException e){
            num = 0;
            Log.d("_EDB", "numerr:" + e.toString());
        }
        return new Number(cell,row,num);
    }


    public static Label getLabel(int cell, int row, String value){
        return new Label(cell,row,value);
    }


    public static void addSegmentsToExcelCellsArrayList(WritableSheet ws,Resources res, ArrayList<ArrayList<String>> segments, int rowStart, int cellStart){
        if(segments == null || segments.isEmpty()) return;

        for (int i = 0; i < segments.size(); ++i) {
            ArrayList<String> segment = segments.get(i);
            int currentCell = cellStart + i;

            addLabelToSheet(ws, currentCell, rowStart, segment.get(0));
            String unitType = Utils.getUnitValue(res, Integer.valueOf(segment.get(1)));
            addLabelToSheet(ws, currentCell, rowStart + 1, segment.get(2) + " [" + unitType + "]");
            int inputFieldsStartIndex = 3, rowOffset = 2;
            if (inputFieldsStartIndex < segment.size())
                for (int j = inputFieldsStartIndex; j < segment.size(); ++j) {
                    addNumberToSheet(ws,currentCell, rowStart + rowOffset,segment.get(j));
                    ++rowOffset;
                }

        }
    }


    public static void addTitleToSheet(WritableSheet ws, int cellStart, int cellEnd, int row, String label){
        try {
            Label title = addLabelToSheet(ws, cellStart, row, label);
            WritableFont cellFont = new WritableFont(WritableFont.TIMES, 14);
            cellFont.setBoldStyle(WritableFont.BOLD);
            WritableCellFormat format = new WritableCellFormat(cellFont);
            format.setAlignment(Alignment.CENTRE);
            title.setCellFormat(format);
            ws.mergeCells(cellStart, row, cellEnd, row);
        } catch (WriteException e){
            e.printStackTrace();
        }

    }



}
