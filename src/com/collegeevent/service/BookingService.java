package com.collegeevent.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.collegeevent.model.BookingPurpose;
import com.collegeevent.model.BookingStatus;
import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventBooking;
import com.collegeevent.model.User;
import com.collegeevent.model.Venue;

public class BookingService {

    private final List<EventBooking> bookings = new CopyOnWriteArrayList<>();
    private final AtomicInteger bookingCounter = new AtomicInteger(1);

    private final Supplier<String> bookingIdSupplier =
            () -> "B" + bookingCounter.getAndIncrement();

    public EventBooking createBooking(User user, CollegeEvent event, Venue venue) {
        return createBooking(
                user,
                venue,
                BookingPurpose.EVENT,
                event.getTitle(),
                event.getDate(),
                LocalTime.of(10, 0),
                event
        );
    }

    public EventBooking createBooking(
            User user,
            Venue venue,
            BookingPurpose purpose,
            String bookingTitle,
            LocalDate bookingDate,
            LocalTime bookingTime,
            CollegeEvent linkedEvent) {
        validateBooking(venue, purpose, bookingTitle, bookingDate, bookingTime, linkedEvent);
        String bookingId = bookingIdSupplier.get();

        EventBooking booking = new EventBooking(
                bookingId,
                user,
                venue,
                purpose,
                bookingTitle,
                bookingDate,
                bookingTime,
                linkedEvent,
                BookingStatus.CONFIRMED
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

    public boolean isPurposeSupported(Venue venue, BookingPurpose purpose) {
        return switch (venue.type()) {
            case AUDITORIUM -> purpose == BookingPurpose.EVENT;
            case SEMINAR_HALL -> purpose == BookingPurpose.EVENT || purpose == BookingPurpose.CLASS;
            case CLASSROOM -> purpose == BookingPurpose.EVENT || purpose == BookingPurpose.CLASS;
            case LAB -> purpose == BookingPurpose.LAB;
        };
    }

    public LocalTime getNextAvailableTime(Venue venue, LocalDate bookingDate, LocalTime preferredTime) {
        LocalTime candidateTime = preferredTime;

        while (isVenueBookedAt(venue, bookingDate, candidateTime)) {
            candidateTime = candidateTime.plusHours(1);
        }

        return candidateTime;
    }

    private void validateBooking(
            Venue venue,
            BookingPurpose purpose,
            String bookingTitle,
            LocalDate bookingDate,
            LocalTime bookingTime,
            CollegeEvent linkedEvent) {
        if (venue == null) {
            throw new IllegalArgumentException("Venue is required.");
        }

        if (purpose == null) {
            throw new IllegalArgumentException("Booking purpose is required.");
        }

        if (bookingTitle == null || bookingTitle.isBlank()) {
            throw new IllegalArgumentException("Booking title is required.");
        }

        if (bookingDate == null) {
            throw new IllegalArgumentException("Booking date is required.");
        }

        if (bookingTime == null) {
            throw new IllegalArgumentException("Booking time is required.");
        }

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);

        if (bookingDate.isBefore(today)) {
            throw new IllegalArgumentException("Booking date cannot be in the past.");
        }

        if (bookingDate.equals(today) && bookingTime.isBefore(currentTime)) {
            throw new IllegalArgumentException(
                    "That time has already passed for today. Please choose a future time slot.");
        }

        if (!isPurposeSupported(venue, purpose)) {
            throw new IllegalArgumentException(
                    formatLabel(purpose.name()) + " bookings are not supported for "
                            + formatLabel(venue.type().name()) + ".");
        }

        if (purpose == BookingPurpose.EVENT) {
            if (linkedEvent == null) {
                throw new IllegalArgumentException("A valid event is required for an event booking.");
            }

            if (!bookingDate.equals(linkedEvent.getDate())) {
                throw new IllegalArgumentException(
                        "This event is scheduled for %s. Please use the event date."
                                .formatted(linkedEvent.getDate()));
            }
        }

        if (isVenueBookedAt(venue, bookingDate, bookingTime)) {
            throw new IllegalArgumentException("This venue is already booked for that time slot. Please try a different time slot.");
        }
    }

    private boolean isVenueBookedAt(Venue venue, LocalDate bookingDate, LocalTime bookingTime) {
        return bookings.stream()
                .filter(existingBooking -> existingBooking.getStatus() != BookingStatus.CANCELLED)
                .anyMatch(existingBooking ->
                        existingBooking.getVenue().id().equalsIgnoreCase(venue.id())
                                && existingBooking.getBookingDate().equals(bookingDate)
                                && existingBooking.getBookingTime().equals(bookingTime));
    }

    private String formatLabel(String value) {
        String formatted = value.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);
    }
}
