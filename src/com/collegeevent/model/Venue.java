package com.collegeevent.model;

public record Venue(String id, String name, int capacity, VenueType type) {

    @Override
    public String toString() {
        return "%s | %s | Capacity %d | %s"
                .formatted(id, name, capacity, formatLabel(type.name()));
    }

    private static String formatLabel(String value) {
        String formatted = value.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);
    }
}
