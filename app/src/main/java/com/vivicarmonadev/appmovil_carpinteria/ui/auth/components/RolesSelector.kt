package com.vivicarmonadev.appmovil_carpinteria.ui.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class UserRole { CLIENT, CARPENTER }

@Composable
fun RoleSelector(
    selected: UserRole?,
    onSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RoleCard(
            title = "Cliente",
            subtitle = "Busco profesionales",
            isSelected = selected == UserRole.CLIENT,
            onClick = { onSelected(UserRole.CLIENT) },
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Person
        )
        RoleCard(
            title = "Carpintero",
            subtitle = "Ofrezco servicios",
            isSelected = selected == UserRole.CARPENTER,
            onClick = { onSelected(UserRole.CARPENTER) },
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Construction
        )
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    // Colores directos según estado — SIN alpha para que se vean bien
    val containerColor = if (isSelected)
        Color(0xFF8B5A2B)          // marrón fuerte cuando seleccionado
    else
        Color(0xFFF0E8DE)          // beige un poco más marcado para que se vea la tarjeta

    val contentColor = if (isSelected)
        Color(0xFFF9F7F5)          // blanco hueso
    else
        Color(0xFF2C2C2C)          // carbón directo (SIN alpha)

    val borderColor = if (isSelected)
        Color(0xFF8B5A2B)
    else
        Color(0xFFD5CDC4)          // borde gris cálido más marcado

    val subtitleColor = if (isSelected)
        Color(0xFFF9F7F5).copy(alpha = 0.85f)
    else
        Color(0xFF555555)          // gris medio, sin alpha bajo

    Card(
        modifier = modifier
            .height(120.dp)
            .selectable(selected = isSelected, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = subtitleColor
            )
        }
    }
}