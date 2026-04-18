package com.collegeevent.advancedfeatures;

import java.lang.ScopedValue;

public class UserContextManager {

    private static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();

    public static void runWithUser(String username, Runnable action) {
        ScopedValue.where(CURRENT_USER, username).run(action);
    }

    public static String getCurrentUser() {
        return CURRENT_USER.orElse("anonymous");
    }
}