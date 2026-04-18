# College Event Booking System

This project is set up as an OOP2 assignment showcase for Java language fundamentals, advanced APIs, and Java 25 features. The app uses automatic locale detection with English fallback, so there is no language-selection menu.

## Feature coverage

### Fundamentals
- Sorting with `Comparator.comparing(...)` in `EventService` and `VenueService`
- Lambdas with `Consumer`, `Predicate`, `Supplier`, and `Function` in `EventService`, `BookingService`, and `EventLambdaService`
- Streams
- Terminal operations: `min`, `max`, `count`, `findAny`, `findFirst`, `allMatch`, `anyMatch`, `noneMatch`, `forEach`
- Collectors: `toMap`, `groupingBy`, `partitioningBy`
- Intermediate operations: `filter`, `distinct`, `limit`, `map`, `sorted`
- Switch expressions and pattern matching in `CollegeEventApp` and `UserUtils`
- Sealed interfaces via `User`
- Date/Time API via `LocalDate`, `Period`, `ChronoUnit`, and helper utilities
- Records via `Venue`

### Advanced
- Concurrency using `ExecutorService`, `Callable`, and `Future` in `ConcurrencyService`
- NIO2 file handling in `FileStorageService`
- Localisation with `ResourceBundle` in `MessageService`, automatic locale detection, and English/Irish bundles
- Java 25 compact source files and instance main methods in `src/CompactEventLauncher.java`
- Java 25 flexible constructor bodies in `BookingRequest`

### Extra marks
- Scoped Values in `UserContextManager`
- Stream Gatherers in `EventService#getEventTitleWindows`

## Run the main app

Use Java 25 because the project includes preview features.

```powershell
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
if (Test-Path target\classes) { Remove-Item target\classes -Recurse -Force }
New-Item -ItemType Directory -Path target\classes | Out-Null
javac --release 25 --enable-preview -d target\classes $files
Copy-Item src\*.properties target\classes
java --enable-preview -cp target\classes com.collegeevent.app.CollegeEventApp
```

To demonstrate localisation explicitly, you can run with an Irish JVM locale:

```powershell
java -Duser.language=ga -Duser.country=IE --enable-preview -cp target\classes com.collegeevent.app.CollegeEventApp
```

## Run the compact source file launcher

```powershell
java --enable-preview -cp target\classes CompactEventLauncher
```

## Maven note

`pom.xml` is configured for Java 25 preview features and for this repo's non-standard `src` source layout.
