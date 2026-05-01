# ✈️ Flight Booking & Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Java%20Swing-GUI-blue?style=for-the-badge&logo=java&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-Testing-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)
![Tests](https://img.shields.io/badge/Tests-634%2F634%20Passed-brightgreen?style=for-the-badge)

**A comprehensive Java desktop application for managing flight bookings, customers, and administration — with both a CLI and a full Swing GUI.**

*Built by [Sushant Shrestha](https://github.com/) & [Utsav Rai](https://github.com/Utu8848) · Birmingham City University — School of Architecture, Built Environment, Computing and Engineering*

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [System Architecture](#-system-architecture)
- [Package Structure](#-package-structure)
- [OOP Design](#-oop-design)
- [Technologies](#-technologies)
- [Getting Started](#-getting-started)
- [CLI Commands](#-cli-commands)
- [Testing](#-testing)
- [License](#-license)

---

## 🌟 Overview

The **Flight Booking & Management System** is a fully functional desktop application built in Java. It provides an end-to-end solution for managing flights, customers, and bookings through both a command-line interface and a polished graphical interface built with Java Swing.

Key highlights:

- 🖥️ **Dual Interface** — Use the system entirely from the terminal or switch to a full GUI anytime
- 👤 **Role-Based Access** — Separate portals for Administrators and Customers
- 💰 **Dynamic Pricing** — Ticket prices adjust in real time based on seat occupancy and days until departure
- 📁 **File-Based Persistence** — All data survives across sessions without a database
- ✅ **Fully Tested** — 634 JUnit 5 tests, zero errors, zero failures

---

## 🚀 Features

### 🔐 Authentication
- Login / Logout for both admins and customers
- Customer self-registration via CLI and GUI
- Role-based access control — admins see admin tools, customers see their own portal
- Session management and password security

### ✈️ Flight Management *(Admin)*
- Add new flights with number, route, date, capacity, and base price
- View all upcoming flights with real-time dynamic pricing displayed
- Soft-delete flights — bookings on deleted flights are auto-cancelled with a full refund
- Duplicate flight detection with a side-by-side comparison dialog

### 👥 Customer Management *(Admin)*
- Add, view, and soft-delete customers
- A linked user account is automatically created when a customer is added
- Input validation for email format and 10-digit phone numbers
- View full booking history per customer

### 🎫 Booking Operations

| Operation | Admin | Customer | Fee |
|---|---|---|---|
| Book one-way trip | ✅ | ✅ | — |
| Book round trip | ✅ | ✅ | — |
| Cancel one-way | ✅ | ✅ | £25 |
| Cancel round-trip | ✅ | ✅ | £50 |
| Rebook / Update flight | ✅ | ✅ | £15 |
| View booking history | ✅ | ✅ (own only) | — |

> If an admin deletes a flight that has active bookings, all affected bookings are automatically cancelled with **no cancellation fee** and a full refund issued.

### 💾 Data Persistence

All data is saved locally in plain-text files:

```
resources/
├── flights.txt
├── customers.txt
├── bookings.txt
└── users.txt
```

---

## 🏗️ System Architecture

The project follows a clean **Three-Tier Architecture**:

```
┌────────────────────────────────────────────────┐
│              PRESENTATION LAYER                │
│    CLI (Main.java)    │    GUI (Java Swing)     │
└──────────────┬─────────────────────────────────┘
               │
┌──────────────▼─────────────────────────────────┐
│            BUSINESS LOGIC LAYER                │
│  14 Commands  │  Domain Models  │  Auth Service │
│  Validation   │  Dynamic Pricing│  Session Mgmt │
└──────────────┬─────────────────────────────────┘
               │
┌──────────────▼─────────────────────────────────┐
│           DATA PERSISTENCE LAYER               │
│  flights.txt  │  customers.txt  │  bookings.txt │
│                   users.txt                    │
└────────────────────────────────────────────────┘
```

---

## 📦 Package Structure

```
src/bcu/cmp5332/bookingsystem/
│
├── 📁 main/         → Entry point and CLI engine (4 files)
│
├── 📁 commands/     → 14 Command Pattern implementations
│   ├── AddFlight, DeleteFlight, ShowFlight, ListFlights
│   ├── AddCustomer, DeleteCustomer, ShowCustomer, ListCustomers
│   ├── AddBooking, CancelBooking, UpdateBooking
│   └── Help, LoadGUI, Login, Logout, Register
│
├── 📁 model/        → Core domain entities (6 files)
│   ├── Flight.java
│   ├── Customer.java
│   ├── Booking.java
│   ├── User.java
│   ├── FlightBookingSystem.java
│   └── AuthenticationService.java
│
├── 📁 data/         → File I/O and persistence (5 files)
│
├── 📁 gui/          → Java Swing windows (5 files)
│   ├── MainWindow.java        (Admin portal)
│   ├── CustomerWindow.java    (Customer portal)
│   ├── LoginWindow.java
│   ├── RegistrationWindow.java
│   └── AddFlightWindow.java
│
├── 📁 auth/         → Authentication and session logic (3 files)
├── 📁 util/         → Input validation utilities (1 file)
└── 📁 test/         → JUnit 5 test suite (8 files)

Total: 8 Packages · 46 Files
```

---

## 🧱 OOP Design

### Encapsulation
All model fields are `private`. Data is accessed only through validated getters and setters. Internal collections use defensive copying to prevent external mutation.

```java
// Flight.java — safe read-only access to passenger list
public List<Customer> getPassengers() {
    return Collections.unmodifiableList(new ArrayList<>(passengers));
}
```

### Inheritance
A custom exception hierarchy extends Java's built-in `Exception`:

```
Exception
└── FlightBookingSystemException
    └── DuplicateFlightException  (carries reference to the existing flight)
```

All 14 command classes implement a common `Command` interface — a form of type inheritance that keeps the system extensible without touching existing code.

### Polymorphism
**Runtime polymorphism** via the Command Pattern — the parser returns a `Command` reference and `execute()` behaves differently depending on the actual type at runtime:

```java
Command command = CommandParser.parse(userInput);
command.execute(fbs); // AddFlight, CancelBooking, ListFlights — all called identically
```

**Compile-time polymorphism** via constructor overloading in `Flight`, `User`, and `Booking`.

### Abstraction
Complex logic is hidden behind clean interfaces:

- The `Command` interface exposes only `execute()` — callers never know the internals
- The `DataManager` interface abstracts all file I/O away from the rest of the system
- Dynamic pricing is fully hidden inside `Booking.getPrice()`, which applies:
  - **Urgency factor:** +50% (≤7 days), +30% (≤14 days), +15% (≤30 days to departure)
  - **Occupancy factor:** up to +40% based on how full the flight is

---

## 🛠️ Technologies

| Category | Stack |
|---|---|
| Language | Java 17+ |
| GUI | Java Swing |
| Testing | JUnit 5 |
| IDE | Eclipse |
| Version Control | Git & GitHub |
| Storage | File-based (`.txt`) |

---

## ⚙️ Getting Started

### Prerequisites

- Java JDK 17 or higher
- Eclipse IDE (recommended) or any Java-compatible IDE
- JUnit 5 on the classpath (for running tests)

### Clone & Run

```bash
git clone https://github.com/Utu8848/Flight-Booking-and-Management-System.git
cd Flight-Booking-and-Management-System
```

**In Eclipse:**
1. `File` → `Import` → `Existing Projects into Workspace`
2. Select the cloned folder → `Finish`
3. Open `src/bcu/cmp5332/bookingsystem/main/Main.java`
4. Right-click → `Run As` → `Java Application`

### Default Admin Login

```
Username: admin
Password: admin123
```

Customers can register themselves using the `register` command in the CLI, or the **"Register as Customer"** link on the GUI login screen.

---

## 💻 CLI Commands

### General — Available to Everyone

| Command | Description |
|---|---|
| `login` | Log in to the system |
| `logout` | Log out of the current session |
| `register` | Create a new customer account |
| `listflights` | View all upcoming flights |
| `showflight <id>` | View details of a specific flight |
| `loadgui` | Launch the graphical interface |
| `help` | Show all available commands |
| `exit` | Exit the application |

### Admin Commands

| Command | Description |
|---|---|
| `addflight` | Add a new flight (interactive prompts) |
| `deleteflight <id>` | Remove a flight (auto-cancels bookings with full refund) |
| `addcustomer` | Add a new customer and auto-create their account |
| `deletecustomer <id>` | Remove a customer and their linked user account |
| `listcustomers` | View all active customers |
| `showcustomer <id>` | View customer profile and full booking history |
| `addbooking <custID> <outboundID>` | Create a one-way booking |
| `addbooking <custID> <outboundID> <returnID>` | Create a round-trip booking |
| `cancelbooking <custID> <flightID>` | Cancel a one-way booking (£25 fee) |
| `cancelbooking <custID> <outboundID> <returnID>` | Cancel a round-trip booking (£50 fee) |
| `updatebooking <custID> <oldFlightID> <newFlightID>` | Move a booking to a different flight (£15 fee) |

### Customer Commands

| Command | Description |
|---|---|
| `addbooking <outboundID>` | Book a one-way flight |
| `addbooking <outboundID> <returnID>` | Book a round trip |
| `cancelbooking <flightID>` | Cancel a one-way booking (£25 fee) |
| `cancelbooking <outboundID> <returnID>` | Cancel a round-trip booking (£50 fee) |
| `rebook <oldFlightID> <newFlightID>` | Switch to a different flight (£15 fee) |

---

## 🧪 Testing

The project includes a comprehensive JUnit 5 test suite covering all major components:

```
Test Results — 634 runs · 0 errors · 0 failures ✅
─────────────────────────────────────────────────
✅ AuthenticationServiceTest
✅ BookingTest
✅ FlightTest
✅ CustomerTest
✅ UserTest
✅ ValidationUtilTest
✅ FlightBookingSystemTest
✅ AllTests (full suite)
```

**To run in Eclipse:**
Right-click the `test` package → `Run As` → `JUnit Test`

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">

Made with ☕ and Java<br>
Birmingham City University · School of Architecture, Built Environment, Computing and Engineering

</div>
