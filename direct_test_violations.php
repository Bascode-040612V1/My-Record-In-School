<?php
// Direct test for violations endpoint
error_reporting(E_ALL);
ini_set('display_errors', 1);

echo "=== Direct Violations Test ===\n";

// Set up environment
$_SERVER['REQUEST_METHOD'] = 'GET';
$_SERVER['PATH_INFO'] = '/220342';

echo "Testing violations for student 220342...\n\n";

try {
    // Include the violations endpoint
    ob_start();
    include 'backend/violations/index.php';
    $output = ob_get_clean();
    
    echo "Output:\n";
    echo $output;
    echo "\n\n";
    
} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
    echo "Line: " . $e->getLine() . "\n";
    echo "File: " . $e->getFile() . "\n";
}

echo "=== Test Complete ===\n";
?>