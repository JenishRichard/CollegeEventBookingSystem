package com.collegeevent.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventBooking {
    private final String bookingId;
    private final User bookedBy;
    private final Venue venue;
    private final BookingPurpose purpose;
    private final String bookingTitle;
    private final LocalDate bookingDate;
    private final LocalTime bookingTime;
    private final CollegeEvent event;
    private BookingStatus status;

    public EventBooking(
            String bookingId,
            User bookedBy,
            Venue venue,
            BookingPurpose purpose,
            String bookingTitle,
            LocalDate bookingDate,
            LocalTime bookingTime,
            CollegeEvent event,
            BookingStatus status) {
        this.bookingId = bookingId;
        this.bookedBy = bookedBy;
        this.venue = venue;
        this.purpose = purpose;
        this.bookingTitle = bookingTitle;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.event = event;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public User getBookedBy() {
        return bookedBy;
    }

    public Venue getVenue() {
        return venue;
    }

    public BookingPurpose getPurpose() {
        return purpose;
    }

    public String getBookingTitle() {
        return bookingTitle;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public CollegeEvent getEvent() {
        return event;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "%s | %s | %s | %s @ %s | %s %s | %s"
                .formatted(
                        bookingId,
                        bookedBy.getName(),
                        formatLabel(purpose.name()),
                        bookingTitle,
                        venue.name(),
                        bookingDate,
                        bookingTime,
                        formatLabel(status.name()));
    }

    private static String formatLabel(String value) {
        String formatted = value.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);
    }
}
