package com.collegeevent.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.collegeevent.localisation.MessageService;
import com.collegeevent.model.EventBooking;

public class FileStorageService {

    private final Path bookingFilePath = Path.of("target", "data", "bookings.txt");

    public void saveBookings(List<EventBooking> bookings) {
        saveBookings(bookings, new MessageService());
    }

    public void saveBookings(List<EventBooking> bookings, MessageService messageService) {
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

            System.out.println(messageService.getMessage("bookings.saved", bookingFilePath.toAbsolutePath()));
        } catch (IOException e) {
            System.out.println(messageService.getMessage("bookings.save.error", e.getMessage()));
        }
    }

    public void readBookingsFromFile() {
        readBookingsFromFile(new MessageService());
    }

    public void readBookingsFromFile(MessageService messageService) {
        try {
            if (Files.exists(bookingFilePath)) {
                List<String> lines = Files.readAllLines(bookingFilePath, StandardCharsets.UTF_8);
                System.out.println(messageService.getMessage("bookings.saved.heading"));
                lines.forEach(System.out::println);
            } else {
                System.out.println(messageService.getMessage("bookings.file.missing"));
            }
        } catch (IOException e) {
            System.out.println(messageService.getMessage("bookings.read.error", e.getMessage()));
        }
    }
}
