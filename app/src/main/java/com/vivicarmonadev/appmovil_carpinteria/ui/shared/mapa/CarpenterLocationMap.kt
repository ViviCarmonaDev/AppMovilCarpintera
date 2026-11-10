package com.vivicarmonadev.appmovil_carpinteria.ui.shared.mapa

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import android.view.MotionEvent
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.motionEventSpy
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun CarpenterLocationMap(
    latitud: Double,
    longitud: Double,
    onScrollEnabledChange: (Boolean) -> Unit,
    nombreTaller: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ubicacion = LatLng(latitud, longitud)

    Column(modifier = modifier.fillMaxWidth()) {
        // Mapa
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .clip(RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onScrollEnabledChange(false) // Deshabilita el scroll del LazyColumn
                            tryAwaitRelease()
                            onScrollEnabledChange(true)  // Lo vuelve a habilitar al soltar
                        }
                    )
                },

            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(ubicacion, 15f)
            }
        ) {
            Marker(
                state = rememberMarkerState(position = ubicacion),
                title = nombreTaller
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para abrir ruta en Google Maps
        Button(
            onClick = {
                // Intent para abrir Google Maps con la ruta
                val uri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud($nombreTaller)")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5A2B))
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Abrir ruta en Google Maps", color = Color.White)
        }
    }
}