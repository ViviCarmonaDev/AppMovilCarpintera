package com.vivicarmonadev.appmovil_carpinteria.ui.projects

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem

@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel,
    onCreateClick: () -> Unit,
    onEditClick: (PortfolioItem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFAF6F1))) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ---- HEADER ----
            PortfolioHeader(isCarpenter = uiState.isCarpenter)

            // ---- BUSCADOR ----
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onClear = { viewModel.clearSearch() }
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
                    EmptyState(
                        isCarpenter = uiState.isCarpenter,
                        onCreateClick = onCreateClick
                    )
                }

                uiState.isSearchEmpty -> {
                    SearchEmptyState(query = uiState.searchQuery)
                }

                else -> {
                    ItemsList(
                        items = uiState.filteredItems,
                        isCarpenter = uiState.isCarpenter,
                        onEditClick = onEditClick,
                        onDeleteClick = { viewModel.onDeleteClick(it) }
                    )
                }
            }
        }

        // ---- FAB (solo para carpinteros) ----
        if (uiState.isCarpenter && !uiState.isLoading && !uiState.isEmpty) {
            FloatingAddButton(
                onClick = onCreateClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            )
        }

        // ---- DIÁLOGO DE CONFIRMACIÓN DE ELIMINACIÓN ----
        if (uiState.itemToDelete != null) {
            DeleteConfirmDialog(
                itemTitle = uiState.itemToDelete?.titulo ?: "",
                isDeleting = uiState.isDeleting,
                onConfirm = { viewModel.onDeleteConfirm() },
                onCancel = { viewModel.onDeleteCancel() }
            )
        }
    }
}

// HEADER
@Composable
private fun PortfolioHeader(isCarpenter: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5A2B))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Text(
                text = if (isCarpenter) "Mis trabajos" else "Catálogo",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F7F5)
            )
            Text(
                text = if (isCarpenter)
                    "Gestiona tu portafolio de trabajos"
                else
                    "Explora los trabajos de carpinteros",
                fontSize = 13.sp,
                color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
            )
        }
    }
}

// BARRA DE BÚSQUEDA

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
                text = "Buscar por título, categoría o material...",
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

// LISTA DE TRABAJOS
@Composable
private fun ItemsList(
    items: List<PortfolioItem>,
    isCarpenter: Boolean,
    onEditClick: (PortfolioItem) -> Unit,
    onDeleteClick: (PortfolioItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            PortfolioItemCard(
                item = item,
                isCarpenter = isCarpenter,
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item) }
            )
        }
    }
}

// TARJETA DE UN TRABAJO

@Composable
private fun PortfolioItemCard(
    item: PortfolioItem,
    isCarpenter: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = item.titulo,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Categoría + Material
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (item.categoria.isNotBlank()) {
                Tag(text = item.categoria, color = Color(0xFFF5EFE7), textColor = Color(0xFF8B5A2B))
            }
            if (item.material.isNotBlank()) {
                Tag(text = item.material, color = Color(0xFFF5EFE7), textColor = Color(0xFF6B6B6B))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Descripción
        if (item.descripcion.isNotBlank()) {
            Text(
                text = item.descripcion,
                fontSize = 13.sp,
                color = Color(0xFF6B6B6B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Precio + Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.precioTexto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B)
            )

            if (isCarpenter) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF8B5A2B)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = Color(0xFFC5544A)
                        )
                    }
                }
            }
        }
    }
}

// TAG / CHIP

@Composable
private fun Tag(text: String, color: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

// ESTADO VACÍO

@Composable
private fun EmptyState(
    isCarpenter: Boolean,
    onCreateClick: () -> Unit
) {
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
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isCarpenter)
                "Aún no tienes trabajos publicados"
            else
                "Aún no hay trabajos en el catálogo",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isCarpenter)
                "Publica tu primer trabajo para atraer clientes"
            else
                "Cuando los carpinteros publiquen trabajos, aparecerán acá",
            fontSize = 14.sp,
            color = Color(0xFF6B6B6B),
            textAlign = TextAlign.Center
        )

        if (isCarpenter) {
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
                    text = "PUBLICAR TRABAJO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFFF9F7F5)
                )
            }
        }
    }
}

// ESTADO VACÍO DE BÚSQUEDA

@Composable
private fun SearchEmptyState(query: String) {
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
            text = "No se encontraron trabajos que coincidan con \"$query\"",
            fontSize = 14.sp,
            color = Color(0xFF6B6B6B),
            textAlign = TextAlign.Center
        )
    }
}

// FAB (botón flotante de agregar)

@Composable
private fun FloatingAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Color(0xFF8B5A2B))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Agregar trabajo",
            tint = Color(0xFFF9F7F5),
            modifier = Modifier.size(30.dp)
        )
    }
}

// DIÁLOGO DE CONFIRMACIÓN

@Composable
private fun DeleteConfirmDialog(
    itemTitle: String,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isDeleting) onCancel() },
        title = {
            Text(
                text = "Eliminar trabajo",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que quieres eliminar \"$itemTitle\"? Esta acción no se puede deshacer.",
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isDeleting
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFC5544A)
                    )
                } else {
                    Text(
                        text = "Eliminar",
                        color = Color(0xFFC5544A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                enabled = !isDeleting
            ) {
                Text(
                    text = "Cancelar",
                    color = Color(0xFF6B6B6B)
                )
            }
        }
    )
}