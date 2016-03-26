package tk.melnichuk.kommunalchik.DataManagers.Tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public abstract class BaseTable  {

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
