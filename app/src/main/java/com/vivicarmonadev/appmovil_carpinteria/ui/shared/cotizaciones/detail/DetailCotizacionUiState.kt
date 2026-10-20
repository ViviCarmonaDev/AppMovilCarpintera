package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.detail

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion

data class DetailCotizacionUiState(
    val cotizacion: Cotizacion? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // Rol del usuario actual
    val isCarpenter: Boolean = false,
    val currentUid: String = "",

    // Cliente: observación
    val observacionInput: String = "",

    // Carpintero: respuesta
    val respuestaInput: String = "",

    // Diálogos
    val showAnularDialog: Boolean = false,
    val showAceptarDialog: Boolean = false,
    val showRechazarDialog: Boolean = false,
    val showSolicitarCambiosDialog: Boolean = false,
    val showRechazarCambiosDialog: Boolean = false,
    val isProcessing: Boolean = false,
    val actionSuccess: Boolean = false,

    // Flags de permisos
    val puedoEditar: Boolean = false,
    val puedoAnular: Boolean = false,
    val puedoAceptarRechazar: Boolean = false,
    val puedoSolicitarCambios: Boolean = false,
    val puedoResponderCambios: Boolean = false
) {

    fun withPermissions(cotizacion: Cotizacion, isCarpenter: Boolean): DetailCotizacionUiState {
        val isOwner = cotizacion.carpinteroUid == currentUid
        val isClient = cotizacion.clienteUid == currentUid

        return copy(
            puedoEditar = isCarpenter && isOwner && cotizacion.estado.name == "PENDIENTE",
            puedoAnular = isCarpenter && isOwner && cotizacion.estado.name == "PENDIENTE",
            puedoAceptarRechazar = !isCarpenter && isClient && cotizacion.estado.name == "PENDIENTE" && !cotizacion.solicitudCambio,
            puedoSolicitarCambios = !isCarpenter && isClient && cotizacion.estado.name == "PENDIENTE" && !cotizacion.solicitudCambio,
            puedoResponderCambios = isCarpenter && isOwner && cotizacion.solicitudCambio
        )
    }
}