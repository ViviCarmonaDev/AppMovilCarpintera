package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.detail

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoSolicitud
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
        MervetaEditHeader(title = "Detalle de cotización", onBack = onBack)

        when {
            uiState.isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF8B5A2B))
            }

            uiState.cotizacion == null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.errorMessage ?: "No encontrada", color = Color(0xFFC5544A))
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
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text(
                            cotizacion.numeroPedido.ifBlank { "—" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5A2B)
                        )
                        EstadoCotizacionBadge(estado = cotizacion.estado)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Cliente: ${cotizacion.clienteNombre}",
                        fontSize = 14.sp,
                        color = Color(0xFF6B6B6B)
                    )

                    Spacer(Modifier.height(24.dp))

                    // Materiales
                    Text("Materiales", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    cotizacion.materiales.forEach { material ->
                        MaterialRow(material = material)
                        Spacer(Modifier.height(6.dp))
                    }

                    Spacer(Modifier.height(16.dp))

                    // Mano de obra
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5EFE7))
                            .padding(14.dp),
                        Arrangement.SpaceBetween
                    ) {
                        Text("Mano de obra", fontSize = 14.sp)
                        Text(
                            "S/ ${"%.2f".format(cotizacion.manoDeObra)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Costo total
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEFEFE9))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8A9E7A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Payments,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Costo total", fontSize = 12.sp, color = Color(0xFF6B6B6B))
                            Text(
                                cotizacion.costoTotalTexto,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Tiempo
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5EFE7))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Schedule,
                            null,
                            tint = Color(0xFF8B5A2B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Tiempo estimado: ", fontSize = 14.sp)
                        Text(
                            cotizacion.tiempoEstimadoTexto,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5A2B)
                        )
                    }

                    // Comentarios del carpintero
                    if (cotizacion.comentarios.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))
                        Text("Comentarios del carpintero", fontWeight = FontWeight.Bold)
                        Text(cotizacion.comentarios, fontSize = 14.sp, color = Color(0xFF4A4A4A))
                    }

                    // ==========================================
                    // CLIENTE: Observación + botón solicitar cambios
                    // ==========================================
                    if (uiState.puedoSolicitarCambios) {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "¿Deseas solicitar cambios?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.observacionInput,
                            onValueChange = { viewModel.onObservacionChange(it) },
                            placeholder = { Text("Escribe tu observación...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5A2B),
                                unfocusedBorderColor = Color(0xFF000000),
                                focusedTextColor = Color(0xFF000000),
                                unfocusedTextColor = Color(0xFF000000),
                                focusedPlaceholderColor = Color(0xFF6B6B6B),
                                unfocusedPlaceholderColor = Color(0xFF6B6B6B),
                                cursorColor = Color(0xFF8B5A2B),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        MervetaActionButton(
                            text = "Solicitar cambios",
                            backgroundColor = Color(0xFF8B5A2B),
                            contentColor = Color.White,
                            onClick = { viewModel.solicitarCambios() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // ==========================================
                    // CLIENTE: Si ya solicitó cambios
                    // ==========================================
                    if (cotizacion.solicitudCambio && !uiState.isCarpenter) {
                        Spacer(Modifier.height(16.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFCEBD0))
                                .padding(14.dp)
                        ) {
                            Text(
                                "Has solicitado cambios. Observación: \"${cotizacion.observacionCliente}\". Espera la respuesta del carpintero.",
                                fontSize = 13.sp,
                                color = Color(0xFF8B5A2B)
                            )
                        }
                    }

                    // ==========================================
                    // CLIENTE: Si los cambios fueron ACEPTADOS
                    // ==========================================
                    if (!uiState.isCarpenter && cotizacion.estadoSolicitud == EstadoSolicitud.ACEPTADA) {
                        Spacer(Modifier.height(16.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFD4E0D9))
                                .padding(14.dp)
                        ) {
                            Text(
                                "✓ El carpintero aceptó tus cambios y actualizó la cotización. Revisa los nuevos detalles.",
                                fontSize = 13.sp,
                                color = Color(0xFF2E4A3E)
                            )
                        }
                    }

                    // ==========================================
                    // CARPINTERO: Ver observación del cliente
                    // ==========================================
                    if (uiState.isCarpenter && cotizacion.solicitudCambio) {
                        Spacer(Modifier.height(16.dp))
                        Text("Observación del cliente", fontWeight = FontWeight.Bold)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFCEBD0))
                                .padding(14.dp)
                        ) {
                            Text(cotizacion.observacionCliente ?: "", fontSize = 14.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                            MervetaActionButton(
                                text = "Aceptar y editar",
                                backgroundColor = Color(0xFF4A5D3E),
                                contentColor = Color.White,
                                onClick = {
                                    viewModel.aceptarCambios(onNavigateToEdit = onEditClick)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            MervetaActionButton(
                                text = "Rechazar",
                                backgroundColor = Color(0xFFFADBD8),
                                contentColor = Color(0xFFC5544A),
                                onClick = { viewModel.onRechazarCambiosClick() },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // ==========================================
                    // Si el carpintero ya rechazó los cambios
                    // ==========================================
                    if (cotizacion.estadoSolicitud == EstadoSolicitud.RECHAZADA &&
                        cotizacion.respuestaCarpintero != null
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text("Respuesta del carpintero", fontWeight = FontWeight.Bold)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFADBD8))
                                .padding(14.dp)
                        ) {
                            Text(cotizacion.respuestaCarpintero ?: "", fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // ==========================================
                    // ACCIONES ORIGINALES
                    // ==========================================
                    if (uiState.puedoEditar) {
                        MervetaActionButton(
                            text = "Editar",
                            icon = Icons.Filled.Edit,
                            onClick = { onEditClick(cotizacion.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
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
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                            MervetaActionButton(
                                text = "Aceptar",
                                backgroundColor = Color(0xFF4A5D3E),
                                contentColor = Color.White,
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

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    // ============================================================
    // DIÁLOGOS
    // ============================================================

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

    // ============================================================
    // DIÁLOGO DE RECHAZAR CAMBIOS (con TextField)
    // ============================================================
    if (uiState.showRechazarCambiosDialog) {
        Dialog(onDismissRequest = { viewModel.onRechazarCambiosCancel() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Rechazar cambios",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "¿Estás seguro de rechazar los cambios solicitados?",
                        fontSize = 14.sp,
                        color = Color(0xFF6B6B6B)
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.respuestaInput,
                        onValueChange = { viewModel.onRespuestaChange(it) },
                        placeholder = { Text("Explica por qué rechazas los cambios...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8B5A2B),
                            unfocusedBorderColor = Color(0xFF000000),
                            focusedTextColor = Color(0xFF000000),
                            unfocusedTextColor = Color(0xFF000000),
                            focusedPlaceholderColor = Color(0xFF6B6B6B),
                            unfocusedPlaceholderColor = Color(0xFF6B6B6B),
                            cursorColor = Color(0xFF8B5A2B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MervetaActionButton(
                            text = "Cancelar",
                            backgroundColor = Color(0xFFF0EDE8),
                            contentColor = Color(0xFF2C2C2C),
                            onClick = { viewModel.onRechazarCambiosCancel() },
                            modifier = Modifier.weight(1f)
                        )
                        MervetaActionButton(
                            text = "Rechazar",
                            backgroundColor = Color(0xFFC5544A),
                            contentColor = Color.White,
                            onClick = { viewModel.rechazarCambios() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// COMPONENTES INTERNOS
// ============================================================

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