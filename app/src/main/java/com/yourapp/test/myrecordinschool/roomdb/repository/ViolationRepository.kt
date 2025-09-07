package com.yourapp.test.myrecordinschool.roomdb.repository

import com.yourapp.test.myrecordinschool.roomdb.dao.ViolationDao
import com.yourapp.test.myrecordinschool.roomdb.entity.ViolationEntity
import kotlinx.coroutines.flow.Flow

class ViolationRepository(private val violationDao: ViolationDao) {

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
}
