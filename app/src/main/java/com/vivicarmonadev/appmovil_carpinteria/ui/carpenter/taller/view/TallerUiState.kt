package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.taller.view

import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile

data class TallerUiState(
    val profile: CarpenterProfile? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)