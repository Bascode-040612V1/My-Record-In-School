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
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

try {
    $database = new Database();
    $violations_conn = $database->getViolationsConnection();
    $rfid_conn = $database->getRfidConnection();
    
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || 
        !isset($input['student_id']) || 
        !isset($input['name']) || 
        !isset($input['password']) || 
        !isset($input['year']) || 
        !isset($input['course']) || 
        !isset($input['section'])) {
        echo json_encode(array(
            "success" => false,
            "message" => "All required fields must be provided (student_id, name, password, year, course, section)"
        ));
        exit();
    }
    
    $student_id = trim($input['student_id']);
    $name = trim($input['name']);
    $password = $input['password']; // Keep original password for storage
    $year = trim($input['year']);
    $course = trim($input['course']);
    $section = trim($input['section']);
    $rfid = isset($input['rfid']) ? trim($input['rfid']) : null; 
    
    // Hash the password for security (but keep backward compatibility)
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);
    
    // Check if student already exists in violations database
    $check_query = "SELECT student_id FROM students WHERE student_id = :student_id LIMIT 1";
    $check_stmt = $violations_conn->prepare($check_query);
    $check_stmt->bindParam(':student_id', $student_id);
    $check_stmt->execute();
    
    if ($check_stmt->rowCount() > 0) {
        echo json_encode(array(
            "success" => false,
            "message" => "Student ID already exists"
        ));
        exit();
    }
    
    // Begin transaction for both databases
    $violations_conn->beginTransaction();
    $rfid_conn->beginTransaction();
    
    try {
        // Insert into violations database with hashed password
        $insert_query = "INSERT INTO students (student_id, student_name, year_level, course, section, password) 
                        VALUES (:student_id, :name, :year, :course, :section, :password)";
        
        $stmt1 = $violations_conn->prepare($insert_query);
        $stmt1->bindParam(':student_id', $student_id);
        $stmt1->bindParam(':name', $name);
        $stmt1->bindParam(':password', $hashed_password); // Use hashed password
        $stmt1->bindParam(':year', $year);
        $stmt1->bindParam(':course', $course);
        $stmt1->bindParam(':section', $section);
        $stmt1->execute();

        //Insert into rfid_system database
        if ($rfid) {
    $rfid_insert_query = "INSERT INTO students (name, student_number, rfid) 
                          VALUES (:name, :student_number, :rfid)";
    $rfid_stmt = $rfid_conn->prepare($rfid_insert_query);
    $rfid_stmt->bindParam(':name', $name);
    $rfid_stmt->bindParam(':student_number', $student_id);
    $rfid_stmt->bindParam(':rfid', $rfid);
    $rfid_stmt->execute();
}

        $violations_student_id = $violations_conn->lastInsertId();
        
     
        
        // Commit both transactions
        $violations_conn->commit();
        $rfid_conn->commit();
        
        // Get the created student data for response
        $get_student_query = "SELECT id, student_id, student_name, year_level, course, section, added_at, updated_at 
                             FROM students WHERE student_id = :student_id LIMIT 1";
        $get_stmt = $violations_conn->prepare($get_student_query);
        $get_stmt->bindParam(':student_id', $student_id);
        $get_stmt->execute();
        $student = $get_stmt->fetch(PDO::FETCH_ASSOC);
        
        echo json_encode(array(
            "success" => true,
            "message" => "Registration successful",
            "student" => array(
                "id" => intval($student['id']),
                "student_id" => $student['student_id'],
                "name" => $student['student_name'],
                "year" => $student['year_level'],
                "course" => $student['course'],
                "section" => $student['section'],
                "created_at" => $student['added_at'] ?? date('Y-m-d H:i:s'),
                "updated_at" => $student['updated_at'] ?? date('Y-m-d H:i:s')
            )
        ));
        
    } catch(Exception $e) {
        // Rollback both transactions
        $violations_conn->rollback();
        $rfid_conn->rollback();
        throw $e;
    }
    
} catch(Exception $e) {
    // Log the error for debugging
    error_log("Registration error: " . $e->getMessage());
    
    echo json_encode(array(
        "success" => false,
        "message" => "Registration error: " . $e->getMessage(),
        "debug_info" => array(
            "line" => $e->getLine(),
            "file" => $e->getFile()
        )
    ));
}
?>