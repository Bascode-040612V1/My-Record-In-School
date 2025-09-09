package com.yourapp.test.myrecordinschool.roomdb.repository

import com.yourapp.test.myrecordinschool.roomdb.dao.AttendanceDao
import com.yourapp.test.myrecordinschool.roomdb.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(private val attendanceDao: AttendanceDao) {

    // Cache timeout: 10 minutes (following memory specification)
    private val cacheTimeout = 10 * 60 * 1000L

    fun getAllAttendance(): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAllAttendance()
    }

    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudent(studentId)
    }

    fun getAttendanceByStudentAndMonth(studentId: String, yearMonth: String): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudentAndMonth(studentId, yearMonth)
    }

    suspend fun saveAttendance(attendance: List<AttendanceEntity>) {
        attendanceDao.insertAttendance(attendance)
    }

    suspend fun saveAttendance(attendance: AttendanceEntity) {
        attendanceDao.insertSingleAttendance(attendance)
    }

    suspend fun clearAttendanceForStudent(studentId: String) {
        attendanceDao.deleteAttendanceByStudent(studentId)
    }

    suspend fun clearAttendanceForMonth(studentId: String, yearMonth: String) {
        attendanceDao.deleteAttendanceByMonth(studentId, yearMonth)
    }

    suspend fun getPresentCount(studentId: String): Int {
        return attendanceDao.getPresentCount(studentId)
    }

    suspend fun getAbsentCount(studentId: String): Int {
        return attendanceDao.getAbsentCount(studentId)
    }

    suspend fun getLateCount(studentId: String): Int {
        return attendanceDao.getLateCount(studentId)
    }

    // Optimized methods for delta sync and caching
    suspend fun getRecentAttendance(studentId: String, limit: Int = 30): List<AttendanceEntity> {
        return attendanceDao.getRecentAttendance(studentId, limit)
    }

    suspend fun getAttendanceSince(studentId: String, since: String): List<AttendanceEntity> {
        return attendanceDao.getAttendanceSince(studentId, since)
    }

    suspend fun getLastUpdateTimestamp(studentId: String): Long {
        return attendanceDao.getLastUpdateTimestamp(studentId) ?: 0L
    }

    suspend fun isCacheValid(studentId: String): Boolean {
        val lastUpdate = getLastUpdateTimestamp(studentId)
        return (System.currentTimeMillis() - lastUpdate) < cacheTimeout
    }

    suspend fun getAttendanceByDateRange(studentId: String, startDate: String, endDate: String): List<AttendanceEntity> {
        return attendanceDao.getAttendanceByDateRange(studentId, startDate, endDate)
    }

    suspend fun getAttendanceStats(studentId: String): AttendanceStats {
        val presentCount = attendanceDao.getPresentCount(studentId)
        val absentCount = attendanceDao.getAbsentCount(studentId)
        val lateCount = attendanceDao.getLateCount(studentId)
        return AttendanceStats.calculate(presentCount, absentCount, lateCount)
    }
}