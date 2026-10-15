package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.home

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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.layout.offset
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.SectionTitleWithButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.EstadoPedidoBadge
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaHorizontalList
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.HomeEmptyHint
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.PortfolioMiniCard

@Composable
fun CarpenterHomeScreen(
    userName: String,
    portfolioItems: List<PortfolioItem>,
    pedidos: List<Pedido>,
    onPortfolioItemClick: (String) -> Unit,
    onSeeAllPortfolioClick: () -> Unit,
    onPedidoClick: (String) -> Unit,
    onSeeAllPedidosClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        MervetaHeader(
            title = "Hola, $userName",
            subtitle = "Gestiona tus proyectos de hoy",
            onNotificationClick = { /* Acción para la campana */ },
            onSettingsClick = { /* Acción para el engranaje */ }
        )

        // Métricas
        MetricsRow(
            portfolioCount = portfolioItems.size,
            onPedidosClick = onSeeAllPedidosClick,
            onIngresosClick = { /* Acción para Ingresos */ },
            onTrabajosClick = onSeeAllPortfolioClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Pedidos activos
        SectionTitleWithButton(
            icon = Icons.Filled.Schedule,
            title = "Pedidos activos",
            subtitle = "Estos son tus pedidos en curso",
            actionText = "Ver todos",
            onActionClick = onSeeAllPedidosClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        if (pedidos.isEmpty()) {
            HomeEmptyHint(
                icon = Icons.Filled.Schedule,
                title = "Sin pedidos por ahora",
                subtitle = "Cuando un cliente te contacte, aparecerá acá",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        } else {
            MervetaHorizontalList(
                items = pedidos,
                key = { it.id }
            ) { pedido ->
                PedidoMiniCard(
                    pedido = pedido,
                    onClick = { onPedidoClick(pedido.id) }
                )
            }
        }

        // Mi portafolio
        SectionTitleWithButton(
            icon = Icons.Filled.Folder,
            title = "Mi portafolio",
            subtitle = "Tus trabajos publicados",
            actionText = "Ver todos",
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
            MervetaHorizontalList(
                items = portfolioItems,
                key = { it.id }
            ) { item ->
                PortfolioMiniCard(
                    item = item,
                    onClick = { onPortfolioItemClick(item.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// CARD COMPACTO DE PEDIDO
@Composable
private fun PedidoMiniCard(
    pedido: Pedido,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF5EFE7))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        // Número + badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pedido.numeroPedido,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B),
                letterSpacing = 0.5.sp
            )
            EstadoPedidoBadge(estado = pedido.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cliente
        if (pedido.clienteNombre.isNotBlank()) {
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
                    color = Color(0xFF8B5A2B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Título
        Text(
            text = pedido.titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// MÉTRICAS (3 CARDS DE ARRIBA)
@Composable
private fun MetricsRow(
    portfolioCount: Int,
    onPedidosClick: () -> Unit,
    onIngresosClick: () -> Unit,
    onTrabajosClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard(
            icon = Icons.Filled.Assignment,
            value = "0",
            label = "Pedidos",
            onClick = onPedidosClick,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            icon = Icons.Filled.AttachMoney,
            value = "S/.0",
            label = "Ingresos",
            onClick = onIngresosClick,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            icon = Icons.Filled.Folder,
            value = "$portfolioCount",
            label = "Trabajos",
            onClick = onTrabajosClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .clipToBounds()     // recorta los círculos que se salgan
            .background(Color(0xFFFBF1E8)) // Fondo beige claro
            .clickable(onClick = onClick)
    ) {
        // --- ONDAS DECORATIVAS DE FONDO ---
        // Círculo grande arriba a la derecha
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)     // LO MOVEMOS FUERA DEL CARD
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E1D0).copy(alpha = 0.6f))
        )
        // Círculo pequeño abajo a la izquierda
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-25).dp, y = 25.dp)     //LO MOVEMOS FUERA DEL CARD
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E1D0).copy(alpha = 0.5f))
        )

        // --- CONTENIDO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Fila superior: Icono + Flecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono en cuadro marrón
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF8B5A2B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Flecha circular clickeable
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3E1D0))
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Ver más",
                        tint = Color(0xFF8B5A2B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Valor grande
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Label
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de progreso decorativa
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f) // 60% del ancho
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFF3E1D0))
            )
        }
    }
}
