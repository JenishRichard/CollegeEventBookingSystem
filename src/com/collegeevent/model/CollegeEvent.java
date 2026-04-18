package com.collegeevent.model;

import java.time.LocalDate;
import java.time.Period;

public class CollegeEvent implements Comparable<CollegeEvent> {
    private final String eventId;
    private final String title;
    private final EventCategory category;
    private final LocalDate date;

    public CollegeEvent(String eventId, String title, EventCategory category, LocalDate date) {
        this.eventId = eventId;
        this.title = title;
        this.category = category;
        this.date = date;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public EventCategory getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public Period getTimeUntilEvent() {
        return Period.between(LocalDate.now(), date);
    }

    @Override
    public int compareTo(CollegeEvent other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public String toString() {
        return "%s | %s | %s | %s"
                .formatted(eventId, title, formatLabel(category.name()), date);
    }

    private static String formatLabel(String value) {
        String formatted = value.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);
    }
}
