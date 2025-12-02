package com.emergency.patterns.factory;

import com.emergency.model.*;
import com.emergency.service.EmergencyService;

/**
 * Factory Pattern - Creates user objects based on type
 */
public class UserFactory {
    
    public static User createUser(String type, String id, String name, String email, 
                                String phoneNumber, String additionalInfo, EmergencyService service) {
        switch (type.toUpperCase()) {
            case "CITIZEN":
                return new Citizen(id, name, email, phoneNumber, additionalInfo, service);
            case "RESPONDER":
                // For responders, additionalInfo contains department|specialization
                String[] parts = additionalInfo.split("\\|");
                String department = parts.length > 0 ? parts[0] : "General";
                String specialization = parts.length > 1 ? parts[1] : "General";
                return new Responder(id, name, email, phoneNumber, department, specialization, service);
            case "ADMIN":
                return new Admin(id, name, email, phoneNumber, service);
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
    
    public static User createCitizen(String id, String name, String email, 
                                   String phoneNumber, String address, EmergencyService service) {
        return new Citizen(id, name, email, phoneNumber, address, service);
    }
    
    public static User createResponder(String id, String name, String email, 
                                     String phoneNumber, String department, 
                                     String specialization, EmergencyService service) {
        return new Responder(id, name, email, phoneNumber, department, specialization, service);
    }
    
    public static User createAdmin(String id, String name, String email, 
                                 String phoneNumber, EmergencyService service) {
        return new Admin(id, name, email, phoneNumber, service);
    }
}
