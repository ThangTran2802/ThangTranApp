# ThangTranApp - Smart Ride Booking System

A ride booking application (similar to Grab/Uber) built as a final project for Object-Oriented Programming course. The system supports two roles: **Customer** and **Driver**.

## Features
1. **Customer:**
   - Book a ride by entering pickup location, dropoff location, and distance.
   - Automatic fare calculation (10,000 VND base fee + 8,000 VND/km).
   - View ride history filtered by phone number.
2. **Driver:**
   - Secure login with username and password (masked input).
   - View dashboard with stats (completed trips, total earnings, vehicle info).
   - View current active ride details and mark as completed.
   - Register new driver accounts.

## OOP Principles Used
- **Inheritance:** `User` and `Driver` extend `Person` base class.
- **Encapsulation:** All fields are `private` with getter methods.
- **Polymorphism:** `getRole()` and `getInfo()` are overridden in subclasses to return role-specific information.

## Technology Stack
- **Language:** Java (OOP principles: Inheritance, Encapsulation, Polymorphism)
- **GUI:** Java Swing (custom dark theme with gradient panels, styled components)
- **Database:** MySQL (connected via `mysql-connector-j-8.3.0.jar`)

## How to Run
1. Ensure **Java (JDK)** and **MySQL** are installed.
2. Create the database and tables in MySQL (SQL scripts provided in the project report).
3. Double-click **`run.bat`** (Windows).
   - This script automatically compiles all source code and includes the MySQL connector library.

## Default Test Accounts (Driver):
- Username: `driver1` | Password: `123456` (Cristiano Ronaldo)
- Username: `driver2` | Password: `234567` (Lionel Messi)

## Project Structure
```
ThangTranApp/
├── Person.java           - Base class (name, phone, getRole(), getInfo())
├── Driver.java           - Extends Person (vehicle, availability, overrides getRole/getInfo)
├── User.java             - Extends Person (customer booking, overrides getRole/getInfo)
├── Ride.java             - Ride data model (route, fare, status)
├── Database.java         - Data Access Layer (MySQL CRUD operations)
├── BookingAppGUI.java    - Swing GUI (Login, Customer, Driver dashboards)
├── Main.java             - Console-based interface (alternative)
├── TestApp.java          - Automated test suite (6 test cases)
├── run.bat               - Build & run script
├── mysql-connector-j-8.3.0.jar
└── README.md
```

## Authors
- Tran Quyet Thang
