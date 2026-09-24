package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthScreenContainer
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.SkillSelector
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

@Composable
fun CarpenterProfileScreen(
    viewModel: CarpenterProfileViewModel,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando se guarda exitosamente, navegamos
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onSaveSuccess()
        }
    }

    AuthScreenContainer(
        title = if (uiState.isEditMode) "Mi taller" else "Tu taller",
        subtitle = if (uiState.isEditMode)
            "Actualiza los datos de tu taller"
        else
            "Cuéntanos sobre tu negocio"
    ) {
        Spacer(modifier = Modifier.height(8.dp))

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
            placeholder = "Ej. Especialistas en muebles a medida con más de 10 años de experiencia...",
            errorMessage = uiState.descripcionError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- DIRECCIÓN DEL TALLER ----
        AuthTextField(
            value = uiState.direccionTaller,
            onValueChange = { viewModel.onDireccionTallerChange(it) },
            label = "Dirección del taller",
            placeholder = "Ej. Av. Lima 123, Miraflores",
            errorMessage = uiState.direccionTallerError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- TELÉFONO DEL TALLER ----
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

        // Error de skills
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
                text = if (uiState.isEditMode) "Guardar cambios" else "Continuar",
                onClick = { viewModel.saveProfile() },
                enabled = uiState.isFormValid
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}