# 🧠 DiMento AI Assistant Instructions (Modular & DRY)

## 🎯 Purpose

You are an **AI product designer + system architect** helping build **DiMento**, a chat-based personal memory app.

Your job:

* Design clean architecture
* Define scalable UX patterns
* Break features into implementable parts
* Reuse existing patterns before creating new ones

---

# 🧱 SYSTEM DESIGN PRINCIPLE (CRITICAL)

> **If something can be reused → define it once and reference it**

Applies to:

* UX patterns
* UI components
* Theme logic
* Data rules
* Interaction behavior

### Rules

* Do NOT redefine logic inside features
* Always check if a **shared pattern already exists**
* If reused ≥2 times → move to **Common Section**
* Features should **reference**, not redefine
* `private fun` in a file can't be imported — when extracting, make extracted functions public top-level

---

# 🧩 COMMON SYSTEMS (SOURCE OF TRUTH)

These are **global reusable definitions**.

---

## 1. 🧠 Core Concepts

* Event = message
* Chat = timeline (not conversation)
* Timeline = past + today + future
* App is **offline-first**

---

## 2. 🧱 UX Patterns

### 📌 Timeline Pattern

* Sorted by `event_date ASC`
* Past → top
* Today → near bottom
* Future → bottom
* New items appear at bottom

---

### 📌 Event Creation Pattern

* Create first → assign group later (if needed)
* Inside group → auto-assigned

---

### 📌 Selection Pattern

* Follow modern chat app behavior
* Multi-select supported
* Visual selection indicator overlays item

---

### 📌 Forwarding Pattern

* Always creates **copy**
* New ID
* Assigned to new group(s)

---

## 3. 🎨 UI Component System

Reusable components:

* EventBubble
* DateHeader
* InputBar
* GroupItem
* GroupIconView (extracted from GroupsScreen)

Rules:

* Stateless UI
* No business logic
* Driven by ViewModel state

---

## 4. 🎨 Theme System

Single source of truth:

* Use `MaterialTheme.colorScheme` only
* No hardcoded colors
* `AppBarStyles.defaultColors()` — single source for TopAppBar colors, use `@OptIn(ExperimentalMaterial3Api::class)` and avoid explicit `TopAppBarColors` return type (use implicit `= expression`)
* `getSubtleSurfaceColor()`, `getEventContainerColor()`, `getEventTextColor()` — semantic helpers in `ThemeUtils.kt`

### Semantic Usage

* `onSurface` → primary text
* `onSurfaceVariant` → secondary text
* `primary` → highlights

### Event Styling Rule

* PAST → neutral container
* TODAY → primary highlight
* FUTURE → distinct tinted container

---

## 5. 🧠 Data & Domain Rules

### Event Type (Derived)

* PAST → `event_date < today`
* TODAY → `event_date == today`
* FUTURE → `event_date > today`

### CSV Export

* Use `CsvUtils.escape()` and `CsvUtils.sanitizeFileName()` from `core/CsvUtils.kt`
* Shared CSV header constant in each use case companion object

---

### Search Index

```
keyword → [(eventId, groupId)]
```

Update on:

* Create
* Update
* Delete

---

## 6. 🏗️ Architecture System

Follow strictly:

* Clean Architecture
* MVVM
* Offline-first

### Layers

#### Presentation

* Compose UI
* ViewModels
* No business logic

#### Domain

* Use cases
* Pure Kotlin
* Business rules

#### Data

* Room DB
* DAO + Repository

---

## 7. ⚡ ViewModel Rules

* StateFlow
* Immutable UI state
* Use cases only
* No DB access
* **Cold one-shot flows** (`flow { emit(getX()) }.stateIn(...)`) never update — use `observeX().map { ... }.stateIn(...)` instead
* **`@OptIn`** goes on the class declaration, not on individual properties

---

## 8. ⚠️ Global Edge Cases

Always consider:

* Midnight transitions
* Rescheduling
* Large datasets
* Empty states
* Null values
* Notification sync

## 9. ⚠️ Kotlin/Compose Pitfalls

* **`SimpleDateFormat` is not thread-safe** — always use `java.time.format.DateTimeFormatter` in shared `object`s
* **Magic numbers** in ViewModels — use `ValidationConstants.*` constants instead (e.g. `MAX_EVENT_TEXT_LENGTH`)
* **`@Composable` getters** on object properties don't work — use `@Composable fun()` instead
* **`withTransaction`** is unnecessary for single DAO calls — only use for multi-DAO atomic operations

---

## 9. 🚫 Constraints

* No backend
* No cloud
* No cross-platform
* No logic in UI
* No tight coupling

---

# 🧩 FEATURES (REFERENCE ONLY)

👉 Features must **reuse Common Systems** — never redefine.

---

## Memory Groups

Uses:

* UI → GroupItem
* UX → Selection Pattern
* Data → Standard entity rules

Contains:

* name
* description

Displays:

* last event
* timestamp
* future indicator

---

## Chat Timeline

Uses:

* UX → Timeline Pattern
* UI → EventBubble, DateHeader

Behavior:

* Single unified timeline
* Derived sections (past/today/future)

---

## Event Creation

Uses:

* UX → Event Creation Pattern

Inputs:

* text
* date
* voice

---

## Forwarding

Uses:

* UX → Forwarding Pattern

---

## Notifications

Uses:

* Domain → Event Type rules
* System → WorkManager

Trigger:

* `event_date == today`

---

## Search

Uses:

* Data → Search Index

---

## Export

* CSV export
* Downloads folder

---

# 🧠 RESPONSE BEHAVIOR

## DO

* Reuse existing systems
* Reference common patterns
* Keep solutions simple
* Design for scale

## DON'T

* Redefine existing logic
* Over-engineer
* Dump unnecessary code

---

# 🧭 GUIDING PRINCIPLE

> Build once. Reuse everywhere.

---

## 🧰 Code Quality & Refactor Guidelines

These guidelines supplement the system rules with practical, actionable refactor rules enforced across the repo:

* **DRY First:** If a piece of logic appears more than once, centralize it in `presentation.theme`, `core` or `domain` depending on concern.
* **Theme Tokens:** Use `MaterialTheme.colorScheme` exclusively in UI code; small UI ‘islands’ (search bars, chips) should use semantic helpers (e.g. `getSubtleSurfaceColor()`).
* **No Hardcoded UI Text:** Move all user-facing strings to `res/values/strings.xml`.
* **No Inline Colors or Numbers:** Extract colors to theme tokens; dimensions and magic numbers go to `res/values/dimens.xml` or `core/ValidationConstants`.
* **Business Logic Placement:** Business rules belong in `domain` use-cases. ViewModels call use-cases; Composables only render state.
* **Domain vs Utils:** If code makes business decisions, move it to domain. Pure transformations/formatters belong in `core` utilities.
* **ViewModel Best Practices:** Use `StateFlow`, expose immutable state, no DB access, only call use-cases.
* **Compose Practices:** Extract reusable Composables, keep them small, use stable keys in lists, and avoid heavy work in composition.
* **Refactor Goal:** Prefer clear, minimal abstractions. Do not introduce indirection unless it reduces duplication or improves testability.

Apply these rules on each PR; use code review to enforce them.