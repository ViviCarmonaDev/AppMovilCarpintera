package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEmptyState
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaFloatingButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaSearchBar
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.components.OrderCard
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.components.PedidoFilterChips

@Composable
fun PedidosScreen(
    viewModel: PedidosViewModel,
    onCreateClick: () -> Unit,
    onItemClick: (Pedido) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ---- HEADER ----
            MervetaHeader(
                title = if (uiState.isCarpenter) "Pedidos" else "Mis pedidos",
                subtitle = if (uiState.isCarpenter)
                    "Gestiona las solicitudes de tus clientes"
                else
                    "Sigue el estado de tus pedidos",
                avatarIcon = Icons.Filled.Inbox,
                verticalPadding = 40.dp,
                bottomPadding = 20.dp
            )

            // ---- BUSCADOR ----
            MervetaSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onClear = { viewModel.clearSearch() },
                placeholder = "Buscar nombre, categoria o material ..."
            )

            // ---- FILTROS ----
            PedidoFilterChips(
                estadoSeleccionado = uiState.estadoFiltro,
                onEstadoSelected = { viewModel.onEstadoFiltroChange(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
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
                            icon = Icons.Filled.Inbox,
                            title = if (uiState.isCarpenter)
                                "Aún no hay pedidos"
                            else
                                "Aún no tienes pedidos",
                            subtitle = if (uiState.isCarpenter)
                                "Cuando un cliente te contacte, aparecerá acá"
                            else
                                "Solicita tu primer mueble a un carpintero",
                            actionText = if (!uiState.isCarpenter) "NUEVO PEDIDO" else null,
                            actionIcon = if (!uiState.isCarpenter) Icons.Filled.Add else null,
                            onActionClick = if (!uiState.isCarpenter) onCreateClick else null
                        )
                    }

                    uiState.isFilterEmpty -> {
                        MervetaEmptyState(
                            icon = Icons.Filled.Search,
                            title = "Sin resultados",
                            subtitle = "No hay pedidos que coincidan con los filtros aplicados"
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.pedidosFiltrados, key = { it.id }) { pedido ->
                                OrderCard(
                                    pedido = pedido,
                                    isCarpenter = uiState.isCarpenter,
                                    onClick = { onItemClick(pedido) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ---- FAB (solo cliente) ----
        if (!uiState.isCarpenter && !uiState.isLoading && !uiState.isEmpty) {
            MervetaFloatingButton(
                onClick = onCreateClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            )
        }

        // ---- DIÁLOGO DE CANCELACIÓN ----
        if (uiState.pedidoACancelar != null) {
            MervetaConfirmDialog(
                title = "Cancelar pedido",
                message = "¿Estás seguro de que quieres cancelar \"${uiState.pedidoACancelar?.titulo ?: ""}\"? Esta acción no se puede deshacer.",
                confirmText = "Cancelar pedido",
                cancelText = "Volver",
                isLoading = uiState.isCancelling,
                onConfirm = { viewModel.onCancelarConfirm() },
                onCancel = { viewModel.onCancelarCancel() }
            )
        }
    }
}