package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PedidoRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del Centro de Pedidos.
 *
 * Se adapta según el rol:
 *  - CLIENTE: carga SUS pedidos.
 *  - CARPINTERO: carga los pedidos asignados a él + los pedidos libres.
 *
 * También permite:
 *  - Filtrar por estado.
 *  - Buscar por texto.
 *  - Cancelar (cliente).
 *  - Tomar pedidos libres (carpintero).
 *  - Cambiar estados (carpintero).
 */
class PedidosViewModel(
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PedidosUiState())
    val uiState: StateFlow<PedidosUiState> = _uiState.asStateFlow()

    // Job del Flow actual (para cancelarlo si cambia el usuario)
    private var pedidosJob: Job? = null

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    val uidChanged = _uiState.value.currentUid != user.uid

                    _uiState.update {
                        it.copy(
                            currentUid = user.uid,
                            currentUserName = user.nombres,
                            isCarpenter = user.role == UserRole.CARPENTER
                        )
                    }

                    if (uidChanged) {
                        loadPedidos()
                    }
                } else {
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

    // CARGAR PEDIDOS SEGÚN ROL

    private fun loadPedidos() {
        pedidosJob?.cancel()

        val state = _uiState.value

        pedidosJob = if (state.isCarpenter) {
            // 👇 CARPINTERO: pedidos asignados + pedidos libres
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                combine(
                    pedidoRepository.getPedidosAsignadosAMi(state.currentUid),
                    pedidoRepository.getPedidosLibres()
                ) { asignados, libres ->
                    // Combinamos: primero los asignados, luego los libres (sin duplicados)
                    (asignados + libres).distinctBy { it.id }
                }.collect { pedidos ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            pedidos = pedidos,
                            pedidosFiltrados = aplicarFiltros(
                                pedidos = pedidos,
                                query = currentState.searchQuery,
                                estado = currentState.estadoFiltro
                            ),
                            isLoading = false
                        )
                    }
                }
            }
        } else {
            // 👇 CLIENTE: solo sus pedidos
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                pedidoRepository.getMyPedidos(state.currentUid).collect { pedidos ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            pedidos = pedidos,
                            pedidosFiltrados = aplicarFiltros(
                                pedidos = pedidos,
                                query = currentState.searchQuery,
                                estado = currentState.estadoFiltro
                            ),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        if (_uiState.value.currentUid.isNotBlank()) {
            loadPedidos()
        }
    }

    // FILTROS

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

        // Filtro por búsqueda
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            resultado = resultado.filter { pedido ->
                pedido.titulo.lowercase().contains(q) ||
                        pedido.categoria.lowercase().contains(q) ||
                        pedido.tipoMadera.lowercase().contains(q) ||
                        pedido.descripcion.lowercase().contains(q) ||
                        pedido.clienteNombre.lowercase().contains(q) ||
                        (pedido.carpinteroNombre?.lowercase()?.contains(q) == true)
            }
        }

        return resultado
    }

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

    // CLIENTE: CANCELAR PEDIDO

    fun onCancelarClick(pedido: Pedido) {
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

    // CARPINTERO: ACCIONES

    /**
     * El carpintero toma un pedido libre. Se asigna a sí mismo.
     */
    fun onTomarPedido(pedido: Pedido) {
        val state = _uiState.value
        if (state.currentUid.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }

            val result = pedidoRepository.asignarCarpintero(
                pedidoId = pedido.id,
                carpenterUid = state.currentUid,
                carpinteroNombre = state.currentUserName
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            successMessage = "Pedido tomado"
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            errorMessage = "Error al tomar el pedido: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Cambia el estado de un pedido.
     * Uso: ACEPTADO, EN_PROCESO, TERMINADO, ENTREGADO.
     */
    fun onCambiarEstado(pedido: Pedido, nuevoEstado: EstadoPedido) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }

            val result = pedidoRepository.updatePedidoStatus(
                pedidoId = pedido.id,
                nuevoEstado = nuevoEstado
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            successMessage = "Estado actualizado"
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            errorMessage = "Error al actualizar: ${exception.message}"
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