package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 31.03.16.
 */
public class SegmentRowTable extends BaseTable {

    //segment_row(id,segment_id, position, value);

    public SegmentRowTable(){

    }

    public final static String TABLE_NAME = "segment_row";

    public final static String
            COL_ID = "id",
            COL_SEGMENT_ID = "bill_id",
            COL_POSITION = "position",
            COL_VALUE = "value";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_SEGMENT_ID,
            COL_POSITION,
            COL_VALUE
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                    COL_ID + SUFFIX_ID + COMMA +
                    COL_SEGMENT_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_POSITION + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_VALUE + SUFFIX_TEXT_NOT_NULL +
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
