package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.remote.cloudinary.CloudinaryDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class EditPortfolioViewModel(
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val cloudinaryDataSource: CloudinaryDataSource = CloudinaryDataSource() // ✅ NUEVO
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPortfolioUiState())
    val uiState: StateFlow<EditPortfolioUiState> = _uiState.asStateFlow()

    // INICIALIZAR — EDITAR (pre-llenado)
    fun initializeEdit(itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = portfolioRepository.getPortfolioItemById(itemId)

            result.fold(
                onSuccess = { item ->
                    if (item == null) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "El trabajo no existe")
                        }
                        return@fold
                    }

                    val precioStr = item.precioReferencial?.let { formatPrecio(it) } ?: ""
                    val anchoStr = item.anchoCm?.let { formatPrecio(it) } ?: ""
                    val altoStr = item.altoCm?.let { formatPrecio(it) } ?: ""
                    val profStr = item.profundidadCm?.let { formatPrecio(it) } ?: ""

                    _uiState.update {
                        it.copy(
                            id = item.id,
                            uid = item.uid,
                            titulo = item.titulo,
                            descripcion = item.descripcion,
                            categoria = item.categoria,
                            material = item.material,
                            anchoCm = anchoStr,
                            altoCm = altoStr,
                            profundidadCm = profStr,
                            precioReferencial = precioStr,
                            tipoPrecio = item.tipoPrecio,
                            imagenesUrls = item.imagenesUrls,
                            originalImagenesUrls = item.imagenesUrls,
                            isEditMode = true,
                            isLoading = false,
                            originalTitulo = item.titulo,
                            originalDescripcion = item.descripcion,
                            originalCategoria = item.categoria,
                            originalMaterial = item.material,
                            originalAnchoCm = anchoStr,
                            originalAltoCm = altoStr,
                            originalProfundidadCm = profStr,
                            originalPrecio = precioStr,
                            originalTipoPrecio = item.tipoPrecio
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al cargar: ${exception.message}")
                    }
                }
            )
        }
    }

    // EVENTOS DE EDICIÓN
    fun onTituloChange(value: String) {
        _uiState.update { it.copy(titulo = value, tituloTouched = true, errorMessage = null) }
    }

    fun onDescripcionChange(value: String) {
        _uiState.update { it.copy(descripcion = value, descripcionTouched = true, errorMessage = null) }
    }

    fun onCategoriaChange(value: String) {
        _uiState.update { it.copy(categoria = value, categoriaTouched = true, errorMessage = null) }
    }

    fun onMaterialChange(value: String) {
        _uiState.update { it.copy(material = value, materialTouched = true, errorMessage = null) }
    }

    fun onPrecioChange(value: String) {
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
            it.copy(tipoPrecio = tipo, tipoPrecioTouched = true, errorMessage = null)
        }
    }

    // GUARDAR (con lógica de Cloudinary)
    fun save() {
        val state = _uiState.value

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

            // 1. Separar imágenes locales (content://) de las remotas (https://)
            val urlsFinales = mutableListOf<String>()
            val imagenesLocales = state.imagenesUrls.filter {
                it.startsWith("content://") || it.startsWith("file://")
            }

            // 2. Subir las imágenes locales a Cloudinary
            var errorSubida: String? = null
            for (uriLocal in imagenesLocales) {
                val url = suspendCancellableCoroutine<String?> { continuation ->
                    cloudinaryDataSource.subirImagen(
                        uriLocal = uriLocal,
                        carpeta = "portafolio",
                        onSuccess = { if (continuation.isActive) continuation.resume(it) },
                        onError = { if (continuation.isActive) continuation.resume(null) }
                    )
                }
                if (url != null) {
                    urlsFinales.add(url)
                } else {
                    errorSubida = "Error al subir una de las imágenes"
                    break
                }
            }

            if (errorSubida != null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = errorSubida) }
                return@launch
            }

            // 3. Agregar las imágenes que ya eran remotas (no se tocan)
            val imagenesRemotas = state.imagenesUrls.filter { it.startsWith("http") }
            urlsFinales.addAll(imagenesRemotas)

            // 4. Crear el item con las URLs finales
            val precioDouble = state.precioReferencial.toDoubleOrNull()

            val item = PortfolioItem(
                id = state.id,
                uid = state.uid,
                titulo = state.titulo.trim(),
                descripcion = state.descripcion.trim(),
                categoria = state.categoria.trim(),
                material = state.material.trim(),
                anchoCm = state.anchoCm.toDoubleOrNull(),
                altoCm = state.altoCm.toDoubleOrNull(),
                profundidadCm = state.profundidadCm.toDoubleOrNull(),
                precioReferencial = precioDouble,
                tipoPrecio = state.tipoPrecio,
                imagenesUrls = urlsFinales,
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
            message.contains("network", ignoreCase = true) -> "Sin conexión. Revisa tu internet"
            message.contains("PERMISSION_DENIED", ignoreCase = true) -> "No tienes permiso para realizar esta acción"
            else -> message
        }
    }

    private fun filtrarNumero(value: String): String {
        val filtered = value.filter { it.isDigit() || it == '.' }
        return if (filtered.count { it == '.' } > 1) {
            filtered.substring(0, filtered.lastIndexOf('.'))
        } else {
            filtered
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

    // MEDIDAS
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
}