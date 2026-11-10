package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.taller.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

private val CAJAMARCA = LatLng(-7.1617, -78.5128)
private const val DEFAULT_ZOOM = 14f

@Composable
private fun rememberUpdatedMarkerState(newPosition: LatLng): MarkerState =
    remember { MarkerState(position = newPosition) }
        .apply { position = newPosition }
@Composable
fun MervetaMapPickerDialog(
    initialLat: Double?,
    initialLng: Double?,
    onConfirm: (lat: Double?, lng: Double?) -> Unit,
    onDismiss: () -> Unit
) {
    val startPosition = remember(initialLat, initialLng) {
        if (initialLat != null && initialLng != null)
            LatLng(initialLat, initialLng)
        else CAJAMARCA
    }

    // 1. Mantenemos la posición en un estado mutable para el texto inferior
    var markerPosition by remember { mutableStateOf<LatLng?>(startPosition) }

    // 2. Creamos el MarkerState (usando nuestra función custom)
    val markerState = rememberUpdatedMarkerState(newPosition = startPosition)

    // 3. EFECTO CLAVE: Observamos los cambios de posición del marcador
    // Al no poder usar 'isDragging', escuchamos directamente 'markerState.position'.
    // Esto se actualiza tanto si tocas el mapa como si arrastras el marcador.
    LaunchedEffect(markerState.position) {
        markerPosition = markerState.position
    }

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPosition, DEFAULT_ZOOM)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = MaterialTheme.shapes.large,
            color = Color.White
        ) {
            Column(Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ubicación del taller",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2C2C2C)
                    )
                }

                // Mapa
                Box(modifier = Modifier.weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraState,
                        onMapClick = { latLng ->
                            // Al tocar el mapa, actualizamos ambos estados
                            markerState.position = latLng
                            markerPosition = latLng
                        }
                    ) {
                        Marker(
                            state = markerState,
                            draggable = true // Ya no dará error porque usamos el MarkerState
                        )
                    }
                }

                // Coordenadas + botones
                Column(modifier = Modifier.padding(16.dp)) {
                    val pos = markerPosition
                    Text(
                        text = if (pos != null)
                            "Lat: %.6f, Lng: %.6f".format(pos.latitude, pos.longitude)
                        else "Toca el mapa para elegir un punto",
                        fontSize = 13.sp,
                        color = Color(0xFF6B6B6B)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                markerPosition?.let { onConfirm(it.latitude, it.longitude) }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = markerPosition != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8B5A2B)
                            )
                        ) {
                            Text("Guardar ubicación", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}