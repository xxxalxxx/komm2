package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class HeatingTable extends BaseTable {

    public final static String TABLE_NAME = "heating";


    public final static String
            COL_ID = "id",
            COL_BILL_ID = "bill_id",
            COL_CALC = "calc",
            COL_RECALC = "recalc",
            COL_SUB = "sub",
            COL_COMP = "comp",
            COL_ADDPAY = "addpay",
            COL_OVERPAY = "overpay",
            COL_TOTAL = "total",
            COL_SUM = "sum";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_BILL_ID,
            COL_CALC,
            COL_RECALC,
            COL_SUB,
            COL_COMP,
            COL_TOTAL,
            COL_OVERPAY,
            COL_ADDPAY,
            COL_SUM
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                COL_ID + SUFFIX_ID + COMMA +
                COL_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_CALC + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_RECALC + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_SUB + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_COMP + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_TOTAL + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_ADDPAY + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_OVERPAY + SUFFIX_TEXT_NOT_NULL + COMMA +
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
