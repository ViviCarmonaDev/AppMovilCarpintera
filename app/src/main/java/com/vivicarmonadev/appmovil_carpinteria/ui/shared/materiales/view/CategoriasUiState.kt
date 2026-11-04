package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.view

import com.vivicarmonadev.appmovil_carpinteria.domain.model.CategoriaMaterial

data class CategoriasUiState(
    val categorias: List<CategoriaMaterial> = emptyList(),
    val categoriasFiltradas: List<CategoriaMaterial> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isCarpenter: Boolean = false
) {
    val isEmpty: Boolean
        get() = !isLoading && categorias.isEmpty()

    val isSearchEmpty: Boolean
        get() = !isLoading && categorias.isNotEmpty() && categoriasFiltradas.isEmpty()
}