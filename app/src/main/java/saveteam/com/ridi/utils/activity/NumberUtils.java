package saveteam.com.ridi.utils.activity;

import java.text.DecimalFormat;

public class NumberUtils {
    public static double format(double value) {
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
        return new Double(df2.format(value)).doubleValue();
    }

    public static double formatMoney(double value) {
        DecimalFormat df2 = new DecimalFormat( "#,###,###,###" );
        return new Double(df2.format(value)).doubleValue();
    }
}
