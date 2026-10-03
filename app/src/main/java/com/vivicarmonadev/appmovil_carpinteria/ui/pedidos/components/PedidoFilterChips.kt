package com.vivicarmonadev.appmovil_carpinteria.ui.pedidos.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido

/**
 * Chips de filtro por estado para el Centro de Pedidos.
 *
 * Se muestra en un scroll horizontal con las opciones:
 *  - TODOS (sin filtro)
 *  - PENDIENTES
 *  - ACEPTADOS
 *  - EN PROCESO
 *  - ENTREGADOS
 *  - CANCELADOS
 */
@Composable
fun PedidoFilterChips(
    estadoSeleccionado: EstadoPedido?,
    onEstadoSelected: (EstadoPedido?) -> Unit,
    modifier: Modifier = Modifier
) {
    val opciones: List<Pair<String, EstadoPedido?>> = listOf(
        "TODOS" to null,
        "PENDIENTES" to EstadoPedido.PENDIENTE,
        "ACEPTADOS" to EstadoPedido.ACEPTADO,
        "EN PROCESO" to EstadoPedido.EN_PROCESO,
        "ENTREGADOS" to EstadoPedido.ENTREGADO,
        "CANCELADOS" to EstadoPedido.CANCELADO
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { (label, estado) ->
            val isSelected = estadoSeleccionado == estado

            FilterChip(
                selected = isSelected,
                onClick = { onEstadoSelected(estado) },
                label = {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFFF9F7F5) else Color(0xFF2C2C2C),
                        letterSpacing = 0.5.sp
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFFF5EFE7),
                    labelColor = Color(0xFF2C2C2C),
                    selectedContainerColor = Color(0xFF8B5A2B),
                    selectedLabelColor = Color(0xFFF9F7F5)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) Color(0xFF8B5A2B) else Color(0xFFE0DCD7)
                )
            )
        }
    }
}