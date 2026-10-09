package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.create

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.ItemCotizacion
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaDiscardDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEditHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaSectionHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

/**
 * Pantalla para CREAR una cotización sobre un pedido.
 */
@Composable
fun CreateCotizacionScreen(
    viewModel: CreateCotizacionViewModel,
    pedidoId: String,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Inicializar con el pedido
    LaunchedEffect(pedidoId) {
        viewModel.initialize(pedidoId)
    }

    // Navegar atrás al guardar
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onSaveSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .systemBarsPadding()
    ) {
        // Header
        MervetaEditHeader(
            title = "Nueva cotización",
            onBack = {
                val canExit = viewModel.onBackClick()
                if (canExit) onBack()
            }
        )

        // Contenido
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8B5A2B))
                }
            }

            uiState.errorCarga != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorCarga ?: "",
                        color = Color(0xFFC5544A),
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    // ---- INFO DEL PEDIDO ----
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5EFE7))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Pedido ${uiState.numeroPedido}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5A2B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.tituloPedido,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C)
                            )
                            if (uiState.descripcionPedido.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = uiState.descripcionPedido,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B6B6B),
                                    maxLines = 2
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cliente: ${uiState.clienteNombre}",
                                fontSize = 12.sp,
                                color = Color(0xFF6B6B6B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ---- SECCIÓN 1: MATERIALES ----
                    MervetaSectionHeader(number = "1", title = "Materiales")

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.materiales.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5EFE7))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin materiales. Agrega al menos uno.",
                                fontSize = 13.sp,
                                color = Color(0xFF6B6B6B)
                            )
                        }
                    } else {
                        uiState.materiales.forEachIndexed { index, material ->
                            MaterialItem(
                                material = material,
                                onDelete = { viewModel.onEliminarMaterial(index) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón agregar material
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF8B5A2B))
                            .clickable { viewModel.onOpenMaterialDialog() }
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color(0xFFF9F7F5),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Agregar material",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF9F7F5)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // ---- SECCIÓN 2: MANO DE OBRA ----
                    MervetaSectionHeader(number = "2", title = "Mano de obra")

                    Spacer(modifier = Modifier.height(12.dp))

                    AuthTextField(
                        value = uiState.manoDeObra,
                        onValueChange = { viewModel.onManoDeObraChange(it) },
                        label = "Costo de mano de obra (S/)",
                        placeholder = "Ej. 200",
                        errorMessage = uiState.manoDeObraError,
                        keyboardType = KeyboardType.Decimal
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // ---- SECCIÓN 3: TIEMPO ESTIMADO ----
                    MervetaSectionHeader(number = "3", title = "Tiempo estimado")

                    Spacer(modifier = Modifier.height(12.dp))

                    AuthTextField(
                        value = uiState.tiempoEstimadoDias,
                        onValueChange = { viewModel.onTiempoEstimadoChange(it) },
                        label = "Días estimados de entrega",
                        placeholder = "Ej. 15",
                        errorMessage = uiState.tiempoEstimadoError,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ---- COMENTARIOS ----
                    AuthTextField(
                        value = uiState.comentarios,
                        onValueChange = { viewModel.onComentariosChange(it) },
                        label = "Comentarios (opcional)",
                        placeholder = "Detalles adicionales sobre la cotización...",
                        errorMessage = null
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ---- TOTAL ----
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEFEFE9))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Costo total",
                                fontSize = 12.sp,
                                color = Color(0xFF6B6B6B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.costoTotalTexto,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ---- ERROR GENERAL ----
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            fontSize = 13.sp,
                            color = Color(0xFFC5544A),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // ---- BOTÓN GUARDAR ----
                    if (uiState.isSaving) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(color = Color(0xFF8B5A2B), strokeWidth = 3.dp)
                        }
                    } else {
                        PrimaryButton(
                            text = "Enviar cotización",
                            onClick = { viewModel.save() },
                            enabled = uiState.isFormValid
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // ---- DIÁLOGO DE AGREGAR MATERIAL ----
    if (uiState.showMaterialDialog) {
        AgregarMaterialDialog(
            nombre = uiState.nuevoMaterialNombre,
            cantidad = uiState.nuevoMaterialCantidad,
            precio = uiState.nuevoMaterialPrecio,
            unidad = uiState.nuevoMaterialUnidad,
            onNombreChange = { viewModel.onNuevoMaterialNombreChange(it) },
            onCantidadChange = { viewModel.onNuevoMaterialCantidadChange(it) },
            onPrecioChange = { viewModel.onNuevoMaterialPrecioChange(it) },
            onUnidadChange = { viewModel.onNuevoMaterialUnidadChange(it) },
            onConfirm = { viewModel.onAgregarMaterial() },
            onDismiss = { viewModel.onCloseMaterialDialog() }
        )
    }

    // ---- DIÁLOGO DESCARTAR ----
    if (uiState.showDiscardDialog) {
        MervetaDiscardDialog(
            onConfirm = {
                viewModel.onDiscardConfirm()
                onBack()
            },
            onCancel = { viewModel.onDiscardCancel() }
        )
    }
}

// ============================================
// ITEM DE MATERIAL
// ============================================
@Composable
private fun MaterialItem(
    material: ItemCotizacion,
    onDelete: () -> Unit
) {
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
            Spacer(modifier = Modifier.height(2.dp))
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

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFC5544A).copy(alpha = 0.1f))
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Eliminar",
                tint = Color(0xFFC5544A),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// DIÁLOGO AGREGAR MATERIAL

@Composable
private fun AgregarMaterialDialog(
    nombre: String,
    cantidad: String,
    precio: String,
    unidad: String,
    onNombreChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onPrecioChange: (String) -> Unit,
    onUnidadChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF5EFE7),          // blanco
        titleContentColor = Color(0xFF2C2C2C),        // oscuro
        textContentColor = Color(0xFF2C2C2C),
        title = { Text(text = "Agregar material", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                AuthTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = "Nombre",
                    placeholder = "Ej. Madera de cedro",
                    errorMessage = null
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthTextField(
                    value = cantidad,
                    onValueChange = onCantidadChange,
                    label = "Cantidad",
                    placeholder = "Ej. 2",
                    errorMessage = null,
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthTextField(
                    value = unidad,
                    onValueChange = onUnidadChange,
                    label = "Unidad",
                    placeholder = "Ej. unidad, metro, pliego",
                    errorMessage = null
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthTextField(
                    value = precio,
                    onValueChange = onPrecioChange,
                    label = "Precio unitario (S/)",
                    placeholder = "Ej. 150",
                    errorMessage = null,
                    keyboardType = KeyboardType.Decimal
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Agregar",
                    color = Color(0xFF8B5A2B),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = Color(0xFF6B6B6B))
            }
        }
    )
}