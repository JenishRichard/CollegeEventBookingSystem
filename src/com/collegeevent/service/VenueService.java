package com.collegeevent.service;

import com.collegeevent.model.Venue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VenueService {
    private final List<Venue> venues;

    public VenueService(List<Venue> venues) {
        this.venues = new ArrayList<>(venues);
    }

    public void addVenue(Venue venue) {
        venues.add(venue);
    }

    public List<Venue> getAllVenues() {
        return new ArrayList<>(venues);
    }

    public List<Venue> getVenuesSortedByCapacity() {
        return venues.stream()
                .sorted(Comparator.comparing(Venue::capacity))
                .toList();
    }
}