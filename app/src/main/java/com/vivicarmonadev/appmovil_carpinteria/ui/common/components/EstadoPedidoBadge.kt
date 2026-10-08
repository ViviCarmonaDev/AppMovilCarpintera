package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido

/**
 * Badge visual para mostrar el estado de un pedido.
 * Reutilizable en cards y pantallas de detalle.
 */
@Composable
fun EstadoPedidoBadge(
    estado: EstadoPedido,
    modifier: Modifier = Modifier,
    textoGrande: Boolean = false
) {
    val (bgColor, textColor) = when (estado) {
        EstadoPedido.PENDIENTE -> Color(0xFFFCEBD0) to Color(0xFF8B5A2B)
        EstadoPedido.ACEPTADO -> Color(0xFFD4E0D9) to Color(0xFF2E4A3E)
        EstadoPedido.EN_PROCESO -> Color(0xFF8B5A2B) to Color(0xFFF9F7F5)
        EstadoPedido.TERMINADO -> Color(0xFFD6E5F0) to Color(0xFF2E4A5F)
        EstadoPedido.ENTREGADO -> Color(0xFF4A6B5D) to Color(0xFFF9F7F5)
        EstadoPedido.CANCELADO -> Color(0xFFFADBD8) to Color(0xFFC5544A)
    }

    val fontSize = if (textoGrande) 11.sp else 9.sp
    val horizontalPadding = if (textoGrande) 12.dp else 8.dp
    val verticalPadding = if (textoGrande) 5.dp else 3.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (textoGrande) 8.dp else 6.dp))
            .background(bgColor)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
    ) {
        Text(
            text = estado.toDisplayText().uppercase(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}