<?php
// Fix database data issues for violations and attendance
ini_set('display_errors', 1);
error_reporting(E_ALL);

echo "<h1>Fixing Database Data Issues</h1>";

try {
    include_once 'config/database.php';
    $database = new Database();
    
    $violations_conn = $database->getViolationsConnection();
    $rfid_conn = $database->getRfidConnection();
    
    echo "<h2>Step 1: Checking Student 220342</h2>";
    
    // Check if student exists in violations database
    $viol_check = $violations_conn->prepare("SELECT * FROM students WHERE student_id = ?");
    $viol_check->execute(['220342']);
    
    if ($viol_check->rowCount() > 0) {
        $viol_student = $viol_check->fetch(PDO::FETCH_ASSOC);
        echo "✅ Student found in violations DB: {$viol_student['student_name']}<br>";
        
        // Check if student exists in RFID database
        $rfid_check = $rfid_conn->prepare("SELECT * FROM students WHERE student_number = ?");
        $rfid_check->execute(['220342']);
        
        if ($rfid_check->rowCount() == 0) {
            echo "❌ Student NOT found in RFID database. Adding now...<br>";
            
            // Add student to RFID database
            $rfid_insert = $rfid_conn->prepare("
                INSERT INTO students (name, student_number, rfid, image) 
                VALUES (?, ?, ?, ?)
            ");
            
            $success = $rfid_insert->execute([
                $viol_student['student_name'],
                $viol_student['student_id'],
                '3870' . rand(100000, 999999), // Generate random RFID
                'assets/default-profile.png'
            ]);
            
            if ($success) {
                $new_student_id = $rfid_conn->lastInsertId();
                echo "✅ Student added to RFID database with ID: $new_student_id<br>";
                
                // Add sample attendance data
                echo "<h2>Step 2: Adding Sample Attendance Data</h2>";
                
                $attendance_insert = $rfid_conn->prepare("
                    INSERT INTO attendance (student_id, time_in, time_out, date) 
                    VALUES (?, ?, ?, ?)
                ");
                
                // Add attendance for the past 20 days
                $attendance_added = 0;
                for ($i = 1; $i <= 20; $i++) {
                    $date = date('Y-m-d', strtotime("-$i days"));
                    $time_in = sprintf('%02d:%02d:00', rand(7, 8), rand(0, 59));
                    $time_out = sprintf('%02d:%02d:00', rand(16, 17), rand(0, 59));
                    
                    $success = $attendance_insert->execute([
                        $new_student_id,
                        "$date $time_in",
                        "$date $time_out", 
                        $date
                    ]);
                    
                    if ($success) {
                        $attendance_added++;
                    }
                }
                
                echo "✅ Added $attendance_added attendance records<br>";
                
            } else {
                echo "❌ Failed to add student to RFID database<br>";
            }
        } else {
            $rfid_student = $rfid_check->fetch(PDO::FETCH_ASSOC);
            echo "✅ Student already exists in RFID DB: {$rfid_student['name']} (ID: {$rfid_student['id']})<br>";
            
            // Check attendance count
            $att_count = $rfid_conn->prepare("SELECT COUNT(*) as count FROM attendance WHERE student_id = ?");
            $att_count->execute([$rfid_student['id']]);
            $count = $att_count->fetch(PDO::FETCH_ASSOC)['count'];
            
            echo "Current attendance records: $count<br>";
            
            if ($count < 10) {
                echo "Adding more attendance records...<br>";
                
                $attendance_insert = $rfid_conn->prepare("
                    INSERT INTO attendance (student_id, time_in, time_out, date) 
                    VALUES (?, ?, ?, ?)
                ");
                
                $attendance_added = 0;
                for ($i = 1; $i <= 20; $i++) {
                    $date = date('Y-m-d', strtotime("-$i days"));
                    
                    // Check if attendance already exists for this date
                    $check_existing = $rfid_conn->prepare("SELECT COUNT(*) as count FROM attendance WHERE student_id = ? AND date = ?");
                    $check_existing->execute([$rfid_student['id'], $date]);
                    $exists = $check_existing->fetch(PDO::FETCH_ASSOC)['count'];
                    
                    if ($exists == 0) {
                        $time_in = sprintf('%02d:%02d:00', rand(7, 8), rand(0, 59));
                        $time_out = sprintf('%02d:%02d:00', rand(16, 17), rand(0, 59));
                        
                        $success = $attendance_insert->execute([
                            $rfid_student['id'],
                            "$date $time_in",
                            "$date $time_out", 
                            $date
                        ]);
                        
                        if ($success) {
                            $attendance_added++;
                        }
                    }
                }
                
                echo "✅ Added $attendance_added new attendance records<br>";
            }
        }
        
    } else {
        echo "❌ Student 220342 not found in violations database<br>";
    }
    
    echo "<h2>Step 3: Testing API Endpoints</h2>";
    
    // Test violations endpoint
    echo "<h3>Testing Violations API</h3>";
    include 'violations/index.php';
    
} catch(Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "<br>";
    echo "Line: " . $e->getLine() . "<br>";
}

?>

<style>
body { font-family: Arial, sans-serif; margin: 20px; }
h1 { color: #2196F3; }
h2 { color: #FF9800; }
h3 { color: #4CAF50; }
</style>