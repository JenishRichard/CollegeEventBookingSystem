package com.collegeevent.model;

import java.time.LocalDate;

public class CollegeEvent {
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

    @Override
    public String toString() {
        return "CollegeEvent{id='%s', title='%s', category=%s, date=%s}"
                .formatted(eventId, title, category, date);
    }
}