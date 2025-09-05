<?php
// Direct test of violations endpoint for student 220342
echo "=== Testing Violations Endpoint for Student 220342 ===\n\n";

// Set up the environment to simulate the API call
$_SERVER['REQUEST_METHOD'] = 'GET';
$_SERVER['PATH_INFO'] = '/220342';

// Include the violations endpoint directly
ob_start();
include 'backend/violations/index.php';
$response = ob_get_clean();

echo "Response from violations endpoint:\n";
echo $response;
echo "\n\n=== Test Complete ===\n";
?>