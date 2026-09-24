package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de "Completar perfil".
 *
 * Se usa cuando un usuario entra por Google y necesita completar
 * los datos faltantes: rol, teléfono y dirección.
 *
 * Los nombres, apellidos, email y foto vienen pre-llenados desde Google.
 */
class CompleteProfileViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState: StateFlow<CompleteProfileUiState> = _uiState.asStateFlow()

    // INICIALIZAR CON DATOS DEL USUARIO ACTUAL

    fun initialize(user: User) {
        _uiState.update {
            it.copy(
                uid = user.uid,
                nombres = user.nombres,
                apellidos = user.apellidos,
                email = user.email,
                photoUrl = user.photoUrl
            )
        }
    }

    // EVENTOS DE EDICIÓN

    fun onRoleSelected(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role, roleTouched = true) }
    }

    fun onNombresChange(value: String) {
        _uiState.update {
            it.copy(nombres = value, nombresTouched = true, errorMessage = null)
        }
    }

    fun onApellidosChange(value: String) {
        _uiState.update {
            it.copy(apellidos = value, apellidosTouched = true, errorMessage = null)
        }
    }

    fun onTelefonoChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(9)
        _uiState.update {
            it.copy(telefono = filtered, telefonoTouched = true, errorMessage = null)
        }
    }

    fun onDireccionChange(value: String) {
        _uiState.update {
            it.copy(direccion = value, direccionTouched = true, errorMessage = null)
        }
    }

    // GUARDAR PERFIL COMPLETO

    fun saveProfile() {
        val state = _uiState.value

        // Marcar todo como tocado para mostrar errores
        _uiState.update {
            it.copy(
                nombresTouched = true,
                apellidosTouched = true,
                telefonoTouched = true,
                direccionTouched = true,
                roleTouched = true
            )
        }

        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos") }
            return
        }

        if (state.selectedRole == null) {
            _uiState.update { it.copy(errorMessage = "Selecciona un rol") }
            return
        }

        if (state.uid.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Error: usuario no identificado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.completeProfile(
                uid = state.uid,
                nombres = state.nombres.trim(),
                apellidos = state.apellidos.trim(),
                telefono = state.telefono.trim(),
                direccion = state.direccion.trim(),
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

    // MAPEO DE ERRORES

    private fun mapErrorToMessage(exception: Throwable): String {
        val message = exception.message ?: return "Error desconocido"
        return when {
            message.contains("network", ignoreCase = true) ->
                "Sin conexión. Revisa tu internet"

            message.contains("PERMISSION_DENIED", ignoreCase = true) ->
                "No tienes permiso para actualizar"

            else -> message
        }
    }
}