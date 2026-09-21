package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthScreenContainer
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.StepIndicator
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

@Composable
fun RegisterCredentialsScreen(
    onFinish: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AuthScreenContainer(
        title = "Registro",
        subtitle = "Credenciales"
    ) {
        StepIndicator(currentStep = 2, totalSteps = 2)

        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            placeholder = "ejemplo@correo.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            placeholder = "••••••••",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirmar contraseña",
            placeholder = "••••••••",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Finalizar",
            onClick = onFinish
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
            TextButton(
                onClick = onBackToLogin,
                contentPadding = PaddingValues(0.dp)
            ) {
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