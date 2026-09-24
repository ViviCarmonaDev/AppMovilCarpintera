package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CarpenterRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del "Perfil del taller".
 *
 * Carga los datos del perfil existente (si ya existe) o inicializa
 * el formulario vacío (si es la primera vez).
 * Guarda los datos en Firestore.
 */
class CarpenterProfileViewModel(
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarpenterProfileUiState())
    val uiState: StateFlow<CarpenterProfileUiState> = _uiState.asStateFlow()

    // INICIALIZAR

    // Se llama al abrir la pantalla, con el uid del usuario actual.
    // Carga el perfil si ya existe, o inicializa vacío si es nuevo.
    fun initialize(uid: String) {
        _uiState.update { it.copy(uid = uid, isLoading = true) }

        viewModelScope.launch {
            val result = carpenterRepository.getCarpenterProfileOnce(uid)

            if (result.isSuccess) {
                val existingProfile = result.getOrNull()

                if (existingProfile != null) {
                    // Ya existe un perfil → cargar datos + modo edición
                    _uiState.update {
                        it.copy(
                            uid = uid,
                            nombreTaller = existingProfile.nombreTaller,
                            ruc = existingProfile.ruc,
                            descripcion = existingProfile.descripcion,
                            direccionTaller = existingProfile.direccionTaller,
                            telefonoTaller = existingProfile.telefonoTaller,
                            aniosExperiencia = if (existingProfile.aniosExperiencia > 0)
                                existingProfile.aniosExperiencia.toString() else "",
                            skills = existingProfile.skills,
                            isEditMode = true,
                            isLoading = false
                        )
                    }
                } else {
                    // No existe → formulario vacío
                    _uiState.update {
                        it.copy(uid = uid, isLoading = false, isEditMode = false)
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar el perfil"
                    )
                }
            }
        }
    }

    // EVENTOS DE EDICIÓN

    fun onNombreTallerChange(value: String) {
        _uiState.update {
            it.copy(nombreTaller = value, nombreTallerTouched = true, errorMessage = null)
        }
    }

    fun onRucChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(11)
        _uiState.update {
            it.copy(ruc = filtered, rucTouched = true, errorMessage = null)
        }
    }

    fun onDescripcionChange(value: String) {
        _uiState.update {
            it.copy(descripcion = value, descripcionTouched = true, errorMessage = null)
        }
    }

    fun onDireccionTallerChange(value: String) {
        _uiState.update {
            it.copy(direccionTaller = value, direccionTallerTouched = true, errorMessage = null)
        }
    }

    fun onTelefonoTallerChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(9)
        _uiState.update {
            it.copy(telefonoTaller = filtered, telefonoTallerTouched = true, errorMessage = null)
        }
    }

    fun onAniosExperienciaChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(2)
        _uiState.update {
            it.copy(
                aniosExperiencia = filtered,
                aniosExperienciaTouched = true,
                errorMessage = null
            )
        }
    }

    fun onSkillToggle(skill: String) {
        _uiState.update { state ->
            val newSkills = if (skill in state.skills) {
                state.skills - skill
            } else {
                state.skills + skill
            }
            state.copy(skills = newSkills, skillsTouched = true, errorMessage = null)
        }
    }

    // GUARDAR

    fun saveProfile() {
        val state = _uiState.value

        // Marcar todo como tocado para mostrar errores
        _uiState.update {
            it.copy(
                nombreTallerTouched = true,
                rucTouched = true,
                descripcionTouched = true,
                direccionTallerTouched = true,
                telefonoTallerTouched = true,
                aniosExperienciaTouched = true,
                skillsTouched = true
            )
        }

        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Revisa los datos ingresados") }
            return
        }

        if (state.uid.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Error: usuario no identificado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val profile = CarpenterProfile(
                uid = state.uid,
                nombreTaller = state.nombreTaller.trim(),
                ruc = state.ruc.trim(),
                descripcion = state.descripcion.trim(),
                direccionTaller = state.direccionTaller.trim(),
                telefonoTaller = state.telefonoTaller.trim(),
                aniosExperiencia = state.aniosExperiencia.toIntOrNull() ?: 0,
                skills = state.skills,
                profileCompleted = true
            )

            val result = carpenterRepository.saveCarpenterProfile(profile)

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

    // RESET

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    // MAPEO DE ERRORES

    private fun mapErrorToMessage(exception: Throwable): String {
        val message = exception.message ?: return "Error desconocido"
        return when {
            message.contains("network", ignoreCase = true) ->
                "Sin conexión. Revisa tu internet"

            message.contains("PERMISSION_DENIED", ignoreCase = true) ->
                "No tienes permiso para guardar el perfil"

            else -> message
        }
    }
}