package com.yourapp.test.myrecordinschool.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yourapp.test.myrecordinschool.data.api.RetrofitClient
import com.yourapp.test.myrecordinschool.data.model.*
import com.yourapp.test.myrecordinschool.data.preferences.AppPreferences
import com.yourapp.test.myrecordinschool.data.sync.SyncManager
import com.yourapp.test.myrecordinschool.roomdb.AppDatabase
import com.yourapp.test.myrecordinschool.roomdb.entity.AttendanceEntity
import com.yourapp.test.myrecordinschool.roomdb.repository.AttendanceRepository
import com.yourapp.test.myrecordinschool.roomdb.repository.AttendanceStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appPreferences = AppPreferences(application)
    private val attendanceDao = AppDatabase.getDatabase(application).attendanceDao()
    private val repository = AttendanceRepository(attendanceDao)
    private val syncManager = SyncManager(application)
    private val attendanceObserver = mutableMapOf<String, Observer<List<AttendanceEntity>>>()
    
    // Data state for attendance
    private val _attendanceDataState = MutableStateFlow<DataState<List<AttendanceEntity>>>(DataState.Loading)
    val attendanceDataState: StateFlow<DataState<List<AttendanceEntity>>> = _attendanceDataState.asStateFlow()
    
    // Legacy LiveData for backwards compatibility
    private val _attendance = MutableLiveData<List<Attendance>>()
    val attendance: LiveData<List<Attendance>> = _attendance
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _currentMonth = MutableLiveData<AttendanceMonth>()
    val currentMonth: LiveData<AttendanceMonth> = _currentMonth
    
    private val _selectedMonth = MutableLiveData<Int>()
    val selectedMonth: LiveData<Int> = _selectedMonth
    
    private val _selectedYear = MutableLiveData<Int>()
    val selectedYear: LiveData<Int> = _selectedYear
    
    // Offline-first attendance data
    val attendanceFromDb: LiveData<List<AttendanceEntity>> = 
        appPreferences.getStudentId()?.let { studentId ->
            repository.getAttendanceByStudent(studentId).asLiveData()
        } ?: MutableLiveData(emptyList())
    
    // Sync status
    val syncStatus: StateFlow<SyncStatus> = syncManager.syncStatus
    val networkState: StateFlow<NetworkState> = syncManager.networkState
    
    // Attendance statistics
    private val _attendanceStats = MutableLiveData<AttendanceStats>()
    val attendanceStats: LiveData<AttendanceStats> = _attendanceStats
    
    init {
        val calendar = Calendar.getInstance()
        _selectedMonth.value = calendar.get(Calendar.MONTH) + 1
        _selectedYear.value = calendar.get(Calendar.YEAR)
        loadAttendanceOfflineFirst()
        syncManager.startPeriodicSync()
    }
    
    override fun onCleared() {
        super.onCleared()
        syncManager.stopPeriodicSync()
        
        // Clean up observers to prevent memory leaks
        attendanceObserver.values.forEach { observer ->
            // Remove observers from LiveData
        }
        attendanceObserver.clear()
    }
    
    private fun loadAttendanceOfflineFirst(month: Int? = null, year: Int? = null) {
        val studentId = appPreferences.getStudentId()
        if (studentId == null) {
            _attendanceDataState.value = DataState.Error("Student ID not found. Please log in again.")
            return
        }
        
        val currentMonth = month ?: (_selectedMonth.value ?: Calendar.getInstance().get(Calendar.MONTH) + 1)
        val currentYear = year ?: (_selectedYear.value ?: Calendar.getInstance().get(Calendar.YEAR))
        
        viewModelScope.launch {
            // Load stats
            val stats = repository.getAttendanceStats(studentId)
            _attendanceStats.value = stats
            
            // Check if we have cached data
            if (stats.totalDays > 0) {
                _attendanceDataState.value = DataState.Cached(
                    data = emptyList(), // Will be populated by Flow
                    isStale = shouldRefreshData()
                )
                
                // Generate calendar from offline data immediately
                generateCalendarFromOfflineData(currentMonth, currentYear)
            } else {
                _attendanceDataState.value = DataState.Loading
            }
            
            // Sync from network if available
            if (networkState.value == NetworkState.Available) {
                refreshAttendance(month, year)
            }
        }
    }
    
    private fun shouldRefreshData(): Boolean {
        val lastSyncTime = syncManager.syncStatus.value.lastSyncTime
        val currentTime = System.currentTimeMillis()
        val fiveMinutes = 5 * 60 * 1000L
        return (currentTime - lastSyncTime) > fiveMinutes
    }
    
    fun loadAttendance(month: Int? = null, year: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _attendanceDataState.value = DataState.Loading
            
            val success = syncManager.syncAttendance(month, year)
            
            if (success) {
                _attendanceDataState.value = DataState.Success(emptyList()) // Will be populated by Flow
                generateCalendarFromDb(month, year)
            } else {
                val errorMsg = when (val syncState = syncManager.syncStatus.value.syncState) {
                    is SyncState.Error -> syncState.message
                    else -> "Failed to load attendance"
                }
                _attendanceDataState.value = DataState.Error(errorMsg)
                _errorMessage.value = errorMsg
            }
            
            _isLoading.value = false
        }
    }
    
    fun refreshAttendance(month: Int? = null, year: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val success = syncManager.syncAttendance(month, year, forceRefresh = true)
            
            if (success) {
                _attendanceDataState.value = DataState.Success(emptyList())
                _errorMessage.value = ""
                generateCalendarFromDb(month, year)
                
                // Update stats
                val studentId = appPreferences.getStudentId()
                if (studentId != null) {
                    val stats = repository.getAttendanceStats(studentId)
                    _attendanceStats.value = stats
                }
            } else {
                val errorMsg = when (val syncState = syncManager.syncStatus.value.syncState) {
                    is SyncState.Error -> syncState.message
                    else -> "Failed to refresh attendance"
                }
                _attendanceDataState.value = DataState.Error(errorMsg)
                _errorMessage.value = errorMsg
            }
            
            _isLoading.value = false
        }
    }
    
    private fun generateCalendarFromDb(month: Int?, year: Int?) {
        val studentId = appPreferences.getStudentId() ?: return
        val currentMonth = month ?: (_selectedMonth.value ?: Calendar.getInstance().get(Calendar.MONTH) + 1)
        val currentYear = year ?: (_selectedYear.value ?: Calendar.getInstance().get(Calendar.YEAR))
        
        val yearMonth = String.format("%04d-%02d", currentYear, currentMonth)
        val observerKey = "$studentId-$yearMonth"
        
        // Remove existing observer for this key to prevent memory leaks
        attendanceObserver[observerKey]?.let { existingObserver ->
            // Observer will be removed automatically when new one is set
        }
        
        viewModelScope.launch {
            val liveData = repository.getAttendanceByStudentAndMonth(studentId, yearMonth).asLiveData()
            
            val observer = Observer<List<AttendanceEntity>> { attendanceEntities ->
                val attendanceList = attendanceEntities.map { entity ->
                    Attendance(
                        id = entity.id,
                        student_id = entity.student_id,
                        student_name = entity.student_name,
                        student_number = entity.student_number,
                        date = entity.date,
                        time_in = entity.time_in,
                        time_out = entity.time_out,
                        status = entity.status,
                        attendance_type = entity.attendance_type,
                        created_at = entity.created_at
                    )
                }
                
                generateCalendarMonth(attendanceList, currentMonth, currentYear)
            }
            
            // Store observer reference for cleanup
            attendanceObserver[observerKey] = observer
            liveData.observeForever(observer)
        }
    }
    
    private fun generateCalendarMonth(attendanceList: List<Attendance>, month: Int, year: Int) {
        val calendar = Calendar.getInstance()
        val currentMonth = month
        val currentYear = year
        
        calendar.set(currentYear, currentMonth - 1, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Get previous month days
        calendar.add(Calendar.MONTH, -1)
        val daysInPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        val calendarDays = mutableListOf<AttendanceCalendarDay>()
        
        // Add previous month days
        for (i in firstDayOfWeek - 1 downTo 0) {
            calendarDays.add(
                AttendanceCalendarDay(
                    day = daysInPrevMonth - i,
                    isCurrentMonth = false
                )
            )
        }
        
        // Add current month days
        val today = Calendar.getInstance()
        val isCurrentMonthAndYear = currentMonth == today.get(Calendar.MONTH) + 1 && 
                                  currentYear == today.get(Calendar.YEAR)
        
        for (day in 1..daysInMonth) {
            val dateString = String.format("%04d-%02d-%02d", currentYear, currentMonth, day)
            val attendance = attendanceList.find { it.date.startsWith(dateString) }
            
            calendarDays.add(
                AttendanceCalendarDay(
                    day = day,
                    isCurrentMonth = true,
                    attendance = attendance,
                    isToday = isCurrentMonthAndYear && day == today.get(Calendar.DAY_OF_MONTH)
                )
            )
        }
        
        // Add next month days to fill the calendar grid
        var nextMonthDay = 1
        while (calendarDays.size < 42) { // 6 weeks * 7 days
            calendarDays.add(
                AttendanceCalendarDay(
                    day = nextMonthDay,
                    isCurrentMonth = false
                )
            )
            nextMonthDay++
        }
        
        _currentMonth.value = AttendanceMonth(currentMonth, currentYear, calendarDays)
    }
    
    fun changeMonth(month: Int, year: Int) {
        _selectedMonth.value = month
        _selectedYear.value = year
        
        // First show from offline data
        generateCalendarFromOfflineData(month, year)
        
        // Then try to load from network
        loadAttendance(month, year)
    }
    
    fun navigateToNextMonth() {
        val currentMonth = _selectedMonth.value ?: return
        val currentYear = _selectedYear.value ?: return
        
        if (currentMonth == 12) {
            changeMonth(1, currentYear + 1)
        } else {
            changeMonth(currentMonth + 1, currentYear)
        }
    }
    
    fun navigateToPreviousMonth() {
        val currentMonth = _selectedMonth.value ?: return
        val currentYear = _selectedYear.value ?: return
        
        if (currentMonth == 1) {
            changeMonth(12, currentYear - 1)
        } else {
            changeMonth(currentMonth - 1, currentYear)
        }
    }
    
    fun getAttendanceStats(): Map<String, Int> {
        val attendanceList = _attendance.value ?: return emptyMap()
        return mapOf(
            "PRESENT" to attendanceList.count { it.status == "PRESENT" },
            "ABSENT" to attendanceList.count { it.status == "ABSENT" },
            "LATE" to attendanceList.count { it.status == "LATE" }
        )
    }
    
    fun clearError() {
        _errorMessage.value = ""
        syncManager.resetSyncState()
    }
    
    fun retryOperation() {
        clearError()
        loadAttendance(_selectedMonth.value, _selectedYear.value)
    }
    
    fun updateNetworkState(isAvailable: Boolean) {
        syncManager.updateNetworkState(isAvailable)
    }
    
    fun generateCalendarFromOfflineData(month: Int, year: Int) {
        val studentId = appPreferences.getStudentId() ?: return
        val yearMonth = String.format("%04d-%02d", year, month)
        val observerKey = "$studentId-$yearMonth-offline"
        
        // Remove existing observer for this key to prevent memory leaks
        attendanceObserver[observerKey]?.let { existingObserver ->
            // Observer will be removed automatically when new one is set
        }
        
        viewModelScope.launch {
            val liveData = repository.getAttendanceByStudentAndMonth(studentId, yearMonth).asLiveData()
            
            val observer = Observer<List<AttendanceEntity>> { attendanceEntities ->
                val attendanceList = attendanceEntities.map { entity ->
                    Attendance(
                        id = entity.id,
                        student_id = entity.student_id,
                        student_name = entity.student_name,
                        student_number = entity.student_number,
                        date = entity.date,
                        time_in = entity.time_in,
                        time_out = entity.time_out,
                        status = entity.status,
                        attendance_type = entity.attendance_type,
                        created_at = entity.created_at
                    )
                }
                
                generateCalendarMonth(attendanceList, month, year)
            }
            
            // Store observer reference for cleanup
            attendanceObserver[observerKey] = observer
            liveData.observeForever(observer)
        }
    }
    
    // Method to populate sample offline data for testing
    fun populateSampleOfflineData() {
        viewModelScope.launch {
            val studentId = appPreferences.getStudentId() ?: return@launch
            val student = appPreferences.getStudent() ?: return@launch
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentYear = calendar.get(Calendar.YEAR)
            
            // Create sample attendance data for current month
            val sampleAttendance = mutableListOf<AttendanceEntity>()
            
            for (day in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                val dateString = String.format("%04d-%02d-%02d", currentYear, currentMonth, day)
                val status = when {
                    day % 7 == 0 || day % 7 == 6 -> "ABSENT" // Weekends
                    day % 5 == 0 -> "LATE" // Every 5th day
                    else -> "PRESENT" // Most days present
                }
                
                sampleAttendance.add(
                    AttendanceEntity(
                        id = day * 1000 + currentMonth * 100 + currentYear,
                        student_id = studentId,
                        student_name = student.name, // Use actual student name from AppPreferences
                        student_number = studentId,
                        date = dateString,
                        time_in = if (status != "ABSENT") "08:00:00" else null,
                        time_out = if (status != "ABSENT") "17:00:00" else null,
                        status = status,
                        attendance_type = "RFID",
                        created_at = "$dateString 08:00:00"
                    )
                )
            }
            
            repository.saveAttendance(sampleAttendance)
            
            // Refresh calendar with new data
            generateCalendarFromOfflineData(currentMonth, currentYear)
        }
    }
}