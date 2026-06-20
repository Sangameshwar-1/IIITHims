# IMS - Institute Management System

An Android application built with **Jetpack Compose** for managing educational institute operations including student admissions, examination management, and a comprehensive dashboard.

## Team
- **2024102017** - Sangameshwar
- **2024102054**

## Modules Implemented

### 1. Dashboard (Mandatory)
- Overview stats: total students, active courses, upcoming exams, pending admissions
- Global search bar for instant navigation across all entities
- Quick action buttons for common operations
- Latest news feed and upcoming exam preview
- Recent students list with status indicators

### 2. Examinations (Category A - Core Process Module)
- Create, edit, and delete exams with course/batch/subject association
- Support for multiple exam types: Marks, Grade, GPA, CCE, CWA
- Student result recording with marks, grades, and pass/fail status
- Statistical analytics: average score, pass percentage, highest/lowest
- Visual reports with bar charts and exam-wise breakdown
- Status tracking: Scheduled → Ongoing → Completed/Cancelled

### 3. Student Admission (Category B - Data Management Module)
- Multi-step admission form: Personal Info → Guardian Info → Education History
- Unique student ID auto-generation (STU-YEAR-XXXX format)
- Multiple guardian support with emergency contact designation
- Advanced search with filters: status, course, batch, category, gender
- Student detail view with organized info sections
- Edit and delete with confirmation dialogs

## Tech Stack
| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose (Material 3) |
| Architecture | MVVM (Model-View-ViewModel) |
| Database | Room Persistence Library |
| Navigation | Jetpack Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| Network | OkHttp (for app identifier) |
| DI | Manual (Application-level singletons) |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |

## Architecture

```
com.ims.app/
├── data/
│   ├── model/          # Room entities (Student, Exam, Course, etc.)
│   ├── dao/            # Data Access Objects (StudentDao, ExamDao, etc.)
│   ├── database/       # Room database singleton
│   └── repository/     # Repository layer (StudentRepo, ExamRepo, DashboardRepo)
├── ui/
│   ├── theme/          # Color palette, typography, Material 3 theme
│   ├── navigation/     # Navigation routes (Screen) and NavGraph
│   ├── components/     # Shared UI components (StatCard, SearchBar, etc.)
│   └── screens/
│       ├── dashboard/  # Dashboard screen + ViewModel
│       ├── admission/  # Admission list, form, detail screens + ViewModel
│       ├── examination/ # Exam list, create, results, reports screens + ViewModel
│       └── settings/   # App settings screen
├── util/               # SeedData utility
├── ImsApplication.kt   # Application class with DI setup
└── MainActivity.kt     # Single activity with bottom navigation
```

### Design Patterns Used
1. **MVVM**: Clear separation between UI (Composables), business logic (ViewModels), and data (Repositories/DAOs)
2. **Repository Pattern**: Abstracts data sources behind a clean API, decoupling ViewModels from Room DAOs
3. **Singleton Pattern**: Database instance via `ImsDatabase.getDatabase()` ensures single DB connection
4. **Factory Pattern**: `ViewModelProvider.Factory` for creating ViewModels with dependencies
5. **Observer Pattern**: Kotlin `StateFlow` for reactive UI updates from database changes

### Design Anti-Patterns Avoided
1. **God Object**: Each module has its own ViewModel instead of a single monolithic one
2. **Tight Coupling**: Repository layer prevents direct DAO access from UI layer

## Building & Running

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Steps
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device (min SDK 24)

### Build Configuration
The `APP_IDENTIFIER` is set in `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "APP_IDENTIFIER", "\"2024102017.2024102054\"")
```

## Database
The app uses Room with a local SQLite database (`ims_database`). On first launch, the database is seeded with sample data including:
- 4 courses (CSE, ECE, ME, MBA)
- 5 batches across courses
- 8 subjects with credit hours
- 10 students with personal details
- 5 guardians with emergency contacts
- 5 exams (mix of completed and scheduled)
- 6 exam results with grades
- 5 news articles

## UI Design
- **Dark theme** with premium indigo-purple primary color palette
- Material 3 components throughout
- Gradient stat cards and action buttons
- Animated transitions between screens
- Consistent 20dp horizontal padding and 12dp spacing
- Status chips with semantic colors (green=active, blue=scheduled, red=error)

## Usability Principles
1. **Visibility of System Status**: Status chips and progress indicators keep users informed
2. **Recognition over Recall**: Filter chips and search bars reduce memory load
3. **Consistency**: Uniform card designs, color coding, and navigation patterns throughout

## License
This project is for academic purposes only.
