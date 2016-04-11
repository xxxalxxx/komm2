package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 29.03.16.
 */
public class GasTable extends  BaseTable {
    //gas(id,bill_id, calcby, area, registered, curr, prev,diff, rate, calc, sub, addpay, total )

    public final static String TABLE_NAME = "gas";

    public final static int
            CALC_BY_RATE = 0,
            CALC_BY_COUNTER = 1;

    public final static String
            COL_ID = "id",
            COL_BILL_ID = "bill_id",
            COL_CALC_BY = "calc_by",
            COL_AREA = "area",
            COL_REGISTERED = "registered",
            COL_CURR = "curr",
            COL_PREV = "prev",
            COL_DIFF = "diff",
            COL_RATE = "rate",
            COL_CALC = "calc",
            COL_SUB = "sub",
            COL_ADDPAY = "addpay",
            COL_TOTAL = "total",
            COL_SUM = "sum";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_BILL_ID,
            COL_CALC_BY,
            COL_AREA,
            COL_REGISTERED,
            COL_CURR,
            COL_PREV,
            COL_DIFF,
            COL_RATE,
            COL_CALC,
            COL_SUB,
            COL_ADDPAY,
            COL_TOTAL,
            COL_SUM
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                    COL_ID + SUFFIX_ID + COMMA +
                    COL_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_CALC_BY + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_AREA + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_REGISTERED + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_CURR + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_PREV + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_DIFF + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_RATE + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_CALC + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_SUB + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_ADDPAY + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_TOTAL + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_SUM + SUFFIX_TEXT_NOT_NULL +
            "); ";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    String[] getColumnNames() {
        return COLUMN_NAMES;
    }


    @Override
    public String getCreateSql() {
        return SQL_CREATE;
    }

}
