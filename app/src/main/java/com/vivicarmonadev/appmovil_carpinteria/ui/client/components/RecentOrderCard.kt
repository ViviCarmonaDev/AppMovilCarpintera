package com.vivicarmonadev.appmovil_carpinteria.ui.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.layout.fillMaxWidth
import coil.compose.AsyncImage
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.tiempoRelativo

@Composable
fun RecentOrderCard(
    pedido: Pedido,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primeraImagen = pedido.imagenesUrls.firstOrNull()
    val estadoColor = estadoColorPara(pedido.status)

    Row(
        modifier = modifier
            .width(280.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFBF1E8))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen o placeholder
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5EFE7))
        ) {
            if (!primeraImagen.isNullOrBlank()) {
                AsyncImage(
                    model = primeraImagen,
                    contentDescription = pedido.titulo,
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

        Spacer(modifier = Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            // Título
            Text(
                text = pedido.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Chip de estado + tiempo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Chip de estado
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(estadoColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = pedido.status.toDisplayText().uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = estadoColor,
                        letterSpacing = 0.3.sp
                    )
                }

                // Tiempo
                Text(
                    text = tiempoRelativo(pedido.createdAt),
                    fontSize = 10.sp,
                    color = Color(0xFF8A8A8A)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Precio
            if (pedido.presupuestoTexto != null) {
                Text(
                    text = pedido.presupuestoTexto!!,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5A2B)
                )
            } else {
                Text(
                    text = "A tratar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B)
                )
            }
        }
    }
}

private fun estadoColorPara(estado: EstadoPedido): Color = when (estado) {
    EstadoPedido.PENDIENTE -> Color(0xFFE0A458)
    EstadoPedido.ACEPTADO -> Color(0xFF6B8E7F)
    EstadoPedido.EN_PROCESO -> Color(0xFF8B5A2B)
    EstadoPedido.TERMINADO -> Color(0xFF4A6B8B)
    EstadoPedido.ENTREGADO -> Color(0xFF4A6B5D)
    EstadoPedido.CANCELADO -> Color(0xFFC5544A)
}

/**
 * Card placeholder que se muestra cuando el cliente no tiene pedidos.
 */
@Composable
fun EmptyOrdersCard(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFBF1E8))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFFFFF))
                .padding(horizontal = 10.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📦",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.padding(start = 12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Aún no tenés pedidos",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Cuando hagas un pedido, aparecerá acá",
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B)
            )
        }
    }
}