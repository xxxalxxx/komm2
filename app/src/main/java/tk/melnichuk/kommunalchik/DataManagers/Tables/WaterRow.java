package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class WaterRow extends BaseTable {

    //cold_water_row(id,water_bill_id, cold_water_id,type(normal_row, counter_row), calc, recalc, sub, comp, total);


    public final static String
            TABLE_NAME = "water_row";

    public final static int
            ROW_TYPE_NORMAL  = 0,
            ROW_TYPE_COUNTER = 1;

    public final static String
            COL_ID = "id",
            COL_WATER_BILL_ID = "water_bill_id",
            COL_ROW_TYPE = "row_type",
            COL_CALC = "calc",
            COL_RECALC = "recalc",
            COL_SUB = "sub",
            COL_COMP = "comp",
            COL_TOTAL = "total";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_WATER_BILL_ID,
            COL_ROW_TYPE,
            COL_CALC,
            COL_RECALC,
            COL_SUB ,
            COL_COMP,
            COL_TOTAL,
    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                    COL_ID + SUFFIX_ID + COMMA +
                    COL_WATER_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_ROW_TYPE + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_CALC + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_RECALC + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_SUB + SUFFIX_TEXT_NOT_NULL + COMMA +
                    COL_COMP + SUFFIX_TEXT_NOT_NULL + COMMA +
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
