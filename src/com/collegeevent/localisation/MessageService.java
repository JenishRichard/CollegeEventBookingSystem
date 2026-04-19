package com.collegeevent.localisation;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class MessageService {

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final Locale IRISH_LOCALE = Locale.forLanguageTag("ga-IE");
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of(
            Locale.ENGLISH.getLanguage(),
            IRISH_LOCALE.getLanguage()
    );

    private final ResourceBundle messages;
    private final Locale activeLocale;

    public MessageService() {
        this(Locale.getDefault());
    }

    public MessageService(Locale locale) {
        this.activeLocale = resolveLocale(locale);
        this.messages = ResourceBundle.getBundle("messages", activeLocale);
    }

    public String getMessage(String key) {
        return messages.getString(key);
    }

    public String getMessage(String key, Object... values) {
        return getMessage(key).formatted(values);
    }

    public Locale getActiveLocale() {
        return activeLocale;
    }

    private Locale resolveLocale(Locale locale) {
        if (locale != null && SUPPORTED_LANGUAGES.contains(locale.getLanguage())) {
            return locale;
        }

        return DEFAULT_LOCALE;
    }
}
