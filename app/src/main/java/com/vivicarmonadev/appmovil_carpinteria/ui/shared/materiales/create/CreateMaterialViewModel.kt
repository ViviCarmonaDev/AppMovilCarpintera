package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.remote.cloudinary.CloudinaryDataSource
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.MaterialRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Material
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateMaterialViewModel(
    private val materialRepository: MaterialRepositoryImpl = MaterialRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val cloudinaryDataSource: CloudinaryDataSource = CloudinaryDataSource()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMaterialUiState())
    val uiState: StateFlow<CreateMaterialUiState> = _uiState.asStateFlow()

    private var currentUid: String = ""

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) currentUid = user.uid
            }
        }
    }

    // ✅ Recibe el categoriaId desde la pantalla anterior
    fun initialize(categoriaId: String) {
        _uiState.update { it.copy(categoriaId = categoriaId) }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, nombreTouched = true, errorMessage = null) }
    }

    fun onDescripcionChange(value: String) {
        _uiState.update { it.copy(descripcion = value, descripcionTouched = true, errorMessage = null) }
    }

    fun onPrecioChange(value: String) {
        // Solo permitir dígitos y un punto decimal
        val filtered = value.filter { it.isDigit() || it == '.' }
        val sanitized = if (filtered.count { it == '.' } > 1) {
            filtered.substring(0, filtered.lastIndexOf('.'))
        } else {
            filtered
        }
        _uiState.update {
            it.copy(precioReferencial = sanitized, precioTouched = true, errorMessage = null)
        }
    }

    fun onImagenChange(url: String?) {
        _uiState.update { it.copy(imagenUrl = url) }
    }

    fun save() {
        val state = _uiState.value

        _uiState.update {
            it.copy(nombreTouched = true, descripcionTouched = true, precioTouched = true)
        }

        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Revisa los datos ingresados") }
            return
        }

        if (currentUid.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Usuario no identificado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val material = Material(
                id = "",
                categoriaId = state.categoriaId,
                nombre = state.nombre.trim(),
                descripcion = state.descripcion.trim(),
                precioReferencial = state.precioReferencial.toDoubleOrNull() ?: 0.0,
                imagenUrl = state.imagenUrl,
                creadoPor = currentUid
            )

            val result = materialRepository.createMaterial(material)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true, errorMessage = null) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Error al crear")
                    }
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