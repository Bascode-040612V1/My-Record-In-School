# My Record in School - Backend API

This is the backend API for the My Record in School Android application. It provides REST endpoints for student authentication, violation tracking, and attendance management.

## Setup Instructions

### 1. XAMPP Installation
1. Download and install XAMPP from https://www.apachefriends.org/
2. Start Apache and MySQL services from XAMPP Control Panel

### 2. Database Setup
1. Open phpMyAdmin (http://localhost/phpmyadmin)
2. **Import the provided database schemas:**
   
   **For student_violation_db:**
   - Create new database: `student_violation_db`
   - Import: `my Actual Database SQL/student_violation_db (1).sql`
   
   **For rfid_system:**
   - Create new database: `rfid_system`
   - Import: `my Actual Database SQL/rfid_system.sql`

3. This will create both databases with all tables, relationships, and sample data

### 3. Backend Deployment
1. Copy the entire `backend` folder to your XAMPP `htdocs` directory
   - Path: `C:\xampp\htdocs\backend` (Windows)
   - Path: `/Applications/XAMPP/htdocs/backend` (Mac)
2. Ensure proper file permissions (755 for directories, 644 for files)

### 4. Configuration
1. Edit `config/database.php` if needed to match your database credentials:
   ```php
   private $host = "localhost";
   private $username = "root";
   private $password = "";
   ```

### 5. Testing the API
Visit `http://localhost:8080/backend/test_connection.php` to test database connections.

## API Endpoints

### Base URL
```
http://localhost:8080/backend/
```

### Authentication

#### Login
- **POST** `/auth/login.php`
- **Body:**
  ```json
  {
    "student_id": "2023001",
    "password": "2023001"
  }
  ```

#### Register
- **POST** `/auth/register.php`
- **Body:**
  ```json
  {
    "student_id": "2023004",
    "name": "New Student",
    "password": "2023004",
    "year": "Grade 11",
    "course": "ICT",
    "section": "IC1MA"
  }
  ```

### Student Management

#### Update Student Info
- **PUT** `/student/update.php`
- **Body:**
  ```json
  {
    "student_id": "2023001",
    "year": "Grade 12",
    "course": "BSCS",
    "section": "BS1MA"
  }
  ```

### Violations

#### Get Student Violations
- **GET** `/violations/{student_id}`
- **Example:** `/violations/2023001`

#### Acknowledge Violation
- **PUT** `/violations/acknowledge/{violation_id}`
- **Example:** `/violations/acknowledge/1`

### Attendance

#### Get Student Attendance
- **GET** `/attendance/{student_id}?month={month}&year={year}`
- **Example:** `/attendance/2023001?month=12&year=2024`
- **Parameters:**
  - `month` (optional): Month number (1-12)
  - `year` (optional): Year (e.g., 2024)

### System

#### Test Connection
- **GET** `/test_connection.php`
- Tests both database connections

## File Structure

```
backend/
├── config/
│   └── database.php          # Database configuration and connection
├── database/
│   └── schema.sql           # Database schema and sample data
├── auth/
│   ├── login.php            # Student login endpoint
│   └── register.php         # Student registration endpoint
├── student/
│   └── update.php           # Update student information
├── violations/
│   ├── index.php            # Get student violations
│   └── acknowledge.php      # Acknowledge violation
├── attendance/
│   └── index.php            # Get student attendance
├── test_connection.php      # Database connection test
├── index.php               # Main router (optional)
└── README.md               # This file
```

## Database Schema

### student_violation_db.students
- `id` - Primary key
- `student_id` - Unique student identifier
- `name` - Full name
- `password` - Password (same as student_id)
- `year` - Academic year
- `course` - Course/Strand
- `section` - Class section
- `created_at` - Created timestamp
- `updated_at` - Updated timestamp

### student_violation_db.violations
- `id` - Primary key
- `student_id` - Foreign key to students
- `violation_type` - Type of violation
- `violation_description` - Description
- `offense_count` - Number of offense (1st, 2nd, 3rd+)
- `penalty` - Applied penalty
- `recorded_by` - Staff member name
- `date_recorded` - When recorded
- `acknowledged` - Whether student acknowledged (0/1)
- `category` - Violation category

### rfid_system.students
- Same structure as violation database for consistency

### rfid_system.attendance
- `id` - Primary key
- `student_id` - Foreign key to students
- `date` - Attendance date
- `time_in` - Entry time
- `time_out` - Exit time
- `status` - PRESENT/ABSENT/LATE
- `created_at` - Record timestamp

## Sample Data

The schema includes sample data for testing:
- 3 sample students (2023001, 2023002, 2023003)
- Sample violations for testing
- Sample attendance records for current month

## Error Handling

All endpoints return JSON responses with:
```json
{
  "success": true/false,
  "message": "Description",
  "data": {...}  // When applicable
}
```

## CORS Support

All endpoints include CORS headers to allow cross-origin requests from the Android application.

## Security Notes

- This is a development setup with basic security
- For production, implement:
  - Password hashing
  - JWT tokens
  - Input validation
  - SQL injection protection (using prepared statements)
  - Rate limiting

## Troubleshooting

### Common Issues

1. **Database connection failed**
   - Check XAMPP MySQL is running
   - Verify database credentials in `config/database.php`
   - Ensure databases are created via `schema.sql`

2. **CORS errors**
   - Ensure CORS headers are present in responses
   - Check if Apache is allowing .htaccess files

3. **404 errors**
   - Verify file paths are correct
   - Check XAMPP document root configuration
   - Ensure files have proper permissions

### Testing Endpoints

Use tools like Postman, curl, or the Android app to test endpoints:

```bash
# Test connection
curl http://localhost:8080/backend/test_connection.php

# Test login
curl -X POST http://localhost:8080/backend/auth/login.php \
  -H "Content-Type: application/json" \
  -d '{"student_id":"2023001","password":"2023001"}'
```

## Support

For issues with the backend setup:
1. Check XAMPP error logs
2. Verify database schema is imported correctly
3. Ensure all files are in the correct directory structure
4. Test endpoints individually before using with the Android app