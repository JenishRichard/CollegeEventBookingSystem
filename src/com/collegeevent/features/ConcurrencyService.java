package com.collegeevent.features;

import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventBooking;
import com.collegeevent.model.User;
import com.collegeevent.model.Venue;
import com.collegeevent.service.BookingService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrencyService {

    public List<EventBooking> simulateConcurrentBookings(
            BookingService bookingService,
            List<User> users,
            CollegeEvent event,
            Venue venue
    ) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Callable<EventBooking>> tasks = new ArrayList<>();

        for (User user : users) {
            tasks.add(new BookingTask(bookingService, user, event, venue));
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