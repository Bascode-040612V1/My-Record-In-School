package com.yourapp.test.myrecordinschool.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yourapp.test.myrecordinschool.roomdb.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(records: List<AttendanceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleAttendance(record: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE student_id = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND date LIKE :yearMonth || '%' ORDER BY date DESC")
    fun getAttendanceByStudentAndMonth(studentId: String, yearMonth: String): Flow<List<AttendanceEntity>>

    @Query("DELETE FROM attendance WHERE student_id = :studentId")
    suspend fun deleteAttendanceByStudent(studentId: String)

    @Query("SELECT COUNT(*) FROM attendance WHERE student_id = :studentId AND status = 'PRESENT'")
    suspend fun getPresentCount(studentId: String): Int

    @Query("SELECT COUNT(*) FROM attendance WHERE student_id = :studentId AND status = 'ABSENT'")
    suspend fun getAbsentCount(studentId: String): Int

    @Query("SELECT COUNT(*) FROM attendance WHERE student_id = :studentId AND status = 'LATE'")
    suspend fun getLateCount(studentId: String): Int

    @Query("DELETE FROM attendance WHERE student_id = :studentId AND date LIKE :yearMonth || '%'")
    suspend fun deleteAttendanceByMonth(studentId: String, yearMonth: String)
    
    // Optimized queries for delta sync and caching
    @Query("SELECT * FROM attendance WHERE student_id = :studentId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentAttendance(studentId: String, limit: Int): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND date > :since ORDER BY date DESC")
    suspend fun getAttendanceSince(studentId: String, since: String): List<AttendanceEntity>

    @Query("SELECT MAX(date) FROM attendance WHERE student_id = :studentId")
    suspend fun getLastUpdateTimestamp(studentId: String): Long?

    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getAttendanceByDateRange(studentId: String, startDate: String, endDate: String): List<AttendanceEntity>

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>
    
    // Pagination support for large datasets
    @Query("SELECT * FROM attendance WHERE student_id = :studentId ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getAttendancePaginated(studentId: String, limit: Int, offset: Int): List<AttendanceEntity>
    
    @Query("SELECT * FROM attendance ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllAttendancePaginated(limit: Int, offset: Int): List<AttendanceEntity>
    
    // Get total count for pagination
    @Query("SELECT COUNT(*) FROM attendance WHERE student_id = :studentId")
    suspend fun getTotalAttendanceCount(studentId: String): Int
    
    // Data cleanup for optimization
    @Query("DELETE FROM attendance WHERE student_id = :studentId AND date < :cutoffDate")
    suspend fun deleteOldAttendance(studentId: String, cutoffDate: String)
    
    // Monthly pagination
    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND date LIKE :yearMonth || '%' ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getMonthlyAttendancePaginated(studentId: String, yearMonth: String, limit: Int, offset: Int): List<AttendanceEntity>
}