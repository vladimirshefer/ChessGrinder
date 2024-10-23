package com.chessgrinder.chessgrinder.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CacheUtil {
    /**
     * Map, which drops eldest element on [capacity] overflow.
     * Synchronized for thread safety.
     */
    public static <K, V> Map<K, V> createCache(int capacity) {
        return Collections.synchronizedMap(
                new LinkedHashMap<>(capacity) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                        return size() > capacity;
                    }
                }
        );
    }
}
