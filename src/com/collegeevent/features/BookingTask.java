package com.collegeevent.features;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.Callable;

import com.collegeevent.model.BookingPurpose;
import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventBooking;
import com.collegeevent.model.User;
import com.collegeevent.model.Venue;
import com.collegeevent.service.BookingService;

public class BookingTask implements Callable<EventBooking> {

    private final BookingService bookingService;
    private final User user;
    private final CollegeEvent event;
    private final Venue venue;
    private final LocalDate bookingDate;
    private final LocalTime bookingTime;

    public BookingTask(
            BookingService bookingService,
            User user,
            CollegeEvent event,
            Venue venue,
            LocalDate bookingDate,
            LocalTime bookingTime) {
        this.bookingService = bookingService;
        this.user = user;
        this.event = event;
        this.venue = venue;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
    }

    @Override
    public EventBooking call() {
        return bookingService.createBooking(
                user,
                venue,
                BookingPurpose.EVENT,
                event.getTitle(),
                bookingDate,
                bookingTime,
                event
        );
    }
}
