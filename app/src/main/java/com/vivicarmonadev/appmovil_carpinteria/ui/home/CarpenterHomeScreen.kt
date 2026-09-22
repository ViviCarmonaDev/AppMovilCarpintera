package com.vivicarmonadev.appmovil_carpinteria.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.SectionTitle

@Composable
fun CarpenterHomeScreen(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ---- HEADER ----
        CarpenterHeader(userName = userName)

        // ---- MÉTRICAS ----
        MetricsRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // ---- PEDIDOS ACTIVOS ----
        SectionTitle(
            title = "Pedidos activos",
            actionText = "Ver todos →",
            onActionClick = { /* TODO */ },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        EmptyOrdersHint(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // ---- MI PORTAFOLIO ----
        SectionTitle(
            title = "Mi portafolio",
            actionText = "Gestionar →",
            onActionClick = { /* TODO */ },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        EmptyPortfolioHint(
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================
// HEADER
// ============================================
@Composable
private fun CarpenterHeader(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5A2B))
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F7F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFF8B5A2B),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hola, $userName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF9F7F5)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Gestiona tus proyectos de hoy",
                    fontSize = 13.sp,
                    color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ============================================
// MÉTRICAS (estadísticas rápidas)
// ============================================
@Composable
private fun MetricsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            icon = Icons.Filled.Assignment,
            value = "0",
            label = "Pedidos",
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            icon = Icons.Filled.AttachMoney,
            value = "$0",
            label = "Ingresos",
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            icon = Icons.Filled.CheckCircle,
            value = "0",
            label = "Completados",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFFFF))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8B5A2B),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF6B6B6B)
        )
    }
}

// ============================================
// HINT DE PEDIDOS VACÍO
// ============================================
@Composable
private fun EmptyOrdersHint(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5EFE7))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Sin pedidos por ahora",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cuando un cliente te contacte, aparecerá acá",
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B),
                lineHeight = 16.sp
            )
        }
    }
}

// ============================================
// HINT DE PORTAFOLIO VACÍO
// ============================================
@Composable
private fun EmptyPortfolioHint(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5EFE7))
            .clickable { /* TODO: ir a portafolio */ }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Tu portafolio está vacío",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sube fotos de tus trabajos para atraer clientes",
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B),
                lineHeight = 16.sp
            )
        }
    }
}