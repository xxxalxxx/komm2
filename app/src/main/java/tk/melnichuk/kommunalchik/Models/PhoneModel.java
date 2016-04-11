package tk.melnichuk.kommunalchik.Models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by al on 30.03.16.
 */
public class PhoneModel extends BaseModel {
    public static final int
        INDEX_TAX = 0,

        INDEX_PHONE_NO_TAX = 1,
        INDEX_PHONE_TAX = 2,
        INDEX_PHONE_TOTAL = 3,

        INDEX_RADIO_NO_TAX = 4,
        INDEX_RADIO_TAX = 5,
        INDEX_RADIO_TOTAL = 6,

        INDEX_SUM_NO_TAX = 7,
        INDEX_SUM_TAX = 8,
        INDEX_SUM_TOTAL = 9;

    @Override
    void calcMainTable(BigDecimal[] b) {

        BigDecimal noTax1,withTax1,total1,
                noTax2, withTax2,total2,
                noTax3,withTax3,total3;

        final BigDecimal TAX = b[INDEX_TAX].divide(new BigDecimal(100));

        noTax1 = b[INDEX_PHONE_NO_TAX].setScale(2, RoundingMode.HALF_UP);
        withTax1 = noTax1.multiply(TAX).setScale(2, RoundingMode.HALF_UP);
        if(noTax1.doubleValue()==0 || withTax1.doubleValue()==0)
            total1 = b[INDEX_PHONE_TOTAL].setScale(2, RoundingMode.HALF_UP);
        else total1 = noTax1.add(withTax1);

        noTax2 = b[INDEX_RADIO_NO_TAX].setScale(2, RoundingMode.HALF_UP);
        withTax2 = noTax2.multiply(TAX).setScale(2, RoundingMode.HALF_UP);
        if(noTax2.doubleValue()==0 || withTax2.doubleValue()==0)
            total2 = b[INDEX_RADIO_TOTAL].setScale(2, RoundingMode.HALF_UP);
        else total2 = noTax2.add(withTax2);

        noTax3 = noTax1.add(noTax2);
        withTax3 = withTax1.add(withTax2);
        if(total1.doubleValue()!=0 || total2.doubleValue()!=0)
            total3 = total1.add(total2);
        else if(noTax3.add(withTax3).doubleValue()!=0)
            total3 = noTax3.add(withTax3);
        else total3 = b[INDEX_SUM_TOTAL].setScale(2, RoundingMode.HALF_UP);

        b[INDEX_PHONE_NO_TAX] = noTax1;
        b[INDEX_PHONE_TAX] = withTax1;
        b[INDEX_PHONE_TOTAL] = total1;

        b[INDEX_RADIO_NO_TAX] = noTax2;
        b[INDEX_RADIO_TAX] = withTax2;
        b[INDEX_RADIO_TOTAL] = total2;

        b[INDEX_SUM_NO_TAX] = noTax3;
        b[INDEX_SUM_TAX] = withTax3;
        b[INDEX_SUM_TOTAL] = total3;

    }

    @Override
    BigDecimal[] getLastColumnItems() {
        return new BigDecimal[]{
                mMainTableData[INDEX_PHONE_TOTAL],
                mMainTableData[INDEX_RADIO_TOTAL],
                mMainTableData[INDEX_SUM_TOTAL]
        };
    }

}
