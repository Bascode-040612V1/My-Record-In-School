package com.yourapp.test.myrecordinschool.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yourapp.test.myrecordinschool.roomdb.dao.AttendanceDao
import com.yourapp.test.myrecordinschool.roomdb.dao.ViolationDao
import com.yourapp.test.myrecordinschool.roomdb.entity.AttendanceEntity
import com.yourapp.test.myrecordinschool.roomdb.entity.ViolationEntity

@Database(
    entities = [ViolationEntity::class, AttendanceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun violationDao(): ViolationDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "school_db"   // ✅ database file name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
