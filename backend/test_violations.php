<?php
// Test violations API specifically
ini_set('display_errors', 1);
error_reporting(E_ALL);

include_once 'config/database.php';

echo "<h2>Violations API Debug Test</h2>";

$database = new Database();
$conn = $database->getViolationsConnection();

$test_student_id = '220342'; // Test with your main student

echo "<h3>Testing violations lookup for student: {$test_student_id}</h3>";

try {
    // Step 1: Check if student exists in violations database
    echo "<h4>Step 1: Looking up student in violations database</h4>";
    $student_check = $conn->prepare("SELECT * FROM students WHERE student_id = ?");
    $student_check->execute([$test_student_id]);
    
    if ($student_check->rowCount() == 0) {
        echo "❌ Student {$test_student_id} not found in violations database<br>";
        
        // Let's see what students ARE in the database
        echo "<h4>Students available in violations database:</h4>";
        $all_students = $conn->query("SELECT id, student_id, student_name, course, section FROM students ORDER BY id");
        while ($row = $all_students->fetch(PDO::FETCH_ASSOC)) {
            echo "ID: {$row['id']}, Student ID: {$row['student_id']}, Name: {$row['student_name']}, Course: {$row['course']}<br>";
        }
    } else {
        $student = $student_check->fetch(PDO::FETCH_ASSOC);
        echo "✅ Student found: {$student['student_name']}<br>";
    }
    
    // Step 2: Look for violations
    echo "<h4>Step 2: Looking for violations</h4>";
    $query = "SELECT v.*, s.student_name, 
                     GROUP_CONCAT(vd.violation_type SEPARATOR ', ') as violations_list
              FROM violations v 
              JOIN students s ON v.student_id = s.student_id 
              LEFT JOIN violation_details vd ON v.id = vd.violation_id
              WHERE v.student_id = :student_id 
              GROUP BY v.id
              ORDER BY v.recorded_at DESC";
              
    $stmt = $conn->prepare($query);
    $stmt->bindParam(':student_id', $test_student_id);
    $stmt->execute();
    
    echo "Query executed. Row count: " . $stmt->rowCount() . "<br>";
    
    if ($stmt->rowCount() == 0) {
        echo "❌ No violations found for student {$test_student_id}<br>";
        
        // Let's see what violations ARE in the database
        echo "<h4>Available violations:</h4>";
        $all_violations = $conn->query("SELECT v.student_id, s.student_name, v.penalty, v.recorded_at FROM violations v JOIN students s ON v.student_id = s.student_id ORDER BY v.recorded_at DESC LIMIT 10");
        while ($row = $all_violations->fetch(PDO::FETCH_ASSOC)) {
            echo "Student: {$row['student_id']} ({$row['student_name']}), Penalty: {$row['penalty']}, Date: {$row['recorded_at']}<br>";
        }
    } else {
        echo "✅ Found {$stmt->rowCount()} violations:<br>";
        
        $violations = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            // Determine category based on violation types
            $category = "MINOR_OFFENSE"; // Default
            $violations_list = $row['violations_list'] ?? "";
            
            // Categorize based on violation types
            if (strpos($violations_list, 'No ID') !== false || 
                strpos($violations_list, 'Improper wearing of uniform') !== false ||
                strpos($violations_list, 'rubber slippers') !== false) {
                $category = "DRESS_CODE_VIOLATION";
            } elseif (strpos($violations_list, 'Cutting Classes') !== false ||
                      strpos($violations_list, 'Cheating') !== false ||
                      strpos($violations_list, 'Gambling') !== false) {
                $category = "CONDUCT_VIOLATION";
            } elseif (strpos($violations_list, 'Stealing') !== false ||
                      strpos($violations_list, 'Vandalism') !== false ||
                      strpos($violations_list, 'assault') !== false) {
                $category = "MAJOR_OFFENSE";
            }
            
            $violations[] = array(
                "id" => intval($row['id']),
                "student_id" => $row['student_id'],
                "violation_type" => $violations_list ?: "Multiple Violations",
                "violation_description" => $violations_list ?: "Multiple Violations",
                "offense_count" => intval($row['offense_count']),
                "penalty" => $row['penalty'] ?: "Warning",
                "recorded_by" => $row['recorded_by'],
                "date_recorded" => $row['recorded_at'],
                "acknowledged" => intval($row['acknowledged']),
                "category" => $category
            );
            
            echo "Violation ID: {$row['id']}, Types: {$violations_list}, Penalty: {$row['penalty']}, Date: {$row['recorded_at']}<br>";
        }
        
        echo "<h4>Final JSON Response:</h4>";
        echo "<pre>" . json_encode(array(
            "success" => true,
            "message" => "Violations retrieved successfully",
            "violations" => $violations
        ), JSON_PRETTY_PRINT) . "</pre>";
    }
    
} catch(Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "<br>";
    echo "Line: " . $e->getLine() . "<br>";
    echo "File: " . $e->getFile() . "<br>";
}

?>