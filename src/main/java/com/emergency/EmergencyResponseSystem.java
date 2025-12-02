package com.emergency;

import com.emergency.model.*;
import com.emergency.model.emergency.Emergency;
import com.emergency.service.EmergencyService;
import com.emergency.service.EmergencyServiceImpl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.emergency.model.emergency.EmergencyStatus;

public class EmergencyResponseSystem {
    private final EmergencyService emergencyService;
    private final Scanner scanner;
    private User currentUser;
    private final Map<String, User> users;
    private final ExecutorService executorService;
    private boolean running;

    public EmergencyResponseSystem() {
        this.emergencyService = new EmergencyServiceImpl();
        this.scanner = new Scanner(System.in);
        this.users = new HashMap<>();
        this.executorService = Executors.newCachedThreadPool();
        this.running = true;
        
        // Initialize with some test users
        initializeTestData();
    }

    private void initializeTestData() {
        // Create some test users
        User citizen1 = new Citizen("citizen1", "John Doe", "john@example.com", "1234567890", 
                                 "123 Main St", emergencyService);
        User citizen2 = new Citizen("citizen2", "Jane Smith", "jane@example.com", "0987654321",
                                 "456 Oak Ave", emergencyService);
        
        User responder1 = new Responder("resp1", "Firefighter Bob", "bob@firedept.org", "1112223333",
                                     "Fire Department", "Fire Suppression", emergencyService);
        User responder2 = new Responder("resp2", "Officer Alice", "alice@police.org", "4445556666",
                                     "Police Department", "Law Enforcement", emergencyService);
        
        User admin = new Admin("admin1", "Admin User", "admin@emergency.org", "9998887777", emergencyService);
        
        // Register users
        users.put(citizen1.getId(), citizen1);
        users.put(citizen2.getId(), citizen2);
        users.put(responder1.getId(), responder1);
        users.put(responder2.getId(), responder2);
        users.put(admin.getId(), admin);
        
        // Register responders with the emergency service
        emergencyService.registerResponder((Responder) responder1);
        emergencyService.registerResponder((Responder) responder2);
    }

    public void start() {
        System.out.println("=== Emergency Response Coordination System ===\n");
        
        while (running) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showUserMenu();
            }
        }
        
        shutdown();
    }

    private void showLoginMenu() {
        System.out.println("\n=== Login ===");
        System.out.println("1. Login as Citizen");
        System.out.println("2. Login as Responder");
        System.out.println("3. Login as Admin");
        System.out.println("4. Exit");
        System.out.print("Select an option: ");
        
        int choice = readIntInput(1, 4);
        
        switch (choice) {
            case 1:
                loginAsCitizen();
                break;
            case 2:
                loginAsResponder();
                break;
            case 3:
                loginAsAdmin();
                break;
            case 4:
                running = false;
                break;
        }
    }

    private void loginAsCitizen() {
        System.out.println("\nAvailable Citizens:");
        users.values().stream()
             .filter(user -> user instanceof Citizen)
             .forEach(user -> System.out.println("ID: " + user.getId() + " - " + user.getName()));
             
        System.out.print("Enter citizen ID: ");
        String userId = scanner.nextLine().trim();
        
        User user = users.get(userId);
        if (user instanceof Citizen) {
            currentUser = user;
            System.out.println("\nWelcome, " + user.getName() + "!");
        } else {
            System.out.println("Invalid citizen ID.");
        }
    }

    private void loginAsResponder() {
        System.out.println("\nAvailable Responders:");
        users.values().stream()
             .filter(user -> user instanceof Responder)
             .forEach(user -> System.out.println("ID: " + user.getId() + " - " + user.getName()));
             
        System.out.print("Enter responder ID: ");
        String userId = scanner.nextLine().trim();
        
        User user = users.get(userId);
        if (user instanceof Responder) {
            currentUser = user;
            System.out.println("\nWelcome, " + user.getName() + "!");
        } else {
            System.out.println("Invalid responder ID.");
        }
    }

    private void loginAsAdmin() {
        System.out.print("\nEnter admin username: ");
        String username = scanner.nextLine().trim();
        
        // In a real system, you would validate credentials properly
        User user = users.values().stream()
                        .filter(u -> u instanceof Admin && u.getId().equals(username))
                        .findFirst()
                        .orElse(null);
                        
        if (user != null) {
            currentUser = user;
            System.out.println("\nWelcome, Administrator!");
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    private void showUserMenu() {
        if (currentUser instanceof Citizen) {
            showCitizenMenu();
        } else if (currentUser instanceof Responder) {
            showResponderMenu();
        } else if (currentUser instanceof Admin) {
            showAdminMenu();
        }
    }

    private void showCitizenMenu() {
        Citizen citizen = (Citizen) currentUser;
        citizen.displayMenu();
        
        int choice = readIntInput(1, 3);
        
        switch (choice) {
            case 1:
                reportEmergency();
                break;
            case 2:
                viewReportedEmergencies();
                break;
            case 3:
                System.out.println("Logging out...");
                currentUser = null;
                break;
        }
    }

    private void showResponderMenu() {
        Responder responder = (Responder) currentUser;
        
        while (currentUser != null) {
            responder.displayMenu();
            int choice = readIntInput(1, 4);
            
            switch (choice) {
                case 1:
                    viewAssignedEmergencies(responder);
                    break;
                case 2:
                    markEmergencyResolved(responder);
                    break;
                case 3:
                    updateAvailability(responder);
                    break;
                case 4:
                    System.out.println("Logging out...");
                    currentUser = null;
                    return;
            }
        }
    }
    
    private void viewAssignedEmergencies(Responder responder) {
        System.out.println("\n=== Your Assigned Emergencies ===");
        List<Emergency> emergencies = emergencyService.getAllEmergencies();
        
        List<Emergency> assignedEmergencies = emergencies.stream()
            .filter(e -> e.getAssignedResponder() != null && 
                        e.getAssignedResponder().getId().equals(responder.getId()) &&
                        e.getStatus() != EmergencyStatus.RESOLVED &&
                        e.getStatus() != EmergencyStatus.CANCELLED)
            .toList();
            
        if (assignedEmergencies.isEmpty()) {
            System.out.println("You have no assigned emergencies.");
            return;
        }
        
        System.out.println("ID                                   | Type            | Status     | Location    | Reporter");
        System.out.println("--------------------------------------+-----------------+------------+-------------+------------");
        
        for (Emergency emergency : assignedEmergencies) {
            System.out.printf("%-37s | %-15s | %-10s | %-11s | %s%n",
                emergency.getId().substring(0, Math.min(8, emergency.getId().length())) + "...",
                emergency.getType(),
                emergency.getStatus(),
                emergency.getLocation().length() > 10 ? 
                    emergency.getLocation().substring(0, 7) + "..." : emergency.getLocation(),
                emergency.getReporter().getName());
        }
    }
    
    private void markEmergencyResolved(Responder responder) {
        List<Emergency> activeEmergencies = emergencyService.getAllEmergencies().stream()
            .filter(e -> e.getAssignedResponder() != null && 
                        e.getAssignedResponder().getId().equals(responder.getId()) &&
                        e.getStatus() != EmergencyStatus.RESOLVED &&
                        e.getStatus() != EmergencyStatus.CANCELLED)
            .toList();
            
        if (activeEmergencies.isEmpty()) {
            System.out.println("\nNo active emergencies to resolve.");
            return;
        }
        
        System.out.println("\nSelect an emergency to mark as resolved:");
        for (int i = 0; i < activeEmergencies.size(); i++) {
            Emergency e = activeEmergencies.get(i);
            System.out.printf("%d. %s at %s (Status: %s)%n", 
                i + 1, e.getType(), e.getLocation(), e.getStatus());
        }
        
        System.out.print("Enter emergency number (0 to cancel): ");
        int choice = readIntInput(0, activeEmergencies.size());
        
        if (choice > 0) {
            Emergency emergency = activeEmergencies.get(choice - 1);
            emergency.updateStatus(EmergencyStatus.RESOLVED);
            System.out.println("\n[System] Emergency " + emergency.getId() + " marked as RESOLVED");
        }
    }
    
    private void updateAvailability(Responder responder) {
        System.out.println("\nCurrent status: " + (responder.isAvailable() ? "Available" : "On Assignment"));
        System.out.println("1. Set as Available");
        System.out.println("2. Set as Unavailable");
        System.out.print("Select option (0 to cancel): ");
        
        int choice = readIntInput(0, 2);
        
        if (choice == 1) {
            // In a real implementation, we would update the responder's status in the service
            System.out.println("\n[System] You are now marked as Available");
        } else if (choice == 2) {
            System.out.println("\n[System] You are now marked as Unavailable");
        }
    }

    private void showAdminMenu() {
        while (currentUser != null) {
            System.out.println("\n=== Admin Dashboard ===");
            System.out.println("1. View All Emergencies");
            System.out.println("2. View All Responders");
            System.out.println("3. Generate Report");
            System.out.println("4. Logout");
            
            int choice = readIntInput(1, 4);
            
            switch (choice) {
                case 1:
                    viewAllEmergencies();
                    break;
                case 2:
                    viewAllResponders();
                    break;
                case 3:
                    generateReport();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    currentUser = null;
                    return;
            }
        }
    }
    
    private void viewAllEmergencies() {
        System.out.println("\n=== All Emergencies ===");
        List<Emergency> emergencies = emergencyService.getAllEmergencies();
        
        if (emergencies.isEmpty()) {
            System.out.println("No emergencies found in the system.");
            return;
        }
        
        System.out.println("ID                                   | Type            | Status     | Location    | Reporter      | Assigned To");
        System.out.println("--------------------------------------+-----------------+------------+-------------+----------------+----------------");
        
        for (Emergency emergency : emergencies) {
            String assignedTo = "None";
            if (emergency.getAssignedResponder() != null) {
                assignedTo = emergency.getAssignedResponder().getName();
            }
            
            System.out.printf("%-37s | %-15s | %-10s | %-11s | %-14s | %s%n",
                emergency.getId().substring(0, Math.min(8, emergency.getId().length())) + "...",
                emergency.getType(),
                emergency.getStatus(),
                emergency.getLocation().length() > 10 ? emergency.getLocation().substring(0, 7) + "..." : emergency.getLocation(),
                emergency.getReporter().getName(),
                assignedTo);
        }
    }
    
    private void viewAllResponders() {
        System.out.println("\n=== All Responders ===");
        // This is a simplified view - in a real app, we'd get this from a UserService
        users.values().stream()
            .filter(user -> user instanceof Responder)
            .map(user -> (Responder) user)
            .forEach(responder -> {
                System.out.println("\nID: " + responder.getId());
                System.out.println("Name: " + responder.getName());
                System.out.println("Department: " + responder.getDepartment());
                System.out.println("Specialization: " + responder.getSpecialization());
                System.out.println("Status: " + (responder.isAvailable() ? "Available" : "On Assignment"));
            });
    }
    
    private void generateReport() {
        System.out.println("\n=== Emergency System Report ===");
        List<Emergency> emergencies = emergencyService.getAllEmergencies();
        long activeEmergencies = emergencies.stream()
            .filter(e -> e.getStatus() != EmergencyStatus.RESOLVED && 
                        e.getStatus() != EmergencyStatus.CANCELLED)
            .count();
            
        System.out.println("Total Emergencies: " + emergencies.size());
        System.out.println("Active Emergencies: " + activeEmergencies);
        System.out.println("Resolved/Cancelled: " + (emergencies.size() - activeEmergencies));
        
        // Emergency type breakdown
        System.out.println("\nEmergency Type Breakdown:");
        emergencies.stream()
            .collect(Collectors.groupingBy(Emergency::getType, Collectors.counting()))
            .forEach((type, count) -> System.out.printf("- %-15s: %d%n", type, count));
    }

    private void reportEmergency() {
        System.out.println("\n=== Report Emergency ===");
        
        System.out.print("Emergency type (FIRE, MEDICAL, POLICE, etc.): ");
        String type = scanner.nextLine().trim();
        
        System.out.print("Location: ");
        String location = scanner.nextLine().trim();
        
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        
        System.out.print("Severity (1-5, with 5 being most severe): ");
        int severity = readIntInput(1, 5);
        
        try {
            Emergency emergency = emergencyService.createEmergency(type, description, location, severity, currentUser);
            System.out.println("\n[System] Emergency reported successfully! ID: " + emergency.getId());
        } catch (Exception e) {
            System.out.println("Error reporting emergency: " + e.getMessage());
        }
    }

    private void viewReportedEmergencies() {
        System.out.println("\n=== Your Reported Emergencies ===");
        List<Emergency> emergencies = emergencyService.getAllEmergencies();
        
        emergencies.stream()
                  .filter(e -> e.getReporter().equals(currentUser))
                  .forEach(e -> System.out.println(
                      String.format("ID: %s | Type: %s | Status: %s | Location: %s",
                          e.getId(), e.getType(), e.getStatus(), e.getLocation())));
    }

    private int readIntInput(int min, int max) {
        while (true) {
            try {
                System.out.print("\nEnter your choice (" + min + "-" + max + "): ");
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void shutdown() {
        System.out.println("\nShutting down Emergency Response System...");
        if (emergencyService instanceof EmergencyServiceImpl) {
            ((EmergencyServiceImpl) emergencyService).shutdown();
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        scanner.close();
        System.out.println("System shutdown complete. Goodbye!");
    }

    public static void main(String[] args) {
        EmergencyResponseSystem system = new EmergencyResponseSystem();
        system.start();
    }
}
