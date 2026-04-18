package com.collegeevent.util;

import java.util.List;

public final class GenericUtils {
    private GenericUtils() {
    }

    public static <T> void printItems(List<T> items) {
        items.forEach(System.out::println);
    }

    public static <T> T getFirstItem(List<T> items) {
        return items.isEmpty() ? null : items.get(0);
    }
}