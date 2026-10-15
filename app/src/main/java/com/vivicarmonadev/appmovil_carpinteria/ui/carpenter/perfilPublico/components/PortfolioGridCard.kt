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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
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
import coil.compose.AsyncImage
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem

@Composable
fun PortfolioGridCard(
    item: PortfolioItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primeraImagen = item.imagenesUrls.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFBF1E8))
            .clickable(onClick = onClick)
    ) {
        // ---- IMAGEN ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            if (!primeraImagen.isNullOrBlank()) {
                AsyncImage(
                    model = primeraImagen,
                    contentDescription = item.titulo,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFFF5EFE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = Color(0xFF8B5A2B).copy(alpha = 0.4f),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // ---- INFO ----
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = item.titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Categoría
            if (item.categoria.isNotBlank()) {
                Text(
                    text = item.categoria,
                    fontSize = 11.sp,
                    color = Color(0xFF8B5A2B),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Precio
            Text(
                text = item.precioTexto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B)
            )
        }
    }
}