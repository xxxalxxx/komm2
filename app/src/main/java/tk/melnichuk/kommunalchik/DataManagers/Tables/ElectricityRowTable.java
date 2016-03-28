package tk.melnichuk.kommunalchik.DataManagers.Tables;

/**
 * Created by al on 28.03.16.
 */
public class ElectricityRowTable extends BaseTable{
    //electricity_row(id, electricity_bill_id, type(nomral,subsidy), step ,rate,total)
    public final static String
        TABLE_NAME = "electricity_row";

    public final static int
        STEP_SUBSIDY = 0,
        STEP_1 = 1,
        STEP_2 = 2,
        STEP_3 = 3,
        TYPE_NORMAL  = 0,
        TYPE_SUBSIDY = 1;

    public final static String
            COL_ID = "id",
            COL_ELECTRICITY_BILL_ID = "electricity_bill_id",
            COL_STEP = "step",
            COL_RATE = "rate",
            COL_TOTAL = "total";

    public final static String[] COLUMN_NAMES = {
            COL_ID,
            COL_ELECTRICITY_BILL_ID,
            COL_STEP,
            COL_RATE,
            COL_TOTAL,

    };

    final static String SQL_CREATE =
            CREATE_TABLE_IF_NOT_EXISTS + TABLE_NAME + "("+
                    COL_ID + SUFFIX_ID + COMMA +
                    COL_ELECTRICITY_BILL_ID + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_STEP + SUFFIX_INTEGER_NOT_NULL + COMMA +
                    COL_RATE + SUFFIX_TEXT_NOT_NULL + COMMA +
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
