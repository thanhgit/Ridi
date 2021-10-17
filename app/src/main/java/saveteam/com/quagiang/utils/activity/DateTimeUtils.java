package saveteam.com.quagiang.utils.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {
    /**
     * Get now
     */

    public static String getNow() {
        SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        return myDateFormat.format(new Date());
    }

    /**
     * Date time format
     */

    public static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
    }

    public static Date stringToDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }

    public static String getEndTime(String strStartDate, int seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        try {
            Date start = sdf.parse(strStartDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            cal.add(Calendar.SECOND, seconds);
            return sdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static SimpleDateFormat getTimeFormat() {
        return new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
    }

    public static String getShortDate(String date){
        SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        try {
            Date currentDate = myDateFormat.parse(date.trim());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault());
            return sdf.format(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
