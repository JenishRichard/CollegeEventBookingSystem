package com.collegeevent.model;

public sealed interface User permits Admin, Student, Staff {
    String getId();
    String getName();
    String getUsername();
    String getPassword();
    String getEmail();
    String getRole();
}