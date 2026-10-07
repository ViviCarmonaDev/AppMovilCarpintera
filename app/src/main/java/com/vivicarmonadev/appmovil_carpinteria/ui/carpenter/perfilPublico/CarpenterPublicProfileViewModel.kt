package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CarpenterRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del perfil público del carpintero.
 *
 * Carga 3 fuentes de datos en paralelo:
 *  - User (datos básicos)
 *  - CarpenterProfile (datos del taller)
 *  - Lista de PortfolioItems (sus trabajos)
 */
class CarpenterPublicProfileViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl(),
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarpenterPublicProfileUiState())
    val uiState: StateFlow<CarpenterPublicProfileUiState> = _uiState.asStateFlow()

    // Carga los datos del carpintero por su uid. Se llama al abrir la pantalla.

    fun loadCarpenter(uid: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // 1. Cargar datos básicos del User
            val userResult = authRepository.getUserById(uid)
            val user = userResult.getOrNull()

            if (user == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se encontró el carpintero"
                    )
                }
                return@launch
            }

            // 2. Cargar perfil del taller
            val profileResult = carpenterRepository.getCarpenterProfileOnce(uid)
            val profile = profileResult.getOrNull()

            if (profile == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        profile = null,
                        errorMessage = "Este carpintero aún no completó su perfil de taller"
                    )
                }
                return@launch
            }

            // 3. Cargar sus trabajos publicados
            portfolioRepository.getMyPortfolioItems(uid).collect { items ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        profile = profile,
                        portfolioItems = items
                    )
                }
            }
        }
    }

    // Construye el link de WhatsApp para contactar al carpintero. Formato: https://wa.me/51XXXXXXXXX?text=...

    fun buildWhatsAppLink(): String? {
        val profile = _uiState.value.profile ?: return null

        val telefono = profile.telefonoTaller
            .filter { it.isDigit() }
            .take(9)

        if (telefono.length != 9) return null

        val numeroConCodigo = "51$telefono"   // Código de Perú

        val nombreTaller = profile.nombreTaller.ifBlank { "tu taller" }
        val mensaje = "Hola! Vi $nombreTaller en Merveta y me gustaría consultarte sobre tus servicios."

        val mensajeEncoded = java.net.URLEncoder.encode(mensaje, "UTF-8")

        return "https://wa.me/$numeroConCodigo?text=$mensajeEncoded"
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
