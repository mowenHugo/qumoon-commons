package com.trilemon.commons;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

/**
 * @author kevin
 */
public class DateUtils {
    public static final String yyyyMMdd2 = "yyyy-MM-dd";
    public static final String yyyyMMdd3 = "yyyy/MM/dd";
    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String MMdd = "MM-dd";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static DateTime parse(String date, String formatter) {
        return DateTime.parse(date, DateTimeFormat.forPattern(formatter));
    }

    public static String format(Date date, String formatter) {
        return new DateTime(date.getTime()).toString(formatter);
    }

    public static Date startOf(Date day) {
        return new DateTime(day).withTimeAtStartOfDay().toDate();
    }

    public static Date endOf(Date day) {
        return new DateTime(day).millisOfDay().withMaximumValue().toDate();
    }

    public static DateTime startOfYesterday() {
        return startOfNDaysBefore(1);
    }

    public static DateTime endOfYesterday() {
        return endOfNDaysBefore(1);
    }

    public static DateTime startOfNDaysBefore(int n) {
        return DateTime.now().minusDays(n).withTimeAtStartOfDay();
    }

    public static DateTime endOfNDaysBefore(int n) {
        return DateTime.now().minusDays(n).secondOfDay().withMaximumValue();
    }
}
