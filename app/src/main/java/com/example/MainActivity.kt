package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppState
import com.example.model.MainViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            
            // If isDarkMode is null, use system default. Otherwise use user preference.
            val useDarkTheme = isDarkMode ?: androidx.compose.foundation.isSystemInDarkTheme()
            
            MyApplicationTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val appState by viewModel.appState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val authError by viewModel.authError.collectAsState()

    when (appState) {
        is AppState.Splash -> {
            SplashScreen(onSplashComplete = {
                viewModel.checkAuth()
            })
        }
        is AppState.Auth -> {
            AuthScreen(
                authError = authError,
                onClearError = { viewModel.clearAuthError() },
                onSignIn = { email, pass -> viewModel.signInWithEmail(email, pass) },
                onSignUp = { email, pass, name -> viewModel.signUpWithEmail(email, pass, name) },
                onSkipAuth = { viewModel.skipAuthForMock() }
            )
        }
        is AppState.Onboarding -> {
            OnboardingScreen(
                initialName = currentUser?.name ?: "",
                onGeneratePlan = { name, age, height, weight ->
                    viewModel.saveMetrics(name, age, height, weight)
                }
            )
        }
        is AppState.Home -> {
            if (currentUser != null && currentPlan != null) {
                com.example.ui.components.MainLayout(
                    user = currentUser!!,
                    title = "My Diet Plan",
                    onSignOut = { viewModel.signOut() },
                    onRecalculate = { viewModel.skipAuthForMock() },
                    onToggleTheme = { viewModel.toggleTheme() }
                ) { paddingValues ->
                    HomeScreen(
                        user = currentUser!!,
                        plan = currentPlan!!,
                        onRecalculate = { viewModel.skipAuthForMock() },
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}
