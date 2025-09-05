package com.yourapp.test.myrecordinschool.data.api

import com.yourapp.test.myrecordinschool.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface StudentApi {
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @PUT("student/update.php")
    suspend fun updateStudent(@Body request: UpdateStudentRequest): Response<AuthResponse>
    
    @GET("student/{student_id}")
    suspend fun getStudent(@Path("student_id") studentId: String): Response<AuthResponse>
}

interface ViolationApi {
    @GET("violations/index.php/{student_id}")
    suspend fun getStudentViolations(@Path("student_id") studentId: String): Response<ViolationResponse>
    
    @PUT("violations/acknowledge.php/{id}")
    suspend fun acknowledgeViolation(@Path("id") violationId: Int): Response<ViolationResponse>
}

interface AttendanceApi {
    @GET("attendance/index.php/{student_number}")
    suspend fun getStudentAttendance(
         @Path("student_number") studentNumber: String,
        @Query("month") month: Int? = null,
        @Query("year") year: Int? = null
    ): Response<AttendanceResponse>
}

interface ConnectionApi {
    @GET("test_connection.php")
    suspend fun testConnection(): Response<ConnectionTestResponse>
}

interface RfidApi {
    @GET("rfid/get_latest.php")
    suspend fun getLatestRfid(): Response<RfidResponse>
}

data class RfidResponse(
    val success: Boolean,
    val message: String,
    val rfid_number: String? = null,
    val scanned_at: String? = null
)