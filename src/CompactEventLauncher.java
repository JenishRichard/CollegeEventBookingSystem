import java.util.stream.Collectors;

import com.collegeevent.util.SampleDataUtil;

void main() {
    System.out.println("Compact source file launcher");
    System.out.println(buildEventSummary());
}

String buildEventSummary() {
    return SampleDataUtil.getEvents().stream()
            .map(event -> event.getTitle() + " on " + event.getDate())
            .collect(Collectors.joining(System.lineSeparator()));
}
