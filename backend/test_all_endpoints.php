<?php
// Comprehensive test script for all backend endpoints
error_reporting(E_ALL);
ini_set('display_errors', 1);

echo "<h1>MyRecordinSchool Backend Test Suite</h1>\n";
echo "<p>Testing all endpoints...</p>\n";

function testEndpoint($url, $method = 'GET', $data = null) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
    
    if ($data && ($method === 'POST' || $method === 'PUT')) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    }
    
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $error = curl_error($ch);
    curl_close($ch);
    
    return [
        'response' => $response,
        'http_code' => $httpCode,
        'error' => $error
    ];
}

$baseUrl = 'http://localhost/backend';

echo "<h2>1. Testing Database Connections</h2>\n";
$result = testEndpoint("$baseUrl/test_connection.php");
echo "<pre>HTTP {$result['http_code']}: " . htmlspecialchars($result['response']) . "</pre>\n";

echo "<h2>2. Testing Root API Info</h2>\n";
$result = testEndpoint("$baseUrl/");
echo "<pre>HTTP {$result['http_code']}: " . htmlspecialchars($result['response']) . "</pre>\n";

echo "<h2>3. Testing Student Registration</h2>\n";
$registerData = [
    'student_id' => 'TEST2024001',
    'name' => 'Test Student API',
    'password' => 'TEST2024001',
    'year' => '4th Year',
    'course' => 'BSCS',
    'section' => 'Test Section'
];
$result = testEndpoint("$baseUrl/auth/register.php", 'POST', $registerData);
echo "<pre>HTTP {$result['http_code']}: " . htmlspecialchars($result['response']) . "</pre>\n";

echo "<h2>4. Testing Student Login</h2>\n";
$loginData = [
    'student_id' => 'TEST2024001',
    'password' => 'TEST2024001'
];
$result = testEndpoint("$baseUrl/auth/login.php", 'POST', $loginData);
echo "<pre>HTTP {$result['http_code']}: " . htmlspecialchars($result['response']) . "</pre>\n";

echo "<h2>5. Testing Violations Endpoint</h2>\n";
$result = testEndpoint("$baseUrl/violations/TEST2024001");
echo "<pre>HTTP {$result['http_code']}: " . htmlspecialchars($result['response']) . "</pre>\n";

echo "<h2>6. Testing Attendance Endpoint</h2>\n";
$result = testEndpoint("$baseUrl/attendance/TEST2024001");
echo "<pre>HTTP {$result['http_code']}: " . htmlspecialchars($result['response']) . "</pre>\n";

echo "<h2>7. Testing with Existing Student (220342)</h2>\n";
$result = testEndpoint("$baseUrl/violations/220342");
echo "<pre>Violations HTTP {$result['http_code']}: " . htmlspecialchars($result['response']) . "</pre>\n";

$result = testEndpoint("$baseUrl/attendance/220342");
echo "<pre>Attendance HTTP {$result['http_code']}: " . htmlspecialchars($result['response']) . "</pre>\n";

echo "<h2>Test Complete!</h2>\n";
echo "<p>If all tests show HTTP 200 and success:true, the backend is working correctly.</p>\n";
?>