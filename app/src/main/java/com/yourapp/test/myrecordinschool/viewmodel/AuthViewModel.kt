package com.yourapp.test.myrecordinschool.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yourapp.test.myrecordinschool.data.api.RetrofitClient
import com.yourapp.test.myrecordinschool.data.model.*
import com.yourapp.test.myrecordinschool.data.sync.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.yourapp.test.myrecordinschool.data.preferences.AppPreferences
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appPreferences = AppPreferences(application)
    private val syncManager = SyncManager(application)
    
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    
    // Enhanced state management
    private val _authDataState = MutableStateFlow<DataState<Student>>(DataState.Loading)
    val authDataState: StateFlow<DataState<Student>> = _authDataState.asStateFlow()
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage
    
    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess
    
    // Network state
    val networkState: StateFlow<NetworkState> = syncManager.networkState
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        if (appPreferences.isLoggedIn()) {
            val student = appPreferences.getStudent()
            _authState.value = AuthState.Authenticated(student)
            _authDataState.value = if (student != null) {
                DataState.Success(student)
            } else {
                DataState.Error("Student data not found")
            }
        } else {
            _authState.value = AuthState.Unauthenticated
            _authDataState.value = DataState.Loading
        }
    }
    
    fun login(studentId: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            _authDataState.value = DataState.Loading
            
            try {
                val config = appPreferences.getAppConfig()
                val api = RetrofitClient.getStudentApi(config.baseUrl)
                val response = api.login(LoginRequest(studentId, password))
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val student = response.body()?.student
                    if (student != null) {
                        appPreferences.saveStudent(student)
                        appPreferences.setLoggedIn(true)
                        _authState.value = AuthState.Authenticated(student)
                        _authDataState.value = DataState.Success(student)
                        _successMessage.value = "Login successful! Welcome ${student.name}"
                        
                        // Update sync manager network state
                        syncManager.updateNetworkState(true)
                    } else {
                        val errorMsg = "Invalid student data received"
                        _errorMessage.value = errorMsg
                        _authDataState.value = DataState.Error(errorMsg)
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Login failed"
                    val finalErrorMsg = if (errorMsg.contains("Invalid", ignoreCase = true)) {
                        "Student not found or incorrect credentials"
                    } else {
                        errorMsg
                    }
                    _errorMessage.value = finalErrorMsg
                    _authDataState.value = DataState.Error(finalErrorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host", ignoreCase = true) == true -> 
                        "Cannot connect to server. Please check your internet connection and server settings."
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Connection timeout. Please try again."
                    else -> "Network error: ${e.message}"
                }
                _errorMessage.value = errorMsg
                _authDataState.value = DataState.Error(errorMsg)
                
                // Update sync manager network state
                syncManager.updateNetworkState(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            _registrationSuccess.value = false
            try {
                val config = appPreferences.getAppConfig()
                val api = RetrofitClient.getStudentApi(config.baseUrl)
                val response = api.register(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val student = response.body()?.student
                    if (student != null) {
                        // Don't auto-login, just show success message and switch to login
                        _successMessage.value = "Registration successful! You can now login with your student ID."
                        _registrationSuccess.value = true
                    } else {
                        _errorMessage.value = "Registration successful but student data not received"
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Registration failed"
                    _errorMessage.value = if (errorMsg.contains("already exists", ignoreCase = true)) {
                        "Student ID already registered. Please try logging in instead."
                    } else {
                        errorMsg
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = when {
                    e.message?.contains("Unable to resolve host", ignoreCase = true) == true -> 
                        "Cannot connect to server. Please check your internet connection and server settings."
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Connection timeout. Please try again."
                    else -> "Network error: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun logout() {
        appPreferences.logout()
        _authState.value = AuthState.Unauthenticated
        _authDataState.value = DataState.Loading
        syncManager.stopPeriodicSync()
    }
    
    fun updateStudentInfo(year: String, course: String, section: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val studentId = appPreferences.getStudentId()
                if (studentId != null) {
                    val config = appPreferences.getAppConfig()
                    val api = RetrofitClient.getStudentApi(config.baseUrl)
                    val response = api.updateStudent(
                        UpdateStudentRequest(studentId, year, course, section)
                    )
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        appPreferences.updateStudentInfo(year, course, section)
                        val updatedStudent = appPreferences.getStudent()
                        _authState.value = AuthState.Authenticated(updatedStudent)
                    } else {
                        _errorMessage.value = response.body()?.message ?: "Update failed"
                    }
                } else {
                    _errorMessage.value = "Student ID not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }
    
    fun clearRegistrationSuccess() {
        _registrationSuccess.value = false
    }
    
    fun retryLogin(studentId: String, password: String) {
        clearError()
        login(studentId, password)
    }
    
    fun updateNetworkState(isAvailable: Boolean) {
        syncManager.updateNetworkState(isAvailable)
    }
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val student: Student?) : AuthState()
}