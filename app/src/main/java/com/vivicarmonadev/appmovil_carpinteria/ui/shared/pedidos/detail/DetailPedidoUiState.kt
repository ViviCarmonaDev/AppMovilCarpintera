package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.detail

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido

/**
 * Estado de la UI para la pantalla de detalle de un pedido.
 *
 * Se adapta según el rol:
 *  - CLIENTE: puede editar o cancelar (solo si está PENDIENTE).
 *  - CARPINTERO: puede tomar el pedido y avanzar el estado.
 */
data class DetailPedidoUiState(
    val pedido: Pedido? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // ---- Rol del usuario actual ----
    val isCarpenter: Boolean = false,
    val currentUid: String = "",
    val currentUserName: String = "",

    // ---- Diálogos ----
    val showDeleteDialog: Boolean = false,
    val showCancelDialog: Boolean = false,
    val isProcessing: Boolean = false,

    // ---- Resultado ----
    val actionSuccess: Boolean = false
)