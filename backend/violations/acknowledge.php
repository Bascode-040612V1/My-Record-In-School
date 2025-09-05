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
    $conn = $database->getViolationsConnection();
    
    // Get violation_id from URL path
    $path_info = $_SERVER['PATH_INFO'] ?? '';
    $path_parts = explode('/', trim($path_info, '/'));
    
    if (empty($path_parts[0]) || !is_numeric($path_parts[0])) {
        echo json_encode(array(
            "success" => false,
            "message" => "Valid violation ID is required"
        ));
        exit();
    }
    
    $violation_id = intval($path_parts[0]);
    
    // Update violation acknowledgment
    $query = "UPDATE violations SET acknowledged = 1 WHERE id = :violation_id";
    $stmt = $conn->prepare($query);
    $stmt->bindParam(':violation_id', $violation_id);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        echo json_encode(array(
            "success" => true,
            "message" => "Violation acknowledged successfully"
        ));
    } else {
        echo json_encode(array(
            "success" => false,
            "message" => "Violation not found or already acknowledged"
        ));
    }
    
} catch(Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Error acknowledging violation: " . $e->getMessage(),
        "error_details" => array(
            "line" => $e->getLine(),
            "file" => basename($e->getFile())
        )
    ));
}
?>