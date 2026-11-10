package com.vivicarmonadev.appmovil_carpinteria.ui.client.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.PedidoHomeCard
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.ui.client.components.CarpenterCard
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaHorizontalList
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaSearchBar
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
    onSeeAllOrdersClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    notificacionesNoLeidas: Int = 0,
) {
    // --- ESTADO LOCAL PARA LA BÚSQUEDA ---
    var searchQuery by remember { mutableStateOf("") }

    // --- FILTRADO EN TIEMPO REAL ---
    // 1. Filtramos carpinteros por nombre, taller o habilidad
    val filteredCarpenters = if (searchQuery.isBlank()) {
        carpenters
    } else {
        carpenters.filter { (user, profile) ->
            user.fullName.contains(searchQuery, ignoreCase = true) ||
                    profile.nombreTaller.contains(searchQuery, ignoreCase = true) ||
                    profile.skills.any { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    // 2. Filtramos portafolio por título, material o categoría
    val filteredPortfolio = if (searchQuery.isBlank()) {
        recentPortfolioItems
    } else {
        recentPortfolioItems.filter { item ->
            item.titulo.contains(searchQuery, ignoreCase = true) ||
                    item.material.contains(searchQuery, ignoreCase = true) ||
                    item.categoria.contains(searchQuery, ignoreCase = true)
        }
    }

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
            onNotificationClick = onNotificationClick,
            notificacionesNoLeidas = notificacionesNoLeidas
        )

        // Buscador Oficial
        MervetaSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClear = { searchQuery = "" },
            placeholder = "Buscar carpintero o trabajo..."
        )

        // Pedidos recientes (Solo se muestran si no está buscando)
        if (searchQuery.isBlank()) {
            SectionTitleWithButton(
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
                MervetaHorizontalList(items = recentOrders, key = { it.id }) { pedido ->
                    PedidoHomeCard(pedido = pedido, onClick = { onOrderClick(pedido.id) })
                }
            }
        }

        // Carpinteros destacados / Resultados de búsqueda
        if (searchQuery.isBlank() || filteredCarpenters.isNotEmpty()) {
            SectionTitleWithButton(
                title = if (searchQuery.isBlank()) "Carpinteros destacados" else "Carpinteros encontrados",
                subtitle = if (searchQuery.isBlank()) "Profesionales cerca de ti" else "Resultados para \"$searchQuery\"",
                actionText = "Ver más",
                onActionClick = onSeeAllCarpentersClick,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            if (filteredCarpenters.isEmpty()) {
                HomeEmptyHint(
                    icon = Icons.Filled.Person,
                    title = "Aún no hay carpinteros",
                    subtitle = "Cuando haya carpinteros registrados, aparecerán acá",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            } else {
                MervetaHorizontalList(items = filteredCarpenters, key = { it.first.uid }) { (user, profile) ->
                    CarpenterCard(user = user, profile = profile, onClick = { onCarpenterClick(user.uid) })
                }
            }
        }

        // Trabajos recientes / Resultados de búsqueda
        if (searchQuery.isBlank() || filteredPortfolio.isNotEmpty()) {
            SectionTitleWithButton(
                title = if (searchQuery.isBlank()) "Trabajos recientes" else "Trabajos encontrados",
                subtitle = if (searchQuery.isBlank()) "Explora los últimos trabajos" else "Resultados para \"$searchQuery\"",
                actionText = "Ver más",
                onActionClick = onSeeAllPortfolioClick,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            if (filteredPortfolio.isEmpty()) {
                HomeEmptyHint(
                    icon = Icons.Filled.Folder,
                    title = "Aún no hay trabajos publicados",
                    subtitle = "Explorá los trabajos cuando haya disponibles",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            } else {
                MervetaHorizontalList(items = filteredPortfolio, key = { it.id }) { item ->
                    PortfolioMiniCard(item = item, onClick = { onPortfolioItemClick(item.id) })
                }
            }
        }

        // Mensaje si no hay ningún resultado en general
        if (searchQuery.isNotBlank() && filteredCarpenters.isEmpty() && filteredPortfolio.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron resultados para \"$searchQuery\"",
                    fontSize = 14.sp,
                    color = Color(0xFF6B6B6B),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}