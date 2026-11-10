package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.detail

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Material

data class DetailMaterialUiState(
    val material: Material? = null,
    val isLoading: Boolean = true,
    val isCarpenter: Boolean = false,
    val errorMessage: String? = null,

    // Estado para el diálogo de eliminación
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteSuccess: Boolean = false
)