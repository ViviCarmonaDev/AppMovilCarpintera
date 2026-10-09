package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.component

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.fechaCorta

@Composable
fun CotizacionCard(
    cotizacion: Cotizacion,
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
        // Fila superior: número + badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cotizacion.numeroPedido.ifBlank { "Sin número" },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B),
                letterSpacing = 0.5.sp
            )
            EstadoCotizacionBadge(estado = cotizacion.estado)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cliente
        Text(
            text = "Cliente: ${cotizacion.clienteNombre}",
            fontSize = 12.sp,
            color = Color(0xFF6B6B6B)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Costo
        Text(
            text = cotizacion.costoTotalTexto,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Fecha
        Text(
            text = "Enviada el ${fechaCorta(cotizacion.createdAt)}",
            fontSize = 11.sp,
            color = Color(0xFF8A8A8A)
        )
    }
}

@Composable
private fun EstadoCotizacionBadge(estado: EstadoCotizacion) {
    val (bgColor, textColor) = when (estado) {
        EstadoCotizacion.PENDIENTE -> Color(0xFFFCEBD0) to Color(0xFF8B5A2B)
        EstadoCotizacion.ACEPTADA -> Color(0xFFD4E0D9) to Color(0xFF2E4A3E)
        EstadoCotizacion.RECHAZADA -> Color(0xFFFADBD8) to Color(0xFFC5544A)
        EstadoCotizacion.ANULADA -> Color(0xFFE0DCD7) to Color(0xFF6B6B6B)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado.toDisplayText().uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}