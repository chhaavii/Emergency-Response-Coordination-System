package com.emergency.model;

import com.emergency.service.EmergencyService;
import com.emergency.exception.InvalidEmergencyTypeException;

public class Citizen extends User {
    private String address;
    private EmergencyService emergencyService;

    public Citizen(String id, String name, String email, String phoneNumber, String address, EmergencyService emergencyService) {
        super(id, name, email, phoneNumber);
        this.address = address;
        this.emergencyService = emergencyService;
    }

    public void reportEmergency(String emergencyType, String description, String location, int severity) 
            throws InvalidEmergencyTypeException {
        System.out.println("\n[Citizen " + name + " is reporting a " + emergencyType + " emergency at " + location + "]");
        emergencyService.createEmergency(emergencyType, description, location, severity, this);
    }

    public void receiveConfirmation(String emergencyId) {
        System.out.println("\n[Citizen " + name + " received confirmation: Emergency " + emergencyId + " has been received and is being processed]");
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Citizen Menu ===");
        System.out.println("1. Report Emergency");
        System.out.println("2. View Reported Emergencies");
        System.out.println("3. Exit");
    }

    // Getters and setters
    public String getAddress() { return address; }
}
