package com.vivicarmonadev.appmovil_carpinteria.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.vivicarmonadev.appmovil_carpinteria.R
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.SecondaryButton
import com.vivicarmonadev.appmovil_carpinteria.ui.theme.AppMovilCarpinteriaTheme

@Composable
fun WelcomeScreen2(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

    // CARD SUPERIOR — BLANCO

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)
                .background(Color(0xFFF9F7F5))
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Merveta Taller",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Un diseño con propósito",
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B),
                textAlign = TextAlign.Center
            )
        }

        // CARD INFERIOR — MARRÓN
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .clip(
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .background(Color(0xFFA67A4E))
                .padding(horizontal = 32.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = "Bienvenido",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF9F7F5)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Descubre el arte de la madera hecha a mano. " +
                            "Conecta con carpinteros locales y transforma " +
                            "tus ideas en piezas únicas.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFFF9F7F5).copy(alpha = 0.85f)
                )
            }

            // Botones en fila
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) //espacio entre botones
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SecondaryButton(
                        text = "Iniciar Sesión",
                        onClick = onLoginClick
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    SecondaryButton(
                        text = "Registrarme",
                        onClick = onRegisterClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreen2Preview() {
    AppMovilCarpinteriaTheme(darkTheme = false) {
        WelcomeScreen2(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}