package com.vivicarmonadev.appmovil_carpinteria.ui.projects

import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem

/**
 * Estado de la UI del catálogo de trabajos.
 *
 * Se muestra en la pantalla "Proyectos" del bottom nav.
 *
 * Comportamiento según rol:
 *  - Cliente: ve TODOS los trabajos del catálogo (solo lectura)
 *  - Carpintero: ve SOLO sus trabajos (puede crear/editar/eliminar)
 */
data class PortfolioUiState(
    // ---- Lista de trabajos ----
    val items: List<PortfolioItem> = emptyList(),
    val filteredItems: List<PortfolioItem> = emptyList(),

    // ---- Búsqueda ----
    val searchQuery: String = "",

    // ---- Estado general ----
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // ---- Rol del usuario actual ----
    val isCarpenter: Boolean = false,

    // ---- Eliminación ----
    val itemToDelete: PortfolioItem? = null,     // ← para el diálogo de confirmación
    val isDeleting: Boolean = false,

    // ---- Éxito (para mostrar Snackbar) ----
    val deleteSuccessMessage: String? = null
) {

     // ¿Está vacío el catálogo (sin aplicar búsqueda)?

    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty()


    // ¿La búsqueda no dio resultados?

    val isSearchEmpty: Boolean
        get() = !isLoading && items.isNotEmpty() && filteredItems.isEmpty() && searchQuery.isNotBlank()

    // ¿Hay búsqueda activa?

    val isSearching: Boolean
        get() = searchQuery.isNotBlank()
}