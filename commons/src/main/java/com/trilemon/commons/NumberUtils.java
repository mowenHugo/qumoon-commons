package com.trilemon.commons;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kevin
 */
public class NumberUtils {
    private static Pattern numberRegexPattern = Pattern.compile("(少于)*([\\d,\\\\.]+)([万]*)");
    private static NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public static int parseInt(String str) throws ParseException {
        Matcher matcher = numberRegexPattern.matcher(str);
        String numberStr = null;
        String unit = null;
        if (matcher.find()) {
            numberStr = matcher.group(2);
            unit = matcher.group(3);
        }
        if (null != numberStr) {
            if (null != unit) {
                if ("万".equals(unit)) {
                    return (int)(numberFormat.parse(numberStr).doubleValue() * 10000);
                }
            }
            return (int)(numberFormat.parse(numberStr).doubleValue());
        }
        return 0;
    }

    public static Number parse(String str) throws ParseException {
        return numberFormat.parse(str);
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(NumberUtils.parseInt("19.9万人"));
    }
}
