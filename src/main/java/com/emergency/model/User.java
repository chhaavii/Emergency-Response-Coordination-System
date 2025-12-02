package com.emergency.model;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String phoneNumber;

    public User(String id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    // Abstract method for user actions
    public abstract void displayMenu();
}
