package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.components

import androidx.compose.runtime.Composable
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaConfirmDialog

/**
 * Diálogo de confirmación para eliminar un trabajo del portafolio.

 * Reutiliza MervetaConfirmDialog con los textos específicos del portafolio.
 */

@Composable
fun DeletePortfolioScreen(
    itemTitle: String,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    MervetaConfirmDialog(
        title = "Eliminar trabajo",
        message = "¿Estás seguro de que quieres eliminar \"$itemTitle\"? Esta acción no se puede deshacer.",
        confirmText = "Eliminar",
        cancelText = "Cancelar",
        isLoading = isDeleting,
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}