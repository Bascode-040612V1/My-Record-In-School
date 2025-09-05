<?php
// Database configuration and connection management
// This file should only be included, not executed directly

class Database {
    private $host = "localhost";
    private $db_name_violations = "student_violation_db";
    private $db_name_rfid = "rfid_system";
    private $username = "root";
    private $password = "";
    public $conn_violations;
    public $conn_rfid;

    public function getViolationsConnection() {
        $this->conn_violations = null;
        try {
            $this->conn_violations = new PDO(
                "mysql:host=" . $this->host . ";dbname=" . $this->db_name_violations . ";charset=utf8mb4",
                $this->username,
                $this->password,
                array(
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_EMULATE_PREPARES => false,
                    PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4"
                )
            );
        } catch(PDOException $exception) {
            http_response_code(500);
            echo json_encode(array(
                "success" => false,
                "message" => "Violations database connection error: " . $exception->getMessage(),
                "error_code" => $exception->getCode()
            ));
            exit();
        }
        return $this->conn_violations;
    }

    public function getRfidConnection() {
        $this->conn_rfid = null;
        try {
            $this->conn_rfid = new PDO(
                "mysql:host=" . $this->host . ";dbname=" . $this->db_name_rfid . ";charset=utf8mb4",
                $this->username,
                $this->password,
                array(
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_EMULATE_PREPARES => false,
                    PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4"
                )
            );
        } catch(PDOException $exception) {
            http_response_code(500);
            echo json_encode(array(
                "success" => false,
                "message" => "RFID database connection error: " . $exception->getMessage(),
                "error_code" => $exception->getCode()
            ));
            exit();
        }
        return $this->conn_rfid;
    }

    public function testConnections() {
        try {
            // Test violations database
            $violations_conn = $this->getViolationsConnection();
            $violations_test = $violations_conn->query("SELECT COUNT(*) as count FROM students");
            $violations_result = $violations_test->fetch(PDO::FETCH_ASSOC);
            
            // Test RFID database
            $rfid_conn = $this->getRfidConnection();
            $rfid_test = $rfid_conn->query("SELECT COUNT(*) as count FROM students");
            $rfid_result = $rfid_test->fetch(PDO::FETCH_ASSOC);
            
            if ($violations_test && $rfid_test) {
                return array(
                    "success" => true,
                    "message" => "Both databases connected successfully",
                    "details" => array(
                        "violations_db" => "Connected - " . $violations_result['count'] . " students",
                        "rfid_db" => "Connected - " . $rfid_result['count'] . " students",
                        "timestamp" => date('Y-m-d H:i:s')
                    )
                );
            } else {
                return array(
                    "success" => false,
                    "message" => "Failed to connect to one or both databases"
                );
            }
        } catch(Exception $e) {
            return array(
                "success" => false,
                "message" => "Connection test failed: " . $e->getMessage(),
                "error_details" => array(
                    "line" => $e->getLine(),
                    "file" => basename($e->getFile())
                )
            );
        }
    }
    
    // Helper method to synchronize student data between databases
    public function syncStudentData($student_id, $student_name, $violations_db_id = null, $rfid_number = null) {
        try {
            $rfid_conn = $this->getRfidConnection();
            
            // Check if student exists in RFID database
            $check_query = "SELECT id FROM students WHERE student_number = :student_number LIMIT 1";
            $check_stmt = $rfid_conn->prepare($check_query);
            $check_stmt->bindParam(':student_number', $student_id);
            $check_stmt->execute();
            
            if ($check_stmt->rowCount() == 0) {
                // Student doesn't exist in RFID database, create them
                $insert_query = "INSERT INTO students (name, student_number, rfid, image) 
                               VALUES (:name, :student_number, :rfid, 'assets/default-profile.png')";
                $insert_stmt = $rfid_conn->prepare($insert_query);
                $insert_stmt->bindParam(':name', $student_name);
                $insert_stmt->bindParam(':student_number', $student_id);
                $insert_stmt->bindParam(':rfid', $rfid_number ?: ''); // Use provided RFID or empty string
                $insert_stmt->execute();
                return $rfid_conn->lastInsertId();
            } else {
                // Student exists, update RFID if provided
                if ($rfid_number) {
                    $update_query = "UPDATE students SET rfid = :rfid WHERE student_number = :student_number";
                    $update_stmt = $rfid_conn->prepare($update_query);
                    $update_stmt->bindParam(':rfid', $rfid_number);
                    $update_stmt->bindParam(':student_number', $student_id);
                    $update_stmt->execute();
                }
                
                $student_data = $check_stmt->fetch(PDO::FETCH_ASSOC);
                return $student_data['id'];
            }
        } catch(Exception $e) {
            throw new Exception("Failed to sync student data: " . $e->getMessage());
        }
    }
}

// Set appropriate headers when this file is accessed directly
if (!defined('INCLUDED_FROM_API')) {
    header('Content-Type: application/json');
    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, Authorization');
    
    if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
        exit(0);
    }
}
?>