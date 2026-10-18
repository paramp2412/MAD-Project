# 🎓 VIVA, PRESENTATION SCRIPT & TEAM EVALUATION GUIDE
## Campus Lost and Found Mobile Application
**Course:** Mobile Application Development Technologies (MAD)

---

## 1. TEAM ROLES & INDIVIDUAL CONTRIBUTIONS BREAKDOWN
*(Prepared for 4–5 Team Members as per Section 2 of Course Guidelines)*

| Member Name | Assigned Role | Specific Modules & Technical Contributions |
| :--- | :--- | :--- |
| **Member 1 (Team Leader)** | Project Coordination & Architecture | Overall project planning, Git version control, activity transitions, intent management, and final submission packaging. |
| **Member 2 (UI/UX Developer)** | Screen Design & Layouts | Designed XML layouts (`activity_main.xml`, `activity_add_edit_item.xml`, `activity_item_detail.xml`, `item_lost_found_card.xml`), custom badge drawables, color scheme, and typography. |
| **Member 3 (Application Developer)** | Core Logic & Adapter | Developed `MainActivity.java`, `ItemAdapter.java` (ViewHolder pattern), search TextWatcher filter, chip group listener, and category spinner logic. |
| **Member 4 (Database Developer)** | SQLite & Data Management | Implemented `DatabaseHelper.java` (`SQLiteOpenHelper`), table creation, CRUD operations (`insertItem`, `getItemById`, `getItems`, `updateItem`, `deleteItem`, `markAsClaimed`), and data seeding. |
| **Member 5 (Tester / Documentation)** | Testing & Final Report | Formulated test cases (`ItemModelTest.java`), conducted UI and validation testing, authored `PROJECT_REPORT.md`, README, and presentation slides. |

---

## 2. 15-MINUTE PRESENTATION SCRIPT
*(Matches the exact time allocations mandated in Section 11 of the guidelines)*

### Part 1: Introduction & Problem Statement (2 Minutes)
> *"Respected Professor and evaluators, good morning. Misplacing items like ID cards, calculators, chargers, and notebooks on campus is an everyday nuisance. Today, students rely on chaotic WhatsApp groups where messages get buried within an hour, or bulletin boards that nobody checks. We identified the need for a dedicated, offline-first mobile utility. We built the **Campus Lost and Found App**—a fast, native Android app that lets students report, search, and reclaim lost items in seconds, completely offline without any login hassles."*

### Part 2: Design & Technology Stack (2–3 Minutes)
> *"For our technology stack, we chose **Java** with **Android Studio** and **SQLite** as the embedded database. We chose SQLite because it runs locally on the user's phone, meaning our app never fails even in basement laboratories or during campus Wi-Fi outages. Architecturally, we followed the **Model-View-Adapter (MVA)** pattern. The UI is built using **Google Material Design 3**, utilizing Material Cards, choice chips, outlined text fields, and distinct color-coded badges: Red for Lost, Green for Found, and Purple for Claimed."*

### Part 3: Major Features Walkthrough (3 Minutes)
> *"Our application is built around four major technical pillars:*
> 1. *Full CRUD Operations: Adding reports with validation, editing details, marking as claimed, and deleting obsolete reports.*
> 2. *Multi-criteria Filtering: Real-time dynamic search as you type, coupled with category dropdowns and status chips.*
> 3. *Direct Dialer Integration: Instead of copying phone numbers, a single tap on 'Call Contact' triggers Android's native dialer via `Intent.ACTION_DIAL`.*
> 4. *Live Statistics: The top dashboard displays instant counts of Lost, Found, and Claimed items to keep campus awareness transparent."*

### Part 4: Live Application Demonstration (5 Minutes)
1. **Launch:** Show the Splash screen; mention that realistic demo data is pre-seeded automatically on first install.
2. **Dashboard:** Point out the top counters (e.g., 2 Lost, 2 Found). Scroll through the RecyclerView cards.
3. **Search & Filter:**
   - Type `"Charger"` in the search bar — show the list immediately filter down to the Dell charger.
   - Tap the `"Found"` chip — show that only found items appear.
   - Select `"ID Cards & Documents"` in the category dropdown.
4. **Add Item:** Tap the `+` FAB. Intentionally tap "Save" with empty fields to show validation errors. Then fill in a sample lost item (e.g., "Casio Watch", "Sports Complex", date, contact phone) and save. Show it appears immediately at the top of the list with a Red badge.
5. **Detail & Intent:** Tap the item card. Show the formatted details. Tap "Call Contact" to show the Android dialer popping up.
6. **Status Update:** Tap "Mark as Claimed / Recovered". Show how the badge turns purple and the Claim button disables.
7. **Delete:** Tap "Delete", show the confirmation dialog, confirm, and verify the item is removed.

### Part 5: Testing, Results & Future Scope (2 Minutes)
> *"We executed 12 comprehensive test cases spanning form validation, SQLite persistence, and intent handling, all passing with 100% success. For future scope, we plan to add camera photo attachments and optional Firebase cloud syncing for university security desks."*

### Part 6: Q&A / Viva Defense (2–3 Minutes)
*(Refer to section 3 below for expected questions and answers)*

---

## 3. TOP 10 VIVA QUESTIONS & MODEL ANSWERS

#### Q1: Why did you choose SQLite over SharedPreferences?
> **Answer:** SharedPreferences is designed strictly for simple key-value pairs (like user settings or theme preferences). Our project deals with structured, relational entities with multiple attributes (title, location, date, category, status). SQLite provides relational indexing, SQL filtering (`WHERE`, `LIKE`, `ORDER BY`), and ACID transaction support.

#### Q2: Why did you choose SQLite instead of Firebase or a Cloud API?
> **Answer:** As highlighted in our project proposal, students often misplace items in areas with weak connectivity, such as basement computer labs, underground parking, or remote campus fields. SQLite guarantees 100% offline uptime, zero hosting costs, zero network latency, and complete privacy because no personal data leaves the device.

#### Q3: What is the purpose of `DatabaseHelper` and `SQLiteOpenHelper`?
> **Answer:** `SQLiteOpenHelper` is an Android framework class that manages database creation and version management. In `DatabaseHelper`, we override `onCreate()` to execute our `CREATE TABLE` query and `onUpgrade()` to handle schema migrations. It also encapsulates our CRUD methods (`insertItem`, `getItems`, `updateItem`, `deleteItem`), keeping database operations cleanly separated from our UI activities.

#### Q4: How does your search feature update the list in real-time?
> **Answer:** We attach an `addTextChangedListener` with a `TextWatcher` to the `EditText` search bar. Whenever the text changes in `onTextChanged()`, it triggers `loadItemsFromDatabase()`, which passes the query string to `dbHelper.getItems()`. The SQLite query uses `LIKE %query%` across `title`, `description`, and `location`. The adapter's `updateList()` method is then called, which invokes `notifyDataSetChanged()`.

#### Q5: What is the ViewHolder pattern in RecyclerView and why is it better than ListView?
> **Answer:** In traditional `ListView`, `findViewById()` was called repeatedly as the user scrolled, which is computationally expensive. The `ViewHolder` pattern caches view references in memory. When a card scrolls off-screen, its view container is recycled for the next item rather than being re-inflated, resulting in smooth 60 FPS scrolling and minimal memory consumption.

#### Q6: How does the "Call Contact" feature work without asking for dangerous permissions?
> **Answer:** We used `Intent.ACTION_DIAL` with `Uri.parse("tel:" + phoneNumber)`. Unlike `Intent.ACTION_CALL`, which dials immediately and requires the dangerous runtime permission `android.permission.CALL_PHONE`, `ACTION_DIAL` simply opens the system dialer with the number pre-filled. The user retains final control to press the call button, which is standard Android security best practice and requires no extra manifest permissions.

#### Q7: What are `ContentValues` and `Cursor` in Android SQLite?
> **Answer:** 
> - `ContentValues` is a key-value store used to prepare data for `insert()` and `update()` operations, mapping column names to values.
> - `Cursor` is an interface that provides random read-write access to the result set returned by an SQLite database query, allowing us to iterate over rows using `moveToFirst()` and `moveToNext()`.

#### Q8: How did you implement form validation?
> **Answer:** In `AddEditItemActivity.java`, before creating or updating the record, we verify each mandatory field using `TextUtils.isEmpty()`. If a field is blank, we use `textInputLayout.setError("Field is required")` and `requestFocus()`, halting execution so corrupt or incomplete data never reaches the database.

#### Q9: How do you handle activity lifecycles so the list refreshes after adding or editing an item?
> **Answer:** We place our database query (`loadItemsFromDatabase()`) inside `onResume()` of `MainActivity.java` rather than solely in `onCreate()`. Whenever a user returns from `AddEditItemActivity` or `ItemDetailActivity`, `onResume()` is automatically triggered by the Android OS, ensuring the RecyclerView always displays the most up-to-date data.

#### Q10: How could this application be scaled in a real university deployment?
> **Answer:** To scale campus-wide, we could integrate Firebase Firestore or a REST API backend with Room DB for offline caching. We could also add student college email authentication (`@college.edu`) and image attachment support using Android's Storage Access Framework.
