# 🚗 Car Rental System

## 📋 Project Overview
The **Car Rental System** is a modern web application that revolutionizes the car rental experience by connecting customers with car owners through a seamless digital platform. Our system emphasizes user experience, security, and efficient booking management.

## ✨ Key Features

### 👤 For Customers
- **Smart Account Management**
  - Secure authentication with multi-level password strength
  - Profile customization with avatar support
  - Digital wallet integration for seamless transactions
  - Personalized booking history and preferences

- **Intelligent Booking System**
  - Advanced car search with multiple filters
  - Smart pickup date/time selection
  - Real-time availability checking
  - Interactive map integration for location selection

- **Enhanced User Experience**
  - Drag-enabled car recommendation carousel
  - Interactive booking calendar
  - Real-time notifications
  - Responsive design for all devices

### 🔑 For Car Owners
- **Comprehensive Vehicle Management**
  - Detailed car listing dashboard
  - Real-time booking notifications
  - Revenue tracking and analytics
  - Customizable availability calendar

- **Business Tools**
  - Automated pricing suggestions
  - Booking management interface
  - Performance analytics
  - Customer feedback management

## 🛠 Technical Requirements

### System Requirements
- **Backend**: Java 17+
- **Framework**: Spring Boot
- **Database**: SQL Server
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven/Gradle

### Hardware Requirements
- **Server**: 
  - CPU: 4+ cores
  - RAM: 8GB minimum
  - Storage: 50GB+ SSD recommended

### Software Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- Add other dependencies -->
</dependencies>
```

## 🚀 Quick Start Guide

### 1. Environment Setup
```bash
# Clone repository
git clone https://github.com/Starlightll/PJB_JAVA_02_RentalCar.git

# Navigate to project
cd car-rental-system

# Install dependencies
mvn install
```

### 2. Database Configuration
Create `application.properties`:
```properties
# Database Configuration
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.url=jdbc:sqlserver://localhost:1433;encrypt=true;databaseName=RentalCar;encrypt=false;
spring.datasource.username=root
spring.datasource.password=yourpassword

# JPA Properties
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Server Configuration
server.port=8080
```

### 3. Launch Application
```bash
mvn spring-boot:run
```

## 🔄 API Reference

### Authentication Endpoints
```
wating update
```

### Car Management
```
GET    /api/brands            # List all brands
GET   /api/additionalFunction           # List all functions
GET    /api/searchCar/{id}       # View car detail
GET /api/searchCar       # List all cars
```

### Booking Management
```
waiting update
```

## 💡 Best Practices

### For Customers
- Always verify car details before booking
- Complete profile for faster booking process
- Maintain sufficient wallet balance
- Review rental terms carefully

### For Car Owners
- Keep car information updated
- Respond to bookings promptly
- Maintain accurate availability calendar
- Regular price optimization

## 🔍 Monitoring & Analytics

The system provides comprehensive analytics including:
- Booking trends
- Revenue analysis
- Customer satisfaction metrics
- Performance indicators

## 🔒 Security Features

- Multi-factor authentication
- Encrypted data transmission
- Secure payment processing
- Regular security audits

## 🤝 Support

For technical support:
- 📧 Email: support@carrentalsystem.com
- 💬 Live Chat: Available 24/7
- 📱 Phone: +1-XXX-XXX-XXXX

---

**Version**: 1.1.0  
**Last Updated**: December 2024  
**Contributors**: Team PJB_JAVA_02
