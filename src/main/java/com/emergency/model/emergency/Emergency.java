package com.emergency.model.emergency;

import com.emergency.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public class Emergency {
    private final String id;
    private final EmergencyType type;
    private final String description;
    private final String location;
    private final int severity; // 1-5, with 5 being most severe
    private final User reporter;
    private EmergencyStatus status;
    private User assignedResponder;
    private final LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;

    public Emergency(EmergencyType type, String description, String location, int severity, User reporter) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.description = description;
        this.location = location;
        this.severity = Math.max(1, Math.min(5, severity)); // Ensure severity is between 1-5
        this.reporter = reporter;
        this.status = EmergencyStatus.REPORTED;
        this.reportedAt = LocalDateTime.now();
    }

    public synchronized void assignResponder(User responder) {
        if (responder == null) {
            throw new IllegalArgumentException("Responder cannot be null");
        }
        this.assignedResponder = responder;
        updateStatus(EmergencyStatus.ASSIGNED);
        System.out.println("[System] Assigned " + responder.getName() + " to emergency " + id);
    }

    public synchronized void updateStatus(EmergencyStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        // Validate status transition
        if (this.status == EmergencyStatus.RESOLVED || this.status == EmergencyStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a " + this.status + " emergency");
        }
        
        this.status = newStatus;
        
        if (newStatus == EmergencyStatus.RESOLVED || newStatus == EmergencyStatus.CANCELLED) {
            this.resolvedAt = LocalDateTime.now();
            System.out.println("[System] Emergency " + id + " has been marked as " + newStatus);
        } else if (newStatus == EmergencyStatus.IN_PROGRESS) {
            System.out.println("[System] Emergency " + id + " is now IN_PROGRESS");
        }
    }

    // Getters
    public String getId() { return id; }
    public EmergencyType getType() { return type; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getSeverity() { return severity; }
    public User getReporter() { return reporter; }
    public EmergencyStatus getStatus() { return status; }
    public User getAssignedResponder() { return assignedResponder; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }

    @Override
    public String toString() {
        return String.format("Emergency[%s] - %s at %s (Severity: %d, Status: %s)",
                id, type, location, severity, status);
    }
}
