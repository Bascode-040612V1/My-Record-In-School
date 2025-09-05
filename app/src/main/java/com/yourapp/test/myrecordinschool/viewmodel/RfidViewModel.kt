package com.yourapp.test.myrecordinschool.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yourapp.test.myrecordinschool.data.api.RetrofitClient
import com.yourapp.test.myrecordinschool.data.preferences.AppPreferences
import kotlinx.coroutines.launch

class RfidViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appPreferences = AppPreferences(application)
    
    private val _rfidNumber = MutableLiveData<String>()
    val rfidNumber: LiveData<String> = _rfidNumber
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    fun fetchLatestRfid() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                val config = appPreferences.getAppConfig()
                android.util.Log.d("RfidViewModel", "Fetching latest RFID from: ${config.baseUrl}rfid/get_latest.php")
                
                val api = RetrofitClient.getRfidApi(config.baseUrl)
                val response = api.getLatestRfid()
                
                android.util.Log.d("RfidViewModel", "RFID Response - Success: ${response.isSuccessful}, Code: ${response.code()}")
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    android.util.Log.d("RfidViewModel", "RFID Response body: $responseBody")
                    
                    if (responseBody?.success == true && responseBody.rfid_number != null) {
                        _rfidNumber.value = responseBody.rfid_number
                        android.util.Log.d("RfidViewModel", "RFID fetched successfully: ${responseBody.rfid_number}")
                    } else {
                        _errorMessage.value = responseBody?.message ?: "No RFID scanned yet"
                        android.util.Log.w("RfidViewModel", "No RFID available: ${responseBody?.message}")
                    }
                } else {
                    val errorMsg = "Failed to fetch RFID (HTTP ${response.code()})"
                    _errorMessage.value = errorMsg
                    android.util.Log.e("RfidViewModel", errorMsg)
                    android.util.Log.e("RfidViewModel", "Response error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                android.util.Log.e("RfidViewModel", "Exception fetching RFID", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearRfid() {
        _rfidNumber.value = ""
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
}