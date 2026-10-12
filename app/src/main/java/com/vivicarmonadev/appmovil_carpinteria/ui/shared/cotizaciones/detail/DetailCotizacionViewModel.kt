package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CotizacionRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailCotizacionViewModel(
    private val cotizacionRepository: CotizacionRepositoryImpl = CotizacionRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailCotizacionUiState())
    val uiState: StateFlow<DetailCotizacionUiState> = _uiState.asStateFlow()

    private var currentCotizacionId: String = ""

    fun initialize(cotizacionId: String) {
        currentCotizacionId = cotizacionId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            isCarpenter = user.role == UserRole.CARPENTER,
                            currentUid = user.uid
                        )
                    }
                }
                cargarCotizacion()
            }
        }
    }

    private suspend fun cargarCotizacion() {
        val result = cotizacionRepository.getCotizacionById(currentCotizacionId)

        result.fold(
            onSuccess = { cotizacion ->
                if (cotizacion == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "La cotización no existe")
                    }
                } else {
                    _uiState.update {
                        it.copy(cotizacion = cotizacion, isLoading = false)
                    }
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

    // ANULAR (carpintero)

    fun onAnularClick() {
        _uiState.update { it.copy(showAnularDialog = true) }
    }

    fun onAnularCancel() {
        _uiState.update { it.copy(showAnularDialog = false) }
    }

    fun onAnularConfirm() {
        val cotizacion = _uiState.value.cotizacion ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val result = cotizacionRepository.anularCotizacion(cotizacion.id)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showAnularDialog = false,
                            actionSuccess = true
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showAnularDialog = false,
                            errorMessage = "Error: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // ACEPTAR (cliente)

    fun onAceptarClick() {
        _uiState.update { it.copy(showAceptarDialog = true) }
    }

    fun onAceptarCancel() {
        _uiState.update { it.copy(showAceptarDialog = false) }
    }

    fun onAceptarConfirm() {
        val cotizacion = _uiState.value.cotizacion ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val result = cotizacionRepository.aceptarCotizacion(cotizacion.id)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showAceptarDialog = false,
                            actionSuccess = true
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showAceptarDialog = false,
                            errorMessage = "Error: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // RECHAZAR (cliente)

    fun onRechazarClick() {
        _uiState.update { it.copy(showRechazarDialog = true) }
    }

    fun onRechazarCancel() {
        _uiState.update { it.copy(showRechazarDialog = false) }
    }

    fun onRechazarConfirm() {
        val cotizacion = _uiState.value.cotizacion ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val result = cotizacionRepository.rechazarCotizacion(cotizacion.id)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showRechazarDialog = false,
                            actionSuccess = true
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showRechazarDialog = false,
                            errorMessage = "Error: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    fun clearActionSuccess() {
        _uiState.update { it.copy(actionSuccess = false) }
    }
}