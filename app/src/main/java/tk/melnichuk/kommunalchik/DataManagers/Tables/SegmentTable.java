package tk.melnichuk.kommunalchik.DataManagers.Tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import tk.melnichuk.kommunalchik.DataManagers.DbManager;

/**
 * Created by al on 24.03.16.
 */
public class SegmentTable extends BaseTable {
    // segment(id, bill_id (id of instance), bill_type (phone,electr..) , segment_type(global, common, local) , unit(decimal, percent, fraction) )

    public SegmentTable(){

    }

    public final static String TABLE_NAME = "segment";

    public final static int
        UNIT_DECIMAL  = 0,
        UNIT_PERCENT  = 1,
        UNIT_FRACTION = 2,

        BILL_TYPE_NONE = -1,
        TYPE_LOCAL  = 0,
        TYPE_GLOBAL = 1;
    public final static int BILL_ID_GLOBAL = -1;

    public final static String
            COL_ID = "id",
            COL_BILL_ID = "bill_id",
            COL_BILL_TYPE = "bill_type",
            COL_TYPE = "type",
            COL_NAME = "name",
            COL_UNIT = "unit",
            COL_VALUE = "value";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_BILL_ID,
            COL_BILL_TYPE,
            COL_TYPE,
            COL_NAME,
            COL_UNIT,
            COL_VALUE
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                COL_ID + SUFFIX_ID + COMMA +
                COL_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_BILL_TYPE + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_TYPE + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_NAME + SUFFIX_TEXT_NOT_NULL + COMMA +
                COL_UNIT + SUFFIX_INTEGER_NOT_NULL + COMMA +
                COL_VALUE + SUFFIX_TEXT_NOT_NULL +
            //  " FOREIGN KEY (" + TASK_CAT + ") REFERENCES " + CAT_TABLE +"("+CAT_ID+"));";
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
