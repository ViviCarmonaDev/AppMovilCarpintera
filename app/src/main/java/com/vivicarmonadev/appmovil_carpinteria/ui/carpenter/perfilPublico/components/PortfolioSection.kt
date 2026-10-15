package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem

@Composable
fun PortfolioSection(
    items: List<PortfolioItem>,
    onItemClick: (PortfolioItem) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ✅ TODO dentro de un Card blanco con bordes redondeados
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFBF1E8))
            .padding(16.dp)
    ) {

        // ---- CABECERA DE LA SECCIÓN ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de portafolio
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF8B5A2B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Título + subtítulo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Portafolio",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = "Muebles, puertas, cocinas y más",
                    fontSize = 12.sp,
                    color = Color(0xFF6B6B6B)
                )
            }

            // Botón "Ver todo"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF8B5A2B))
                    .clickable(onClick = onSeeAllClick)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ver todo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---- LISTA HORIZONTAL DE TRABAJOS ----
        if (items.isEmpty()) {
            // Estado vacío: fondo crema igual que antes, pero dentro del Card blanco
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Este carpintero aún no publicó trabajos",
                    fontSize = 13.sp,
                    color = Color(0xFF6B6B6B)
                )
            }
        } else {
            LazyRow(
                // Sin contentPadding horizontal extra, porque el Card ya tiene 16dp de padding
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    PortfolioImageCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

// CARD DE IMAGEN DEL PORTAFOLIO
@Composable
private fun PortfolioImageCard(
    item: PortfolioItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primeraImagen = item.imagenesUrls.firstOrNull()

    Box(
        modifier = modifier
            .size(width = 160.dp, height = 130.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF5EFE7))
            .clickable(onClick = onClick)
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
                    .height(130.dp)
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
}