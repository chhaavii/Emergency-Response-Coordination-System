package com.emergency.patterns.observer;

import com.emergency.model.emergency.Emergency;

/**
 * Observer Pattern - Interface for emergency notifications
 */
public interface NotificationObserver {
    void update(Emergency emergency, String message);
    String getObserverId();
}
