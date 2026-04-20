package com.collegeevent.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;

public final class DateTimeUtil {
    private DateTimeUtil() {
    }

    public static boolean isFutureEvent(LocalDate eventDate) {
        return eventDate != null && eventDate.isAfter(LocalDate.now());
    }

    public static boolean isTodayOrFuture(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public static Period getTimeUntilEvent(LocalDate eventDate) {
        return Period.between(LocalDate.now(), eventDate);
    }

    public static boolean isValidBookingTime(LocalTime time) {
        return time != null && !time.isBefore(LocalTime.of(9, 0))
                && !time.isAfter(LocalTime.of(18, 0));
    }

    public static boolean isSameDay(LocalDate firstDate, LocalDate secondDate) {
        return firstDate != null && firstDate.equals(secondDate);
    }
}