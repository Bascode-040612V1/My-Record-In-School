<?php
// Define constant to indicate this file is being included from API
define('INCLUDED_FROM_API', true);

// Disable error reporting and clean output buffer
ini_set('display_errors', 0);
error_reporting(0);
ob_clean();

include_once '../config/database.php';

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

try {
    $database = new Database();
    $conn = $database->getViolationsConnection();
    
    // Get student_id from URL path
    $path_info = $_SERVER['PATH_INFO'] ?? '';
    $path_parts = explode('/', trim($path_info, '/'));
    
    if (empty($path_parts[0])) {
        echo json_encode(array(
            "success" => false,
            "message" => "Student ID is required"
        ));
        exit();
    }
    
    $student_id = $path_parts[0];
    
    // Check for delta sync parameters for optimization
    $since_timestamp = isset($_GET['since']) ? intval($_GET['since']) : 0;
    $limit = isset($_GET['limit']) ? intval($_GET['limit']) : 0;
    
    // Build optimized query based on parameters
    if ($since_timestamp > 0) {
        // Delta sync - only get records modified since timestamp
        $since_date = date('Y-m-d H:i:s', $since_timestamp / 1000);
        $query = "SELECT v.id, v.student_id, v.student_name, v.year_level, v.course, v.section,
                         v.offense_count, v.penalty, v.recorded_by, v.recorded_at, v.acknowledged,
                         GROUP_CONCAT(DISTINCT vd.violation_type ORDER BY vd.id SEPARATOR ', ') as violations_list
                  FROM violations v 
                  LEFT JOIN violation_details vd ON v.id = vd.violation_id
                  WHERE v.student_id = :student_id AND v.recorded_at > :since_date
                  GROUP BY v.id
                  ORDER BY v.recorded_at DESC";
        if ($limit > 0) {
            $query .= " LIMIT :limit";
        }
        $stmt = $conn->prepare($query);
        $stmt->bindParam(':student_id', $student_id);
        $stmt->bindParam(':since_date', $since_date);
        if ($limit > 0) {
            $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        }
    } else {
        // Full sync with optional limit
        $query = "SELECT v.id, v.student_id, v.student_name, v.year_level, v.course, v.section,
                         v.offense_count, v.penalty, v.recorded_by, v.recorded_at, v.acknowledged,
                         GROUP_CONCAT(DISTINCT vd.violation_type ORDER BY vd.id SEPARATOR ', ') as violations_list,
                         COALESCE((SELECT MAX(soc.offense_count) 
                                  FROM student_violation_offense_counts soc 
                                  INNER JOIN violation_details vd2 ON vd2.violation_type = soc.violation_type 
                                  WHERE vd2.violation_id = v.id AND soc.student_id = v.student_id), v.offense_count) as highest_offense_count
                  FROM violations v 
                  LEFT JOIN violation_details vd ON v.id = vd.violation_id
                  WHERE v.student_id = :student_id 
                  GROUP BY v.id
                  ORDER BY v.recorded_at DESC";
        if ($limit > 0) {
            $query .= " LIMIT :limit";
        }
        $stmt = $conn->prepare($query);
        $stmt->bindParam(':student_id', $student_id);
        if ($limit > 0) {
            $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        }
    }
    
    $stmt->execute();
    
    $violations = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        // Determine category based on violation types
        $category = "MINOR_OFFENSE"; // Default
        $violations_list = $row['violations_list'] ?? "";
        
        // Categorize based on violation types
        if (strpos($violations_list, 'No ID') !== false || 
            strpos($violations_list, 'Improper wearing of uniform') !== false ||
            strpos($violations_list, 'rubber slippers') !== false ||
            strpos($violations_list, 'earring') !== false ||
            strpos($violations_list, 'haircut') !== false) {
            $category = "DRESS_CODE_VIOLATION";
        } elseif (strpos($violations_list, 'Cutting Classes') !== false ||
                  strpos($violations_list, 'Cheating') !== false ||
                  strpos($violations_list, 'Gambling') !== false ||
                  strpos($violations_list, 'cellphone') !== false ||
                  strpos($violations_list, 'Smoking') !== false) {
            $category = "CONDUCT_VIOLATION";
        } elseif (strpos($violations_list, 'Stealing') !== false ||
                  strpos($violations_list, 'Vandalism') !== false ||
                  strpos($violations_list, 'assault') !== false ||
                  strpos($violations_list, 'Drugs') !== false ||
                  strpos($violations_list, 'Liquor') !== false ||
                  strpos($violations_list, 'fraternity') !== false) {
            $category = "MAJOR_OFFENSE";
        }
        
        $violations[] = array(
            "id" => intval($row['id']),
            "student_id" => $row['student_id'],
            "student_name" => $row['student_name'],
            "year_level" => $row['year_level'],
            "course" => $row['course'],
            "section" => $row['section'],
            "violation_type" => $violations_list ?: "No specific violation",
            "violation_description" => $violations_list ?: "Multiple violations",
            "offense_count" => intval($row['highest_offense_count'] ?? $row['offense_count']),
            "original_offense_count" => intval($row['offense_count']),
            "penalty" => $row['penalty'] ?: "Warning",
            "recorded_by" => $row['recorded_by'],
            "date_recorded" => $row['recorded_at'],
            "acknowledged" => intval($row['acknowledged']),
            "category" => $category
        );
    }
    
    // Return optimized results
    echo json_encode(array(
        "success" => true,
        "message" => count($violations) > 0 ? "Violations retrieved successfully" : "No violations found for this student. Student ID: {$student_id}",
        "violations" => $violations,
        "sync_info" => array(
            "is_delta_sync" => $since_timestamp > 0,
            "since_timestamp" => $since_timestamp,
            "limit_applied" => $limit > 0 ? $limit : null,
            "server_timestamp" => time() * 1000 // Current server time in milliseconds
        ),
        "debug_info" => array(
            "student_id_searched" => $student_id,
            "violations_count" => count($violations)
        )
    ));
    
} catch(Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Error retrieving violations: " . $e->getMessage(),
        "error_details" => array(
            "line" => $e->getLine(),
            "file" => basename($e->getFile())
        )
    ));
}
?>