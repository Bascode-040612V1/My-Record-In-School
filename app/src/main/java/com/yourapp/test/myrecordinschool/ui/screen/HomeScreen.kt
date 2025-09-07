package com.yourapp.test.myrecordinschool.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourapp.test.myrecordinschool.roomdb.entity.ViolationEntity
import com.yourapp.test.myrecordinschool.data.model.Violation
import com.yourapp.test.myrecordinschool.data.model.*
import com.yourapp.test.myrecordinschool.ui.theme.*
import com.yourapp.test.myrecordinschool.ui.components.*
import com.yourapp.test.myrecordinschool.viewmodel.AttendanceViewModel
import com.yourapp.test.myrecordinschool.viewmodel.ViolationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToViolationDetail: (Violation) -> Unit,
    violationViewModel: ViolationViewModel = viewModel(),
    attendanceViewModel: AttendanceViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Violations", "My Attendance")
    
    // Sync status for both ViewModels
    val violationSyncStatus by violationViewModel.syncStatus.collectAsState()
    val attendanceSyncStatus by attendanceViewModel.syncStatus.collectAsState()
    val violationNetworkState by violationViewModel.networkState.collectAsState()
    
    LaunchedEffect(Unit) {
        violationViewModel.loadViolations()
        attendanceViewModel.loadAttendance()
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar with Sync Status
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "My Record in School",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    
                    // Show network status
                    if (violationNetworkState == NetworkState.Unavailable) {
                        Text(
                            text = "Offline Mode",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            },
            actions = {
                // Refresh button
                IconButton(
                    onClick = {
                        if (selectedTab == 0) {
                            violationViewModel.refreshViolations()
                        } else {
                            attendanceViewModel.refreshAttendance()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue40
            )
        )
        
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = title,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        
        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> ViolationsTab(
                    violationViewModel = violationViewModel,
                    onNavigateToDetail = onNavigateToViolationDetail
                )
                1 -> AttendanceTab(
                    attendanceViewModel = attendanceViewModel
                )
            }
        }
    }
}

@Composable
private fun ViolationsTab(
    violationViewModel: ViolationViewModel,
    onNavigateToDetail: (Violation) -> Unit
) {
    val violationsFromDb by violationViewModel.violationsFromDb.observeAsState(emptyList())
    val violationDataState by violationViewModel.violationDataState.collectAsState()
    val isLoading by violationViewModel.isLoading.observeAsState(false)
    val errorMessage by violationViewModel.errorMessage.observeAsState("")
    
    Column {
        // Header with Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Violation History",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Track your school violations and their status",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Refresh Button
                IconButton(
                    onClick = { 
                        violationViewModel.clearError()
                        violationViewModel.refreshViolations()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Show data from Room database (offline-first)
        when {
            isLoading -> {
                LoadingIndicator(
                    isLoading = true,
                    message = "Loading violations..."
                )
            }
            
            violationsFromDb.isEmpty() && errorMessage.isEmpty() -> {
                EmptyStateCard(
                    message = "Great job! You have no violations on record.",
                    icon = Icons.Default.CheckCircle
                )
            }
            
            errorMessage.isNotEmpty() -> {
                ErrorCard(
                    message = errorMessage,
                    onRetry = { violationViewModel.retryOperation() }
                )
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(violationsFromDb) { violationEntity ->
                        ViolationEntityCard(
                            violationEntity = violationEntity,
                            onClick = {
                                // Hidden acknowledge functionality - auto-acknowledge on view
                                if (violationEntity.acknowledged == 0) {
                                    violationViewModel.acknowledgeViolation(violationEntity.id)
                                }
                                val violation = violationViewModel.convertToViolation(violationEntity)
                                onNavigateToDetail(violation)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViolationEntityCard(
    violationEntity: ViolationEntity,
    onClick: () -> Unit
) {
    val offenseColor = when (violationEntity.offense_count) {
        1 -> Green
        2 -> Orange
        else -> Red
    }
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val formattedDate = try {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(violationEntity.date_recorded)
        dateFormat.format(date ?: Date())
    } catch (e: Exception) {
        violationEntity.date_recorded
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = if (violationEntity.acknowledged == 1) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (violationEntity.acknowledged == 1) 2.dp else 6.dp
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = offenseColor.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Enhanced Offense Indicator
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = offenseColor.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, offenseColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = offenseColor
                        )
                        Text(
                            text = "${violationEntity.offense_count}${getOrdinalSuffix(violationEntity.offense_count)} Offense",
                            style = MaterialTheme.typography.labelMedium,
                            color = offenseColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Text(
                text = "Warning: ${violationEntity.penalty}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Violation: ${violationEntity.violation_description}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Recorded by: ${violationEntity.recorded_by}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Always show View Details button
                Button(
                    onClick = onClick,
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "View Details",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = violationEntity.category.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun getOrdinalSuffix(number: Int): String {
    return when {
        number % 100 in 11..13 -> "th"
        number % 10 == 1 -> "st"
        number % 10 == 2 -> "nd"
        number % 10 == 3 -> "rd"
        else -> "th"
    }
}

@Composable
private fun AttendanceTab(
    attendanceViewModel: AttendanceViewModel
) {
    val attendanceDataState by attendanceViewModel.attendanceDataState.collectAsState()
    val isLoading by attendanceViewModel.isLoading.observeAsState(false)
    val errorMessage by attendanceViewModel.errorMessage.observeAsState("")
    
    when {
        isLoading -> {
            LoadingIndicator(
                isLoading = true,
                message = "Loading attendance..."
            )
        }
        
        errorMessage.isNotEmpty() -> {
            ErrorCard(
                message = errorMessage,
                onRetry = { attendanceViewModel.retryOperation() }
            )
        }
        
        else -> {
            AttendanceCalendar(attendanceViewModel = attendanceViewModel)
        }
    }
}