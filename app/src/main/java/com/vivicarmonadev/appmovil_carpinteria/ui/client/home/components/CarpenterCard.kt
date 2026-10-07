package com.vivicarmonadev.appmovil_carpinteria.ui.client.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User

@Composable
fun CarpenterCard(
    user: User,
    profile: CarpenterProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Iniciales del taller (o del nombre del usuario)
    val initials = getInitials(profile.nombreTaller, user.fullName)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar con iniciales
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF8B5A2B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F7F5)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Info del carpintero
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = profile.nombreTaller.ifBlank { user.fullName },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating + Experiencia
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFE0A458),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "%.1f".format(profile.rating),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = "  ·  ",
                    fontSize = 12.sp,
                    color = Color(0xFF6B6B6B)
                )
                Text(
                    text = experienceText(profile.aniosExperiencia),
                    fontSize = 12.sp,
                    color = Color(0xFF6B6B6B)
                )
            }

            // Skills (primeras 2)
            if (profile.skills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    profile.skills.take(2).forEach { skill ->
                        SkillMiniChip(text = skill)
                    }
                    if (profile.skills.size > 2) {
                        Text(
                            text = "+${profile.skills.size - 2}",
                            fontSize = 11.sp,
                            color = Color(0xFF8B5A2B),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillMiniChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF5EFE7))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color(0xFF8B5A2B),
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun getInitials(nombreTaller: String, fallbackNombre: String): String {
    val nombre = if (nombreTaller.isNotBlank()) nombreTaller else fallbackNombre
    if (nombre.isBlank()) return "?"

    val palabras = nombre.trim().split(" ").filter { it.isNotBlank() }
    return when {
        palabras.size >= 2 ->
            "${palabras[0].first().uppercaseChar()}${palabras[1].first().uppercaseChar()}"
        palabras.size == 1 ->
            palabras[0].take(2).uppercase()
        else -> "?"
    }
}

private fun experienceText(anios: Int): String = when {
    anios == 0 -> "Sin exp."
    anios == 1 -> "1 año"
    else -> "$anios años"
}