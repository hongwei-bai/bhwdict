package bhwWords.filter;

import java.util.Calendar;
import bhwWords.provider.WordDbHelper;

import android.util.Log;

public class DateFilter {
    private static final long DAY_IN_MILLISECOND = 24 * 60 * 60 * 1000;
    private static final int HOUR_FILTER = 5; // from yesterday 5.am - present
                                              // counts for latest 1 day.
    private static final int HOUR_RECORD = 6;

    public static String getLatestDayClause(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, HOUR_FILTER);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long today = calendar.getTimeInMillis();

        long lastnday = today - DAY_IN_MILLISECOND * day;
        String selection = WordDbHelper.TABLE_WORD.DATE_ADDED + " > " + lastnday;
        return selection;
    }

    public static String getLatestDayClause(int day, int dayto) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, HOUR_FILTER);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long today = calendar.getTimeInMillis();

        long lastnday = today - DAY_IN_MILLISECOND * day;
        long lastnday2 = today - DAY_IN_MILLISECOND * dayto;
        String selection = WordDbHelper.TABLE_WORD.DATE_ADDED + " > " + lastnday2;
        selection += " AND " + WordDbHelper.TABLE_WORD.DATE_ADDED + " <= " + lastnday;
        return selection;
    }

    public static String dbgMillisecondToString(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Log.d("aaaa", "milliseconds indicates " + year + "-" + month + "-" + day);
        return calendar.toString();
    }

    public static String toString(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        String string = /* calendar.get(Calendar.YEAR) + "/" + */(calendar.get(Calendar.MONTH) + 1) + "/"
                + calendar.get(Calendar.DAY_OF_MONTH);
        return string;
    }

    public static String toExportString(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        String string = "";
        string += calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10) {
            string += "0";
        }
        string += month;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            string += "0";
        }
        string += day;
        return string;
    }

    public static String toExportTimeString(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        String string = "";
        if (!isToday(ms)) {
            int month = calendar.get(Calendar.MONTH) + 1;
            if (month < 10) {
                string += "0";
            }
            string += month;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day < 10) {
                string += "0";
            }
            string += "-" + day;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        string += " ";
        if (hour < 10) {
            string += "0";
        }
        string += hour + ":";
        if (minute < 10) {
            string += "0";
        }
        string += minute;
        return string;
    }

    public static boolean isToday(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        Calendar todayCalendar = Calendar.getInstance();
        if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
            return true;
        }
        return false;
    }

    public static long buildCalenderDate(String exportDate) {
        int year = Integer.valueOf(exportDate.substring(0, 4));
        int month = Integer.valueOf(exportDate.substring(4, 6)) - 1;
        int day = Integer.valueOf(exportDate.substring(6, 8));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, HOUR_RECORD, 0);
        return calendar.getTimeInMillis();
    }
}
