# XAMPP Backend Setup Guide

## Step-by-Step Installation Instructions

### 1. Download and Install XAMPP
1. Go to https://www.apachefriends.org/download.html
2. Download XAMPP for your operating system
3. Install XAMPP with default settings
4. Make sure to include Apache, MySQL, and phpMyAdmin

### 2. Start XAMPP Services
1. Open XAMPP Control Panel
2. Start **Apache** service (click Start button)
3. Start **MySQL** service (click Start button)
4. Verify both services are running (green checkmarks)

### 3. Copy Backend Files
1. Navigate to your XAMPP installation directory:
   - **Windows**: `C:\xampp\htdocs\`
   - **Mac**: `/Applications/XAMPP/htdocs/`
   - **Linux**: `/opt/lampp/htdocs/`

2. Copy the entire `backend` folder from your project to the `htdocs` directory
   - Final path should be: `C:\xampp\htdocs\backend\` (Windows)

### 4. Setup Databases
1. Open your web browser
2. Go to: http://localhost/phpmyadmin
3. **Import the provided database schemas:**
   
   **For student_violation_db:**
   - Click "New" to create database
   - Name: `student_violation_db`
   - Click "Create"
   - Select the database
   - Click "Import" tab
   - Choose file: `backend/my Actual Database SQL/student_violation_db (1).sql`
   - Click "Go"
   
   **For rfid_system:**
   - Click "New" to create database
   - Name: `rfid_system`
   - Click "Create"
   - Select the database
   - Click "Import" tab
   - Choose file: `backend/my Actual Database SQL/rfid_system.sql`
   - Click "Go"

4. Verify both databases are created with all tables and sample data

### 5. Test the Backend
1. Open your web browser
2. Go to: http://localhost/backend/test_connection.php
3. You should see:
   ```json
   {
     "success": true,
     "message": "Both databases connected successfully"
   }
   ```

### 6. Configure Android App
1. Find your computer's IP address:
   - **Windows**: Open Command Prompt, type `ipconfig`, look for "IPv4 Address"
   - **Mac/Linux**: Open Terminal, type `ifconfig`, look for "inet" address
   - Example: `192.168.1.100`

2. In your Android app:
   - Open Settings01 from the login screen
   - Set IP Address: `192.168.1.100` (your actual IP)
   - Set Port: `80` (or `8080` if using port 8080)
   - Click "Test Connection" to verify

## Default Configuration

### Database Settings
- **Host**: localhost
- **Username**: root
- **Password**: (empty)
- **Databases**: 
  - student_violation_db
  - rfid_system

### Sample Login Credentials
After importing your database files, you can test with existing data:
- **From student_violation_db**: Use any existing student_id with the same value as password
- **Example**: If student_id `220342` exists, use `220342` as both username and password
- **Check existing students**: Look in phpMyAdmin under `student_violation_db` > `students` table

Or create new accounts through the registration screen in the Android app.

## API Endpoints Reference

### Base URLs
- **Local**: `http://localhost/backend/`
- **Network**: `http://YOUR_IP_ADDRESS/backend/`

### Available Endpoints
- `GET /test_connection.php` - Test database connections
- `POST /auth/login.php` - Student login
- `POST /auth/register.php` - Student registration
- `PUT /student/update.php` - Update student info
- `GET /violations/{student_id}` - Get violations
- `PUT /violations/acknowledge/{id}` - Acknowledge violation
- `GET /attendance/{student_id}` - Get attendance

## Troubleshooting

### XAMPP Won't Start
- **Port conflicts**: Change Apache port to 8080 in XAMPP config
- **Permission issues**: Run XAMPP as administrator
- **Firewall**: Allow XAMPP through Windows firewall

### Database Connection Issues
- Verify MySQL is running in XAMPP
- Check if databases exist in phpMyAdmin
- Ensure schema.sql was executed completely

### App Can't Connect
- Check IP address is correct
- Try using `localhost` if testing on same computer
- Verify firewall isn't blocking connections
- Test endpoints in browser first

### Common Error Messages

1. **"Connection failed"**
   - MySQL service not running
   - Wrong database credentials

2. **"Endpoint not found"**
   - Backend files not in correct directory
   - Wrong URL in app settings

3. **"Access denied"**
   - Database user permissions issue
   - Try using root with no password

4. **"Use JsonReader.setLenient(true) to accept malformed JSON"**
   - **MOST COMMON ISSUE**: PHP errors are being output as HTML instead of JSON
   - **Solution**: Ensure error_reporting is disabled in PHP files
   - **Quick Fix**: Add these lines to the top of all PHP files:
     ```php
     ini_set('display_errors', 0);
     error_reporting(0);
     ob_clean(); // Clean any previous output
     ```
   - **Check**: Visit the endpoint in browser to see actual response
   - **Test URL**: `http://localhost/backend/test_connection.php`
   - **Expected**: Pure JSON response, no HTML tags
   - **Common Causes**:
     - PHP syntax errors
     - Missing database connections
     - Wrong file paths in include statements
     - XAMPP not fully started

5. **"Network timeout" or "Unable to resolve host"**
   - Check IP address configuration
   - Ensure both devices are on same network
   - Try using `localhost` instead of IP for local testing

## File Permissions (Linux/Mac)
If you're on Linux or Mac, set proper permissions:
```bash
chmod -R 755 /path/to/htdocs/backend/
chmod -R 644 /path/to/htdocs/backend/*.php
```

## Security Notes for Production

⚠️ **This setup is for development only!**

For production deployment:
1. Change default MySQL root password
2. Create dedicated database user
3. Enable password hashing
4. Use HTTPS instead of HTTP
5. Implement proper input validation
6. Add rate limiting
7. Use environment variables for sensitive data

## Getting Help

If you encounter issues:
1. Check XAMPP error logs (`xampp/apache/logs/error.log`)
2. Test each endpoint individually in browser
3. Verify database structure in phpMyAdmin
4. Ensure Android app and server are on same network

## Quick Test Commands

Test your setup with these browser URLs:
```
http://localhost/backend/test_connection.php
http://localhost/phpmyadmin
http://YOUR_IP:80/backend/test_connection.php
```

## Network Access Setup

To allow other devices (like your Android phone) to access the server:

1. **Find your IP address**
2. **Configure Windows Firewall** (if on Windows):
   - Allow "Apache HTTP Server" through firewall
   - Or temporarily disable firewall for testing
3. **Test from another device**:
   - Use `http://YOUR_IP_ADDRESS/backend/test_connection.php`
4. **Configure router** (if needed):
   - Port forward port 80 to your computer's IP