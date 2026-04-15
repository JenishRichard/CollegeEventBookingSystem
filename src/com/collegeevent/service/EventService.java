package com.collegeevent.service;

import com.collegeevent.model.CollegeEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventService {
    private final List<CollegeEvent> events;

    public EventService(List<CollegeEvent> events) {
        this.events = new ArrayList<>(events);
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
    
    public void addEvent(CollegeEvent event) {
        events.add(event);
    }

    public List<CollegeEvent> getAllEvents() {
        return new ArrayList<>(events);
    }

    public List<CollegeEvent> getUpcomingEvents() {
        return events.stream()
                .filter(event -> !event.getDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(CollegeEvent::getDate))
                .collect(Collectors.toList());
    }
}