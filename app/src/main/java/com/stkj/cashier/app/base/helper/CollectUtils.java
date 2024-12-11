package com.stkj.cashier.app.base.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合工具类
 */
public class CollectUtils {

    public static <T> ArrayList<T> copyList(List<T> sourceList) {
        return new ArrayList<>(sourceList);
    }

    public static <T> HashSet<T> copySet(Set<T> sourceSet) {
        return new HashSet<>(sourceSet);
    }

    public static <K, V> HashMap<K, V> copyMap(Map<K, V> sourceMap) {
        return new HashMap<>(sourceMap);
    }

}
