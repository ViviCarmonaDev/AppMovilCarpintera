package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.EstadoPedidoBadge
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.fechaCorta
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.tiempoRelativo

/**
 * Tarjeta de un pedido en la lista.
 * NO tiene botones: se toca para ver el detalle.
 * Se adapta según el rol:
 *  - Cliente: muestra el nombre del carpintero (o "Esperando carpintero").
 *  - Carpintero: muestra el nombre del cliente.
 */
@Composable
fun OrderCard(
    pedido: Pedido,
    isCarpenter: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5EFE7))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // ---- Fila superior: número + badge + tiempo ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = pedido.numeroPedido,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B),
                    letterSpacing = 0.5.sp
                )

                EstadoPedidoBadge(estado = pedido.status)
            }

            Text(
                text = tiempoRelativo(pedido.createdAt),
                fontSize = 11.sp,
                color = Color(0xFF8A8A8A)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ---- Nombre según rol ----
        if (isCarpenter && pedido.clienteNombre.isNotBlank()) {
            // Vista carpintero → nombre del cliente
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFF8B5A2B),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pedido.clienteNombre,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF8B5A2B)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        } else if (!isCarpenter) {
            // Vista cliente → nombre del carpintero o "Libre"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = if (pedido.isLibre) Color(0xFF8A8A8A) else Color(0xFF8B5A2B),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (pedido.isLibre) "Esperando carpintero"
                    else pedido.carpinteroNombre ?: "Carpintero asignado",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (pedido.isLibre) Color(0xFF8A8A8A) else Color(0xFF8B5A2B)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // ---- Título ----
        Text(
            text = pedido.titulo,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // ---- Descripción ----
        if (pedido.descripcion.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = pedido.descripcion,
                fontSize = 13.sp,
                color = Color(0xFF6B6B6B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ---- Material + Fecha estimada ----
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pedido.tipoMadera.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Park,
                        contentDescription = null,
                        tint = Color(0xFF8B5A2B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pedido.tipoMadera.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C2C2C),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            if (pedido.fechaEstimada != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF8B5A2B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Est. ${fechaCorta(pedido.fechaEstimada)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B6B6B)
                    )
                }
            }
        }

        // ---- Medidas ----
        if (pedido.medidasTexto != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Medidas: ${pedido.medidasTexto}",
                fontSize = 11.sp,
                color = Color(0xFF6B6B6B)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---- Precio ----
        if (pedido.presupuestoTexto != null) {
            Text(
                text = pedido.presupuestoTexto ?: "",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B)
            )
        }
    }
}