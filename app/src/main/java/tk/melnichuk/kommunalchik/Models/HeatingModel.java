package tk.melnichuk.kommunalchik.Models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by al on 30.03.16.
 */
public class HeatingModel extends BaseModel {
    public static final int
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

}
