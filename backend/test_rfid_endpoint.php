<?php
// Test script to debug RFID endpoint issues
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

echo json_encode(array(
    "test_results" => array(
        "server_info" => array(
            "request_uri" => $_SERVER['REQUEST_URI'],
            "request_method" => $_SERVER['REQUEST_METHOD'],
            "script_name" => $_SERVER['SCRIPT_NAME'],
            "path_info" => $_SERVER['PATH_INFO'] ?? 'Not set',
            "query_string" => $_SERVER['QUERY_STRING'] ?? 'Not set'
        ),
        "file_info" => array(
            "script_filename" => $_SERVER['SCRIPT_FILENAME'],
            "document_root" => $_SERVER['DOCUMENT_ROOT']
        ),
        "endpoint_tests" => array(
            "rfid_endpoint_exists" => file_exists('rfid/get_latest.php'),
            "index_file_exists" => file_exists('index.php')
        )
    )
));
?>