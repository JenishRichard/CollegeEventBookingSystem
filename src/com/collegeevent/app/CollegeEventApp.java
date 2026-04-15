package com.collegeevent.app;

import com.collegeevent.features.ConcurrencyService;
import com.collegeevent.features.UserContextService.UserContextService;
import com.collegeevent.io.FileStorageService;
import com.collegeevent.localisation.MessageService;
import com.collegeevent.model.Admin;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

public class CollegeEventApp {

    public static void main(String[] args) {
        UserService userService = new UserService(SampleDataUtil.getUsers());
        VenueService venueService = new VenueService(SampleDataUtil.getVenues());
        EventService eventService = new EventService(SampleDataUtil.getEvents());
        BookingService bookingService = new BookingService();

        Scanner sc = new Scanner(System.in);
        MessageService messageService = new MessageService(Locale.ENGLISH);

        System.out.println("=== College Event Booking System ===");
        System.out.println(messageService.getMessage("welcome"));
        System.out.print("Enter User name: ");
        String username = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        Optional<User> loggedInUser = userService.login(username, password);

        if (loggedInUser.isEmpty()) {
            System.out.println(messageService.getMessage("invalid.login"));
            sc.close();
            return;
        }

        User currentUser = loggedInUser.get();

        UserContextService.setCurrentUser(currentUser.getUsername());

        System.out.println(messageService.getMessage("login.success") + ". Welcome " + currentUser.getName());

        if (currentUser.getRole().equals("ADMIN")) {
            showAdminMenu(sc, currentUser, userService, venueService, eventService, bookingService);
        } else {
            showUserMenu(sc, currentUser, venueService, eventService, bookingService);
        }

        UserContextService.clear();
        sc.close();
    }

    private static void showAdminMenu(Scanner sc, User currentUser, UserService userService,
                                      VenueService venueService, EventService eventService,
                                      BookingService bookingService) {
        int choice;

        do {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("User: " + UserContextService.getCurrentUser());
            System.out.println("1. Add Event");
            System.out.println("2. Add Venue");
            System.out.println("3. View Event");
            System.out.println("4. View Venue");
            System.out.println("5. View Booking");
            System.out.println("6. Cancel Booking");
            System.out.println("7. Add User");
            System.out.println("8. Run Concurrent Booking Simulation");
            System.out.println("9. Save Bookings to File");
            System.out.println("10. Read Bookings from File");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> {
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

                case 2 -> {
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

                case 3 -> eventService.printAllEvents();

                case 4 -> venueService.getAllVenues().forEach(System.out::println);

                case 5 -> bookingService.getAllBookings().forEach(System.out::println);

                case 6 -> {
                    System.out.print("Enter booking id: ");
                    String bookingId = sc.nextLine();
                    boolean cancelled = bookingService.cancelBooking(bookingId, currentUser);
                    System.out.println(cancelled ? "Booking cancelled." : "Booking not found.");
                }

                case 7 -> {
                    System.out.print("Enter role (ADMIN, STUDENT, STAFF): ");
                    String role = sc.nextLine().toUpperCase();
                    System.out.print("Enter user id: ");
                    String id = sc.nextLine();
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter username: ");
                    String uname = sc.nextLine();
                    System.out.print("Enter password: ");
                    String pass = sc.nextLine();
                    System.out.print("Enter email: ");
                    String email = sc.nextLine();

                    User user = switch (role) {
                        case "ADMIN" -> new Admin(id, name, uname, pass, email);
                        case "STUDENT" -> new Student(id, name, uname, pass, email);
                        case "STAFF" -> new Staff(id, name, uname, pass, email);
                        default -> null;
                    };

                    if (user != null) {
                        userService.addUser(user);
                        System.out.println("User added successfully.");
                    } else {
                        System.out.println("Invalid role.");
                    }
                }

                case 8 -> {
                    List<User> users = userService.getAllUsers().stream()
                            .filter(user -> !user.getRole().equals("ADMIN"))
                            .toList();

                    List<CollegeEvent> events = eventService.getAllEvents();
                    List<Venue> venues = venueService.getAllVenues();

                    if (users.isEmpty() || events.isEmpty() || venues.isEmpty()) {
                        System.out.println("Need users, events, and venues to run simulation.");
                        break;
                    }

                    ConcurrencyService concurrencyService = new ConcurrencyService();
                    List<EventBooking> results = concurrencyService.simulateConcurrentBookings(
                            bookingService,
                            users,
                            events.get(0),
                            venues.get(0)
                    );

                    System.out.println("Concurrent booking simulation completed:");
                    results.forEach(System.out::println);
                }

                case 9 -> {
                    FileStorageService fileStorageService = new FileStorageService();
                    fileStorageService.saveBookings(bookingService.getAllBookings());
                }

                case 10 -> {
                    FileStorageService fileStorageService = new FileStorageService();
                    fileStorageService.readBookingsFromFile();
                }

                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice.");
            }

        } while (choice != 0);
    }

    private static void showUserMenu(Scanner sc, User currentUser,
                                     VenueService venueService, EventService eventService,
                                     BookingService bookingService) {
        int choice;

        do {
            System.out.println("\n=== User Menu ===");
            System.out.println("User: " + UserContextService.getCurrentUser());
            System.out.println("1. View Event");
            System.out.println("2. View Venue");
            System.out.println("3. Create Booking");
            System.out.println("4. View My Booking");
            System.out.println("5. Cancel My Booking");
            System.out.println("6. View Upcoming Events");
            System.out.println("7. View Events Sorted by Title");
            System.out.println("8. View Venues Sorted by Capacity");
            System.out.println("9. Group Events by Category");
            System.out.println("10. Partition Events (Future/Past)");
            System.out.println("11. Map Events by ID");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> eventService.printAllEvents();

                case 2 -> venueService.getAllVenues().forEach(System.out::println);

                case 3 -> {
                    System.out.print("Enter event id: ");
                    String eventId = sc.nextLine();

                    List<CollegeEvent> events = eventService.getAllEvents();
                    CollegeEvent selectedEvent = events.stream()
                            .filter(event -> event.getEventId().equalsIgnoreCase(eventId))
                            .findFirst()
                            .orElse(null);

                    if (selectedEvent == null) {
                        System.out.println("Event not found.");
                        break;
                    }

                    System.out.print("Enter venue id: ");
                    String venueId = sc.nextLine();

                    List<Venue> venues = venueService.getAllVenues();
                    Venue selectedVenue = venues.stream()
                            .filter(venue -> venue.id().equalsIgnoreCase(venueId))
                            .findFirst()
                            .orElse(null);

                    if (selectedVenue == null) {
                        System.out.println("Venue not found.");
                        break;
                    }

                    EventBooking booking = bookingService.createBooking(currentUser, selectedEvent, selectedVenue);
                    System.out.println("Booking created successfully: " + booking);
                }

                case 4 -> bookingService.getBookingsByUser(currentUser).forEach(System.out::println);

                case 5 -> {
                    System.out.print("Enter booking id: ");
                    String bookingId = sc.nextLine();
                    boolean cancelled = bookingService.cancelBooking(bookingId, currentUser);
                    System.out.println(cancelled ? "Booking cancelled." : "Booking not found or not allowed.");
                }

                case 6 -> eventService.getUpcomingEvents().forEach(System.out::println);

                case 7 -> eventService.getEventsSortedByTitle().forEach(System.out::println);

                case 8 -> venueService.getVenuesSortedByCapacity().forEach(System.out::println);

                case 9 -> {
                    var grouped = eventService.groupEventsByCategory();
                    grouped.forEach((category, list) -> {
                        System.out.println("\n" + category + ":");
                        list.forEach(System.out::println);
                    });
                }

                case 10 -> {
                    var partitioned = eventService.partitionEventsByDate();

                    System.out.println("\nFuture Events:");
                    partitioned.get(true).forEach(System.out::println);

                    System.out.println("\nPast Events:");
                    partitioned.get(false).forEach(System.out::println);
                }

                case 11 -> {
                    var eventMap = eventService.mapEventsById();
                    eventMap.forEach((id, event) ->
                            System.out.println("ID: " + id + " -> " + event));
                }

                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice.");
            }

        } while (choice != 0);
    }
}