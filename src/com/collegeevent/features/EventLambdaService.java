package com.collegeevent.features;

import com.collegeevent.model.CollegeEvent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EventLambdaService {

    public static void processEventsWithLambdas(List<CollegeEvent> events) {

        Predicate<CollegeEvent> isSeminar =
                event -> event.getCategory().name().equalsIgnoreCase("SEMINAR");

        Function<CollegeEvent, String> getEventTitle =
                CollegeEvent::getTitle;

        Consumer<String> printTitle =
                title -> System.out.println("Event Title: " + title);

        Supplier<String> message =
                () -> "\n=== Processing Events Using Lambdas ===";

        System.out.println(message.get());

        events.stream()
                .filter(isSeminar)
                .map(getEventTitle)
                .forEach(printTitle);
    }
}