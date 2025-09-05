package com.yourapp.test.myrecordinschool.roomdb.repository

import com.yourapp.test.myrecordinschool.roomdb.dao.ViolationDao
import com.yourapp.test.myrecordinschool.roomdb.entity.ViolationEntity
import kotlinx.coroutines.flow.Flow

class ViolationRepository(private val violationDao: ViolationDao) {

    fun getViolations(): Flow<List<ViolationEntity>> {
        return violationDao.getAllViolations()
    }

    suspend fun saveViolations(violations: List<ViolationEntity>) {
        violationDao.insertViolations(violations)
    }
}
