package com.collegeevent.features;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.collegeevent.localisation.MessageService;
import com.collegeevent.model.CollegeEvent;

public class EventLambdaService {

    public static void processEventsWithLambdas(List<CollegeEvent> events) {
        processEventsWithLambdas(events, new MessageService());
    }

    public static void processEventsWithLambdas(List<CollegeEvent> events, MessageService messageService) {

        Predicate<CollegeEvent> isSeminar =
                event -> event.getCategory().name().equalsIgnoreCase("SEMINAR");

        Function<CollegeEvent, String> getEventTitle =
                CollegeEvent::getTitle;

        Consumer<String> printTitle =
                title -> System.out.println("- " + title);

        Supplier<String> message =
                () -> messageService.getMessage("events.featured.seminars");

        System.out.println(message.get());

        events.stream()
                .filter(isSeminar)
                .map(getEventTitle)
                .forEach(printTitle);
    }
}
