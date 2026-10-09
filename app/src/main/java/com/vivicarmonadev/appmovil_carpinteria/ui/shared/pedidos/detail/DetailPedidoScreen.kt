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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.fechaCorta
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.EstadoPedidoBadge
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaActionButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaImagesSection
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.fechaLarga
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.tiempoRelativo

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

                    // GALERÍA DE IMÁGENES

                    MervetaImagesSection(
                        imagenes = pedido.imagenesUrls,
                        aspectRatio = 1f
                    )

                    // CARD BLANCO SUPERPUESTO

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
                                start = 20.dp,
                                end = 20.dp,
                                top = 28.dp,
                                bottom = 24.dp
                            )
                    ) {

                        // ---- FILA SUPERIOR: número + tiempo + badge ----
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Número de pedido en chip beige
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF5EFE7))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = pedido.numeroPedido,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8B5A2B)
                                    )
                                }

                                // Tiempo relativo

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Schedule,
                                        contentDescription = null,
                                        tint = Color(0xFF8A8A8A),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = tiempoRelativo(pedido.createdAt),
                                        fontSize = 12.sp,
                                        color = Color(0xFF8A8A8A)
                                    )
                                }
                            }

                            EstadoPedidoBadge(estado = pedido.status)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ---- TÍTULO ----
                        Text(
                            text = pedido.titulo,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )

                        // ---- DESCRIPCIÓN ----
                        if (pedido.descripcion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = pedido.descripcion,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF6B6B6B)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // ---- CARD HORIZONTAL: Tipo + Medidas + Entrega ----
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5EFE7))
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tipo (material)
                            InfoColumn(
                                icon = Icons.Filled.Park,
                                label = "Material",
                                value = pedido.tipoMadera.ifBlank { "—" },
                                modifier = Modifier.weight(1f)
                            )

                            // Separador
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
                                value = pedido.medidasTexto ?: "—",
                                modifier = Modifier.weight(1f)
                            )

                            // Separador
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(48.dp)
                                    .background(Color(0xFFE0DCD7))
                            )

                            // Entrega
                            InfoColumn(
                                icon = Icons.Filled.CalendarToday,
                                label = "Entrega",
                                value = pedido.fechaEstimada?.let { fechaCorta(it) } ?: "—",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ---- CARD DEL CARPINTERO ----
                        if (!uiState.isCarpenter) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF5EFE7))
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF8B5A2B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color(0xFFF9F7F5),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Carpintero",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B6B6B)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (pedido.isLibre) {
                                            "Esperando carpintero"
                                        } else {
                                            pedido.carpinteroNombre ?: "Carpintero asignado"
                                        },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (pedido.isLibre) Color(0xFF8A8A8A) else Color(0xFF2C2C2C)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5A2B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ---- CARD DEL CLIENTE (si soy carpintero) ----
                        if (uiState.isCarpenter && pedido.clienteNombre.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF5EFE7))
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF8B5A2B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color(0xFFF9F7F5),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Cliente",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B6B6B)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = pedido.clienteNombre,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2C2C2C)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ---- PRESUPUESTO ----
                        if (pedido.presupuestoTexto != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFEFEFE9))
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ícono circular verde
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (pedido.status) {
                                                EstadoPedido.PENDIENTE -> Color(0xFFA8B89A)  // verde claro (pendiente)
                                                EstadoPedido.ACEPTADO -> Color(0xFF8A9E7A)   // verde medio (aceptado)
                                                EstadoPedido.EN_PROCESO -> Color(0xFF8A9E7A)
                                                EstadoPedido.TERMINADO -> Color(0xFF8A9E7A)
                                                EstadoPedido.ENTREGADO -> Color(0xFF8A9E7A)
                                                EstadoPedido.CANCELADO -> Color(0xFFA8B89A)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Sell,
                                        contentDescription = null,
                                        tint = Color(0xFFF9F7F5),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Presupuesto",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B6B6B)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = pedido.presupuestoTexto!!,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2C2C2C)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // ACCIONES SEGÚN ROL
                        // ---- CLIENTE ----
                        if (!uiState.isCarpenter) {
                            when (pedido.status) {
                                EstadoPedido.PENDIENTE -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        MervetaActionButton(
                                            text = "Editar",
                                            icon = Icons.Filled.Edit,
                                            backgroundColor = Color(0xFF8B5A2B),
                                            contentColor = Color(0xFFF9F7F5),
                                            onClick = { onEditClick(pedido.id) },
                                            modifier = Modifier.weight(1f)
                                        )

                                        MervetaActionButton(
                                            text = "Cancelar",
                                            backgroundColor = Color(0xFFFADBD8),
                                            contentColor = Color(0xFFC5544A),
                                            onClick = { viewModel.onCancelarClick() },
                                            modifier = Modifier.weight(1f)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Color(0xFFFADBD8))
                                                .clickable { viewModel.onEliminarClick() },
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



                                EstadoPedido.CANCELADO -> {
                                    MervetaActionButton(
                                        text = "Eliminar pedido",
                                        icon = Icons.Filled.Delete,
                                        backgroundColor = Color(0xFFFADBD8),
                                        contentColor = Color(0xFFC5544A),
                                        onClick = { viewModel.onEliminarClick() },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                else -> { /* Sin acciones */ }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

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
                                            onClick = { viewModel.onCambiarEstado(EstadoPedido.ACEPTADO) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                EstadoPedido.ACEPTADO -> {
                                    MervetaActionButton(
                                        text = "Iniciar trabajo",
                                        onClick = { viewModel.onCambiarEstado(EstadoPedido.EN_PROCESO) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                EstadoPedido.EN_PROCESO -> {
                                    MervetaActionButton(
                                        text = "Marcar como terminado",
                                        onClick = { viewModel.onCambiarEstado(EstadoPedido.TERMINADO) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                EstadoPedido.TERMINADO -> {
                                    MervetaActionButton(
                                        text = "Marcar como entregado",
                                        backgroundColor = Color(0xFF4A6B5D),
                                        onClick = { viewModel.onCambiarEstado(EstadoPedido.ENTREGADO) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                else -> { /* Sin acciones */ }
                            }
                        }
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

    // Diálogos
    if (uiState.showCancelDialog) {
        MervetaConfirmDialog(
            title = "Cancelar pedido",
            message = "¿Estás seguro de que quieres cancelar \"${uiState.pedido?.titulo ?: ""}\"?",
            confirmText = "Cancelar pedido",
            cancelText = "Volver",
            isLoading = uiState.isProcessing,
            onConfirm = { viewModel.onCancelarConfirm() },
            onCancel = { viewModel.onCancelarCancel() }
        )
    }

    if (uiState.showDeleteDialog) {
        MervetaConfirmDialog(
            title = "Eliminar pedido",
            message = "¿Estás seguro de que quieres eliminar este pedido?",
            confirmText = "Eliminar",
            cancelText = "Volver",
            isLoading = uiState.isProcessing,
            onConfirm = { viewModel.onEliminarConfirm() },
            onCancel = { viewModel.onEliminarCancel() }
        )
    }
}

// COLUMNA DE INFO CON ÍCONO (para el card horizontal)

@Composable
private fun InfoColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}