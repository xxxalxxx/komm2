package tk.melnichuk.kommunalchik.DataManagers;

/**
 * Created by al on 21.04.16.
 */
public class ExcelCell {
    int mRow, mCell;
    public int mSpan = 1;
    String mValue;
    public ExcelCell(int row, int cell, String value){
        mRow = row;
        mCell = cell;
        mValue = value;
    }

    public void setSpan(int span){
        mSpan = span;
    }

    public int getRow(){return mRow;}
    public int getCell(){return mCell;}
    public int getmSpan(){return mSpan;}
    public String getValue(){return mValue;}

}
