package com.jaime.rede_social_esportiva.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaime.rede_social_esportiva.data.remote.ApiClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    
    private val apiClient = ApiClient()
    
    var uiState by mutableStateOf(LoginUiState())
        private set
    
    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, error = null)
    }
    
    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }
    
    fun login(onSuccess: () -> Unit) {
        val email = uiState.email.trim()
        val password = uiState.password
        
        // Validação
        when {
            email.isEmpty() -> {
                uiState = uiState.copy(error = "Digite seu e-mail")
                return
            }
            password.isEmpty() -> {
                uiState = uiState.copy(error = "Digite sua senha")
                return
            }
        }
        
        uiState = uiState.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val response = apiClient.login(email, password)
                // TODO: Salvar tokens no DataStore
                uiState = uiState.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao fazer login. Verifique suas credenciais."
                )
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
