package com.vivicarmonadev.appmovil_carpinteria.ui.more

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.navigation.MainViewModel

@Composable
fun MoreScreen(
    mainViewModel: MainViewModel,
    onEditProfile: () -> Unit = {},
    onSettings: () -> Unit = {},
    onLanguage: () -> Unit = {},
    onHelp: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    onTerms: () -> Unit = {},
    onInvite: () -> Unit = {},
    onVouchers: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val user by mainViewModel.currentUser.collectAsStateWithLifecycle()

    val displayName = user?.fullName?.ifBlank { "Usuario" } ?: "Usuario"
    val displayEmail = user?.email?.ifBlank { "Sin correo" } ?: "Sin correo"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F1))
            .verticalScroll(rememberScrollState())
    ) {

        UserHeader(
            userName = displayName,
            userEmail = displayEmail,
            onEditProfile = onEditProfile
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectionTitleText(text = "Cuenta")

        MoreItem(
            icon = Icons.Filled.Person,
            title = "Editar perfil",
            subtitle = "Nombre, foto y datos personales",
            onClick = onEditProfile
        )
        MoreItem(
            icon = Icons.Filled.Settings,
            title = "Configuración",
            subtitle = "Notificaciones y preferencias",
            onClick = onSettings
        )
        MoreItem(
            icon = Icons.Filled.Language,
            title = "Idioma",
            subtitle = "Español",
            onClick = onLanguage
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectionTitleText(text = "Recompensas")

        MoreItem(
            icon = Icons.Filled.CardGiftcard,
            title = "Mis cupones",
            subtitle = "Descuentos y promociones",
            onClick = onVouchers
        )
        MoreItem(
            icon = Icons.Filled.Star,
            title = "Invitar amigos",
            subtitle = "Gana recompensas por referidos",
            onClick = onInvite
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectionTitleText(text = "Ayuda e información")

        MoreItem(
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            title = "Ayuda",
            subtitle = "Preguntas frecuentes y soporte",
            onClick = onHelp
        )
        MoreItem(
            icon = Icons.Filled.PrivacyTip,
            title = "Política de privacidad",
            subtitle = "Cómo usamos tus datos",
            onClick = onPrivacy
        )
        MoreItem(
            icon = Icons.Filled.PrivacyTip,
            title = "Términos y condiciones",
            subtitle = "Reglas de uso de la app",
            onClick = onTerms
        )

        Spacer(modifier = Modifier.height(16.dp))

        LogoutButton(onClick = onLogout)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================
// HEADER CON DATOS DEL USUARIO
// ============================================
@Composable
private fun UserHeader(
    userName: String,
    userEmail: String,
    onEditProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5A2B))
            .padding(horizontal = 20.dp, vertical = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFF9F7F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F7F5)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = userEmail,
                fontSize = 13.sp,
                color = Color(0xFFF9F7F5).copy(alpha = 0.75f)
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFF9F7F5).copy(alpha = 0.2f))
                .clickable(onClick = onEditProfile),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Editar",
                tint = Color(0xFFF9F7F5),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SectionTitleText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = Color(0xFF6B6B6B),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
private fun MoreItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5EFE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF8B5A2B),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C2C2C)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF8A8A8A),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFFFF))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = Color(0xFFC5544A),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Cerrar sesión",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC5544A)
        )
    }
}