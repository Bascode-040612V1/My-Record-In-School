<?php
// Debug script to check database connections and data
ini_set('display_errors', 1);
error_reporting(E_ALL);

include_once 'config/database.php';

echo "<h2>Database Connection Test</h2>";

$database = new Database();

// Test violations database
echo "<h3>Testing Violations Database (student_violation_db)</h3>";
try {
    $violations_conn = $database->getViolationsConnection();
    echo "✅ Violations database connection: SUCCESS<br>";
    
    // Check students table
    $stmt = $violations_conn->query("SELECT student_id, student_name, course, section FROM students LIMIT 5");
    echo "<h4>Students in violations database:</h4>";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "ID: {$row['student_id']}, Name: {$row['student_name']}, Course: {$row['course']}, Section: {$row['section']}<br>";
    }
    
    // Check violations table
    $stmt = $violations_conn->query("SELECT student_id, penalty, recorded_at FROM violations ORDER BY recorded_at DESC LIMIT 5");
    echo "<h4>Recent violations:</h4>";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "Student: {$row['student_id']}, Penalty: {$row['penalty']}, Date: {$row['recorded_at']}<br>";
    }
    
} catch(Exception $e) {
    echo "❌ Violations database error: " . $e->getMessage() . "<br>";
}

// Test RFID database
echo "<h3>Testing RFID Database (rfid_system)</h3>";
try {
    $rfid_conn = $database->getRfidConnection();
    echo "✅ RFID database connection: SUCCESS<br>";
    
    // Check students table
    $stmt = $rfid_conn->query("SELECT id, name, student_number FROM students LIMIT 5");
    echo "<h4>Students in RFID database:</h4>";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "ID: {$row['id']}, Name: {$row['name']}, Student Number: {$row['student_number']}<br>";
    }
    
    // Check attendance table
    $stmt = $rfid_conn->query("SELECT student_id, date, time_in FROM attendance ORDER BY date DESC LIMIT 5");
    echo "<h4>Recent attendance:</h4>";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "Student ID: {$row['student_id']}, Date: {$row['date']}, Time In: {$row['time_in']}<br>";
    }
    
} catch(Exception $e) {
    echo "❌ RFID database error: " . $e->getMessage() . "<br>";
}

// Test specific student lookup
echo "<h3>Testing Student Data Lookup</h3>";
$test_student_id = '220342'; // Your main test student

echo "<h4>Looking for student: {$test_student_id}</h4>";

// Check in violations database
try {
    $stmt = $violations_conn->prepare("SELECT * FROM students WHERE student_id = ?");
    $stmt->execute([$test_student_id]);
    $student = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($student) {
        echo "✅ Student found in violations DB: {$student['student_name']}<br>";
        
        // Check violations for this student
        $stmt = $violations_conn->prepare("SELECT COUNT(*) as count FROM violations WHERE student_id = ?");
        $stmt->execute([$test_student_id]);
        $count = $stmt->fetch(PDO::FETCH_ASSOC);
        echo "Violations count: {$count['count']}<br>";
    } else {
        echo "❌ Student {$test_student_id} NOT found in violations database<br>";
    }
} catch(Exception $e) {
    echo "❌ Error checking violations: " . $e->getMessage() . "<br>";
}

// Check in RFID database
try {
    $stmt = $rfid_conn->prepare("SELECT * FROM students WHERE student_number = ?");
    $stmt->execute([$test_student_id]);
    $student = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($student) {
        echo "✅ Student found in RFID DB: {$student['name']} (ID: {$student['id']})<br>";
        
        // Check attendance for this student
        $stmt = $rfid_conn->prepare("SELECT COUNT(*) as count FROM attendance WHERE student_id = ?");
        $stmt->execute([$student['id']]);
        $count = $stmt->fetch(PDO::FETCH_ASSOC);
        echo "Attendance records count: {$count['count']}<br>";
    } else {
        echo "❌ Student {$test_student_id} NOT found in RFID database<br>";
    }
} catch(Exception $e) {
    echo "❌ Error checking attendance: " . $e->getMessage() . "<br>";
}

?>