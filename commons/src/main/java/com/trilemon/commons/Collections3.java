package com.trilemon.commons;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * @author kevin
 */
public class Collections3 {
    public static final Splitter COMMA_SPLITTER = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings();
    public static final Joiner COMMA_JOINER = Joiner.on(',').skipNulls();

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

    public static List<Long> getLongList(String str) {
        Iterable<String> stringList = COMMA_SPLITTER.split(str);
        return string2Long(Lists.newArrayList(stringList));
    }

    public static List<Integer> getIntList(String str) {
        Iterable<String> stringList = COMMA_SPLITTER.split(str);
        return string2Int(Lists.newArrayList(stringList));
    }

    public static List<Long> string2Long(List<String> from) {
        List<Long> longList = Lists.newArrayListWithCapacity(from.size());
        for (String ele : from) {
            longList.add(Long.valueOf(ele));
        }
        return longList;
    }

    public static List<Integer> string2Int(List<String> from) {
        List<Integer> longList = Lists.newArrayListWithCapacity(from.size());
        for (String ele : from) {
            longList.add(Integer.valueOf(ele));
        }
        return longList;
    }

    public static List<String> number2String(List<? extends Number> from) {
        List<String> StringList = Lists.newArrayListWithCapacity(from.size());
        for (Number ele : from) {
            StringList.add(String.valueOf(ele));
        }
        return StringList;
    }
}
