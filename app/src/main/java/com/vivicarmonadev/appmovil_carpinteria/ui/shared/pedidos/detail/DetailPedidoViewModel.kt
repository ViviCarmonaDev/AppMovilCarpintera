package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PedidoRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CotizacionRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import kotlinx.coroutines.Job

/**
 * ViewModel para la pantalla de detalle de un pedido.

 * Carga el pedido por ID, permite:
 *  - Cliente: editar, cancelar.
 *  - Carpintero: tomar pedido, avanzar estado.
 */
class DetailPedidoViewModel(
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val cotizacionRepository: CotizacionRepositoryImpl = CotizacionRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailPedidoUiState())
    val uiState: StateFlow<DetailPedidoUiState> = _uiState.asStateFlow()

    private var currentPedidoId: String = ""
    private var cotizacionesJob: Job? = null

    // INICIALIZAR

    fun initialize(pedidoId: String) {
        currentPedidoId = pedidoId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            isCarpenter = user.role == UserRole.CARPENTER,
                            currentUid = user.uid,
                            currentUserName = user.nombres
                        )
                    }
                }
                cargarPedido()
            }
        }
        cargarCotizaciones(pedidoId)
    }

    private suspend fun cargarPedido() {
        val result = pedidoRepository.getPedidoById(currentPedidoId)

        result.fold(
            onSuccess = { pedido ->
                if (pedido == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "El pedido no existe"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(pedido = pedido, isLoading = false)
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

    private fun cargarCotizaciones(pedidoId: String) {
        cotizacionesJob?.cancel()
        cotizacionesJob = viewModelScope.launch {
            cotizacionRepository.getCotizacionesByPedido(pedidoId).collect { lista ->
                _uiState.update { it.copy(cotizaciones = lista) }
            }
        }
    }

    // ACCIONES DEL CLIENTE
    fun onCancelarClick() {
        _uiState.update { it.copy(showCancelDialog = true) }
    }

    fun onCancelarCancel() {
        _uiState.update { it.copy(showCancelDialog = false) }
    }

    fun onCancelarConfirm() {
        val pedido = _uiState.value.pedido ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val result = pedidoRepository.updatePedidoStatus(
                pedidoId = pedido.id,
                nuevoEstado = EstadoPedido.CANCELADO
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showCancelDialog = false,
                            actionSuccess = true
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showCancelDialog = false,
                            errorMessage = "Error al cancelar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // ELIMINAR PEDIDO (solo cliente, solo PENDIENTE)

    fun onEliminarClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onEliminarCancel() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onEliminarConfirm() {
        val pedido = _uiState.value.pedido ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val result = pedidoRepository.deletePedido(pedido.id)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showDeleteDialog = false,
                            actionSuccess = true
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            showDeleteDialog = false,
                            errorMessage = "Error al eliminar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // ACCIONES DEL CARPINTERO

    fun onTomarPedido() {
        val state = _uiState.value
        val pedido = state.pedido ?: return
        if (state.currentUid.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val result = pedidoRepository.asignarCarpintero(
                pedidoId = pedido.id,
                carpenterUid = state.currentUid,
                carpinteroNombre = state.currentUserName
            )

            result.fold(
                onSuccess = {
                    // Recargar el pedido para ver el cambio
                    cargarPedido()
                    _uiState.update {
                        it.copy(isProcessing = false, actionSuccess = true)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            errorMessage = "Error al tomar el pedido: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    fun onCambiarEstado(nuevoEstado: EstadoPedido) {
        val pedido = _uiState.value.pedido ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val result = pedidoRepository.updatePedidoStatus(
                pedidoId = pedido.id,
                nuevoEstado = nuevoEstado
            )

            result.fold(
                onSuccess = {
                    cargarPedido()
                    _uiState.update {
                        it.copy(isProcessing = false, actionSuccess = true)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            errorMessage = "Error al actualizar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // ACEPTAR / RECHAZAR COTIZACIÓN (cliente)

    fun onAceptarCotizacionClick(cotizacion: Cotizacion) {
        _uiState.update { it.copy(cotizacionAAceptar = cotizacion) }
    }

    fun onAceptarCotizacionCancel() {
        _uiState.update { it.copy(cotizacionAAceptar = null) }
    }

    fun onAceptarCotizacionConfirm() {
        val cotizacion = _uiState.value.cotizacionAAceptar ?: return
        val pedido = _uiState.value.pedido ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcesandoCotizacion = true) }

            // 1. Marcar la cotización elegida como ACEPTADA
            cotizacionRepository.aceptarCotizacion(cotizacion.id)

            // 2. Asignar el carpintero al pedido
            val asignarResult = pedidoRepository.asignarCarpintero(
                pedidoId = pedido.id,
                carpenterUid = cotizacion.carpinteroUid,
                carpinteroNombre = cotizacion.carpinteroNombre
            )

            // 3. Cambiar el pedido a ACEPTADO
            pedidoRepository.updatePedidoStatus(pedido.id, EstadoPedido.ACEPTADO)

            // 4. Rechazar las demás cotizaciones pendientes
            _uiState.value.cotizaciones
                .filter { it.id != cotizacion.id && it.estado == EstadoCotizacion.PENDIENTE }
                .forEach { otra ->
                    cotizacionRepository.rechazarCotizacion(otra.id)
                }
            asignarResult.fold(
                onSuccess = {
                    cargarPedido()
                    _uiState.update {
                        it.copy(
                            isProcesandoCotizacion = false,
                            cotizacionAAceptar = null,
                            cotizacionSuccessMessage = "Cotización aceptada"
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcesandoCotizacion = false,
                            cotizacionAAceptar = null,
                            errorMessage = "Error: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    fun onRechazarCotizacionClick(cotizacion: Cotizacion) {
        _uiState.update { it.copy(cotizacionARechazar = cotizacion) }
    }

    fun onRechazarCotizacionCancel() {
        _uiState.update { it.copy(cotizacionARechazar = null) }
    }

    fun onRechazarCotizacionConfirm() {
        val cotizacion = _uiState.value.cotizacionARechazar ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcesandoCotizacion = true) }

            val result = cotizacionRepository.rechazarCotizacion(cotizacion.id)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isProcesandoCotizacion = false,
                            cotizacionARechazar = null,
                            cotizacionSuccessMessage = "Cotización rechazada"
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isProcesandoCotizacion = false,
                            cotizacionARechazar = null,
                            errorMessage = "Error: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // MENSAJES

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    fun clearActionSuccess() {
        _uiState.update { it.copy(actionSuccess = false) }
    }
    fun clearCotizacionSuccessMessage() {
        _uiState.update { it.copy(cotizacionSuccessMessage = null) }
    }
}