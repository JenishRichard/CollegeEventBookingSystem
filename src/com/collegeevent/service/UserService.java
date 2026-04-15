package com.collegeevent.service;

import com.collegeevent.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final List<User> users;

    public UserService(List<User> users) {
        this.users = new ArrayList<>(users);
    }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username)
                        && user.getPassword().equals(password))
                .findFirst();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}