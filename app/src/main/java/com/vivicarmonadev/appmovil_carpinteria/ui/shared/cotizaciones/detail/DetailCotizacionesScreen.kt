package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.detail

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.ItemCotizacion
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaActionButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEditHeader

@Composable
fun DetailCotizacionScreen(
    viewModel: DetailCotizacionViewModel,
    cotizacionId: String,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    onAnularSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(cotizacionId) {
        viewModel.initialize(cotizacionId)
    }

    LaunchedEffect(uiState.actionSuccess) {
        if (uiState.actionSuccess) {
            viewModel.clearActionSuccess()
            onAnularSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .systemBarsPadding()
    ) {
        MervetaEditHeader(
            title = "Detalle de cotización",
            onBack = onBack
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8B5A2B))
                }
            }

            uiState.cotizacion == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Cotización no encontrada",
                        color = Color(0xFFC5544A),
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                val cotizacion = uiState.cotizacion!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    // Número + estado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cotizacion.numeroPedido.ifBlank { "—" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5A2B)
                        )
                        EstadoCotizacionBadge(estado = cotizacion.estado)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cliente
                    Text(
                        text = "Cliente: ${cotizacion.clienteNombre}",
                        fontSize = 14.sp,
                        color = Color(0xFF6B6B6B)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sección materiales
                    Text(
                        text = "Materiales",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    cotizacion.materiales.forEach { material ->
                        MaterialRow(material = material)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mano de obra
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5EFE7))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mano de obra",
                            fontSize = 14.sp,
                            color = Color(0xFF2C2C2C)
                        )
                        Text(
                            text = "S/ ${"%.2f".format(cotizacion.manoDeObra)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Costo total
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
                                text = "Costo total",
                                fontSize = 12.sp,
                                color = Color(0xFF6B6B6B)
                            )
                            Text(
                                text = cotizacion.costoTotalTexto,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tiempo estimado
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5EFE7))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF8B5A2B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tiempo estimado: ",
                            fontSize = 14.sp,
                            color = Color(0xFF2C2C2C)
                        )
                        Text(
                            text = cotizacion.tiempoEstimadoTexto,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5A2B)
                        )
                    }

                    // Comentarios
                    if (cotizacion.comentarios.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Comentarios",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cotizacion.comentarios,
                            fontSize = 14.sp,
                            color = Color(0xFF4A4A4A)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // ACCIONES
                    if (uiState.puedoEditar) {
                        MervetaActionButton(
                            text = "Editar",
                            icon = Icons.Filled.Edit,
                            onClick = { onEditClick(cotizacion.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (uiState.puedoAnular) {
                        MervetaActionButton(
                            text = "Anular cotización",
                            icon = Icons.Filled.Delete,
                            backgroundColor = Color(0xFFFADBD8),
                            contentColor = Color(0xFFC5544A),
                            onClick = { viewModel.onAnularClick() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState.puedoAceptarRechazar) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MervetaActionButton(
                                text = "Aceptar",
                                backgroundColor = Color(0xFF4A5D3E),
                                contentColor = Color(0xFFF9F7F5),
                                onClick = { viewModel.onAceptarClick() },
                                modifier = Modifier.weight(1f)
                            )
                            MervetaActionButton(
                                text = "Rechazar",
                                backgroundColor = Color(0xFFFADBD8),
                                contentColor = Color(0xFFC5544A),
                                onClick = { viewModel.onRechazarClick() },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Diálogos
    if (uiState.showAnularDialog) {
        MervetaConfirmDialog(
            title = "Anular cotización",
            message = "¿Estás seguro de que quieres anular esta cotización?",
            confirmText = "Anular",
            cancelText = "Cancelar",
            isLoading = uiState.isProcessing,
            onConfirm = { viewModel.onAnularConfirm() },
            onCancel = { viewModel.onAnularCancel() }
        )
    }

    if (uiState.showAceptarDialog) {
        MervetaConfirmDialog(
            title = "Aceptar cotización",
            message = "¿Confirmas la cotización por ${uiState.cotizacion?.costoTotalTexto}?",
            confirmText = "Aceptar",
            cancelText = "Cancelar",
            isLoading = uiState.isProcessing,
            onConfirm = { viewModel.onAceptarConfirm() },
            onCancel = { viewModel.onAceptarCancel() }
        )
    }

    if (uiState.showRechazarDialog) {
        MervetaConfirmDialog(
            title = "Rechazar cotización",
            message = "¿Estás seguro de que quieres rechazar esta cotización?",
            confirmText = "Rechazar",
            cancelText = "Cancelar",
            isLoading = uiState.isProcessing,
            onConfirm = { viewModel.onRechazarConfirm() },
            onCancel = { viewModel.onRechazarCancel() }
        )
    }
}

// FILA DE MATERIAL
@Composable
private fun MaterialRow(material: ItemCotizacion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5EFE7))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = material.nombre,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
            Text(
                text = "${material.cantidad} ${material.unidad} × S/ ${"%.2f".format(material.precio)}",
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B)
            )
        }
        Text(
            text = "S/ ${"%.2f".format(material.precio * material.cantidad)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B5A2B)
        )
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