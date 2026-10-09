package com.vivicarmonadev.appmovil_carpinteria.ui.client.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.tiempoRelativo
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.ui.client.components.CarpenterCard
import com.vivicarmonadev.appmovil_carpinteria.ui.client.components.RecentOrderCard
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaHorizontalList
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.SectionTitle
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.HomeEmptyHint
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.PortfolioMiniCard

@Composable
fun ClientHomeScreen(
    userName: String,
    carpenters: List<Pair<User, CarpenterProfile>>,
    recentPortfolioItems: List<PortfolioItem>,
    recentOrders: List<Pedido>,
    hasRecentOrders: Boolean,
    onCarpenterClick: (String) -> Unit,
    onPortfolioItemClick: (String) -> Unit,
    onSeeAllCarpentersClick: () -> Unit,
    onSeeAllPortfolioClick: () -> Unit,
    onOrderClick: (String) -> Unit,
    onSeeAllOrdersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        MervetaHeader(
            title = "Hola, $userName",
            subtitle = "¿Qué vas a construir hoy?"
        )

        // Buscador
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Categorías
        SectionTitle(
            title = "Categorías",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        CategoriesRow(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Pedidos recientes
        SectionTitle(
            title = "Pedidos recientes",
            actionText = "Ver todos →",
            onActionClick = onSeeAllOrdersClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (recentOrders.isEmpty()) {
            HomeEmptyHint(
                icon = Icons.Filled.Search,
                title = "Aún no tienes pedidos",
                subtitle = "Solicita tu primer mueble a un carpintero",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            MervetaHorizontalList(
                items = recentOrders,
                key = { it.id }
            ) { pedido ->
                RecentOrderCard(
                    titulo = pedido.titulo,
                    estadoTexto = pedido.status.toDisplayText().uppercase(),
                    estadoColor = estadoColorPara(pedido.status),
                    tiempoTexto = tiempoRelativo(pedido.createdAt),
                    onClick = { onOrderClick(pedido.id) }
                )
            }
        }

        // Carpinteros destacados
        SectionTitle(
            title = "Carpinteros destacados",
            actionText = "Ver más →",
            onActionClick = onSeeAllCarpentersClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (carpenters.isEmpty()) {
            HomeEmptyHint(
                icon = Icons.Filled.Person,
                title = "Aún no hay carpinteros",
                subtitle = "Cuando haya carpinteros registrados, aparecerán acá",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            MervetaHorizontalList(
                items = carpenters,
                key = { it.first.uid }
            ) { (user, profile) ->
                CarpenterCard(
                    user = user,
                    profile = profile,
                    onClick = { onCarpenterClick(user.uid) }
                )
            }
        }

        // Trabajos recientes
        SectionTitle(
            title = "Trabajos recientes",
            actionText = "Ver más →",
            onActionClick = onSeeAllPortfolioClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (recentPortfolioItems.isEmpty()) {
            HomeEmptyHint(
                icon = Icons.Filled.Search,
                title = "Aún no hay trabajos publicados",
                subtitle = "Explorá los trabajos cuando haya disponibles",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            MervetaHorizontalList(
                items = recentPortfolioItems,
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

@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5EFE7))
            .clickable { /* TODO: ir a búsqueda */ }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = Color(0xFF6B6B6B),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Buscar carpinteros...",
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A)
        )
    }
}

@Composable
private fun CategoriesRow(modifier: Modifier = Modifier) {
    val categories = listOf(
        CategoryItem("Muebles", Icons.Filled.Chair),
        CategoryItem("Puertas", Icons.Filled.DoorBack),
        CategoryItem("Cocina", Icons.Filled.Kitchen),
        CategoryItem("Salas", Icons.Filled.MeetingRoom)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEach { category ->
            CategoryChip(
                item = category,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class CategoryItem(
    val label: String,
    val icon: ImageVector
)

@Composable
private fun CategoryChip(
    item: CategoryItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5EFE7))
            .clickable { /* TODO: filtrar por categoría */ }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = Color(0xFF8B5A2B),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2C2C2C)
        )
    }
}

private fun estadoColorPara(estado: EstadoPedido): Color = when (estado) {
    EstadoPedido.PENDIENTE -> Color(0xFFE0A458)
    EstadoPedido.ACEPTADO -> Color(0xFF6B8E7F)
    EstadoPedido.EN_PROCESO -> Color(0xFF8B5A2B)
    EstadoPedido.TERMINADO -> Color(0xFF4A6B8B)
    EstadoPedido.ENTREGADO -> Color(0xFF4A6B5D)
    EstadoPedido.CANCELADO -> Color(0xFFC5544A)
}