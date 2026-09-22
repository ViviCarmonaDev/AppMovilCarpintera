package com.vivicarmonadev.appmovil_carpinteria.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.EmptyStateScreen

@Composable
fun ServicesScreen() {
    Column(modifier = Modifier.fillMaxSize()) {

        // ---- HEADER ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF8B5A2B))
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column {
                Text(
                    text = "Servicios",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF9F7F5)
                )
                Text(
                    text = "Lo que ofreces a tus clientes",
                    fontSize = 13.sp,
                    color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
                )
            }
        }

        // ---- CONTENIDO VACÍO ----
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateScreen(
                icon = Icons.Filled.Build,
                title = "Sin servicios todavía",
                subtitle = "Agrega los servicios que ofreces (muebles a medida, restauración, etc.) para que los clientes te encuentren"
            )
        }
    }
}