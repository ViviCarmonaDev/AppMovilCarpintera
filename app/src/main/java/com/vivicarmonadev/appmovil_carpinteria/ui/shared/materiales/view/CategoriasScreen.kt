package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CategoriaMaterial
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEmptyState
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaFloatingButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaSearchBar
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.components.CategoriaCard

@Composable
fun CategoriasScreen(
    viewModel: CategoriasViewModel,
    onCreateClick: () -> Unit,
    onCategoriaClick: (CategoriaMaterial) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            MervetaHeader(
                title = "Categorías de materiales",
                subtitle = "Explora los tipos de madera disponibles",
                avatarIcon = Icons.Filled.Folder,
                verticalPadding = 40.dp,
                bottomPadding = 20.dp
            )

            MervetaSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onClear = { viewModel.clearSearch() },
                placeholder = "Buscar categoría..."
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF8B5A2B))
                    }

                    uiState.isEmpty -> MervetaEmptyState(
                        icon = Icons.Filled.Folder,
                        title = "Sin categorías",
                        subtitle = "Agrega la primera categoría de materiales",
                        actionText = if (uiState.isCarpenter) "AGREGAR CATEGORÍA" else null,
                        actionIcon = if (uiState.isCarpenter) Icons.Filled.Add else null,
                        onActionClick = if (uiState.isCarpenter) onCreateClick else null
                    )

                    uiState.isSearchEmpty -> MervetaEmptyState(
                        icon = Icons.Filled.Search,
                        title = "Sin resultados",
                        subtitle = "No hay categorías que coincidan con \"${uiState.searchQuery}\""
                    )

                    else -> LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.categoriasFiltradas, key = { it.id }) { categoria ->
                            CategoriaCard(
                                categoria = categoria,
                                onClick = { onCategoriaClick(categoria) }
                            )
                        }
                    }
                }
            }
        }

        if (uiState.isCarpenter && !uiState.isLoading && !uiState.isEmpty) {
            MervetaFloatingButton(
                onClick = onCreateClick,
                modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
            )
        }
    }
}