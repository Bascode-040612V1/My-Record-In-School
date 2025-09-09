package com.yourapp.test.myrecordinschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yourapp.test.myrecordinschool.data.sync.SyncManager
import com.yourapp.test.myrecordinschool.navigation.MyRecordInSchoolNavigation
import com.yourapp.test.myrecordinschool.ui.theme.MyRecordInSchoolTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var syncManager: SyncManager
    
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            if (::syncManager.isInitialized) {
                syncManager.setAppForegroundState(true)
                syncManager.notifyUserInteraction()
            }
        }
        
        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            if (::syncManager.isInitialized) {
                syncManager.setAppForegroundState(false)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize SyncManager
        syncManager = SyncManager(application)
        
        // Register lifecycle observer
        lifecycle.addObserver(lifecycleObserver)
        
        setContent {
            MyRecordInSchoolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyRecordInSchoolNavigation()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(lifecycleObserver)
    }
}