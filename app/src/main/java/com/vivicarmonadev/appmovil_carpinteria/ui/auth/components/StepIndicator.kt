package com.vivicarmonadev.appmovil_carpinteria.ui.auth.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = currentStep.toFloat() / totalSteps,
        label = "step_progress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "PASO $currentStep DE $totalSteps",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color(0xFF8B5A2B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Fondo de la barra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    color = Color(0xFFE0DCD7),   // ← gris claro directo
                    shape = RoundedCornerShape(2.dp)
                )
        ) {
            // Barra de progreso animada
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .background(
                        color = Color(0xFF8B5A2B),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}