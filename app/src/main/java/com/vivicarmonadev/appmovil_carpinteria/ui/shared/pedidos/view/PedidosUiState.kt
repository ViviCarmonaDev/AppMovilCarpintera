package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.view

import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido

/**
 * Estado de la UI del Centro de Pedidos.
 *
 * Se adapta según el rol:
 *  - CLIENTE: ve sus pedidos y puede cancelarlos.
 *  - CARPINTERO: ve pedidos asignados + libres y puede cambiar estados.
 */
data class PedidosUiState(
    // ---- Datos ----
    val pedidos: List<Pedido> = emptyList(),
    val pedidosFiltrados: List<Pedido> = emptyList(),

    // ---- Usuario actual ----
    val currentUid: String = "",
    val currentUserName: String = "",
    val isCarpenter: Boolean = false,

    // ---- Búsqueda y filtros ----
    val searchQuery: String = "",
    val estadoFiltro: EstadoPedido? = null,

    // ---- Estado ----
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,

    // ---- Pedido a cancelar (para diálogo) ----
    val pedidoACancelar: Pedido? = null,
    val isCancelling: Boolean = false,

    // ---- Mensaje de éxito ----
    val successMessage: String? = null
) {

    /** ¿No hay pedidos en absoluto? */
    val isEmpty: Boolean
        get() = !isLoading && pedidos.isEmpty()

    /** ¿La búsqueda/filtro no dio resultados? */
    val isFilterEmpty: Boolean
        get() = !isLoading && pedidos.isNotEmpty() && pedidosFiltrados.isEmpty()

    /** ¿Hay búsqueda o filtro activo? */
    val hasActiveFilter: Boolean
        get() = searchQuery.isNotBlank() || estadoFiltro != null

    // CONTADORES POR ESTADO (para mostrar badges)

    val totalPedidos: Int
        get() = pedidos.size

    val totalPendientes: Int
        get() = pedidos.count { it.status == EstadoPedido.PENDIENTE }

    val totalEnProceso: Int
        get() = pedidos.count { it.status == EstadoPedido.EN_PROCESO }

    val totalTerminados: Int
        get() = pedidos.count { it.status == EstadoPedido.TERMINADO }

    val totalEntregados: Int
        get() = pedidos.count { it.status == EstadoPedido.ENTREGADO }

    val totalCancelados: Int
        get() = pedidos.count { it.status == EstadoPedido.CANCELADO }
}