package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.detail

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion

data class DetailCotizacionUiState(
    val cotizacion: Cotizacion? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // Rol del usuario actual
    val isCarpenter: Boolean = false,
    val currentUid: String = "",

    // Diálogos
    val showAnularDialog: Boolean = false,
    val showAceptarDialog: Boolean = false,
    val showRechazarDialog: Boolean = false,
    val isProcessing: Boolean = false,
    val actionSuccess: Boolean = false
) {

    val esMiCotizacion: Boolean
        get() = cotizacion?.carpinteroUid == currentUid

    val puedoEditar: Boolean
        get() = esMiCotizacion && cotizacion?.estado?.name == "PENDIENTE"

    val puedoAnular: Boolean
        get() = esMiCotizacion && cotizacion?.estado?.name == "PENDIENTE"

    val puedoAceptarRechazar: Boolean
        get() = !isCarpenter &&
                cotizacion?.estado?.name == "PENDIENTE" &&
                cotizacion?.clienteUid == currentUid
}