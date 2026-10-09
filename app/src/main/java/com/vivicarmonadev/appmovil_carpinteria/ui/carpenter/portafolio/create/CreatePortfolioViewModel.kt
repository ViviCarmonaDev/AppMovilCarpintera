package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.compareTo

/**
 * ViewModel para CREAR un trabajo nuevo del portafolio.
 * Formulario vacío. Al guardar, crea un nuevo item en Firestore.
 */
class CreatePortfolioViewModel(
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePortfolioUiState())
    val uiState: StateFlow<CreatePortfolioUiState> = _uiState.asStateFlow()

    // Guardamos el uid del usuario actual
    private var currentUid: String = ""

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    currentUid = user.uid
                }
            }
        }
    }

    // ---- EVENTOS DE EDICIÓN ----

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

    fun onAnchoChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update { it.copy(anchoCm = filtered, anchoTouched = true, errorMessage = null) }
    }

    fun onAltoChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update { it.copy(altoCm = filtered, altoTouched = true, errorMessage = null) }
    }

    fun onProfundidadChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update { it.copy(profundidadCm = filtered, profundidadTouched = true, errorMessage = null) }
    }

    private fun filtrarNumero(value: String): String {
        val filtered = value.filter { it.isDigit() || it == '.' }
        return if (filtered.count { it == '.' } > 1) {
            filtered.substring(0, filtered.lastIndexOf('.'))
        } else {
            filtered
        }
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

    fun onTipoPrecioChange(tipo: TipoPrecio) {
        _uiState.update {
            it.copy(tipoPrecio = tipo, errorMessage = null)
        }
    }

    // ---- GUARDAR ----
    fun save() {
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

        val state = _uiState.value

        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Revisa los datos ingresados") }
            return
        }

        if (currentUid.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Error: usuario no identificado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val precioDouble = state.precioReferencial.toDoubleOrNull()

            val item = PortfolioItem(
                id = "",                              // Firestore genera el ID
                uid = currentUid,
                titulo = state.titulo.trim(),
                descripcion = state.descripcion.trim(),
                categoria = state.categoria.trim(),
                material = state.material.trim(),
                anchoCm = state.anchoCm.toDoubleOrNull(),
                altoCm = state.altoCm.toDoubleOrNull(),
                profundidadCm = state.profundidadCm.toDoubleOrNull(),
                precioReferencial = precioDouble,
                tipoPrecio = state.tipoPrecio?: TipoPrecio.A_TRATAR,
            )

            val result = portfolioRepository.createPortfolioItem(item)

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

    // ---- DESCARTAR CAMBIOS ----

    fun onBackClick(): Boolean {
        val state = _uiState.value
        return if (state.hasChanges) {
            _uiState.update { it.copy(showDiscardDialog = true) }
            false
        } else {
            true
        }
    }

    fun onDiscardCancel() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun onDiscardConfirm() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    // ---- UTILIDADES ----

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
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

    // IMÁGENES

    fun onAgregarImagen(url: String) {
        _uiState.update { state ->
            if (state.imagenesUrls.size >= 3) state
            else state.copy(imagenesUrls = state.imagenesUrls + url)
        }
    }

    fun onEliminarImagen(index: Int) {
        _uiState.update { state ->
            val nuevas = state.imagenesUrls.toMutableList().apply {
                if (index in indices) removeAt(index)
            }
            state.copy(imagenesUrls = nuevas)
        }
    }
}