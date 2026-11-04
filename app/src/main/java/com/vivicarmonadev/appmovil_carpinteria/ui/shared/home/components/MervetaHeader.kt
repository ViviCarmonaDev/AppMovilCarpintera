package com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun MervetaHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    userName: String? = null, //  Opcional: si se pasa, se muestran 3 líneas
    avatarUrl: String? = null,
    avatarIcon: ImageVector = Icons.Filled.Person,
    onAvatarClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    verticalPadding: Dp = 60.dp,
    bottomPadding: Dp = 24.dp
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
           // .clip(RoundedCornerShape(bottomStart = 20.dp,bottomEnd = 20.dp))
            .background(Color(0xFF8B5A2B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = verticalPadding,
                    bottom = bottomPadding
                ),
            verticalAlignment = Alignment.CenterVertically // Alinea avatar y textos al centro
        ) {
            // ---- AVATAR (izquierda) ----
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F7F5))
                    .then(
                        if (onAvatarClick != null) {
                            Modifier.clickable(onClick = onAvatarClick)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = avatarIcon,
                        contentDescription = null,
                        tint = Color(0xFF8B5A2B),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ---- TEXTOS (al lado del avatar) ----
            Column(modifier = Modifier.weight(1f)) {
                if (userName != null) {
                    //  3 LÍNEAS
                    Text(
                        text = "$title 👋",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
                    )
                    Text(
                        text = userName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF9F7F5)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFFF9F7F5).copy(alpha = 0.75f)
                    )
                } else {
                    //  2 LÍNEAS (para otras pantallas)
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF9F7F5)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
                    )
                }
            }

            // ---- ÍCONOS (derecha) ----
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onNotificationClick != null) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable(onClick = onNotificationClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color(0xFFF9F7F5),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                if (onSettingsClick != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable(onClick = onSettingsClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Configuración",
                            tint = Color(0xFFF9F7F5),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}