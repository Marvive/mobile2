package com.mmarvive.wgumobileproject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility to set the date
 * */

public class DateUtility {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd-yyyy h:mm a z", Locale.getDefault());
//    24 * 60 * 60 * 1000 - Turns amount of days to milliseconds
    public static final long millisecondMultiplier = 86400000;

    public static long getDateTimestamp(String dateInput) {
        try {
            Date date = DateUtility.dateFormat.parse(dateInput + TimeZone.getDefault().getDisplayName());
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static long todayLong() {
        String currentDate = DateUtility.dateFormat.format(new Date());
        return getDateTimestamp(currentDate);
    }

    public static long todayLongWithTime() {
        return System.currentTimeMillis();
    }
}
