<?php
// Quick test endpoint to verify violations and attendance are working
ini_set('display_errors', 0);
error_reporting(0);
ob_clean();

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

try {
    include_once '../config/database.php';
    
    $database = new Database();
    $violations_conn = $database->getViolationsConnection();
    $rfid_conn = $database->getRfidConnection();
    
    // Test data availability
    $violations_test = $violations_conn->query("SELECT COUNT(*) as count FROM violations");
    $violations_count = $violations_test->fetch(PDO::FETCH_ASSOC)['count'];
    
    $attendance_test = $rfid_conn->query("SELECT COUNT(*) as count FROM attendance");
    $attendance_count = $attendance_test->fetch(PDO::FETCH_ASSOC)['count'];
    
    $students_violations = $violations_conn->query("SELECT COUNT(*) as count FROM students");
    $students_v_count = $students_violations->fetch(PDO::FETCH_ASSOC)['count'];
    
    $students_rfid = $rfid_conn->query("SELECT COUNT(*) as count FROM students");
    $students_r_count = $students_rfid->fetch(PDO::FETCH_ASSOC)['count'];
    
    // Test specific student data
    $student_id = '220342'; // Use existing student from your database
    
    // Check violations for this student
    $viol_query = "SELECT COUNT(*) as count FROM violations WHERE student_id = :student_id";
    $viol_stmt = $violations_conn->prepare($viol_query);
    $viol_stmt->bindParam(':student_id', $student_id);
    $viol_stmt->execute();
    $student_violations = $viol_stmt->fetch(PDO::FETCH_ASSOC)['count'];
    
    // Check attendance for this student (need to find their ID in RFID system)
    $rfid_query = "SELECT id FROM students WHERE student_number = :student_id";
    $rfid_stmt = $rfid_conn->prepare($rfid_query);
    $rfid_stmt->bindParam(':student_id', $student_id);
    $rfid_stmt->execute();
    $rfid_student = $rfid_stmt->fetch(PDO::FETCH_ASSOC);
    
    $student_attendance = 0;
    if ($rfid_student) {
        $att_query = "SELECT COUNT(*) as count FROM attendance WHERE student_id = :rfid_id";
        $att_stmt = $rfid_conn->prepare($att_query);
        $att_stmt->bindParam(':rfid_id', $rfid_student['id']);
        $att_stmt->execute();
        $student_attendance = $att_stmt->fetch(PDO::FETCH_ASSOC)['count'];
    }
    
    echo json_encode(array(
        "success" => true,
        "message" => "Data test completed",
        "data_summary" => array(
            "total_violations" => intval($violations_count),
            "total_attendance" => intval($attendance_count),
            "students_in_violations_db" => intval($students_v_count),
            "students_in_rfid_db" => intval($students_r_count),
            "test_student_id" => $student_id,
            "test_student_violations" => intval($student_violations),
            "test_student_attendance" => intval($student_attendance),
            "rfid_student_exists" => $rfid_student !== false
        ),
        "endpoints_to_test" => array(
            "violations" => "GET /backend/violations/" . $student_id,
            "attendance" => "GET /backend/attendance/" . $student_id
        )
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        "success" => false,
        "message" => "Test failed: " . $e->getMessage()
    ));
}
?>