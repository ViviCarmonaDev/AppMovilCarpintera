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
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.EstadoPedidoBadge
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaActionButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaImagesSection
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaInfoCard
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.fechaCorta
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.tiempoRelativo

@Composable
fun DetailPedidoScreen(
    viewModel: DetailPedidoViewModel,
    pedidoId: String,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    onActionSuccess: () -> Unit,
    onCrearCotizacion: (String) -> Unit
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
                    // Galería de imágenes
                    MervetaImagesSection(
                        imagenes = pedido.imagenesUrls,
                        aspectRatio = 1f
                    )

                    // Card blanco superpuesto
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-24).dp)
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .background(Color(0xFFFFFFFF))
                            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 40.dp) // Espaciado inferior
                    ) {
                        // Fila superior: número + tiempo + badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
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

                        // Título
                        Text(
                            text = pedido.titulo,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )

                        // Descripción
                        if (pedido.descripcion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = pedido.descripcion,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF6B6B6B)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Info del cliente (si soy carpintero)
                        if (uiState.isCarpenter && pedido.clienteNombre.isNotBlank()) {
                            MervetaInfoCard(
                                icon = Icons.Filled.Person,
                                label = "Cliente",
                                value = pedido.clienteNombre
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Info del carpintero (si soy cliente)
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
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Card horizontal: Tipo + Medidas + Entrega
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5EFE7))
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InfoColumn(
                                icon = Icons.Filled.Chair,
                                label = "Tipo",
                                value = pedido.tipoMadera.ifBlank { "—" },
                                modifier = Modifier.weight(0.9f)
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(48.dp)
                                    .background(Color(0xFFE0DCD7))
                            )

                            InfoColumn(
                                icon = Icons.Filled.Straighten,
                                label = "Medidas",
                                value = pedido.medidasTexto ?: "—",
                                modifier = Modifier.weight(1.3f)
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(48.dp)
                                    .background(Color(0xFFE0DCD7))
                            )

                            InfoColumn(
                                icon = Icons.Filled.CalendarToday,
                                label = "Entrega",
                                value = pedido.fechaEstimada?.let { fechaCorta(it) } ?: "—",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Card de presupuesto
                        if (pedido.presupuestoTexto != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFEFEFE9))
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF8A9E7A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Payments,
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
                        }

                        // COTIZACIONES RECIBIDAS (solo para el cliente)

                        if (!uiState.isCarpenter && uiState.cotizaciones.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Cotizaciones recibidas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            uiState.cotizaciones.forEach { cotizacion ->
                                CotizacionRecibidaCard(
                                    cotizacion = cotizacion,
                                    onAceptar = { viewModel.onAceptarCotizacionClick(cotizacion) },
                                    onRechazar = { viewModel.onRechazarCotizacionClick(cotizacion) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

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
                                    MervetaActionButton(
                                        text = "Crear cotización",
                                        icon = Icons.Filled.RequestQuote,
                                        onClick = { onCrearCotizacion(pedido.id) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
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

    // DIÁLOGOS

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

    if (uiState.cotizacionAAceptar != null) {
        MervetaConfirmDialog(
            title = "Aceptar cotización",
            message = "¿Confirmas la cotización de ${uiState.cotizacionAAceptar?.carpinteroNombre} por ${uiState.cotizacionAAceptar?.costoTotalTexto}?",
            confirmText = "Aceptar",
            cancelText = "Cancelar",
            isLoading = uiState.isProcesandoCotizacion,
            onConfirm = { viewModel.onAceptarCotizacionConfirm() },
            onCancel = { viewModel.onAceptarCotizacionCancel() }
        )
    }

    if (uiState.cotizacionARechazar != null) {
        MervetaConfirmDialog(
            title = "Rechazar cotización",
            message = "¿Estás seguro de que quieres rechazar la cotización de ${uiState.cotizacionARechazar?.carpinteroNombre}?",
            confirmText = "Rechazar",
            cancelText = "Cancelar",
            isLoading = uiState.isProcesandoCotizacion,
            onConfirm = { viewModel.onRechazarCotizacionConfirm() },
            onCancel = { viewModel.onRechazarCotizacionCancel() }
        )
    }
}

// CARD DE COTIZACIÓN RECIBIDA

@Composable
private fun CotizacionRecibidaCard(
    cotizacion: Cotizacion,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5EFE7))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cotizacion.carpinteroNombre,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
            EstadoCotizacionBadge(estado = cotizacion.estado)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = cotizacion.costoTotalTexto,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B5A2B)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = null,
                tint = Color(0xFF6B6B6B),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = cotizacion.tiempoEstimadoTexto,
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B)
            )
        }

        if (cotizacion.comentarios.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cotizacion.comentarios,
                fontSize = 13.sp,
                color = Color(0xFF4A4A4A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (cotizacion.estado == EstadoCotizacion.PENDIENTE) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MervetaActionButton(
                    text = "Aceptar",
                    backgroundColor = Color(0xFF4A5D3E),
                    contentColor = Color(0xFFF9F7F5),
                    onClick = onAceptar,
                    modifier = Modifier.weight(1f)
                )
                MervetaActionButton(
                    text = "Rechazar",
                    backgroundColor = Color(0xFFFADBD8),
                    contentColor = Color(0xFFC5544A),
                    onClick = onRechazar,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// BADGE DE COTIZACIÓN

@Composable
private fun EstadoCotizacionBadge(estado: EstadoCotizacion) {
    val (bgColor, textColor) = when (estado) {
        EstadoCotizacion.PENDIENTE -> Color(0xFFFCEBD0) to Color(0xFF8B5A2B)
        EstadoCotizacion.ACEPTADA -> Color(0xFFD4E0D9) to Color(0xFF2E4A3E)
        EstadoCotizacion.RECHAZADA -> Color(0xFFFADBD8) to Color(0xFFC5544A)
        EstadoCotizacion.ANULADA -> Color(0xFFE0DCD7) to Color(0xFF6B6B6B)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado.toDisplayText().uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}

// COLUMNA DE INFO CON ÍCONO
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
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}