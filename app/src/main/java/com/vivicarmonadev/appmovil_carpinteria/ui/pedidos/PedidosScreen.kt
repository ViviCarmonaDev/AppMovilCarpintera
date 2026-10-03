package com.vivicarmonadev.appmovil_carpinteria.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.ui.pedidos.components.OrderCard
import com.vivicarmonadev.appmovil_carpinteria.ui.pedidos.components.PedidoFilterChips

@Composable
fun PedidosScreen(
    viewModel: PedidosViewModel,
    onCreateClick: () -> Unit,
    onEditClick: (Pedido) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F1))
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ---- HEADER ----
            PedidosHeader()

            // ---- BUSCADOR ----
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onClear = { viewModel.clearSearch() }
            )

            // ---- FILTROS ----
            PedidoFilterChips(
                estadoSeleccionado = uiState.estadoFiltro,
                onEstadoSelected = { viewModel.onEstadoFiltroChange(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // ---- CONTENIDO ----
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
                    EmptyState(onCreateClick = onCreateClick)
                }

                uiState.isFilterEmpty -> {
                    FilterEmptyState()
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
                                onEditClick = { onEditClick(pedido) },
                                onCancelClick = { viewModel.onCancelarClick(pedido) }
                            )
                        }
                    }
                }
            }
        }

        // ---- FAB (siempre visible si hay pedidos) ----
        if (!uiState.isLoading && !uiState.isEmpty) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5A2B))
                    .clickable(onClick = onCreateClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Nuevo pedido",
                    tint = Color(0xFFF9F7F5),
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        // ---- DIÁLOGO DE CANCELACIÓN ----
        if (uiState.pedidoACancelar != null) {
            CancelarPedidoDialog(
                titulo = uiState.pedidoACancelar?.titulo ?: "",
                isCancelling = uiState.isCancelling,
                onConfirm = { viewModel.onCancelarConfirm() },
                onCancel = { viewModel.onCancelarCancel() }
            )
        }
    }
}

// HEADER
@Composable
private fun PedidosHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5A2B))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Text(
                text = "Mis pedidos",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F7F5)
            )
            Text(
                text = "Gestiona tus solicitudes de carpintería",
                fontSize = 13.sp,
                color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
            )
        }
    }
}

// BÚSQUEDA

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        placeholder = {
            Text(
                text = "Buscar por nombre, categoría o material...",
                color = Color(0xFF6B6B6B),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF6B6B6B)
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Limpiar",
                        tint = Color(0xFF6B6B6B)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF8B5A2B),
            unfocusedBorderColor = Color(0xFFE0DCD7),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

// ESTADO VACÍO
@Composable
private fun EmptyState(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5EFE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Inbox,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Aún no tienes pedidos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Solicita tu primer mueble a un carpintero",
            fontSize = 14.sp,
            color = Color(0xFF6B6B6B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF8B5A2B))
                .clickable(onClick = onCreateClick)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color(0xFFF9F7F5),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NUEVO PEDIDO",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color(0xFFF9F7F5)
            )
        }
    }
}

// ESTADO VACÍO DE FILTRO

@Composable
private fun FilterEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = Color(0xFF8B5A2B).copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sin resultados",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No hay pedidos que coincidan con los filtros aplicados",
            fontSize = 14.sp,
            color = Color(0xFF6B6B6B),
            textAlign = TextAlign.Center
        )
    }
}

// ============================================
// DIÁLOGO DE CANCELACIÓN
// ============================================
@Composable
private fun CancelarPedidoDialog(
    titulo: String,
    isCancelling: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { if (!isCancelling) onCancel() },
        title = {
            Text(
                text = "Cancelar pedido",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que quieres cancelar \"$titulo\"? Esta acción no se puede deshacer.",
                fontSize = 14.sp
            )
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = onConfirm,
                enabled = !isCancelling
            ) {
                if (isCancelling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFC5544A)
                    )
                } else {
                    Text(
                        text = "Cancelar pedido",
                        color = Color(0xFFC5544A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onCancel,
                enabled = !isCancelling
            ) {
                Text(
                    text = "Volver",
                    color = Color(0xFF6B6B6B)
                )
            }
        }
    )
}