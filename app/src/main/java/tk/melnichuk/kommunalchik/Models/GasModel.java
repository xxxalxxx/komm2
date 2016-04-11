package tk.melnichuk.kommunalchik.Models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by al on 30.03.16.
 */
public class GasModel extends BaseModel {
    public static final int
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
        BigDecimal total1 = b[INDEX_TOTAL].setScale(2, RoundingMode.HALF_UP);

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


}
