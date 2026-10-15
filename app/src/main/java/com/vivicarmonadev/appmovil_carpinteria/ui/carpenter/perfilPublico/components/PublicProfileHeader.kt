package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun PublicProfileHeader(
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onMessageClick: () -> Unit,
    fotoTallerUrl: String?,
    fotoMaestroUrl: String?,
    initials: String,
    nombreTaller: String,
    oficio: String,
    rating: String,
    totalReviews: Int,
    proyectosCount: Int,
    aniosExperiencia: Int,
    isVerified: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(                           // borde curvado abajo
                RoundedCornerShape(
                    bottomStart = 26.dp,
                    bottomEnd = 26.dp
                )
            )
    ) {
        // 1. IMAGEN DE FONDO (foto del taller)
        if (!fotoTallerUrl.isNullOrBlank()) {
            AsyncImage(
                model = fotoTallerUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF8B5A2B))
            )
        }

        // 2. OVERLAY OSCURO (gradiente)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // 3. BOTÓN BACK
        Box(
            modifier = Modifier
                .padding(top = 44.dp, start = 16.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // 4. BOTÓN FAVORITO
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 44.dp, end = 16.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(onClick = onFavoriteClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = "Favorito",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        // 5. CONTENIDO CENTRADO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ---- AVATAR CIRCULAR ----
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5A2B))
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!fotoMaestroUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = fotoMaestroUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = initials,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ---- NOMBRE + VERIFICADO ----
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = nombreTaller.ifBlank { "Carpintero" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = "Verificado",
                        tint = Color(0xFFF9D77E),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ---- OFICIO ----
            Text(
                text = oficio,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ---- BARRA DE ESTADÍSTICAS ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                StatItem(
                    icon = Icons.Filled.Star,
                    value = rating,
                    label = if (totalReviews > 0) "($totalReviews)" else "Sin reseñas",
                    modifier = Modifier.weight(1f)
                )

                Divider()

                // Proyectos
                StatItem(
                    icon = Icons.Filled.Folder,
                    value = "$proyectosCount",
                    label = "Proyectos",
                    modifier = Modifier.weight(1f)
                )

                Divider()

                // Experiencia
                StatItem(
                    icon = Icons.Filled.Schedule,
                    value = if (aniosExperiencia > 0) "$aniosExperiencia años" else "Nuevo",
                    label = "Experiencia",
                    modifier = Modifier.weight(1f)
                )

                Divider()

                // Botón Mensaje
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onMessageClick),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Mensaje",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Mensaje",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(Color.White.copy(alpha = 0.2f))
    )
}