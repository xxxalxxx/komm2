package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class PhoneTable extends BaseTable{
    //phone(id,bill_id,tax)
    public final static String
            TABLE_NAME = "phone";


    public final static String
            COL_ID = "id",
            COL_BILL_ID = "bill_id",
            COL_TAX = "step";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_BILL_ID,
            COL_TAX
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                    COL_ID + SUFFIX_ID + COMMA +
                    COL_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_TAX + SUFFIX_INTEGER_NOT_NULL +
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