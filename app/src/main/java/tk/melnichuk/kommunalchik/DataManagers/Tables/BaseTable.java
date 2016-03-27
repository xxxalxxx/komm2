package tk.melnichuk.kommunalchik.DataManagers.Tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public abstract class BaseTable  {
    //bill(id,[type(temp, temp_db ,db)], name, [desc], create_at, updated_at)
    //communal(id,bill_id, calc, recalc, sub, comp, overpay, addpay, total)
    //gas(id,bill_id, calcby, area, registered, curr, prev, rate, calc, sub, addpay, total )
    //cold_water(id,bill_id, counter, curr, prev, rate)
    //cold_water_row(id,cold_water_bill_id, cold_water_id,type(normal_row, counter_row), calc, recalc, sub, comp, total);
    //heating
    //hot_water
    //electricity(id,bill_id, curr, prev)
    //electricity_row(id, electricity_bill_id, type(nomral,subsidy), step ,rate,total)
    //phone(id,bill_id,tax,calc_phone, calc_radio, total_phone, total_radio)
    public final static String
    COMMA = ",",
    CREATE_TABLE = "CREATE TABLE ",
    INSERT_INTO = "INSERT INTO ",
    CREATE_TABLE_IF_NOT_EXISTS = CREATE_TABLE + "IF NOT EXISTS ",
    DROP_TABLE = "DROP TABLE ",
    SUFFIX_ID = " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ",
    SUFFIX_TEXT_NOT_NULL = " TEXT NOT NULL ",
    SUFFIX_INTEGER_NOT_NULL = " INTEGER NOT NULL ",
    SUFFIX_DEFAULT_0 = " DEFAULT 0 ",
    ORDER_ASC = " ASC",
    ORDER_DESC = " DESC";

    public abstract String getTableName();

    abstract String[] getColumnNames();
    public abstract String getCreateSql();

    public String getDropTableString(){
        return DROP_TABLE + getTableName();
    }



}
