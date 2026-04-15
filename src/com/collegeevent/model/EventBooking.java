package com.collegeevent.model;

public class EventBooking {
    private final String bookingId;
    private final User bookedBy;
    private final CollegeEvent event;
    private final Venue venue;
    private BookingStatus status;

    public EventBooking(String bookingId, User bookedBy, CollegeEvent event, Venue venue, BookingStatus status) {
        this.bookingId = bookingId;
        this.bookedBy = bookedBy;
        this.event = event;
        this.venue = venue;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public User getBookedBy() {
        return bookedBy;
    }

    public CollegeEvent getEvent() {
        return event;
    }

    public Venue getVenue() {
        return venue;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventBooking{id='%s', user='%s', event='%s', venue='%s', status=%s}"
                .formatted(bookingId, bookedBy.getName(), event.getTitle(), venue.name(), status);
    }
}