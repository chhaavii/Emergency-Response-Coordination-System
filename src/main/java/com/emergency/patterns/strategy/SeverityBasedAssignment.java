package com.emergency.patterns.strategy;

import com.emergency.model.emergency.Emergency;
import com.emergency.model.Responder;
import java.util.List;
import java.util.Optional;

/**
 * Strategy Pattern - Severity-based emergency assignment
 */
public class SeverityBasedAssignment implements EmergencyAssignmentStrategy {
    
    @Override
    public Responder assignEmergency(Emergency emergency, List<Responder> availableResponders) {
        if (availableResponders.isEmpty()) {
            return null;
        }
        
        // Assign based on severity and specialization
        Optional<Responder> specializedResponder = findSpecializedResponder(emergency, availableResponders);
        if (specializedResponder.isPresent()) {
            return specializedResponder.get();
        }
        
        // Fallback to round-robin based on department
        return availableResponders.stream()
            .findFirst()
            .orElse(null);
    }
    
    private Optional<Responder> findSpecializedResponder(Emergency emergency, List<Responder> responders) {
        String emergencyType = emergency.getType().toString();
        
        return responders.stream()
            .filter(Responder::isAvailable)
            .filter(r -> isSpecializedForEmergency(r, emergencyType))
            .findFirst();
    }
    
    private boolean isSpecializedForEmergency(Responder responder, String emergencyType) {
        String specialization = responder.getSpecialization().toLowerCase();
        String department = responder.getDepartment().toLowerCase();
        
        return (emergencyType.contains("fire") && (specialization.contains("fire") || department.contains("fire"))) ||
               (emergencyType.contains("medical") && (specialization.contains("medical") || department.contains("medical"))) ||
               (emergencyType.contains("police") && (specialization.contains("law") || department.contains("police")));
    }
}
