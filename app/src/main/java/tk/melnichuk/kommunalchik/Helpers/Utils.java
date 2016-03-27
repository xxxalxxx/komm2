package tk.melnichuk.kommunalchik.Helpers;

import android.content.Context;
import android.content.res.Resources;

import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentTable;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 25.03.16.
 */
public class Utils {

    public static String getUnitValue(Context context,int unit){
        Resources res = context.getResources();
        return unit == SegmentTable.UNIT_DECIMAL
        ? res.getString(R.string.format_decimal)
        : ( unit == SegmentTable.UNIT_PERCENT
            ? res.getString(R.string.format_percent)
            : res.getString(R.string.format_fraction) );
    }
}
