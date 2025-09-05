package com.yourapp.test.myrecordinschool.data.model

data class Student(
    val id: Int = 0,
    val student_id: String = "",
    val name: String = "",
    val password: String = "",
    val year: String = "",
    val course: String = "",
    val section: String = "",
    val created_at: String = "",
    val updated_at: String = ""
)

data class LoginRequest(
    val student_id: String,
    val password: String
)

data class RegisterRequest(
    val student_id: String,
    val name: String,
    val password: String,
    val year: String,
    val course: String,
    val section: String,
    val rfid: String = ""
)

data class UpdateStudentRequest(
    val student_id: String,
    val year: String,
    val course: String,
    val section: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val student: Student? = null
)