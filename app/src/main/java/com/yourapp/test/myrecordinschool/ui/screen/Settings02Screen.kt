package com.yourapp.test.myrecordinschool.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourapp.test.myrecordinschool.data.model.DropdownOptions
import com.yourapp.test.myrecordinschool.data.model.Student
import com.yourapp.test.myrecordinschool.data.preferences.AppPreferences
import com.yourapp.test.myrecordinschool.ui.components.CustomButton
import com.yourapp.test.myrecordinschool.ui.components.CustomDropdownField
import com.yourapp.test.myrecordinschool.ui.components.CustomTextField
import com.yourapp.test.myrecordinschool.ui.theme.Blue40
import com.yourapp.test.myrecordinschool.viewmodel.AuthState
import com.yourapp.test.myrecordinschool.viewmodel.AuthViewModel
import com.yourapp.test.myrecordinschool.viewmodel.ConnectionStatus
import com.yourapp.test.myrecordinschool.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings02Screen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val authState by authViewModel.authState.observeAsState()
    val appConfig by settingsViewModel.appConfig.observeAsState()
    val connectionStatus by settingsViewModel.connectionStatus.observeAsState(ConnectionStatus.Idle)
    val isLoading by settingsViewModel.isLoading.observeAsState(false)
    
    var showUpdateForm by remember { mutableStateOf(false) }
    var showIpDialog by remember { mutableStateOf(false) }
    
    val student = (authState as? AuthState.Authenticated)?.student
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Blue40,
                        Blue40.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue40
            )
        )
        
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Data Card
            student?.let { studentData ->
                StudentDataCard(student = studentData)
            }
            
            // Update Information Card
            UpdateInformationCard(
                student = student,
                onShowUpdateForm = { showUpdateForm = true }
            )
            
            // Server Configuration Card
            ServerConfigurationCard(
                appConfig = appConfig,
                onShowIpDialog = { showIpDialog = true },
                onTestConnection = { settingsViewModel.testDatabaseConnection() },
                connectionStatus = connectionStatus,
                isLoading = isLoading
            )
            
            // Quick Actions Card
            QuickActionsCard(
                onTestConnection = { settingsViewModel.testDatabaseConnection() },
                isLoading = isLoading,
                connectionStatus = connectionStatus
            )
            
            // Logout Card
            LogoutCard(onLogout = {
                authViewModel.logout()
                onLogout()
            })
        }
    }
    
    // Update Form Dialog
    if (showUpdateForm) {
        UpdateInformationDialog(
            student = student,
            onDismiss = { showUpdateForm = false },
            onUpdate = { year, course, section ->
                authViewModel.updateStudentInfo(year, course, section)
                showUpdateForm = false
            }
        )
    }
    
    // IP Configuration Dialog
    if (showIpDialog) {
        IpConfigurationDialog(
            currentConfig = appConfig,
            onDismiss = { showIpDialog = false },
            onSave = { ip, port ->
                settingsViewModel.updateConfig(ip, port)
                showIpDialog = false
            }
        )
    }
}

@Composable
private fun StudentDataCard(student: Student) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Info Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.size(64.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Column {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = student.student_id,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            
            // Course and Section Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Course",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = student.course,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Year & Section",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${student.year} - ${student.section}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateInformationCard(
    student: Student?,
    onShowUpdateForm: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Update Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = "Update your year level, course, and section information",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            CustomButton(
                text = "Update My Information",
                onClick = onShowUpdateForm,
                enabled = student != null
            )
        }
    }
}

@Composable
private fun ServerConfigurationCard(
    appConfig: com.yourapp.test.myrecordinschool.data.model.AppConfig?,
    onShowIpDialog: () -> Unit,
    onTestConnection: () -> Unit,
    connectionStatus: ConnectionStatus,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Router,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Server Configuration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = "IP Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = appConfig?.baseUrl ?: "Not configured",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onShowIpDialog,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Change IP")
                }
                
                OutlinedButton(
                    onClick = onShowIpDialog,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Change Port")
                }
            }
        }
    }
}

@Composable
private fun QuickActionsCard(
    onTestConnection: () -> Unit,
    isLoading: Boolean,
    connectionStatus: ConnectionStatus
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FlashOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            CustomButton(
                text = if (isLoading) "Testing..." else "Test Database Connection",
                onClick = onTestConnection,
                isLoading = isLoading
            )
            
            // Connection Status Display
            when (connectionStatus) {
                is ConnectionStatus.Success -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = connectionStatus.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                is ConnectionStatus.Failed -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = connectionStatus.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                else -> { /* No status to show */ }
            }
        }
    }
}

@Composable
private fun LogoutCard(onLogout: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateInformationDialog(
    student: Student?,
    onDismiss: () -> Unit,
    onUpdate: (String, String, String) -> Unit
) {
    var selectedYear by remember { mutableStateOf(student?.year ?: "") }
    var selectedCourse by remember { mutableStateOf(student?.course ?: "") }
    var selectedSection by remember { mutableStateOf(student?.section ?: "") }
    
    val availableSections = remember(selectedCourse, selectedYear) {
        DropdownOptions.getSectionsForCourseAndYear(selectedCourse, selectedYear)
    }
    
    // Reset section when course or year changes
    LaunchedEffect(selectedCourse, selectedYear) {
        if (!availableSections.contains(selectedSection)) {
            selectedSection = ""
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update My Information",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Current Information:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                student?.let {
                    Text("Year: ${it.year}")
                    Text("Course: ${it.course}")
                    Text("Section: ${it.section}")
                }
                
                Divider()
                
                Text(
                    text = "Update Form:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                CustomDropdownField(
                    value = selectedYear,
                    onValueChange = { selectedYear = it },
                    label = "Year",
                    options = DropdownOptions.YEARS
                )
                
                CustomDropdownField(
                    value = selectedCourse,
                    onValueChange = { selectedCourse = it },
                    label = "Course/Strand",
                    options = DropdownOptions.COURSES
                )
                
                CustomDropdownField(
                    value = selectedSection,
                    onValueChange = { selectedSection = it },
                    label = "Section",
                    options = availableSections,
                    enabled = availableSections.isNotEmpty()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdate(selectedYear, selectedCourse, selectedSection)
                },
                enabled = selectedYear.isNotBlank() && 
                         selectedCourse.isNotBlank() && 
                         selectedSection.isNotBlank()
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun IpConfigurationDialog(
    currentConfig: com.yourapp.test.myrecordinschool.data.model.AppConfig?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var ipAddress by remember { mutableStateOf(currentConfig?.ipAddress ?: "") }
    var port by remember { mutableStateOf(currentConfig?.port ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "IP Configuration",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = "IP Address",
                    keyboardType = KeyboardType.Number
                )
                
                CustomTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = "Port",
                    keyboardType = KeyboardType.Number
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(ipAddress, port) },
                enabled = ipAddress.isNotBlank() && port.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}