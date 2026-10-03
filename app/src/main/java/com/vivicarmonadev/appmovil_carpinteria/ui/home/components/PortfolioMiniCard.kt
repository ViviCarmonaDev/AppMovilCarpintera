package com.vivicarmonadev.appmovil_carpinteria.ui.home.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio

/**
 * Tarjeta compacta de un trabajo del portafolio.
 *
 * Se usa en el Home del cliente (scroll horizontal de trabajos recientes).
 * Más compacta que la tarjeta del catálogo completo.
 */
@Composable
fun PortfolioMiniCard(
    item: PortfolioItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(12.dp)
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

        Spacer(modifier = Modifier.height(6.dp))

        // Categoría (pequeña)
        if (item.categoria.isNotBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF5EFE7))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.categoria,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF8B5A2B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Precio + etiqueta
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.precioTexto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B),
                maxLines = 1
            )

            if (item.etiquetaPrecio != null) {
                TipoPrecioMiniTag(
                    texto = item.etiquetaPrecio!!,
                    tipo = item.tipoPrecio
                )
            }
        }
    }
}

@Composable
private fun TipoPrecioMiniTag(texto: String, tipo: TipoPrecio) {
    val (bgColor, textColor) = when (tipo) {
        TipoPrecio.FIJO -> Color(0xFF8B5A2B) to Color(0xFFF9F7F5)
        TipoPrecio.A_TRATAR -> Color(0xFFD4E0D9) to Color(0xFF2E4A3E)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = texto,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            color = textColor,
            maxLines = 1
        )
    }
}