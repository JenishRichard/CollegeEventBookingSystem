package com.collegeevent.app;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.collegeevent.advancedfeatures.BookingRequest;
import com.collegeevent.advancedfeatures.UserContextManager;
import com.collegeevent.features.ConcurrencyService;
import com.collegeevent.features.EventLambdaService;
import com.collegeevent.features.usercontext.UserContextService;
import com.collegeevent.io.FileStorageService;
import com.collegeevent.localisation.MessageService;
import com.collegeevent.model.Admin;
import com.collegeevent.model.BookingPurpose;
import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventBooking;
import com.collegeevent.model.EventCategory;
import com.collegeevent.model.Staff;
import com.collegeevent.model.Student;
import com.collegeevent.model.User;
import com.collegeevent.model.Venue;
import com.collegeevent.model.VenueType;
import com.collegeevent.service.BookingService;
import com.collegeevent.service.EventService;
import com.collegeevent.service.UserService;
import com.collegeevent.service.VenueService;
import com.collegeevent.util.SampleDataUtil;
import com.collegeevent.util.UserUtils;

public class CollegeEventApp {

    public static void main(String[] args) {
        UserService userService = new UserService(SampleDataUtil.getUsers());
        VenueService venueService = new VenueService(SampleDataUtil.getVenues());
        EventService eventService = new EventService(SampleDataUtil.getEvents());
        BookingService bookingService = new BookingService();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                MessageService messageService = new MessageService();

                System.out.println("=== College Event Booking System ===");
                System.out.println(messageService.getMessage("welcome"));
                System.out.println("Enter EXIT at the username prompt to close the application.");
                System.out.print(messageService.getMessage("prompt.username"));
                String username = sc.nextLine().trim();

                if (username.equalsIgnoreCase("EXIT")) {
                    System.out.println(messageService.getMessage("farewell"));
                    break;
                }

                System.out.print(messageService.getMessage("prompt.password"));
                String password = sc.nextLine();

                Optional<User> loggedInUser = userService.login(username, password);

                if (loggedInUser.isEmpty()) {
                    System.out.println(messageService.getMessage("invalid.login"));
                    System.out.println();
                    continue;
                }

                User currentUser = loggedInUser.get();
                UserContextService.setCurrentUser(currentUser.getUsername());

                System.out.println(messageService.getMessage("login.success") + ". Welcome " + currentUser.getName());
                System.out.println(UserUtils.describeUser(currentUser));
                UserContextManager.runWithUser(currentUser.getUsername(), () ->
                        System.out.println(messageService.getMessage("scoped.user") + ": "
                                + UserContextManager.getCurrentUser()));

                try {
                    if (currentUser.getRole().equals("ADMIN")) {
                        showAdminMenu(sc, currentUser, userService, venueService, eventService, bookingService);
                    } else {
                        showUserMenu(sc, currentUser, venueService, eventService, bookingService);
                    }
                } finally {
                    UserContextService.clear();
                }

                System.out.println("\nSession ended. Returning to the login page...\n");
            }
        }
    }

    private static void showAdminMenu(
            Scanner sc,
            User currentUser,
            UserService userService,
            VenueService venueService,
            EventService eventService,
            BookingService bookingService) {

        int choice = -1;

        do {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("User: " + UserContextService.getCurrentUser());
            System.out.println("1. Add Event");
            System.out.println("2. Add Venue");
            System.out.println("3. Add User");
            System.out.println("4. Manage Bookings");
            System.out.println("5. Save Booking Records");
            System.out.println("6. Run Booking Operations");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            switch (choice) {
                case 1 -> addEvent(sc, eventService);
                case 2 -> addVenue(sc, venueService);
                case 3 -> addUser(sc, userService);
                case 4 -> manageAllBookings(sc, currentUser, bookingService);
                case 5 -> saveBookingRecords(bookingService);
                case 6 -> runBookingOperations(bookingService, userService, eventService, venueService);
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }

        } while (choice != 0);
    }

    private static void showUserMenu(
            Scanner sc,
            User currentUser,
            VenueService venueService,
            EventService eventService,
            BookingService bookingService) {

        int choice = -1;

        do {
            System.out.println("\n=== User Menu ===");
            System.out.println("User: " + UserContextService.getCurrentUser());
            System.out.println("1. Browse Events");
            System.out.println("2. Browse Venues");
            System.out.println("3. Create Booking");
            System.out.println("4. Manage My Bookings");
            System.out.println("5. View Event Insights");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            switch (choice) {
                case 1 -> showUpcomingEvents(eventService);
                case 2 -> showVenues(venueService, bookingService);
                case 3 -> createBooking(sc, currentUser, venueService, eventService, bookingService);
                case 4 -> manageUserBookings(sc, currentUser, bookingService);
                case 5 -> showEventInsights(currentUser, eventService, venueService, bookingService);
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }

        } while (choice != 0);
    }

    private static void addEvent(Scanner sc, EventService eventService) {
        System.out.print("Enter event id: ");
        String eventId = sc.nextLine();

        System.out.print("Enter event title: ");
        String title = sc.nextLine();

        System.out.print("Enter category (SEMINAR, WORKSHOP, CULTURAL, SPORTS): ");
        EventCategory category = EventCategory.valueOf(sc.nextLine().toUpperCase());

        System.out.print("Enter event date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(sc.nextLine());

        eventService.addEvent(new CollegeEvent(eventId, title, category, date));
        System.out.println("Event added successfully.");
    }

    private static void addVenue(Scanner sc, VenueService venueService) {
        System.out.print("Enter venue id: ");
        String venueId = sc.nextLine();

        System.out.print("Enter venue name: ");
        String name = sc.nextLine();

        System.out.print("Enter capacity: ");
        int capacity = Integer.parseInt(sc.nextLine());

        System.out.print("Enter venue type (CLASSROOM, SEMINAR_HALL, AUDITORIUM, LAB): ");
        VenueType type = VenueType.valueOf(sc.nextLine().toUpperCase());

        venueService.addVenue(new Venue(venueId, name, capacity, type));
        System.out.println("Venue added successfully.");
    }

    private static void addUser(Scanner sc, UserService userService) {
        System.out.print("Enter role (ADMIN, STUDENT, STAFF): ");
        String role = sc.nextLine().toUpperCase();

        System.out.print("Enter user id: ");
        String id = sc.nextLine();

        System.out.print("Enter name: ");
        String name = sc.nextLine();

        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        User user = switch (role) {
            case "ADMIN" -> new Admin(id, name, username, password, email);
            case "STUDENT" -> new Student(id, name, username, password, email);
            case "STAFF" -> new Staff(id, name, username, password, email);
            default -> null;
        };

        if (user != null) {
            userService.addUser(user);
            System.out.println("User added successfully.");
        } else {
            System.out.println("Invalid role.");
        }
    }

    private static void showUpcomingEvents(EventService eventService) {
        List<CollegeEvent> upcomingEvents = eventService.getUpcomingEvents();

        System.out.println("\n=== Event Catalogue ===");
        if (upcomingEvents.isEmpty()) {
            System.out.println("No upcoming events are available.");
            return;
        }

        upcomingEvents.forEach(System.out::println);

        List<CollegeEvent> highlightedEvents = eventService.getTop2Events();
        if (!highlightedEvents.isEmpty()) {
            System.out.println("\nHighlighted events:");
            highlightedEvents.forEach(System.out::println);
        }
    }

    private static void showVenues(VenueService venueService, BookingService bookingService) {
        System.out.println("\n=== Venue Directory ===");
        venueService.getVenuesSortedByCapacity().forEach(venue -> {
            System.out.println(venue);
            System.out.println("  Suitable for: " + describeSupportedPurposes(venue, bookingService));
        });
        System.out.println("Capacity options: " + venueService.getUniqueSortedCapacities());
    }

    private static void createBooking(
            Scanner sc,
            User currentUser,
            VenueService venueService,
            EventService eventService,
            BookingService bookingService) {

        System.out.println("\n=== Create Booking ===");
        System.out.println("Available venues:");
        venueService.getVenuesSortedByCapacity().forEach(System.out::println);
        System.out.print("Enter venue id: ");
        String venueId = sc.nextLine();

        Venue selectedVenue = venueService.getAllVenues().stream()
                .filter(venue -> venue.id().equalsIgnoreCase(venueId))
                .findFirst()
                .orElse(null);

        if (selectedVenue == null) {
            System.out.println("Venue not found.");
            return;
        }

        BookingPurpose bookingPurpose = promptForBookingPurpose(sc, selectedVenue, bookingService);
        if (bookingPurpose == null) {
            System.out.println("Invalid booking type.");
            return;
        }

        try {
            String eventId = "";
            String bookingTitle = "";
            LocalDate bookingDate;
            LocalTime bookingTime;
            CollegeEvent selectedEvent = null;

            if (bookingPurpose == BookingPurpose.EVENT) {
                System.out.println("\nAvailable events:");
                eventService.getUpcomingEvents().forEach(System.out::println);
                System.out.print("Enter event id: ");
                eventId = sc.nextLine();
                String selectedEventId = eventId;

                selectedEvent = eventService.getAllEvents().stream()
                        .filter(event -> event.getEventId().equalsIgnoreCase(selectedEventId))
                        .findFirst()
                        .orElse(null);

                if (selectedEvent == null) {
                    System.out.println("Event not found.");
                    return;
                }

                bookingDate = selectedEvent.getDate();
                System.out.println("Booking date set to event date: " + bookingDate);
                System.out.print("Enter booking time (HH:MM): ");
                bookingTime = LocalTime.parse(sc.nextLine());
            } else {
                System.out.print("Enter booking date (YYYY-MM-DD): ");
                bookingDate = LocalDate.parse(sc.nextLine());

                System.out.print("Enter booking time (HH:MM): ");
                bookingTime = LocalTime.parse(sc.nextLine());

                System.out.print("Enter booking title: ");
                bookingTitle = sc.nextLine();
            }

            BookingRequest bookingRequest = new BookingRequest(
                    venueId,
                    bookingPurpose,
                    bookingDate,
                    bookingTime,
                    eventId,
                    bookingTitle
            );

            String finalBookingTitle = bookingRequest.getBookingTitle();

            if (bookingRequest.getBookingPurpose() == BookingPurpose.EVENT) {
                finalBookingTitle = selectedEvent.getTitle();
            }

            EventBooking booking = bookingService.createBooking(
                    currentUser,
                    selectedVenue,
                    bookingRequest.getBookingPurpose(),
                    finalBookingTitle,
                    bookingRequest.getBookingDate(),
                    bookingRequest.getBookingTime(),
                    selectedEvent
            );
            System.out.println("Booking created successfully: " + booking);
        } catch (RuntimeException e) {
            System.out.println("Booking could not be created: " + e.getMessage());
        }
    }

    private static void manageUserBookings(Scanner sc, User currentUser, BookingService bookingService) {
        List<EventBooking> myBookings = bookingService.getBookingsByUser(currentUser);

        System.out.println("\n=== My Bookings ===");
        if (myBookings.isEmpty()) {
            System.out.println("You do not have any bookings yet.");
            return;
        }

        myBookings.forEach(System.out::println);
        System.out.print("\nEnter a booking id to cancel, or press Enter to return: ");
        String bookingId = sc.nextLine().trim();

        if (bookingId.isBlank()) {
            return;
        }

        boolean cancelled = bookingService.cancelBooking(bookingId, currentUser);
        System.out.println(cancelled ? "Booking cancelled." : "Booking not found or not allowed.");
    }

    private static void manageAllBookings(Scanner sc, User currentUser, BookingService bookingService) {
        List<EventBooking> allBookings = bookingService.getAllBookings();

        System.out.println("\n=== Booking Directory ===");
        if (allBookings.isEmpty()) {
            System.out.println("There are no bookings to manage.");
            return;
        }

        allBookings.forEach(System.out::println);
        System.out.print("\nEnter a booking id to cancel, or press Enter to return: ");
        String bookingId = sc.nextLine().trim();

        if (bookingId.isBlank()) {
            return;
        }

        boolean cancelled = bookingService.cancelBooking(bookingId, currentUser);
        System.out.println(cancelled ? "Booking cancelled." : "Booking not found.");
    }

    private static void saveBookingRecords(BookingService bookingService) {
        System.out.println("\n=== Save Booking Records ===");
        if (bookingService.getAllBookings().isEmpty()) {
            System.out.println("There are no bookings to save.");
            return;
        }

        FileStorageService fileStorageService = new FileStorageService();
        fileStorageService.saveBookings(bookingService.getAllBookings());
    }

    private static void runBookingOperations(
            BookingService bookingService,
            UserService userService,
            EventService eventService,
            VenueService venueService) {

        System.out.println("\n=== Booking Operations ===");
        List<User> users = userService.getAllUsers().stream()
                .filter(user -> !user.getRole().equals("ADMIN"))
                .toList();

        List<CollegeEvent> events = eventService.getAllEvents();
        List<Venue> venues = venueService.getAllVenues();

        if (users.isEmpty() || events.isEmpty() || venues.isEmpty()) {
            System.out.println("Users, events, and venues are required before processing bookings.");
            return;
        }

        ConcurrencyService concurrencyService = new ConcurrencyService();
        List<EventBooking> results = concurrencyService.simulateConcurrentBookings(
                bookingService,
                users,
                events.get(0),
                venues.get(0)
        );

        System.out.println("Processed " + results.size() + " booking request(s) in parallel.");
        results.forEach(System.out::println);
    }

    private static void showEventInsights(
            User currentUser,
            EventService eventService,
            VenueService venueService,
            BookingService bookingService) {

        List<CollegeEvent> upcomingEvents = eventService.getUpcomingEvents();
        var eventsByCategory = eventService.groupEventsByCategory();
        var partitionedEvents = eventService.partitionEventsByDate();
        var eventLookup = eventService.mapEventsById();

        System.out.println("\n=== Event Insights ===");
        System.out.println("Profile: " + UserUtils.describeUser(currentUser));

        System.out.println("\nUpcoming schedule:");
        if (upcomingEvents.isEmpty()) {
            System.out.println("No upcoming events are scheduled.");
        } else {
            upcomingEvents.forEach(System.out::println);
        }

        System.out.println("\nPlanning snapshot:");
        System.out.println("- Total events: " + eventService.countEvents());
        System.out.println("- Upcoming or current events: " + partitionedEvents.get(true).size());
        System.out.println("- Past events: " + partitionedEvents.get(false).size());
        System.out.println("- Next event: " + (upcomingEvents.isEmpty() ? "None" : upcomingEvents.get(0)));
        System.out.println("- Latest scheduled event: " + eventService.getLatestEvent().orElse(null));
        System.out.println("- First listed event: " + eventService.findFirstEvent().orElse(null));
        System.out.println("- Spotlight event: " + eventService.findAnyEvent().orElse(null));
        System.out.println("- Workshop available: " + yesNo(eventService.anyWorkshopEvent()));
        System.out.println("- All events are future dated: " + yesNo(eventService.allEventsAreFuture()));
        System.out.println("- Past events already exist: " + yesNo(!eventService.noPastEvents()));

        System.out.println("\nCategory overview:");
        eventsByCategory.entrySet().stream()
                .sorted((first, second) -> first.getKey().compareTo(second.getKey()))
                .forEach(entry -> System.out.println(
                        "- " + formatLabel(entry.getKey().name()) + ": " + entry.getValue().size() + " event(s)"));

        System.out.println("\nVenue usage guide:");
        venueService.getVenuesSortedByCapacity().forEach(venue ->
                System.out.println("- " + venue.name() + ": " + describeSupportedPurposes(venue, bookingService)));

        System.out.println("\nCatalogue details:");
        System.out.println("- Event codes: " + eventLookup.keySet().stream().sorted().toList());
        System.out.println("- Active categories: "
                + eventService.getDistinctCategories().stream().map(CollegeEventApp::formatLabel).toList());
        System.out.println("- Venue capacity bands: " + venueService.getUniqueSortedCapacities());

        List<CollegeEvent> highlightedEvents = eventService.getTop2Events();
        if (!highlightedEvents.isEmpty()) {
            System.out.println("\nHighlighted events:");
            highlightedEvents.forEach(System.out::println);
        }

        System.out.println();
        EventLambdaService.processEventsWithLambdas(eventService.getAllEvents());

        System.out.println("\nSchedule batches:");
        eventService.getEventTitleWindows(2)
                .forEach(window -> System.out.println("- " + window));

        System.out.println("\nTimeline:");
        eventService.getEventCountdowns().forEach(line -> System.out.println("- " + line));
    }

    private static BookingPurpose promptForBookingPurpose(
            Scanner sc,
            Venue selectedVenue,
            BookingService bookingService) {
        List<BookingPurpose> availablePurposes = List.of(BookingPurpose.values()).stream()
                .filter(purpose -> bookingService.isPurposeSupported(selectedVenue, purpose))
                .toList();

        System.out.println("Selected venue: " + selectedVenue);
        System.out.println("Available booking types:");
        for (int index = 0; index < availablePurposes.size(); index++) {
            System.out.println((index + 1) + ". " + formatLabel(availablePurposes.get(index).name()));
        }
        System.out.print("Choose booking type: ");

        try {
            int selectedIndex = Integer.parseInt(sc.nextLine());
            if (selectedIndex >= 1 && selectedIndex <= availablePurposes.size()) {
                return availablePurposes.get(selectedIndex - 1);
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    private static String describeSupportedPurposes(Venue venue, BookingService bookingService) {
        return List.of(BookingPurpose.values()).stream()
                .filter(purpose -> bookingService.isPurposeSupported(venue, purpose))
                .map(purpose -> formatLabel(purpose.name()))
                .toList()
                .toString();
    }

    private static String yesNo(boolean value) {
        return value ? "Yes" : "No";
    }

    private static String formatLabel(String value) {
        String formatted = value.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);
    }
}
