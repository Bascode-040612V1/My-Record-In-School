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
    
    if (!$input || !isset($input['student_id']) || !isset($input['password'])) {
        echo json_encode(array(
            "success" => false,
            "message" => "Student ID and password are required"
        ));
        exit();
    }
    
    $student_id = $input['student_id'];
    $password = $input['password'];
    
    // Check in violations database first (this is where login credentials are stored)
    $query = "SELECT id, student_id, student_name, year_level, course, section, password, added_at, updated_at 
              FROM students WHERE student_id = :student_id LIMIT 1";
    $stmt = $violations_conn->prepare($query);
    $stmt->bindParam(':student_id', $student_id);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        $student = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Check password - handle both plain text and hashed passwords
        $password_valid = false;
        if ($student['password']) {
            // Check if it's a hashed password
            if (password_verify($password, $student['password'])) {
                $password_valid = true;
            } else if ($student['password'] === $password) {
                // Plain text password match (for backward compatibility)
                $password_valid = true;
            }
        } else {
            // No password set, check if password matches student_id (default behavior)
            $password_valid = ($password === $student_id);
        }
        
        if ($password_valid) {
            // Ensure student exists in RFID database for attendance tracking
            try {
                $database->syncStudentData($student_id, $student['student_name'], $student['id']);
            } catch(Exception $sync_error) {
                // Log sync error but don't fail login
                error_log("Student sync failed: " . $sync_error->getMessage());
            }
            echo json_encode(array(
                "success" => true,
                "message" => "Login successful",
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
        } else {
            echo json_encode(array(
                "success" => false,
                "message" => "Invalid password"
            ));
        }
    } else {
        echo json_encode(array(
            "success" => false,
            "message" => "Student ID not found"
        ));
    }
    
} catch(Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Login error: " . $e->getMessage(),
        "error_details" => array(
            "line" => $e->getLine(),
            "file" => basename($e->getFile())
        )
    ));
}
?>