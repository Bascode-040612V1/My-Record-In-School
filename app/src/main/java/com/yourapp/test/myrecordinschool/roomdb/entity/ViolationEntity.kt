package com.yourapp.test.myrecordinschool.roomdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "violations")
data class ViolationEntity(
    @PrimaryKey val id: Int,  // from backend
    val student_id: String,
    val student_name: String,
    val year_level: String,
    val course: String,
    val section: String,
    val violation_type: String,
    val violation_description: String,
    val offense_count: Int,
    val original_offense_count: Int,
    val penalty: String,
    val recorded_by: String,
    val date_recorded: String,
    val acknowledged: Int,
    val category: String
)
