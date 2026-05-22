package com.jaime.rede_social_esportiva.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaime.rede_social_esportiva.data.remote.ApiClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    
    private val apiClient = ApiClient()
    
    var uiState by mutableStateOf(RegisterUiState())
        private set
    
    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username, error = null)
    }
    
    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, error = null)
    }
    
    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }
    
    fun onConfirmPasswordChange(confirmPassword: String) {
        uiState = uiState.copy(confirmPassword = confirmPassword, error = null)
    }
    
    fun register(onSuccess: () -> Unit) {
        val username = uiState.username.trim()
        val email = uiState.email.trim()
        val password = uiState.password
        val confirmPassword = uiState.confirmPassword
        
        // Validação
        when {
            username.isEmpty() -> {
                uiState = uiState.copy(error = "Digite um nome de usuário")
                return
            }
            email.isEmpty() -> {
                uiState = uiState.copy(error = "Digite seu e-mail")
                return
            }
            password.isEmpty() -> {
                uiState = uiState.copy(error = "Digite uma senha")
                return
            }
            password.length < 6 -> {
                uiState = uiState.copy(error = "A senha deve ter pelo menos 6 caracteres")
                return
            }
            password != confirmPassword -> {
                uiState = uiState.copy(error = "As senhas não coincidem")
                return
            }
        }
        
        uiState = uiState.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val response = apiClient.register(email, password, username)
                // TODO: Salvar tokens no DataStore
                uiState = uiState.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao criar conta. Tente novamente."
                )
            }
        }
    }
}

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
