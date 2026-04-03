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

### Layer Responsibilities

#### Presentation

* Jetpack Compose UI
* ViewModels
* NO business logic

#### Domain

* Use cases ONLY
* Pure Kotlin (no Android deps)
* All business rules live here

#### Data

* Room DB
* DAO + Repository implementations

---

## 📦 Project Structure

```
app/
├── data/
├── domain/
├── presentation/
├── notifications/
└── core/
```

---

## 🧩 Core Features

### 1. Memory Groups

* Create / rename / delete groups
* Support for **Name** and **Description** (≤200 chars)
* Default group: **General**
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
* completed_date (nullable)
* type: PAST / TODAY / FUTURE

👉 Type is **derived from date (not manually set)**

---

## 4. Event Creation

**Inputs:**

* Text
* Date
* Voice (optional)

**Flow:**

* From FAB → create → then choose group(s)
* Inside chat → direct assign

---

### 5. Forwarding

* Always creates a **copy**
* New event ID
* Assigned to new group(s)

---

### 6. Notifications

Use **WorkManager**

Trigger when:

* `event_date == today`

Actions:

* Mark complete
* Delete

---

### 7. Search

* Global + group-specific
* Use **reverse index**

Structure:

```
keyword → [(eventId, groupId)]
```

Update index on:

* Create
* Update
* Delete

---

### 8. CSV Export

Export all event fields to:

* Downloads folder

---

## ⚡ ViewModel Rules

* Use StateFlow
* Expose immutable UI state
* Only call use cases
* No direct DB access

---

## 🎨 UI Rules (Compose)

* Use LazyColumn (chat)
* Stable keys required
* Avoid recomposition issues

Components:

* EventBubble
* DateHeader
* InputBar
* GroupItem

---

## 🌙 Dark & Light Theme Rules

**Critical Theme Implementation:**

All theme colors are now **centralized and responsive** to system dark/light mode. The background is a simple solid color.

### Color Structure

**Light Theme (Surface = #F8FAF9, OnSurface = #2D3433):**
* Background: #CCCCCC (80% white) - Solid color, no bubbles/accents.
* Header (Top Bar): Uses `MaterialTheme.colorScheme.onSurface` (Dark text: #2D3433)
* Group Names: Uses `MaterialTheme.colorScheme.onSurface`
* Containers: Subtle, low-contrast backgrounds
* Text: Dark for readability

**Dark Theme (Background = #0D1110, Surface = #1B2420):**
* Background: #333333 (80% black) - Solid color, no bubbles/accents.
* Header (Top Bar): Uses `MaterialTheme.colorScheme.onSurface` (Light text: #E2E8E6)
* Group Names: Uses `MaterialTheme.colorScheme.onSurface`
* Containers: Subtle with reduced saturation (#262E2A for PAST)
* Text: Light, bright colors (#E2E8E6)

### Event Type Colors (Automatic Theme Switching)

| Event Type | Light | Dark |
|---|---|---|
| **PAST** | surfaceContainerLow (subtle gray) | surfaceContainerLow (dark gray) |
| **TODAY** | primary gradient (bright green) | primary gradient (bright green) |
| **FUTURE** | tertiaryContainer (pale yellow) | tertiaryContainer (muted olive) |

### Theme Usage Rules (DO's & DON'Ts)

#### ✅ DO:
* Use `MaterialTheme.colorScheme.onSurface` for headers and group names.
* Use `onSurfaceVariant` for secondary text (last event text, timestamps).
* Use `MaterialTheme.colorScheme.primary` for future event indicators.
* Use `MaterialTheme.colorScheme.*` for all colors (automatic switching)
* Use `ThemeUtils.kt` helper functions for consistent colors
* Call `getEventContainerColor()`, `getEventTextColor()` for event styling

#### ❌ DON'T:
* Use complex background generation logic (300+ lines of bubble/gradient logic removed).
* Hardcode color values unless specifically required for the solid background values.
* Use color constants directly (use MaterialTheme instead)
* Mix light theme colors in dark theme components

### Component Integration

**EventBubble:**
* Container: `MaterialTheme.colorScheme.surfaceContainerLow` (PAST)
* TODAY gradient uses `colorScheme.primary` (auto-brightens in dark)
* FUTURE uses `colorScheme.tertiaryContainer` (auto-muted in dark)
* Text color: `getEventTextColor(type)` ensures contrast

**Background & Containers:**
* Always use `MaterialTheme.colorScheme.background`
* Simplified to solid colors per theme.

### Debugging Theme Issues

* Check if component uses `MaterialTheme.colorScheme.*` → if not, file is broken
* If text is hard to read in dark → ensure using `onSurface`, not `surface`
* Test in both light + dark system themes (Settings → Display)

---

## ⚠️ Edge Cases (ALWAYS consider)

* Midnight transition (future → today)
* Event rescheduling
* Large datasets (1000+ events)
* Empty states
* Null values
* Notification resync

---

## 🚫 Hard Constraints

* ❌ No backend / server
* ❌ No cloud sync
* ❌ No Flutter / cross-platform
* ❌ No business logic in UI
* ❌ No tight coupling

---

## 🧠 How You Should Respond

When helping me:

### DO:

* Break features into **steps + layers**
* Suggest **data flow + architecture**
* Highlight **edge cases**
* Keep solutions **simple and scalable**

### DON'T:

* Dump full code unless asked
* Over-engineer
* Add unnecessary abstractions

---

## 🧩 When Code is Requested

* Use:

  * Room
  * WorkManager
  * Jetpack Compose
* Keep it:

  * Modular
  * Testable
  * Production-ready

---

## 🧭 Guiding Principle

If unsure:

* Choose **simplicity**
* Keep logic in **domain**
* Keep UI **dumb**

---

## ✅ What Changed (Redundancy Removed)

From your original file :

* Removed repeated architecture rules
* Merged duplicate “DO NOT” sections
* Unified product + engineering instructions
* Eliminated repeated explanations of same concepts
* Converted into **actionable AI instructions instead of documentation**
* **Updated for solid background theme, group descriptions, and WhatsApp-style selection.**

---
