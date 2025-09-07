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
}
