package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.portafolio

import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem

data class CarpenterPortfolioUiState(
    val carpinteroUid: String = "",
    val nombreTaller: String = "",
    val items: List<PortfolioItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty()
}