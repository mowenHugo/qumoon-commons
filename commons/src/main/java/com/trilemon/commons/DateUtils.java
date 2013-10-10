package com.trilemon.commons;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.List;

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

    public static List<Interval> splitByDay(DateTime startTime, DateTime endTime, int days) {
        int range = endTime.getDayOfYear() - startTime.getDayOfYear();
        List<Interval> checkTimeRanges = Lists.newArrayList();
        if (startTime.getDayOfYear() == endTime.getDayOfYear()) {
            checkTimeRanges.add(new Interval(startTime, endTime));
        } else {
            for (; range > 0; range--) {
                checkTimeRanges.add(new Interval(startTime, startTime.secondOfDay().withMaximumValue()));
                startTime = startTime.plusDays(days).secondOfDay().withMinimumValue();
            }
            checkTimeRanges.add(new Interval(startTime, endTime));
        }
        return checkTimeRanges;
    }

    public static List<Interval> splitByMinute(DateTime startTime, DateTime endTime, int minutes) {
        List<Interval> chunks = Lists.newArrayList();
        DateTime nextChunkStartTime;
        while (true) {
            nextChunkStartTime = startTime.plusMinutes(minutes);
            if (nextChunkStartTime.isBefore(endTime)) {
                chunks.add(new Interval(startTime, nextChunkStartTime));
                startTime = nextChunkStartTime.plusSeconds(1);
            } else {
                chunks.add(new Interval(startTime, endTime));
                break;
            }
        }
        return chunks;
    }
}
