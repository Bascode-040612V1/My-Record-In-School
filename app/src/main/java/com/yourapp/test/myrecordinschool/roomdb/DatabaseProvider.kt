package com.yourapp.test.myrecordinschool.roomdb

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "school_records_db"
            )
                .fallbackToDestructiveMigration() // reset DB if schema mismatch
                .build()
            INSTANCE = instance
            instance
        }
    }
}
    