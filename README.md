# Car Rental System

## Project Overview
The **Car Rental System** is a web application designed for customers to rent cars and car owners to manage their vehicle rentals. The system streamlines the process of searching, booking, and renting cars, while providing features for managing bookings, payments, and feedback.

## Key Features
- **Customer Account Management**: Users can create an account, log in, and manage their profile details.
- **Car Search & Booking**: Customers can search for available cars based on location and desired rental periods, view details, and make bookings.
- **Booking Management**: Users can edit or cancel their bookings and confirm the pick-up or return of the rented cars.
- **Car Owner Management**: Car owners can list their cars for rent, manage car details, and handle booking confirmations.
- **Payment & Wallet**: The system provides options for handling deposits, payments, and refunds, with a wallet feature for customers.
- **Feedback & Ratings**: Customers can provide feedback and rate the cars after the rental period.

## System Requirements
- **Functional Requirements**:
  - Support customer and car owner roles.
  - Manage user accounts, car listings, bookings, payments, and ratings.
  - Display real-time car availability.
  - Secure login and password recovery options.
  - Notification system for booking confirmations and status changes.

- **Non-Functional Requirements**:
  - High performance for concurrent users.
  - Reliable availability and uptime.
  - Security measures for data privacy and secure transactions.

## Getting Started

### Prerequisites
- **Java 17+**
- **Spring Boot**Navigate to the project directory:
- **SQL Server** for database
- **Thymeleaf** for server-side rendering (Optional if using frontend frameworks)
- **Maven** or **Gradle** for dependency management

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Starlightll/PJB_JAVA_02_RentalCar.git
   ```
2. Navigate to the project directory:
   ```bash
   cd car-rental-system
   ```
3. Configure the database in the `application.properties`
   ```properties
   spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
   spring.datasource.url= jdbc:sqlserver://localhost:1433;encrypt=true;databaseName=RentalCar;encrypt=false;
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
   spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   
## API Endpoints
- POST /login: Login to the system.
- ...
- UPDATING

## Usage
### Customers:
- Create an account or log in.
- Search for available cars based on location, pick-up, and drop-off times.
- View car details and rent a car.
- Manage bookings, confirm pick-up, and return cars.

### Car Owners:
- Register as a car owner.
- List available cars and manage car information.
- Confirm bookings, deposits, and payments.

