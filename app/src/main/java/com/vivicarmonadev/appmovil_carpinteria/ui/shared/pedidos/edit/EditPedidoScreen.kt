package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.edit

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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaGalleryPicker
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpinteroResumen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaDateField
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaDiscardDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEditHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaSectionHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPedidoScreen(
    viewModel: EditPedidoViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

        MervetaEditHeader(
            title = "Editar pedido",
            onBack = {
                val canExit = viewModel.onBackClick()
                if (canExit) onBack()
            }
        )

        if (uiState.isLoading && uiState.titulo.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF8B5A2B))
            }
        } else if (uiState.errorMessage != null && uiState.titulo.isBlank()) {
            ErrorLoadState(message = uiState.errorMessage ?: "", onBack = onBack)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {

                // Imágenes
                MervetaGalleryPicker(
                    imagenes = uiState.imagenesUrls,
                    label = "Imágenes de referencia",
                    maxImagenes = 3,
                    onAgregarClick = { /* TODO */ },
                    onEliminarClick = { index -> viewModel.onEliminarImagen(index) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // SECCIÓN 1
                MervetaSectionHeader(number = "1", title = "Tipo de Mueble y Medidas")
                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.titulo,
                    onValueChange = { viewModel.onTituloChange(it) },
                    label = "Título del pedido",
                    placeholder = "Ej. Mesa de comedor a medida",
                    errorMessage = uiState.tituloError
                )
                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.categoria,
                    onValueChange = { viewModel.onCategoriaChange(it) },
                    label = "Categoría de mueble",
                    placeholder = "Ej. Muebles, Puertas, Cocinas",
                    errorMessage = uiState.categoriaError
                )
                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.tipoMadera,
                    onValueChange = { viewModel.onTipoMaderaChange(it) },
                    label = "Tipo de madera preferida",
                    placeholder = "Ej. Roble, Pino, Cedro, Nogal",
                    errorMessage = uiState.tipoMaderaError
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                            label = "Ancho",
                            placeholder = "cm",
                            errorMessage = null,
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AuthTextField(
                            value = uiState.altoCm,
                            onValueChange = { viewModel.onAltoChange(it) },
                            label = "Alto",
                            placeholder = "cm",
                            errorMessage = null,
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AuthTextField(
                            value = uiState.profundidadCm,
                            onValueChange = { viewModel.onProfundidadChange(it) },
                            label = "Prof.",
                            placeholder = "cm",
                            errorMessage = null,
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // SECCIÓN 2
                MervetaSectionHeader(number = "2", title = "Detalles y Referencias")
                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = "Descripción del proyecto",
                    placeholder = "Describe los detalles...",
                    errorMessage = uiState.descripcionError
                )
                Spacer(modifier = Modifier.height(16.dp))

                MervetaDateField(
                    label = "Fecha estimada de entrega (opcional)",
                    fecha = uiState.fechaEstimada,
                    onClick = { viewModel.openDatePicker() },
                    onClear = { viewModel.clearFecha() }
                )
                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.presupuestoMax,
                    onValueChange = { viewModel.onPresupuestoChange(it) },
                    label = "Presupuesto máximo (opcional)",
                    placeholder = "Ej. 500 o 500.50",
                    errorMessage = uiState.presupuestoError,
                    keyboardType = KeyboardType.Decimal
                )

                Spacer(modifier = Modifier.height(32.dp))

                // SECCIÓN 3
                MervetaSectionHeader(number = "3", title = "Carpintero")
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Elige un carpintero o deja el pedido libre.",
                    fontSize = 13.sp,
                    color = Color(0xFF6B6B6B),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                CarpinteroDropdown(
                    carpinteros = uiState.carpinterosDisponibles,
                    seleccionado = uiState.carpinterosDisponibles.find { it.uid == uiState.carpenterUid },
                    onSeleccionar = { carpintero ->
                        viewModel.onCarpinteroChange(
                            carpenterUid = carpintero?.uid,
                            carpinteroNombre = carpintero?.displayName
                        )
                    }
                )

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

                if (uiState.isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8B5A2B), strokeWidth = 3.dp)
                    }
                } else {
                    PrimaryButton(
                        text = "Guardar cambios",
                        onClick = { viewModel.save() },
                        enabled = uiState.isFormValid
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // DatePicker
        if (uiState.showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.fechaEstimada)

            DatePickerDialog(
                onDismissRequest = { viewModel.closeDatePicker() },
                confirmButton = {
                    TextButton(onClick = { viewModel.onFechaSeleccionada(datePickerState.selectedDateMillis) }) {
                        Text(text = "Aceptar", color = Color(0xFF8B5A2B), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeDatePicker() }) {
                        Text(text = "Cancelar", color = Color(0xFF6B6B6B))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Descartar
        if (uiState.showDiscardDialog) {
            MervetaDiscardDialog(
                onConfirm = { viewModel.onDiscardConfirm(); onBack() },
                onCancel = { viewModel.onDiscardCancel() }
            )
        }
    }
}

// ERROR LOAD STATE

@Composable
private fun ErrorLoadState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "😕", fontSize = 48.sp)
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

// DROPDOWN DE CARPINTERO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarpinteroDropdown(
    carpinteros: List<CarpinteroResumen>,
    seleccionado: CarpinteroResumen?,
    onSeleccionar: (CarpinteroResumen?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textoMostrado = seleccionado?.displayName ?: "Dejar libre"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = textoMostrado,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5EFE7),
                unfocusedContainerColor = Color(0xFFF5EFE7),
                focusedBorderColor = Color(0xFF8B5A2B),
                unfocusedBorderColor = Color(0xFFE0DCD7),
                focusedTextColor = Color(0xFF2C2C2C),
                unfocusedTextColor = Color(0xFF2C2C2C)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFFFFFFFF))
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Dejar libre",
                        color = if (seleccionado == null) Color(0xFF8B5A2B) else Color(0xFF2C2C2C),
                        fontWeight = if (seleccionado == null) FontWeight.Bold else FontWeight.Normal
                    )
                },
                onClick = { onSeleccionar(null); expanded = false }
            )

            carpinteros.forEach { carpintero ->
                val isSelected = seleccionado?.uid == carpintero.uid
                DropdownMenuItem(
                    text = {
                        Text(
                            text = carpintero.displayName,
                            color = if (isSelected) Color(0xFF8B5A2B) else Color(0xFF2C2C2C),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = { onSeleccionar(carpintero); expanded = false }
                )
            }
        }
    }
}