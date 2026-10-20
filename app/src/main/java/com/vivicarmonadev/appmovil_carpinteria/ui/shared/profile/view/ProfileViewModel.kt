package com.vivicarmonadev.appmovil_carpinteria.ui.shared.profile.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PedidoRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.ReviewRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.remote.cloudinary.CloudinaryDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User

class ProfileViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl(),
    private val reviewRepository: ReviewRepositoryImpl = ReviewRepositoryImpl(),
    private val cloudinaryDataSource: CloudinaryDataSource = CloudinaryDataSource()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        //  Escuchar el usuario actual en tiempo real
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update { it.copy(user = user, isLoading = false) }
                    loadStats(user)
                } else {
                    _uiState.update { it.copy(user = null, isLoading = false) }
                }
            }
        }
    }

    private fun loadStats(user: User) {
        viewModelScope.launch {
            try {
                if (user.role.name.lowercase() == "carpenter") {
                    val pedidos = pedidoRepository.getPedidosAsignadosAMi(user.uid).firstOrNull() ?: emptyList()
                    val reviews = reviewRepository.getReviewsOnce(user.uid).getOrNull() ?: emptyList()

                    _uiState.update {
                        it.copy(
                            totalPedidos = pedidos.size,
                            totalResenas = reviews.size,
                            ratingPromedio = if (reviews.isNotEmpty()) reviews.map { r -> r.rating }.average() else 0.0
                        )
                    }
                }else {
                    val pedidos = pedidoRepository.getMyPedidos(user.uid).firstOrNull() ?: emptyList()
                    _uiState.update {
                        it.copy(
                            totalPedidos = pedidos.size,
                            totalResenas = 0,
                            ratingPromedio = 0.0
                        )
                    }
                }
            } catch (e: Exception) {
                // Ignorar errores de stats
            }
        }
    }

    fun uploadProfilePhoto(
        uriLocal: String,
        cloudinaryDataSource: CloudinaryDataSource,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true, photoError = null) }

            val user = authRepository.currentUser.firstOrNull()

            if (user == null) {
                _uiState.update { it.copy(isUploadingPhoto = false, photoError = "Usuario no identificado") }
                return@launch
            }

            // 1. Subir a Cloudinary
            val url = suspendCancellableCoroutine<String?> { continuation ->
                cloudinaryDataSource.subirImagen(
                    uriLocal = uriLocal,
                    carpeta = "usuarios",
                    onSuccess = { if (continuation.isActive) continuation.resume(it) },
                    onError = { if (continuation.isActive) continuation.resume(null) }
                )
            }

            if (url == null) {
                _uiState.update { it.copy(isUploadingPhoto = false, photoError = "Error al subir la foto") }
                return@launch
            }

            // 2. Guardar en Firestore
            val result = authRepository.updateUserPhoto(user.uid, url)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isUploadingPhoto = false) }
                    loadUserData() // Recargar para reflejar el cambio
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isUploadingPhoto = false, photoError = e.message) }
                }
            )
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // 1. Cargar usuario actual
                val user = authRepository.currentUser.firstOrNull()
                if (user == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "No hay usuario autenticado")
                    }
                    return@launch
                }

                // 2. Cargar estadísticas según el rol
                if (user.role.name.lowercase() == "carpenter") {
                    // CARPINTERO: contar pedidos asignados y reseñas recibidas
                    val pedidos = pedidoRepository.getPedidosAsignadosAMi(user.uid).firstOrNull() ?: emptyList()
                    val reviews = reviewRepository.getReviewsOnce(user.uid).getOrNull() ?: emptyList()

                    val totalPedidos = pedidos.size
                    val totalResenas = reviews.size
                    val ratingPromedio = if (reviews.isNotEmpty()) {
                        reviews.map { it.rating }.average()
                    } else 0.0

                    _uiState.update {
                        it.copy(
                            user = user,
                            totalPedidos = totalPedidos,
                            totalResenas = totalResenas,
                            ratingPromedio = ratingPromedio,
                            isLoading = false
                        )
                    }
                } else {
                    // CLIENTE: contar pedidos hechos
                    val pedidos = pedidoRepository.getMyPedidos(user.uid).firstOrNull() ?: emptyList()
                    _uiState.update {
                        it.copy(
                            user = user,
                            totalPedidos = pedidos.size,
                            totalResenas = 0, // Los clientes no reciben reseñas
                            ratingPromedio = 0.0,
                            isLoading = false
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar perfil: ${e.message}"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadUserData()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}