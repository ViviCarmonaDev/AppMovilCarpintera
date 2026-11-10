package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog

@Composable
fun DetailMaterialScreen(
    viewModel: DetailMaterialViewModel,
    materialId: String,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = materialId) {
        viewModel.initialize(materialId)
    }

    LaunchedEffect(key1 = uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            onDeleteSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFFFFF))
    ) {
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF8B5A2B))
            }

            uiState.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(uiState.errorMessage!!, color = Color(0xFFC5544A))
            }

            uiState.material != null -> {
                val material = uiState.material!!

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    // Imagen de cabecera
                    if (!material.imagenUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = material.imagenUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFFF5EFE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Payments, null, tint = Color(0xFF8B5A2B), modifier = Modifier.size(64.dp))
                        }
                    }

                    // Tarjeta superpuesta
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-24).dp)
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .background(Color(0xFFFFFFFF))
                            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp)
                    ) {
                        Text(
                            text = material.nombre,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )

                        if (material.descripcion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = material.descripcion,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF6B6B6B)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Precio referencial
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFEFEFE9))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF8A9E7A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Payments, null, tint = Color(0xFFF9F7F5), modifier = Modifier.size(22.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Precio referencial", fontSize = 12.sp, color = Color(0xFF6B6B6B))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = material.precioTexto,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C2C2C)
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
                                // Botón Editar
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF8B5A2B))
                                        .clickable { onEditClick(material.id) }
                                        .padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Edit, null, tint = Color(0xFFF9F7F5), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Editar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF9F7F5))
                                }

                                // Botón Eliminar
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFC5544A).copy(alpha = 0.1f))
                                        .clickable { viewModel.onDeleteClick() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Delete, "Eliminar", tint = Color(0xFFC5544A), modifier = Modifier.size(22.dp))
                                }
                            }
                        }
                    }
                }

                // Botón Atrás flotante
                Box(
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFFFFF).copy(alpha = 0.9f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color(0xFF2C2C2C), modifier = Modifier.size(22.dp))
                }
            }
        }
    }

    // Diálogo de eliminación
    if (uiState.showDeleteDialog) {
        MervetaConfirmDialog(
            title = "Eliminar material",
            message = "¿Estás seguro de que quieres eliminar \"${uiState.material?.nombre ?: ""}\"? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            isLoading = uiState.isDeleting,
            onConfirm = { viewModel.onDeleteConfirm() },
            onCancel = { viewModel.onDeleteCancel() }
        )
    }
}