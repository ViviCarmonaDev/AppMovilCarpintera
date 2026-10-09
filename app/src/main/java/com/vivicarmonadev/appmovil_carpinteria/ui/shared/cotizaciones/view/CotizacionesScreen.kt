package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.RequestQuote
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEmptyState
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaSearchBar
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.component.CotizacionCard
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader

@Composable
fun CotizacionesScreen(
    viewModel: CotizacionesViewModel,
    onCotizacionClick: (Cotizacion) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        MervetaHeader(
            title = "Mis cotizaciones",
            subtitle = "Gestiona tus cotizaciones enviadas",
            avatarIcon = Icons.Filled.RequestQuote,
            verticalPadding = 40.dp,
            bottomPadding = 20.dp
        )

        // Buscador
        MervetaSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onClear = { viewModel.clearSearch() },
            placeholder = "Buscar por cliente o pedido..."
        )

        // Filtros
        FiltrosRow(
            estadoSeleccionado = uiState.estadoFiltro,
            onEstadoSelected = { viewModel.onEstadoFiltroChange(it) }
        )

        // Contenido
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
                        icon = Icons.Filled.RequestQuote,
                        title = "Sin cotizaciones",
                        subtitle = "Cuando envíes una cotización, aparecerá acá"
                    )
                }

                uiState.isFilterEmpty -> {
                    MervetaEmptyState(
                        icon = Icons.Filled.RequestQuote,
                        title = "Sin resultados",
                        subtitle = "No hay cotizaciones que coincidan con los filtros"
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.cotizacionesFiltradas, key = { it.id }) { cotizacion ->
                            CotizacionCard(
                                cotizacion = cotizacion,
                                onClick = { onCotizacionClick(cotizacion) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// FILTROS POR ESTADO
@Composable
private fun FiltrosRow(
    estadoSeleccionado: EstadoCotizacion?,
    onEstadoSelected: (EstadoCotizacion?) -> Unit
) {
    val opciones = listOf(
        "Todas" to null,
        "Pendientes" to EstadoCotizacion.PENDIENTE,
        "Aceptadas" to EstadoCotizacion.ACEPTADA,
        "Rechazadas" to EstadoCotizacion.RECHAZADA,
        "Anuladas" to EstadoCotizacion.ANULADA
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { (label, estado) ->
            val isSelected = estadoSeleccionado == estado

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) Color(0xFF8B5A2B) else Color(0xFFF5EFE7)
                    )
                    .clickable { onEstadoSelected(estado) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFFF9F7F5) else Color(0xFF2C2C2C)
                )
            }
        }
    }
}