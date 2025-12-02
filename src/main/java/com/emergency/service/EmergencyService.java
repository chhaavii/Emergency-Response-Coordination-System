package com.emergency.service;

import com.emergency.model.User;
import com.emergency.model.emergency.Emergency;
import com.emergency.exception.InvalidEmergencyTypeException;
import com.emergency.exception.NoAvailableResponderException;

import java.util.List;
import java.util.Optional;

public interface EmergencyService {
    Emergency createEmergency(String emergencyType, String description, String location, int severity, User reporter) 
        throws InvalidEmergencyTypeException;
    
    boolean assignResponder(String emergencyId) throws NoAvailableResponderException;
    
    boolean completeEmergency(String emergencyId);
    
    List<Emergency> getAllEmergencies();
    
    Optional<Emergency> getEmergencyById(String id);
    
    List<Emergency> getEmergenciesByStatus(String status);
    
    void registerResponder(User responder);
    
    void unregisterResponder(String responderId);
}
