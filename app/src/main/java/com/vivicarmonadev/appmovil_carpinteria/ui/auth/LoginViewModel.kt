package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del login.
 *
 * Contiene el estado y la lógica de la pantalla de inicio de sesión.
 * La pantalla solo muestra el estado y dispara eventos al ViewModel.
 */
class LoginViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // ---- EVENTOS ----
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value.trim(), errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    // ---- ACCIÓN: Login ----
    fun login() {
        val state = _uiState.value

        if (!state.isEmailValid) {
            _uiState.update { it.copy(errorMessage = "El correo no es válido") }
            return
        }
        if (!state.isPasswordValid) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.login(
                email = state.email.trim(),
                password = state.password
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = true, errorMessage = null)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = mapErrorToMessage(exception)
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun mapErrorToMessage(exception: Throwable): String {
        val message = exception.message ?: return "Error desconocido"
        return when {
            message.contains("password is invalid", ignoreCase = true) ||
                    message.contains("credential is incorrect", ignoreCase = true) ||
                    message.contains("no user record", ignoreCase = true) ->
                "Correo o contraseña incorrectos"

            message.contains("network", ignoreCase = true) ->
                "Sin conexión. Revisa tu internet"

            message.contains("too many requests", ignoreCase = true) ->
                "Demasiados intentos. Espera un momento"

            else -> message
        }
    }
}