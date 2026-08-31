package com.example.ui.screens.auth

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthProvider
import com.example.data.auth.AuthRepository
import com.example.data.auth.AuthResult
import com.example.data.auth.AuthUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: AuthUser? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSignUpMode: Boolean = false,
    val showGoogleConnectDialog: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(currentUser = authRepository.currentUser.value))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
            }
        }
    }

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isSignUpMode = !_uiState.value.isSignUpMode,
            errorMessage = null,
            successMessage = null
        )
    }

    fun showGoogleConnectDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showGoogleConnectDialog = show)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun signInAsGuest(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signInAsGuest()
            _uiState.value = _uiState.value.copy(isLoading = false)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    successMessage = "Welcome! Guest streak is preserved locally."
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = err.message ?: "Failed to sign in as guest."
                )
            }
        }
    }

    fun connectGoogleAccount(email: String, displayName: String? = null, onSuccess: () -> Unit = {}) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid Google email address")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signInWithGoogleEmail(cleanEmail, displayName)
            _uiState.value = _uiState.value.copy(isLoading = false, showGoogleConnectDialog = false)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    successMessage = "Connected as ${user.email}! Streak synchronized."
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = err.localizedMessage ?: "Failed to connect Google account"
                )
            }
        }
    }

    fun signInWithEmail(email: String, pass: String, onSuccess: () -> Unit = {}) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter both email and password")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signInWithEmail(email, pass)
            _uiState.value = _uiState.value.copy(isLoading = false)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    successMessage = "Signed in successfully. Streak synced!"
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = err.localizedMessage ?: "Failed to sign in with email."
                )
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String, name: String, onSuccess: () -> Unit = {}) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter email and password")
            return
        }
        if (pass.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signUpWithEmail(email, pass, name)
            _uiState.value = _uiState.value.copy(isLoading = false)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    successMessage = "Account created! Streak tracking is active."
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = err.localizedMessage ?: "Failed to create account."
                )
            }
        }
    }

    fun signInWithGoogle(context: Context, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.launchGoogleSignIn(context)
            _uiState.value = _uiState.value.copy(isLoading = false)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    successMessage = "Google account linked. Streak synchronized!"
                )
                onSuccess()
            }.onFailure { err ->
                // Prompt user to enter/confirm their real Google email seamlessly
                _uiState.value = _uiState.value.copy(
                    showGoogleConnectDialog = true,
                    errorMessage = null
                )
            }
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            authRepository.signOut()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                currentUser = null,
                successMessage = "Signed out successfully."
            )
            onComplete()
        }
    }
}
