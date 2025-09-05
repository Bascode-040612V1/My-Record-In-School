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
    // Add debug headers
    header('X-RFID-Debug: Endpoint reached successfully');
    
    $database = new Database();
    $conn = $database->getRfidConnection();
    
    // Add debug info about the connection
    header('X-RFID-DB-Connected: true');
    
    // 1. Get the latest RFID scan
    $sql = "SELECT * FROM rfid_scans ORDER BY scanned_at DESC LIMIT 1";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $rfid = $row['rfid_number'];
        
        // 2. Delete all older scans (keep only latest one)
        $cleanup = "DELETE FROM rfid_scans 
                    WHERE id NOT IN (
                        SELECT * FROM (
                            SELECT id FROM rfid_scans ORDER BY scanned_at DESC LIMIT 1
                        ) AS temp
                    )";
        $cleanup_stmt = $conn->prepare($cleanup);
        $cleanup_stmt->execute();
        
        echo json_encode(array(
            "success" => true,
            "message" => "Latest RFID retrieved successfully",
            "rfid_number" => $rfid,
            "scanned_at" => $row['scanned_at'],
            "cleaned_records" => $cleanup_stmt->rowCount()
        ));
    } else {
        echo json_encode(array(
            "success" => false,
            "message" => "No RFID scanned yet",
            "rfid_number" => null
        ));
    }
    
} catch(Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Error retrieving RFID: " . $e->getMessage(),
        "error_details" => array(
            "line" => $e->getLine(),
            "file" => basename($e->getFile())
        )
    ));
}
?>