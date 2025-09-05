<?php
// Test script to simulate RFID scanning for testing purposes
ini_set('display_errors', 0);
error_reporting(0);
ob_clean();

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
    include_once '../config/database.php';
    
    $database = new Database();
    $conn = $database->getRfidConnection();
    
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['rfid_number']) || !isset($input['action'])) {
        echo json_encode(array(
            "success" => false,
            "message" => "RFID number and action are required"
        ));
        exit();
    }
    
    $rfid_number = trim($input['rfid_number']);
    $action = $input['action'];
    
    if ($action === 'insert_test_scan') {
        // Clear any existing scans first
        $clear_query = "DELETE FROM rfid_scans";
        $clear_stmt = $conn->prepare($clear_query);
        $clear_stmt->execute();
        
        // Insert new RFID scan
        $insert_query = "INSERT INTO rfid_scans (rfid_number, scanned_at) VALUES (:rfid_number, NOW())";
        $insert_stmt = $conn->prepare($insert_query);
        $insert_stmt->bindParam(':rfid_number', $rfid_number);
        $insert_stmt->execute();
        
        echo json_encode(array(
            "success" => true,
            "message" => "Test RFID scan inserted successfully",
            "rfid_number" => $rfid_number,
            "cleared_records" => $clear_stmt->rowCount(),
            "scan_id" => $conn->lastInsertId()
        ));
    } else {
        echo json_encode(array(
            "success" => false,
            "message" => "Invalid action"
        ));
    }
    
} catch(Exception $e) {
    echo json_encode(array(
        "success" => false,
        "message" => "Error: " . $e->getMessage(),
        "error_details" => array(
            "line" => $e->getLine(),
            "file" => basename($e->getFile())
        )
    ));
}
?>