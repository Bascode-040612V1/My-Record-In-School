<?php
// Define constant to indicate this file is being included from API
define('INCLUDED_FROM_API', true);

// Disable error reporting and clean output buffer
ini_set('display_errors', 0);
error_reporting(0);
ob_clean();

include_once '../config/database.php';

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

try {
    $database = new Database();
    $conn = $database->getRfidConnection();
    
    // Get student_id from URL path
    $path_info = $_SERVER['PATH_INFO'] ?? '';
    $path_parts = explode('/', trim($path_info, '/'));
    
    if (empty($path_parts[0])) {
        echo json_encode(array(
            "success" => false,
            "message" => "Student ID is required"
        ));
        exit();
    }
    
    $student_number = $path_parts[0]; // This is the student_id from violations DB
    
    // First, get the student's ID from RFID database using student_number
    $student_query = "SELECT id, name, student_number FROM students WHERE student_number = :student_number LIMIT 1";
    $student_stmt = $conn->prepare($student_query);
    $student_stmt->bindParam(':student_number', $student_number);
    $student_stmt->execute();
    
    if ($student_stmt->rowCount() == 0) {
        // Student not found in RFID database, try to sync from violations database
        include_once '../config/database.php';
        $database = new Database();
        $violations_conn = $database->getViolationsConnection();
        
        // Get student from violations database
        $violations_query = "SELECT student_id, student_name FROM students WHERE student_id = :student_id LIMIT 1";
        $violations_stmt = $violations_conn->prepare($violations_query);
        $violations_stmt->bindParam(':student_id', $student_number);
        $violations_stmt->execute();
        
        if ($violations_stmt->rowCount() > 0) {
            $violations_student = $violations_stmt->fetch(PDO::FETCH_ASSOC);
            
            // Sync student to RFID database
            try {
                $sync_id = $database->syncStudentData($student_number, $violations_student['student_name']);
                
                // Now try to get the student from RFID database again
                $student_stmt->execute();
                if ($student_stmt->rowCount() > 0) {
                    $student_data = $student_stmt->fetch(PDO::FETCH_ASSOC);
                    $rfid_student_id = $student_data['id'];
                    $student_name = $student_data['name'];
                } else {
                    echo json_encode(array(
                        "success" => true,
                        "message" => "Student synchronized but no attendance data found",
                        "attendance" => array()
                    ));
                    exit();
                }
            } catch (Exception $sync_error) {
                echo json_encode(array(
                    "success" => true,
                    "message" => "Student found in violations database but sync failed: " . $sync_error->getMessage(),
                    "attendance" => array()
                ));
                exit();
            }
        } else {
            echo json_encode(array(
                "success" => true,
                "message" => "Student not found in any database",
                "attendance" => array()
            ));
            exit();
        }
    } else {
        $student_data = $student_stmt->fetch(PDO::FETCH_ASSOC);
        $rfid_student_id = $student_data['id']; // This is the ID used in attendance table
        $student_name = $student_data['name'];
    }
    
    // Get optional month and year parameters
    $month = isset($_GET['month']) ? intval($_GET['month']) : null;
    $year = isset($_GET['year']) ? intval($_GET['year']) : null;
    
// Build attendance query (wrap UNION in subquery)
$query = "
    SELECT *
    FROM (
        SELECT a.id, a.student_id, a.time_in, a.time_out, a.date, 
               s.name as student_name, s.student_number,
               'regular' as attendance_type
        FROM attendance a 
        JOIN students s ON a.student_id = s.id 
        WHERE a.student_id = :student_id

        UNION ALL

        SELECT sa.id, sa.student_id, sa.saved_time_in as time_in, sa.saved_time_out as time_out, 
               sa.saved_date as date, sa.name as student_name, sa.student_number,
               'saved' as attendance_type
        FROM saved_attendance sa 
        WHERE sa.student_id = :student_id
    ) AS combined
    WHERE 1=1
";

// Add filters
$params = [':student_id' => $rfid_student_id];

if ($month && $year) {
    $query .= " AND MONTH(date) = :month AND YEAR(date) = :year";
    $params[':month'] = $month;
    $params[':year'] = $year;
} elseif ($year) {
    $query .= " AND YEAR(date) = :year";
    $params[':year'] = $year;
}

$query .= " ORDER BY date DESC";

$stmt = $conn->prepare($query);
foreach ($params as $key => $value) {
    $stmt->bindValue($key, $value);
}
$stmt->execute();

    
    $attendance = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        // Determine status based on time_in/time_out
        $status = "ABSENT";
        $time_in_formatted = null;
        $time_out_formatted = null;
        
        if ($row['time_in']) {
            try {
                $time_in = new DateTime($row['time_in']);
                $time_in_formatted = $time_in->format('H:i:s');
                
                // Define cutoff times
                $early_cutoff = new DateTime('07:30:00'); // Before 7:30 AM = Early
                $on_time_cutoff = new DateTime('08:00:00'); // Before 8:00 AM = On Time
                $late_cutoff = new DateTime('08:30:00');   // Before 8:30 AM = Late
                
                if ($time_in <= $early_cutoff) {
                    $status = "EARLY";
                } elseif ($time_in <= $on_time_cutoff) {
                    $status = "PRESENT";
                } elseif ($time_in <= $late_cutoff) {
                    $status = "LATE";
                } else {
                    $status = "VERY_LATE";
                }
            } catch (Exception $date_error) {
                $status = "PRESENT"; // Default if date parsing fails
                $time_in_formatted = $row['time_in'];
            }
        }
        
        if ($row['time_out']) {
            try {
                $time_out = new DateTime($row['time_out']);
                $time_out_formatted = $time_out->format('H:i:s');
            } catch (Exception $date_error) {
                $time_out_formatted = $row['time_out'];
            }
        }
        
        $attendance[] = array(
            "id" => intval($row['id']),
            "student_id" => $student_number, // Return the original student_id
            "student_name" => $row['student_name'] ?: $student_name,
            "student_number" => $row['student_number'] ?: $student_number,
            "date" => $row['date'],
            "time_in" => $time_in_formatted,
            "time_out" => $time_out_formatted,
            "status" => $status,
            "attendance_type" => $row['attendance_type'] ?? 'regular',
            "created_at" => $row['date'] // Using date as created_at
        );
    }
    
    echo json_encode(array(
        "success" => true,
        "message" => count($attendance) > 0 ? "Attendance retrieved successfully" : "No attendance records found",
        "attendance" => $attendance
    ));
    
} catch(Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Error retrieving attendance: " . $e->getMessage(),
        "error_details" => array(
            "line" => $e->getLine(),
            "file" => basename($e->getFile())
        )
    ));
}
?>