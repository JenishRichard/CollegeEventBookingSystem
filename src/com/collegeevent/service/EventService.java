package com.collegeevent.service;

import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventCategory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public void printAllEvents() {
        Consumer<CollegeEvent> printEvent = event -> System.out.println(event);
        events.forEach(printEvent);
    }

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

    public List<CollegeEvent> getUpcomingEvents() {
        Predicate<CollegeEvent> isUpcoming =
                event -> !event.getDate().isBefore(LocalDate.now());

        return events.stream()
                .filter(isUpcoming)
                .sorted(Comparator.comparing(CollegeEvent::getDate))
                .toList();
    }

    public List<String> getAllEventTitles() {
        Function<CollegeEvent, String> getTitle = CollegeEvent::getTitle;

        return events.stream()
                .map(getTitle)
                .toList();
    }

    public Map<EventCategory, List<CollegeEvent>> groupEventsByCategory() {
        return events.stream()
                .collect(Collectors.groupingBy(CollegeEvent::getCategory));
    }

    public Map<Boolean, List<CollegeEvent>> partitionEventsByDate() {
        Predicate<CollegeEvent> isFuture =
                event -> event.getDate().isAfter(LocalDate.now());

        return events.stream()
                .collect(Collectors.partitioningBy(isFuture));
    }

    public Map<String, CollegeEvent> mapEventsById() {
        Function<CollegeEvent, String> getId = CollegeEvent::getEventId;

        return events.stream()
                .collect(Collectors.toMap(getId, event -> event));
    }
}