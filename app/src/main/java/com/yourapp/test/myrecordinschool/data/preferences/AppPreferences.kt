package com.yourapp.test.myrecordinschool.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.yourapp.test.myrecordinschool.data.model.AppConfig
import com.yourapp.test.myrecordinschool.data.model.Student

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "MyRecordInSchoolPrefs"
        private const val KEY_IP_ADDRESS = "ip_address"
        private const val KEY_PORT = "port"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_STUDENT_ID = "student_id"
        private const val KEY_STUDENT_NAME = "student_name"
        private const val KEY_STUDENT_YEAR = "student_year"
        private const val KEY_STUDENT_COURSE = "student_course"
        private const val KEY_STUDENT_SECTION = "student_section"
        private const val KEY_FIRST_TIME = "first_time"
        
        // Sync tracking keys for optimization
        private const val KEY_LAST_VIOLATION_SYNC = "last_violation_sync"
        private const val KEY_LAST_ATTENDANCE_SYNC = "last_attendance_sync"
        private const val KEY_CACHE_TIMEOUT = "cache_timeout"
        
        // App usage and sync optimization keys
        private const val KEY_SYNC_PAGE_SIZE = "sync_page_size"
        private const val KEY_AUTO_CLEANUP_ENABLED = "auto_cleanup_enabled"
        private const val KEY_LAST_CLEANUP_TIME = "last_cleanup_time"
    }
    
    // App Configuration
    fun saveAppConfig(config: AppConfig) {
        sharedPreferences.edit().apply {
            putString(KEY_IP_ADDRESS, config.ipAddress)
            putString(KEY_PORT, config.port)
            apply()
        }
    }
    
    fun getAppConfig(): AppConfig {
        return AppConfig(
            ipAddress = sharedPreferences.getString(KEY_IP_ADDRESS, "localhost") ?: "localhost",
            port = sharedPreferences.getString(KEY_PORT, "80") ?: "80"
        )
    }
    
    // Login State
    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    // Student Information
    fun saveStudent(student: Student) {
        sharedPreferences.edit().apply {
            putString(KEY_STUDENT_ID, student.student_id)
            putString(KEY_STUDENT_NAME, student.name)
            putString(KEY_STUDENT_YEAR, student.year)
            putString(KEY_STUDENT_COURSE, student.course)
            putString(KEY_STUDENT_SECTION, student.section)
            apply()
        }
    }
    
    fun getStudent(): Student? {
        val studentId = sharedPreferences.getString(KEY_STUDENT_ID, null) ?: return null
        return Student(
            student_id = studentId,
            name = sharedPreferences.getString(KEY_STUDENT_NAME, "") ?: "",
            year = sharedPreferences.getString(KEY_STUDENT_YEAR, "") ?: "",
            course = sharedPreferences.getString(KEY_STUDENT_COURSE, "") ?: "",
            section = sharedPreferences.getString(KEY_STUDENT_SECTION, "") ?: ""
        )
    }
    
    fun getStudentId(): String? {
        return sharedPreferences.getString(KEY_STUDENT_ID, null)
    }
    
    fun updateStudentInfo(year: String, course: String, section: String) {
        sharedPreferences.edit().apply {
            putString(KEY_STUDENT_YEAR, year)
            putString(KEY_STUDENT_COURSE, course)
            putString(KEY_STUDENT_SECTION, section)
            apply()
        }
    }
    
    // First Time Setup
    fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME, true)
    }
    
    fun setFirstTimeComplete() {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME, false).apply()
    }
    
    // Clear all data (logout)
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
        // Restore default config
        saveAppConfig(AppConfig())
    }
    
    fun logout() {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_STUDENT_ID)
            remove(KEY_STUDENT_NAME)
            remove(KEY_STUDENT_YEAR)
            remove(KEY_STUDENT_COURSE)
            remove(KEY_STUDENT_SECTION)
            // Clear sync data on logout
            remove(KEY_LAST_VIOLATION_SYNC)
            remove(KEY_LAST_ATTENDANCE_SYNC)
            apply()
        }
    }

    // Sync tracking methods for optimization
    fun setLastViolationSync(timestamp: Long) {
        sharedPreferences.edit()
            .putLong(KEY_LAST_VIOLATION_SYNC, timestamp)
            .apply()
    }

    fun getLastViolationSync(): Long {
        return sharedPreferences.getLong(KEY_LAST_VIOLATION_SYNC, 0L)
    }

    fun setLastAttendanceSync(timestamp: Long) {
        sharedPreferences.edit()
            .putLong(KEY_LAST_ATTENDANCE_SYNC, timestamp)
            .apply()
    }

    fun getLastAttendanceSync(): Long {
        return sharedPreferences.getLong(KEY_LAST_ATTENDANCE_SYNC, 0L)
    }

    fun setCacheTimeout(timeoutMs: Long) {
        sharedPreferences.edit()
            .putLong(KEY_CACHE_TIMEOUT, timeoutMs)
            .apply()
    }

    fun getCacheTimeout(): Long {
        return sharedPreferences.getLong(KEY_CACHE_TIMEOUT, 10 * 60 * 1000L) // Default 10 minutes
    }

    fun clearSyncData() {
        sharedPreferences.edit()
            .remove(KEY_LAST_VIOLATION_SYNC)
            .remove(KEY_LAST_ATTENDANCE_SYNC)
            .apply()
    }
    
    // App usage and sync optimization methods
    fun setSyncPageSize(pageSize: Int) {
        sharedPreferences.edit()
            .putInt(KEY_SYNC_PAGE_SIZE, pageSize)
            .apply()
    }
    
    fun getSyncPageSize(): Int {
        return sharedPreferences.getInt(KEY_SYNC_PAGE_SIZE, 50) // Default 50 items per page
    }
    
    fun setAutoCleanupEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_AUTO_CLEANUP_ENABLED, enabled)
            .apply()
    }
    
    fun isAutoCleanupEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTO_CLEANUP_ENABLED, true) // Default enabled
    }
    
    fun setLastCleanupTime(timestamp: Long) {
        sharedPreferences.edit()
            .putLong(KEY_LAST_CLEANUP_TIME, timestamp)
            .apply()
    }
    
    fun getLastCleanupTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_CLEANUP_TIME, 0L)
    }
    
    fun shouldPerformCleanup(): Boolean {
        val lastCleanup = getLastCleanupTime()
        val cleanupInterval = 7 * 24 * 60 * 60 * 1000L // 7 days
        return (System.currentTimeMillis() - lastCleanup) > cleanupInterval
    }
}