# MyTuition — Kotlin Android App
## Full Build Prompt + Architecture Specification

> **Purpose:** This document is a complete, copy-paste-ready prompt for an AI coding agent (Gemini, Claude Code, Cursor, Android Studio AI, etc.) to build the **MyTuition native Android app in Kotlin**.
>
> **Source of truth:** The product direction in this prompt is based on the provided MyTuition business plan. The business plan describes a student/parent-facing native Android app backed by an institute/tutor automation platform, with a clean, minimal, to-do-list-style experience. It also identifies Homework, Subjects, Library, Calendar, Fee Status and Messages as core student/parent app areas.  
> fileciteturn0file0L10-L21  
> fileciteturn0file0L54-L64
>
> The business plan also states that the app should initially be built against mock data, then wired to the existing LMS backend, while the WhatsApp automation layer is developed in parallel.  
> fileciteturn0file0L120-L137

---

# 1. PRODUCT VISION

Build **MyTuition**, a modern Android app for students and parents using tuition/coaching services.

The app must feel:

- Minimal
- Calm
- Fast
- Premium
- Extremely easy to understand
- More like a focused productivity app than a traditional education ERP
- Card-driven but **not visually bloated**
- Simple enough that a parent with low technical familiarity can use it
- Strong on hierarchy, whitespace and typography
- Designed for daily use, especially around homework, classes, fees, attendance and communication

The product should **not** look like a complicated school ERP.

The business plan explicitly positions the student/parent app around:

- Homework
- Subjects
- Library
- Calendar
- Fee status
- Messages

The tutor/admin side is a separate product surface and should not be mixed into the student app navigation.  
fileciteturn0file0L54-L74

---

# 2. PRIMARY GOAL FOR THIS BUILD

Start with a polished native Android application in **Kotlin + Jetpack Compose**.

## Phase 1 goal

Implement:

1. App foundation
2. Splash screen
3. Authentication flow
4. Login screen
5. Session handling
6. Student home/dashboard
7. Bottom navigation
8. Homework
9. Subjects
10. Library
11. Calendar
12. Fees
13. Messages
14. Profile/settings
15. Mock repository/data layer
16. Clean architecture that can later connect to the existing LMS API
17. Navigation guards and logged-in/logged-out state
18. Loading, empty, error and success states
19. Reusable design system/components
20. Unit and UI tests for critical flows

Do **not** build the backend inside this Android project.

The Android app must use an abstraction layer so the backend can later be swapped from mock data to the real LMS API.

The product roadmap specifically calls for building the app against mock data first and then reusing/extending the existing LMS API instead of building a second backend.  
fileciteturn0file0L125-L130

---

# 3. IMPORTANT PRODUCT RULES

## 3.1 No student self-registration

The business model assumes that the tutor/institute creates the student account and the student/parent simply logs in.  
fileciteturn0file0L61-L64

Therefore:

- Do NOT show "Create account" as the primary login CTA.
- Do NOT build public student registration.
- The authentication flow should be designed around an account already created by the institute.
- Support a future invite/activation flow, but do not implement unnecessary onboarding in v1.

---

# 4. TECHNOLOGY STACK

Use a modern Android stack.

## Core

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX
- Kotlin Coroutines
- Kotlin Flow / StateFlow
- Navigation Compose
- ViewModel
- Hilt for dependency injection
- Room for local persistence
- Retrofit or Ktor Client for future API integration
- Kotlin Serialization or Moshi for DTO serialization
- DataStore for lightweight preferences/session flags
- Coil for remote/local images
- JUnit
- AndroidX Test
- Compose UI testing

## Minimum architectural principles

- Single-activity architecture
- Compose UI
- Unidirectional data flow
- ViewModel per feature/screen where useful
- Repository pattern
- Domain models separated from API/DB models
- Dependency inversion
- Feature-oriented package structure
- No business logic inside composables
- No direct network calls from UI
- No direct Room calls from composables
- No global mutable singleton state
- No hardcoded backend behavior in UI code

---

# 5. RECOMMENDED ARCHITECTURE

Use:

**Clean Architecture + MVVM + Repository + UDF**

High-level flow:

```text
UI
 ↓
ViewModel
 ↓
Use Case
 ↓
Repository Interface
 ↓
Repository Implementation
 ↓
Remote / Local Data Source
 ↓
API / Room / DataStore
```

Example:

```text
LoginScreen
    ↓
AuthViewModel
    ↓
LoginUseCase
    ↓
AuthRepository
    ↓
AuthRemoteDataSource
    ↓
LmsApi
```

For mock mode:

```text
LoginScreen
    ↓
AuthViewModel
    ↓
LoginUseCase
    ↓
AuthRepository
    ↓
MockAuthRepository
```

This must allow:

```text
Mock Backend
     ↓
Real LMS Backend
```

without rewriting UI screens.

---

# 6. PROJECT PACKAGE STRUCTURE

Use a feature-oriented architecture.

Recommended structure:

```text
com.my tuition.app
```

Replace the package name with a valid final Android package ID, for example:

```text
com.mytuition.app
```

Structure:

```text
app/
└── src/main/java/com/mytuition/app/

    ├── MainActivity.kt
    ├── MyTuitionApp.kt

    ├── core/
    │   ├── common/
    │   │   ├── Result.kt
    │   │   ├── UiState.kt
    │   │   └── Constants.kt
    │   │
    │   ├── designsystem/
    │   │   ├── Color.kt
    │   │   ├── Typography.kt
    │   │   ├── Shape.kt
    │   │   ├── Theme.kt
    │   │   ├── Dimens.kt
    │   │   └── Components.kt
    │   │
    │   ├── navigation/
    │   │   ├── AppNavGraph.kt
    │   │   ├── Routes.kt
    │   │   └── NavigationExtensions.kt
    │   │
    │   ├── network/
    │   │   ├── ApiConfig.kt
    │   │   ├── NetworkModule.kt
    │   │   └── AuthInterceptor.kt
    │   │
    │   ├── database/
    │   │   ├── AppDatabase.kt
    │   │   ├── DatabaseModule.kt
    │   │   └── dao/
    │   │
    │   ├── datastore/
    │   │   ├── SessionDataStore.kt
    │   │   └── DataStoreModule.kt
    │   │
    │   └── di/
    │       ├── RepositoryModule.kt
    │       ├── UseCaseModule.kt
    │       └── AppModule.kt
    │
    ├── data/
    │   ├── remote/
    │   │   ├── api/
    │   │   ├── dto/
    │   │   └── datasource/
    │   │
    │   ├── local/
    │   │   ├── dao/
    │   │   ├── entities/
    │   │   └── datasource/
    │   │
    │   ├── repository/
    │   │   └── implementation/
    │   │
    │   └── mapper/
    │
    ├── domain/
    │   ├── model/
    │   ├── repository/
    │   └── usecase/
    │
    └── feature/
        ├── splash/
        │   ├── SplashScreen.kt
        │   └── SplashViewModel.kt
        │
        ├── auth/
        │   ├── login/
        │   │   ├── LoginScreen.kt
        │   │   ├── LoginViewModel.kt
        │   │   └── LoginUiState.kt
        │   ├── session/
        │   │   └── SessionViewModel.kt
        │   └── components/
        │       ├── AuthHeader.kt
        │       ├── AuthTextField.kt
        │       └── PrimaryButton.kt
        │
        ├── home/
        │   ├── HomeScreen.kt
        │   ├── HomeViewModel.kt
        │   ├── HomeUiState.kt
        │   └── components/
        │
        ├── homework/
        │   ├── HomeworkScreen.kt
        │   ├── HomeworkViewModel.kt
        │   ├── HomeworkDetailScreen.kt
        │   └── components/
        │
        ├── subjects/
        │   ├── SubjectsScreen.kt
        │   ├── SubjectDetailScreen.kt
        │   ├── SubjectsViewModel.kt
        │   └── components/
        │
        ├── library/
        │   ├── LibraryScreen.kt
        │   ├── ResourceDetailScreen.kt
        │   ├── LibraryViewModel.kt
        │   └── components/
        │
        ├── calendar/
        │   ├── CalendarScreen.kt
        │   ├── CalendarViewModel.kt
        │   └── components/
        │
        ├── fees/
        │   ├── FeesScreen.kt
        │   ├── FeeDetailScreen.kt
        │   ├── FeesViewModel.kt
        │   └── components/
        │
        ├── messages/
        │   ├── MessagesScreen.kt
        │   ├── MessageDetailScreen.kt
        │   ├── MessagesViewModel.kt
        │   └── components/
        │
        └── profile/
            ├── ProfileScreen.kt
            ├── ProfileViewModel.kt
            └── components/
```

---

# 7. APP NAVIGATION

## Root navigation

```text
Splash
   │
   ├── Logged in → Main App
   │
   └── Logged out → Login
```

Main app:

```text
Home
Homework
Subjects
Library
Calendar
Fees
Messages
Profile
```

Use bottom navigation for the most important daily destinations.

Recommended initial bottom nav:

```text
Home
Homework
Calendar
Fees
Profile
```

Secondary areas can be accessed from Home or a "More" surface without overcrowding the bottom bar.

Important:

Do not put 7–8 navigation items in the bottom bar.

The UI should remain minimal and focused.

---

# 8. FIRST SCREEN — SPLASH SCREEN

Build a short, polished splash screen.

## Visual direction

- White/light neutral background
- MyTuition logo/wordmark centered
- Subtle animation only
- No unnecessary loading spinner
- Very short duration
- Accessible content description

Logic:

```text
Splash
 ↓
check stored auth/session
 ↓
if valid session → Home
else → Login
```

Do not make the splash depend on a slow network request.

---

# 9. FIRST LOGIN PAGE — DETAILED SPEC

## Goal

The login page should immediately communicate:

> "Your tuition, organized."

It should feel trustworthy for both:

- Students
- Parents

It must not look like a generic corporate ERP login.

---

## Login layout

Recommended hierarchy:

```text
[Logo]

Welcome back

Your classes, homework and tuition updates
all in one place.

[Mobile number / email field]

[Password / OTP field depending on auth mode]

[Primary CTA]

Forgot password?

Need help?
```

Because the business plan leaves the OTP-vs-password decision open, implement the authentication layer so either method can be plugged in later.  
fileciteturn0file0L187-L190

For the UI prototype, use one of these approaches:

### Preferred prototype

Use:

```text
Mobile number
[ Continue ]
```

Then show:

```text
OTP Verification
[ _ _ _ _ _ _ ]
[ Verify ]
[ Resend OTP ]
```

Do not implement real SMS delivery in this prototype.

Use a mock OTP.

Example:

```text
OTP = 123456
```

Keep this strictly inside the mock authentication repository.

---

# 10. LOGIN SCREEN UI DETAILS

## Background

Use a clean neutral background.

Avoid:

- giant gradients
- multiple illustrations
- noisy education graphics
- excessive shadows
- excessive borders

---

## Logo

Place the MyTuition logo near the top.

Spacing should create a premium first impression.

---

## Heading

Example:

```text
Welcome back
```

Typography:

- Large
- Medium or semibold
- High contrast
- Left aligned

---

## Supporting text

Example:

```text
Stay on top of your classes, homework and payments.
```

Use a smaller muted text style.

---

## Input field

Mobile number field:

```text
+91 | Enter mobile number
```

Requirements:

- Numeric keyboard
- India-friendly formatting
- Validation
- Error message
- Clear focus state
- Accessible semantics

Validation:

```text
Empty:
"Please enter your mobile number"

Invalid:
"Enter a valid mobile number"
```

Do not use aggressive red UI.

---

# 11. LOGIN CTA

Primary button:

```text
Continue
```

Button states:

```text
Idle
Loading
Success
Error
Disabled
```

During loading:

```text
[ CircularProgressIndicator ] 
```

Avoid layout jumping.

---

# 12. OTP SCREEN

After Continue:

```text
Verify your number

We sent a 6-digit code to
+91 XXXXX XXXXX

[ 1 ][ 2 ][ 3 ][ 4 ][ 5 ][ 6 ]

Resend code in 30s

[ Verify ]
```

Features:

- Auto-focus
- Numeric keyboard
- Paste support
- Auto-read OTP when available later
- Countdown
- Resend
- Invalid OTP state

Mock behavior:

```text
123456 → success
any other value → error
```

Do not expose this mock OTP prominently in the production UI.

---

# 13. LOGIN SUCCESS

On successful login:

```text
Login
 ↓
Persist session
 ↓
Navigate to Home
 ↓
Clear auth stack
```

Back button must not return to login.

---

# 14. AUTH DOMAIN MODEL

Create:

```kotlin
data class UserSession(
    val userId: String,
    val instituteId: String,
    val studentId: String,
    val role: UserRole,
    val accessToken: String?,
    val refreshToken: String?,
    val expiresAt: Long?
)
```

Role:

```kotlin
enum class UserRole {
    STUDENT,
    PARENT
}
```

Design the repository so additional roles can be introduced later without rewriting navigation.

---

# 15. AUTH REPOSITORY

Interface:

```kotlin
interface AuthRepository {

    suspend fun requestOtp(
        mobileNumber: String
    ): Result<Unit>

    suspend fun verifyOtp(
        mobileNumber: String,
        otp: String
    ): Result<UserSession>

    suspend fun getCurrentSession(): UserSession?

    suspend fun logout()

}
```

Mock implementation:

```text
MockAuthRepository
```

Later:

```text
LmsAuthRepository
```

No UI screen should know which implementation is active.

---

# 16. HOME SCREEN

The Home screen should answer:

> "What do I need to know today?"

Do not turn it into a dashboard with 20 cards.

Recommended order:

```text
Good morning, Mohit

Class 10-A
Today, Monday

[ Today's Classes ]

[ Homework Due ]

[ Fee Status ]

[ Quick Actions ]

Recent updates
```

---

# 17. HOME SCREEN CONTENT

## Greeting

Dynamic based on time:

```text
Good morning
Good afternoon
Good evening
```

Name below or beside it.

---

## Today's classes

Example:

```text
Today's classes

5:00 PM
Mathematics
Chapter 8 — Quadratic Equations

6:30 PM
Physics
Current Electricity
```

Use timeline/card style.

---

## Homework summary

Example:

```text
Homework

2 tasks due today
```

CTA:

```text
View homework →
```

---

## Fee summary

Example:

```text
Fees

₹2,500
Due on 10 Sep
```

Do not make this feel like a banking app.

---

## Recent messages

Display only the latest 2–3 important updates.

---

# 18. HOME UI PRINCIPLES

Do:

- Strong typography
- Large whitespace
- Subtle borders
- Small shadows only when needed
- Rounded but not toy-like cards
- Consistent iconography

Avoid:

- 3D illustrations
- excessive gradients
- giant icons
- excessive emojis
- dashboard clutter
- noisy charts

---

# 19. HOME MOCK MODEL

```kotlin
data class HomeSummary(
    val student: Student,
    val todayClasses: List<ClassSession>,
    val homeworkDueCount: Int,
    val outstandingFee: Money?,
    val recentMessages: List<Message>
)
```

---

# 20. HOME SCREEN STATES

Implement all:

```text
Loading
Success
Empty
Error
Offline with cached data
```

Example empty state:

```text
You're all caught up.

No homework or updates for today.
```

---

# 21. HOMEWORK

Homework is a primary feature.

The business plan explicitly lists Homework as one of the core student-facing app areas.  
fileciteturn0file0L56-L58

## List screen

Group by:

```text
Today
Tomorrow
Upcoming
Completed
```

Each task:

```text
Mathematics

Solve questions 1–10

Due today

[ Pending ]
```

---

# 22. HOMEWORK DETAIL

Display:

```text
Subject
Title
Description
Assigned on
Due date
Teacher
Attachments
Submission status
```

Actions:

```text
Mark complete
Submit
Open attachment
```

For v1, submission can remain mock-only.

---

# 23. SUBJECTS

Subject list example:

```text
Mathematics
Physics
Chemistry
English
Computer Science
```

Each subject:

```text
Subject name
Teacher
Next class
Homework count
```

---

# 24. SUBJECT DETAIL

Sections:

```text
Overview
Upcoming classes
Homework
Resources
Recent activity
```

Future-ready fields can include:

```text
Attendance percentage
Performance
Teacher notes
```

Do not build a complex analytics dashboard in v1.

---

# 25. LIBRARY

Library = shared academic resources.

Categories:

```text
Notes
PDFs
Videos
Question Papers
Assignments
Other
```

Each item:

```text
Title
Subject
Type
Uploaded date
Size
```

Actions:

```text
Open
Download
Share
```

Use a repository abstraction for file loading.

---

# 26. CALENDAR

Calendar should combine:

- Classes
- Homework deadlines
- Exams
- Institute events

Use visual categories but keep the UI restrained.

Default:

```text
This Week
```

Primary daily content:

```text
Mon
Tue
Wed
Thu
Fri
Sat
```

---

# 27. FEES

The business plan identifies fee automation as a core commercial wedge on the tutor/institute side, while the student/parent app includes fee status.  
fileciteturn0file0L56-L58  
fileciteturn0file0L102-L106

Student-side fee screen:

```text
Fees

Outstanding
₹2,500

Due date
10 Sep 2026

Status
Pending

Payment history
June      ₹2,500   Paid
July      ₹2,500   Paid
August    ₹2,500   Pending
```

For v1:

- Show fee status
- Show history
- Show payment state
- Keep actual payment wiring behind a repository
- Do not make in-app payments a mandatory dependency for the first UI prototype

The business plan explicitly lists "no in-app payments" among v1 scope guardrails.  
fileciteturn0file0L177-L181

---

# 28. MESSAGES

Messages should focus on institute/teacher updates.

Do not build a full custom chat system in v1.

Example:

```text
Messages

Mathematics
Homework updated
Today

Admin
Tomorrow's class starts at 5 PM
Yesterday
```

Message detail:

```text
Title
Sender
Date/time
Body
Attachments
```

Do not implement a new chat SDK unless separately required.

The business plan explicitly calls out avoiding a built-in chat SDK in v1 scope.  
fileciteturn0file0L177-L181

---

# 29. PROFILE

Profile:

```text
Student photo
Name
Class
Institute
Student ID
Parent contact
```

Actions:

```text
Edit profile
Notifications
Language
Help & Support
Privacy
Logout
```

---

# 30. DATA MODELS

Create domain models similar to:

```kotlin
data class Student(
    val id: String,
    val name: String,
    val className: String,
    val section: String?,
    val instituteId: String,
    val profileImageUrl: String?
)
```

```kotlin
data class Subject(
    val id: String,
    val name: String,
    val teacherName: String,
    val iconKey: String?
)
```

```kotlin
data class Homework(
    val id: String,
    val subjectId: String,
    val title: String,
    val description: String,
    val assignedAt: Instant,
    val dueAt: Instant?,
    val status: HomeworkStatus,
    val attachments: List<Resource>
)
```

```kotlin
enum class HomeworkStatus {
    PENDING,
    SUBMITTED,
    COMPLETED,
    OVERDUE
}
```

```kotlin
data class ClassSession(
    val id: String,
    val subjectId: String,
    val title: String,
    val startAt: Instant,
    val endAt: Instant,
    val teacherName: String,
    val room: String?
)
```

```kotlin
data class FeeRecord(
    val id: String,
    val monthLabel: String,
    val amount: Long,
    val dueDate: LocalDate,
    val status: FeeStatus
)
```

```kotlin
enum class FeeStatus {
    PAID,
    PENDING,
    OVERDUE
}
```

```kotlin
data class Message(
    val id: String,
    val title: String,
    val senderName: String,
    val body: String,
    val createdAt: Instant,
    val isRead: Boolean
)
```

```kotlin
data class Resource(
    val id: String,
    val title: String,
    val type: ResourceType,
    val url: String?,
    val sizeBytes: Long?
)
```

---

# 31. MONEY MODEL

Do not use floating-point values for currency.

Use minor units:

```kotlin
data class Money(
    val amountMinor: Long,
    val currency: String = "INR"
)
```

Example:

```text
₹2,500
```

Stored as:

```text
250000
```

if using paise.

---

# 32. UI STATE PATTERN

Each feature should have an explicit UI state.

Example:

```kotlin
sealed interface HomeUiState {

    data object Loading : HomeUiState

    data class Success(
        val data: HomeSummary
    ) : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState
}
```

For screens that need offline-first behavior, support cached states.

---

# 33. VIEWMODEL RULES

A ViewModel should:

- Own UI state
- Call use cases
- Handle user events
- Map domain errors into UI-friendly messages
- Avoid Android UI references where possible
- Survive configuration changes

Example:

```kotlin
class LoginViewModel(
    private val requestOtp: RequestOtpUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun submitMobile(number: String) {
        // validation + use case call
    }
}
```

---

# 34. COMPOSABLE RULES

Composable functions should be:

- Small
- Reusable
- Stateless where possible
- Driven by state
- Previewable

Prefer:

```kotlin
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onMobileChanged: (String) -> Unit,
    onContinue: () -> Unit
)
```

instead of putting all state and networking in the composable.

---

# 35. DESIGN SYSTEM

Create a central design system.

## Typography

Define:

```text
Display
Headline
Title
Body
Label
Caption
```

Keep typography confident and modern.

Avoid dozens of text styles.

---

# 36. COLORS

Use a restrained palette.

Base:

```text
Light background
Dark primary text
Muted secondary text
Single brand accent
Success
Warning
Error
```

Do not use a different color for every feature.

The app should feel like one product.

---

# 37. SHAPES

Recommended:

```text
Cards: 16–20dp radius
Buttons: 14–16dp radius
Inputs: 14–16dp radius
Bottom sheet: 24dp top corners
```

Do not over-round everything into pill shapes.

---

# 38. ELEVATION

Prefer borders and spacing over heavy elevation.

Default cards:

```text
subtle border
minimal shadow
```

Avoid:

```text
huge drop shadows
```

The business plan's current UI iteration specifically calls for removal of shadow-heavy treatments and simplification of visual styling.  
fileciteturn0file0L187-L190

---

# 39. ICONOGRAPHY

Use one consistent icon system.

Prefer:

- Material Symbols
- Simple line icons
- Consistent stroke weight

Avoid mixing:

- Material icons
- random SVG icon packs
- emoji icons
- 3D icons

---

# 40. RESPONSIVENESS

Support:

- Small Android phones
- Standard phones
- Large phones
- Tablet layouts where practical

Use:

```text
WindowSizeClass
```

or a clean responsive modifier strategy.

Do not hardcode pixel positions.

---

# 41. ACCESSIBILITY

Implement:

- Content descriptions
- Touch targets ≥ 48dp
- Sufficient contrast
- Screen-reader-friendly labels
- Clear error states
- Semantic headings where appropriate
- No information communicated only through color

---

# 42. OFFLINE-FIRST FOUNDATION

The first prototype uses mock data, but architecture must be ready for offline caching.

Recommended:

```text
Remote source → Repository → Cache
                              ↓
                             Room
                              ↓
                              UI
```

When API integration starts:

```text
UI
 ↓
Use Case
 ↓
Repository
 ├── Remote
 └── Local
```

This avoids rebuilding the app later.

---

# 43. SESSION PERSISTENCE

Store lightweight session information with DataStore.

Do not store sensitive tokens as plain text in arbitrary SharedPreferences.

Use a secure token strategy when real authentication is connected.

At prototype stage:

```text
DataStore
 ↓
isLoggedIn
userId
studentId
```

---

# 44. NETWORK LAYER

Prepare an API abstraction.

Example:

```kotlin
interface MyTuitionApi {

    @POST("auth/request-otp")
    suspend fun requestOtp(
        @Body request: RequestOtpRequest
    ): ApiResponse<Unit>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): ApiResponse<SessionDto>

    @GET("student/home")
    suspend fun getHome(): ApiResponse<HomeDto>

    @GET("student/homework")
    suspend fun getHomework(): ApiResponse<List<HomeworkDto>>

    @GET("student/subjects")
    suspend fun getSubjects(): ApiResponse<List<SubjectDto>>

    @GET("student/library")
    suspend fun getLibrary(): ApiResponse<List<ResourceDto>>

    @GET("student/calendar")
    suspend fun getCalendar(): ApiResponse<CalendarDto>

    @GET("student/fees")
    suspend fun getFees(): ApiResponse<List<FeeDto>>

    @GET("student/messages")
    suspend fun getMessages(): ApiResponse<List<MessageDto>>

}
```

Do not assume these endpoints already exist.

Create them as future placeholders.

---

# 45. MOCK DATA

Create:

```text
MockDataFactory
```

Populate realistic sample data.

Example student:

```text
Mohit Raj
Class 10-A
```

Example subjects:

```text
Mathematics
Physics
Chemistry
English
Computer Science
```

Example homework:

```text
Quadratic Equations Worksheet
Current Electricity Numericals
Chemical Reactions Notes
```

Example fees:

```text
June      Paid
July      Paid
August    Pending
```

Example messages:

```text
"Tomorrow's Mathematics class will begin at 5 PM."
"New Physics worksheet has been uploaded."
```

Use deterministic mock data.

---

# 46. MOCK MODE

Use an app configuration flag.

Example:

```kotlin
enum class DataEnvironment {
    MOCK,
    API
}
```

Development default:

```text
MOCK
```

Production later:

```text
API
```

Do not scatter:

```kotlin
if (mock)
```

throughout UI code.

The dependency graph should decide which repository implementation is active.

---

# 47. DEPENDENCY INJECTION

Use Hilt.

For example:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: MockAuthRepository
    ): AuthRepository
}
```

Later switch implementation:

```text
MockAuthRepository
        ↓
LmsAuthRepository
```

without UI changes.

---

# 48. NAVIGATION ROUTES

Create strongly typed or centrally managed routes.

Example:

```kotlin
object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Otp = "otp"
    const val Home = "home"
    const val Homework = "homework"
    const val HomeworkDetail = "homework/{id}"
    const val Subjects = "subjects"
    const val SubjectDetail = "subjects/{id}"
    const val Library = "library"
    const val ResourceDetail = "library/{id}"
    const val Calendar = "calendar"
    const val Fees = "fees"
    const val FeeDetail = "fees/{id}"
    const val Messages = "messages"
    const val MessageDetail = "messages/{id}"
    const val Profile = "profile"
}
```

Pass IDs, not full objects through navigation.

---

# 49. NAVIGATION SECURITY

If the user is logged out:

```text
Any protected route → Login
```

If login succeeds:

```text
Clear auth route stack
```

If session expires:

```text
Show session expired message
 ↓
Logout
 ↓
Login
```

Do not leave protected screens accessible from back navigation.

---

# 50. ERROR HANDLING

Create standard error types.

```kotlin
sealed interface AppError {

    data object Network : AppError

    data object Unauthorized : AppError

    data object NotFound : AppError

    data object Validation : AppError

    data object Server : AppError

    data object Unknown : AppError
}
```

Map errors into friendly UI copy.

Bad:

```text
HTTP 401
```

Good:

```text
Your session has expired. Please sign in again.
```

---

# 51. LOADING UX

Do not show full-screen spinners everywhere.

Prefer:

- Skeletons
- Content placeholders
- Inline progress
- Button progress

For Home:

```text
Greeting visible
 ↓
content skeletons
 ↓
real data
```

This feels faster.

---

# 52. EMPTY STATES

Each major feature should have a meaningful empty state.

Homework:

```text
Nothing due

You’re all caught up.
```

Library:

```text
No resources yet

Your institute hasn't uploaded resources here yet.
```

Messages:

```text
No new messages

You're all caught up.
```

Fees:

```text
No outstanding fees

Everything is paid.
```

---

# 53. ERROR STATES

Use inline retry.

Example:

```text
Couldn't load homework.

Check your connection and try again.

[ Retry ]
```

Do not destroy the entire screen for a single network failure.

---

# 54. NOTIFICATION ARCHITECTURE

Do not make push notifications a hard dependency for v1.

Prepare for:

- Homework reminders
- Fee reminders
- New message alerts
- Class reminders
- Institute announcements

Future architecture:

```text
FCM
 ↓
NotificationHandler
 ↓
Deep Link
 ↓
Relevant Screen
```

---

# 55. DEEP LINKS

Prepare routes such as:

```text
mytuition://homework/{id}
mytuition://fees
mytuition://message/{id}
```

These will later allow WhatsApp/email/push campaigns to open the relevant app screen.

This is especially relevant because the broader MyTuition product is designed around WhatsApp-native communication and automation.  
fileciteturn0file0L19-L20

---

# 56. SECURITY RULES

Never:

- hardcode production API secrets
- commit tokens
- store plaintext passwords
- log authentication tokens
- log OTPs in release builds
- expose backend admin credentials to the Android app

Use:

```text
BuildConfig
environment variables
secret management
```

for environment-specific values.

---

# 57. BUILD CONFIGURATION

Create:

```text
debug
release
```

Optionally:

```text
mock
staging
production
```

Recommended:

```text
debugMock
debugApi
releaseStaging
releaseProduction
```

Do not overcomplicate Gradle initially; create only the variants that provide actual value.

---

# 58. PACKAGE NAMING

Use:

```text
com.mytuition.app
```

Application name:

```text
MyTuition
```

Minimum SDK: choose a sensible modern baseline that supports the selected Compose/AndroidX versions.

Target SDK: latest stable supported by the project environment.

Do not hardcode an outdated SDK version merely for compatibility.

---

# 59. APP THEME

Use Material 3 theme.

Support:

```text
Light mode
Dark mode
System default
```

However:

- Light mode should be the primary design reference.
- Dark mode should preserve hierarchy.
- Avoid simply inverting colors.
- Define semantic color tokens.

---

# 60. SCREEN PREVIEWS

Every important composable should have Compose previews.

Examples:

```kotlin
@Preview
@Composable
private fun LoginScreenPreview() {}
```

Create previews for:

- Empty
- Loading
- Success
- Error
- Dark mode where practical
- Large font where practical

---

# 61. TESTING STRATEGY

## Unit tests

Test:

- Login validation
- OTP validation
- Use cases
- ViewModels
- Repository mapping
- Session handling
- Fee calculations
- Homework status filtering

Example:

```text
LoginViewModelTest
HomeworkViewModelTest
FeesViewModelTest
SessionViewModelTest
```

---

# 62. UI TESTS

Critical flows:

```text
Launch → Login → OTP → Home

Home → Homework → Homework Detail

Home → Fees

Home → Messages → Message Detail

Profile → Logout → Login
```

Test:

- buttons
- navigation
- validation
- accessibility semantics
- loading states

---

# 63. DATA LAYER TESTS

Test:

```text
DTO → domain model
Entity → domain model
Repository fallback
Mock repository
```

---

# 64. PERFORMANCE

Avoid:

- unnecessary recompositions
- giant LazyColumn item trees
- loading huge images
- network calls during composition
- repeated database queries
- unnecessary state hoisting

Use:

```text
remember
derivedStateOf
stable models
LazyColumn keys
```

only where they provide real benefit.

Do not optimize prematurely.

---

# 65. IMAGE HANDLING

Use Coil.

Support:

```text
loading placeholder
error placeholder
content description
crossfade
```

Profile images should be cached.

---

# 66. FORM VALIDATION

Create reusable validators.

Example:

```kotlin
object Validators {

    fun mobile(number: String): ValidationResult

    fun otp(otp: String): ValidationResult
}
```

Do not duplicate validation strings in multiple screens.

---

# 67. STRING MANAGEMENT

All visible copy must be in:

```text
res/values/strings.xml
```

Do not hardcode user-facing text inside composables.

Prepare the app for future regional language support.

The business plan specifically identifies regional-language support as part of the roadmap.  
fileciteturn0file0L139-L140

---

# 68. LOCALIZATION

Initial language:

```text
English
```

Architecture should support:

```text
English
Hindi
future regional languages
```

Do not concatenate sentences in Kotlin code.

Bad:

```kotlin
"$name has $count tasks"
```

Better:

```text
<string name="homework_due_count">%1$d tasks due</string>
```

---

# 69. UX COPY STYLE

Use:

- short sentences
- calm language
- no unnecessary exclamation marks
- no corporate jargon
- no intimidating warnings
- clear actions

Example:

Good:

```text
Payment is due on 10 Sep.
```

Avoid:

```text
ATTENTION! YOUR PAYMENT IS OVERDUE!!!
```

---

# 70. DESIGN REFERENCE

The aesthetic should be approximately:

```text
Apple-like
+
Notion-like clarity
+
Modern Indian education product
```

But do not copy any protected interface directly.

The intended product position is a clean, minimal to-do-list-style experience rather than a heavyweight ERP.  
fileciteturn0file0L19-L21

---

# 71. WHAT NOT TO BUILD IN V1

Strictly avoid feature creep.

Do NOT build:

- tutor marketplace
- public tutor discovery
- full social network
- public student registration
- built-in chat SDK
- complex ERP administration
- multi-branch management
- advanced analytics dashboard
- AI chatbot
- AI grading directly inside this first Android build
- attendance face recognition directly inside this first Android build
- full payment gateway as a prerequisite
- live video classroom SDK as a prerequisite

These capabilities can be integrated later from the broader platform/modules.

The business plan explicitly frames Markly, PresenceX and LMS capabilities as reusable modules and identifies WhatsApp automation + the student app + integration glue as the new work.  
fileciteturn0file0L23-L31  
fileciteturn0file0L68-L74

---

# 72. FUTURE MODULE INTEGRATION

Architecture should make room for:

```text
MyTuition Android
       │
       ├── LMS
       │
       ├── Markly
       │
       ├── PresenceX
       │
       ├── LiveKit
       │
       └── WhatsApp Automation
```

Do not tightly couple these systems inside UI code.

Use domain-level interfaces and API adapters.

---

# 73. FUTURE AI GRADING INTEGRATION

Later:

```text
Homework
 ↓
Markly grading service
 ↓
Result
 ↓
Student performance
```

Android should consume the resulting data.

Do not place grading algorithms inside the Android app.

---

# 74. FUTURE ATTENDANCE INTEGRATION

Later:

```text
PresenceX
 ↓
Attendance API
 ↓
AttendanceRepository
 ↓
Student Attendance UI
```

Possible future feature:

```text
Attendance
90%
```

Do not implement face-recognition logic in Android.

---

# 75. FUTURE WHATSAPP INTEGRATION

WhatsApp automation belongs on the backend.

Example:

```text
Fee due
 ↓
Automation engine
 ↓
WhatsApp message
 ↓
Deep link
 ↓
MyTuition app
 ↓
Fee status
```

The Android app should not automate WhatsApp messages itself.

The business plan explicitly identifies WhatsApp automation as a backend-side differentiator and warns against building the product around personal-number automation.  
fileciteturn0file0L177-L180

---

# 76. LOGGING

Create centralized logging.

Debug:

```text
Timber
```

Release:

```text
No sensitive logs
```

Never log:

- OTP
- access token
- refresh token
- passwords
- sensitive personal data

---

# 77. ANALYTICS FOUNDATION

Create an abstraction:

```kotlin
interface AnalyticsTracker {
    fun track(
        event: AnalyticsEvent
    )
}
```

Start with a no-op or debug implementation.

Later integrate an analytics provider.

Possible events:

```text
login_started
login_success
home_viewed
homework_opened
homework_completed
fee_viewed
message_opened
logout
```

Do not make analytics calls directly in every composable.

---

# 78. CRASH REPORTING FOUNDATION

Prepare a crash reporting abstraction.

Do not block development on a third-party crash service.

---

# 79. FEATURE FLAGS

Create a simple feature flag mechanism.

Example:

```kotlin
data class FeatureFlags(
    val feesEnabled: Boolean = true,
    val messagesEnabled: Boolean = true,
    val libraryEnabled: Boolean = true,
    val attendanceEnabled: Boolean = false,
    val aiInsightsEnabled: Boolean = false
)
```

This allows modules to be enabled gradually.

---

# 80. IMPLEMENTATION ORDER

Follow this exact build order.

## Phase A — Foundation

1. Create Android project
2. Configure Kotlin
3. Configure Compose
4. Configure Material 3
5. Configure Hilt
6. Configure Navigation
7. Create design system
8. Create core utilities
9. Create mock repositories
10. Create domain models

---

## Phase B — Authentication

11. Splash
12. Session repository
13. Login screen
14. Mobile validation
15. OTP screen
16. Mock OTP verification
17. Persist session
18. Logout
19. Auth navigation guards

---

## Phase C — Core App Shell

20. Main scaffold
21. Bottom navigation
22. Home
23. Global top-level app state
24. Loading/empty/error handling

---

## Phase D — Student Features

25. Homework
26. Homework detail
27. Subjects
28. Subject detail
29. Library
30. Resource detail
31. Calendar
32. Fees
33. Fee detail
34. Messages
35. Message detail
36. Profile

---

## Phase E — Persistence

37. DataStore session
38. Room entities
39. DAOs
40. Local caching
41. Repository offline fallback

---

## Phase F — Quality

42. Tests
43. Accessibility
44. Responsive UI
45. Dark mode
46. Localization foundation
47. Performance check
48. Crash handling
49. Release build validation

---

# 81. FIRST MILESTONE

Do not attempt to build every feature in one giant generation step.

The first milestone must be:

```text
Splash
 ↓
Login
 ↓
OTP
 ↓
Home
```

This flow must be production-quality before expanding.

---

# 82. FIRST MILESTONE ACCEPTANCE CRITERIA

The first milestone is complete only when:

### Splash

- Looks polished
- Loads fast
- Reads saved session
- Routes correctly

### Login

- Validates mobile number
- Shows useful errors
- Has proper loading state
- Has accessible controls
- Looks premium

### OTP

- Numeric entry
- Six digit validation
- Resend countdown
- Mock success path
- Invalid OTP state

### Home

- Loads mock data
- Displays today's information
- Has bottom navigation
- Handles empty/error states

### Navigation

- Login cannot be returned to after success
- Logout returns to login
- App restores logged-in session

---

# 83. MOCK ACCOUNT FOR DEVELOPMENT

Create a development-only mock user:

```text
Mobile:
+91 9876543210

OTP:
123456
```

Keep these values inside mock/test code rather than user-facing documentation in release builds.

---

# 84. SAMPLE HOME DATA

Use deterministic sample data:

```text
Student:
Mohit Raj

Class:
10-A

Institute:
MyTuition Demo Institute
```

Classes:

```text
5:00 PM — Mathematics
6:30 PM — Physics
```

Homework:

```text
Mathematics — Quadratic Equations Worksheet
Physics — Current Electricity Numericals
```

Fees:

```text
August — ₹2,500 — Pending
July — ₹2,500 — Paid
June — ₹2,500 — Paid
```

Messages:

```text
Tomorrow's Mathematics class starts at 5 PM.
New Physics worksheet uploaded.
```

---

# 85. CODE QUALITY RULES

The generated code must:

- Compile
- Avoid placeholder TODOs for core functionality
- Avoid fake APIs mixed into UI
- Avoid duplicated business logic
- Avoid huge files
- Avoid god ViewModels
- Avoid god composables
- Follow Kotlin naming conventions
- Prefer immutable state
- Use suspend functions appropriately
- Use Flow for streams
- Use sealed interfaces for UI states where useful

---

# 86. FILE SIZE GUIDELINE

Avoid files larger than approximately:

```text
300–400 lines
```

unless the file is genuinely a central infrastructure component.

Extract:

- Components
- Mappers
- Validators
- Models
- Navigation helpers
- Use cases

when needed.

---

# 87. COMMENTS

Do not comment obvious Kotlin code.

Good comment:

```kotlin
// Keep session lookup local-first so app launch does not block on network.
```

Bad comment:

```kotlin
// Set loading to true
isLoading = true
```

---

# 88. RESULT-STYLE API

Use a consistent result strategy.

Example:

```kotlin
sealed interface AppResult<out T> {

    data class Success<T>(
        val data: T
    ) : AppResult<T>

    data class Failure(
        val error: AppError
    ) : AppResult<Nothing>
}
```

Avoid throwing generic exceptions across every layer for expected errors.

---

# 89. DATE/TIME

Store backend timestamps as UTC/Instant where appropriate.

Convert to local timezone only for display.

Example:

```text
2026-09-10T12:30:00Z
```

Display:

```text
10 Sep · 6:00 PM
```

Do not store presentation strings as the source of truth.

---

# 90. API DTO RULE

Do not expose DTOs to UI.

Flow:

```text
API DTO
 ↓
Mapper
 ↓
Domain model
 ↓
ViewModel
 ↓
UI
```

Likewise:

```text
Room Entity
 ↓
Mapper
 ↓
Domain model
```

---

# 91. ROOM

Prepare entities for:

```text
StudentEntity
HomeworkEntity
SubjectEntity
ClassSessionEntity
FeeRecordEntity
MessageEntity
ResourceEntity
```

Add DAOs as required.

Cache only data that has a meaningful offline use case.

---

# 92. REPOSITORY RESPONSIBILITIES

Repository should decide:

- remote vs local source
- cache refresh
- error mapping
- synchronization
- data merging

Repository should NOT contain:

- Compose state
- UI strings
- navigation
- button click logic

---

# 93. USE CASE RESPONSIBILITIES

Examples:

```text
RequestOtpUseCase
VerifyOtpUseCase
ObserveSessionUseCase
LogoutUseCase
GetHomeSummaryUseCase
GetHomeworkUseCase
GetHomeworkDetailUseCase
GetSubjectsUseCase
GetLibraryUseCase
GetCalendarUseCase
GetFeesUseCase
GetMessagesUseCase
```

Use cases should remain small and focused.

---

# 94. HOME DATA STRATEGY

For initial prototype:

```text
MockHomeRepository
 ↓
HomeViewModel
 ↓
HomeScreen
```

Later:

```text
LmsHomeRepository
 ├── remote
 └── local
 ↓
HomeViewModel
 ↓
HomeScreen
```

---

# 95. DESIGN SYSTEM COMPONENTS

Build reusable:

```text
MyTuitionButton
MyTuitionOutlinedButton
MyTuitionTextField
MyTuitionCard
MyTuitionTopBar
MyTuitionBottomBar
MyTuitionChip
MyTuitionEmptyState
MyTuitionErrorState
MyTuitionSkeleton
MyTuitionSectionHeader
MyTuitionAvatar
```

Do not duplicate styling across features.

---

# 96. BUTTON VARIANTS

Use only a few:

```text
Primary
Secondary
Text
Destructive
```

Do not invent new button styles per screen.

---

# 97. CARD VARIANTS

Use:

```text
Basic
Interactive
Status
Summary
```

Keep cards visually related.

---

# 98. HOMEWORK STATUS UI

Map status to semantic presentation:

```text
PENDING
SUBMITTED
COMPLETED
OVERDUE
```

Use icon + text as well as color.

Example:

```text
Pending
Overdue
Completed
```

---

# 99. FEES STATUS UI

Use:

```text
Paid
Pending
Overdue
```

Do not rely only on green/yellow/red.

---

# 100. APP STARTUP

Startup sequence:

```text
Application start
 ↓
DI initialization
 ↓
App preferences/session read
 ↓
Splash
 ↓
route
```

Avoid performing expensive network work before the first frame.

---

# 101. ENVIRONMENT CONFIG

Prepare:

```text
BASE_URL
BUILD_VARIANT
ENABLE_LOGGING
DATA_SOURCE
```

Example:

```properties
MYTUITION_BASE_URL=https://api.example.com/
MYTUITION_DATA_SOURCE=MOCK
```

Do not put real secrets here.

---

# 102. README

Create a repository README containing:

```text
Project overview
Features
Architecture
Tech stack
Setup
Build
Mock login
Environment configuration
Testing
Future backend integration
```

---

# 103. GIT STRUCTURE

Use clean commits:

```text
chore: initialize Android app
feat: add design system
feat: add authentication flow
feat: add home shell
feat: add homework
feat: add subjects
feat: add library
feat: add calendar
feat: add fees
feat: add messages
feat: add profile
test: add auth tests
test: add navigation tests
```

---

# 104. DOCUMENTATION

Also create:

```text
docs/
├── architecture.md
├── authentication.md
├── api-integration.md
├── design-system.md
└── roadmap.md
```

---

# 105. DO NOT MAKE UNVERIFIED BACKEND ASSUMPTIONS

There is an existing LMS codebase planned for reuse, but the exact API contract is not specified in this document.

Therefore:

- abstract the API
- use mock DTOs
- keep endpoint definitions easy to change
- do not pretend that an endpoint already exists
- do not hardcode unknown backend behavior

The business plan explicitly says real backend integration should reuse/extend the existing LMS API, and also flags whether a messages API already exists as an open question.  
fileciteturn0file0L129-L130  
fileciteturn0file0L187-L190

---

# 106. WHAT THE AI CODING AGENT MUST DO

When implementing this project, the coding agent must:

1. First create the project skeleton.
2. Create the design system.
3. Create domain models.
4. Create repository interfaces.
5. Create mock repository implementations.
6. Create Hilt modules.
7. Create Splash.
8. Create Login.
9. Create OTP.
10. Create session persistence.
11. Create Main navigation.
12. Create Home.
13. Then implement feature screens one by one.
14. Run/build after each major module.
15. Fix compilation errors before continuing.
16. Keep architecture consistent.
17. Do not bypass repositories.
18. Do not put fake data directly in composables.
19. Keep all visible strings localized.
20. Add tests for each important feature.

---

# 107. IMPLEMENTATION COMMANDMENT

**Build the smallest clean architecture that can scale to the full product.**

Do not build an enterprise architecture for the sake of architecture.

Do not build a throwaway prototype either.

The correct target is:

```text
Simple now
Scalable later
```

---

# 108. MASTER AI CODING PROMPT

Copy everything below into your Kotlin/Android coding agent:

---

## BEGIN MASTER PROMPT

You are a senior Android engineer and product designer.

Build a production-quality native Android application called **MyTuition** using Kotlin and Jetpack Compose.

MyTuition is a student/parent-facing education application for tuition and coaching centers. Its product philosophy is minimal, calm, fast and easy to use—not a traditional heavy ERP.

The app must initially use mock repositories/data but must be architected so it can later connect to an existing LMS backend without rewriting the UI or domain layer.

### Technology

Use:

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX
- Coroutines
- Flow / StateFlow
- ViewModel
- Navigation Compose
- Hilt
- Room
- DataStore
- Retrofit or Ktor
- Kotlin Serialization or Moshi
- Coil
- JUnit
- Compose UI tests

### Architecture

Use:

**Clean Architecture + MVVM + Repository + Unidirectional Data Flow**

Architecture:

```text
Composable UI
 ↓
ViewModel
 ↓
Use Case
 ↓
Repository interface
 ↓
Repository implementation
 ↓
Remote / Local data source
 ↓
API / Room / DataStore
```

Never perform networking or database work directly from composables.

### Package structure

Use a feature-oriented structure:

```text
core/
data/
domain/
feature/
```

with separate feature modules/packages for:

```text
splash
auth
home
homework
subjects
library
calendar
fees
messages
profile
```

### Authentication

Do not implement public student registration.

The institute/tutor is expected to create the student account.

Initial flow:

```text
Splash
 ↓
Session check
 ↓
Login
 ↓
Mobile number
 ↓
OTP
 ↓
Home
```

Use a mock authentication repository.

Development mock:

```text
Mobile: +91 9876543210
OTP: 123456
```

Do not expose the mock OTP in production UI.

The authentication layer must make it easy to replace mock OTP authentication with the eventual real backend authentication mechanism.

### Splash

Create a clean, premium splash screen with the MyTuition logo.

Do not perform slow network calls on the splash screen.

Only determine whether a valid stored session exists.

### Login

Create a polished first-login page.

Visual direction:

- premium
- minimal
- lots of whitespace
- subtle borders
- very limited shadows
- one brand accent
- no giant illustrations
- no clutter
- accessible
- responsive

Content:

```text
MyTuition logo

Welcome back

Stay on top of your classes, homework and tuition updates.

Mobile number field

Continue button

Need help?
```

Validate:

```text
empty
invalid number
loading
success
failure
```

### OTP

Screen:

```text
Verify your number

We sent a 6-digit code to
+91 XXXXX XXXXX

[ 6 digit OTP ]

Resend code in 30s

Verify
```

Support:

- numeric keyboard
- validation
- paste
- loading
- invalid OTP
- resend countdown

### Session

Persist login session through a session repository backed by DataStore.

After successful login:

```text
Login → Home
```

Clear authentication routes.

Logout:

```text
Profile → Logout → Login
```

Back navigation must not reopen protected screens after logout.

### Main app navigation

Use a minimal bottom navigation.

Preferred:

```text
Home
Homework
Calendar
Fees
Profile
```

Access:

```text
Subjects
Library
Messages
```

through Home or a secondary destination rather than overcrowding the bottom navigation.

### Home

Home answers:

> What do I need to know today?

Show:

```text
Greeting
Today's classes
Homework due
Fee status
Recent messages
Quick actions
```

Example:

```text
Good morning, Mohit
Class 10-A

Today's classes
5:00 PM — Mathematics
6:30 PM — Physics

Homework
2 tasks due today

Fees
₹2,500 due on 10 Sep

Recent updates
...
```

Support:

- loading state
- success state
- empty state
- error state
- cached/offline state

### Homework

Create:

```text
Homework list
Homework detail
```

Group list by:

```text
Today
Tomorrow
Upcoming
Completed
```

Show:

```text
Subject
Title
Due date
Status
Teacher
Attachments
```

### Subjects

Create:

```text
Subjects
Subject detail
```

Include:

```text
Teacher
Next class
Homework
Resources
Recent activity
```

Do not build complex analytics.

### Library

Categories:

```text
Notes
PDFs
Videos
Question Papers
Assignments
Other
```

Support:

```text
Open
Download
Share
```

### Calendar

Combine:

```text
Classes
Homework deadlines
Exams
Institute events
```

Default to current week.

### Fees

Show:

```text
Outstanding
Due date
Payment status
Payment history
```

Use INR.

Never use floating point for monetary calculations.

Use integer minor units.

Do not make actual in-app payment integration mandatory for this first build.

### Messages

Create an announcements/messages area.

Do not implement a full real-time chat SDK.

List:

```text
Sender
Title
Date
Preview
```

Detail:

```text
Sender
Date
Body
Attachments
```

### Profile

Show:

```text
Profile photo
Name
Class
Institute
Student ID
Parent contact
```

Actions:

```text
Notifications
Language
Help & Support
Privacy
Logout
```

### Design system

Create a centralized MyTuition design system.

Components:

```text
MyTuitionButton
MyTuitionTextField
MyTuitionCard
MyTuitionTopBar
MyTuitionBottomBar
MyTuitionChip
MyTuitionEmptyState
MyTuitionErrorState
MyTuitionSkeleton
MyTuitionSectionHeader
MyTuitionAvatar
```

Use:

- Material 3
- semantic color tokens
- restrained typography
- consistent spacing
- 16–20dp card corners
- subtle borders
- minimal elevation

The result should feel modern and premium.

### Localization

All visible strings go into `strings.xml`.

Initial language:

```text
English
```

Prepare architecture for:

```text
Hindi
regional Indian languages
```

### Accessibility

Ensure:

- 48dp minimum touch targets
- content descriptions
- screen-reader labels
- semantic hierarchy
- sufficient contrast
- no color-only status indicators

### Mock data

Create a deterministic mock dataset.

Student:

```text
Mohit Raj
Class 10-A
MyTuition Demo Institute
```

Subjects:

```text
Mathematics
Physics
Chemistry
English
Computer Science
```

Classes:

```text
5:00 PM — Mathematics
6:30 PM — Physics
```

Homework:

```text
Mathematics — Quadratic Equations Worksheet
Physics — Current Electricity Numericals
```

Fees:

```text
June — Paid
July — Paid
August — ₹2,500 Pending
```

Messages:

```text
Tomorrow's Mathematics class starts at 5 PM.
New Physics worksheet uploaded.
```

### Repository layer

Create repository interfaces for:

```text
AuthRepository
HomeRepository
HomeworkRepository
SubjectsRepository
LibraryRepository
CalendarRepository
FeesRepository
MessagesRepository
ProfileRepository
```

Create mock implementations now.

Design the system so real LMS implementations can later replace them.

### Backend readiness

Prepare interfaces for API integration.

Do not claim undocumented endpoints already exist.

Use placeholder API definitions only.

Keep DTOs separate from domain models.

Use mappers:

```text
DTO → Domain
Entity → Domain
```

Do not expose DTOs to UI.

### Local cache

Prepare Room entities for:

```text
Student
Homework
Subject
ClassSession
FeeRecord
Message
Resource
```

Use repository-based caching.

### Error handling

Create centralized app errors:

```text
Network
Unauthorized
NotFound
Validation
Server
Unknown
```

Show human-friendly messages.

### Analytics

Create:

```kotlin
AnalyticsTracker
```

with a no-op/debug implementation.

Track:

```text
login_started
login_success
home_viewed
homework_opened
homework_completed
fee_viewed
message_opened
logout
```

Do not directly call analytics from random composables.

### Deep links

Prepare for:

```text
mytuition://homework/{id}
mytuition://fees
mytuition://message/{id}
```

### Future integration

Architect for future connections to:

```text
Existing LMS
Markly
PresenceX
LiveKit
WhatsApp automation backend
```

These must remain behind repositories/API adapters.

Do not put:

- AI grading
- face recognition
- WhatsApp automation
- live video logic

inside the Android UI layer.

### Strict v1 scope guardrails

Do not build:

- tutor marketplace
- public registration
- social network
- built-in chat SDK
- large ERP admin
- multi-branch admin
- complex analytics
- AI chatbot
- full payment gateway
- face recognition
- live video infrastructure
- tutor discovery

### Testing

Add:

- unit tests
- ViewModel tests
- repository tests
- navigation tests
- Compose UI tests

At minimum test:

```text
Login validation
OTP validation
Session persistence
Login → Home
Logout → Login
Homework list
Fee screen
Message detail navigation
```

### Implementation order

Follow exactly:

```text
1. Project foundation
2. Design system
3. Domain models
4. Repository interfaces
5. Mock repositories
6. Hilt
7. Splash
8. Login
9. OTP
10. Session persistence
11. Navigation shell
12. Home
13. Homework
14. Subjects
15. Library
16. Calendar
17. Fees
18. Messages
19. Profile
20. Room
21. Offline caching
22. Tests
23. Accessibility
24. Dark mode
25. Localization foundation
26. Build/release validation
```

### First milestone

Before implementing all features, get this exact flow working:

```text
Splash
 ↓
Login
 ↓
OTP
 ↓
Home
```

It must compile and run before expanding to the rest of the product.

### Coding behavior

While implementing:

- create real files
- write compile-ready Kotlin
- do not leave core logic as TODO
- run/build after major milestones
- fix compilation issues before proceeding
- do not duplicate state
- do not place business logic in composables
- do not hardcode mock data into UI
- use dependency injection
- use immutable UI state
- keep screens and components reasonably small
- use previews
- use string resources
- use meaningful naming

### Final deliverables

At the end of the implementation, the project should contain:

```text
Android project
Working login flow
Working mock OTP
Working session management
Working Home
Working Homework
Working Subjects
Working Library
Working Calendar
Working Fees
Working Messages
Working Profile
Reusable design system
Mock repositories
API-ready architecture
Room-ready architecture
DataStore session
Hilt DI
Tests
README
Architecture documentation
```

Do not sacrifice product polish for architecture complexity.

The end result should feel like a real app that could be shown to a student or parent—not a developer demo.

## END MASTER PROMPT

---

# 109. EXPECTED END RESULT

The first usable prototype should feel like:

```text
Launch app
   ↓
Beautiful splash
   ↓
Simple login
   ↓
OTP
   ↓
Personalized home
   ↓
Homework / Calendar / Fees
   ↓
Simple daily workflow
```

The key product principle is:

> **MyTuition should reduce the mental load of managing tuition, not add another complicated education app.**

---

# 110. IMPORTANT SOURCE NOTES

The provided business plan supports the following product decisions used above:

- MyTuition is a student/parent-facing native Android app paired with an institute/tutor automation backend. fileciteturn0file0L10-L16
- The product differentiation is centered on WhatsApp-native automation, AI and transparent pricing, with a clean minimal UX. fileciteturn0file0L19-L21
- The student/parent app is defined around homework, subjects, library, calendar, fee status and messages. fileciteturn0file0L54-L58
- Tutor-created accounts rather than student self-signup are part of the product model. fileciteturn0file0L61-L64
- The planned build sequence starts with mock data and later connects to the existing LMS API. fileciteturn0file0L125-L130
- Regional-language support is a later roadmap item. fileciteturn0file0L139-L140
- The business plan warns against v1 scope creep such as a tutor marketplace, in-app payments and built-in chat SDK. fileciteturn0file0L177-L181
- OTP vs password login and whether a messages API already exists are explicitly identified as open questions before real backend integration. fileciteturn0file0L187-L190

Anything in this document beyond those source-backed product decisions is an **implementation recommendation** for a scalable Kotlin Android architecture, not a claim about an already-existing MyTuition backend contract.
