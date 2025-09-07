package com.yourapp.test.myrecordinschool.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yourapp.test.myrecordinschool.roomdb.entity.ViolationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ViolationDao {

    @Query("SELECT * FROM violations WHERE student_id = :studentId ORDER BY date_recorded DESC")
    fun getViolationsByStudent(studentId: String): Flow<List<ViolationEntity>>

    @Query("SELECT * FROM violations ORDER BY date_recorded DESC")
    fun getAllViolations(): Flow<List<ViolationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolations(violations: List<ViolationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolation(violation: ViolationEntity)

    @Update
    suspend fun updateViolation(violation: ViolationEntity)

    @Query("UPDATE violations SET acknowledged = :acknowledged WHERE id = :violationId")
    suspend fun updateAcknowledgment(violationId: Int, acknowledged: Int)

    @Query("DELETE FROM violations WHERE student_id = :studentId")
    suspend fun deleteViolationsByStudent(studentId: String)

    @Query("SELECT COUNT(*) FROM violations WHERE student_id = :studentId")
    suspend fun getViolationCount(studentId: String): Int

    @Query("SELECT * FROM violations WHERE student_id = :studentId AND acknowledged = 0")
    fun getUnacknowledgedViolations(studentId: String): Flow<List<ViolationEntity>>
}
