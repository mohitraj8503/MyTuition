<div align="center">

<img src="art/logo_rounded_white.png" width="130" style="border-radius: 20px; background-color: #ffffff; padding: 6px;" alt="MyTuition Logo"/>

# MyTuition
### Your complete offline tuition & coaching companion

[![Release](https://img.shields.io/github/v/release/mohitraj8503/MyTuition?color=6C48FF&label=Latest%20Release&style=for-the-badge)](https://github.com/mohitraj8503/MyTuition/releases/latest)
[![Version](https://img.shields.io/badge/Version-2.0.0-6C48FF?style=for-the-badge)](https://github.com/mohitraj8503/MyTuition)
[![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=for-the-badge&logo=android)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![PocketBase](https://img.shields.io/badge/PocketBase-Self--Hosted-B8860B?style=for-the-badge&logo=sqlite)](https://pocketbase.io)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

**A calm, fast, and production-ready Android application for offline tuition centers and coaching institutes.**  
Designed with a cohesive claymorphic design system and powered by a self-hosted, lightweight PocketBase backend.

[📲 Download APK](https://github.com/mohitraj8503/MyTuition/releases/latest) · [🐛 Report Bug](https://github.com/mohitraj8503/MyTuition/issues) · [✨ Request Feature](https://github.com/mohitraj8503/MyTuition/issues)

</div>

---

## 🚀 What's New in Version 2.0

- 🏛️ **Full PocketBase Backend Migration**: Replaced legacy Firebase stack with self-hosted PocketBase (`v0.23+`) running on SQLite + Go. Sub-millisecond queries, zero vendor lock-in, and full data sovereignty.
- 👨‍🏫 **Teacher Dashboard & Management**:
  - **Live Class Management**: Real-time batch session tracking, attendance marking, student rosters.
  - **Homework Review**: Manage homework assignments, review student submissions, track overdue work.
  - **Fee Collection**: Manual offline fee collection tracking (Cash / UPI / Cheque) with instant status update and student reminders.
  - **Teacher Earnings & Analytics**: Monthly collection summaries and batch breakdowns.
- 🧑‍🎓 **Revamped Student Experience**:
  - **Dynamic Schedule**: Mon–Sun dynamic week schedule computing real classes.
  - **Smart Next Class Finder**: Intelligent fallback to next upcoming class across batches.
  - **Homework Tracker**: Live status updates directly synced to backend.
  - **Attendance & Fees**: Real attendance rates and offline payment tracking.
- 🎨 **Enhanced Claymorphic Design System**:
  - Refined soft clay pill buttons, embossed input fields, and reactive pastel gradient blobs.
  - New "My Tuition" branded wordmark and redesigned username + password authentication screen.
  - 1-tap instant Demo Student & Demo Teacher modes for live previews.

---

## 📸 Overview

> **Instant Demo Mode available** — tap "Try demo mode →" or "👨‍🏫 Try Teacher Demo Mode" on the login screen to explore both student and teacher portals without registering.

| Student Home | Teacher Home | Attendance & Roster | Fee Management |
|:---:|:---:|:---:|:---:|
| Next class countdown, today's schedule & stats | Batch cards, today's timeline & pending reviews | 1-tap mark attendance (Present / Absent / Excused) | Offline fee status with manual mark-paid |

---

## ✨ Features

### 🧑‍🎓 For Students
- **📅 Daily Schedule & Calendar** — View upcoming classes with batch name, teacher, time, and room details.
- **⏰ Next Class Indicator** — Dynamic countdown banner to the next scheduled lecture.
- **📝 Homework Tracker** — Subject-wise assignments with completion tracking.
- **✅ Attendance Records** — Real-time attendance percentage calculated across all enrolled sessions.
- **💰 Offline Fee Status** — Track paid, pending, and overdue tuition dues.

### 👨‍🏫 For Teachers
- **📋 Batch Overview & Management** — View enrolled students, active batches, and batch schedules.
- **✅ Attendance Management** — Quick attendance marking per session with present/absent counts.
- **✍️ Homework Assignment & Grading** — Create homework, review submissions, and track pending student tasks.
- **💵 Offline Payment Recording** — Mark student tuition fees as paid (Cash, Direct UPI, etc.) with custom payment notes.
- **📈 Monthly Earnings Summary** — Track collected fees vs. pending dues per batch.

### ⚡ Tech Highlights
- **100% Native Jetpack Compose** — Modern declarative UI with Compose 2024 standards.
- **Clean Architecture & MVVM** — Strictly decoupled Presentation, Domain, Data, and Network layers.
- **Room Database + DataStore** — Offline persistence and token management.
- **Retrofit & Moshi** — Type-safe, high-performance REST client communicating with PocketBase API.
- **Self-Hosted VPS Ready** — Complete Caddy reverse-proxy + systemd configuration included for Hostinger VPS or any Linux server.

---

## 🏗️ System Architecture

MyTuition follows **Clean Architecture** with strict Unidirectional Data Flow (UDF):

```
┌──────────────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER (UI)                        │
│  Jetpack Compose Screens  ──(StateFlow / Events)──► ViewModels   │
└─────────────────────────────────┬────────────────────────────────┘
                                  │
                                  ▼
┌──────────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER (BUSINESS)                     │
│   Use Cases / Interactors  ──►  Domain Models  ──►  Interfaces   │
└─────────────────────────────────┬────────────────────────────────┘
                                  │
                                  ▼
┌──────────────────────────────────────────────────────────────────┐
│                      DATA LAYER (STORAGE & NET)                  │
│  PocketBase Repositories  ──► Room DB / DataStore / PocketBase   │
└─────────────────────────────────┬────────────────────────────────┘
                                  │
                                  ▼
┌──────────────────────────────────────────────────────────────────┐
│                   BACKEND LAYER (POCKETBASE)                     │
│  PocketBase v0.23+ ──► pb_hooks (JS) ──► SQLite (WAL Mode)       │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📦 Directory Structure

```
MyTuition/
├── app/src/main/java/com/example/mytuition/
│   ├── core/
│   │   ├── data/
│   │   │   ├── local/          # Room DB (AppDatabase, DAOs, Entities) & DataStore
│   │   │   ├── network/        # PocketBase API client, AuthInterceptor, DTOs
│   │   │   └── repository/     # PocketBaseAuthRepository, PocketBaseTeacherRepository, etc.
│   │   ├── designsystem/       # Claymorphic tokens, pastel gradients, buttons, cards
│   │   ├── di/                 # AppContainer (dependency injection root)
│   │   ├── domain/             # Core business models, user roles, repository interfaces
│   │   └── navigation/         # Jetpack Navigation Compose (Routes, AppNavGraph)
│   └── feature/
│       ├── auth/               # Username/password login, demo launchers
│       ├── splash/             # Startup token check & role-based routing
│       ├── home/               # Student Home (Next class, schedule, quick actions)
│       ├── homework/           # Student Homework list & detail views
│       ├── calendar/           # Student monthly schedule grid
│       ├── profile/            # Student profile & settings
│       └── teacher/            # Teacher portal:
│           ├── home/           # Teacher dashboard & stats
│           ├── attendance/     # Attendance marking screen
│           ├── homework/       # Homework management & submission reviewer
│           ├── students/       # Batch student list & student profiles
│           └── earnings/       # Fee collection & payment marker
├── pb_hooks/                   # PocketBase Server Extensions (JavaScript runtime)
│   ├── mytuition_routes.js     # Custom aggregation API endpoints (/home-data, /teacher-home, etc.)
│   ├── mytuition_cron.js       # Background jobs (overdue fees, daily class reminders)
│   ├── mytuition_record_hooks.js # Record validation & cascade actions
│   └── seed.js                 # Realistic seed data for demo environments
└── DEPLOY_HOSTINGER_VPS.md     # Full VPS deployment guide with systemd & Caddy HTTPS
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Koala / Hedgehog
- JDK 17+
- Android SDK 24+ (Android 7.0+)

### 1. Clone the Repository
```bash
git clone https://github.com/mohitraj8503/MyTuition.git
cd MyTuition
```

### 2. Configure Environment
Create a `.env` file in the project root:
```properties
POCKETBASE_URL=https://api.techtomorrow.in
```
*(Or point to your local PocketBase instance at `http://10.0.2.2:8090` when testing with Android Emulator).*

### 3. Build & Run
```bash
./gradlew assembleDebug
```
Install the generated APK onto your connected device or emulator:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🖥️ PocketBase Backend Setup

To host your own backend:

1. Download PocketBase from [pocketbase.io](https://pocketbase.io/docs/).
2. Place the contents of `pb_hooks/` into your PocketBase `pb_hooks/` directory.
3. Start the server:
   ```bash
   ./pocketbase serve --http="0.0.0.0:8090"
   ```
4. Access the admin dashboard at `http://127.0.0.1:8090/_/` to create your superuser account.
5. See [DEPLOY_HOSTINGER_VPS.md](DEPLOY_HOSTINGER_VPS.md) for automated systemd service setup and Caddy reverse proxy with automatic SSL.

---

## 🎮 Demo Mode

Want to test without setting up a backend server?

1. Open the app.
2. On the Login screen:
   - Tap **"Try demo mode →"** to test as **Student** (Ayush Singh / Mohit Raj).
   - Tap **"👨‍🏫 Try Teacher Demo Mode"** to test as **Teacher** (Dr. Aalvina Fatehi).
3. Experience the full interactive UI instantly.

---

## 📄 License

Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.

<div align="center">

Made with ❤️ by [Mohit Raj](https://github.com/mohitraj8503)

**MyTuition v2.0** — *Empowering educators, inspiring students.*

</div>
