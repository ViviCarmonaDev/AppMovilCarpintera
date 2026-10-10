package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Header reutilizable para pantallas de perfil (personal y taller).
 *
 * Muestra:
 *  - Avatar grande (ícono o iniciales).
 *  - Nombre principal.
 *  - Subtítulo opcional.
 *  - Badge opcional (rol, rating, etc.).
 */
@Composable
fun MervetaProfileHeader(
    title: String,
    modifier: Modifier = Modifier,
    avatarIcon: ImageVector? = null,
    avatarInitials: String? = null,
    subtitle: String? = null,
    badgeText: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5A2B))
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F7F5)),
                contentAlignment = Alignment.Center
            ) {
                if (avatarInitials != null) {
                    Text(
                        text = avatarInitials,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5A2B)
                    )
                } else if (avatarIcon != null) {
                    Icon(
                        imageVector = avatarIcon,
                        contentDescription = null,
                        tint = Color(0xFF8B5A2B),
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F7F5),
                textAlign = TextAlign.Center
            )

            // Subtítulo
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFFF9F7F5).copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // Badge
            if (badgeText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF9F7F5).copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF9F7F5)
                    )
                }
            }
        }
    }
}