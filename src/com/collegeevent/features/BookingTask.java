package com.collegeevent.features;

import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventBooking;
import com.collegeevent.model.User;
import com.collegeevent.model.Venue;
import com.collegeevent.service.BookingService;

import java.util.concurrent.Callable;

public class BookingTask implements Callable<EventBooking> {

    private final BookingService bookingService;
    private final User user;
    private final CollegeEvent event;
    private final Venue venue;

    public BookingTask(BookingService bookingService, User user, CollegeEvent event, Venue venue) {
        this.bookingService = bookingService;
        this.user = user;
        this.event = event;
        this.venue = venue;
    }

    @Override
    public EventBooking call() {
        return bookingService.createBooking(user, event, venue);
    }
}