package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Diálogo de confirmación para descartar cambios sin guardar.
 * Reutilizable en cualquier formulario (create, edit, etc.).
 */

@Composable
fun MervetaDiscardDialog(
    title: String = "Descartar cambios",
    message: String = "Tenés cambios sin guardar. ¿Querés salir igual?",
    confirmText: String = "Salir sin guardar",
    cancelText: String = "Seguir editando",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = Color(0xFFC5544A),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    text = cancelText,
                    color = Color(0xFF6B6B6B)
                )
            }
        }
    )
}