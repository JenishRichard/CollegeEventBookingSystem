package com.collegeevent.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.collegeevent.model.EventBooking;

public class FileStorageService {

    private final Path bookingFilePath = Path.of("target", "data", "bookings.txt");

    public void saveBookings(List<EventBooking> bookings) {
        try {
            List<String> lines = bookings.stream()
                    .map(EventBooking::toString)
                    .toList();

            Files.createDirectories(bookingFilePath.getParent());
            Files.write(
                    bookingFilePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println("Bookings saved to " + bookingFilePath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    public void readBookingsFromFile() {
        try {
            if (Files.exists(bookingFilePath)) {
                List<String> lines = Files.readAllLines(bookingFilePath, StandardCharsets.UTF_8);
                System.out.println("Saved Bookings:");
                lines.forEach(System.out::println);
            } else {
                System.out.println("Booking file does not exist.");
            }
        } catch (IOException e) {
            System.out.println("Error reading bookings: " + e.getMessage());
        }
    }
}
