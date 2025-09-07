package com.yourapp.test.myrecordinschool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yourapp.test.myrecordinschool.data.model.DataState
import com.yourapp.test.myrecordinschool.data.model.SyncState
import com.yourapp.test.myrecordinschool.ui.theme.*

@Composable
fun LoadingIndicator(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.wrapContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun <T> DataStateHandler(
    dataState: DataState<T>,
    onRetry: () -> Unit = {},
    onRefresh: () -> Unit = {},
    loadingMessage: String = "Loading...",
    emptyMessage: String = "No data available",
    emptyIcon: ImageVector = Icons.Default.Inbox,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (dataState) {
            is DataState.Loading -> {
                LoadingIndicator(
                    isLoading = true,
                    message = loadingMessage
                )
            }
            
            is DataState.Success -> {
                content(dataState.data)
            }
            
            is DataState.Cached -> {
                Column {
                    if (dataState.isStale) {
                        SyncStatusBanner(
                            message = "Data may be outdated",
                            type = SyncStatusType.Warning,
                            onRefresh = onRefresh
                        )
                    }
                    content(dataState.data)
                }
            }
            
            is DataState.Error -> {
                ErrorCard(
                    message = dataState.message,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    showRetry: Boolean = true
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            if (showRetry) {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    icon: ImageVector = Icons.Default.Inbox,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "No Data",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (actionText != null && onAction != null) {
                Button(onClick = onAction) {
                    Text(actionText)
                }
            }
        }
    }
}

enum class SyncStatusType {
    Info, Warning, Error, Success
}

@Composable
fun SyncStatusBanner(
    message: String,
    type: SyncStatusType,
    onRefresh: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    val backgroundColor = when (type) {
        SyncStatusType.Info -> MaterialTheme.colorScheme.primaryContainer
        SyncStatusType.Warning -> MaterialTheme.colorScheme.secondaryContainer
        SyncStatusType.Error -> MaterialTheme.colorScheme.errorContainer
        SyncStatusType.Success -> MaterialTheme.colorScheme.tertiaryContainer
    }
    
    val contentColor = when (type) {
        SyncStatusType.Info -> MaterialTheme.colorScheme.onPrimaryContainer
        SyncStatusType.Warning -> MaterialTheme.colorScheme.onSecondaryContainer
        SyncStatusType.Error -> MaterialTheme.colorScheme.onErrorContainer
        SyncStatusType.Success -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    
    val icon = when (type) {
        SyncStatusType.Info -> Icons.Default.Info
        SyncStatusType.Warning -> Icons.Default.Warning
        SyncStatusType.Error -> Icons.Default.Error
        SyncStatusType.Success -> Icons.Default.CheckCircle
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (onRefresh != null) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(16.dp),
                            tint = contentColor
                        )
                    }
                }
                
                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier.size(16.dp),
                            tint = contentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SyncStateIndicator(
    syncState: SyncState,
    modifier: Modifier = Modifier
) {
    val (icon, color, message) = when (syncState) {
        is SyncState.Idle -> Triple(Icons.Default.CloudOff, MaterialTheme.colorScheme.onSurfaceVariant, "")
        is SyncState.Syncing -> Triple(Icons.Default.Sync, MaterialTheme.colorScheme.primary, "Syncing...")
        is SyncState.Success -> Triple(Icons.Default.CloudDone, Color(0xFF4CAF50), "Synced")
        is SyncState.Error -> Triple(Icons.Default.CloudOff, MaterialTheme.colorScheme.error, "Sync failed")
        is SyncState.Conflict -> Triple(Icons.Default.Warning, Color(0xFFFF9800), "Conflicts")
    }
    
    if (syncState !is SyncState.Idle) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
        }
    }
}