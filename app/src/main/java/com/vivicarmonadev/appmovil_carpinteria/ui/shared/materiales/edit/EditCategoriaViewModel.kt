package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CategoriaMaterialRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CategoriaMaterial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI STATE
data class EditCategoriaUiState(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagenUrl: String? = null,

    // Originales
    val originalNombre: String = "",
    val originalDescripcion: String = "",
    val originalImagenUrl: String? = null,

    // Tocado
    val nombreTouched: Boolean = false,
    val descripcionTouched: Boolean = false,

    // General
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showDiscardDialog: Boolean = false
) {
    val nombreError: String?
        get() = when {
            !nombreTouched -> null
            nombre.isBlank() -> "Ingresa el nombre de la categoría"
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

    val isFormValid: Boolean
        get() = nombre.isNotBlank() && nombreError == null &&
                descripcion.isNotBlank() && descripcionError == null

    val hasChanges: Boolean
        get() = nombre != originalNombre ||
                descripcion != originalDescripcion ||
                imagenUrl != originalImagenUrl
}

// VIEW MODEL
class EditCategoriaViewModel(
    private val categoriaRepository: CategoriaMaterialRepositoryImpl = CategoriaMaterialRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditCategoriaUiState())
    val uiState: StateFlow<EditCategoriaUiState> = _uiState.asStateFlow()

    fun initialize(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = categoriaRepository.getCategoriaById(id)
            result.fold(
                onSuccess = { categoria ->
                    if (categoria != null) {
                        _uiState.update {
                            it.copy(
                                id = categoria.id,
                                nombre = categoria.nombre,
                                descripcion = categoria.descripcion,
                                imagenUrl = categoria.imagenUrl,
                                originalNombre = categoria.nombre,
                                originalDescripcion = categoria.descripcion,
                                originalImagenUrl = categoria.imagenUrl,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Categoría no encontrada") }
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
    fun onImagenChange(url: String?) { _uiState.update { it.copy(imagenUrl = url) } }

    fun save() {
        val state = _uiState.value
        _uiState.update { it.copy(nombreTouched = true, descripcionTouched = true) }

        if (!state.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val categoria = CategoriaMaterial(
                id = state.id,
                nombre = state.nombre.trim(),
                descripcion = state.descripcion.trim(),
                imagenUrl = state.imagenUrl
            )

            // Asumo que tienes este método en tu repositorio
            val result = categoriaRepository.updateCategoria(categoria)
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