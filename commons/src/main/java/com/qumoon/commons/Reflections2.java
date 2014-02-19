package com.qumoon.commons;

import com.google.common.collect.Lists;
import org.reflections.Reflections;

import java.util.List;

/**
 * @author kevin
 */
public class Reflections2 {
    public static <T> List<Class<? extends T>> getSubTypesOf(String packageName, final Class<T> type) {
        List<Class<? extends T>> subTypes = Lists.newArrayList();
        for (Class<? extends T> subType : new Reflections(packageName).getSubTypesOf(type)) {
            if (subType.isInterface()) {
                subTypes.addAll(getSubTypesOf(packageName, subType));
            } else {
                subTypes.add(subType);
            }
        }
        return subTypes;
    }
}
