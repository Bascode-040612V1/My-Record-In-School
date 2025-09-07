package com.yourapp.test.myrecordinschool.data.model

sealed class DataState<out T> {
    object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val message: String, val exception: Throwable? = null) : DataState<Nothing>()
    data class Cached<T>(val data: T, val isStale: Boolean = false) : DataState<T>()
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
    data class Conflict(val conflictedItems: List<String>) : SyncState()
}

data class SyncStatus(
    val lastSyncTime: Long = 0L,
    val syncState: SyncState = SyncState.Idle,
    val pendingOperations: Int = 0
)

sealed class NetworkState {
    object Available : NetworkState()
    object Unavailable : NetworkState()
    object Unknown : NetworkState()
}