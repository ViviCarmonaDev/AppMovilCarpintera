package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.components.PortfolioItemCard
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEmptyState
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaFloatingButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaSearchBar
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader

@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel,
    onCreateClick: () -> Unit,
    onItemClick: (PortfolioItem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ---- HEADER ----
            MervetaHeader(
                title = if (uiState.isCarpenter) "Mis trabajos" else "Catálogo",
                subtitle = if (uiState.isCarpenter)
                    "Gestiona tu portafolio"
                else
                    "Explora los trabajos de carpinteros",
                avatarIcon = Icons.Filled.Folder,
                verticalPadding = 40.dp,
                bottomPadding = 20.dp
            )

            // ---- BUSCADOR ----
            MervetaSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onClear = { viewModel.clearSearch() },
                placeholder = "Buscar nombre, categoría o material ..."
            )

            // ---- CONTENIDO ----
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF8B5A2B))
                        }
                    }

                    uiState.isEmpty -> {
                        MervetaEmptyState(
                            icon = Icons.Filled.Folder,
                            title = if (uiState.isCarpenter)
                                "Aún no tienes trabajos publicados"
                            else
                                "Aún no hay trabajos en el catálogo",
                            subtitle = if (uiState.isCarpenter)
                                "Publica tu primer trabajo para atraer clientes"
                            else
                                "Cuando los carpinteros publiquen trabajos, aparecerán acá",
                            actionText = if (uiState.isCarpenter) "PUBLICAR TRABAJO" else null,
                            actionIcon = if (uiState.isCarpenter) Icons.Filled.Add else null,
                            onActionClick = if (uiState.isCarpenter) onCreateClick else null
                        )
                    }

                    uiState.isSearchEmpty -> {
                        MervetaEmptyState(
                            icon = Icons.Filled.Search,
                            title = "Sin resultados",
                            subtitle = "No se encontraron trabajos que coincidan con \"${uiState.searchQuery}\""
                        )
                    }

                    else -> {
                        ItemsList(
                            items = uiState.filteredItems,
                            onItemClick = onItemClick
                        )
                    }
                }
            }
        }

        // ---- FAB ----
        if (uiState.isCarpenter && !uiState.isLoading && !uiState.isEmpty) {
            MervetaFloatingButton(
                onClick = onCreateClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            )
        }
    }
}

// LISTA DE TRABAJOS (grid de 2 columnas)

@Composable
private fun ItemsList(
    items: List<PortfolioItem>,
    onItemClick: (PortfolioItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            PortfolioItemCard(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}