package com.collegeevent.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class DateTimeUtil {
    private DateTimeUtil() {
    }

    public static boolean isOldEnoughToVote(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }

    public static boolean isOldEnoughToDrive(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= 17;
    }

    public static Period getDateDifference(LocalDate startDate, LocalDate endDate) {
        return Period.between(startDate, endDate);
    }

    public static ZonedDateTime calculateArrivalTime(
            ZonedDateTime departureTime,
            int flightHours,
            int flightMinutes,
            String arrivalZoneId) {
        return departureTime
                .plusHours(flightHours)
                .plusMinutes(flightMinutes)
                .withZoneSameInstant(ZoneId.of(arrivalZoneId));
    }
}