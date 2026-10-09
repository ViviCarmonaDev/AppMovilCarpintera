package com.vivicarmonadev.appmovil_carpinteria.ui.shared.more

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
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
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.vivicarmonadev.appmovil_carpinteria.ui.navigation.MainViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader

@Composable
fun MoreScreen(
    mainViewModel: MainViewModel,
    onEditProfile: () -> Unit = {},
    onEditCarpenterProfile: () -> Unit = {},
    onSettings: () -> Unit = {},
    onLanguage: () -> Unit = {},
    onHelp: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    onTerms: () -> Unit = {},
    onInvite: () -> Unit = {},
    onVouchers: () -> Unit = {},
    onVerCotizaciones: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val user by mainViewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .verticalScroll(rememberScrollState())
    ) {
        // Header compartido
        MervetaHeader(
            title = "Más",
            subtitle = "Configuración y opciones",
            avatarIcon = Icons.Filled.MoreHoriz,
            verticalPadding = 40.dp,
            bottomPadding = 20.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ---- CUENTA ----
        SectionTitleText(text = "Cuenta")

        MoreItem(
            icon = Icons.Filled.Person,
            title = "Editar perfil",
            subtitle = "Nombre, foto y datos personales",
            onClick = onEditProfile
        )

        // Solo para carpinteros
        if (user?.role == UserRole.CARPENTER) {
            MoreItem(
                icon = Icons.Filled.Store,
                title = "Mi taller",
                subtitle = "RUC, nombre, habilidades",
                onClick = onEditCarpenterProfile
            )
        }

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

        // ---- RECOMPENSAS ----
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

        MoreItem(
            icon = Icons.Filled.RequestQuote,
            title = "Mis cotizaciones",
            subtitle = "Gestiona las cotizaciones enviadas",
            onClick = onVerCotizaciones
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ---- AYUDA E INFORMACIÓN ----
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

        // ---- CERRAR SESIÓN ----
        LogoutButton(onClick = onLogout)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// SECCIÓN

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

// ITEM

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

// CERRAR SESIÓN

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5EFE7))
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