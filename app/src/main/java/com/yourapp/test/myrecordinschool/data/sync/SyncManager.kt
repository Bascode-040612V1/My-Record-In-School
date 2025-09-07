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
    }
    
    suspend fun syncViolations(forceRefresh: Boolean = false): Boolean {
        val studentId = appPreferences.getStudentId() ?: return false
        
        return try {
            _syncStatus.value = _syncStatus.value.copy(syncState = SyncState.Syncing)
            
            val config = appPreferences.getAppConfig()
            val api = RetrofitClient.getViolationApi(config.baseUrl)
            val response = api.getStudentViolations(studentId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val violations = response.body()?.violations ?: emptyList()
                
                // Convert to entities
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
                        category = violation.category
                    )
                }
                
                // Save to local database
                if (forceRefresh) {
                    violationRepository.clearViolationsForStudent(studentId)
                }
                violationRepository.saveViolations(entities)
                
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
            
            val config = appPreferences.getAppConfig()
            val api = RetrofitClient.getAttendanceApi(config.baseUrl)
            val response = api.getStudentAttendance(studentId, month, year)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val attendance = response.body()?.attendance ?: emptyList()
                
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
                        performFullSync()
                    } catch (e: Exception) {
                        Log.e(TAG, "Periodic sync failed", e)
                    }
                }
            }
        }
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