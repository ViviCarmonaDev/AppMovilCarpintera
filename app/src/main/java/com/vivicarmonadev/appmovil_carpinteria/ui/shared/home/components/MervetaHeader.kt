package com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp

/**
 * Header estándar de Merveta.
 * Se usa en TODAS las pantallas (home cliente, home carpintero, portafolio, pedidos, etc.)
 * Solo cambia el título, subtítulo y (opcionalmente) el ícono/avatar.
 */
@Composable
fun MervetaHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    avatarIcon: ImageVector = Icons.Filled.Person,
    onAvatarClick: (() -> Unit)? = null,
    verticalPadding: Dp = 60.dp,
    bottomPadding: Dp = 28.dp
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .background(Color(0xFF8B5A2B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = verticalPadding,      // texto más abajo)
                    bottom = bottomPadding      // texto más arriba
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con ícono / avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F7F5))
                    .then(
                        if (onAvatarClick != null) {
                            Modifier.clickable(onClick = onAvatarClick)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = avatarIcon,
                    contentDescription = null,
                    tint = Color(0xFF8B5A2B),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF9F7F5)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
                )
            }
        }
    }
}