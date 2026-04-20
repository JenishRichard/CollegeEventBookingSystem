package com.collegeevent.app;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
import com.collegeevent.model.BookingStatus;
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
            Locale selectedLocale = selectLocale(sc);
            MessageService messageService = new MessageService(selectedLocale);
  
            while (true) {
                System.out.println("=== " + messageService.getMessage("app.title") + " ===");
                System.out.println(messageService.getMessage("welcome"));
                System.out.println(messageService.getMessage("exit.hint"));
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

                System.out.println(messageService.getMessage("login.success.user", currentUser.getName()));
                System.out.println(UserUtils.describeUser(currentUser, messageService));
                UserContextManager.runWithUser(currentUser.getUsername(), () ->
                        System.out.println(messageService.getMessage("scoped.user") + ": "
                                + UserContextManager.getCurrentUser()));

                try {
                    if (currentUser.getRole().equals("ADMIN")) {
                        showAdminMenu(
                                sc,
                                currentUser,
                                userService,
                                venueService,
                                eventService,
                                bookingService,
                                messageService);
                    } else {
                        showUserMenu(sc, currentUser, venueService, eventService, bookingService, messageService);
                    }
                } finally {
                    UserContextService.clear();
                }

                System.out.println("\n" + messageService.getMessage("session.ended") + "\n");
            }
        }
    }

    private static Locale selectLocale(Scanner sc) {
        while (true) {
            System.out.println("Choose language / Roghnaigh teanga:");
            System.out.println("1. English");
            System.out.println("2. Irish");
            System.out.print("Enter choice / Iontráil rogha: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    return Locale.ENGLISH;
                }
                case "2" -> {
                    return Locale.forLanguageTag("ga-IE");
                }
                default -> System.out.println("Invalid choice / Rogha neamhbhailí. Please enter 1 or 2.");
            }
        }
    }

    private static void showAdminMenu(
            Scanner sc,
            User currentUser,
            UserService userService,
            VenueService venueService,
            EventService eventService,
            BookingService bookingService,
            MessageService messageService) {

        int choice = -1;

        do {
            System.out.println("\n=== " + messageService.getMessage("menu.admin.title") + " ===");
            System.out.println(messageService.getMessage("label.user") + ": " + UserContextService.getCurrentUser());
            System.out.println("1. " + messageService.getMessage("menu.admin.add.event"));
            System.out.println("2. " + messageService.getMessage("menu.admin.add.venue"));
            System.out.println("3. " + messageService.getMessage("menu.admin.add.user"));
            System.out.println("4. " + messageService.getMessage("menu.admin.manage.bookings"));
            System.out.println("5. " + messageService.getMessage("menu.admin.save.bookings"));
            System.out.println("6. " + messageService.getMessage("menu.admin.run.operations"));
            System.out.println("0. " + messageService.getMessage("menu.logout"));
            System.out.print(messageService.getMessage("prompt.choice"));

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(messageService.getMessage("invalid.input"));
                continue;
            }

            switch (choice) {
                case 1 -> addEvent(sc, eventService, messageService);
                case 2 -> addVenue(sc, venueService, messageService);
                case 3 -> addUser(sc, userService, messageService);
                case 4 -> manageAllBookings(sc, currentUser, bookingService, messageService);
                case 5 -> saveBookingRecords(bookingService, messageService);
                case 6 -> runBookingOperations(bookingService, userService, eventService, venueService, messageService);
                case 0 -> System.out.println(messageService.getMessage("logout.in.progress"));
                default -> System.out.println(messageService.getMessage("invalid.choice"));
            }

        } while (choice != 0);
    }

    private static void showUserMenu(
            Scanner sc,
            User currentUser,
            VenueService venueService,
            EventService eventService,
            BookingService bookingService,
            MessageService messageService) {

        int choice = -1;

        do {
            System.out.println("\n=== " + messageService.getMessage("menu.user.title") + " ===");
            System.out.println(messageService.getMessage("label.user") + ": " + UserContextService.getCurrentUser());
            System.out.println("1. " + messageService.getMessage("menu.user.browse.events"));
            System.out.println("2. " + messageService.getMessage("menu.user.browse.venues"));
            System.out.println("3. " + messageService.getMessage("menu.user.create.booking"));
            System.out.println("4. " + messageService.getMessage("menu.user.manage.bookings"));
            System.out.println("5. " + messageService.getMessage("menu.user.event.insights"));
            System.out.println("0. " + messageService.getMessage("menu.logout"));
            System.out.print(messageService.getMessage("prompt.choice"));

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(messageService.getMessage("invalid.input"));
                continue;
            }

            switch (choice) {
                case 1 -> showUpcomingEvents(eventService, messageService);
                case 2 -> showVenues(venueService, bookingService, messageService);
                case 3 -> createBooking(sc, currentUser, venueService, eventService, bookingService, messageService);
                case 4 -> manageUserBookings(sc, currentUser, bookingService, messageService);
                case 5 -> showEventInsights(currentUser, eventService, venueService, bookingService, messageService);
                case 0 -> System.out.println(messageService.getMessage("logout.in.progress"));
                default -> System.out.println(messageService.getMessage("invalid.choice"));
            }

        } while (choice != 0);
    }

    private static void addEvent(Scanner sc, EventService eventService, MessageService messageService) {
        System.out.print(messageService.getMessage("prompt.event.id"));
        String eventId = sc.nextLine();

        System.out.print(messageService.getMessage("prompt.event.title"));
        String title = sc.nextLine();

        System.out.print(messageService.getMessage("prompt.event.category"));
        EventCategory category = EventCategory.valueOf(sc.nextLine().toUpperCase());

        System.out.print(messageService.getMessage("prompt.event.date"));
        LocalDate date = LocalDate.parse(sc.nextLine());

        eventService.addEvent(new CollegeEvent(eventId, title, category, date));
        System.out.println(messageService.getMessage("event.added"));
    }

    private static void addVenue(Scanner sc, VenueService venueService, MessageService messageService) {
        System.out.print(messageService.getMessage("prompt.venue.id"));
        String venueId = sc.nextLine();

        System.out.print(messageService.getMessage("prompt.venue.name"));
        String name = sc.nextLine();

        System.out.print(messageService.getMessage("prompt.venue.capacity"));
        int capacity = Integer.parseInt(sc.nextLine());

        System.out.print(messageService.getMessage("prompt.venue.type"));
        VenueType type = VenueType.valueOf(sc.nextLine().toUpperCase());

        venueService.addVenue(new Venue(venueId, name, capacity, type));
        System.out.println(messageService.getMessage("venue.added"));
    }

    private static void addUser(Scanner sc, UserService userService, MessageService messageService) {
        System.out.print(messageService.getMessage("prompt.role"));
        String role = sc.nextLine().toUpperCase();

        System.out.print(messageService.getMessage("prompt.user.id"));
        String id = sc.nextLine();

        System.out.print(messageService.getMessage("prompt.name"));
        String name = sc.nextLine();

        System.out.print(messageService.getMessage("prompt.username"));
        String username = sc.nextLine();

        System.out.print(messageService.getMessage("prompt.password"));
        String password = sc.nextLine();

        System.out.print(messageService.getMessage("prompt.email"));
        String email = sc.nextLine();

        User user = switch (role) {
            case "ADMIN" -> new Admin(id, name, username, password, email);
            case "STUDENT" -> new Student(id, name, username, password, email);
            case "STAFF" -> new Staff(id, name, username, password, email);
            default -> null;
        };

        if (user != null) {
            userService.addUser(user);
            System.out.println(messageService.getMessage("user.added"));
        } else {
            System.out.println(messageService.getMessage("invalid.role"));
        }
    }

    private static void showUpcomingEvents(EventService eventService, MessageService messageService) {
        List<CollegeEvent> upcomingEvents = eventService.getUpcomingEvents();

        System.out.println("\n=== " + messageService.getMessage("event.catalogue.title") + " ===");
        if (upcomingEvents.isEmpty()) {
            System.out.println(messageService.getMessage("events.none.upcoming"));
            return;
        }

        upcomingEvents.forEach(event -> System.out.println(formatEvent(event, messageService)));

        List<CollegeEvent> highlightedEvents = eventService.getTop2Events();
        if (!highlightedEvents.isEmpty()) {
            System.out.println("\n" + messageService.getMessage("events.highlighted"));
            highlightedEvents.forEach(event -> System.out.println(formatEvent(event, messageService)));
        }
    }

    private static void showVenues(
            VenueService venueService,
            BookingService bookingService,
            MessageService messageService) {
        System.out.println("\n=== " + messageService.getMessage("venue.directory.title") + " ===");
        venueService.getVenuesSortedByCapacity().forEach(venue -> {
            System.out.println(formatVenue(venue, messageService));
            System.out.println("  " + messageService.getMessage("venue.suitable.for") + ": "
                    + describeSupportedPurposes(venue, bookingService, messageService));
        });
        System.out.println(messageService.getMessage(
                "venue.capacity.options",
                venueService.getUniqueSortedCapacities()));
    }

    private static void createBooking(
            Scanner sc,
            User currentUser,
            VenueService venueService,
            EventService eventService,
            BookingService bookingService,
            MessageService messageService) {

        System.out.println("\n=== " + messageService.getMessage("booking.create.title") + " ===");
        System.out.println(messageService.getMessage("venues.available"));
        venueService.getVenuesSortedByCapacity()
                .forEach(venue -> System.out.println(formatVenue(venue, messageService)));
        System.out.print(messageService.getMessage("prompt.venue.id"));
        String venueId = sc.nextLine();

        Venue selectedVenue = venueService.getAllVenues().stream()
                .filter(venue -> venue.id().equalsIgnoreCase(venueId))
                .findFirst()
                .orElse(null);

        if (selectedVenue == null) {
            System.out.println(messageService.getMessage("venue.not.found"));
            return;
        }

        BookingPurpose bookingPurpose = promptForBookingPurpose(sc, selectedVenue, bookingService, messageService);
        if (bookingPurpose == null) {
            System.out.println(messageService.getMessage("invalid.booking.type"));
            return;
        }

        try {
            String eventId = "";
            String bookingTitle = "";
            LocalDate bookingDate;
            LocalTime bookingTime;
            CollegeEvent selectedEvent = null;

            if (bookingPurpose == BookingPurpose.EVENT) {
                System.out.println("\n" + messageService.getMessage("events.available"));
                eventService.getUpcomingEvents()
                        .forEach(event -> System.out.println(formatEvent(event, messageService)));
                System.out.print(messageService.getMessage("prompt.event.id"));
                eventId = sc.nextLine();
                String selectedEventId = eventId;

                selectedEvent = eventService.getAllEvents().stream()
                        .filter(event -> event.getEventId().equalsIgnoreCase(selectedEventId))
                        .findFirst()
                        .orElse(null);

                if (selectedEvent == null) {
                    System.out.println(messageService.getMessage("event.not.found"));
                    return;
                }

                bookingDate = selectedEvent.getDate();
                System.out.println(messageService.getMessage("booking.date.uses.event", bookingDate));
                System.out.print(messageService.getMessage("prompt.booking.time"));
                bookingTime = LocalTime.parse(sc.nextLine());
            } else {
                System.out.print(messageService.getMessage("prompt.booking.date"));
                bookingDate = LocalDate.parse(sc.nextLine());

                System.out.print(messageService.getMessage("prompt.booking.time"));
                bookingTime = LocalTime.parse(sc.nextLine());

                System.out.print(messageService.getMessage("prompt.booking.title"));
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
            System.out.println(messageService.getMessage("booking.created", formatBooking(booking, messageService)));
        } catch (RuntimeException e) {
            System.out.println(messageService.getMessage("booking.create.failed", e.getMessage()));
        }
    }

    private static void manageUserBookings(
            Scanner sc,
            User currentUser,
            BookingService bookingService,
            MessageService messageService) {
        List<EventBooking> myBookings = bookingService.getBookingsByUser(currentUser);

        System.out.println("\n=== " + messageService.getMessage("booking.mine.title") + " ===");
        if (myBookings.isEmpty()) {
            System.out.println(messageService.getMessage("bookings.none.mine"));
            return;
        }

        myBookings.forEach(booking -> System.out.println(formatBooking(booking, messageService)));
        System.out.print("\n" + messageService.getMessage("prompt.cancel.booking"));
        String bookingId = sc.nextLine().trim();

        if (bookingId.isBlank()) {
            return;
        }

        boolean cancelled = bookingService.cancelBooking(bookingId, currentUser);
        System.out.println(cancelled
                ? messageService.getMessage("booking.cancelled")
                : messageService.getMessage("booking.not.found.or.not.allowed"));
    }

    private static void manageAllBookings(
            Scanner sc,
            User currentUser,
            BookingService bookingService,
            MessageService messageService) {
        List<EventBooking> allBookings = bookingService.getAllBookings();

        System.out.println("\n=== " + messageService.getMessage("booking.directory.title") + " ===");
        if (allBookings.isEmpty()) {
            System.out.println(messageService.getMessage("bookings.none.manage"));
            return;
        }

        allBookings.forEach(booking -> System.out.println(formatBooking(booking, messageService)));
        System.out.print("\n" + messageService.getMessage("prompt.cancel.booking"));
        String bookingId = sc.nextLine().trim();

        if (bookingId.isBlank()) {
            return;
        }

        boolean cancelled = bookingService.cancelBooking(bookingId, currentUser);
        System.out.println(cancelled
                ? messageService.getMessage("booking.cancelled")
                : messageService.getMessage("booking.not.found"));
    }

    private static void saveBookingRecords(BookingService bookingService, MessageService messageService) {
        System.out.println("\n=== " + messageService.getMessage("booking.save.title") + " ===");
        if (bookingService.getAllBookings().isEmpty()) {
            System.out.println(messageService.getMessage("bookings.none.save"));
            return;
        }

        FileStorageService fileStorageService = new FileStorageService();
        fileStorageService.saveBookings(bookingService.getAllBookings(), messageService);
    }

    private static void runBookingOperations(
            BookingService bookingService,
            UserService userService,
            EventService eventService,
            VenueService venueService,
            MessageService messageService) {

        System.out.println("\n=== " + messageService.getMessage("booking.operations.title") + " ===");
        List<User> users = userService.getAllUsers().stream()
                .filter(user -> !user.getRole().equals("ADMIN"))
                .toList();

        List<CollegeEvent> events = eventService.getAllEvents();
        List<Venue> venues = venueService.getAllVenues();

        if (users.isEmpty() || events.isEmpty() || venues.isEmpty()) {
            System.out.println(messageService.getMessage("booking.operations.missing.data"));
            return;
        }

        ConcurrencyService concurrencyService = new ConcurrencyService(messageService);
        List<EventBooking> results = concurrencyService.simulateConcurrentBookings(
                bookingService,
                users,
                events.get(0),
                venues.get(0)
        );

        System.out.println(messageService.getMessage("booking.operations.processed", results.size()));
        results.forEach(booking -> System.out.println(formatBooking(booking, messageService)));
    }

    private static void showEventInsights(
            User currentUser,
            EventService eventService,
            VenueService venueService,
            BookingService bookingService,
            MessageService messageService) {

        List<CollegeEvent> upcomingEvents = eventService.getUpcomingEvents();
        var eventsByCategory = eventService.groupEventsByCategory();
        var partitionedEvents = eventService.partitionEventsByDate();
        var eventLookup = eventService.mapEventsById();

        System.out.println("\n=== " + messageService.getMessage("event.insights.title") + " ===");
        System.out.println(messageService.getMessage(
                "profile.summary",
                UserUtils.describeUser(currentUser, messageService)));

        System.out.println("\n" + messageService.getMessage("upcoming.schedule"));
        if (upcomingEvents.isEmpty()) {
            System.out.println(messageService.getMessage("events.none.scheduled"));
        } else {
            upcomingEvents.forEach(event -> System.out.println(formatEvent(event, messageService)));
        }

        System.out.println("\n" + messageService.getMessage("planning.snapshot"));
        System.out.println("- " + messageService.getMessage("insight.total.events", eventService.countEvents()));
        System.out.println("- " + messageService.getMessage(
                "insight.upcoming.current.events",
                partitionedEvents.get(true).size()));
        System.out.println("- " + messageService.getMessage(
                "insight.past.events",
                partitionedEvents.get(false).size()));
        System.out.println("- " + messageService.getMessage(
                "insight.next.event",
                upcomingEvents.isEmpty()
                        ? messageService.getMessage("label.none")
                        : formatEvent(upcomingEvents.get(0), messageService)));
        System.out.println("- " + messageService.getMessage(
                "insight.latest.event",
                formatEventOrNone(eventService.getLatestEvent(), messageService)));
        System.out.println("- " + messageService.getMessage(
                "insight.first.event",
                formatEventOrNone(eventService.findFirstEvent(), messageService)));
        System.out.println("- " + messageService.getMessage(
                "insight.spotlight.event",
                formatEventOrNone(eventService.findAnyEvent(), messageService)));
        System.out.println("- " + messageService.getMessage(
                "insight.workshop.available",
                yesNo(eventService.anyWorkshopEvent(), messageService)));
        System.out.println("- " + messageService.getMessage(
                "insight.all.future",
                yesNo(eventService.allEventsAreFuture(), messageService)));
        System.out.println("- " + messageService.getMessage(
                "insight.past.exist",
                yesNo(!eventService.noPastEvents(), messageService)));

        System.out.println("\n" + messageService.getMessage("category.overview"));
        eventsByCategory.entrySet().stream()
                .sorted((first, second) -> first.getKey().compareTo(second.getKey()))
                .forEach(entry -> System.out.println(
                        "- " + formatCategory(entry.getKey(), messageService) + ": "
                                + messageService.getMessage("event.count", entry.getValue().size())));

        System.out.println("\n" + messageService.getMessage("venue.usage.guide"));
        venueService.getVenuesSortedByCapacity().forEach(venue ->
                System.out.println("- " + venue.name() + ": "
                        + describeSupportedPurposes(venue, bookingService, messageService)));

        System.out.println("\n" + messageService.getMessage("catalogue.details"));
        System.out.println("- " + messageService.getMessage(
                "event.codes",
                eventLookup.keySet().stream().sorted().toList()));
        System.out.println("- " + messageService.getMessage(
                "active.categories",
                eventService.getDistinctCategories().stream()
                        .map(category -> formatCategory(category, messageService))
                        .toList()));
        System.out.println("- " + messageService.getMessage(
                "venue.capacity.bands",
                venueService.getUniqueSortedCapacities()));

        List<CollegeEvent> highlightedEvents = eventService.getTop2Events();
        if (!highlightedEvents.isEmpty()) {
            System.out.println("\n" + messageService.getMessage("events.highlighted"));
            highlightedEvents.forEach(event -> System.out.println(formatEvent(event, messageService)));
        }

        System.out.println();
        EventLambdaService.processEventsWithLambdas(eventService.getAllEvents(), messageService);

        System.out.println("\n" + messageService.getMessage("schedule.batches"));
        eventService.getEventTitleWindows(2)
                .forEach(window -> System.out.println("- " + window));

        System.out.println("\n" + messageService.getMessage("timeline"));
        eventService.getAllEvents().stream()
                .sorted(Comparator.comparing(CollegeEvent::getDate))
                .map(event -> formatCountdown(event, messageService))
                .forEach(line -> System.out.println("- " + line));
    }

    private static BookingPurpose promptForBookingPurpose(
            Scanner sc,
            Venue selectedVenue,
            BookingService bookingService,
            MessageService messageService) {
        List<BookingPurpose> availablePurposes = List.of(BookingPurpose.values()).stream()
                .filter(purpose -> bookingService.isPurposeSupported(selectedVenue, purpose))
                .toList();

        System.out.println(messageService.getMessage("venue.selected", formatVenue(selectedVenue, messageService)));
        System.out.println(messageService.getMessage("booking.types.available"));
        for (int index = 0; index < availablePurposes.size(); index++) {
            System.out.println((index + 1) + ". " + formatPurpose(availablePurposes.get(index), messageService));
        }
        System.out.print(messageService.getMessage("prompt.booking.type"));

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

    private static String describeSupportedPurposes(
            Venue venue,
            BookingService bookingService,
            MessageService messageService) {
        return List.of(BookingPurpose.values()).stream()
                .filter(purpose -> bookingService.isPurposeSupported(venue, purpose))
                .map(purpose -> formatPurpose(purpose, messageService))
                .toList()
                .toString();
    }

    private static String yesNo(boolean value, MessageService messageService) {
        return value ? messageService.getMessage("label.yes") : messageService.getMessage("label.no");
    }

    private static String formatEvent(CollegeEvent event, MessageService messageService) {
        return messageService.getMessage(
                "event.display",
                event.getEventId(),
                event.getTitle(),
                formatCategory(event.getCategory(), messageService),
                event.getDate());
    }

    private static String formatEventOrNone(Optional<CollegeEvent> event, MessageService messageService) {
        return event.map(value -> formatEvent(value, messageService))
                .orElse(messageService.getMessage("label.none"));
    }

    private static String formatVenue(Venue venue, MessageService messageService) {
        return messageService.getMessage(
                "venue.display",
                venue.id(),
                venue.name(),
                venue.capacity(),
                formatVenueType(venue.type(), messageService));
    }

    private static String formatBooking(EventBooking booking, MessageService messageService) {
        return messageService.getMessage(
                "booking.display",
                booking.getBookingId(),
                booking.getBookedBy().getName(),
                formatPurpose(booking.getPurpose(), messageService),
                booking.getBookingTitle(),
                booking.getVenue().name(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                formatStatus(booking.getStatus(), messageService));
    }

    private static String formatCountdown(CollegeEvent event, MessageService messageService) {
        Period timeUntil = event.getTimeUntilEvent();
        long totalDays = ChronoUnit.DAYS.between(LocalDate.now(), event.getDate());

        if (totalDays >= 0) {
            return messageService.getMessage(
                    "event.countdown.future",
                    event.getTitle(),
                    totalDays,
                    timeUntil.getMonths(),
                    timeUntil.getDays());
        }

        return messageService.getMessage("event.countdown.past", event.getTitle(), Math.abs(totalDays));
    }

    private static String formatCategory(EventCategory category, MessageService messageService) {
        return formatCategory(category.name(), messageService);
    }

    private static String formatCategory(String category, MessageService messageService) {
        return messageService.getMessage("category." + category.toLowerCase());
    }

    private static String formatVenueType(VenueType type, MessageService messageService) {
        return messageService.getMessage("venue.type." + type.name().toLowerCase());
    }

    private static String formatPurpose(BookingPurpose purpose, MessageService messageService) {
        return messageService.getMessage("booking.purpose." + purpose.name().toLowerCase());
    }

    private static String formatStatus(BookingStatus status, MessageService messageService) {
        return messageService.getMessage("booking.status." + status.name().toLowerCase());
    }

}
