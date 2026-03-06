You are an expert Android software architect.

Design and scaffold a complete Android application project called **DiMento** using **Kotlin + Jetpack Compose** with **Clean Architecture + MVVM**.

The goal is to create a **production-quality codebase** with proper architecture, linting, formatting, documentation, and development standards.

The app must be **offline-first** and store all data locally.

---

# App Overview

App Name: **DiMento**

Tagline: **RRR – Record, Reflect, Reason**

Concept:

A personal event journaling application where users can record events related to:

Past
Now
Future

Users can later **export events to CSV** to run **AI reasoning outside the app**.

---

# Technology Requirements

Language: Kotlin

UI: Jetpack Compose

Architecture:

Clean Architecture + MVVM

Local Database:

Room (SQLite)

Notifications:

AlarmManager or WorkManager

Speech to Text:

Android SpeechRecognizer API

Export:

CSV file generation to Downloads folder

Minimum SDK: 24
Target SDK: Latest stable

Gradle: Kotlin DSL

---

# Mandatory Engineering Standards

The project must include the following engineering standards:

## Code Quality

Use:

* ktlint for formatting
* detekt for static analysis
* spotless for formatting enforcement

Enable:

* auto-format on build
* strict lint rules

Follow:

* Kotlin official coding conventions
* meaningful naming
* small composable functions
* SOLID principles

---

# Documentation Files

Create the following project documentation:

### agents.md

Define AI agents responsible for parts of the system:

Example agents:

UIAgent
DatabaseAgent
SearchIndexAgent
NotificationAgent
SpeechAgent
ExportAgent

Each agent must describe:

* responsibility
* input
* output
* dependencies

---

### copilot-instructions.md

Provide instructions for GitHub Copilot about how to generate code in this repository.

It should enforce:

* Clean Architecture boundaries
* MVVM structure
* naming conventions
* Compose best practices
* repository pattern
* testability

Copilot must never generate code outside the allowed architecture layers.

---

# Project Architecture

Follow strict Clean Architecture.

Folder structure:

app/

data/
database/
dao/
entity/
repository/
index/

domain/
model/
repository/
usecase/

presentation/
screens/
components/
viewmodel/

core/
notifications/
speech/
export/
indexing/
utils/

di/

---

# Core Feature Requirements

## Home Screen

The home screen must contain three **large circular buttons** vertically centered.

Buttons:

Past
Now
Future

UI requirements:

* minimal design
* center aligned
* circular buttons
* Compose implementation

---

# Event Data Model

Event fields:

id (primary key)

recorded_date
event_date
completed_date (nullable)

event_text (max 200 chars)

event_type

Values:

PAST
NOW
FUTURE

---

# Event Entry

When user selects:

## Past

User selects a past date.

recorded_date = current date
event_date = selected date

Fields:

Date Picker
Text input
Voice recording

---

## Now

event_date = current date automatically.

User only enters text or voice.

---

## Future

User selects a future date.

After saving:

Schedule a **notification reminder**.

---

# Notification Behavior

When reminder triggers:

Notification shows:

Mark Completed
Delete Event

Mark Completed:

event_type changes:

FUTURE → NOW

completed_date = current date

Delete:

remove event from database.

---

# Search System

Implement a **Reverse Hash Map Search Index**.

Concept:

keyword → list of event IDs

Example:

meeting → [2, 18, 44]
gym → [5, 22]

Whenever events are:

* added
* updated
* deleted

The index must update accordingly.

This should be implemented in:

core/indexing

---

# Manage Events Screen

Screen must allow:

Search events
Filter events
Update events
Delete events
Export filtered events to CSV

---

# CSV Export

Export location:

Downloads folder

CSV columns:

id
recorded_date
event_date
completed_date
event_type
event_text

---

# Compose UI Guidelines

Use:

Material3

Follow:

* stateless composables
* state hoisting
* preview functions
* reusable components

Common components:

CircularActionButton
EventTextField
VoiceInputButton
EventCard

---

# Dependency Injection

Use **Hilt**.

All repositories and services must be injectable.

---

# Testing

Add support for:

Unit tests

Use:

JUnit
MockK

Tests should cover:

UseCases
Repository logic
Search indexing

---

# Output Required

Generate:

1. Full project folder structure
2. Gradle files
3. lint configuration
4. detekt configuration
5. spotless configuration
6. agents.md
7. copilot-instructions.md
8. base Compose screens
9. Room database setup
10. repository interfaces
11. use cases
12. viewmodels

The generated project must compile and run in Android Studio.

Focus on:

* maintainability
* scalability
* clean architecture boundaries
* modular design

Avoid unnecessary complexity.
