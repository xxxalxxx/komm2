package tk.melnichuk.kommunalchik.Models;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by al on 30.03.16.
 */
public class ElectricityModel extends BaseModel {
    public static final int
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

}
