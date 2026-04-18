package com.collegeevent.service;

import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventCategory;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

public class EventService {

    private final List<CollegeEvent> events;

    public EventService(List<CollegeEvent> events) {
        this.events = new ArrayList<>(events);
    }

    public void addEvent(CollegeEvent event) {
        events.add(event);
    }

    public List<CollegeEvent> getAllEvents() {
        return new ArrayList<>(events);
    }

    // forEach()
    public void printAllEvents() {
        Consumer<CollegeEvent> printEvent = event -> System.out.println(event);
        events.forEach(printEvent);
    }

    // sorting
    public List<CollegeEvent> getEventsSortedByTitle() {
        return events.stream()
                .sorted(Comparator.comparing(CollegeEvent::getTitle))
                .toList();
    }

    public List<CollegeEvent> getEventsSortedByDateDesc() {
        return events.stream()
                .sorted(Comparator.comparing(CollegeEvent::getDate).reversed())
                .toList();
    }

    // filter() + sorted()
    public List<CollegeEvent> getUpcomingEvents() {
        Predicate<CollegeEvent> isUpcoming =
                event -> !event.getDate().isBefore(LocalDate.now());

        return events.stream()
                .filter(isUpcoming)
                .sorted(Comparator.comparing(CollegeEvent::getDate))
                .toList();
    }

    // map()
    public List<String> getAllEventTitles() {
        Function<CollegeEvent, String> getTitle = CollegeEvent::getTitle;

        return events.stream()
                .map(getTitle)
                .toList();
    }

    // groupingBy()
    public Map<EventCategory, List<CollegeEvent>> groupEventsByCategory() {
        return events.stream()
                .collect(Collectors.groupingBy(CollegeEvent::getCategory));
    }

    // partitioningBy()
    public Map<Boolean, List<CollegeEvent>> partitionEventsByDate() {
        Predicate<CollegeEvent> isFuture =
                event -> !event.getDate().isBefore(LocalDate.now());

        return events.stream()
                .collect(Collectors.partitioningBy(isFuture));
    }

    // toMap()
    public Map<String, CollegeEvent> mapEventsById() {
        Function<CollegeEvent, String> getId = CollegeEvent::getEventId;

        return events.stream()
                .collect(Collectors.toMap(getId, event -> event));
    }

    // distinct()
    public List<String> getDistinctCategories() {
        return events.stream()
                .map(e -> e.getCategory().name())
                .distinct()
                .toList();
    }

    // limit()
    public List<CollegeEvent> getTop2Events() {
        return events.stream()
                .filter(event -> !event.getDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(CollegeEvent::getDate))
                .limit(2)
                .toList();
    }

    // min()
    public Optional<CollegeEvent> getEarliestEvent() {
        return events.stream()
                .min(Comparator.comparing(CollegeEvent::getDate));
    }

    // max()
    public Optional<CollegeEvent> getLatestEvent() {
        return events.stream()
                .max(Comparator.comparing(CollegeEvent::getDate));
    }

    // findAny()
    public Optional<CollegeEvent> findAnyEvent() {
        return events.stream().findAny();
    }

    // findFirst()
    public Optional<CollegeEvent> findFirstEvent() {
        return events.stream().findFirst();
    }

    // allMatch()
    public boolean allEventsAreFuture() {
        return events.stream()
                .allMatch(e -> e.getDate().isAfter(LocalDate.now()));
    }

    // anyMatch()
    public boolean anyWorkshopEvent() {
        return events.stream()
                .anyMatch(e -> e.getCategory() == EventCategory.WORKSHOP);
    }

    // noneMatch()
    public boolean noPastEvents() {
        return events.stream()
                .noneMatch(e -> e.getDate().isBefore(LocalDate.now()));
    }

    // count()
    public long countEvents() {
        return events.stream().count();
    }

    public List<String> getEventTitleWindows(int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be greater than zero.");
        }

        return events.stream()
                .sorted(Comparator.comparing(CollegeEvent::getDate))
                .gather(Gatherers.windowFixed(windowSize))
                .map(window -> window.stream()
                        .map(CollegeEvent::getTitle)
                        .collect(Collectors.joining(" | ")))
                .toList();
    }

    public List<String> getEventCountdowns() {
        return events.stream()
                .sorted(Comparator.comparing(CollegeEvent::getDate))
                .map(event -> {
                    Period timeUntil = event.getTimeUntilEvent();
                    long totalDays = ChronoUnit.DAYS.between(LocalDate.now(), event.getDate());

                    if (totalDays >= 0) {
                        return "%s -> in %d day(s) (%d month(s), %d day(s))"
                                .formatted(
                                        event.getTitle(),
                                        totalDays,
                                        timeUntil.getMonths(),
                                        timeUntil.getDays());
                    }

                    return "%s -> happened %d day(s) ago"
                            .formatted(event.getTitle(), Math.abs(totalDays));
                })
                .toList();
    }
}
