package com.vivicarmonadev.appmovil_carpinteria.ui.client.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PedidoRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PedidosViewModel(
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PedidosUiState())
    val uiState: StateFlow<PedidosUiState> = _uiState.asStateFlow()

    // Job del Flow de pedidos, para poder cancelarlo si es necesario
    private var pedidosJob: Job? = null

    init {
        // Observamos al usuario actual (solo para saber quién es)
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    val uidChanged = _uiState.value.currentUid != user.uid

                    _uiState.update { it.copy(currentUid = user.uid) }

                    // Solo cargar si cambió el uid (evita recargas innecesarias)
                    if (uidChanged) {
                        loadPedidos(user.uid)
                    }
                } else {
                    // Sin sesión → limpiar
                    pedidosJob?.cancel()
                    _uiState.update {
                        it.copy(
                            currentUid = "",
                            pedidos = emptyList(),
                            pedidosFiltrados = emptyList(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    // CARGAR PEDIDOS

    private fun loadPedidos(clientUid: String) {
        // Cancelar el Job anterior si existe
        pedidosJob?.cancel()

        pedidosJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            pedidoRepository.getMyPedidos(clientUid).collect { pedidos ->
                _uiState.update { state ->
                    state.copy(
                        pedidos = pedidos,
                        pedidosFiltrados = aplicarFiltros(
                            pedidos = pedidos,
                            query = state.searchQuery,
                            estado = state.estadoFiltro
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    // REFRESH MANUAL (por si hace falta)
    fun refresh() {
        val uid = _uiState.value.currentUid
        if (uid.isNotBlank()) {
            loadPedidos(uid)
        }
    }

    // APLICAR FILTROS
    private fun aplicarFiltros(
        pedidos: List<Pedido>,
        query: String,
        estado: EstadoPedido?
    ): List<Pedido> {
        var resultado = pedidos

        // Filtro por estado
        if (estado != null) {
            resultado = resultado.filter { it.status == estado }
        }

        // Filtro por búsqueda (título, categoría, material, descripción)
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            resultado = resultado.filter { pedido ->
                pedido.titulo.lowercase().contains(q) ||
                        pedido.categoria.lowercase().contains(q) ||
                        pedido.tipoMadera.lowercase().contains(q) ||
                        pedido.descripcion.lowercase().contains(q)
            }
        }

        return resultado
    }

    // BÚSQUEDA
    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                pedidosFiltrados = aplicarFiltros(
                    pedidos = state.pedidos,
                    query = query,
                    estado = state.estadoFiltro
                )
            )
        }
    }

    fun clearSearch() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                pedidosFiltrados = aplicarFiltros(
                    pedidos = state.pedidos,
                    query = "",
                    estado = state.estadoFiltro
                )
            )
        }
    }

    // FILTRO POR ESTADO

    fun onEstadoFiltroChange(estado: EstadoPedido?) {
        _uiState.update { state ->
            state.copy(
                estadoFiltro = estado,
                pedidosFiltrados = aplicarFiltros(
                    pedidos = state.pedidos,
                    query = state.searchQuery,
                    estado = estado
                )
            )
        }
    }

    // CANCELAR PEDIDO
    fun onCancelarClick(pedido: Pedido) {
        // Solo se pueden cancelar pedidos en estado PENDIENTE
        if (pedido.status == EstadoPedido.PENDIENTE) {
            _uiState.update { it.copy(pedidoACancelar = pedido) }
        }
    }

    fun onCancelarCancel() {
        _uiState.update { it.copy(pedidoACancelar = null) }
    }

    fun onCancelarConfirm() {
        val pedido = _uiState.value.pedidoACancelar ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true, errorMessage = null) }

            val result = pedidoRepository.updatePedidoStatus(
                pedidoId = pedido.id,
                nuevoEstado = EstadoPedido.CANCELADO
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isCancelling = false,
                            pedidoACancelar = null,
                            successMessage = "Pedido cancelado"
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isCancelling = false,
                            pedidoACancelar = null,
                            errorMessage = "Error al cancelar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // MENSAJES

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

}