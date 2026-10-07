package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
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
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.SectionTitle
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.HomeEmptyHint
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.PortfolioMiniCard

@Composable
fun CarpenterHomeScreen(
    userName: String,
    portfolioItems: List<PortfolioItem>,
    onPortfolioItemClick: (String) -> Unit,
    onSeeAllPortfolioClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ---- HEADER (componente compartido) ----
        MervetaHeader(
            title = "Hola, $userName",
            subtitle = "Gestiona tus proyectos de hoy"
        )

        // ---- MÉTRICAS ----
        MetricsRow(
            portfolioCount = portfolioItems.size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // ---- PEDIDOS ACTIVOS ----
        SectionTitle(
            title = "Pedidos activos",
            actionText = "Ver todos →",
            onActionClick = { /* TODO: ir a pedidos */ },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        HomeEmptyHint(
            icon = Icons.Filled.Schedule,
            title = "Sin pedidos por ahora",
            subtitle = "Cuando un cliente te contacte, aparecerá acá",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // ---- MI PORTAFOLIO ----
        SectionTitle(
            title = "Mi portafolio",
            actionText =  "Ver todos →",
            onActionClick = onSeeAllPortfolioClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (portfolioItems.isEmpty()) {
            HomeEmptyHint(
                icon = Icons.Filled.Folder,
                title = "Tu portafolio está vacío",
                subtitle = "Publicá tu primer trabajo para atraer clientes",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(portfolioItems.take(5), key = { it.id }) { item ->
                    PortfolioMiniCard(
                        item = item,
                        onClick = { onPortfolioItemClick(item.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// MÉTRICAS (solo carpintero por ahora)

@Composable
private fun MetricsRow(
    portfolioCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
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
            icon = Icons.Filled.Folder,
            value = "$portfolioCount",
            label = "Trabajos",
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
            .background(Color(0xFFF5EFE7))
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