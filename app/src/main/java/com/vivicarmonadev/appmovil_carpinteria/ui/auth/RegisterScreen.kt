package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthScreenContainer
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.RoleSelector
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.StepIndicator
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onContinue: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AuthScreenContainer(
        title = "Registro",
        subtitle = "Datos básicos"
    ) {
        StepIndicator(currentStep = 1, totalSteps = 2)

        Spacer(modifier = Modifier.height(24.dp))

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

        AuthTextField(
            value = uiState.nombres,
            onValueChange = { viewModel.onNombresChange(it) },
            label = "Nombres",
            placeholder = "Ej. Juan Carlos",
            errorMessage = uiState.nombresError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = uiState.apellidos,
            onValueChange = { viewModel.onApellidosChange(it) },
            label = "Apellidos",
            placeholder = "Ej. Pérez García",
            errorMessage = uiState.apellidosError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = uiState.telefono,
            onValueChange = { viewModel.onTelefonoChange(it) },
            label = "Teléfono",
            placeholder = "Ej. 912345678",
            errorMessage = uiState.telefonoError,
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Continuar",
            onClick = onContinue,
            enabled = uiState.isStep1Valid
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "¿Ya tienes una cuenta?",
                fontSize = 14.sp,
                color = Color(0xFF2C2C2C).copy(alpha = 0.7f)
            )
            TextButton(onClick = onBackToLogin) {
                Text(
                    text = "Inicia sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5A2B)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}