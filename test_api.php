<?php
// Test API endpoints directly
echo "=== API Endpoint Testing ===\n\n";

// Test violations endpoint
echo "1. Testing violations endpoint for student 220342...\n";
$_SERVER['REQUEST_METHOD'] = 'GET';
$_SERVER['PATH_INFO'] = '/220342';

ob_start();
include 'backend/violations/index.php';
$violations_response = ob_get_clean();

echo "Violations Response:\n";
echo $violations_response . "\n\n";

// Test attendance endpoint for student 220062 (exists in RFID)
echo "2. Testing attendance endpoint for student 220062...\n";
$_SERVER['PATH_INFO'] = '/220062';

ob_start();
include 'backend/attendance/index.php';
$attendance_response = ob_get_clean();

echo "Attendance Response:\n";
echo $attendance_response . "\n\n";

// Test with a student that exists in violations but not RFID
echo "3. Testing attendance endpoint for student 220342 (exists in violations, not in RFID)...\n";
$_SERVER['PATH_INFO'] = '/220342';

ob_start();
include 'backend/attendance/index.php';
$attendance_response2 = ob_get_clean();

echo "Attendance Response for 220342:\n";
echo $attendance_response2 . "\n\n";

echo "=== API Testing Complete ===\n";
?>