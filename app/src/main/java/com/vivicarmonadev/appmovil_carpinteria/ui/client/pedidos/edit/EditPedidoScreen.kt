package com.vivicarmonadev.appmovil_carpinteria.ui.client.pedidos.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPedidoScreen(
    viewModel: EditPedidoViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando guarda exitosamente, volvemos
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onSaveSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F1))
            .systemBarsPadding()
    ) {

        // ---- HEADER ----
        EditPedidoHeader(
            isEditMode = uiState.isEditMode,
            onBack = {
                val canExit = viewModel.onBackClick()
                if (canExit) onBack()
            }
        )

        // ---- CONTENIDO ----
        if (uiState.isLoading && uiState.titulo.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF8B5A2B))
            }
        } else if (uiState.errorMessage != null && uiState.titulo.isBlank()) {
            // Error de carga (ej: pedido no existe o no es editable)
            ErrorLoadState(
                message = uiState.errorMessage ?: "",
                onBack = onBack
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {

                // SECCIÓN 1: TIPO DE MUEBLE Y MEDIDAS

                SectionHeader(
                    number = "1",
                    title = "Tipo de Mueble y Medidas"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                AuthTextField(
                    value = uiState.titulo,
                    onValueChange = { viewModel.onTituloChange(it) },
                    label = "Título del pedido",
                    placeholder = "Ej. Mesa de comedor a medida",
                    errorMessage = uiState.tituloError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Categoría
                AuthTextField(
                    value = uiState.categoria,
                    onValueChange = { viewModel.onCategoriaChange(it) },
                    label = "Categoría de mueble",
                    placeholder = "Ej. Muebles, Puertas, Cocinas",
                    errorMessage = uiState.categoriaError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tipo de madera
                AuthTextField(
                    value = uiState.tipoMadera,
                    onValueChange = { viewModel.onTipoMaderaChange(it) },
                    label = "Tipo de madera preferida",
                    placeholder = "Ej. Roble, Pino, Cedro, Nogal",
                    errorMessage = uiState.tipoMaderaError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Medidas
                Text(
                    text = "Medidas (opcional)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C2C2C)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        AuthTextField(
                            value = uiState.anchoCm,
                            onValueChange = { viewModel.onAnchoChange(it) },
                            label = "Ancho (cm)",
                            placeholder = "Ej. 120",
                            errorMessage = uiState.anchoError,
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AuthTextField(
                            value = uiState.altoCm,
                            onValueChange = { viewModel.onAltoChange(it) },
                            label = "Alto (cm)",
                            placeholder = "Ej. 75",
                            errorMessage = uiState.altoError,
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AuthTextField(
                            value = uiState.profundidadCm,
                            onValueChange = { viewModel.onProfundidadChange(it) },
                            label = "Prof. (cm)",
                            placeholder = "Ej. 80",
                            errorMessage = uiState.profundidadError,
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // SECCIÓN 2: DETALLES Y REFERENCIAS

                SectionHeader(
                    number = "2",
                    title = "Detalles y Referencias"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                AuthTextField(
                    value = uiState.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = "Descripción del proyecto",
                    placeholder = "Describe los detalles de diseño, acabados o cualquier requerimiento especial...",
                    errorMessage = uiState.descripcionError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fecha estimada (DatePicker)
                FechaEstimadaField(
                    fechaEstimada = uiState.fechaEstimada,
                    onClick = { viewModel.openDatePicker() },
                    onClear = { viewModel.clearFecha() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Presupuesto
                AuthTextField(
                    value = uiState.presupuestoMax,
                    onValueChange = { viewModel.onPresupuestoChange(it) },
                    label = "Presupuesto máximo (opcional)",
                    placeholder = "Ej. 500 o 500.50",
                    errorMessage = uiState.presupuestoError,
                    keyboardType = KeyboardType.Decimal
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Imágenes de referencia (placeholder)
                ImagenesReferenciaPlaceholder()

                // ---- ERROR GENERAL ----
                if (uiState.errorMessage != null && uiState.titulo.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFC5544A),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ---- BOTÓN GUARDAR ----
                if (uiState.isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF8B5A2B),
                            strokeWidth = 3.dp
                        )
                    }
                } else {
                    PrimaryButton(
                        text = if (uiState.isEditMode) "Guardar cambios" else "Enviar pedido",
                        onClick = { viewModel.save() },
                        enabled = uiState.isFormValid
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // ---- DATE PICKER DIALOG ----
        if (uiState.showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.fechaEstimada
            )

            DatePickerDialog(
                onDismissRequest = { viewModel.closeDatePicker() },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.onFechaSeleccionada(datePickerState.selectedDateMillis)
                        }
                    ) {
                        Text(
                            text = "Aceptar",
                            color = Color(0xFF8B5A2B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeDatePicker() }) {
                        Text(
                            text = "Cancelar",
                            color = Color(0xFF6B6B6B)
                        )
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // ---- DIÁLOGO DE DESCARTAR CAMBIOS ----
        if (uiState.showDiscardDialog) {
            DiscardChangesDialog(
                onConfirm = {
                    viewModel.onDiscardConfirm()
                    onBack()
                },
                onCancel = { viewModel.onDiscardCancel() }
            )
        }
    }
}

// HEADER CON BOTÓN ATRÁS
@Composable
private fun EditPedidoHeader(
    isEditMode: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5A2B))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = Color(0xFFF9F7F5),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (isEditMode) "Editar pedido" else "Solicitar pedido",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF9F7F5)
        )
    }
}

// HEADER DE SECCIÓN NUMERADA
@Composable
private fun SectionHeader(number: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF8B5A2B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F7F5)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C)
        )
    }
}

// CAMPO DE FECHA ESTIMADA (con DatePicker)
@Composable
private fun FechaEstimadaField(
    fechaEstimada: Long?,
    onClick: () -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Fecha estimada de entrega (opcional)",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2C2C2C).copy(alpha = 0.05f))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (fechaEstimada != null) {
                    formatearFecha(fechaEstimada)
                } else {
                    "Seleccionar fecha"
                },
                fontSize = 15.sp,
                color = if (fechaEstimada != null) {
                    Color(0xFF2C2C2C)
                } else {
                    Color(0xFF2C2C2C).copy(alpha = 0.4f)
                },
                modifier = Modifier.weight(1f)
            )

            if (fechaEstimada != null) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClear),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Limpiar fecha",
                        tint = Color(0xFF6B6B6B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// PLACEHOLDER DE IMÁGENES DE REFERENCIA
@Composable
private fun ImagenesReferenciaPlaceholder() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Imágenes de referencia (opcional)",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5EFE7))
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0DCD7),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = null,
                    tint = Color(0xFF8B5A2B).copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sube fotos, bocetos o planos",
                    fontSize = 13.sp,
                    color = Color(0xFF6B6B6B),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PNG, JPG (Máx 10MB) — Próximamente",
                    fontSize = 11.sp,
                    color = Color(0xFF8A8A8A)
                )
            }
        }
    }
}

// ESTADO DE ERROR DE CARGA
@Composable
private fun ErrorLoadState(
    message: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😕",
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFF2C2C2C),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF8B5A2B))
                .clickable(onClick = onBack)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VOLVER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color(0xFFF9F7F5)
            )
        }
    }
}

// DIÁLOGO DE DESCARTAR CAMBIOS
@Composable
private fun DiscardChangesDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Descartar cambios",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Tenés cambios sin guardar. ¿Querés salir igual?",
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Salir sin guardar",
                    color = Color(0xFFC5544A),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    text = "Seguir editando",
                    color = Color(0xFF6B6B6B)
                )
            }
        }
    )
}

// HELPERS
private fun formatearFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "PE"))
    return sdf.format(Date(timestamp))
}