<?php
// Debug script to see what path is being received
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$request_uri = $_SERVER['REQUEST_URI'];
$request_method = $_SERVER['REQUEST_METHOD'];

// Remove query string and decode URL
$path = parse_url($request_uri, PHP_URL_PATH);
$path = urldecode($path);

// Remove base path if running in subdirectory
$base_path = '/backend'; // Adjust this based on your XAMPP setup

echo json_encode(array(
    "debug_info" => array(
        "request_uri" => $request_uri,
        "request_method" => $request_method,
        "parsed_path" => $path,
        "base_path" => $base_path,
        "path_starts_with_base" => strpos($path, $base_path) === 0,
        "path_after_base_check" => strpos($path, $base_path) === 0 ? substr($path, strlen($base_path)) : "N/A"
    )
));
?>