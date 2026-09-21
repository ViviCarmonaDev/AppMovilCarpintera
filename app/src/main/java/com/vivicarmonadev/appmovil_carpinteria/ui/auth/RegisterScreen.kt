package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthScreenContainer
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.RoleSelector
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.StepIndicator
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.UserRole
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

@Composable
fun RegisterScreen(
    onContinue: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

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
            selected = selectedRole,
            onSelected = { selectedRole = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(
            value = nombres,
            onValueChange = { nombres = it },
            label = "Nombres",
            placeholder = "Ej. Juan Carlos"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = "Apellidos",
            placeholder = "Ej. Pérez García"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = "Teléfono",
            placeholder = "+34 600 000 000"
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Continuar",
            onClick = onContinue
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically   // ← alinea verticalmente
        ) {
            Text(
                text = "¿Ya tienes una cuenta? ",
                fontSize = 14.sp,
                color = Color(0xFF2C2C2C).copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(6.dp))          // ← espacio entre los dos
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


