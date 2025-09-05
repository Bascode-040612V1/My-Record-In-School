<?php
// Direct test for RFID functionality - bypasses routing
error_reporting(E_ALL);
ini_set('display_errors', 1);

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

echo "Testing RFID functionality...\n";

try {
    // Test database connection
    include_once 'config/database.php';
    
    $database = new Database();
    $conn = $database->getRfidConnection();
    
    echo "Database connection successful!\n";
    
    // Check if rfid_scans table exists
    $table_check = $conn->query("SHOW TABLES LIKE 'rfid_scans'");
    if ($table_check->rowCount() > 0) {
        echo "rfid_scans table exists!\n";
        
        // Check data in table
        $data_check = $conn->query("SELECT COUNT(*) as count FROM rfid_scans");
        $count = $data_check->fetch(PDO::FETCH_ASSOC)['count'];
        echo "Records in rfid_scans: $count\n";
        
        // Get latest scan
        $latest_query = "SELECT * FROM rfid_scans ORDER BY scanned_at DESC LIMIT 1";
        $stmt = $conn->prepare($latest_query);
        $stmt->execute();
        
        if ($stmt->rowCount() > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            
            $result = array(
                "success" => true,
                "message" => "Latest RFID retrieved successfully",
                "rfid_number" => $row['rfid_number'],
                "scanned_at" => $row['scanned_at'],
                "total_records" => $count,
                "debug" => "Direct test successful"
            );
        } else {
            $result = array(
                "success" => false,
                "message" => "No RFID scanned yet",
                "rfid_number" => null,
                "total_records" => $count,
                "debug" => "Table exists but no data"
            );
        }
    } else {
        $result = array(
            "success" => false,
            "message" => "rfid_scans table does not exist",
            "debug" => "Table missing"
        );
    }
    
    echo json_encode($result, JSON_PRETTY_PRINT);
    
} catch(Exception $e) {
    echo json_encode(array(
        "success" => false,
        "message" => "Error: " . $e->getMessage(),
        "debug" => "Exception caught",
        "line" => $e->getLine(),
        "file" => $e->getFile()
    ), JSON_PRETTY_PRINT);
}
?>