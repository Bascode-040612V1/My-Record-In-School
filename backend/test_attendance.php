<?php
// Test attendance API specifically
ini_set('display_errors', 1);
error_reporting(E_ALL);

include_once 'config/database.php';

echo "<h2>Attendance API Debug Test</h2>";

$database = new Database();
$conn = $database->getRfidConnection();

$test_student_id = '220342'; // Test with your main student

echo "<h3>Testing attendance lookup for student: {$test_student_id}</h3>";

try {
    // Step 1: Find student in RFID database
    echo "<h4>Step 1: Looking up student in RFID database</h4>";
    $student_query = "SELECT id, name FROM students WHERE student_number = :student_number LIMIT 1";
    $student_stmt = $conn->prepare($student_query);
    $student_stmt->bindParam(':student_number', $test_student_id);
    $student_stmt->execute();
    
    if ($student_stmt->rowCount() == 0) {
        echo "❌ Student {$test_student_id} not found in RFID database<br>";
        
        // Let's see what students ARE in the database
        echo "<h4>Students available in RFID database:</h4>";
        $all_students = $conn->query("SELECT id, name, student_number FROM students ORDER BY id");
        while ($row = $all_students->fetch(PDO::FETCH_ASSOC)) {
            echo "ID: {$row['id']}, Name: {$row['name']}, Student Number: {$row['student_number']}<br>";
        }
        exit();
    }
    
    $student_data = $student_stmt->fetch(PDO::FETCH_ASSOC);
    $rfid_student_id = $student_data['id'];
    
    echo "✅ Student found: {$student_data['name']} (RFID ID: {$rfid_student_id})<br>";
    
    // Step 2: Look for attendance records
    echo "<h4>Step 2: Looking for attendance records</h4>";
    $query = "SELECT a.*, s.name as student_name 
              FROM attendance a 
              JOIN students s ON a.student_id = s.id 
              WHERE a.student_id = :student_id
              ORDER BY a.date DESC";
    
    $stmt = $conn->prepare($query);
    $stmt->bindParam(':student_id', $rfid_student_id);
    $stmt->execute();
    
    echo "Query executed. Row count: " . $stmt->rowCount() . "<br>";
    
    if ($stmt->rowCount() == 0) {
        echo "❌ No attendance records found for student ID {$rfid_student_id}<br>";
        
        // Let's see what attendance records ARE in the database
        echo "<h4>Available attendance records:</h4>";
        $all_attendance = $conn->query("SELECT a.student_id, s.name, a.date, a.time_in FROM attendance a JOIN students s ON a.student_id = s.id ORDER BY a.date DESC LIMIT 10");
        while ($row = $all_attendance->fetch(PDO::FETCH_ASSOC)) {
            echo "Student ID: {$row['student_id']}, Name: {$row['name']}, Date: {$row['date']}, Time In: {$row['time_in']}<br>";
        }
    } else {
        echo "✅ Found {$stmt->rowCount()} attendance records:<br>";
        
        $attendance = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            // Determine status based on time_in/time_out
            $status = "ABSENT";
            if ($row['time_in']) {
                $time_in = new DateTime($row['time_in']);
                $cutoff_time = new DateTime('08:00:00'); // 8 AM cutoff for late
                
                if ($time_in <= $cutoff_time) {
                    $status = "PRESENT";
                } else {
                    $status = "LATE";
                }
            }
            
            $attendance[] = array(
                "id" => intval($row['id']),
                "student_id" => $test_student_id,
                "date" => $row['date'],
                "time_in" => $row['time_in'] ? date('H:i:s', strtotime($row['time_in'])) : null,
                "time_out" => $row['time_out'] ? date('H:i:s', strtotime($row['time_out'])) : null,
                "status" => $status,
                "created_at" => $row['date']
            );
            
            echo "Date: {$row['date']}, Time In: {$row['time_in']}, Status: {$status}<br>";
        }
        
        echo "<h4>Final JSON Response:</h4>";
        echo "<pre>" . json_encode(array(
            "success" => true,
            "message" => "Attendance retrieved successfully",
            "attendance" => $attendance
        ), JSON_PRETTY_PRINT) . "</pre>";
    }
    
} catch(Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "<br>";
    echo "Line: " . $e->getLine() . "<br>";
    echo "File: " . $e->getFile() . "<br>";
}

?>