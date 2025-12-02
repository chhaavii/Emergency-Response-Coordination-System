package com.emergency.patterns.strategy;

import com.emergency.model.emergency.Emergency;
import com.emergency.model.Responder;
import java.util.List;

/**
 * Strategy Pattern - Defines interface for emergency assignment algorithms
 */
public interface EmergencyAssignmentStrategy {
    Responder assignEmergency(Emergency emergency, List<Responder> availableResponders);
}
