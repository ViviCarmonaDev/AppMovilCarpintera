package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.view

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion

data class CotizacionesUiState(
    val cotizaciones: List<Cotizacion> = emptyList(),
    val cotizacionesFiltradas: List<Cotizacion> = emptyList(),
    val searchQuery: String = "",
    val estadoFiltro: EstadoCotizacion? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val currentUid: String = "",
    val isCarpenter: Boolean = false
) {
    val isEmpty: Boolean
        get() = !isLoading && cotizaciones.isEmpty()

    val isFilterEmpty: Boolean
        get() = !isLoading && cotizaciones.isNotEmpty() && cotizacionesFiltradas.isEmpty()
}