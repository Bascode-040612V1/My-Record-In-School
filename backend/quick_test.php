<?php
// Quick test to check if violations endpoint is working
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

// Test database connection
include_once 'config/database.php';

try {
    $database = new Database();
    $conn = $database->getViolationsConnection();
    
    // Test if we have any violations data
    $query = "SELECT COUNT(*) as total FROM violations";
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    // Test specific student
    $student_id = '220342';
    $query2 = "SELECT * FROM violations WHERE student_id = :student_id LIMIT 5";
    $stmt2 = $conn->prepare($query2);
    $stmt2->bindParam(':student_id', $student_id);
    $stmt2->execute();
    $violations = $stmt2->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode(array(
        "success" => true,
        "message" => "Database connection successful",
        "total_violations" => $result['total'],
        "student_violations_count" => count($violations),
        "sample_violations" => $violations,
        "test_url" => "http://localhost/backend/violations/220342"
    ));
    
} catch(Exception $e) {
    echo json_encode(array(
        "success" => false,
        "message" => "Database error: " . $e->getMessage()
    ));
}
?>