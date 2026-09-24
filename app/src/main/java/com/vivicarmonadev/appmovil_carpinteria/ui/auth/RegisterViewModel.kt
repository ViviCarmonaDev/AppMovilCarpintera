package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // ---- PASO 1 ----
    fun onRoleSelected(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun onNombresChange(value: String) {
        _uiState.update {
            it.copy(nombres = value, nombresTouched = true)
        }
    }

    fun onApellidosChange(value: String) {
        _uiState.update {
            it.copy(apellidos = value, apellidosTouched = true)
        }
    }

    fun onTelefonoChange(value: String) {
        // Solo permitir dígitos, máximo 9 caracteres
        val filtered = value.filter { it.isDigit() }.take(9)
        _uiState.update {
            it.copy(telefono = filtered, telefonoTouched = true)
        }
    }

    fun ondireccionChange(value: String) {
        _uiState.update {
            it.copy(direccion = value, direccionTouched = true)
        }
    }

    // ---- PASO 2 ----
    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(email = value.trim(), emailTouched = true, errorMessage = null)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(password = value, passwordTouched = true)
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(confirmPassword = value, confirmPasswordTouched = true)
        }
    }

    // ---- ACCIÓN ----
    fun register() {
        val state = _uiState.value

        // Marcar todos los campos como tocados para que aparezcan los errores
        _uiState.update {
            it.copy(
                nombresTouched = true,
                apellidosTouched = true,
                telefonoTouched = true,
                direccionTouched = true,
                emailTouched = true,
                passwordTouched = true,
                confirmPasswordTouched = true
            )
        }

        if (!state.isStep1Valid) {
            _uiState.update { it.copy(errorMessage = "Revisa los datos básicos") }
            return
        }
        if (!state.isStep2Valid) {
            _uiState.update { it.copy(errorMessage = "Revisa las credenciales") }
            return
        }
        if (state.selectedRole == null) {
            _uiState.update { it.copy(errorMessage = "Selecciona un rol") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.register(
                nombres = state.nombres.trim(),
                apellidos = state.apellidos.trim(),
                telefono = state.telefono.trim(),
                direccion = state.direccion.trim(),
                email = state.email.trim(),
                password = state.password,
                role = state.selectedRole
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
            message.contains("email address is already in use", ignoreCase = true) ->
                "Ese correo ya está registrado"
            message.contains("badly formatted", ignoreCase = true) ->
                "El correo no tiene un formato válido"
            message.contains("network", ignoreCase = true) ->
                "Sin conexión. Revisa tu internet"
            else -> message
        }
    }
}