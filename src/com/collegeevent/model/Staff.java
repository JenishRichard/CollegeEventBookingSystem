package com.collegeevent.model;

public final class Staff implements User {
    private final String id;
    private final String name;
    private final String username;
    private final String password;
    private final String email;

    public Staff(String id, String name, String username, String password, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getRole() {
        return "STAFF";
    }

    @Override
    public String toString() {
        return "Staff{id='%s', name='%s', username='%s'}".formatted(id, name, username);
    }
}