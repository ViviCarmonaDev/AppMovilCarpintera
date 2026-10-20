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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
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
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.PedidoHomeCard
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.ui.client.components.CarpenterCard
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaHorizontalList
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.SectionTitleWithButton
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.HomeEmptyHint
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.PortfolioMiniCard

@Composable
fun ClientHomeScreen(
    userName: String,
    carpenters: List<Pair<User, CarpenterProfile>>,
    recentPortfolioItems: List<PortfolioItem>,
    recentOrders: List<Pedido>,
    photoUrl: String? = null,
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
            subtitle = "Encuentra a tu carpintero ideal",
            avatarUrl = photoUrl,
            onNotificationClick = { /* ... */ },
            onSettingsClick = { /* ... */ }
        )

        // Buscador
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Pedidos recientes
        SectionTitleWithButton(
            icon = Icons.Filled.Schedule,
            title = "Pedidos recientes",
            subtitle = "Tus pedidos en curso",
            actionText = "Ver todos",
            onActionClick = onSeeAllOrdersClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (recentOrders.isEmpty()) {
            HomeEmptyHint(
                icon = Icons.Filled.Schedule,
                title = "Aún no tienes pedidos",
                subtitle = "Solicita tu primer mueble a un carpintero",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            MervetaHorizontalList(
                items = recentOrders,
                key = { it.id }
            ) { pedido ->
                PedidoHomeCard(
                    pedido = pedido,
                    onClick = { onOrderClick(pedido.id) }
                )
            }
        }

        // Carpinteros destacados
        SectionTitleWithButton(
            icon = Icons.Filled.Person,
            title = "Carpinteros destacados",
            subtitle = "Profesionales cerca de ti",
            actionText = "Ver más",
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
        SectionTitleWithButton(
            icon = Icons.Filled.Folder,
            title = "Trabajos recientes",
            subtitle = "Explora los últimos trabajos",
            actionText = "Ver más",
            onActionClick = onSeeAllPortfolioClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (recentPortfolioItems.isEmpty()) {
            HomeEmptyHint(
                icon = Icons.Filled.Folder,
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
            .background(Color(0xFFFBF1E8))
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
