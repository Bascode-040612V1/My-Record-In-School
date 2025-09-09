package com.yourapp.test.myrecordinschool.roomdb.repository

import com.yourapp.test.myrecordinschool.roomdb.dao.ViolationDao
import com.yourapp.test.myrecordinschool.roomdb.entity.ViolationEntity
import kotlinx.coroutines.flow.Flow

class ViolationRepository(private val violationDao: ViolationDao) {

    // Cache timeout: 10 minutes (following memory specification)
    private val cacheTimeout = 10 * 60 * 1000L

    fun getViolations(): Flow<List<ViolationEntity>> {
        return violationDao.getAllViolations()
    }

    fun getViolationsByStudent(studentId: String): Flow<List<ViolationEntity>> {
        return violationDao.getViolationsByStudent(studentId)
    }

    fun getUnacknowledgedViolations(studentId: String): Flow<List<ViolationEntity>> {
        return violationDao.getUnacknowledgedViolations(studentId)
    }

    suspend fun saveViolations(violations: List<ViolationEntity>) {
        violationDao.insertViolations(violations)
    }

    suspend fun saveViolation(violation: ViolationEntity) {
        violationDao.insertViolation(violation)
    }

    suspend fun updateAcknowledgment(violationId: Int, acknowledged: Int) {
        violationDao.updateAcknowledgment(violationId, acknowledged)
    }

    suspend fun updateViolation(violation: ViolationEntity) {
        violationDao.updateViolation(violation)
    }

    suspend fun clearViolationsForStudent(studentId: String) {
        violationDao.deleteViolationsByStudent(studentId)
    }

    suspend fun getViolationCount(studentId: String): Int {
        return violationDao.getViolationCount(studentId)
    }

    // Optimized methods for delta sync and caching
    suspend fun getRecentViolations(studentId: String, limit: Int = 20): List<ViolationEntity> {
        return violationDao.getRecentViolations(studentId, limit)
    }

    suspend fun getViolationsSince(studentId: String, timestamp: Long): List<ViolationEntity> {
        return violationDao.getViolationsSince(studentId, timestamp)
    }

    suspend fun getLastUpdateTimestamp(studentId: String): Long {
        return violationDao.getLastUpdateTimestamp(studentId) ?: 0L
    }

    suspend fun isCacheValid(studentId: String): Boolean {
        val lastUpdate = getLastUpdateTimestamp(studentId)
        return (System.currentTimeMillis() - lastUpdate) < cacheTimeout
    }

    suspend fun updateSyncTimestamp(studentId: String, timestamp: Long) {
        violationDao.updateSyncTimestamp(studentId, timestamp)
    }

    suspend fun getViolationsByDateRange(studentId: String, startDate: Long, endDate: Long): List<ViolationEntity> {
        return violationDao.getViolationsByDateRange(studentId, startDate, endDate)
    }

    suspend fun getNewUnacknowledgedCount(studentId: String, since: Long): Int {
        return violationDao.getNewUnacknowledgedCount(studentId, since)
    }

    suspend fun getPendingSyncViolations(studentId: String): List<ViolationEntity> {
        return violationDao.getPendingSyncViolations(studentId)
    }
    
    // Pagination support for large datasets
    suspend fun getViolationsPaginated(studentId: String, page: Int, pageSize: Int = 20): List<ViolationEntity> {
        val offset = page * pageSize
        return violationDao.getViolationsPaginated(studentId, pageSize, offset)
    }
    
    suspend fun getAllViolationsPaginated(page: Int, pageSize: Int = 20): List<ViolationEntity> {
        val offset = page * pageSize
        return violationDao.getAllViolationsPaginated(pageSize, offset)
    }
    
    suspend fun getTotalViolationCount(studentId: String): Int {
        return violationDao.getTotalViolationCount(studentId)
    }
    
    suspend fun getTotalPages(studentId: String, pageSize: Int = 20): Int {
        val totalCount = getTotalViolationCount(studentId)
        return (totalCount + pageSize - 1) / pageSize // Ceiling division
    }
    
    // Data cleanup for optimization
    suspend fun cleanupOldViolations(studentId: String, daysToKeep: Int = 90) {
        val cutoffTimestamp = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        violationDao.deleteOldViolations(studentId, cutoffTimestamp)
    }
}
