package com.emergency.service;

import com.emergency.model.User;
import com.emergency.model.Responder;
import com.emergency.model.emergency.Emergency;
import com.emergency.model.emergency.EmergencyStatus;
import com.emergency.model.emergency.EmergencyType;
import com.emergency.exception.InvalidEmergencyTypeException;
import com.emergency.exception.NoAvailableResponderException;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;

public class EmergencyServiceImpl implements EmergencyService, Runnable {
    private final Map<String, Emergency> emergencies = new ConcurrentHashMap<>();
    private final List<Responder> responders = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final long DISPATCH_INTERVAL_MS = 5000; // 5 seconds

    public EmergencyServiceImpl() {
        // Start the dispatcher thread
        new Thread(this).start();
    }

    @Override
    public Emergency createEmergency(String emergencyTypeStr, String description, String location, int severity, User reporter) 
            throws InvalidEmergencyTypeException {
        EmergencyType type;
        try {
            type = EmergencyType.fromString(emergencyTypeStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidEmergencyTypeException("Invalid emergency type: " + emergencyTypeStr);
        }

        Emergency emergency = new Emergency(type, description, location, severity, reporter);
        
        lock.writeLock().lock();
        try {
            emergencies.put(emergency.getId(), emergency);
            System.out.println("[System] New emergency reported: " + emergency);
            return emergency;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean assignResponder(String emergencyId) throws NoAvailableResponderException {
        lock.writeLock().lock();
        try {
            Emergency emergency = emergencies.get(emergencyId);
            if (emergency == null || emergency.getStatus() != EmergencyStatus.REPORTED) {
                return false;
            }

            // Find available responder (simplified matching)
            Optional<Responder> responderOpt = responders.stream()
                .filter(Responder::isAvailable)
                .findFirst();

            if (responderOpt.isPresent()) {
                Responder responder = responderOpt.get();
                emergency.assignResponder(responder);
                System.out.println("[System] Assigned " + responder.getName() + " to emergency " + emergencyId);
                
                // In a real system, we would notify the responder here
                return true;
            } else {
                throw new NoAvailableResponderException("No available responders for emergency " + emergencyId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean completeEmergency(String emergencyId) {
        lock.writeLock().lock();
        try {
            Emergency emergency = emergencies.get(emergencyId);
            if (emergency != null) {
                emergency.updateStatus(EmergencyStatus.RESOLVED);
                System.out.println("[System] Emergency " + emergencyId + " marked as resolved");
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Emergency> getAllEmergencies() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(emergencies.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Emergency> getEmergencyById(String id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(emergencies.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Emergency> getEmergenciesByStatus(String status) {
        lock.readLock().lock();
        try {
            return emergencies.values().stream()
                .filter(e -> e.getStatus().name().equalsIgnoreCase(status))
                .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void registerResponder(User responder) {
        if (responder instanceof Responder) {
            responders.add((Responder) responder);
            System.out.println("[System] Registered responder: " + responder.getName());
        }
    }

    @Override
    public void unregisterResponder(String responderId) {
        responders.removeIf(r -> r.getId().equals(responderId));
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                // Process pending emergencies
                List<Emergency> pendingEmergencies = getEmergenciesByStatus("REPORTED");
                for (Emergency emergency : pendingEmergencies) {
                    try {
                        assignResponder(emergency.getId());
                    } catch (NoAvailableResponderException e) {
                        System.out.println("[System] Warning: " + e.getMessage());
                    }
                }
                
                // Sleep before next dispatch cycle
                Thread.sleep(DISPATCH_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error in dispatcher thread: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        running.set(false);
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
