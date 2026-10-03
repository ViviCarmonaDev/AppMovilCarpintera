package com.vivicarmonadev.appmovil_carpinteria.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.SectionTitle
import com.vivicarmonadev.appmovil_carpinteria.ui.home.components.CarpenterCard
import com.vivicarmonadev.appmovil_carpinteria.ui.home.components.EmptyOrdersCard
import com.vivicarmonadev.appmovil_carpinteria.ui.home.components.PortfolioMiniCard

@Composable
fun ClientHomeScreen(
    userName: String,
    carpenters: List<Pair<User, CarpenterProfile>>,
    recentPortfolioItems: List<PortfolioItem>,
    hasRecentOrders: Boolean,
    onCarpenterClick: (String) -> Unit,
    onPortfolioItemClick: (String) -> Unit,
    onSeeAllCarpentersClick: () -> Unit,
    onSeeAllPortfolioClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ---- HEADER ----
        ClientHeader(userName = userName)

        // ---- BÚSQUEDA ----
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // ---- CATEGORÍAS ----
        SectionTitle(
            title = "Categorías",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        CategoriesRow(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // ---- PEDIDOS RECIENTES ----
        SectionTitle(
            title = "Pedidos recientes",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (hasRecentOrders) {
            // TODO: cuando implementemos pedidos
            Box(modifier = Modifier.padding(horizontal = 20.dp))
        } else {
            EmptyOrdersCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // ---- CARPINTEROS DESTACADOS ----
        SectionTitle(
            title = "Carpinteros destacados",
            actionText = if (carpenters.isNotEmpty()) "Ver más →" else null,
            onActionClick = onSeeAllCarpentersClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (carpenters.isEmpty()) {
            EmptyCarpentersHint(
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                carpenters.take(3).forEach { (user, profile) ->
                    CarpenterCard(
                        user = user,
                        profile = profile,
                        onClick = { onCarpenterClick(user.uid) }
                    )
                }
            }
        }

        // ---- TRABAJOS RECIENTES ----
        SectionTitle(
            title = "Trabajos recientes",
            actionText = if (recentPortfolioItems.isNotEmpty()) "Ver más →" else null,
            onActionClick = onSeeAllPortfolioClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (recentPortfolioItems.isEmpty()) {
            EmptyPortfolioHint(
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentPortfolioItems.take(6), key = { it.id }) { item ->
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

// ============================================
// HEADER
// ============================================
@Composable
private fun ClientHeader(userName: String) {
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
                    text = "¿Qué vas a construir hoy?",
                    fontSize = 13.sp,
                    color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ============================================
// BÚSQUEDA
// ============================================
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

// ============================================
// CATEGORÍAS
// ============================================
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

// ============================================
// HINT DE CARPINTEROS VACÍO
// ============================================
@Composable
private fun EmptyCarpentersHint(modifier: Modifier = Modifier) {
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
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Aún no hay carpinteros",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cuando haya carpinteros registrados, aparecerán acá",
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
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Aún no hay trabajos publicados",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Explorá los trabajos cuando haya disponibles",
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B),
                lineHeight = 16.sp
            )
        }
    }
}