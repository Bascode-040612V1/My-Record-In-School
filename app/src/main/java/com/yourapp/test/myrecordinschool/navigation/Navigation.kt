package com.yourapp.test.myrecordinschool.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourapp.test.myrecordinschool.data.model.Violation
import com.yourapp.test.myrecordinschool.ui.screen.*
import com.yourapp.test.myrecordinschool.viewmodel.AuthState
import com.yourapp.test.myrecordinschool.viewmodel.AuthViewModel

object Screen {
    const val AUTH = "auth"
    const val SETTINGS01 = "settings01"
    const val HOME = "home"
    const val SETTINGS02 = "settings02"
    const val VIOLATION_DETAIL = "violation_detail"
}

@Composable
fun MyRecordInSchoolNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.observeAsState()
    
    // Navigate based on auth state
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                navController.navigate(Screen.AUTH) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Authenticated -> {
                if (navController.currentDestination?.route == Screen.AUTH) {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.AUTH) { inclusive = true }
                    }
                }
            }
            null -> {
                // Handle null auth state - do nothing
            }
        }
    }
    
    // Store violation for detail screen
    var selectedViolation by remember { mutableStateOf<Violation?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.Authenticated) Screen.HOME else Screen.AUTH
    ) {
        composable(Screen.AUTH) {
            AuthScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.SETTINGS01)
                },
                authViewModel = authViewModel
            )
        }
        
        composable(Screen.SETTINGS01) {
            Settings01Screen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.HOME) {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.SETTINGS02)
                },
                onNavigateToViolationDetail = { violation ->
                    selectedViolation = violation
                    navController.navigate(Screen.VIOLATION_DETAIL)
                }
            )
        }
        
        composable(Screen.SETTINGS02) {
            Settings02Screen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        
        composable(Screen.VIOLATION_DETAIL) {
            selectedViolation?.let { violation ->
                ViolationDetailScreen(
                    violation = violation,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}