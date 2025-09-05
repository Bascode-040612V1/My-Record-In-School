package com.yourapp.test.myrecordinschool.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yourapp.test.myrecordinschool.data.api.RetrofitClient
import com.yourapp.test.myrecordinschool.data.model.AppConfig
import com.yourapp.test.myrecordinschool.data.preferences.AppPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appPreferences = AppPreferences(application)
    
    private val _appConfig = MutableLiveData<AppConfig>()
    val appConfig: LiveData<AppConfig> = _appConfig
    
    private val _connectionStatus = MutableLiveData<ConnectionStatus>()
    val connectionStatus: LiveData<ConnectionStatus> = _connectionStatus
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    init {
        loadConfig()
    }
    
    private fun loadConfig() {
        _appConfig.value = appPreferences.getAppConfig()
    }
    
    fun updateIPAddress(ipAddress: String) {
        val currentConfig = _appConfig.value ?: AppConfig()
        val newConfig = currentConfig.copy(ipAddress = ipAddress)
        appPreferences.saveAppConfig(newConfig)
        _appConfig.value = newConfig
    }
    
    fun updatePort(port: String) {
        val currentConfig = _appConfig.value ?: AppConfig()
        val newConfig = currentConfig.copy(port = port)
        appPreferences.saveAppConfig(newConfig)
        _appConfig.value = newConfig
    }
    
    fun updateConfig(ipAddress: String, port: String) {
        val newConfig = AppConfig(ipAddress, port)
        appPreferences.saveAppConfig(newConfig)
        _appConfig.value = newConfig
    }
    
    fun testDatabaseConnection() {
        viewModelScope.launch {
            _isLoading.value = true
            _connectionStatus.value = ConnectionStatus.Testing
            
            try {
                val config = _appConfig.value ?: appPreferences.getAppConfig()
                val api = RetrofitClient.getConnectionApi(config.baseUrl)
                val response = api.testConnection()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _connectionStatus.value = ConnectionStatus.Success(
                        response.body()?.message ?: "Connection successful"
                    )
                } else {
                    _connectionStatus.value = ConnectionStatus.Failed(
                        response.body()?.message ?: "Connection failed"
                    )
                }
            } catch (e: Exception) {
                _connectionStatus.value = ConnectionStatus.Failed("Network error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearConnectionStatus() {
        _connectionStatus.value = ConnectionStatus.Idle
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
}

sealed class ConnectionStatus {
    object Idle : ConnectionStatus()
    object Testing : ConnectionStatus()
    data class Success(val message: String) : ConnectionStatus()
    data class Failed(val message: String) : ConnectionStatus()
}