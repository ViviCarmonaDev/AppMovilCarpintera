package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.MaterialRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Material
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI STATE
data class EditMaterialUiState(
    val id: String = "",
    val categoriaId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precioReferencial: String = "",
    val imagenUrl: String? = null,

    // Originales
    val originalNombre: String = "",
    val originalDescripcion: String = "",
    val originalPrecio: String = "",
    val originalImagenUrl: String? = null,

    // Tocado
    val nombreTouched: Boolean = false,
    val descripcionTouched: Boolean = false,
    val precioTouched: Boolean = false,

    // General
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showDiscardDialog: Boolean = false
) {
    val nombreError: String?
        get() = when {
            !nombreTouched -> null
            nombre.isBlank() -> "Ingresa el nombre del material"
            nombre.trim().length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }

    val descripcionError: String?
        get() = when {
            !descripcionTouched -> null
            descripcion.isBlank() -> "Ingresa una descripción"
            descripcion.trim().length < 10 -> "Mínimo 10 caracteres"
            else -> null
        }

    val precioError: String?
        get() = when {
            !precioTouched -> null
            precioReferencial.isBlank() -> "Ingresa un precio referencial"
            !precioReferencial.matches(Regex("^\\d+(\\.\\d{1,2})?$")) -> "Solo números (ej: 150 o 150.50)"
            (precioReferencial.toDoubleOrNull() ?: 0.0) <= 0 -> "El precio debe ser mayor a 0"
            else -> null
        }

    val isFormValid: Boolean
        get() = nombre.isNotBlank() && nombreError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                precioReferencial.isNotBlank() && precioError == null

    val hasChanges: Boolean
        get() = nombre != originalNombre ||
                descripcion != originalDescripcion ||
                precioReferencial != originalPrecio ||
                imagenUrl != originalImagenUrl
}

// VIEW MODEL
class EditMaterialViewModel(
    private val materialRepository: MaterialRepositoryImpl = MaterialRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditMaterialUiState())
    val uiState: StateFlow<EditMaterialUiState> = _uiState.asStateFlow()

    fun initialize(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = materialRepository.getMaterialById(id)
            result.fold(
                onSuccess = { material ->
                    if (material != null) {
                        val precioStr = if (material.precioReferencial > 0) material.precioReferencial.toString() else ""
                        _uiState.update {
                            it.copy(
                                id = material.id,
                                categoriaId = material.categoriaId,
                                nombre = material.nombre,
                                descripcion = material.descripcion,
                                precioReferencial = precioStr,
                                imagenUrl = material.imagenUrl,
                                originalNombre = material.nombre,
                                originalDescripcion = material.descripcion,
                                originalPrecio = precioStr,
                                originalImagenUrl = material.imagenUrl,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Material no encontrado") }
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun onNombreChange(v: String) { _uiState.update { it.copy(nombre = v, nombreTouched = true) } }
    fun onDescripcionChange(v: String) { _uiState.update { it.copy(descripcion = v, descripcionTouched = true) } }
    fun onPrecioChange(v: String) {
        val filtered = v.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(precioReferencial = filtered, precioTouched = true) }
    }
    fun onImagenChange(url: String?) { _uiState.update { it.copy(imagenUrl = url) } }

    fun save() {
        val state = _uiState.value
        _uiState.update { it.copy(nombreTouched = true, descripcionTouched = true, precioTouched = true) }

        if (!state.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val material = Material(
                id = state.id,
                categoriaId = state.categoriaId,
                nombre = state.nombre.trim(),
                descripcion = state.descripcion.trim(),
                precioReferencial = state.precioReferencial.toDoubleOrNull() ?: 0.0,
                imagenUrl = state.imagenUrl
            )

            val result = materialRepository.updateMaterial(material)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun onBackClick(): Boolean {
        return if (_uiState.value.hasChanges) {
            _uiState.update { it.copy(showDiscardDialog = true) }
            false
        } else true
    }

    fun onDiscardConfirm() { _uiState.update { it.copy(showDiscardDialog = false) } }
    fun onDiscardCancel() { _uiState.update { it.copy(showDiscardDialog = false) } }
    fun resetSuccess() { _uiState.update { it.copy(isSuccess = false) } }
}