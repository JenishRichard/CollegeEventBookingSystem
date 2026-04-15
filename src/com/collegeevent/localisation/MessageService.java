package com.collegeevent.localisation;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageService {

    private final ResourceBundle messages;

    public MessageService(Locale locale) {
        this.messages = ResourceBundle.getBundle("messages", locale);
    }

    public String getMessage(String key) {
        return messages.getString(key);
    }
}