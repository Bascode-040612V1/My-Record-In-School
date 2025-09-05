package com.yourapp.test.myrecordinschool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourapp.test.myrecordinschool.data.model.AttendanceCalendarDay
import com.yourapp.test.myrecordinschool.data.model.AttendanceMonth
import com.yourapp.test.myrecordinschool.ui.theme.*
import com.yourapp.test.myrecordinschool.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AttendanceCalendar(
    attendanceViewModel: AttendanceViewModel = viewModel()
) {
    val currentMonth by attendanceViewModel.currentMonth.observeAsState()
    val isLoading by attendanceViewModel.isLoading.observeAsState(false)
    val errorMessage by attendanceViewModel.errorMessage.observeAsState("")
    val selectedMonth by attendanceViewModel.selectedMonth.observeAsState(1)
    val selectedYear by attendanceViewModel.selectedYear.observeAsState(2024)
    
    val attendanceStats by attendanceViewModel.attendance.observeAsState(emptyList())
    val stats = remember(attendanceStats) {
        attendanceStats.groupingBy { it.status }.eachCount()
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card with Month Navigation and Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title and Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Attendance",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { attendanceViewModel.navigateToPreviousMonth() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronLeft,
                                contentDescription = "Previous Month",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Text(
                            text = getMonthYearText(selectedMonth, selectedYear),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        IconButton(
                            onClick = { attendanceViewModel.navigateToNextMonth() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "Next Month",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                // Attendance Statistics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AttendanceStatCard(
                        label = "Present",
                        count = stats["PRESENT"] ?: 0,
                        color = Green,
                        modifier = Modifier.weight(1f)
                    )
                    AttendanceStatCard(
                        label = "Absent",
                        count = stats["ABSENT"] ?: 0,
                        color = Red,
                        modifier = Modifier.weight(1f)
                    )
                    AttendanceStatCard(
                        label = "Late",
                        count = stats["LATE"] ?: 0,
                        color = Orange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Calendar Content
        when {
            isLoading -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            
            errorMessage.isNotEmpty() -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            currentMonth != null -> {
                CalendarGrid(currentMonth!!)
            }
        }
        
        // Legend
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Legend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    LegendItem("Present", Green)
                    LegendItem("Absent", Red)
                    LegendItem("Late", Orange)
                }
            }
        }
    }
}

@Composable
private fun AttendanceStatCard(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    attendanceMonth: AttendanceMonth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Days of week header
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar days grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(attendanceMonth.days) { day ->
                    CalendarDayItem(day)
                }
            }
        }
    }
}

@Composable
private fun CalendarDayItem(
    day: AttendanceCalendarDay
) {
    val backgroundColor = when {
        !day.isCurrentMonth -> Color.Transparent
        day.attendance?.status == "PRESENT" -> Green.copy(alpha = 0.2f)
        day.attendance?.status == "ABSENT" -> Red.copy(alpha = 0.2f)
        day.attendance?.status == "LATE" -> Orange.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    
    val textColor = when {
        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        day.isToday -> MaterialTheme.colorScheme.primary
        day.attendance != null -> when (day.attendance.status) {
            "PRESENT" -> Green
            "ABSENT" -> Red
            "LATE" -> Orange
            else -> MaterialTheme.colorScheme.onSurface
        }
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (day.isToday) {
                    Modifier.background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color.copy(alpha = 0.6f), CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getMonthYearText(month: Int, year: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, month - 1)
    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    return "${monthFormat.format(calendar.time)} $year"
}