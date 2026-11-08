package com.vivicarmonadev.appmovil_carpinteria.ui.shared.mapa

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEditHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.mapa.CarpenterLocationMap

@Composable
fun UbicacionScreen(
    latitud: Double?,
    longitud: Double?,
    nombreTaller: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        MervetaEditHeader(title = "Ubicación del taller", onBack = onBack)

        if (latitud != null && longitud != null) {
            CarpenterLocationMap(
                latitud = latitud,
                longitud = longitud,
                nombreTaller = nombreTaller,
                onScrollEnabledChange = { /* No hacer nada, no hay scroll aquí */ },
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Text(
                text = "El carpintero aún no ha registrado la ubicación de su taller.",
                fontSize = 14.sp,
                color = Color(0xFF6B6B6B),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}