package com.ims.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ims.app.data.network.ApiClient
import com.ims.app.data.network.LoginRequest
import com.ims.app.data.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = ApiClient.authApi.login(LoginRequest(email, pass))
                tokenManager.saveToken(response.token)
                _authState.value = AuthState.Success(response.user.role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        _authState.value = AuthState.Idle
    }
}
