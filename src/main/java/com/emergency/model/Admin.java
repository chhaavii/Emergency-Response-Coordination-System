package com.emergency.model;

import com.emergency.service.EmergencyService;
import com.emergency.model.emergency.Emergency;
import java.util.List;

public class Admin extends User {
    private EmergencyService emergencyService;

    public Admin(String id, String name, String email, String phoneNumber, EmergencyService emergencyService) {
        super(id, name, email, phoneNumber);
        this.emergencyService = emergencyService;
    }

    public void viewAllEmergencies() {
        System.out.println("\n=== All Active Emergencies ===");
        List<Emergency> emergencies = emergencyService.getAllEmergencies();
        if (emergencies.isEmpty()) {
            System.out.println("No active emergencies.");
        } else {
            emergencies.forEach(emergency -> 
                System.out.printf("ID: %s | Type: %s | Status: %s | Location: %s%n",
                    emergency.getId(),
                    emergency.getType(),
                    emergency.getStatus(),
                    emergency.getLocation())
            );
        }
    }

    public void viewAllResponders() {
        System.out.println("\n=== All Responders ===");
        // This would be implemented to show all responders and their status
        System.out.println("Responder list functionality will be implemented here.");
    }

    public void generateReport() {
        System.out.println("\n=== Generating Emergency Report ===");
        // This would generate a more detailed report
        System.out.println("Emergency report generation will be implemented here.");
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. View All Emergencies");
        System.out.println("2. View All Responders");
        System.out.println("3. Generate Report");
        System.out.println("4. Exit");
    }
}
