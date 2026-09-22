package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthScreenContainer
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.DividerWithText
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.GoogleSignInButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    onGoogleSignIn: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando el login es exitoso, navegamos al Home
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    AuthScreenContainer(
        title = "Bienvenido",
        subtitle = "Inicia sesión para continuar"
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        AuthTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = "Correo electrónico",
            placeholder = "ejemplo@correo.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Contraseña",
            placeholder = "••••••••",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { /* TODO: recuperar contraseña */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontSize = 13.sp,
                    color = Color(0xFF8B5A2B)
                )
            }
        }

        // ---- ERROR ----
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.errorMessage ?: "",
                fontSize = 13.sp,
                color = Color(0xFFC5544A),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---- BOTÓN INICIAR SESIÓN ----
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
                text = "Iniciar sesión",
                onClick = { viewModel.login() },
                enabled = uiState.isFormValid
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DividerWithText(text = "O continúa con")

        Spacer(modifier = Modifier.height(24.dp))

        GoogleSignInButton(
            text = "Continuar con Google",
            onClick = onGoogleSignIn
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta?",
                fontSize = 14.sp,
                color = Color(0xFF2C2C2C).copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            TextButton(
                onClick = onGoToRegister,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Regístrate",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5A2B)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}