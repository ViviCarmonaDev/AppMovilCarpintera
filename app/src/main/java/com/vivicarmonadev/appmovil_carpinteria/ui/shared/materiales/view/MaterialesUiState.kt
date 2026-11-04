package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.view

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Material

data class MaterialesUiState(
    val materiales: List<Material> = emptyList(),
    val materialesFiltrados: List<Material> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isCarpenter: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && materiales.isEmpty()

    val isSearchEmpty: Boolean
        get() = !isLoading && materiales.isNotEmpty() && materialesFiltrados.isEmpty()
}