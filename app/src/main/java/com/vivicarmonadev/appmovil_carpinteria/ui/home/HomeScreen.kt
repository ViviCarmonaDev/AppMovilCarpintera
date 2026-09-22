package com.vivicarmonadev.appmovil_carpinteria.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.vivicarmonadev.appmovil_carpinteria.ui.navigation.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAF6F1)
    ) {
        if (currentUser == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF8B5A2B))
            }
        } else {
            val user = currentUser!!
            when (user.role) {
                UserRole.CARPENTER -> CarpenterHomeScreen(
                    userName = user.nombres.ifBlank { "Usuario" }
                )
                UserRole.CLIENT -> ClientHomeScreen(
                    userName = user.nombres.ifBlank { "Usuario" }
                )
            }
        }
    }
}