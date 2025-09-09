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
    
    $student_number = $path_parts[0];
    
    // Get optional parameters for optimization
    $month = isset($_GET['month']) ? intval($_GET['month']) : null;
    $year = isset($_GET['year']) ? intval($_GET['year']) : null;
    $since_timestamp = isset($_GET['since']) ? intval($_GET['since']) : 0;
    $limit = isset($_GET['limit']) ? intval($_GET['limit']) : 0;
    
    // Get student from RFID database
    $student_query = "SELECT id, name, student_number FROM students WHERE student_number = :student_number LIMIT 1";
    $student_stmt = $conn->prepare($student_query);
    $student_stmt->bindParam(':student_number', $student_number);
    $student_stmt->execute();
    
    if ($student_stmt->rowCount() == 0) {
        // Try to sync from violations database
        $violations_conn = $database->getViolationsConnection();
        $violations_query = "SELECT student_id, student_name FROM students WHERE student_id = :student_id LIMIT 1";
        $violations_stmt = $violations_conn->prepare($violations_query);
        $violations_stmt->bindParam(':student_id', $student_number);
        $violations_stmt->execute();
        
        if ($violations_stmt->rowCount() > 0) {
            $violations_student = $violations_stmt->fetch(PDO::FETCH_ASSOC);
            try {
                $database->syncStudentData($student_number, $violations_student['student_name']);
                $student_stmt->execute();
                if ($student_stmt->rowCount() > 0) {
                    $student_data = $student_stmt->fetch(PDO::FETCH_ASSOC);
                    $rfid_student_id = $student_data['id'];
                    $student_name = $student_data['name'];
                } else {
                    echo json_encode(array("success" => true, "message" => "Student synchronized but no attendance data", "attendance" => array()));
                    exit();
                }
            } catch (Exception $sync_error) {
                echo json_encode(array("success" => true, "message" => "Sync failed: " . $sync_error->getMessage(), "attendance" => array()));
                exit();
            }
        } else {
            echo json_encode(array("success" => true, "message" => "Student not found", "attendance" => array()));
            exit();
        }
    } else {
        $student_data = $student_stmt->fetch(PDO::FETCH_ASSOC);
        $rfid_student_id = $student_data['id'];
        $student_name = $student_data['name'];
    }

    // Build optimized attendance query
    if ($since_timestamp > 0) {
        // Delta sync - only get records since timestamp
        $since_date = date('Y-m-d', $since_timestamp / 1000);
        $query = "
            SELECT *
            FROM (
                SELECT a.id, a.student_id, a.time_in, a.time_out, a.date, 
                       s.name as student_name, s.student_number,
                       'regular' as attendance_type
                FROM attendance a 
                JOIN students s ON a.student_id = s.id 
                WHERE a.student_id = :student_id AND a.date > :since_date

                UNION ALL

                SELECT sa.id, sa.student_id, sa.saved_time_in as time_in, sa.saved_time_out as time_out, 
                       sa.saved_date as date, sa.name as student_name, sa.student_number,
                       'saved' as attendance_type
                FROM saved_attendance sa 
                WHERE sa.student_id = :student_id AND sa.saved_date > :since_date
            ) AS combined
            WHERE 1=1
        ";
        $params = [':student_id' => $rfid_student_id, ':since_date' => $since_date];
    } else {
        // Full sync with optional date filters
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
        $params = [':student_id' => $rfid_student_id];
    }

    // Add month/year filters
    if ($month && $year) {
        $query .= " AND MONTH(date) = :month AND YEAR(date) = :year";
        $params[':month'] = $month;
        $params[':year'] = $year;
    } elseif ($year) {
        $query .= " AND YEAR(date) = :year";
        $params[':year'] = $year;
    }

    $query .= " ORDER BY date DESC";
    
    // Add limit for performance
    if ($limit > 0) {
        $query .= " LIMIT :limit";
        $params[':limit'] = $limit;
    }

    $stmt = $conn->prepare($query);
    foreach ($params as $key => $value) {
        if ($key === ':limit') {
            $stmt->bindValue($key, $value, PDO::PARAM_INT);
        } else {
            $stmt->bindValue($key, $value);
        }
    }
    $stmt->execute();
    
    $attendance = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        // Determine status based on time_in
        $status = "ABSENT";
        $time_in_formatted = null;
        $time_out_formatted = null;
        
        if ($row['time_in']) {
            try {
                $time_in = new DateTime($row['time_in']);
                $time_in_formatted = $time_in->format('H:i:s');
                
                // Efficient status determination
                $hour = intval($time_in->format('H'));
                $minute = intval($time_in->format('i'));
                $total_minutes = $hour * 60 + $minute;
                
                if ($total_minutes <= 450) { // Before 7:30 AM
                    $status = "EARLY";
                } elseif ($total_minutes <= 480) { // Before 8:00 AM
                    $status = "PRESENT";
                } elseif ($total_minutes <= 510) { // Before 8:30 AM
                    $status = "LATE";
                } else {
                    $status = "VERY_LATE";
                }
            } catch (Exception $date_error) {
                $status = "PRESENT";
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
            "student_id" => $student_number,
            "student_name" => $row['student_name'] ?: $student_name,
            "student_number" => $row['student_number'] ?: $student_number,
            "date" => $row['date'],
            "time_in" => $time_in_formatted,
            "time_out" => $time_out_formatted,
            "status" => $status,
            "attendance_type" => $row['attendance_type'] ?? 'regular',
            "created_at" => $row['date']
        );
    }
    
    echo json_encode(array(
        "success" => true,
        "message" => count($attendance) > 0 ? "Attendance retrieved successfully" : "No attendance records found",
        "attendance" => $attendance,
        "sync_info" => array(
            "is_delta_sync" => $since_timestamp > 0,
            "since_timestamp" => $since_timestamp,
            "limit_applied" => $limit > 0 ? $limit : null,
            "month_filter" => $month,
            "year_filter" => $year,
            "server_timestamp" => time() * 1000
        )
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