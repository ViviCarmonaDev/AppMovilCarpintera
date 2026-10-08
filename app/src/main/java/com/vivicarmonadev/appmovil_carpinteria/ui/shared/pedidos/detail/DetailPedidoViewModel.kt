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

/**
 * ViewModel para la pantalla de detalle de un pedido.
 *
 * Carga el pedido por ID, permite:
 *  - Cliente: editar, cancelar.
 *  - Carpintero: tomar pedido, avanzar estado.
 */
class DetailPedidoViewModel(
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailPedidoUiState())
    val uiState: StateFlow<DetailPedidoUiState> = _uiState.asStateFlow()

    private var currentPedidoId: String = ""

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

    // MENSAJES

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearActionSuccess() {
        _uiState.update { it.copy(actionSuccess = false) }
    }
}