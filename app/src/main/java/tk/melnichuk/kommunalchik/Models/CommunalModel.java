package tk.melnichuk.kommunalchik.Models;

import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by al on 30.03.16.
 */
public class CommunalModel extends BaseModel {
    public static final int
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

    @Override
    public void deleteFromDb(SQLiteDatabase db, long billId) {

      /*  int currId = getCurrId(db, CommunalTable.TABLE_NAME, CommunalTable.COL_ID, CommunalTable.COL_BILL_ID, String.valueOf(id));

        if(currId != -1){
            String currIdStr = String.valueOf(currId);
            deleteSegmentsFromDb(db,currIdStr);
            db.delete(CommunalTable.TABLE_NAME, CommunalTable.COL_ID + "=?", new String[]{currIdStr});
        }*/
    }

}
