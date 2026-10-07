package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Forest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaTag

/**
 * Pantalla de detalle de un trabajo.
 * Diseño tipo "producto": imagen grande arriba, card con info abajo superpuesta.
 */

@Composable
fun DetailPortfolioScreen(
    viewModel: DetailPortfolioViewModel,
    itemId: String,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = itemId) {
        viewModel.initialize(itemId)
    }

    LaunchedEffect(key1 = uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            onDeleteSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8B5A2B))
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color(0xFFC5544A),
                        fontSize = 14.sp
                    )
                }
            }

            uiState.item != null -> {
                val item = uiState.item!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    // IMAGEN GRANDE ARRIBA (con scroll horizontal)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                    ) {
                        LazyRow(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .fillMaxSize()
                                        .background(Color(0xFFF5EFE7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Image,
                                            contentDescription = null,
                                            tint = Color(0xFF8B5A2B).copy(alpha = 0.4f),
                                            modifier = Modifier.size(72.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Imagen ${index + 1}",
                                            fontSize = 13.sp,
                                            color = Color(0xFF6B6B6B)
                                        )
                                    }
                                }
                            }
                        }

                        // Indicadores de página (puntitos) - opcional
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == 0) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == 0) Color(0xFFFFFFFF)
                                            else Color(0xFFFFFFFF).copy(alpha = 0.5f)
                                        )
                                )
                            }
                        }
                    }

                    // CARD CON INFO (superpuesto a la imagen)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-16).dp)   //  sube para superponerse
                            .clip(
                                RoundedCornerShape(
                                    topStart = 32.dp,
                                    topEnd = 32.dp
                                )
                            )
                            .background(Color(0xFFFFFFFF))
                            .padding(
                                start = 24.dp,
                                end = 24.dp,
                                top = 36.dp,
                                bottom = 32.dp
                            )
                    ) {
                        // Título + Precio

                        Text(
                            text = item.titulo,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Descripción
                        if (item.descripcion.isNotBlank()) {
                            Text(
                                text = item.descripcion,
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF4A4A4A)
                            )
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        // Precio + etiqueta en la misma línea
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = item.precioTexto,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5A2B)
                            )

                            if (item.etiquetaPrecio != null) {
                                TipoPrecioTag(
                                    texto = item.etiquetaPrecio!!,
                                    tipo = item.tipoPrecio
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // ---- CATEGORÍA + MATERIAL ----
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Categoría
                            if (item.categoria.isNotBlank()) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally //centra texto
                                ) {
                                    Text(
                                        text = "Categoría",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF6B6B6B)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    FeatureChip(
                                        icon = Icons.Filled.Chair,
                                        text = item.categoria
                                    )
                                }
                            }

                            // Material
                            if (item.material.isNotBlank()) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally  // centra texto
                                ) {
                                    Text(
                                        text = "Material",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF6B6B6B)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    FeatureChip(
                                        icon = Icons.Filled.Forest,
                                        text = item.material
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(110.dp))

                        // ---- ACCIONES (solo carpintero) ----
                        if (uiState.isCarpenter) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF8B5A2B))
                                        .clickable { onEditClick(item.id) }
                                        .padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = null,
                                        tint = Color(0xFFF9F7F5),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Editar",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF9F7F5)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFC5544A).copy(alpha = 0.1f))
                                        .clickable { viewModel.onDeleteClick() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color(0xFFC5544A),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // BOTÓN ATRÁS FLOTANTE

                Box(
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFFFFF).copy(alpha = 0.9f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color(0xFF2C2C2C),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }

    // ---- DIÁLOGO DE ELIMINAR ----
    if (uiState.showDeleteDialog) {
        MervetaConfirmDialog(
            title = "Eliminar trabajo",
            message = "¿Estás seguro de que quieres eliminar \"${uiState.item?.titulo ?: ""}\"? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            isLoading = uiState.isDeleting,
            onConfirm = { viewModel.onDeleteConfirm() },
            onCancel = { viewModel.onDeleteCancel() }
        )
    }
}

// ETIQUETA DE TIPO DE PRECIO

@Composable
private fun TipoPrecioTag(texto: String, tipo: TipoPrecio) {
    val (bgColor, textColor) = when (tipo) {
        TipoPrecio.FIJO -> Color(0xFF8B5A2B) to Color(0xFFF9F7F5)
        TipoPrecio.A_TRATAR -> Color(0xFFD4E0D9) to Color(0xFF2E4A3E)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}

// CHIP CON ÍCONO (categoría, material)

@Composable
private fun FeatureChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF5EFE7))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8B5A2B),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2C2C2C)
        )
    }
}