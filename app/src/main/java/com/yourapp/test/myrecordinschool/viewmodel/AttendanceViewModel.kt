package com.yourapp.test.myrecordinschool.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yourapp.test.myrecordinschool.data.api.RetrofitClient
import com.yourapp.test.myrecordinschool.data.model.Attendance
import com.yourapp.test.myrecordinschool.data.model.AttendanceCalendarDay
import com.yourapp.test.myrecordinschool.data.model.AttendanceMonth
import com.yourapp.test.myrecordinschool.data.preferences.AppPreferences
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appPreferences = AppPreferences(application)
    
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
    
    init {
        val calendar = Calendar.getInstance()
        _selectedMonth.value = calendar.get(Calendar.MONTH) + 1
        _selectedYear.value = calendar.get(Calendar.YEAR)
        loadAttendance()
    }
    
    fun loadAttendance(month: Int? = null, year: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val studentId = appPreferences.getStudentId()
                if (studentId != null) {
                    val config = appPreferences.getAppConfig()
                    android.util.Log.d("AttendanceViewModel", "Loading attendance for student: $studentId")
                    android.util.Log.d("AttendanceViewModel", "Using base URL: ${config.baseUrl}")
                    
                    val api = RetrofitClient.getAttendanceApi(config.baseUrl)
                    val response = api.getStudentAttendance(studentId, month, year)
                    
                    android.util.Log.d("AttendanceViewModel", "Response received - Success: ${response.isSuccessful}, Code: ${response.code()}")
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        val attendanceList = response.body()?.attendance ?: emptyList()
                        _attendance.value = attendanceList
                        generateCalendarMonth(attendanceList, month, year)
                        android.util.Log.d("AttendanceViewModel", "Loaded ${attendanceList.size} attendance records successfully")
                    } else {
                        val errorMsg = response.body()?.message ?: "Failed to load attendance"
                        val debugInfo = if (response.body() != null) {
                            " (HTTP ${response.code()}, Success: ${response.body()?.success})"
                        } else {
                            " (HTTP ${response.code()})"
                        }
                        _errorMessage.value = errorMsg + debugInfo
                        android.util.Log.e("AttendanceViewModel", "Load attendance failed: $errorMsg, Response: ${response.body()}")
                        android.util.Log.e("AttendanceViewModel", "Full response error: ${response.errorBody()?.string()}")
                    }
                } else {
                    _errorMessage.value = "Student ID not found. Please log in again."
                    android.util.Log.e("AttendanceViewModel", "Student ID is null - user may not be logged in")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                android.util.Log.e("AttendanceViewModel", "Exception loading attendance", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun generateCalendarMonth(attendanceList: List<Attendance>, month: Int?, year: Int?) {
        val calendar = Calendar.getInstance()
        val currentMonth = month ?: (calendar.get(Calendar.MONTH) + 1)
        val currentYear = year ?: calendar.get(Calendar.YEAR)
        
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
    }
}