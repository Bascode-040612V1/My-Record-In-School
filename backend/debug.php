<?php
// Debug endpoint to help troubleshoot connection issues
ini_set('display_errors', 0);
error_reporting(0);
ob_clean();

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

$debug_info = array();

try {
    // Basic PHP info
    $debug_info['php_version'] = phpversion();
    $debug_info['server_software'] = $_SERVER['SERVER_SOFTWARE'] ?? 'Unknown';
    $debug_info['document_root'] = $_SERVER['DOCUMENT_ROOT'] ?? 'Unknown';
    $debug_info['script_name'] = $_SERVER['SCRIPT_NAME'] ?? 'Unknown';
    
    // Check if database config exists
    $config_path = __DIR__ . '/config/database.php';
    $debug_info['config_file_exists'] = file_exists($config_path);
    
    if ($debug_info['config_file_exists']) {
        // Try to include database config
        include_once $config_path;
        $debug_info['database_class_exists'] = class_exists('Database');
        
        if ($debug_info['database_class_exists']) {
            // Try to create database instance
            $database = new Database();
            $debug_info['database_instance_created'] = true;
            
            // Test violations database
            try {
                $violations_conn = $database->getViolationsConnection();
                $debug_info['violations_db_connection'] = 'Success';
                
                // Test query
                $stmt = $violations_conn->query("SELECT COUNT(*) as count FROM students");
                $result = $stmt->fetch(PDO::FETCH_ASSOC);
                $debug_info['violations_student_count'] = $result['count'];
            } catch (Exception $e) {
                $debug_info['violations_db_connection'] = 'Failed: ' . $e->getMessage();
            }
            
            // Test RFID database
            try {
                $rfid_conn = $database->getRfidConnection();
                $debug_info['rfid_db_connection'] = 'Success';
                
                // Test query
                $stmt = $rfid_conn->query("SELECT COUNT(*) as count FROM students");
                $result = $stmt->fetch(PDO::FETCH_ASSOC);
                $debug_info['rfid_student_count'] = $result['count'];
            } catch (Exception $e) {
                $debug_info['rfid_db_connection'] = 'Failed: ' . $e->getMessage();
            }
        } else {
            $debug_info['database_class_exists'] = false;
        }
    }
    
    // Return debug information
    echo json_encode(array(
        "success" => true,
        "message" => "Debug information collected",
        "debug_info" => $debug_info,
        "timestamp" => date('Y-m-d H:i:s')
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        "success" => false,
        "message" => "Debug failed: " . $e->getMessage(),
        "debug_info" => $debug_info,
        "timestamp" => date('Y-m-d H:i:s')
    ));
}
?>