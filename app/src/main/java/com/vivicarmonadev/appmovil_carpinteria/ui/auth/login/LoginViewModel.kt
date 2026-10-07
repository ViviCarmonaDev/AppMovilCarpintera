package com.vivicarmonadev.appmovil_carpinteria.ui.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.remote.auth.GoogleSignInHelper
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Usuario recién logueado (para que AppNavigation decida el destino)
    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    // ---- EVENTOS ----
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value.trim(), emailTouched = true, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordTouched = true, errorMessage = null) }
    }

    // ---- LOGIN CON EMAIL ----
    fun login() {
        val state = _uiState.value

        _uiState.update {
            it.copy(emailTouched = true, passwordTouched = true)
        }

        if (!state.isEmailValid) {
            _uiState.update { it.copy(errorMessage = "El correo no es válido") }
            return
        }
        if (!state.isPasswordValid) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.login(
                email = state.email.trim(),
                password = state.password
            )

            result.fold(
                onSuccess = { user ->
                    _loggedInUser.value = user       // ← NUEVO
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

    // ---- LOGIN CON GOOGLE ----
    fun loginWithGoogle(
        context: Context,
        serverClientId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val helper = GoogleSignInHelper(context, serverClientId)
                val idToken = helper.getGoogleIdToken()
                val result = authRepository.loginWithGoogle(idToken)

                result.fold(
                    onSuccess = { user ->
                        _loggedInUser.value = user       // ← NUEVO
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

            } catch (e: Exception) {
                val message = when {
                    e.message?.contains("canceled", ignoreCase = true) == true ||
                            e.message?.contains("cancel", ignoreCase = true) == true ->
                        null
                    else -> mapErrorToMessage(e)
                }

                _uiState.update {
                    it.copy(isLoading = false, isSuccess = false, errorMessage = message)
                }
            }
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