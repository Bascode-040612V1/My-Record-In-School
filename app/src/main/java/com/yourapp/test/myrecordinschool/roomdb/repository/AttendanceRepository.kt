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
    
    // Pagination support for large datasets
    suspend fun getAttendancePaginated(studentId: String, page: Int, pageSize: Int = 30): List<AttendanceEntity> {
        val offset = page * pageSize
        return attendanceDao.getAttendancePaginated(studentId, pageSize, offset)
    }
    
    suspend fun getAllAttendancePaginated(page: Int, pageSize: Int = 30): List<AttendanceEntity> {
        val offset = page * pageSize
        return attendanceDao.getAllAttendancePaginated(pageSize, offset)
    }
    
    suspend fun getMonthlyAttendancePaginated(studentId: String, yearMonth: String, page: Int, pageSize: Int = 31): List<AttendanceEntity> {
        val offset = page * pageSize
        return attendanceDao.getMonthlyAttendancePaginated(studentId, yearMonth, pageSize, offset)
    }
    
    suspend fun getTotalAttendanceCount(studentId: String): Int {
        return attendanceDao.getTotalAttendanceCount(studentId)
    }
    
    suspend fun getTotalPages(studentId: String, pageSize: Int = 30): Int {
        val totalCount = getTotalAttendanceCount(studentId)
        return (totalCount + pageSize - 1) / pageSize // Ceiling division
    }
    
    // Data cleanup for optimization
    suspend fun cleanupOldAttendance(studentId: String, daysToKeep: Int = 180) {
        val cutoffDate = java.time.LocalDate.now().minusDays(daysToKeep.toLong()).toString()
        attendanceDao.deleteOldAttendance(studentId, cutoffDate)
    }
}