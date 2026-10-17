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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFBF1E8))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically   // Alinea imagen y texto al centro
    ) {
        // Imagen (izquierda) — Tamaño fijo y cuadrado
        Box(
            modifier = Modifier
                .size(100.dp)   // Vuelve a ser cuadrada y fija
                .clip(RoundedCornerShape(14.dp))
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
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Info (derecha)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            // VISTA CLIENTE (isOwner = false)

            if (!isOwner) {
                if (item.nombreTaller.isNotBlank()) {
                    Text(
                        text = item.nombreTaller,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5A2B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (item.carpinteroNombre.isNotBlank()) {
                    Text(
                        text = "Carpintero: ${item.carpinteroNombre}",
                        fontSize = 11.sp,
                        color = Color(0xFF6B6B6B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Título (para ambos)
            Text(
                text = item.titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C2C2C),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // VISTA CARPINTERO (isOwner = true)

            if (isOwner) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.categoria.isNotBlank()) {
                        Chip(text = item.categoria)
                    }
                    if (item.material.isNotBlank()) {
                        Chip(text = item.material)
                    }
                }
            }

            // Precio (para ambos)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.precioTexto,
                    fontSize = 18.sp,
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
private fun Chip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFFFFFFF))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8B5A2B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TipoPrecioChip(texto: String, tipo: TipoPrecio) {
    val (bgColor, textColor, icon) = when (tipo) {
        TipoPrecio.FIJO -> Triple(
            Color(0xFF4A5D3E),
            Color(0xFFF9F7F5),
            Icons.Filled.Lock
        )
        TipoPrecio.A_TRATAR -> Triple(
            Color(0xFF6B7F5C),
            Color(0xFFF9F7F5),
            Icons.Filled.Handshake
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
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
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            color = textColor
        )
    }
}