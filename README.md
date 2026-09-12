<div align="center">

<img src="art/logo.png" width="120" alt="MyTuition Logo"/>

# MyTuition
### Your offline tuition coaching companion

[![Release](https://img.shields.io/github/v/release/mohitraj8503/MyTuition?color=6C48FF&label=Latest%20Release&style=for-the-badge)](https://github.com/mohitraj8503/MyTuition/releases/latest)
[![Android](https://img.shields.io/badge/Android-5.0%2B-3DDC84?style=for-the-badge&logo=android)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Backend-FFCA28?style=for-the-badge&logo=firebase)](https://firebase.google.com)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

**A beautiful, production-ready Android app for offline coaching/tuition centres.**  
Students track daily schedules, homework, attendance, and fees — all in one place.

[📲 Download APK](https://github.com/mohitraj8503/MyTuition/releases/latest) · [🐛 Report Bug](https://github.com/mohitraj8503/MyTuition/issues) · [✨ Request Feature](https://github.com/mohitraj8503/MyTuition/issues)

</div>

---

## 📸 Screenshots

> **Demo mode available** — tap "Try demo mode →" on login to explore without signing up.

| Login | Home | Homework | Schedule |
|-------|------|----------|----------|
| Claymorphic OTP login | Next class + timeline | Subject-wise homework | Weekly date chips |

---

## ✨ Features

### 🏫 For Students
- **📅 Daily Schedule** — See today's classes with subject, teacher, time & room number
- **⏰ Next Class Card** — Countdown to your upcoming class (e.g. "Room 4B, Today 5:00 PM")
- **📝 Homework Tracker** — Subject-wise pending homework with swipe-to-complete
- **✅ Attendance** — Live attendance percentage with class-by-class history
- **💰 Fee Status** — Monthly fee status (Paid / Pending / Overdue) + Razorpay payment
- **📢 Announcements** — Institute-wide notices and updates
- **🎮 Demo Mode** — Explore the full app without signing in

### 🔥 Tech Highlights
- **Claymorphic UI** — Soft clay design system with bouncy spring animations
- **Real-time Data** — Firestore real-time listeners for live schedule updates  
- **Offline-first** — Firestore offline persistence — works without internet
- **Firebase Auth** — Phone OTP + Google Sign-In
- **Cloud Functions** — Razorpay payment integration via Firebase Functions (asia-south1)
- **Clean Architecture** — MVVM + Repository pattern + Hilt-ready DI container

---

## 🏗️ Architecture

```
MyTuition/
├── app/src/main/java/com/example/mytuition/
│   ├── core/
│   │   ├── data/
│   │   │   ├── model/          # Firestore document models + mappers
│   │   │   ├── repository/     # Firebase repository implementations
│   │   │   └── FirebaseConfig  # Firebase singleton setup
│   │   ├── designsystem/       # Claymorphic design tokens + components
│   │   │   └── components/     # NextClassCard, ShortcutRow, FeeStatusBottomSheet…
│   │   ├── di/                 # AppContainer (dependency injection)
│   │   ├── domain/
│   │   │   ├── model/          # UserSession, HomeData, FeeStatus, HomeSummary…
│   │   │   └── repository/     # Repository interfaces
│   │   └── navigation/         # AppNavGraph + Routes
│   └── feature/
│       ├── auth/               # Login (Phone OTP + Google + Demo Mode)
│       ├── home/               # HomeScreen + HomeViewModel
│       ├── homework/           # HomeworkScreen + HomeworkViewModel
│       ├── classdetail/        # Class Detail Screen
│       ├── subjects/           # Subject List + Subject Detail
│       ├── calendar/           # Calendar Screen
│       └── profile/            # Profile Screen
├── functions/                  # Firebase Cloud Functions (TypeScript)
│   └── src/
│       ├── index.ts            # 11 callable functions
│       └── seed.ts             # Firestore seed script
└── firestore.rules             # Role-based security rules
```

**Pattern:** `UI (Compose) → ViewModel → Repository → Firestore/Firebase`

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17+
- Firebase project (or use Demo Mode without one)

### 1. Clone
```bash
git clone https://github.com/mohitraj8503/MyTuition.git
cd MyTuition
```

### 2. Firebase Setup (optional — skip to use Demo Mode)
1. Create a project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add Android app with package `com.aistudio.mytuition.abxycd`
3. Download `google-services.json` → place in `app/`
4. Enable **Authentication** (Phone + Anonymous)
5. Enable **Firestore** in asia-south1 region
6. Deploy rules: `firebase deploy --only firestore:rules`

### 3. Seed Demo Data (optional)
```bash
cd functions
npm install
npm run build
# Set FIREBASE_PROJECT_ID env then:
node lib/seed.js
```

### 4. Build & Run
```bash
./gradlew assembleDebug
# Install on connected device/emulator:
adb install app/build/outputs/apk/debug/app-debug.apk
```
Or simply open in Android Studio and press **▶ Run**.

---

## 🎮 Demo Mode

No Firebase project? No problem.

1. Open the app
2. On Login screen, tap **"Try demo mode →"**
3. You're in — full app with Chanakya Classes data

**Demo account:**
| Field | Value |
|-------|-------|
| Student | Mohit Raj |
| Institute | Chanakya Classes |
| Grade | Class 10 – Section A |
| Roll No | 27 |
| Next Class | Creative Sketching – Room 4B, 5:00 PM |

---

## 🔌 Firebase Cloud Functions

All backend logic runs in **asia-south1** region:

| Function | Description |
|----------|-------------|
| `onUserLogin` | Auto-provisions user doc on first login |
| `getHomeData` | Aggregates schedule + homework + fee for home screen |
| `getClassDetail` | Single class session details |
| `getHomeworkList` | Subject-filtered homework with submission status |
| `markHomeworkComplete` | Marks homework done + syncs to Firestore |
| `getSubjects` | Student's enrolled subjects |
| `getCalendarData` | Monthly session calendar |
| `getProfileData` | Profile + attendance + fee summary |
| `createRazorpayOrder` | Creates payment order via Razorpay API |
| `verifyRazorpayPayment` | Verifies payment signature + updates fee status |
| `sendNotification` | FCM push notifications |

---

## 🛡️ Firestore Security Rules

Role-based access control across all collections:
- **Students** — read own data only
- **Parents** — read their children's data
- **Authenticated** — read institute/announcements
- **Admin** — full read/write (via custom claims)

---

## 📦 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Auth | Firebase Authentication (Phone OTP, Google, Anonymous) |
| Database | Cloud Firestore (offline persistence) |
| Backend | Firebase Cloud Functions (TypeScript, Node 20) |
| Payments | Razorpay (via Cloud Functions) |
| Push | Firebase Cloud Messaging (FCM) |
| Storage | Firebase Storage |
| Fonts | Poppins (Google Fonts) |
| Animations | Compose Spring Animations (claymorphic) |

---

## 🗺️ Roadmap

- [x] Claymorphic Login with Phone OTP + Demo Mode  
- [x] Home Screen — Next Class Card + Timeline + Shortcuts  
- [x] Homework Tracker with completion sync  
- [x] Firebase backend with Firestore repositories  
- [x] Fee Status BottomSheet + Razorpay integration  
- [x] Offline-first with Firestore persistence  
- [ ] Parent view (see child's progress)  
- [ ] Push notifications for class reminders  
- [ ] Notes/PDF viewer per subject  
- [ ] Biometric login  
- [ ] Release on Play Store  

---

## 🤝 Contributing

1. Fork the repo
2. Create your feature branch (`git checkout -b feat/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feat/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

Distributed under the MIT License. See [`LICENSE`](LICENSE) for more information.

---

<div align="center">

Made with ❤️ by [Mohit Raj](https://github.com/mohitraj8503)

**MyTuition** — *Helping students stay on top of their offline coaching journey.*

</div>
