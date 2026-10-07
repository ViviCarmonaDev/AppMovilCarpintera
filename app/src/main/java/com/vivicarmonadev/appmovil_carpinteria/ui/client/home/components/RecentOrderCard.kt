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

/**
 * Tarjeta de pedido reciente para el Home del cliente.
 *
 * Por ahora es un placeholder con datos de ejemplo hasta que
 * implementemos el sistema completo de pedidos.
 */
@Composable
fun RecentOrderCard(
    titulo: String,
    estadoTexto: String,
    estadoColor: Color,
    tiempoTexto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        // Título del pedido
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Estado + tiempo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge de estado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(estadoColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = estadoTexto,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = estadoColor
                )
            }

            // Tiempo
            Text(
                text = tiempoTexto,
                fontSize = 11.sp,
                color = Color(0xFF6B6B6B)
            )
        }
    }
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
            .background(Color(0xFFF5EFE7))
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