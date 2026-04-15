package com.collegeevent.util;

import com.collegeevent.model.Admin;
import com.collegeevent.model.CollegeEvent;
import com.collegeevent.model.EventCategory;
import com.collegeevent.model.Staff;
import com.collegeevent.model.Student;
import com.collegeevent.model.User;
import com.collegeevent.model.Venue;
import com.collegeevent.model.VenueType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SampleDataUtil {

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(new Admin("A1", "John", "admin", "admin123", "admin@college.com"));
        users.add(new Student("S1", "Arun", "student1", "stud123", "student@college.com"));
        users.add(new Staff("ST1", "Priya", "staff1", "staff123", "staff@college.com"));
        return users;
    }

    public static List<Venue> getVenues() {
        List<Venue> venues = new ArrayList<>();
        venues.add(new Venue("V1", "Room A101", 40, VenueType.CLASSROOM));
        venues.add(new Venue("V2", "Main Hall", 200, VenueType.AUDITORIUM));
        return venues;
    }

    public static List<CollegeEvent> getEvents() {
        List<CollegeEvent> events = new ArrayList<>();
        events.add(new CollegeEvent("E1", "Java Seminar", EventCategory.SEMINAR, LocalDate.now().plusDays(2)));
        events.add(new CollegeEvent("E2", "Dance Night", EventCategory.CULTURAL, LocalDate.now().plusDays(5)));
        return events;
    }
}