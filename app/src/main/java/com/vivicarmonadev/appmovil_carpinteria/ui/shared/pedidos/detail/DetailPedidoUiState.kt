package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.detail

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion

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
    val cotizaciones: List<Cotizacion> = emptyList(),
    val isLoadingCotizaciones: Boolean = false,
    val cotizacionAAceptar: Cotizacion? = null,
    val cotizacionARechazar: Cotizacion? = null,
    val isProcesandoCotizacion: Boolean = false,
    val cotizacionSuccessMessage: String? = null,

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