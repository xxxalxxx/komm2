package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class WaterTable extends BaseTable{
    //cold_water(id,bill_id, type, counter, curr, prev,diff, rate)
    public final static String
        TABLE_NAME = "water";

    public final static int
        COUNTER_NONE = 0,
        COUNTER_EXISTS = 1,
        TYPE_COLD  = 0,
        TYPE_WASTE = 1,
        TYPE_HOT   = 2;

    public final static String
        COL_ID = "id",
        COL_BILL_ID = "bill_id",
        COL_TYPE = "type",
        COL_COUNTER = "counter",
        COL_CURR = "curr",
        COL_PREV = "prev",
        COL_DIFF = "diff",
        COL_RATE = "rate";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_BILL_ID,
            COL_TYPE,
            COL_COUNTER,
            COL_CURR,
            COL_PREV,
            COL_DIFF,
            COL_RATE
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                COL_ID + SUFFIX_ID + COMMA +
                COL_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_TYPE + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_COUNTER + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_CURR + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_PREV + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_DIFF + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_RATE + SUFFIX_TEXT_NOT_NULL +
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
