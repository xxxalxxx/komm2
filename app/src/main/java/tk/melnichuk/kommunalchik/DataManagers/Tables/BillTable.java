package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class BillTable extends BaseTable {
    //bill(id, type,relation, name, desc, created_at, updated_at);

    public final static String TABLE_NAME = "bill", NAME_TEMP = "temp";

    public final static String
            COL_ID = "id",

            COL_STATUS = "status",
            COL_RELATION = "relation",
            COL_NAME = "name",
            COL_DESC = "desc",
            COL_DATE = "date";

    public static final int
    RELATION_NONE = 0,

    STATUS_TEMP = 0,
    STATUS_SAVED = 1,
    STATUS_TEMP_FROM_SAVED = 2,

    TEMP_BILL_ID = 0;

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_STATUS,
            COL_RELATION,
            COL_NAME,
            COL_DESC,
            COL_DATE,
    };

    final static String SQL_CREATE =
        CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
            COL_ID + SUFFIX_ID + COMMA +
           COL_STATUS + SUFFIX_INTEGER_NOT_NULL + COMMA +
            COL_RELATION + SUFFIX_INTEGER_NOT_NULL + COMMA +
            COL_NAME + SUFFIX_TEXT_NOT_NULL + COMMA +
            COL_DESC + SUFFIX_TEXT_NOT_NULL + COMMA +
            COL_DATE + SUFFIX_DATETIME + SUFFIX_DEFAULT_0 +
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
