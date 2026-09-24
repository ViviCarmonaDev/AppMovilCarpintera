package com.vivicarmonadev.appmovil_carpinteria.ui.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthScreenContainer
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.RoleSelector
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.CompleteProfileViewModel

@Composable
fun CompleteProfileScreen(
    viewModel: CompleteProfileViewModel,
    onCompleteSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando se guarda exitosamente, navegamos al Home
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onCompleteSuccess()
        }
    }

    AuthScreenContainer(
        title = "Completa tu perfil",
        subtitle = "Solo unos datos más y listo"
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ---- CAMPO DE EMAIL (solo lectura) ----
        Text(
            text = "Cuenta de Google",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = uiState.email.ifBlank { "sin correo" },
            fontSize = 14.sp,
            color = Color(0xFF6B6B6B),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- NOMBRES ----
        AuthTextField(
            value = uiState.nombres,
            onValueChange = { viewModel.onNombresChange(it) },
            label = "Nombres",
            placeholder = "Ej. Juan Carlos",
            errorMessage = uiState.nombresError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- APELLIDOS ----
        AuthTextField(
            value = uiState.apellidos,
            onValueChange = { viewModel.onApellidosChange(it) },
            label = "Apellidos",
            placeholder = "Ej. Pérez García",
            errorMessage = uiState.apellidosError
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---- SELECCIÓN DE ROL ----
        Text(
            text = "Selecciona tu rol",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(12.dp))

        RoleSelector(
            selected = uiState.selectedRole,
            onSelected = { viewModel.onRoleSelected(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---- TELÉFONO ----
        AuthTextField(
            value = uiState.telefono,
            onValueChange = { viewModel.onTelefonoChange(it) },
            label = "Teléfono",
            placeholder = "Ej. 912345678",
            errorMessage = uiState.telefonoError,
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- DIRECCIÓN ----
        AuthTextField(
            value = uiState.direccion,
            onValueChange = { viewModel.onDireccionChange(it) },
            label = "Dirección",
            placeholder = "Ej. Av. Lima 123",
            errorMessage = uiState.direccionError
        )

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
                text = "Continuar",
                onClick = { viewModel.saveProfile() },
                enabled = uiState.isFormValid
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}