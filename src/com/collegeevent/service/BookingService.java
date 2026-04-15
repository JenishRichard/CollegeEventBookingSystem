package com.collegeevent.service;

import com.collegeevent.model.BookingStatus;
import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventBooking;
import com.collegeevent.model.User;
import com.collegeevent.model.Venue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BookingService {

    private final List<EventBooking> bookings = new ArrayList<>();
    private int bookingCounter = 1;
    public BookingService() {
        System.out.println("Booking Service Initialised");
    }
    private final Supplier<String> bookingIdSupplier =
            () -> "B" + bookingCounter++;

    public EventBooking createBooking(User user, CollegeEvent event, Venue venue) {
        String bookingId = bookingIdSupplier.get();

        EventBooking booking = new EventBooking(
                bookingId, user, event, venue, BookingStatus.PENDING
        );

        bookings.add(booking);
        return booking;
    }

    public List<EventBooking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public List<EventBooking> getBookingsByUser(User user) {
        return bookings.stream()
                .filter(b -> b.getBookedBy().getUsername()
                        .equalsIgnoreCase(user.getUsername()))
                .collect(Collectors.toList());
    }

    public boolean cancelBooking(String bookingId, User currentUser) {
        Optional<EventBooking> bookingOptional = bookings.stream()
                .filter(b -> b.getBookingId().equalsIgnoreCase(bookingId))
                .findFirst();

        if (bookingOptional.isPresent()) {
            EventBooking booking = bookingOptional.get();

            if (currentUser.getRole().equals("ADMIN") ||
                    booking.getBookedBy().getUsername()
                            .equalsIgnoreCase(currentUser.getUsername())) {
                booking.setStatus(BookingStatus.CANCELLED);
                return true;
            }
        }

        return false;
    }
}