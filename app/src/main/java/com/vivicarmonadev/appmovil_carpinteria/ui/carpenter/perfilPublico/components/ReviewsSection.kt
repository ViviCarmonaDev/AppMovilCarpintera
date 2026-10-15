package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components

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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Review
import coil.compose.AsyncImage

// Modelo temporal de reseña (ajústalo a tu modelo real)

@Composable
fun ReviewsSection(
    reviews: List<Review>,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFBF1E8))
            .padding(16.dp)
    ) {

        // ---- CABECERA ----
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Ícono circular
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5A2B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reseñas",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = "Lo que dicen los clientes",
                    fontSize = 12.sp,
                    color = Color(0xFF6B6B6B)
                )
            }

            // Botón "Ver todas"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF8B5A2B))
                    .clickable(onClick = onSeeAllClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ver todas",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---- LISTA DE RESEÑAS ----
        if (reviews.isEmpty()) {
            Text(
                text = "Aún no hay reseñas",
                fontSize = 13.sp,
                color = Color(0xFF6B6B6B),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            reviews.forEachIndexed { index, review ->
                ReviewItem(review = review)
                if (index < reviews.lastIndex) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFF5EFE7))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(review: Review) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Fila: avatar + nombre + fecha
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Avatar del cliente
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5EFE7)),
                contentAlignment = Alignment.Center
            ) {
                if (!review.clienteFotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = review.clienteFotoUrl,
                        contentDescription = review.clienteNombre,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = review.clienteNombre.take(1).uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5A2B)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.clienteNombre,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                // Estrellas
                Row {
                    repeat(5) { i ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i < review.rating) Color(0xFFE0A458) else Color(0xFFE0E0E0),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Text(
                text = formatearFecha(review.createdAt),
                fontSize = 10.sp,
                color = Color(0xFF9B9B9B)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Comentario
        Text(
            text = review.comentario,
            fontSize = 12.sp,
            color = Color(0xFF6B6B6B),
            lineHeight = 18.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Helper para formatear la fecha
private fun formatearFecha(timestamp: Long): String {
    if (timestamp <= 0) return ""
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("es", "PE"))
    return sdf.format(java.util.Date(timestamp))
}