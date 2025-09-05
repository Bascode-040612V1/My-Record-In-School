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
header('Access-Control-Allow-Methods: PUT, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
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
        !isset($input['year']) || 
        !isset($input['course']) || 
        !isset($input['section'])) {
        echo json_encode(array(
            "success" => false,
            "message" => "Student ID, year, course, and section are required"
        ));
        exit();
    }
    
    $student_id = trim($input['student_id']);
    $year = trim($input['year']);
    $course = trim($input['course']);
    $section = trim($input['section']);
    
    // Begin transaction for both databases
    $violations_conn->beginTransaction();
    $rfid_conn->beginTransaction();
    
    try {
        // Update in violations database
        $update_query = "UPDATE students SET year_level = :year, course = :course, section = :section 
                        WHERE student_id = :student_id";
        
        $stmt1 = $violations_conn->prepare($update_query);
        $stmt1->bindParam(':year', $year);
        $stmt1->bindParam(':course', $course);
        $stmt1->bindParam(':section', $section);
        $stmt1->bindParam(':student_id', $student_id);
        $stmt1->execute();
        
        // RFID database doesn't store year/course/section, so no update needed
        // Just mark as successful if violations DB update worked
        
        if ($stmt1->rowCount() > 0) {
            // Commit both transactions
            $violations_conn->commit();
            $rfid_conn->commit();
            
            // Get updated student data
            $get_student_query = "SELECT * FROM students WHERE student_id = :student_id LIMIT 1";
            $get_stmt = $violations_conn->prepare($get_student_query);
            $get_stmt->bindParam(':student_id', $student_id);
            $get_stmt->execute();
            $student = $get_stmt->fetch(PDO::FETCH_ASSOC);
            
            echo json_encode(array(
                "success" => true,
                "message" => "Student information updated successfully",
                "student" => array(
                    "id" => intval($student['id']),
                    "student_id" => $student['student_id'],
                    "name" => $student['student_name'],
                    "year" => $student['year_level'],
                    "course" => $student['course'],
                    "section" => $student['section'],
                    "created_at" => $student['added_at'],
                    "updated_at" => $student['updated_at']
                )
            ));
        } else {
            $violations_conn->rollback();
            $rfid_conn->rollback();
            echo json_encode(array(
                "success" => false,
                "message" => "Student not found or no changes made"
            ));
        }
        
    } catch(Exception $e) {
        $violations_conn->rollback();
        $rfid_conn->rollback();
        throw $e;
    }
    
} catch(Exception $e) {
    echo json_encode(array(
        "success" => false,
        "message" => "Update error: " . $e->getMessage()
    ));
}
?>