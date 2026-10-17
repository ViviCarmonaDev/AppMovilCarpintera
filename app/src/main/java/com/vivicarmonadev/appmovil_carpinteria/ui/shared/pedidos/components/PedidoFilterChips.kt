package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido

@Composable
fun PedidoFilterChips(
    estadoSeleccionado: EstadoPedido?,
    onEstadoSelected: (EstadoPedido?) -> Unit,
    modifier: Modifier = Modifier
) {
    val opciones: List<Triple<String, EstadoPedido?, ImageVector>> = listOf(
        Triple("TODOS", null, Icons.Filled.Inventory2),
        Triple("PENDIENTES", EstadoPedido.PENDIENTE, Icons.Filled.AccessTime),
        Triple("ACEPTADOS", EstadoPedido.ACEPTADO, Icons.Filled.CheckCircle),
        Triple("EN PROCESO", EstadoPedido.EN_PROCESO, Icons.Filled.LocalShipping),
        Triple("ENTREGADOS", EstadoPedido.ENTREGADO, Icons.Filled.Verified),
        Triple("CANCELADOS", EstadoPedido.CANCELADO, Icons.Filled.Cancel)
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { (label, estado, icon) ->
            val isSelected = estadoSeleccionado == estado

            FilterChip(
                selected = isSelected,
                onClick = { onEstadoSelected(estado) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFFF9F7F5) else Color(0xFF8B5A2B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFFF9F7F5) else Color(0xFF2C2C2C),
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFFFBF1E8),
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