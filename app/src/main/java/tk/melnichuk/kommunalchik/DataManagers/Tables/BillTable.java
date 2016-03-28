package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class BillTable extends BaseTable {
    //bill(id, name, desc, created_at, updated_at);

    public final static String TABLE_NAME = "bill";

    public final static String
            COL_ID = "id",
            COL_NAME = "name",
            COL_DESC = "desc",
            COL_CREATED_AT = "created_at",
            COL_UPDATED_AT = "updated_at";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_NAME,
            COL_DESC,
            COL_CREATED_AT,
            COL_UPDATED_AT
    };

    final static String SQL_CREATE =
        CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
            COL_ID + SUFFIX_ID + COMMA +
            COL_NAME + SUFFIX_TEXT_NOT_NULL + COMMA +
            COL_DESC + SUFFIX_TEXT_NOT_NULL + COMMA +
            COL_CREATED_AT + SUFFIX_DATETIME + " DEFAULT CURRENT_TIMESTAMP" + COMMA +
            COL_UPDATED_AT + SUFFIX_DATETIME + " DEFAULT 0 " +
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
