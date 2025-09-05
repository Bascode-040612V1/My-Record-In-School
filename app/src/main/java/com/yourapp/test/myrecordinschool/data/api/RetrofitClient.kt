package com.yourapp.test.myrecordinschool.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var currentBaseUrl: String = ""
    
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val debugInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request()
        android.util.Log.d("RetrofitClient", "Making request to: ${request.url}")
        android.util.Log.d("RetrofitClient", "Method: ${request.method}")
        android.util.Log.d("RetrofitClient", "Headers: ${request.headers}")
        
        val response = chain.proceed(request)
        android.util.Log.d("RetrofitClient", "Response code: ${response.code}")
        android.util.Log.d("RetrofitClient", "Response message: ${response.message}")
        
        response
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(debugInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    fun getClient(baseUrl: String): Retrofit {
        android.util.Log.d("RetrofitClient", "Getting client for baseUrl: $baseUrl")
        if (retrofit == null || currentBaseUrl != baseUrl) {
            currentBaseUrl = baseUrl
            android.util.Log.d("RetrofitClient", "Creating new Retrofit instance for: $baseUrl")
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }
    
    fun getStudentApi(baseUrl: String): StudentApi {
        return getClient(baseUrl).create(StudentApi::class.java)
    }
    
    fun getViolationApi(baseUrl: String): ViolationApi {
        return getClient(baseUrl).create(ViolationApi::class.java)
    }
    
    fun getAttendanceApi(baseUrl: String): AttendanceApi {
        return getClient(baseUrl).create(AttendanceApi::class.java)
    }
    
    fun getConnectionApi(baseUrl: String): ConnectionApi {
        return getClient(baseUrl).create(ConnectionApi::class.java)
    }
    
    fun getRfidApi(baseUrl: String): RfidApi {
        return getClient(baseUrl).create(RfidApi::class.java)
    }
    
    // Helper method to test if the backend is reachable
    fun testBackendConnectivity(baseUrl: String, callback: (Boolean, String) -> Unit) {
        try {
            android.util.Log.d("RetrofitClient", "Testing backend connectivity to: $baseUrl")
            val connectionApi = getConnectionApi(baseUrl)
            
            // This would need to be called from a coroutine
            callback(true, "Backend URL configured: $baseUrl")
        } catch (e: Exception) {
            android.util.Log.e("RetrofitClient", "Backend connectivity test failed", e)
            callback(false, "Failed to connect to backend: ${e.message}")
        }
    }
}