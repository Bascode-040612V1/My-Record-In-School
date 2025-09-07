# My Record in School App

A modern Android application built with Jetpack Compose that allows students to track their school violations and attendance records with full offline functionality.

## Features

### 🔐 Authentication
- **Login/Register System**: Students can register with their complete name, student number, year, course, and section
- **Secure Login**: Uses student number as both username and password
- **Modern UI**: Beautiful blue-themed interface with smooth animations
- **Offline Support**: Login data cached locally for offline access

### 📱 Main Features

#### 🚨 Violation Tracking
- View violation history with detailed information
- Color-coded offense indicators (Green: 1st offense, Orange: 2nd offense, Red: 3rd+ offense)
- **Hidden Acknowledge System**: Violations are automatically acknowledged when viewing details (transparent to students)
- **Offline-First**: View violations even without internet connection
- Real-time sync status and background synchronization
- Categorized violations:
  - **Dress Code Violations**: No ID, improper uniform, etc.
  - **Conduct Violations**: Cutting classes, cheating, etc.
  - **Minor Offenses**: Using cellphones, eating in labs, etc.
  - **Major Offenses**: Stealing, vandalism, etc.

#### 📅 Attendance Calendar
- Interactive calendar view showing attendance status
- Color-coded days (Green: Present, Red: Absent, Orange: Late)
- Monthly navigation with attendance statistics
- **Offline Calendar**: Works without internet with cached data
- Visual attendance tracking with real-time updates

#### ⚙️ Settings & Configuration
- **Settings01**: IP and port configuration for XAMPP server
- **Settings02**: Student profile management and information updates
- Database connection testing
- Network status monitoring
- Logout functionality

### 🎨 Design Features
- **Modern Blue Theme**: Beautiful gradient backgrounds and blue accent colors
- **Rounded Corners**: All UI elements have smooth, rounded edges
- **Material Design 3**: Latest Material Design guidelines
- **Responsive UI**: Adapts to different screen sizes
- **Smooth Animations**: Engaging user experience
- **Loading States**: Professional loading indicators and error handling
- **Sync Status Indicators**: Real-time sync and network status display

### 🌐 Backend Integration & Offline Support
- **Offline-First Architecture**: App works completely without internet
- **Room Database**: Complete local data storage with SQLite
- **Retrofit2**: REST API communication with automatic retry
- **Background Sync**: Automatic synchronization every 5 minutes
- **Conflict Resolution**: Smart data merging and error recovery
- **Network State Detection**: Automatic online/offline mode switching
- **Coroutines**: Asynchronous data operations
- **XAMPP Integration**: Connects to local XAMPP server
- **Dual Database Support**: 
  - `student_violation_db`: For violations data
  - `rfid_system`: For attendance data

## Technical Stack

### Frontend
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Material Design 3**: UI components and theming
- **Navigation Compose**: Screen navigation
- **ViewModel**: MVVM architecture with StateFlow
- **LiveData & StateFlow**: Reactive data observation

### Data Layer
- **Room Database**: Complete offline data persistence
- **Repository Pattern**: Clean data access abstraction
- **DataState Management**: Comprehensive loading state handling
- **SyncManager**: Centralized synchronization logic

### Backend Integration
- **Retrofit2**: HTTP client for API calls
- **Gson**: JSON serialization/deserialization
- **OkHttp**: HTTP logging and interceptors
- **Coroutines**: Asynchronous programming

### Data Management
- **SharedPreferences**: App settings and user preferences
- **Room Database**: Complete offline data storage
- **Automatic Sync**: Background data synchronization

## App Structure

```
app/
├── data/
│   ├── api/                 # API interfaces and Retrofit setup
│   ├── model/              # Data models (Student, Violation, Attendance, DataState)
│   ├── preferences/        # SharedPreferences utilities
│   └── sync/               # SyncManager for offline-online synchronization
├── navigation/             # Navigation setup
├── roomdb/
│   ├── dao/                # Data Access Objects with student filtering
│   ├── entity/             # Room database entities
│   ├── repository/         # Repository pattern implementation
│   ├── AppDatabase.kt      # Room database configuration
│   └── DatabaseProvider.kt # Database instance provider
├── ui/
│   ├── components/         # Reusable UI components (LoadingComponents, etc.)
│   ├── screen/            # App screens with enhanced loading states
│   └── theme/             # Colors, typography, themes
└── viewmodel/             # Business logic with DataState management
```

## Screenshots & UI Flow

### Authentication Flow
1. **Login/Register Screen**: Toggle between login and registration with enhanced loading states
2. **Settings01**: Configure server IP and port before first use

### Main App Flow
1. **Home Screen**: Switch between "My Violations" and "My Attendance" tabs with sync status
2. **Violations Tab**: List of violations with "View Details" buttons (acknowledgment is hidden)
3. **Violation Details**: Detailed view with student data and penalty information
4. **Attendance Tab**: Interactive calendar with monthly navigation and offline support
5. **Settings02**: Profile management and system settings with network status

### Offline Features
- **Offline Mode**: App displays "Offline Mode" when no internet connection
- **Cached Data**: All violations and attendance are cached locally
- **Background Sync**: Data syncs automatically when connection is restored
- **Loading States**: Professional loading indicators throughout the app

## Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 24 (Android 7.0) or higher
- XAMPP server with PHP and MySQL

### Database Setup
1. Install and start XAMPP
2. Create two databases:
   - `student_violation_db`
   - `rfid_system`
3. Set up your PHP API endpoints (not included in this repository)

### App Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Configure your server IP in the app settings
5. Build and run on device/emulator

## Configuration

### Server Setup
1. Start XAMPP with Apache and MySQL
2. Note your local IP address
3. In the app, go to Settings01 to configure:
   - IP Address (e.g., 192.168.1.4)
   - Port (default: 8080)

### Database Schema

#### Students Table (Both Databases)
```sql
- id: Primary key
- student_id: Unique student identifier
- name: Full name
- password: Authentication (uses student_id)
- year: Academic year
- course: Course/Strand
- section: Class section
- created_at: Registration timestamp
```

#### Violations Table (student_violation_db)
```sql
- id: Primary key
- student_id: Foreign key
- violation_type: Type of violation
- violation_description: Details
- offense_count: Number of offense
- penalty: Applied penalty
- recorded_by: Staff member name
- date_recorded: Timestamp
- acknowledged: Student acknowledgment flag
- category: Violation category
```

#### Attendance Table (rfid_system)
```sql
- id: Primary key
- student_id: Foreign key
- date: Attendance date
- time_in: Entry time
- time_out: Exit time
- status: PRESENT/ABSENT/LATE
- created_at: Record timestamp
```

## API Endpoints

The app expects these PHP endpoints:

### Authentication
- `POST /auth/login.php`
- `POST /auth/register.php`
- `PUT /student/update.php`

### Violations
- `GET /violations/{student_id}`
- `PUT /violations/acknowledge/{id}`

### Attendance
- `GET /attendance/{student_id}?month={month}&year={year}`

### System
- `GET /test_connection.php`

## Features in Detail

### Violation System
- **Automatic Penalty Calculation**: Based on offense count and violation type
- **Color Coding**: Visual indicators for severity levels
- **Hidden Acknowledgment System**: Violations are automatically acknowledged when students view details (completely transparent to students)
- **Offline Functionality**: View and acknowledge violations without internet connection
- **Real-time Sync**: Background synchronization with conflict resolution
- **Comprehensive Categories**: Four main violation categories with specific penalties

### Attendance System
- **Calendar Interface**: Easy-to-read monthly view with offline support
- **Status Tracking**: Present, absent, and late status
- **Statistics**: Monthly attendance summaries with local calculation
- **Navigation**: Previous/next month browsing with cached data
- **Offline Mode**: Full calendar functionality without internet

### Settings System
- **Dual Settings Screens**: Separate for system and user settings
- **IP Configuration**: Easy server setup with connection testing
- **Network Monitoring**: Real-time network status display
- **Profile Updates**: Change academic information with sync
- **Offline Indicator**: Clear offline/online mode display

### Sync & Offline Features
- **Offline-First Design**: Local data is always prioritized
- **Background Sync**: Automatic synchronization every 5 minutes
- **Conflict Resolution**: Smart merging of local and remote changes
- **Network Detection**: Automatic online/offline mode switching
- **Retry Logic**: Automatic retry on failed operations
- **Loading States**: Professional loading indicators and error handling

## New Features (Latest Version)

### 🔄 Complete Offline Support
- **Works Without Internet**: Full app functionality available offline
- **Local Database**: Room database for complete data persistence
- **Smart Sync**: Automatic background synchronization
- **Conflict Resolution**: Handles data conflicts intelligently

### 📊 Enhanced Loading States
- **Professional UI**: Loading indicators throughout the app
- **Error Handling**: User-friendly error messages with retry options
- **Sync Status**: Real-time sync and network connectivity indicators
- **Empty States**: Proper empty state handling

### 🔍 Hidden Acknowledge Feature
- **Transparent to Students**: Acknowledgment happens automatically when viewing details
- **Administrative Tracking**: Full acknowledgment tracking maintained
- **Seamless UX**: Students see only "View Details" buttons

### ⚡ Performance Improvements
- **Instant Loading**: Local data displayed immediately
- **Efficient Sync**: Only changed data is synchronized
- **Memory Optimization**: Proper resource management

## Future Enhancements

- [ ] Push notifications for new violations
- [x] ~~Offline data synchronization~~ ✅ **Implemented**
- [ ] Export attendance reports
- [ ] Parent/guardian access
- [ ] Biometric login
- [ ] Dark theme support
- [ ] Multi-language support
- [x] ~~Enhanced loading states~~ ✅ **Implemented**
- [x] ~~Background sync~~ ✅ **Implemented**
- [ ] Data export functionality
- [ ] Advanced analytics dashboard

## Architecture Highlights

### MVVM with Jetpack Compose
- **Clean Architecture**: Separation of concerns with Repository pattern
- **DataState Management**: Comprehensive state handling (Loading, Success, Error, Cached)
- **Reactive UI**: StateFlow and LiveData for reactive programming
- **Offline-First**: Local data prioritized with network sync

### Room Database Integration
- **Complete Offline Storage**: All violations and attendance cached locally
- **Student-Specific Queries**: Secure data filtering by student ID
- **Automatic Sync**: Background synchronization with conflict resolution
- **Performance Optimized**: Efficient queries and data structures

### Sync Management
- **SyncManager**: Centralized synchronization logic
- **Network Awareness**: Automatic online/offline detection
- **Retry Logic**: Robust error handling and recovery
- **Conflict Resolution**: Smart data merging strategies

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
1. Check the documentation
2. Search existing issues
3. Create a new issue with detailed information

## Version History

### v2.0.0 (Current - Enhanced)
- ✅ **Complete Offline Functionality**: Full app works without internet
- ✅ **Room Database Integration**: Complete local data persistence
- ✅ **Enhanced Loading States**: Professional loading indicators and error handling
- ✅ **Background Sync**: Automatic synchronization every 5 minutes
- ✅ **Hidden Acknowledge System**: Transparent violation acknowledgment
- ✅ **Sync Status Indicators**: Real-time network and sync status
- ✅ **DataState Management**: Comprehensive state handling throughout app
- ✅ **Conflict Resolution**: Smart data merging and error recovery
- ✅ **Performance Optimization**: Instant local data display
- ✅ **Enhanced UX**: Modern loading components and error handling

### v1.0.0 (Previous)
- Initial release with all core features
- Modern Compose UI
- Complete CRUD operations
- Attendance calendar
- Dual database support

## Technical Improvements

### Code Quality
- **Clean Architecture**: Repository pattern with proper separation of concerns
- **MVVM Implementation**: Enhanced ViewModels with StateFlow and DataState
- **Error Handling**: Comprehensive error management throughout the app
- **Memory Management**: Proper coroutine lifecycle management

### Performance
- **Offline-First**: Instant data display from local cache
- **Efficient Sync**: Background synchronization with minimal battery usage
- **Memory Optimization**: Proper resource cleanup and management
- **Network Optimization**: Smart data fetching and caching strategies

### User Experience
- **Professional Loading States**: Consistent loading indicators across all screens
- **Error Recovery**: User-friendly error messages with retry mechanisms
- **Network Awareness**: Clear offline/online status indication
- **Seamless Sync**: Transparent background synchronization

---

**Note**: This app requires backend PHP scripts and database setup which are not included in this repository. The app is designed to work with XAMPP server setup for local development and testing.