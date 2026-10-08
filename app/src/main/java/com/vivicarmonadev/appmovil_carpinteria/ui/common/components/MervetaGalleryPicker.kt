package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Galería con imagen principal + miniaturas.
 *
 * - La imagen principal es grande (aspect ratio 1:1).
 * - Las miniaturas aparecen debajo en fila.
 * - Al tocar una miniatura, cambia la imagen principal.
 * - Se puede agregar hasta `maxImagenes`.
 *
 * Se usa en los formularios de crear/editar portafolio y pedidos.
 */
@Composable
fun MervetaGalleryPicker(
    imagenes: List<String>,
    onAgregarClick: () -> Unit,
    onEliminarClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Imágenes",
    maxImagenes: Int = 3
) {
    var seleccionada by remember { mutableIntStateOf(0) }

    // Asegurar que el índice seleccionado no quede fuera de rango
    val indiceActual = seleccionada.coerceIn(0, (imagenes.size - 1).coerceAtLeast(0))

    Column(modifier = modifier.fillMaxWidth()) {
        // ---- Etiqueta ----
        Text(
            text = "$label (${imagenes.size}/$maxImagenes)",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ---- Imagen principal ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF5EFE7))
        ) {
            if (imagenes.isNotEmpty()) {
                // TODO: AsyncImage(model = imagenes[indiceActual])
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = Color(0xFF8B5A2B).copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                }

                // Botón eliminar
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC5544A))
                        .clickable { onEliminarClick(indiceActual) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Eliminar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                // Sin imágenes → mostrar "+ Agregar"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onAgregarClick),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(36.dp))
                                .background(Color(0xFF8B5A2B).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = Color(0xFF8B5A2B),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Agregar imagen",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8B5A2B)
                        )
                    }
                }
            }
        }

        // ---- Miniaturas (debajo) ----
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Miniaturas de las imágenes existentes
            imagenes.forEachIndexed { index, _ ->
                val isSelected = index == indiceActual
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5EFE7))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF8B5A2B) else Color(0xFFE0DCD7),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { seleccionada = index },
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: AsyncImage(model = imagenes[index])
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = Color(0xFF8B5A2B).copy(alpha = 0.4f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Botón "+" para agregar (si no se alcanzó el máximo)
            if (imagenes.size < maxImagenes) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5EFE7))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE0DCD7),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onAgregarClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar imagen",
                        tint = Color(0xFF8B5A2B),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}