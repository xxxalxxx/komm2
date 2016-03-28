package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class ElectricityTable extends BaseTable{

    //electricity(id,bill_id, curr, prev,diff)

    public final static String TABLE_NAME = "electricity";


    public final static String
            COL_ID = "id",
            COL_BILL_ID = "bill_id",
            COL_CURR = "type",
            COL_PREV = "name",
            COL_DIFF = "diff";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_BILL_ID,
            COL_CURR,
            COL_PREV,
            COL_DIFF
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                COL_ID + SUFFIX_ID + COMMA +
                COL_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_CURR + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_PREV + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_DIFF + SUFFIX_TEXT_NOT_NULL +
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
