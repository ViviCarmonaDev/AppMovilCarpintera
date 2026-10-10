package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.taller.edit

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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.SkillSelector
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaDiscardDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEditHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

/**
 * Pantalla para EDITAR el taller del carpintero.
 */
@Composable
fun EditTallerScreen(
    viewModel: EditTallerViewModel,
    uid: String,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cargar datos
    LaunchedEffect(uid) {
        viewModel.initialize(uid)
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
            title = "Editar taller",
            onBack = {
                val canExit = viewModel.onBackClick()
                if (canExit) onBack()
            }
        )

        // Contenido
        if (uiState.isLoading && uiState.nombreTaller.isBlank()) {
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
                // ---- NOMBRE DEL TALLER ----
                AuthTextField(
                    value = uiState.nombreTaller,
                    onValueChange = { viewModel.onNombreTallerChange(it) },
                    label = "Nombre del taller",
                    placeholder = "Ej. Taller Merveta",
                    errorMessage = uiState.nombreTallerError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- RUC ----
                AuthTextField(
                    value = uiState.ruc,
                    onValueChange = { viewModel.onRucChange(it) },
                    label = "RUC",
                    placeholder = "Ej. 20123456789",
                    errorMessage = uiState.rucError,
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- DESCRIPCIÓN ----
                AuthTextField(
                    value = uiState.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = "Descripción",
                    placeholder = "Ej. Especialistas en muebles a medida...",
                    errorMessage = uiState.descripcionError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- DIRECCIÓN ----
                AuthTextField(
                    value = uiState.direccionTaller,
                    onValueChange = { viewModel.onDireccionTallerChange(it) },
                    label = "Dirección del taller",
                    placeholder = "Ej. Av. Lima 123, Miraflores",
                    errorMessage = uiState.direccionTallerError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- TELÉFONO ----
                AuthTextField(
                    value = uiState.telefonoTaller,
                    onValueChange = { viewModel.onTelefonoTallerChange(it) },
                    label = "Teléfono del taller",
                    placeholder = "Ej. 912345678",
                    errorMessage = uiState.telefonoTallerError,
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- AÑOS DE EXPERIENCIA ----
                AuthTextField(
                    value = uiState.aniosExperiencia,
                    onValueChange = { viewModel.onAniosExperienciaChange(it) },
                    label = "Años de experiencia",
                    placeholder = "Ej. 5",
                    errorMessage = uiState.aniosExperienciaError,
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ---- SKILLS ----
                Text(
                    text = "Habilidades",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C2C2C)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Selecciona las que apliquen a tu taller",
                    fontSize = 12.sp,
                    color = Color(0xFF6B6B6B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                SkillSelector(
                    selectedSkills = uiState.skills,
                    onSkillToggle = { viewModel.onSkillToggle(it) }
                )

                if (uiState.skillsError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.skillsError ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFFC5544A),
                        fontWeight = FontWeight.Medium
                    )
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
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8B5A2B), strokeWidth = 3.dp)
                    }
                } else {
                    PrimaryButton(
                        text = "Guardar cambios",
                        onClick = { viewModel.saveChanges() },
                        enabled = uiState.isFormValid && uiState.hasChanges
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Diálogo descartar
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
}