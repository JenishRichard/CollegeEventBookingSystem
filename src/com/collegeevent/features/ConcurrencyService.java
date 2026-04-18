package com.collegeevent.features;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventBooking;
import com.collegeevent.model.User;
import com.collegeevent.model.Venue;
import com.collegeevent.service.BookingService;

public class ConcurrencyService {

    public List<EventBooking> simulateConcurrentBookings(
            BookingService bookingService,
            List<User> users,
            CollegeEvent event,
            Venue venue
    ) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Callable<EventBooking>> tasks = new ArrayList<>();
        LocalTime nextAvailableTime = LocalTime.of(10, 0);

        for (int index = 0; index < users.size(); index++) {
            User user = users.get(index);
            nextAvailableTime = bookingService.getNextAvailableTime(venue, event.getDate(), nextAvailableTime);
            tasks.add(new BookingTask(
                    bookingService,
                    user,
                    event,
                    venue,
                    event.getDate(),
                    nextAvailableTime
            ));
            nextAvailableTime = nextAvailableTime.plusHours(1);
        }

        List<EventBooking> results = new ArrayList<>();

        try {
            List<Future<EventBooking>> futures = executorService.invokeAll(tasks);

            for (Future<EventBooking> future : futures) {
                results.add(future.get());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Booking simulation interrupted.");
        } catch (ExecutionException e) {
            System.out.println("Error while processing booking task: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }

        return results;
    }
}
