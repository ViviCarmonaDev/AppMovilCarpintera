package com.vivicarmonadev.appmovil_carpinteria.ui.shared.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField

@Composable
fun EjercicoPatalla(
    onBack: () -> Unit,
){
    var ombre by remember {mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7F5))
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        // ---- CAMPOS ----
        Text(
            text = "termios y codicioes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---- CAMPO 1: Nombre ----
        AuthTextField(
            value = ombre,
            onValueChange = { ombre = it },
            label = "Nombre",
            placeholder = "Tu nombre",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Volver al inicio de sesión",
            onClick = onBack,
            enabled = true
        )
    }
}
