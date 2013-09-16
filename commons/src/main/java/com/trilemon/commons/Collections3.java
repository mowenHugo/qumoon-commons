package com.trilemon.commons;

import java.util.Collection;

/**
 * @author kevin
 */
public class Collections3 {
    public static int sumInt(Collection<Integer> list) {
        int sum = 0;
        for (Integer ele : list) {
            sum += ele;
        }
        return sum;
    }

    public static long sumLong(Collection<Long> list) {
        long sum = 0;
        for (Long ele : list) {
            sum += ele;
        }
        return sum;
    }
}
