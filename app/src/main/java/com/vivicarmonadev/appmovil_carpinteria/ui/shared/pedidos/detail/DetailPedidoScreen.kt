package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.detail

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.EstadoPedidoBadge
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaActionButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaImagesSection
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaInfoCard
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaTag
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.fechaLarga
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.tiempoRelativo

/**
 * Pantalla de detalle de un pedido.
 *
 * Adapta las acciones según el rol:
 *  - Cliente: puede editar/cancelar/eliminar (si PENDIENTE).
 *  - Carpintero: puede tomar/avanzar estado.
 */
@Composable
fun DetailPedidoScreen(
    viewModel: DetailPedidoViewModel,
    pedidoId: String,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    onActionSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = pedidoId) {
        viewModel.initialize(pedidoId)
    }

    LaunchedEffect(uiState.actionSuccess) {
        if (uiState.actionSuccess) {
            viewModel.clearActionSuccess()
            onActionSuccess()
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

            uiState.errorMessage != null && uiState.pedido == null -> {
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

            uiState.pedido != null -> {
                val pedido = uiState.pedido!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    // ============================================
                    // GALERÍA DE IMÁGENES (arriba, ancho completo)
                    // ============================================
                    MervetaImagesSection(
                        imagenes = pedido.imagenesUrls,
                        aspectRatio = 1f
                    )

                    // ============================================
                    // CARD BLANCO (superpuesto con curvas)
                    // ============================================
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-24).dp)
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
                                top = 32.dp,
                                bottom = 32.dp
                            )
                    ) {

                        // ---- NÚMERO + BADGE + TIEMPO ----
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pedido.numeroPedido,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5A2B),
                                letterSpacing = 0.5.sp
                            )
                            EstadoPedidoBadge(estado = pedido.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = tiempoRelativo(pedido.createdAt),
                            fontSize = 12.sp,
                            color = Color(0xFF8A8A8A)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ---- INFO CLIENTE (si soy carpintero) ----
                        if (uiState.isCarpenter && pedido.clienteNombre.isNotBlank()) {
                            MervetaInfoCard(
                                icon = Icons.Filled.Person,
                                label = "Cliente",
                                value = pedido.clienteNombre
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // ---- INFO CARPINTERO (si soy cliente) ----
                        if (!uiState.isCarpenter) {
                            MervetaInfoCard(
                                icon = Icons.Filled.Person,
                                label = "Carpintero",
                                value = if (pedido.isLibre) {
                                    "Esperando carpintero"
                                } else {
                                    pedido.carpinteroNombre ?: "Carpintero asignado"
                                },
                                isMuted = pedido.isLibre
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ---- TÍTULO ----
                        Text(
                            text = pedido.titulo,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )

                        // ---- DESCRIPCIÓN ----
                        if (pedido.descripcion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = pedido.descripcion,
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF4A4A4A)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // ---- CARACTERÍSTICAS ----
                        Text(
                            text = "Características",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (pedido.categoria.isNotBlank()) {
                                MervetaTag(
                                    text = pedido.categoria,
                                    backgroundColor = Color(0xFF8B5A2B),
                                    textColor = Color(0xFFF9F7F5)
                                )
                            }
                            if (pedido.tipoMadera.isNotBlank()) {
                                MervetaTag(
                                    text = pedido.tipoMadera,
                                    backgroundColor = Color(0xFFF5EFE7),
                                    textColor = Color(0xFF2C2C2C)
                                )
                            }
                        }

                        if (pedido.medidasTexto != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            InfoRow(
                                icon = Icons.Filled.Park,
                                text = "Medidas: ${pedido.medidasTexto}"
                            )
                        }

                        if (pedido.fechaEstimada != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow(
                                icon = Icons.Filled.CalendarToday,
                                text = "Entrega: ${fechaLarga(pedido.fechaEstimada)}"
                            )
                        }

                        // ---- PRESUPUESTO ----
                        if (pedido.presupuestoTexto != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Presupuesto",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = pedido.presupuestoTexto!!,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5A2B)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // ============================================
                        // ACCIONES SEGÚN ROL Y ESTADO
                        // ============================================

                        // ---- CLIENTE ----
                        if (!uiState.isCarpenter) {
                            when (pedido.status) {
                                EstadoPedido.PENDIENTE -> {
                                    // 3 botones en fila: Editar + Cancelar + Eliminar (cuadrado)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Editar (grande)
                                        MervetaActionButton(
                                            text = "Editar",
                                            icon = Icons.Filled.Edit,
                                            backgroundColor = Color(0xFF8B5A2B),
                                            contentColor = Color(0xFFF9F7F5),
                                            onClick = { onEditClick(pedido.id) },
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Cancelar (mediano)
                                        MervetaActionButton(
                                            text = "Cancelar",
                                            backgroundColor = Color(0xFFFADBD8),
                                            contentColor = Color(0xFFC5544A),
                                            onClick = { viewModel.onCancelarClick() },
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Eliminar (cuadrado pequeño)
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Color(0xFFC5544A))
                                                .clickable { viewModel.onEliminarClick() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color(0xFFF9F7F5),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }

                                EstadoPedido.CANCELADO -> {
                                    // Solo Eliminar
                                    MervetaActionButton(
                                        text = "Eliminar pedido",
                                        icon = Icons.Filled.Delete,
                                        backgroundColor = Color(0xFFC5544A),
                                        contentColor = Color(0xFFF9F7F5),
                                        onClick = { viewModel.onEliminarClick() },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                else -> { /* Sin acciones */ }
                            }
                        }

                        // ---- CARPINTERO ----
                        if (uiState.isCarpenter) {
                            when (pedido.status) {
                                EstadoPedido.PENDIENTE -> {
                                    if (pedido.isLibre) {
                                        MervetaActionButton(
                                            text = "Tomar pedido",
                                            onClick = { viewModel.onTomarPedido() },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else if (pedido.carpenterUid == uiState.currentUid) {
                                        MervetaActionButton(
                                            text = "Aceptar pedido",
                                            onClick = {
                                                viewModel.onCambiarEstado(EstadoPedido.ACEPTADO)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                EstadoPedido.ACEPTADO -> {
                                    MervetaActionButton(
                                        text = "Iniciar trabajo",
                                        onClick = {
                                            viewModel.onCambiarEstado(EstadoPedido.EN_PROCESO)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                EstadoPedido.EN_PROCESO -> {
                                    MervetaActionButton(
                                        text = "Marcar como terminado",
                                        onClick = {
                                            viewModel.onCambiarEstado(EstadoPedido.TERMINADO)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                EstadoPedido.TERMINADO -> {
                                    MervetaActionButton(
                                        text = "Marcar como entregado",
                                        backgroundColor = Color(0xFF4A6B5D),
                                        onClick = {
                                            viewModel.onCambiarEstado(EstadoPedido.ENTREGADO)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                else -> { /* Sin acciones */ }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // ============================================
                // BOTÓN ATRÁS FLOTANTE
                // ============================================
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

    // ---- DIÁLOGO CANCELAR PEDIDO ----
    if (uiState.showCancelDialog) {
        MervetaConfirmDialog(
            title = "Cancelar pedido",
            message = "¿Estás seguro de que quieres cancelar \"${uiState.pedido?.titulo ?: ""}\"? Esta acción no se puede deshacer.",
            confirmText = "Cancelar pedido",
            cancelText = "Volver",
            isLoading = uiState.isProcessing,
            onConfirm = { viewModel.onCancelarConfirm() },
            onCancel = { viewModel.onCancelarCancel() }
        )
    }

    // ---- DIÁLOGO ELIMINAR PEDIDO ----
    if (uiState.showDeleteDialog) {
        MervetaConfirmDialog(
            title = "Eliminar pedido",
            message = "¿Estás seguro de que quieres eliminar este pedido? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            cancelText = "Volver",
            isLoading = uiState.isProcessing,
            onConfirm = { viewModel.onEliminarConfirm() },
            onCancel = { viewModel.onEliminarCancel() }
        )
    }
}

// ============================================
// FILA DE INFO CON ÍCONO
// ============================================

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8B5A2B),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF4A4A4A)
        )
    }
}