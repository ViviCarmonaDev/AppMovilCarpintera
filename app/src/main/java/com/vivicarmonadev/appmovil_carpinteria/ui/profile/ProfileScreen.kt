package com.vivicarmonadev.appmovil_carpinteria.ui.profile

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.navigation.MainViewModel

@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    onEditProfile: () -> Unit = {}
) {
    val user by mainViewModel.currentUser.collectAsStateWithLifecycle()

    if (user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAF6F1)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF8B5A2B))
        }
        return
    }

    val currentUser = user!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F1))
            .verticalScroll(rememberScrollState())
    ) {
        // ---- HEADER ----
        ProfileHeader(
            userName = currentUser.fullName.ifBlank { "Usuario" },
            userRole = currentUser.role.name.lowercase(),
            onEditProfile = onEditProfile
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- ESTADÍSTICAS ----
        StatsRow(
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---- INFORMACIÓN ----
        SectionTitleText(text = "Información")

        InfoCard(
            modifier = Modifier.padding(horizontal = 20.dp),
            rows = listOf(
                "Nombre completo" to currentUser.fullName.ifBlank { "Sin nombre" },
                "Correo electrónico" to currentUser.email.ifBlank { "Sin correo" },
                "Teléfono" to currentUser.telefono.ifBlank { "Sin teléfono" },
                "Rol" to if (currentUser.role.name.lowercase() == "carpenter") "Carpintero" else "Cliente"
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---- BOTÓN EDITAR PERFIL ----
        EditProfileButton(onClick = onEditProfile)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================
// HEADER CON AVATAR
// ============================================
@Composable
private fun ProfileHeader(
    userName: String,
    userRole: String,
    onEditProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5A2B))
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar grande
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F7F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFF8B5A2B),
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F7F5),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rol con badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF9F7F5).copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (userRole == "carpenter") "Carpintero" else "Cliente",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF9F7F5)
                )
            }
        }
    }
}

// ============================================
// ESTADÍSTICAS
// ============================================
@Composable
private fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Filled.Star,
            value = "0",
            label = "Reseñas",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Filled.Person,
            value = "0",
            label = "Pedidos",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Filled.Star,
            value = "—",
            label = "Rating",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
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
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            fontSize = 18.sp,
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
// TÍTULO DE SECCIÓN
// ============================================
@Composable
private fun SectionTitleText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = Color(0xFF2C2C2C),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

// ============================================
// TARJETA DE INFORMACIÓN
// ============================================
@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    rows: List<Pair<String, String>>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFFFF))
            .padding(vertical = 8.dp)
    ) {
        rows.forEachIndexed { index, (label, value) ->
            InfoRow(label = label, value = value)
            if (index < rows.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(1.dp)
                        .background(Color(0xFFF0EDE8))
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B6B6B)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2C2C2C)
        )
    }
}

// ============================================
// BOTÓN EDITAR PERFIL
// ============================================
@Composable
private fun EditProfileButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF8B5A2B))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = null,
            tint = Color(0xFFF9F7F5),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "EDITAR PERFIL",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color(0xFFF9F7F5)
        )
    }
}