package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 25.03.16.
 */
public class SegmentBillTypeTable extends BaseTable {

    public SegmentBillTypeTable(){}

    public final static String TABLE_NAME = "segment_bill_type";

    public final static String
            COL_ID = "id",
            COL_SEGMENT_ID = "segment_id",
            COL_TYPE = "type";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_SEGMENT_ID,
            COL_TYPE
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                COL_ID + SUFFIX_ID + COMMA +
                COL_SEGMENT_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_TYPE + SUFFIX_INTEGER_NOT_NULL +
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
