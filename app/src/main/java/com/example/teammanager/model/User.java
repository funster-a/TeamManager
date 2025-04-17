package com.example.teammanager.model;

public class User {
    private String name;
    private String email;
    private Status status;
    private Role role;

    public enum Status {
        ACTIVE, BLOCKED
    }

    public enum Role {
        ADMIN, USER
    }

    public User() {}

    public User(String name, String email, Status status, Role role) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.role = role;
    }

    // Getters и setters...
}
