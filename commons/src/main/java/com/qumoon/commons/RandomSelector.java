package com.qumoon.commons;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Random;

/**
 * 根据概率随机选择一个元素
 * @author kevin
 */
public class RandomSelector<T> {

    private Map<T, Integer> items = Maps.newHashMap();
    private Random rand = new Random();
    private int totalSum = 0;

    private RandomSelector(Map<T, Integer> items) {
        this.items = items;
        for (Integer prob : items.values()) {
            totalSum = totalSum + prob;
        }
    }

    public static <T> RandomSelector build(Map<T, Integer> items) {
        return new RandomSelector(items);
    }

    public T getRandom() {
        if (null == items || items.isEmpty()) {
            return null;
        }
        int index = rand.nextInt(totalSum);
        int sum = 0;
        while (true) {
            for (Map.Entry<T, Integer> entry : items.entrySet()) {
                if (sum >= index) {
                    return entry.getKey();
                }
                sum = sum + entry.getValue();
            }
        }
    }
}
