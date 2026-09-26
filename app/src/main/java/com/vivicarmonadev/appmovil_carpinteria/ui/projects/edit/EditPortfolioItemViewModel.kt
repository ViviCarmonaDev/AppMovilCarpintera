package com.vivicarmonadev.appmovil_carpinteria.ui.projects.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio

/**
 * ViewModel de "Crear/Editar trabajo".
 *
 * Maneja dos casos:
 *  - Crear: formulario vacío. Al guardar, crea nuevo item.
 *  - Editar: formulario pre-llenado. Al guardar, actualiza el item.
 *
 * El flag `isEditMode` indica en qué modo está.
 */
class EditPortfolioItemViewModel(
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPortfolioItemUiState())
    val uiState: StateFlow<EditPortfolioItemUiState> = _uiState.asStateFlow()

    // INICIALIZAR — CREAR (formulario vacío)

    fun initializeCreate() {
        viewModelScope.launch {
            // Necesitamos el uid del usuario actual como dueño
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            uid = user.uid,
                            isEditMode = false,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    // INICIALIZAR — EDITAR (pre-llenado)

    fun initializeEdit(itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = portfolioRepository.getPortfolioItemById(itemId)

            result.fold(
                onSuccess = { item ->
                    if (item == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "El trabajo no existe"
                            )
                        }
                        return@fold
                    }

                    val precioStr = item.precioReferencial?.let { formatPrecio(it) } ?: ""

                    _uiState.update {
                        it.copy(
                            id = item.id,
                            uid = item.uid,
                            titulo = item.titulo,
                            descripcion = item.descripcion,
                            categoria = item.categoria,
                            material = item.material,
                            precioReferencial = precioStr,
                            tipoPrecio = item.tipoPrecio,
                            fotoUrl1 = item.fotoUrl1,
                            fotoUrl2 = item.fotoUrl2,
                            isEditMode = true,
                            isLoading = false,
                            // Guardar originales para detectar cambios
                            originalTitulo = item.titulo,
                            originalDescripcion = item.descripcion,
                            originalCategoria = item.categoria,
                            originalMaterial = item.material,
                            originalPrecio = precioStr,
                            originalTipoPrecio = item.tipoPrecio
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

    // EVENTOS DE EDICIÓN

    fun onTituloChange(value: String) {
        _uiState.update {
            it.copy(titulo = value, tituloTouched = true, errorMessage = null)
        }
    }

    fun onDescripcionChange(value: String) {
        _uiState.update {
            it.copy(descripcion = value, descripcionTouched = true, errorMessage = null)
        }
    }

    fun onCategoriaChange(value: String) {
        _uiState.update {
            it.copy(categoria = value, categoriaTouched = true, errorMessage = null)
        }
    }

    fun onMaterialChange(value: String) {
        _uiState.update {
            it.copy(material = value, materialTouched = true, errorMessage = null)
        }
    }

    fun onPrecioChange(value: String) {
        // Solo permitir dígitos y un punto decimal
        val filtered = value.filter { it.isDigit() || it == '.' }
        // Solo un punto decimal
        val sanitized = if (filtered.count { it == '.' } > 1) {
            filtered.substring(0, filtered.lastIndexOf('.'))
        } else {
            filtered
        }
        _uiState.update {
            it.copy(precioReferencial = sanitized, precioTouched = true, errorMessage = null)
        }
    }

    fun onTipoPrecioChange(tipo: TipoPrecio) {
        _uiState.update {
            it.copy(tipoPrecio = tipo, tipoPrecioTouched = true, errorMessage = null)
        }
    }

    // GUARDAR (crear o actualizar)

    fun save() {
        val state = _uiState.value

        // Marcar todos los campos como tocados
        _uiState.update {
            it.copy(
                tituloTouched = true,
                descripcionTouched = true,
                categoriaTouched = true,
                materialTouched = true,
                precioTouched = true
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

            val precioDouble = state.precioReferencial.toDoubleOrNull()

            val item = PortfolioItem(
                id = state.id,                       // vacío si es nuevo
                uid = state.uid,
                titulo = state.titulo.trim(),
                descripcion = state.descripcion.trim(),
                categoria = state.categoria.trim(),
                material = state.material.trim(),
                precioReferencial = precioDouble,
                tipoPrecio = state.tipoPrecio,
                fotoUrl1 = state.fotoUrl1,
                fotoUrl2 = state.fotoUrl2
            )

            val result = if (state.isEditMode) {
                portfolioRepository.updatePortfolioItem(item)
            } else {
                portfolioRepository.createPortfolioItem(item)
            }

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

    // DESCARTAR CAMBIOS

    fun onBackClick(): Boolean {
        val state = _uiState.value
        return if (state.hasChanges && state.isFormValid) {
            // Hay cambios sin guardar → mostrar diálogo
            _uiState.update { it.copy(showDiscardDialog = true) }
            false
        } else {
            // Sin cambios → puede salir directo
            true
        }
    }

    fun onDiscardCancel() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun onDiscardConfirm() {
        _uiState.update { it.copy(showDiscardDialog = false) }
        // El Screen llama a onBack después
    }

    // UTILIDADES

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    private fun formatPrecio(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            "%.2f".format(value)
        }
    }

    private fun mapErrorToMessage(exception: Throwable): String {
        val message = exception.message ?: return "Error desconocido"
        return when {
            message.contains("network", ignoreCase = true) ->
                "Sin conexión. Revisa tu internet"
            message.contains("PERMISSION_DENIED", ignoreCase = true) ->
                "No tienes permiso para realizar esta acción"
            else -> message
        }
    }
}