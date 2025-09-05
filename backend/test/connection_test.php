<?php
// Disable error reporting to prevent HTML content in JSON response
ini_set('display_errors', 0);
error_reporting(0);

// Clean any previous output
ob_clean();

// Set proper headers
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

try {
    // Include database configuration
    include_once '../config/database.php';
    
    $database = new Database();
    
    // Test both database connections
    $violations_conn = $database->getViolationsConnection();
    $rfid_conn = $database->getRfidConnection();
    
    // Test queries
    $violations_test = $violations_conn->query("SELECT COUNT(*) as count FROM students");
    $violations_count = $violations_test->fetch(PDO::FETCH_ASSOC)['count'];
    
    $rfid_test = $rfid_conn->query("SELECT COUNT(*) as count FROM students");
    $rfid_count = $rfid_test->fetch(PDO::FETCH_ASSOC)['count'];
    
    // Return success response
    echo json_encode(array(
        "success" => true,
        "message" => "Database connections successful",
        "details" => array(
            "violations_db" => array(
                "status" => "Connected",
                "student_count" => $violations_count
            ),
            "rfid_db" => array(
                "status" => "Connected", 
                "student_count" => $rfid_count
            ),
            "timestamp" => date('Y-m-d H:i:s')
        )
    ));
    
} catch(Exception $e) {
    // Return error response
    echo json_encode(array(
        "success" => false,
        "message" => "Database connection failed",
        "error" => $e->getMessage(),
        "error_code" => $e->getCode(),
        "timestamp" => date('Y-m-d H:i:s')
    ));
}
?>