package com.emergency.model;

import com.emergency.service.EmergencyService;
import com.emergency.model.emergency.Emergency;

public class Responder extends User {
    private String department;
    private String specialization;
    private boolean available;
    private Emergency currentEmergency;
    private EmergencyService emergencyService;

    public Responder(String id, String name, String email, String phoneNumber, 
                    String department, String specialization, EmergencyService emergencyService) {
        super(id, name, email, phoneNumber);
        this.department = department;
        this.specialization = specialization;
        this.available = true;
        this.emergencyService = emergencyService;
    }

    public void respondToEmergency(Emergency emergency) {
        this.available = false;
        this.currentEmergency = emergency;
        System.out.println("\n[Responder " + name + " is now responding to emergency " + emergency.getId() + "]");
        
        // Simulate response time
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Simulate time taken to respond
                completeEmergency();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void completeEmergency() {
        if (currentEmergency != null) {
            System.out.println("\n[Responder " + name + " has completed emergency " + currentEmergency.getId() + "]");
            emergencyService.completeEmergency(currentEmergency.getId());
            currentEmergency = null;
            available = true;
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public String getDepartment() {
        return department;
    }

    public String getSpecialization() {
        return specialization;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Responder Menu ===");
        System.out.println("1. View Assigned Emergencies");
        System.out.println("2. Mark Emergency as Resolved");
        System.out.println("3. Update Availability");
        System.out.println("4. Exit");
    }
}
