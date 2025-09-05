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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourapp.test.myrecordinschool.data.model.DropdownOptions
import com.yourapp.test.myrecordinschool.data.model.RegisterRequest
import com.yourapp.test.myrecordinschool.ui.components.CustomButton
import com.yourapp.test.myrecordinschool.ui.components.CustomDropdownField
import com.yourapp.test.myrecordinschool.ui.components.CustomTextField
import com.yourapp.test.myrecordinschool.ui.theme.*
import com.yourapp.test.myrecordinschool.viewmodel.AuthViewModel
import com.yourapp.test.myrecordinschool.viewmodel.RfidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onNavigateToSettings: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var isLoginMode by remember { mutableStateOf(true) }
    val isLoading by authViewModel.isLoading.observeAsState(false)
    val errorMessage by authViewModel.errorMessage.observeAsState("")
    val successMessage by authViewModel.successMessage.observeAsState("")
    val registrationSuccess by authViewModel.registrationSuccess.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle registration success - switch to login mode
    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            isLoginMode = true
            authViewModel.clearRegistrationSuccess()
        }
    }
    
    // Show error/success snackbar
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Long
            )
            authViewModel.clearError()
        }
    }
    
    // Show success message
    LaunchedEffect(successMessage) {
        if (successMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = successMessage,
                duration = SnackbarDuration.Long
            )
            authViewModel.clearError()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "My Record in School",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Student Portal",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Main Content Card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Tab Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TabButton(
                            text = "Login",
                            isSelected = isLoginMode,
                            onClick = { isLoginMode = true },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "Register",
                            isSelected = !isLoginMode,
                            onClick = { isLoginMode = false },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Content
                    if (isLoginMode) {
                        LoginForm(
                            authViewModel = authViewModel,
                            isLoading = isLoading
                        )
                    } else {
                        RegisterForm(
                            authViewModel = authViewModel,
                            isLoading = isLoading,
                            onSwitchToLogin = { isLoginMode = true }
                        )
                    }
                }
            }
        }
        
        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = if (data.visuals.message.contains("successful", ignoreCase = true) || 
                                   data.visuals.message.contains("welcome", ignoreCase = true)) {
                    Color(0xFF4CAF50) // Green for success
                } else {
                    MaterialTheme.colorScheme.error // Red for errors
                },
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun LoginForm(
    authViewModel: AuthViewModel,
    isLoading: Boolean
) {
    var studentName by remember { mutableStateOf("") }
    var studentNumber by remember { mutableStateOf("") }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Sign in to access your school records",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        CustomTextField(
            value = studentName,
            onValueChange = { studentName = it },
            label = "Student Full Name",
            leadingIcon = Icons.Filled.Person,
            imeAction = ImeAction.Next
        )
        
        CustomTextField(
            value = studentNumber,
            onValueChange = { studentNumber = it },
            label = "Student Number",
            leadingIcon = Icons.Filled.Badge,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            onImeAction = {
                if (studentName.isNotBlank() && studentNumber.isNotBlank()) {
                    authViewModel.login(studentNumber, studentNumber) // Using student number as password
                }
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        CustomButton(
            text = "Login",
            onClick = {
                if (studentName.isNotBlank() && studentNumber.isNotBlank()) {
                    authViewModel.login(studentNumber, studentNumber)
                }
            },
            isLoading = isLoading,
            enabled = studentName.isNotBlank() && studentNumber.isNotBlank()
        )
    }
}

@Composable
private fun RegisterForm(
    authViewModel: AuthViewModel,
    isLoading: Boolean,
    onSwitchToLogin: () -> Unit
) {
    var studentName by remember { mutableStateOf("") }
    var studentNumber by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf("") }
    
    // RFID ViewModel
    val rfidViewModel: RfidViewModel = viewModel()
    val rfidNumber by rfidViewModel.rfidNumber.observeAsState("")
    val rfidLoading by rfidViewModel.isLoading.observeAsState(false)
    val rfidError by rfidViewModel.errorMessage.observeAsState("")
    
    val availableSections = remember(selectedCourse, selectedYear) {
        DropdownOptions.getSectionsForCourseAndYear(selectedCourse, selectedYear)
    }
    
    // Reset section when course or year changes
    LaunchedEffect(selectedCourse, selectedYear) {
        if (!availableSections.contains(selectedSection)) {
            selectedSection = ""
        }
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Register to access your school portal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        CustomTextField(
            value = studentName,
            onValueChange = { studentName = it },
            label = "Student Full Name",
            leadingIcon = Icons.Filled.Person,
            imeAction = ImeAction.Next
        )
        
        CustomTextField(
            value = studentNumber,
            onValueChange = { studentNumber = it },
            label = "Student Number",
            leadingIcon = Icons.Filled.Badge,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
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
        
        // RFID Field with Refresh Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTextField(
                value = rfidNumber,
                onValueChange = { /* Read only field */ },
                label = "RFID Number",
                leadingIcon = Icons.Filled.CreditCard,
                modifier = Modifier.weight(1f),
                enabled = false,
                placeholder = "Tap refresh to scan RFID"
            )
            
            IconButton(
                onClick = { 
                    rfidViewModel.clearError()
                    rfidViewModel.fetchLatestRfid()
                },
                enabled = !rfidLoading
            ) {
                if (rfidLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh RFID",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // RFID Error Message
        if (rfidError.isNotEmpty()) {
            Text(
                text = rfidError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        CustomButton(
            text = "Register",
            onClick = {
                val request = RegisterRequest(
                    student_id = studentNumber,
                    name = studentName,
                    password = studentNumber, // Using student number as password
                    year = selectedYear,
                    course = selectedCourse,
                    section = selectedSection,
                    rfid = rfidNumber
                )
                authViewModel.register(request)
            
            },
            isLoading = isLoading,
            enabled = studentName.isNotBlank() && 
                     studentNumber.isNotBlank() && 
                     selectedYear.isNotBlank() && 
                     selectedCourse.isNotBlank() && 
                     selectedSection.isNotBlank() &&
                     rfidNumber.isNotBlank()
        )
        
        TextButton(
            onClick = onSwitchToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account? Login",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}