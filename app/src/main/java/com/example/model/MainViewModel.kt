package com.example.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AppState {
    object Splash : AppState()
    object Auth : AppState()
    object Onboarding : AppState()
    object Home : AppState()
}

class MainViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository()
    
    private val auth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        null
    }

    private val _appState = MutableStateFlow<AppState>(AppState.Splash)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentPlan = MutableStateFlow<DietPlan?>(null)
    val currentPlan: StateFlow<DietPlan?> = _currentPlan.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()
    
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        checkAuth()
    }

    fun toggleTheme() {
        _isDarkMode.value = if (_isDarkMode.value == true) false else true
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun checkAuth() {
        val user = auth?.currentUser
        if (user != null) {
            viewModelScope.launch {
                val dbUser = firestoreRepository.getUser(user.uid)
                if (dbUser != null) {
                    _currentUser.value = dbUser
                    val dbPlan = firestoreRepository.getDietPlan(user.uid)
                    _currentPlan.value = dbPlan
                    _appState.value = AppState.Home
                } else {
                    _currentUser.value = User(id = user.uid, email = user.email ?: "")
                    _appState.value = AppState.Onboarding
                }
            }
        } else {
            _appState.value = AppState.Auth
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            try {
                auth?.signInWithEmailAndPassword(email, pass)?.await()
                checkAuth()
            } catch (e: Exception) {
                _authError.value = e.localizedMessage ?: "Login failed"
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String, name: String) {
        viewModelScope.launch {
            try {
                val result = auth?.createUserWithEmailAndPassword(email, pass)?.await()
                val uid = result?.user?.uid
                if (uid != null) {
                    val user = User(id = uid, name = name, email = email)
                    firestoreRepository.saveUser(user)
                    checkAuth()
                }
            } catch (e: Exception) {
                _authError.value = e.localizedMessage ?: "Registration failed"
            }
        }
    }

    fun skipAuthForMock() {
        val mockUser = User(id = "mock_user", name = "Aanya", email = "demo@example.com")
        _currentUser.value = mockUser
        _appState.value = AppState.Onboarding
    }

    fun saveMetrics(name: String, age: Int, heightCm: Int, weightKg: Float) {
        val user = _currentUser.value ?: return
        val updatedUser = user.copy(name = name, age = age, heightCm = heightCm, weightKg = weightKg)
        _currentUser.value = updatedUser
        
        val plan = DietEngine.generatePlan(updatedUser.id, weightKg)
        _currentPlan.value = plan
        
        viewModelScope.launch {
            firestoreRepository.saveUser(updatedUser)
            firestoreRepository.saveDietPlan(plan)
            _appState.value = AppState.Home
        }
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
        _currentPlan.value = null
        _appState.value = AppState.Auth
    }
}
