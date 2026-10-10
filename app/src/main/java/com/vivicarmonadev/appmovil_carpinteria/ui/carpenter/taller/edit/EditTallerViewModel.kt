package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.taller.edit

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
 * ViewModel de la pantalla "Editar taller".
 */
class EditTallerViewModel(
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTallerUiState())
    val uiState: StateFlow<EditTallerUiState> = _uiState.asStateFlow()

    // ---- CARGAR DATOS INICIALES ----

    fun initialize(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = carpenterRepository.getCarpenterProfileOnce(uid)

            result.fold(
                onSuccess = { profile ->
                    if (profile == null) {
                        _uiState.update {
                            it.copy(
                                uid = uid,
                                isLoading = false,
                                errorMessage = "No existe un taller para este usuario"
                            )
                        }
                        return@fold
                    }

                    val aniosStr = if (profile.aniosExperiencia > 0)
                        profile.aniosExperiencia.toString() else ""

                    _uiState.update {
                        it.copy(
                            uid = uid,
                            nombreTaller = profile.nombreTaller,
                            ruc = profile.ruc,
                            descripcion = profile.descripcion,
                            direccionTaller = profile.direccionTaller,
                            telefonoTaller = profile.telefonoTaller,
                            aniosExperiencia = aniosStr,
                            skills = profile.skills,
                            isLoading = false,
                            // Originales
                            originalNombreTaller = profile.nombreTaller,
                            originalRuc = profile.ruc,
                            originalDescripcion = profile.descripcion,
                            originalDireccionTaller = profile.direccionTaller,
                            originalTelefonoTaller = profile.telefonoTaller,
                            originalAniosExperiencia = aniosStr,
                            originalSkills = profile.skills
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // ---- EVENTOS ----

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

    // ---- GUARDAR ----

    fun saveChanges() {
        val state = _uiState.value

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

        if (!state.hasChanges) {
            _uiState.update { it.copy(errorMessage = "No hay cambios para guardar") }
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
                            errorMessage = "Error: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // ---- DESCARTAR ----

    fun onBackClick(): Boolean {
        return if (_uiState.value.hasChanges && _uiState.value.isFormValid) {
            _uiState.update { it.copy(showDiscardDialog = true) }
            false
        } else true
    }

    fun onDiscardCancel() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun onDiscardConfirm() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}