package tk.melnichuk.kommunalchik.DataManagers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import tk.melnichuk.kommunalchik.DataManagers.Tables.BaseTable;

public class DbManager extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "kommunalchik";
    ArrayList<BaseTable> mTables;


    // bill(id, add_time, upd_time, name, )
    // cold_water_bill(id, counter, prev,curr,diff, rate, calc, recalc ,subsidy, comp, total);
    // segment(id, bill_id (id of instance), bill_type (phone,electr..) , segment_type(global, common, local) , unit(decimal, percent, fraction) )

    public DbManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        mTables = new ArrayList<>();
    }

    public void registerTable(BaseTable table){
        mTables.add(table);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        for(BaseTable table : mTables) {
            db.execSQL(table.getCreateSql());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
