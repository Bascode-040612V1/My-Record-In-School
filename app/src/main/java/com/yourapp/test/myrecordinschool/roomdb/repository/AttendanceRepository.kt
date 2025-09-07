package com.yourapp.test.myrecordinschool.roomdb.repository

import com.yourapp.test.myrecordinschool.roomdb.dao.AttendanceDao
import com.yourapp.test.myrecordinschool.roomdb.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(private val attendanceDao: AttendanceDao) {

    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudent(studentId)
    }

    fun getAttendanceByStudentAndMonth(studentId: String, yearMonth: String): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudentAndMonth(studentId, yearMonth)
    }

    suspend fun saveAttendance(attendance: List<AttendanceEntity>) {
        attendanceDao.insertAttendance(attendance)
    }

    suspend fun saveSingleAttendance(attendance: AttendanceEntity) {
        attendanceDao.insertSingleAttendance(attendance)
    }

    suspend fun clearAttendanceForStudent(studentId: String) {
        attendanceDao.deleteAttendanceByStudent(studentId)
    }

    suspend fun clearAttendanceForMonth(studentId: String, yearMonth: String) {
        attendanceDao.deleteAttendanceByMonth(studentId, yearMonth)
    }

    suspend fun getAttendanceStats(studentId: String): AttendanceStats {
        return AttendanceStats(
            presentCount = attendanceDao.getPresentCount(studentId),
            absentCount = attendanceDao.getAbsentCount(studentId),
            lateCount = attendanceDao.getLateCount(studentId)
        )
    }
}

data class AttendanceStats(
    val presentCount: Int,
    val absentCount: Int,
    val lateCount: Int
) {
    val totalDays: Int get() = presentCount + absentCount + lateCount
    val attendanceRate: Float get() = if (totalDays > 0) presentCount.toFloat() / totalDays else 0f
}