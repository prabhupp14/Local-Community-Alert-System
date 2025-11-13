# Local-Community-Alert-System

Project Title:

Local Community Alert System (User & Admin Versions)

Project Description:

The Local Community Alert System is a Java console application designed to empower communities by enabling residents to report local issues and allowing administrators to manage and monitor these alerts efficiently. This system ensures that community problems like water leaks, potholes, lost pets, power outages, and other civic issues are documented, tracked, and addressed promptly.

The project includes two distinct versions:

User Version: For regular community members to submit alerts, view existing alerts, search, filter, sort, and check statistics.

Admin Version: For system administrators to manage alerts fully, including updating alert statuses, deleting specific alerts, exporting data, and clearing all data.

Key Features:
User Features:

Submit new alerts with title, description, category, urgency, and location

View all community alerts

Sort alerts by urgency or location

Filter alerts by category

Search alerts by location

View statistics of alerts (by category, urgency, and status)

Admin Features (All User Features +):

Update alert status (OPEN → IN_PROGRESS → RESOLVED → CLOSED)

Delete specific alerts with confirmation

Clear all alerts safely

Export alerts to a text file for reporting

Full control over alert data management

Technical Concepts Used:

Object-Oriented Programming (OOP): Classes (Alert, AlertSystemBase, UserAlertSystem, AdminAlertSystem), Enums (AlertCategory, Urgency), inheritance, and encapsulation

Collections: ArrayList, Maps, and Java Streams for filtering and grouping

Sorting: Custom comparators for sorting by urgency and location

File Handling: Persistent storage using serialization, export to text file

Exception Handling: Robust handling of invalid inputs and I/O errors

Java 8+ Features: Lambdas, Streams, and Collectors for filtering, searching, and statistics

Practical Usefulness:

This system is highly practical for local communities and municipalities:

Encourages community engagement by allowing citizens to report issues

Helps administrators monitor problem areas efficiently

Provides persistent storage for long-term tracking of issues

Generates statistics and reports to identify trends and prioritize urgent problems

How It Works:

On launch, users choose between User or Admin mode.

Users can submit, view, search, and filter alerts.

Admins have additional powers to update, delete, clear, and export alerts.

All alerts are stored in a persistent file (alerts_data.ser) so data is preserved across sessions.

Built-in safety measures prevent accidental data loss.

Target Audience:

Community Members: Residents who want to report issues

Administrators: Municipal authorities or local managers overseeing problem resolution

Core Java Learning Outcomes:

Practical implementation of OOP principles

Working with Java Collections and Streams

File handling and persistent data storage

Exception handling and input validation

Building role-based access control systems in Java
