package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CarpenterRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio
import com.vivicarmonadev.appmovil_carpinteria.data.remote.cloudinary.CloudinaryDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine // ✅ NUEVO
import kotlin.coroutines.resume // ✅ NUEVO

class CreatePortfolioViewModel(
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl(),
    private val cloudinaryDataSource: CloudinaryDataSource = CloudinaryDataSource()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePortfolioUiState())
    val uiState: StateFlow<CreatePortfolioUiState> = _uiState.asStateFlow()

    private var currentUid: String = ""
    private var currentUserNombre: String = ""
    private var currentNombreTaller: String = ""

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    currentUid = user.uid
                    currentUserNombre = user.fullName

                    val profile = carpenterRepository
                        .getCarpenterProfile(user.uid)
                        .firstOrNull()

                    currentNombreTaller = profile?.nombreTaller ?: ""
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

            // 1. Subir imágenes a Cloudinary (si hay)
            val urlsSubidas = mutableListOf<String>()
            var errorSubida: String? = null

            for (uriLocal in state.imagenesUrls) {
                // Convertimos el callback de Cloudinary en una corrutina suspendida
                val url = suspendCancellableCoroutine<String?> { continuation ->
                    cloudinaryDataSource.subirImagen(
                        uriLocal = uriLocal,
                        carpeta = "portafolio",
                        onSuccess = { urlPublica ->
                            if (continuation.isActive) continuation.resume(urlPublica)
                        },
                        onError = { error ->
                            if (continuation.isActive) continuation.resume(null)
                        }
                    )
                }

                if (url != null) {
                    urlsSubidas.add(url)
                } else {
                    errorSubida = "Error al subir una de las imágenes"
                    break // Detenemos si falla una
                }
            }

            if (errorSubida != null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = errorSubida) }
                return@launch
            }

            // 2. Obtener datos extra del usuario
            val precioDouble = state.precioReferencial.toDoubleOrNull()
            val user = authRepository.currentUser.firstOrNull()
            val nombre = user?.fullName ?: ""
            val profile = carpenterRepository.getCarpenterProfile(currentUid).firstOrNull()
            val taller = profile?.nombreTaller ?: ""

            // 3. Crear el item con las URLs de Cloudinary
            val item = PortfolioItem(
                id = "",
                uid = currentUid,
                titulo = state.titulo.trim(),
                descripcion = state.descripcion.trim(),
                categoria = state.categoria.trim(),
                material = state.material.trim(),
                carpinteroNombre = nombre,
                nombreTaller = taller,
                anchoCm = state.anchoCm.toDoubleOrNull(),
                altoCm = state.altoCm.toDoubleOrNull(),
                profundidadCm = state.profundidadCm.toDoubleOrNull(),
                precioReferencial = precioDouble,
                tipoPrecio = state.tipoPrecio ?: TipoPrecio.A_TRATAR,
                imagenesUrls = urlsSubidas // ✅ URLs de Cloudinary
            )

            // 4. Guardar en Firestore
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