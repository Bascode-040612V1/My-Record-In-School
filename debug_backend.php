<?php
// Simple debug script to test backend connection and data

echo "=== Backend Debug Script ===\n\n";

// Test database connections
include_once 'backend/config/database.php';

try {
    $database = new Database();
    
    // Test violations database
    echo "1. Testing violations database connection...\n";
    $violations_conn = $database->getViolationsConnection();
    $student_query = "SELECT student_id, student_name FROM students LIMIT 5";
    $student_stmt = $violations_conn->prepare($student_query);
    $student_stmt->execute();
    
    echo "Students in violations database:\n";
    while ($row = $student_stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "  - {$row['student_id']}: {$row['student_name']}\n";
    }
    
    // Test RFID database
    echo "\n2. Testing RFID database connection...\n";
    $rfid_conn = $database->getRfidConnection();
    $rfid_query = "SELECT student_number, name FROM students LIMIT 5";
    $rfid_stmt = $rfid_conn->prepare($rfid_query);
    $rfid_stmt->execute();
    
    echo "Students in RFID database:\n";
    while ($row = $rfid_stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "  - {$row['student_number']}: {$row['name']}\n";
    }
    
    // Test a specific student's violations
    echo "\n3. Testing violations for student 220342...\n";
    $test_student = "220342";
    
    $violation_query = "SELECT v.id, v.student_id, v.offense_count, v.recorded_at,
                               GROUP_CONCAT(DISTINCT vd.violation_type ORDER BY vd.id SEPARATOR ', ') as violations_list
                        FROM violations v 
                        LEFT JOIN violation_details vd ON v.id = vd.violation_id
                        WHERE v.student_id = :student_id 
                        GROUP BY v.id
                        ORDER BY v.recorded_at DESC LIMIT 3";
    
    $stmt = $violations_conn->prepare($violation_query);
    $stmt->bindParam(':student_id', $test_student);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        echo "Violations found:\n";
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            echo "  - ID: {$row['id']}, Offense: {$row['offense_count']}, Types: {$row['violations_list']}, Date: {$row['recorded_at']}\n";
        }
    } else {
        echo "No violations found for student {$test_student}\n";
    }
    
    // Test attendance
    echo "\n4. Testing attendance for student 220342...\n";
    
    // First get RFID student ID
    $rfid_student_query = "SELECT id, name FROM students WHERE student_number = :student_number LIMIT 1";
    $rfid_student_stmt = $rfid_conn->prepare($rfid_student_query);
    $rfid_student_stmt->bindParam(':student_number', $test_student);
    $rfid_student_stmt->execute();
    
    if ($rfid_student_stmt->rowCount() > 0) {
        $student_data = $rfid_student_stmt->fetch(PDO::FETCH_ASSOC);
        $rfid_student_id = $student_data['id'];
        
        $attendance_query = "SELECT a.id, a.date, a.time_in, a.time_out
                            FROM attendance a 
                            WHERE a.student_id = :student_id 
                            ORDER BY a.date DESC LIMIT 5";
        
        $attendance_stmt = $rfid_conn->prepare($attendance_query);
        $attendance_stmt->bindParam(':student_id', $rfid_student_id);
        $attendance_stmt->execute();
        
        if ($attendance_stmt->rowCount() > 0) {
            echo "Attendance found:\n";
            while ($row = $attendance_stmt->fetch(PDO::FETCH_ASSOC)) {
                echo "  - Date: {$row['date']}, In: {$row['time_in']}, Out: {$row['time_out']}\n";
            }
        } else {
            echo "No attendance found for RFID student ID {$rfid_student_id}\n";
        }
    } else {
        echo "Student {$test_student} not found in RFID database\n";
    }
    
} catch(Exception $e) {
    echo "ERROR: " . $e->getMessage() . "\n";
    echo "Line: " . $e->getLine() . "\n";
    echo "File: " . $e->getFile() . "\n";
}

echo "\n=== Debug Complete ===\n";
?>