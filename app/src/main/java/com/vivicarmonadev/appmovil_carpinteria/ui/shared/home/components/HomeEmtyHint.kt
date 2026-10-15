package com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Card de estado vacío para el Home (cliente y carpintero).
 *
 * Se usa para mostrar "Aún no hay X" con un ícono, título y subtítulo.
 * Opcionalmente clickable.
 */
@Composable
fun HomeEmptyHint(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clipToBounds() // Recorta las ondas decorativas que se salgan
            .background(Color(0xFFFBF1E8)) // Fondo beige claro
            .then(clickableModifier)
    ) {
        // --- ONDAS DECORATIVAS DE FONDO ---
        // Usamos Modifier.offset() en lugar de padding() para mover fuera del card.

        // Círculo grande arriba-derecha
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp) // <--- OFFSET, no padding
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E1D0).copy(alpha = 0.6f))
        )
        // Círculo grande abajo-derecha
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 40.dp) // <--- OFFSET
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E1D0).copy(alpha = 0.5f))
        )
        // Círculo pequeño abajo-izquierda
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = 20.dp) // <--- OFFSET
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E1D0).copy(alpha = 0.4f))
        )

        // --- CONTENIDO ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF6B6B6B),
                    lineHeight = 16.sp
                )
            }
        }
    }
}