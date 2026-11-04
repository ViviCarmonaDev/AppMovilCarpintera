package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
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
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio

@Composable
fun PortfolioItemCard(
    item: PortfolioItem,
    isOwner: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primeraImagen = item.imagenesUrls.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        // ---- IMAGEN (sin corazón ni botón "+") ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFFF5EFE7))
        ) {
            if (!primeraImagen.isNullOrBlank()) {
                AsyncImage(
                    model = primeraImagen,
                    contentDescription = item.titulo,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Título
            Text(
                text = item.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Categoría + Material
            if (item.categoria.isNotBlank() || item.material.isNotBlank()) {
                Text(
                    text = listOfNotNull(
                        item.categoria.takeIf { it.isNotBlank() },
                        item.material.takeIf { it.isNotBlank() }
                    ).joinToString(" · "),
                    fontSize = 11.sp,
                    color = Color(0xFF6B6B6B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Precio + Etiqueta (con colores verdes)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.precioTexto,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5A2B)
                )

                if (item.etiquetaPrecio != null) {
                    TipoPrecioChip(
                        texto = item.etiquetaPrecio!!,
                        tipo = item.tipoPrecio
                    )
                }
            }
        }
    }
}

@Composable
private fun TipoPrecioChip(texto: String, tipo: TipoPrecio) {
    val (bgColor, textColor, icon) = when (tipo) {
        TipoPrecio.FIJO -> Triple(
            Color(0xFF4A5D3E), // Verde oscuro
            Color(0xFFF9F7F5), // Blanco
            Icons.Filled.Lock
        )
        TipoPrecio.A_TRATAR -> Triple(
            Color(0xFF6B7F5C), // Verde medio
            Color(0xFFF9F7F5), // Blanco
            Icons.Filled.Handshake
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = texto,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            color = textColor
        )
    }
}