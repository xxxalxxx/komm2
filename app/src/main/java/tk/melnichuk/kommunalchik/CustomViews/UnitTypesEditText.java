package tk.melnichuk.kommunalchik.CustomViews;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

import tk.melnichuk.kommunalchik.Helpers.Fraction;
import tk.melnichuk.kommunalchik.Helpers.Utils;
import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 27.03.16.
 */
public class UnitTypesEditText extends EditText {
    public UnitTypesEditText(Context context) {
        super(context);
    }
    public UnitTypesEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnitTypesEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    enum TYPE {
        DECIMAL,
        PERCENT,
        FRACTION
    }
    final BigDecimal PERCENT_100 = new BigDecimal(100);
    private TYPE type = TYPE.DECIMAL;

    public TYPE getType(){
        return type;
    }

    public String getLabel(){
        if(isFraction()) return getResources().getString(R.string.format_fraction);
        else if(isPercent()) return getResources().getString(R.string.format_percent);
        return getResources().getString(R.string.format_decimal);
    }

    public int getUnitTypeId(){
        return isDecimal() ? 0 : (isPercent() ? 1 : 2);
    }

    public void setUnitTypeFromIdStr(String index){
        if(index.equals("0")) type = TYPE.DECIMAL;
        else if(index.equals("1")) type = TYPE.PERCENT;
        else type = TYPE.FRACTION;
    }

    public void setType(CharSequence c){
        if (c == getResources().getString(R.string.format_fraction))  type = TYPE.FRACTION;
        else if (c == getResources().getString(R.string.format_percent))  type = TYPE.PERCENT;
        else type = TYPE.DECIMAL;
    }

    public void setDecimal(){
        formatToDecimal();
        type = TYPE.DECIMAL;
        if(this.length()!=0)
            this.setSelection(this.length());
    }

    public void setPercent(){
        formatToPercent();
        type = TYPE.PERCENT;
        if(this.length()!=0)
            this.setSelection(this.length());
    }

    public void setFraction(){
        formatToFraction();
        type = TYPE.FRACTION;
        if(this.length()!=0)
            this.setSelection(this.length());
    }
    public boolean isFraction(){
        return type == TYPE.FRACTION;
    }
    public boolean isDecimal(){
        return type == TYPE.DECIMAL;
    }
    public boolean isPercent(){
        return type == TYPE.PERCENT;
    }

    void formatToFraction(){
        if(isFraction() || this.length()==0) return;

        if(isPercent())
            setText(new BigDecimal(getText().toString()).divide(PERCENT_100, 6, RoundingMode.HALF_UP).toString());

        String val = getText().toString();
        double d = Double.parseDouble(val);
        setText(new Fraction(d).toString());
    }





    public void formatToDecimal(){
        if(isDecimal() || this.length()==0) return;
        String val = getText().toString();

        if (isFraction())
            if (val.contains("/")) {
                String[] arr = val.split("/");
                try{
                    setText(new BigDecimal(arr[0]).divide(new BigDecimal(arr[1]), 6, RoundingMode.HALF_UP)
                            .stripTrailingZeros().toString());
                    return;
                }catch (ArrayIndexOutOfBoundsException e){ val = val.replace("/",""); }
            }
        //TODO: if its fraciton without "/",it should be divided by 100! REDO
        if(isPercent()) {
            setText(Utils.getZeroStrippedString(new BigDecimal(val).divide(PERCENT_100, 6, RoundingMode.HALF_UP)));
            return;
        }
        setText(val);
    }




    void formatToPercent(){
        if(isPercent() || this.length()==0) return;
        String val = getText().toString();

        if (isFraction())
            if(val.contains("/")) {
                String[] arr = val.split("/");
                try{
                    setText(new BigDecimal(arr[0]).divide(new BigDecimal(arr[1]), 6, RoundingMode.HALF_UP).multiply(PERCENT_100)
                            .setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                    return;
                } catch (ArrayIndexOutOfBoundsException e){ val = val.replace("/",""); }
            }
        BigDecimal b = new BigDecimal(val).multiply(PERCENT_100).setScale(6, RoundingMode.HALF_UP);

        setText(Utils.getZeroStrippedString(b));
    }

    public String getDecimalVal(){
        String val = getText().toString();
        if(val.length()==0) return "0.00";
        if(isDecimal()) return val;

        if (isFraction()){
            if (val.contains("/")) {
                if (val.indexOf("/")==0 || val.indexOf("/")==val.length()-1){
                    return val.replace("/","");
                }else{
                    String[] arr = val.split("/");
                    if(Double.parseDouble(arr[1])==0){
                        setText("0");
                        return "0";
                    }
                    return  new BigDecimal(arr[0]).divide(new BigDecimal(arr[1]), 6, RoundingMode.HALF_UP)
                            .setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                }
            } else return val;
        }

        return  new BigDecimal(val).divide(PERCENT_100).setScale(6, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();

    }



}
