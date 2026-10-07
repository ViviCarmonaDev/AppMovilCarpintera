package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.components

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
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaTag

/**
 * Tarjeta de un trabajo del portafolio.
 *
 * Se usa en la vista general (grid de 2 columnas).
 * Al tocarla, navega a la pantalla de detalle.
 */
@Composable
fun PortfolioItemCard(
    item: PortfolioItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5EFE7))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // ---- TÍTULO ----
        Text(
            text = item.titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ---- TAG: CATEGORÍA ----
        if (item.categoria.isNotBlank()) {
            MervetaTag(
                text = item.categoria,
                textColor = Color(0xFF8B5A2B)
            )
        }

        // ---- TAG: MATERIAL ----
        if (item.material.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            MervetaTag(
                text = item.material,
                textColor = Color(0xFF6B6B6B)
            )
        }

        // ---- DESCRIPCIÓN ----
        if (item.descripcion.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.descripcion,
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ---- PRECIO + ETIQUETA ----
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.precioTexto,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B)
            )

            if (item.etiquetaPrecio != null) {
                TipoPrecioTag(
                    texto = item.etiquetaPrecio!!,
                    tipo = item.tipoPrecio
                )
            }
        }
    }
}

// ETIQUETA DE TIPO DE PRECIO
@Composable
private fun TipoPrecioTag(texto: String, tipo: TipoPrecio) {
    val (bgColor, textColor) = when (tipo) {
        TipoPrecio.FIJO -> Color(0xFF8B5A2B) to Color(0xFFF9F7F5)
        TipoPrecio.A_TRATAR -> Color(0xFFD4E0D9) to Color(0xFF2E4A3E)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = texto,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}