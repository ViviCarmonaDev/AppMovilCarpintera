package com.vivicarmonadev.appmovil_carpinteria.ui.pedidos.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tarjeta de un pedido en el Centro de Pedidos.
 *
 * Muestra:
 *  - Número de pedido (#ABC123)
 *  - Badge del estado
 *  - Fecha de creación (Hace X días)
 *  - Título del pedido
 *  - Descripción
 *  - Material + fecha estimada
 *  - Precio (presupuesto)
 *  - Botones de acción (editar, cancelar)
 */
@Composable
fun OrderCard(
    pedido: Pedido,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val puedeEditarse = pedido.status == EstadoPedido.PENDIENTE
    val puedeCancelarse = pedido.status == EstadoPedido.PENDIENTE

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
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
                // Número de pedido
                Text(
                    text = pedido.numeroPedido,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B),
                    letterSpacing = 0.5.sp
                )

                // Badge de estado
                EstadoBadge(estado = pedido.status)
            }

            // Tiempo relativo
            Text(
                text = tiempoRelativo(pedido.createdAt),
                fontSize = 11.sp,
                color = Color(0xFF8A8A8A)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

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

        // ---- Precio (presupuesto) ----
        if (pedido.presupuestoTexto != null) {
            Text(
                text = pedido.presupuestoTexto ?: "",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ---- Botones de acción ----
        if (puedeEditarse || puedeCancelarse) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (puedeEditarse) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF5EFE7))
                            .clickable(onClick = onEditClick)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Color(0xFF8B5A2B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EDITAR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF8B5A2B)
                            )
                        }
                    }
                }

                if (puedeCancelarse) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFADBD8))
                            .clickable(onClick = onCancelClick)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CANCELAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFFC5544A)
                        )
                    }
                }
            }
        }
    }
}

// BADGE DE ESTADO

@Composable
private fun EstadoBadge(estado: EstadoPedido) {
    val (bgColor, textColor) = when (estado) {
        EstadoPedido.PENDIENTE -> Color(0xFFFCEBD0) to Color(0xFF8B5A2B)
        EstadoPedido.ACEPTADO -> Color(0xFFD4E0D9) to Color(0xFF2E4A3E)
        EstadoPedido.EN_PROCESO -> Color(0xFF8B5A2B) to Color(0xFFF9F7F5)
        EstadoPedido.TERMINADO -> Color(0xFFD6E5F0) to Color(0xFF2E4A5F)
        EstadoPedido.ENTREGADO -> Color(0xFF4A6B5D) to Color(0xFFF9F7F5)
        EstadoPedido.CANCELADO -> Color(0xFFFADBD8) to Color(0xFFC5544A)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = estado.toDisplayText().uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}

// HELPERS DE FECHA
/**
 * Convierte un timestamp a un texto relativo.
 * Ej: "Hace 2 días", "Hace 5 min", "Hoy"
 */
private fun tiempoRelativo(timestamp: Long): String {
    if (timestamp == 0L) return ""

    val ahora = System.currentTimeMillis()
    val diferencia = ahora - timestamp

    val minutos = diferencia / (1000 * 60)
    val horas = diferencia / (1000 * 60 * 60)
    val dias = diferencia / (1000 * 60 * 60 * 24)

    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "Hace ${minutos}m"
        horas < 24 -> "Hace ${horas}h"
        dias == 1L -> "Ayer"
        dias < 7 -> "Hace $dias días"
        dias < 30 -> "Hace ${dias / 7} semanas"
        else -> {
            val sdf = SimpleDateFormat("dd MMM", Locale("es", "PE"))
            sdf.format(Date(timestamp))
        }
    }
}

/**
 * Formatea una fecha a "15 Nov" (día + mes corto).
 */
private fun fechaCorta(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM", Locale("es", "PE"))
    return sdf.format(Date(timestamp))
}