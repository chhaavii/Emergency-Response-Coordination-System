package com.emergency.model.emergency;

public enum EmergencyType {
    FIRE,
    MEDICAL,
    POLICE,
    TRAFFIC,
    HAZMAT,
    NATURAL_DISASTER,
    OTHER;

    public static EmergencyType fromString(String type) {
        if (type == null || type.trim().isEmpty()) {
            return OTHER;
        }
        
        return switch (type.trim().toUpperCase()) {
            case "FIRE" -> FIRE;
            case "MEDICAL" -> MEDICAL;
            case "POLICE" -> POLICE;
            case "TRAFFIC" -> TRAFFIC;
            case "HAZMAT" -> HAZMAT;
            case "NATURAL_DISASTER" -> NATURAL_DISASTER;
            default -> OTHER;
        };
    }
}
