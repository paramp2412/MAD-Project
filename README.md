# 📱 Campus Lost and Found Mobile Application

A clean, native Android mobile application designed to help college students and campus staff report, track, and recover lost or found belongings across university premises. Built with **Java** and **SQLite** for offline, high-reliability operation without requiring internet connectivity or user login.

---

## 🌟 Key Features

- 📢 **Report Lost or Found Belongings**: Easy-to-use form with item title, category selector, location landmark, date picker, description, and contact info.
- 📋 **Interactive Dashboard**: Real-time display of all reported items with distinct color-coded badges:
  - 🔴 **LOST** (Coral Red)
  - 🟢 **FOUND** (Emerald Green)
  - 🟣 **CLAIMED** (Purple)
- 🔍 **Real-Time Instant Search**: Dynamic filtering as you type across item names, descriptions, and locations.
- 🏷️ **Multi-Dimensional Filtering**:
  - Filter chips: `All Items`, `Lost`, `Found`, `Claimed`
  - Category dropdown: *Electronics, Books, ID Cards, Keys, Bags, Clothing, Bottles, Sports, etc.*
- 📊 **Live Statistical Counters**: Visual header counts for Total, Lost, Found, and Claimed items.
- 📞 **One-Tap Quick Contact**: Dial the finder or owner directly using Android's native dialer integration.
- ✏️ **Full CRUD Operations**: Create reports, Read records, Update details, Delete entries, or Mark as Claimed/Recovered.
- 💾 **100% Offline Local Storage**: All records persist safely on device using SQLite (`SQLiteOpenHelper`).

---

## 🛠️ Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java (JDK 8 / 17 / 22) | Core application business logic |
| **IDE** | Android Studio | Official Android development environment |
| **Database** | SQLite (`SQLiteOpenHelper`) | Embedded relational database |
| **UI Framework** | Material Components & AndroidX | Material Design 3 cards, chips, text inputs, dialogs |
| **Min SDK** | API Level 24 (Android 7.0+) | Runs on 95%+ of active Android devices |
| **Target SDK** | API Level 34 (Android 14) | Modern Android standards compliant |
| **Internet / Auth** | Not Required | Completely offline & privacy-friendly |

---

## 📂 Project Architecture & Directory Structure

```text
MDA Project/
├── app/
│   ├── build.gradle                          # App module build dependencies
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml           # Manifest declaring activities & theme
│   │   │   ├── java/com/campus/lostandfound/
│   │   │   │   ├── SplashActivity.java       # Launch screen with auto-seed demo data
│   │   │   │   ├── MainActivity.java         # Dashboard with search, filter chips, RecyclerView
│   │   │   │   ├── AddEditItemActivity.java  # Input form for adding/editing items
│   │   │   │   ├── ItemDetailActivity.java   # Detail view, dialer intent, delete & claim actions
│   │   │   │   ├── model/
│   │   │   │   │   └── Item.java             # Entity model class
│   │   │   │   ├── database/
│   │   │   │   │   └── DatabaseHelper.java   # SQLiteOpenHelper managing table and CRUD
│   │   │   │   └── adapter/
│   │   │   │       └── ItemAdapter.java      # RecyclerView Adapter & ViewHolder
│   │   │   └── res/
│   │   │       ├── drawable/                 # Vector icons, badges, card backgrounds
│   │   │       ├── layout/                   # Activity XMLs and card row layouts
│   │   │       └── values/                   # colors.xml, strings.xml, arrays.xml, themes.xml
│   │   └── test/java/com/campus/lostandfound/
│   │       └── ItemModelTest.java            # Unit tests for data integrity & state logic
├── build.gradle                              # Root build file
├── settings.gradle                           # Subproject inclusions
├── gradle.properties                         # JVM arguments and AndroidX flags
├── PROJECT_REPORT.md                         # Formal academic mini-project report
├── VIVA_AND_DEMO_GUIDE.md                    # Team roles, contribution & viva questions
└── README.md                                 # Setup and project guide
```

---

## 🚀 How to Open and Run in Android Studio

### Prerequisites
- [Android Studio](https://developer.android.com/studio) (Hedgehog, Iguana, Koala, or newer).
- JDK 17 or higher (bundled with Android Studio).
- Android Emulator or physical Android device enabled with USB Debugging.

### Step-by-Step Instructions
1. **Open Android Studio**.
2. Click **Open** (or `File` > `Open...`).
3. Browse to `d:\Sem 7 project\MDA Project` and click **OK**.
4. Allow Android Studio to sync Gradle dependencies (requires internet connection for first-time Gradle sync).
5. Select an Android Virtual Device (AVD Emulator) or connect your physical phone via USB.
6. Click the green **Run (Play)** button or press `Shift + F10`.
7. The app will launch with pre-seeded demo records demonstrating real-world campus scenarios!

---

## 🧪 Testing

Unit test suite is included in `app/src/test/java/com/campus/lostandfound/ItemModelTest.java`.
To run tests in Android Studio:
- Right-click `ItemModelTest.java` and select **Run 'ItemModelTest'**.
- Or via terminal: `./gradlew testDebugUnitTest`.
