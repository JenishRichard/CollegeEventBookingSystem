package com.collegeevent.util;

import java.util.HashMap;
import java.util.Map;

public final class MapMergeUtil {
    private MapMergeUtil() {
    }

    public static <K, V1, V2> Map<K, String> mergeMaps(Map<K, V1> firstMap, Map<K, V2> secondMap) {
        Map<K, String> merged = new HashMap<>();

        for (K key : firstMap.keySet()) {
            if (secondMap.containsKey(key)) {
                merged.put(key, firstMap.get(key) + " | " + secondMap.get(key));
            }
        }

        return merged;
    }
}