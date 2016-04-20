package tk.melnichuk.kommunalchik.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;

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

    public static int getHeight(Context context, String text, int textSize, int deviceWidth) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    public static BigDecimal getParsedBigDecimal(String s){
        if(s == null) return BigDecimal.ZERO;
        s = s.trim();
        if(s.isEmpty()) return BigDecimal.ZERO;

        try {
            if(s.contains("/")) {
                String[] vals = s.split("/");
                if(Double.parseDouble(vals[1]) == 0.0) return BigDecimal.ZERO;
                return  new BigDecimal(vals[0]).divide(new BigDecimal(vals[1]));
            }

            return  new BigDecimal(s);
        } catch(Exception e) {
            Log.e("_UDB", e.toString());
        }
        return BigDecimal.ZERO;
    }

    public static  String getZeroStrippedString(BigDecimal b){
        BigDecimal zero = BigDecimal.ZERO;
        b = b.compareTo(zero) == 0 ? zero : b.stripTrailingZeros();
        return b.toPlainString();
    }

}
