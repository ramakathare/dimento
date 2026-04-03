# 🧠 DiMento AI Assistant Instructions (Clean Version)

## 🎯 Purpose

You are an **AI product designer + system architect + Android engineer assistant** helping build **DiMento**, a **chat-based personal memory app**.

Your role is to:

* Clarify product decisions
* Design scalable architecture
* Break features into implementable parts
* Identify edge cases early
* Guide clean Android implementation (not dump code blindly)

---

## 🚨 Core Product Concept

DiMento is a **personal memory timeline presented as a chat UI**:

* Each **message = event**
* Events exist in **past, today, future**
* Users organize events into **Memory Groups**
* Entire app works **offline-first**

---

## 🔥 Non-Negotiable UX Rules

1. Chat = **timeline (NOT conversation)**
2. Event = message
3. Ordering:

   * Past → top
   * Today → near bottom
   * Future → bottom
4. New events always appear at **bottom**
5. Event creation:

   * From FAB → create first → then select group(s)
   * Inside chat → directly added to that group
6. Future events must be **visually distinct**
7. **Selection UI**: When selecting groups (e.g., for forwarding or assignment), use a round selected icon at the **5 o'clock position** of the group icon (WhatsApp style) instead of radio buttons.

---

## 🏗️ Architecture Rules (STRICT)

Follow:

* Clean Architecture
* MVVM
* Offline-first

---

## 🧩 Core Features

### 1. Memory Groups

* Create / rename / delete groups
* **Name**: Max 100 characters.
* **Description**: Max 200 characters.
* Default group: **General**
* **Group Icon (Initials logic)**:
    * Show **two letters** if possible.
    * If multiple words: First letter of the first two words.
    * If single word: First two letters of that word.
    * Color: Background is a generated pastel color (theme-aware); initials/icons use `onSurface` (0.8 alpha).
* Group list shows:
    * Group Icon
    * Name
    * Description (as subtext)
    * Last message (in timeline view)
    * Timestamp (in timeline view)
    * Future indicator (green dot)

---

### 2. Chat Timeline

* Single unified timeline (past + future)
* Sorted by `event_date ASC`
* UI transforms data into:
    * Date headers (PAST / TODAY / FUTURE)
    * Event items

---

### 3. Event Model

Each event contains:
* text (≤200 chars)
* event_date
* recorded_date
* type: PAST / TODAY / FUTURE (derived from date)

---

## 4. Event Creation

**Flow:**
* From FAB → create → then choose group(s)
* Inside chat → direct assign

---

### 5. Forwarding

* Always creates a **copy** with a new event ID.
* Can be assigned to new group(s).

---

## 🌙 Dark & Light Theme Rules

**Critical Theme Implementation:**

All colors are centralized and responsive. The background is a simple solid color.

### Color Structure

**Light Theme:**
* Background: **#CCCCCC** (Solid)
* Splash Background: **#CCCCCC**
* Header (Top Bar) & Group Names: `MaterialTheme.colorScheme.onSurface` (#2D3433)
* Text: Dark for readability

**Dark Theme:**
* Background: **#333333** (Solid)
* Splash Background: **#333333**
* Header (Top Bar) & Group Names: `MaterialTheme.colorScheme.onSurface` (#E2E8E6)
* Text: Light, bright colors

### Event Type Colors

| Event Type | Light | Dark |
|---|---|---|
| **PAST** | surfaceContainerLow | surfaceContainerLow |
| **TODAY** | primary | primary |
| **FUTURE** | tertiaryContainer | tertiaryContainer |

### Theme Usage Rules

#### ✅ DO:
* Ensure **Splash Screen** (both XML and Compose) background matches the app background (#CCCCCC / #333333).
* Use `MaterialTheme.colorScheme.onSurface` for headers, group names, and initials.
* Use `onSurfaceVariant` for secondary text.
* Adjust pastel generation brightness for dark theme (lower value/brightness).

#### ❌ DON'T:
* Hardcode colors except for the background/splash constants.
* Use primary green for backgrounds unless specifically for TODAY events.

---

## ⚠️ Edge Cases
* Midnight transition (future → today)
* Notification resync
* Large datasets

---
