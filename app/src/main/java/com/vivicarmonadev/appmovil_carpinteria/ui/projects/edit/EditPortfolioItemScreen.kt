package com.vivicarmonadev.appmovil_carpinteria.ui.projects.edit

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

@Composable
fun EditPortfolioItemScreen(
    viewModel: EditPortfolioItemViewModel,
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
        EditPortfolioHeader(
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {

                // ---- TÍTULO ----
                AuthTextField(
                    value = uiState.titulo,
                    onValueChange = { viewModel.onTituloChange(it) },
                    label = "Título del trabajo",
                    placeholder = "Ej. Mesa de comedor nogal",
                    errorMessage = uiState.tituloError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- DESCRIPCIÓN ----
                AuthTextField(
                    value = uiState.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = "Descripción",
                    placeholder = "Describe el trabajo, sus dimensiones, acabados...",
                    errorMessage = uiState.descripcionError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- CATEGORÍA ----
                AuthTextField(
                    value = uiState.categoria,
                    onValueChange = { viewModel.onCategoriaChange(it) },
                    label = "Categoría",
                    placeholder = "Ej. Muebles, Puertas, Cocinas",
                    errorMessage = uiState.categoriaError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- MATERIAL ----
                AuthTextField(
                    value = uiState.material,
                    onValueChange = { viewModel.onMaterialChange(it) },
                    label = "Material",
                    placeholder = "Ej. Roble, Pino, Cedro, MDF",
                    errorMessage = uiState.materialError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- TIPO DE PRECIO ----
                Text(
                    text = "Tipo de precio",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C2C2C)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TipoPrecioChip(
                        text = "FIJO",
                        subtitle = "Precio final",
                        isSelected = uiState.tipoPrecio == TipoPrecio.FIJO,
                        onClick = { viewModel.onTipoPrecioChange(TipoPrecio.FIJO) },
                        modifier = Modifier.weight(1f)
                    )
                    TipoPrecioChip(
                        text = "A TRATAR",
                        subtitle = "Se negocia",
                        isSelected = uiState.tipoPrecio == TipoPrecio.A_TRATAR,
                        onClick = { viewModel.onTipoPrecioChange(TipoPrecio.A_TRATAR) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---- PRECIO ----
                AuthTextField(
                    value = uiState.precioReferencial,
                    onValueChange = { viewModel.onPrecioChange(it) },
                    label = if (uiState.tipoPrecio == TipoPrecio.FIJO)
                        "Precio (obligatorio)"
                    else
                        "Precio referencial (opcional)",
                    placeholder = "Ej. 250 o 250.50",
                    errorMessage = uiState.precioError,
                    keyboardType = KeyboardType.Decimal
                )

                // ---- INFO SOBRE FOTOS ----
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5EFE7))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "📷 Fotos — próximamente",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5A2B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estamos trabajando para que puedas subir fotos de tus trabajos. Por ahora, publicá tu trabajo sin fotos.",
                            fontSize = 12.sp,
                            color = Color(0xFF6B6B6B),
                            lineHeight = 16.sp
                        )
                    }
                }

                // ---- ERROR GENERAL ----
                if (uiState.errorMessage != null) {
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
                        text = if (uiState.isEditMode) "Guardar cambios" else "Publicar",
                        onClick = { viewModel.save() },
                        enabled = uiState.isFormValid
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
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
private fun EditPortfolioHeader(
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
            text = if (isEditMode) "Editar trabajo" else "Nuevo trabajo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF9F7F5)
        )
    }
}

// DIÁLOGO DE DESCARTAR
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

    // SELECTOR DE TIPO DE PRECIO

    @Composable
    private fun TipoPrecioChip(
        text: String,
        subtitle: String,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val containerColor = if (isSelected)
            Color(0xFF8B5A2B)
        else
            Color(0xFFF5EFE7)

        val contentColor = if (isSelected)
            Color(0xFFF9F7F5)
        else
            Color(0xFF2C2C2C)

        val subtitleColor = if (isSelected)
            Color(0xFFF9F7F5).copy(alpha = 0.85f)
        else
            Color(0xFF6B6B6B)

        val borderColor = if (isSelected)
            Color(0xFF8B5A2B)
        else
            Color(0xFFE0DCD7)

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = subtitleColor
                )
            }
        }
    }
