package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class PhoneRowTable extends BaseTable {

    //phone_row(id,phone_bill_id,type,pretax,tax, total)

    public final static String
            TABLE_NAME = "phone_row";

    public final static int
            TYPE_PHONE  = 0,
            TYPE_RADIO  = 1,
            TYPE_TOTAL  = 2;

    public final static String
            COL_ID = "id",
            COL_PHONE_BILL_ID = "phone_bill_id",
            COL_TYPE = "type",
            COL_PRETAX = "pretax",
            COL_TAX = "tax",
            COL_TOTAL = "total";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_PHONE_BILL_ID,
            COL_TYPE,
            COL_PRETAX,
            COL_TAX,
            COL_TOTAL,
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                COL_ID + SUFFIX_ID + COMMA +
                COL_PHONE_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_TYPE + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_PRETAX + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_TAX + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_TOTAL + SUFFIX_TEXT_NOT_NULL +
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
