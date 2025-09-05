# My Record in School App

A modern Android application built with Jetpack Compose that allows students to track their school violations and attendance records.

## Features

### 🔐 Authentication
- **Login/Register System**: Students can register with their complete name, student number, year, course, and section
- **Secure Login**: Uses student number as both username and password
- **Modern UI**: Beautiful blue-themed interface with smooth animations

### 📱 Main Features

#### 🚨 Violation Tracking
- View violation history with detailed information
- Color-coded offense indicators (Green: 1st offense, Orange: 2nd offense, Red: 3rd+ offense)
- Acknowledge violations by viewing details
- Categorized violations:
  - **Dress Code Violations**: No ID, improper uniform, etc.
  - **Conduct Violations**: Cutting classes, cheating, etc.
  - **Minor Offenses**: Using cellphones, eating in labs, etc.
  - **Major Offenses**: Stealing, vandalism, etc.

#### 📅 Attendance Calendar
- Interactive calendar view showing attendance status
- Color-coded days (Green: Present, Red: Absent, Orange: Late)
- Monthly navigation with attendance statistics
- Visual attendance tracking

#### ⚙️ Settings & Configuration
- **Settings01**: IP and port configuration for XAMPP server
- **Settings02**: Student profile management and information updates
- Database connection testing
- Logout functionality

### 🎨 Design Features
- **Modern Blue Theme**: Beautiful gradient backgrounds and blue accent colors
- **Rounded Corners**: All UI elements have smooth, rounded edges
- **Material Design 3**: Latest Material Design guidelines
- **Responsive UI**: Adapts to different screen sizes
- **Smooth Animations**: Engaging user experience

### 🌐 Backend Integration
- **Retrofit2**: REST API communication
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
- **ViewModel**: MVVM architecture
- **LiveData**: Reactive data observation

### Backend Integration
- **Retrofit2**: HTTP client for API calls
- **Gson**: JSON serialization/deserialization
- **OkHttp**: HTTP logging and interceptors
- **Coroutines**: Asynchronous programming

### Data Management
- **SharedPreferences**: Local data storage
- **Room Database**: (Future enhancement)

## App Structure

```
app/
├── data/
│   ├── api/                 # API interfaces and Retrofit setup
│   ├── model/              # Data models (Student, Violation, Attendance)
│   └── preferences/        # SharedPreferences utilities
├── navigation/             # Navigation setup
├── ui/
│   ├── components/         # Reusable UI components
│   ├── screen/            # App screens
│   └── theme/             # Colors, typography, themes
└── viewmodel/             # Business logic and state management
```

## Screenshots & UI Flow

### Authentication Flow
1. **Login/Register Screen**: Toggle between login and registration
2. **Settings01**: Configure server IP and port before first use

### Main App Flow
1. **Home Screen**: Switch between "My Violations" and "My Attendance" tabs
2. **Violations Tab**: List of violations with acknowledgment feature
3. **Violation Details**: Detailed view with student data and penalty information
4. **Attendance Tab**: Interactive calendar with monthly navigation
5. **Settings02**: Profile management and system settings

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
- **Acknowledgment System**: Students must acknowledge violations
- **Comprehensive Categories**: Four main violation categories with specific penalties

### Attendance System
- **Calendar Interface**: Easy-to-read monthly view
- **Status Tracking**: Present, absent, and late status
- **Statistics**: Monthly attendance summaries
- **Navigation**: Previous/next month browsing

### Settings System
- **Dual Settings Screens**: Separate for system and user settings
- **IP Configuration**: Easy server setup
- **Connection Testing**: Verify database connectivity
- **Profile Updates**: Change academic information

## Future Enhancements

- [ ] Push notifications for new violations
- [ ] Offline data synchronization
- [ ] Export attendance reports
- [ ] Parent/guardian access
- [ ] Biometric login
- [ ] Dark theme support
- [ ] Multi-language support

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

### v1.0.0 (Current)
- Initial release with all core features
- Modern Compose UI
- Complete CRUD operations
- Attendance calendar
- Dual database support

---

**Note**: This app requires backend PHP scripts and database setup which are not included in this repository. The app is designed to work with XAMPP server setup for local development and testing.