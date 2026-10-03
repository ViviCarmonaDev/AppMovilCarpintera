package com.vivicarmonadev.appmovil_carpinteria.ui.pedidos

import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido

/**
 * Estado de la UI del Centro de Pedidos.
 *
 * Se muestra en la pantalla "Pedidos" del bottom nav (para el cliente).
 *
 * Incluye:
 *  - Lista de pedidos del cliente
 *  - Búsqueda por texto (nombre, material, etc.)
 *  - Filtro por estado
 */
data class PedidosUiState(
    // ---- Datos ----
    val pedidos: List<Pedido> = emptyList(),
    val pedidosFiltrados: List<Pedido> = emptyList(),

    // ---- Usuario actual ----
    val currentUid: String = "",

    // ---- Búsqueda y filtros ----
    val searchQuery: String = "",
    val estadoFiltro: EstadoPedido? = null,      // null = "TODOS"

    // ---- Estado ----
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // ---- Pedido a cancelar (para diálogo) ----
    val pedidoACancelar: Pedido? = null,
    val isCancelling: Boolean = false,

    // ---- Mensaje de éxito (Snackbar) ----
    val successMessage: String? = null
) {

     // ¿No hay pedidos en absoluto?

    val isEmpty: Boolean
        get() = !isLoading && pedidos.isEmpty()

    // ¿La búsqueda/filtro no dio resultados?

    val isFilterEmpty: Boolean
        get() = !isLoading && pedidos.isNotEmpty() && pedidosFiltrados.isEmpty()

    // ¿Hay búsqueda o filtro activo?

    val hasActiveFilter: Boolean
        get() = searchQuery.isNotBlank() || estadoFiltro != null
}