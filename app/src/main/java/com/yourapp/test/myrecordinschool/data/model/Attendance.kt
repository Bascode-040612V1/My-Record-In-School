package com.yourapp.test.myrecordinschool.data.model

data class Attendance(
    val id: Int = 0,
    val student_id: String = "",
    val student_name: String = "",
    val student_number: String = "",
    val date: String = "",
    val time_in: String? = null,
    val time_out: String? = null,
    val status: String = "ABSENT", // PRESENT, ABSENT, LATE, EARLY, VERY_LATE
    val attendance_type: String = "regular",
    val created_at: String = ""
)

data class AttendanceResponse(
    val success: Boolean,
    val message: String,
    val attendance: List<Attendance> = emptyList()
)

data class AttendanceCalendarDay(
    val day: Int,
    val isCurrentMonth: Boolean,
    val attendance: Attendance? = null,
    val isToday: Boolean = false
)

data class AttendanceMonth(
    val month: Int,
    val year: Int,
    val days: List<AttendanceCalendarDay>
)
