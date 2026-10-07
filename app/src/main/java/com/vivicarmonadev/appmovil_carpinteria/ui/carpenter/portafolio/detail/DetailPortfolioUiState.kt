package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.detail

import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem

/**
 * Estado de la UI para la pantalla de detalle de un trabajo.
 */
data class DetailPortfolioUiState(
    val item: PortfolioItem? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isCarpenter: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteSuccess: Boolean = false
)