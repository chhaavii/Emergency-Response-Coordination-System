package com.emergency.model.emergency;

public enum EmergencyStatus {
    REPORTED,       // Emergency has been reported but not yet assigned
    ASSIGNED,       // Responder has been assigned
    IN_PROGRESS,    // Responder is on the scene
    RESOLVED,       // Emergency has been resolved
    CANCELLED       // Emergency was cancelled (false alarm, etc.)
}
