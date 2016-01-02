package sebastians.sportan.tools;

import java.util.Calendar;

/**
 * Created by sebastian on 02/01/16.
 */
public class DateCalculations {


    public static long startOfDay(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }


}
