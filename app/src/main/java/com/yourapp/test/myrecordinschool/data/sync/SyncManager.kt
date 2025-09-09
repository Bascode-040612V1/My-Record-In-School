package com.yourapp.test.myrecordinschool.data.sync

import android.app.Application
import android.util.Log
import com.yourapp.test.myrecordinschool.data.api.RetrofitClient
import com.yourapp.test.myrecordinschool.data.model.*
import com.yourapp.test.myrecordinschool.data.preferences.AppPreferences
import com.yourapp.test.myrecordinschool.roomdb.AppDatabase
import com.yourapp.test.myrecordinschool.roomdb.entity.AttendanceEntity
import com.yourapp.test.myrecordinschool.roomdb.entity.ViolationEntity
import com.yourapp.test.myrecordinschool.roomdb.repository.AttendanceRepository
import com.yourapp.test.myrecordinschool.roomdb.repository.ViolationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncManager(private val application: Application) {
    
    private val appPreferences = AppPreferences(application)
    private val database = AppDatabase.getDatabase(application)
    private val violationRepository = ViolationRepository(database.violationDao())
    private val attendanceRepository = AttendanceRepository(database.attendanceDao())
    
    private val _syncStatus = MutableStateFlow(SyncStatus())
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Unknown)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()
    
    private var syncJob: Job? = null
    
    companion object {
        private const val TAG = "SyncManager"
        private const val SYNC_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
        private const val RETRY_DELAY_MS = 30 * 1000L // 30 seconds
        private const val CACHE_TIMEOUT_MS = 10 * 60 * 1000L // 10 minutes
    }
    
    suspend fun syncViolations(forceRefresh: Boolean = false): Boolean {
        val studentId = appPreferences.getStudentId() ?: return false
        
        return try {
            _syncStatus.value = _syncStatus.value.copy(syncState = SyncState.Syncing)
            
            // Smart caching - check if we need to sync
            if (!forceRefresh && violationRepository.isCacheValid(studentId)) {
                Log.d(TAG, "Using cached violations data - cache still valid")
                _syncStatus.value = _syncStatus.value.copy(
                    syncState = SyncState.Success,
                    lastSyncTime = System.currentTimeMillis()
                )
                return true
            }
            
            val config = appPreferences.getAppConfig()
            val api = RetrofitClient.getViolationApi(config.baseUrl)
            
            // Delta sync - only get data since last update
            val lastSyncTime = appPreferences.getLastViolationSync()
            val response = if (lastSyncTime > 0 && !forceRefresh) {
                Log.d(TAG, "Performing delta sync for violations since: $lastSyncTime")
                api.getStudentViolationsSince(studentId, lastSyncTime)
            } else {
                Log.d(TAG, "Performing full sync for violations")
                api.getStudentViolations(studentId)
            }
            
            if (response.isSuccessful && response.body()?.success == true) {
                val violations = response.body()?.violations ?: emptyList()
                
                Log.d(TAG, "Received ${violations.size} violations (delta sync: ${lastSyncTime > 0})")
                
                // Convert to entities with sync tracking
                val entities = violations.map { violation ->
                    ViolationEntity(
                        id = violation.id,
                        student_id = violation.student_id,
                        student_name = violation.student_name,
                        year_level = violation.year_level,
                        course = violation.course,
                        section = violation.section,
                        violation_type = violation.violation_type,
                        violation_description = violation.violation_description,
                        offense_count = violation.offense_count,
                        original_offense_count = violation.original_offense_count,
                        penalty = violation.penalty,
                        recorded_by = violation.recorded_by,
                        date_recorded = violation.date_recorded,
                        acknowledged = violation.acknowledged,
                        category = violation.category,
                        last_sync_timestamp = System.currentTimeMillis(),
                        is_synced = true,
                        local_changes = false
                    )
                }
                
                // Save to local database
                if (forceRefresh) {
                    violationRepository.clearViolationsForStudent(studentId)
                }
                violationRepository.saveViolations(entities)
                
                // Update sync timestamps
                appPreferences.setLastViolationSync(System.currentTimeMillis())
                violationRepository.updateSyncTimestamp(studentId, System.currentTimeMillis())
                
                _syncStatus.value = _syncStatus.value.copy(
                    syncState = SyncState.Success,
                    lastSyncTime = System.currentTimeMillis()
                )
                
                Log.d(TAG, "Violations synced successfully: ${entities.size} items")
                true
            } else {
                _syncStatus.value = _syncStatus.value.copy(
                    syncState = SyncState.Error("Failed to sync violations: ${response.message()}")
                )
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing violations", e)
            _syncStatus.value = _syncStatus.value.copy(
                syncState = SyncState.Error("Network error: ${e.message}")
            )
            false
        }
    }
    
    suspend fun syncAttendance(month: Int? = null, year: Int? = null, forceRefresh: Boolean = false): Boolean {
        val studentId = appPreferences.getStudentId() ?: return false
        
        return try {
            _syncStatus.value = _syncStatus.value.copy(syncState = SyncState.Syncing)
            
            // Smart caching for attendance
            if (!forceRefresh && attendanceRepository.isCacheValid(studentId)) {
                Log.d(TAG, "Using cached attendance data - cache still valid")
                _syncStatus.value = _syncStatus.value.copy(
                    syncState = SyncState.Success,
                    lastSyncTime = System.currentTimeMillis()
                )
                return true
            }
            
            val config = appPreferences.getAppConfig()
            val api = RetrofitClient.getAttendanceApi(config.baseUrl)
            
            // Delta sync for attendance
            val lastSyncTime = appPreferences.getLastAttendanceSync()
            val response = if (lastSyncTime > 0 && !forceRefresh) {
                Log.d(TAG, "Performing delta sync for attendance since: $lastSyncTime")
                api.getStudentAttendanceSince(studentId, lastSyncTime, month, year)
            } else {
                Log.d(TAG, "Performing full sync for attendance")
                api.getStudentAttendance(studentId, month, year)
            }
            
            if (response.isSuccessful && response.body()?.success == true) {
                val attendance = response.body()?.attendance ?: emptyList()
                
                Log.d(TAG, "Received ${attendance.size} attendance records (delta sync: ${lastSyncTime > 0})")
                
                // Convert to entities
                val entities = attendance.map { att ->
                    AttendanceEntity(
                        id = att.id,
                        student_id = att.student_id,
                        student_name = att.student_name,
                        student_number = att.student_number,
                        date = att.date,
                        time_in = att.time_in,
                        time_out = att.time_out,
                        status = att.status,
                        attendance_type = att.attendance_type,
                        created_at = att.created_at
                    )
                }
                
                // Clear old data for the specific month if provided
                if (month != null && year != null && forceRefresh) {
                    val yearMonth = String.format("%04d-%02d", year, month)
                    attendanceRepository.clearAttendanceForMonth(studentId, yearMonth)
                } else if (forceRefresh) {
                    attendanceRepository.clearAttendanceForStudent(studentId)
                }
                
                attendanceRepository.saveAttendance(entities)
                
                // Update sync timestamp
                appPreferences.setLastAttendanceSync(System.currentTimeMillis())
                
                _syncStatus.value = _syncStatus.value.copy(
                    syncState = SyncState.Success,
                    lastSyncTime = System.currentTimeMillis()
                )
                
                Log.d(TAG, "Attendance synced successfully: ${entities.size} items")
                true
            } else {
                _syncStatus.value = _syncStatus.value.copy(
                    syncState = SyncState.Error("Failed to sync attendance: ${response.message()}")
                )
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing attendance", e)
            _syncStatus.value = _syncStatus.value.copy(
                syncState = SyncState.Error("Network error: ${e.message}")
            )
            false
        }
    }
    
    suspend fun syncAcknowledgment(violationId: Int): Boolean {
        return try {
            val config = appPreferences.getAppConfig()
            val api = RetrofitClient.getViolationApi(config.baseUrl)
            val response = api.acknowledgeViolation(violationId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                // Update local database
                violationRepository.updateAcknowledgment(violationId, 1)
                Log.d(TAG, "Acknowledgment synced successfully for violation: $violationId")
                true
            } else {
                Log.e(TAG, "Failed to sync acknowledgment: ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing acknowledgment", e)
            false
        }
    }
    
    suspend fun performFullSync(forceRefresh: Boolean = false): Boolean {
        _syncStatus.value = _syncStatus.value.copy(syncState = SyncState.Syncing)
        
        val violationsSynced = syncViolations(forceRefresh)
        val attendanceSynced = syncAttendance(forceRefresh = forceRefresh)
        
        val success = violationsSynced && attendanceSynced
        
        _syncStatus.value = _syncStatus.value.copy(
            syncState = if (success) SyncState.Success else SyncState.Error("Partial sync failure"),
            lastSyncTime = if (success) System.currentTimeMillis() else _syncStatus.value.lastSyncTime
        )
        
        return success
    }
    
    fun startPeriodicSync() {
        syncJob?.cancel()
        syncJob = CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            while (isActive) {
                delay(SYNC_INTERVAL_MS)
                if (_networkState.value == NetworkState.Available) {
                    try {
                        // Smart sync - only sync if cache is stale
                        performSmartSync()
                    } catch (e: Exception) {
                        Log.e(TAG, "Periodic sync failed", e)
                    }
                }
            }
        }
    }

    private suspend fun performSmartSync(): Boolean {
        val studentId = appPreferences.getStudentId() ?: return false
        
        val needsViolationSync = !violationRepository.isCacheValid(studentId)
        val needsAttendanceSync = !attendanceRepository.isCacheValid(studentId)
        
        var success = true
        
        if (needsViolationSync) {
            Log.d(TAG, "Cache stale, syncing violations...")
            success = syncViolations() && success
        }
        
        if (needsAttendanceSync) {
            Log.d(TAG, "Cache stale, syncing attendance...")
            success = syncAttendance() && success
        }
        
        if (!needsViolationSync && !needsAttendanceSync) {
            Log.d(TAG, "Cache still valid, skipping sync")
        }
        
        return success
    }

    fun stopPeriodicSync() {
        syncJob?.cancel()
        syncJob = null
    }
    
    fun updateNetworkState(isAvailable: Boolean) {
        _networkState.value = if (isAvailable) NetworkState.Available else NetworkState.Unavailable
    }
    
    fun resetSyncState() {
        _syncStatus.value = SyncStatus()
    }
}