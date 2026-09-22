package com.vivicarmonadev.appmovil_carpinteria.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla "Editar perfil".
 *
 * Carga los datos del usuario actual, permite editarlos y guardarlos
 * en Firestore. Cuando el guardado es exitoso, marca `isSuccess = true`
 * para que la pantalla navegue hacia atrás.
 */
class EditProfileViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    // Guardamos el uid del usuario actual para usarlo al guardar
    private var currentUid: String = ""

    // ============================================
    // CARGAR DATOS INICIALES
    // ============================================
    // Se llama una sola vez al abrir la pantalla, con el usuario actual.
    fun initialize(user: User) {
        currentUid = user.uid
        _uiState.update {
            it.copy(
                originalNombres = user.nombres,
                originalApellidos = user.apellidos,
                originalTelefono = user.telefono,
                nombres = user.nombres,
                apellidos = user.apellidos,
                telefono = user.telefono
            )
        }
    }

    // ============================================
    // EVENTOS DE EDICIÓN
    // ============================================
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
        // Solo dígitos, máximo 9 caracteres
        val filtered = value.filter { it.isDigit() }.take(9)
        _uiState.update {
            it.copy(telefono = filtered, telefonoTouched = true, errorMessage = null)
        }
    }

    // ============================================
    // GUARDAR CAMBIOS
    // ============================================
    fun saveChanges() {
        val state = _uiState.value

        // Marcar todos los campos como tocados para mostrar errores si hay
        _uiState.update {
            it.copy(
                nombresTouched = true,
                apellidosTouched = true,
                telefonoTouched = true
            )
        }

        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Revisa los datos ingresados") }
            return
        }

        if (!state.hasChanges) {
            _uiState.update { it.copy(errorMessage = "No hay cambios para guardar") }
            return
        }

        if (currentUid.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Error: usuario no identificado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.updateUser(
                uid = currentUid,
                nombres = state.nombres.trim(),
                apellidos = state.apellidos.trim(),
                telefono = state.telefono.trim()
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
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

    // ============================================
    // RESET (por si el usuario vuelve a entrar)
    // ============================================
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    // ============================================
    // MAPEO DE ERRORES
    // ============================================
    private fun mapErrorToMessage(exception: Throwable): String {
        val message = exception.message ?: return "Error desconocido"
        return when {
            message.contains("network", ignoreCase = true) ->
                "Sin conexión. Revisa tu internet"

            message.contains("PERMISSION_DENIED", ignoreCase = true) ->
                "No tienes permiso para actualizar este perfil"

            else -> message
        }
    }
}