<?php
// Define constant to indicate files are being included from API
define('INCLUDED_FROM_API', true);

// Disable error reporting to prevent HTML in JSON response
ini_set('display_errors', 0);
error_reporting(0);

// Clean any previous output
ob_clean();

// Set JSON headers
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Handle routing for the API endpoints
$request_uri = $_SERVER['REQUEST_URI'];
$request_method = $_SERVER['REQUEST_METHOD'];

// Remove query string and decode URL
$path = parse_url($request_uri, PHP_URL_PATH);
$path = urldecode($path);

// Remove base path if running in subdirectory
$base_path = '/backend'; // Adjust this based on your XAMPP setup
error_log("Original path: $path, Base path: $base_path");
if (strpos($path, $base_path) === 0) {
    $path = substr($path, strlen($base_path));
    error_log("Path after base removal: $path");
}

// If path is still empty after removing base path, set to root
if (empty($path)) {
    $path = '/';
}

// Log the request for debugging
error_log("API Request: $request_method $path (Original: {$_SERVER['REQUEST_URI']})");

// Add debug header for testing
header('X-Debug-Path: ' . $path);
header('X-Debug-Method: ' . $request_method);
header('X-Debug-Original-URI: ' . $_SERVER['REQUEST_URI']);

// Route the requests
switch (true) {
    // Root path - show API info
    case ($path === '' || $path === '/') && $request_method === 'GET':
        echo json_encode(array(
            "success" => true,
            "message" => "MyRecordinSchool Backend API",
            "version" => "1.0",
            "endpoints" => array(
                "POST /auth/login.php" => "Student login",
                "POST /auth/register.php" => "Student registration",
                "PUT /student/update.php" => "Update student info",
                "GET /violations/{student_id}" => "Get student violations",
                "PUT /violations/acknowledge/{id}" => "Acknowledge violation",
                "GET /attendance/{student_id}" => "Get student attendance",
                "GET /rfid/get_latest.php" => "Get latest RFID scan",
                "GET /test_connection.php" => "Test database connections"
            ),
            "timestamp" => date('Y-m-d H:i:s')
        ));
        break;
        
    // Authentication routes
    case $path === '/auth/login.php' && $request_method === 'POST':
        include 'auth/login.php';
        break;
        
    case $path === '/auth/register.php' && $request_method === 'POST':
        include 'auth/register.php';
        break;
    
    // Student routes
    case $path === '/student/update.php' && $request_method === 'PUT':
        include 'student/update.php';
        break;
    
    // Violation routes
    case preg_match('#^/violations/([a-zA-Z0-9]+)$#', $path, $matches) && $request_method === 'GET':
        $_SERVER['PATH_INFO'] = '/' . $matches[1];
        include 'violations/index.php';
        break;
        
    case preg_match('#^/violations/acknowledge/(\d+)$#', $path, $matches) && $request_method === 'PUT':
        $_SERVER['PATH_INFO'] = '/' . $matches[1];
        include 'violations/acknowledge.php';
        break;
    
    // Attendance routes
    case preg_match('#^/attendance/([^/]+)$#', $path, $matches) && $request_method === 'GET':
        $_SERVER['PATH_INFO'] = '/' . $matches[1];
        include 'attendance/index.php';
        break;
    
    // Test connection
    case $path === '/test_connection.php' && $request_method === 'GET':
        include 'test_connection.php';
        break;
    
    // RFID routes
    case $path === '/rfid/get_latest' && $request_method === 'GET':
        error_log("RFID Route Matched: Path=$path, Method=$request_method");
        header('X-RFID-Route-Matched: true');
        include 'rfid/get_latest.php';
        break;

    
    // Handle direct file access (for simple testing)
    default:
        // Check if it's a direct PHP file access
        if (preg_match('#\\.php$#', $path)) {
            $file_path = '.' . $path;
            if (file_exists($file_path)) {
                include $file_path;
                break;
            }
        }
        
        // 404 Not Found
        http_response_code(404);
        header('Content-Type: application/json');
        echo json_encode(array(
            "success" => false,
            "message" => "Endpoint not found",
            "requested_path" => $path,
            "original_uri" => $_SERVER['REQUEST_URI'],
            "method" => $request_method,
            "available_endpoints" => array(
                "GET /violations/{student_id}",
                "PUT /violations/acknowledge/{id}", 
                "GET /attendance/{student_id}",
                "GET /rfid/get_latest.php",
                "POST /auth/login.php",
                "POST /auth/register.php",
                "PUT /student/update.php",
                "GET /test_connection.php"
            )
        ));
        break;
}
?>