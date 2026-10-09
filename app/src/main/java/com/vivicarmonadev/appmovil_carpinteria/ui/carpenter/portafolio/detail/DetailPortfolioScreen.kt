package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Straighten
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Sell
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaImagesSection

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
                    // Galería de imágenes
                    MervetaImagesSection(
                        imagenes = item.imagenesUrls,
                        aspectRatio = 1f
                    )

                    // Card blanco superpuesto
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-24).dp)
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .background(Color(0xFFFFFFFF))
                            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp)
                    ) {
                        // Título
                        Text(
                            text = item.titulo,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )

                        // Descripción
                        if (item.descripcion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.descripcion,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF6B6B6B)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Card horizontal: Categoría + Material + Medidas
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5EFE7))
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Categoría
                            if (item.categoria.isNotBlank()) {
                                InfoColumn(
                                    icon = Icons.Filled.Chair,
                                    label = "Categoría",
                                    value = item.categoria,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Separador
                            if (item.material.isNotBlank() || item.medidasTexto != null) {
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(48.dp)
                                        .background(Color(0xFFE0DCD7))
                                )
                            }

                            // Material
                            if (item.material.isNotBlank()) {
                                InfoColumn(
                                    icon = Icons.Filled.Forest,
                                    label = "Material",
                                    value = item.material,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Separador
                            if (item.medidasTexto != null) {
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(48.dp)
                                        .background(Color(0xFFE0DCD7))
                                )

                                // Medidas
                                InfoColumn(
                                    icon = Icons.Filled.Straighten,
                                    label = "Medidas",
                                    value = item.medidasTexto ?: "—",
                                    modifier = Modifier.weight(1.3f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Card de precio
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFEFEFE9))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ícono circular
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                            when (item.tipoPrecio) {
                                                TipoPrecio.FIJO -> Color(0xFF8A9E7A)      // verde más fuerte
                                                TipoPrecio.A_TRATAR -> Color(0xFFA8B89A)  // verde más claro
                                            }
                                    ),

                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Sell,
                                    contentDescription = null,
                                    tint = Color(0xFFF9F7F5 ),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Precio",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B6B6B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.precioTexto,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C2C2C)
                                )
                            }

                            // Etiqueta FIJO / A TRATAR
                            if (item.etiquetaPrecio != null) {
                                TipoPrecioTag(
                                    texto = item.etiquetaPrecio!!,
                                    tipo = item.tipoPrecio
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Acciones (solo carpintero)
                        if (uiState.isCarpenter) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Botón editar
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

                                // Botón eliminar (cuadrado)
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
                    }
                }

                // Botón atrás flotante
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

    // Diálogo de eliminar
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

@Composable
private fun TipoPrecioTag(texto: String, tipo: TipoPrecio) {
    val (bgColor, textColor, icon) = when (tipo) {
        TipoPrecio.FIJO -> Triple(
            Color(0xFF4A5D3E),
            Color(0xFFF9F7F5),
            Icons.Filled.Lock )

        TipoPrecio.A_TRATAR -> Triple(
            Color(0xFF6B7F5C),
            Color(0xFFF9F7F5),
            Icons.Filled.Handshake
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}

@Composable
private fun InfoColumn(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF6B6B6B)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}