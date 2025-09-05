<?php
// Complete API endpoint testing and verification
ini_set('display_errors', 1);
error_reporting(E_ALL);

echo "<h1>Complete Backend API Testing</h1>";

// Test 1: Database Connections
echo "<h2>1. Testing Database Connections</h2>";
try {
    include_once 'config/database.php';
    $database = new Database();
    
    // Test violations database
    $violations_conn = $database->getViolationsConnection();
    echo "✅ Violations database: Connected<br>";
    
    // Test RFID database  
    $rfid_conn = $database->getRfidConnection();
    echo "✅ RFID database: Connected<br>";
    
    // Check if student 220342 exists in both databases
    echo "<h3>Student 220342 Data Check:</h3>";
    
    // Check violations database
    $viol_check = $violations_conn->prepare("SELECT student_id, student_name FROM students WHERE student_id = ?");
    $viol_check->execute(['220342']);
    if ($viol_check->rowCount() > 0) {
        $student = $viol_check->fetch(PDO::FETCH_ASSOC);
        echo "✅ Found in violations DB: {$student['student_name']}<br>";
        
        // Check violations count
        $viol_count = $violations_conn->prepare("SELECT COUNT(*) as count FROM violations WHERE student_id = ?");
        $viol_count->execute(['220342']);
        $count = $viol_count->fetch(PDO::FETCH_ASSOC)['count'];
        echo "✅ Violations count: {$count}<br>";
    } else {
        echo "❌ Student 220342 NOT found in violations database<br>";
    }
    
    // Check RFID database
    $rfid_check = $rfid_conn->prepare("SELECT id, name, student_number FROM students WHERE student_number = ?");
    $rfid_check->execute(['220342']);
    if ($rfid_check->rowCount() > 0) {
        $student = $rfid_check->fetch(PDO::FETCH_ASSOC);
        echo "✅ Found in RFID DB: {$student['name']} (ID: {$student['id']})<br>";
        
        // Check attendance count
        $att_count = $rfid_conn->prepare("SELECT COUNT(*) as count FROM attendance WHERE student_id = ?");
        $att_count->execute([$student['id']]);
        $count = $att_count->fetch(PDO::FETCH_ASSOC)['count'];
        echo "✅ Attendance count: {$count}<br>";
        
        if ($count == 0) {
            echo "❌ No attendance records found for student ID {$student['id']}<br>";
            echo "📝 Need to add attendance data for this student<br>";
        }
    } else {
        echo "❌ Student 220342 NOT found in RFID database<br>";
        echo "📝 Need to add student to RFID database<br>";
    }
    
} catch(Exception $e) {
    echo "❌ Database error: " . $e->getMessage() . "<br>";
}

// Test 2: API Endpoint URLs
echo "<h2>2. Testing API Endpoint URLs</h2>";

$current_url = "http://" . $_SERVER['HTTP_HOST'] . $_SERVER['REQUEST_URI'];
$base_url = dirname($current_url);

echo "Current URL: $current_url<br>";
echo "Base URL: $base_url<br>";

$endpoints_to_test = [
    "violations/220342" => "GET",
    "attendance/220342" => "GET", 
    "test_connection.php" => "GET"
];

foreach ($endpoints_to_test as $endpoint => $method) {
    $test_url = $base_url . '/' . $endpoint;
    echo "<h3>Testing: $test_url</h3>";
    
    // Use curl to test the endpoint
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $test_url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HEADER, true);
    curl_setopt($ch, CURLOPT_NOBODY, false);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);
    
    $response = curl_exec($ch);
    $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $error = curl_error($ch);
    curl_close($ch);
    
    if ($error) {
        echo "❌ Curl error: $error<br>";
    } else {
        if ($http_code == 200) {
            echo "✅ HTTP $http_code - Success<br>";
            
            // Extract just the JSON part of the response
            $header_size = curl_getinfo($ch, CURLINFO_HEADER_SIZE);
            $body = substr($response, strpos($response, "\r\n\r\n") + 4);
            
            // Try to decode JSON and show result
            $json = json_decode($body, true);
            if ($json) {
                if (isset($json['success']) && $json['success']) {
                    echo "✅ API Response: Success<br>";
                    if (isset($json['violations'])) {
                        echo "✅ Violations count: " . count($json['violations']) . "<br>";
                    }
                    if (isset($json['attendance'])) {
                        echo "✅ Attendance count: " . count($json['attendance']) . "<br>";
                    }
                } else {
                    echo "⚠️ API Response: " . ($json['message'] ?? 'Unknown error') . "<br>";
                }
            } else {
                echo "⚠️ Invalid JSON response<br>";
                echo "Response preview: " . substr($body, 0, 200) . "...<br>";
            }
        } else {
            echo "❌ HTTP $http_code - Failed<br>";
        }
    }
    echo "<br>";
}

// Test 3: Show correct URLs for Android app
echo "<h2>3. Correct URLs for Android App</h2>";

$host = $_SERVER['HTTP_HOST'];
$port = $_SERVER['SERVER_PORT'];

echo "<strong>Use these settings in your Android app:</strong><br>";

if ($port == 80) {
    echo "IP Address: " . explode(':', $host)[0] . "<br>";
    echo "Port: 80<br>";
    echo "Base URL: http://" . explode(':', $host)[0] . "/backend/<br>";
} else {
    echo "IP Address: " . explode(':', $host)[0] . "<br>";
    echo "Port: $port<br>";
    echo "Base URL: http://" . explode(':', $host)[0] . ":$port/backend/<br>";
}

echo "<br><strong>Test these URLs in your browser:</strong><br>";
$test_base = "http://" . $_SERVER['HTTP_HOST'] . dirname($_SERVER['REQUEST_URI']);
echo "• <a href='$test_base/violations/220342' target='_blank'>$test_base/violations/220342</a><br>";
echo "• <a href='$test_base/attendance/220342' target='_blank'>$test_base/attendance/220342</a><br>";
echo "• <a href='$test_base/test_connection.php' target='_blank'>$test_base/test_connection.php</a><br>";

?>

<style>
body { font-family: Arial, sans-serif; margin: 20px; }
h1 { color: #2196F3; }
h2 { color: #FF9800; }
h3 { color: #4CAF50; }
</style>