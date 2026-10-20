package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CotizacionRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoSolicitud
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailCotizacionViewModel(
    private val cotizacionRepository: CotizacionRepositoryImpl = CotizacionRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailCotizacionUiState())
    val uiState: StateFlow<DetailCotizacionUiState> = _uiState.asStateFlow()

    private var currentCotizacionId: String = ""
    private var cotizacionJob: Job? = null

    fun initialize(cotizacionId: String) {
        currentCotizacionId = cotizacionId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Obtener el usuario UNA SOLA VEZ
            val user = authRepository.currentUser.firstOrNull()

            if (user != null) {
                _uiState.update {
                    it.copy(
                        isCarpenter = user.role == UserRole.CARPENTER,
                        currentUid = user.uid
                    )
                }
            }

            //  Escuchar la cotización en TIEMPO REAL
            cotizacionJob?.cancel()
            cotizacionJob = viewModelScope.launch {
                cotizacionRepository.getCotizacionFlow(cotizacionId).collect { cotizacion ->
                    if (cotizacion == null) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "La cotización no existe")
                        }
                    } else {
                        _uiState.update { state ->
                            val updated = state.copy(cotizacion = cotizacion, isLoading = false)
                            updated.withPermissions(cotizacion, state.isCarpenter)
                        }
                    }
                }
            }
        }
    }

    // ANULAR
    fun onAnularClick() { _uiState.update { it.copy(showAnularDialog = true) } }
    fun onAnularCancel() { _uiState.update { it.copy(showAnularDialog = false) } }
    fun onAnularConfirm() {
        val cotizacion = _uiState.value.cotizacion ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            cotizacionRepository.anularCotizacion(cotizacion.id)
            _uiState.update { it.copy(isProcessing = false, showAnularDialog = false, actionSuccess = true) }
        }
    }

    // ACEPTAR
    fun onAceptarClick() { _uiState.update { it.copy(showAceptarDialog = true) } }
    fun onAceptarCancel() { _uiState.update { it.copy(showAceptarDialog = false) } }
    fun onAceptarConfirm() {
        val cotizacion = _uiState.value.cotizacion ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            cotizacionRepository.aceptarCotizacion(cotizacion.id)
            _uiState.update { it.copy(isProcessing = false, showAceptarDialog = false, actionSuccess = true) }
        }
    }

    // RECHAZAR
    fun onRechazarClick() { _uiState.update { it.copy(showRechazarDialog = true) } }
    fun onRechazarCancel() { _uiState.update { it.copy(showRechazarDialog = false) } }
    fun onRechazarConfirm() {
        val cotizacion = _uiState.value.cotizacion ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            cotizacionRepository.rechazarCotizacion(cotizacion.id)
            _uiState.update { it.copy(isProcessing = false, showRechazarDialog = false, actionSuccess = true) }
        }
    }

    // CLIENTE: Solicitar cambios
    fun onObservacionChange(text: String) {
        _uiState.update { it.copy(observacionInput = text) }
    }

    fun solicitarCambios() {
        val cotizacion = _uiState.value.cotizacion ?: return
        val observacion = _uiState.value.observacionInput.trim()
        if (observacion.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val updated = cotizacion.copy(
                observacionCliente = observacion,
                solicitudCambio = true,
                estadoSolicitud = EstadoSolicitud.PENDIENTE
            )

            cotizacionRepository.updateCotizacion(updated)

            _uiState.update {
                it.copy(
                    isProcessing = false,
                    observacionInput = "",
                    showSolicitarCambiosDialog = false
                )
            }
        }
    }

    // CARPINTERO: Aceptar cambios (actualiza y navega a editar)
    fun aceptarCambios(onNavigateToEdit: (String) -> Unit) {
        val cotizacion = _uiState.value.cotizacion ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val updated = cotizacion.copy(
                solicitudCambio = false,
                estadoSolicitud = EstadoSolicitud.ACEPTADA
            )

            val result = cotizacionRepository.updateCotizacion(updated)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isProcessing = false) }
                    // ✅ Navegar a editar (el Flow actualizará la UI automáticamente)
                    onNavigateToEdit(cotizacion.id)
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isProcessing = false, errorMessage = e.message ?: "Error al actualizar")
                    }
                }
            )
        }
    }

    // CARPINTERO: Rechazar cambios
    fun onRespuestaChange(text: String) {
        _uiState.update { it.copy(respuestaInput = text) }
    }

    fun rechazarCambios() {
        val cotizacion = _uiState.value.cotizacion ?: return
        val respuesta = _uiState.value.respuestaInput.trim()

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val updated = cotizacion.copy(
                respuestaCarpintero = respuesta.ifBlank { "Cambios rechazados" },
                estadoSolicitud = EstadoSolicitud.RECHAZADA,
                solicitudCambio = false
            )

            cotizacionRepository.updateCotizacion(updated)

            _uiState.update {
                it.copy(
                    isProcessing = false,
                    respuestaInput = "",
                    showRechazarCambiosDialog = false
                )
            }
        }
    }

    // Diálogos de cambios
    fun onSolicitarCambiosClick() { _uiState.update { it.copy(showSolicitarCambiosDialog = true) } }
    fun onSolicitarCambiosCancel() { _uiState.update { it.copy(showSolicitarCambiosDialog = false) } }
    fun onRechazarCambiosClick() { _uiState.update { it.copy(showRechazarCambiosDialog = true) } }
    fun onRechazarCambiosCancel() { _uiState.update { it.copy(showRechazarCambiosDialog = false) } }

    fun clearActionSuccess() { _uiState.update { it.copy(actionSuccess = false) } }

    override fun onCleared() {
        super.onCleared()
        cotizacionJob?.cancel()
    }
}