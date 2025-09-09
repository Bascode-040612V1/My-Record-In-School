package com.yourapp.test.myrecordinschool.roomdb.repository

data class AttendanceStats(
    val presentCount: Int,
    val absentCount: Int,
    val lateCount: Int,
    val totalDays: Int,
    val attendanceRate: Double
) {
    companion object {
        fun calculate(presentCount: Int, absentCount: Int, lateCount: Int): AttendanceStats {
            val totalDays = presentCount + absentCount + lateCount
            val attendanceRate = if (totalDays > 0) {
                ((presentCount + lateCount).toDouble() / totalDays) * 100
            } else {
                0.0
            }
            
            return AttendanceStats(
                presentCount = presentCount,
                absentCount = absentCount,
                lateCount = lateCount,
                totalDays = totalDays,
                attendanceRate = attendanceRate
            )
        }
    }
}