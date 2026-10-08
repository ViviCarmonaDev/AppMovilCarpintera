package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.components.PortfolioItemFormContent
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaDiscardDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEditHeader

@Composable
fun CreatePortfolioScreen(
    viewModel: CreatePortfolioViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onSaveSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .systemBarsPadding()
    ) {

        MervetaEditHeader(
            title = "Nuevo trabajo",
            onBack = {
                val canExit = viewModel.onBackClick()
                if (canExit) onBack()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            PortfolioItemFormContent(
                titulo = uiState.titulo,
                descripcion = uiState.descripcion,
                categoria = uiState.categoria,
                material = uiState.material,
                tipoPrecio = uiState.tipoPrecio,
                precioReferencial = uiState.precioReferencial,
                tituloError = uiState.tituloError,
                descripcionError = uiState.descripcionError,
                categoriaError = uiState.categoriaError,
                materialError = uiState.materialError,
                precioError = uiState.precioError,
                errorMessage = uiState.errorMessage,
                isLoading = uiState.isLoading,
                isFormValid = uiState.isFormValid,
                buttonText = "Publicar",
                imagenesUrls = uiState.imagenesUrls,

                onTituloChange = { viewModel.onTituloChange(it) },
                onDescripcionChange = { viewModel.onDescripcionChange(it) },
                onCategoriaChange = { viewModel.onCategoriaChange(it) },
                onMaterialChange = { viewModel.onMaterialChange(it) },
                onTipoPrecioChange = { viewModel.onTipoPrecioChange(it) },
                onPrecioChange = { viewModel.onPrecioChange(it) },
                onImagenClick = { /* TODO: abrir galería */ },
                onEliminarImagen = { index -> viewModel.onEliminarImagen(index) },
                onSubmit = { viewModel.save() },
            )
        }

        if (uiState.showDiscardDialog) {
            MervetaDiscardDialog(
                onConfirm = {
                    viewModel.onDiscardConfirm()
                    onBack()
                },
                onCancel = { viewModel.onDiscardCancel() }
            )
        }
    }
}