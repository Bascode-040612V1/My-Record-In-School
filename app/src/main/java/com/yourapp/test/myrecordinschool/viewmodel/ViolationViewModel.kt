package com.yourapp.test.myrecordinschool.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yourapp.test.myrecordinschool.data.api.RetrofitClient
import com.yourapp.test.myrecordinschool.data.model.Violation
import com.yourapp.test.myrecordinschool.data.preferences.AppPreferences
import com.yourapp.test.myrecordinschool.roomdb.AppDatabase
import com.yourapp.test.myrecordinschool.roomdb.entity.ViolationEntity
import com.yourapp.test.myrecordinschool.roomdb.repository.ViolationRepository
import kotlinx.coroutines.launch

class ViolationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appPreferences = AppPreferences(application)
    private val violationDao = AppDatabase.getDatabase(application).violationDao()

    private val _violations = MutableLiveData<List<Violation>>()
    val violations: LiveData<List<Violation>> = _violations
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _selectedViolation = MutableLiveData<Violation?>()
    val selectedViolation: LiveData<Violation?> = _selectedViolation

    // Repository (with Room)
    private val repository = ViolationRepository(violationDao)

    // Observed in UI (offline + auto updates)
    val violationsFromDb: LiveData<List<ViolationEntity>> =
        repository.getViolations().asLiveData()

    // API fetch, then save to DB
    fun loadViolations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val studentId = appPreferences.getStudentId()
                if (studentId != null) {
                    val config = appPreferences.getAppConfig()
                    val api = RetrofitClient.getViolationApi(config.baseUrl)
                    val response = api.getStudentViolations(studentId)

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.success == true) {
                            val violations = responseBody.violations ?: emptyList()

                            // Convert to entities
                            val entities = violations.map { v ->
                                ViolationEntity(
                                    id = v.id,
                                    student_id = v.student_id,
                                    student_name = v.student_name,
                                    year_level = v.year_level,
                                    course = v.course,
                                    section = v.section,
                                    violation_type = v.violation_type,
                                    violation_description = v.violation_description,
                                    offense_count = v.offense_count,
                                    original_offense_count = v.original_offense_count,
                                    penalty = v.penalty,
                                    recorded_by = v.recorded_by,
                                    date_recorded = v.date_recorded,
                                    acknowledged = v.acknowledged,
                                    category = v.category
                                )
                            }

                            // Save into Room DB for offline
                            repository.saveViolations(entities)
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun acknowledgeViolation(violationId: Int) {
        viewModelScope.launch {
            try {
                val config = appPreferences.getAppConfig()
                val api = RetrofitClient.getViolationApi(config.baseUrl)
                val response = api.acknowledgeViolation(violationId)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update the violation in the local list
                    val currentViolations = _violations.value?.toMutableList() ?: mutableListOf()
                    val index = currentViolations.indexOfFirst { it.id == violationId }
                    if (index != -1) {
                        currentViolations[index] = currentViolations[index].copy(acknowledged = 1)
                        _violations.value = currentViolations
                    }
                } else {
                    _errorMessage.value = response.body()?.message ?: "Failed to acknowledge violation"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            }
        }
    }
    
    fun selectViolation(violation: Violation) {
        _selectedViolation.value = violation
    }
    
    fun clearSelectedViolation() {
        _selectedViolation.value = null
    }
    
    fun getViolationsByCategory(category: String): List<Violation> {
        return _violations.value?.filter { it.category == category } ?: emptyList()
    }
    
    fun getViolationCounts(): Map<String, Int> {
        val violations = _violations.value ?: return emptyMap()
        return violations.groupingBy { it.category }.eachCount()
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
    
    fun debugAppState() {
        val studentId = appPreferences.getStudentId()
        val config = appPreferences.getAppConfig()
        val isLoggedIn = appPreferences.isLoggedIn()
        
        android.util.Log.d("ViolationViewModel", "Debug App State:")
        android.util.Log.d("ViolationViewModel", "  - Student ID: $studentId")
        android.util.Log.d("ViolationViewModel", "  - Is Logged In: $isLoggedIn")
        android.util.Log.d("ViolationViewModel", "  - Base URL: ${config.baseUrl}")
        android.util.Log.d("ViolationViewModel", "  - IP Address: ${config.ipAddress}")
        android.util.Log.d("ViolationViewModel", "  - Port: ${config.port}")
    }
    
    fun testConnectivity() {
        viewModelScope.launch {
            try {
                val config = appPreferences.getAppConfig()
                val studentId = appPreferences.getStudentId()
                
                android.util.Log.d("ViolationViewModel", "=== CONNECTIVITY TEST ===")
                android.util.Log.d("ViolationViewModel", "Base URL: ${config.baseUrl}")
                android.util.Log.d("ViolationViewModel", "Student ID: $studentId")
                android.util.Log.d("ViolationViewModel", "Full endpoint: ${config.baseUrl}violations/$studentId")
                
                if (studentId != null) {
                    val api = RetrofitClient.getViolationApi(config.baseUrl)
                    val response = api.getStudentViolations(studentId)
                    
                    android.util.Log.d("ViolationViewModel", "Response Status: ${response.code()}")
                    android.util.Log.d("ViolationViewModel", "Response Success: ${response.isSuccessful}")
                    android.util.Log.d("ViolationViewModel", "Response Body: ${response.body()}")
                    android.util.Log.d("ViolationViewModel", "Response Error: ${response.errorBody()?.string()}")
                    
                    if (response.isSuccessful) {
                        val body = response.body()
                        android.util.Log.d("ViolationViewModel", "Backend Success Flag: ${body?.success}")
                        android.util.Log.d("ViolationViewModel", "Backend Message: ${body?.message}")
                        android.util.Log.d("ViolationViewModel", "Violations Count: ${body?.violations?.size}")
                    }
                } else {
                    android.util.Log.e("ViolationViewModel", "Cannot test connectivity - no student ID")
                }
                android.util.Log.d("ViolationViewModel", "=== END CONNECTIVITY TEST ===")
            } catch (e: Exception) {
                android.util.Log.e("ViolationViewModel", "Connectivity test failed", e)
            }
        }
    }
}