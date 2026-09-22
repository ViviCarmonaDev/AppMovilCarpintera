package com.vivicarmonadev.appmovil_carpinteria.ui.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
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
fun ProjectsScreen() {
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
                    text = "Proyectos",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF9F7F5)
                )
                Text(
                    text = "Tus trabajos en curso y finalizados",
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
                icon = Icons.Filled.Folder,
                title = "Aún no tienes proyectos",
                subtitle = "Cuando aceptes o crees un proyecto, aparecerá acá para que puedas gestionarlo"
            )
        }
    }
}