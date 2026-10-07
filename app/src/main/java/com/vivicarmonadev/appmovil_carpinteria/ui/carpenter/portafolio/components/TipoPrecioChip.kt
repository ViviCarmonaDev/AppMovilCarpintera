package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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

/**
 * Chip para elegir tipo de precio (FIJO / A TRATAR).
 * Compartido entre CREATE y EDIT.
 */
@Composable
fun TipoPrecioChip(
    text: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) Color(0xFF8B5A2B) else Color(0xFFF5EFE7)
    val contentColor = if (isSelected) Color(0xFFF9F7F5) else Color(0xFF2C2C2C)
    val subtitleColor = if (isSelected)
        Color(0xFFF9F7F5).copy(alpha = 0.85f)
    else
        Color(0xFF6B6B6B)
    val borderColor = if (isSelected) Color(0xFF8B5A2B) else Color(0xFFE0DCD7)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = subtitleColor
            )
        }
    }
}