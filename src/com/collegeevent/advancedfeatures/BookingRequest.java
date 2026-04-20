package com.collegeevent.advancedfeatures;

import java.time.LocalDate;
import java.time.LocalTime;

import com.collegeevent.model.BookingPurpose;

public class BookingRequest {

    private final String venueId;
    private final BookingPurpose bookingPurpose;
    private final LocalDate bookingDate;
    private final LocalTime bookingTime;
    private final String eventId;
    private final String bookingTitle;

    public BookingRequest(
            String venueId,
            BookingPurpose bookingPurpose,
            LocalDate bookingDate,
            LocalTime bookingTime,
            String eventId,
            String bookingTitle) {
        venueId = venueId == null ? "" : venueId.trim();
        eventId = eventId == null ? "" : eventId.trim();
        bookingTitle = bookingTitle == null ? "" : bookingTitle.trim();

        if (venueId.isBlank()) {
            throw new IllegalArgumentException("Venue ID cannot be empty");
        }

        if (bookingPurpose == null) {
            throw new IllegalArgumentException("Booking purpose is required");
        }

        if (bookingDate == null) {
            throw new IllegalArgumentException("Booking date is required");
        }

          if (bookingTime == null) {
            throw new IllegalArgumentException("Booking time is required");
        }

        if (bookingPurpose == BookingPurpose.EVENT && eventId.isBlank()) {
            throw new IllegalArgumentException("Event ID cannot be empty for an event booking");
        }

        if (bookingPurpose != BookingPurpose.EVENT && bookingTitle.isBlank()) {
            throw new IllegalArgumentException("Booking title cannot be empty");
        }

        this(venueId, bookingPurpose, bookingDate, bookingTime, eventId, bookingTitle, true);
    }

    private BookingRequest(
            String venueId,
            BookingPurpose bookingPurpose,
            LocalDate bookingDate,
            LocalTime bookingTime,
            String eventId,
            String bookingTitle,
            boolean validated) {
        this.venueId = venueId;
        this.bookingPurpose = bookingPurpose;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.eventId = eventId;
        this.bookingTitle = bookingTitle;
    }

    public String getVenueId() {
        return venueId;
    }

    public BookingPurpose getBookingPurpose() {
        return bookingPurpose;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public String getEventId() {
        return eventId;
    }

    public String getBookingTitle() {
        return bookingTitle;
    }
}
