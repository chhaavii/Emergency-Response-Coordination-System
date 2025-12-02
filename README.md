# Emergency Response Coordination System

A console-based Java application that simulates an Emergency Response Coordination System for managing emergencies and dispatching responders.

## Features

- **User Roles**:
  - **Citizen**: Report emergencies and view reported incidents
  - **Responder**: Receive and respond to emergency assignments
  - **Admin**: Monitor system activity and generate reports

- **Emergency Management**:
  - Report different types of emergencies (FIRE, MEDICAL, POLICE, etc.)
  - Automatic classification and prioritization of emergencies
  - Dynamic responder assignment based on availability
  - Real-time status updates

- **Technical Highlights**:
  - Multi-threaded architecture for concurrent emergency handling
  - Thread-safe data structures for concurrent access
  - Custom exceptions for error handling
  - Clean OOP design with proper encapsulation

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven

### Running the Application

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean package
   ```
3. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.emergency.EmergencyResponseSystem"
   ```

## Usage

1. **Login**: Choose your role (Citizen, Responder, or Admin)
2. **Citizen**:
   - Report new emergencies
   - View your reported emergencies
3. **Responder**:
   - View assigned emergencies
   - Update emergency status
4. **Admin**:
   - View all emergencies
   - Monitor responder status
   - Generate reports

## Design Patterns Used

- **Singleton**: For managing the EmergencyService instance
- **Factory Method**: For creating different types of users
- **Observer**: For notifying users about emergency updates
- **Command**: For handling user actions

## Error Handling

The system includes custom exceptions for:
- Invalid emergency types
- Unavailable responders
- Authentication failures

## Testing

To run tests:
```bash
mvn test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
