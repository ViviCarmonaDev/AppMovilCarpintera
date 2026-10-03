package com.vivicarmonadev.appmovil_carpinteria.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CarpenterRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel del Home.
 *
 * Carga los datos según el rol del usuario:
 *  - Cliente: lista de carpinteros + trabajos recientes + pedidos (placeholder)
 *  - Carpintero: sus últimos trabajos + métricas
 */
class HomeViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl(),
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUid: String = ""
    private var currentRole: UserRole = UserRole.CLIENT

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    currentUid = user.uid
                    currentRole = user.role

                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            isCarpenter = user.role == UserRole.CARPENTER
                        )
                    }

                    when (user.role) {
                        UserRole.CLIENT -> loadClientHome()
                        UserRole.CARPENTER -> loadCarpenterHome(user.uid)
                    }
                }
            }
        }
    }

    // HOME DEL CLIENTE

    private fun loadClientHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHome = true) }

            loadAllCarpenters()
            loadRecentPortfolioItems()

            _uiState.update {
                it.copy(
                    isLoadingHome = false,
                    recentOrders = emptyList()
                )
            }
        }
    }

    private suspend fun loadAllCarpenters() {
        try {
            val carpentersList = mutableListOf<Pair<User, CarpenterProfile>>()

            val usersSnapshot = com.google.firebase.ktx.Firebase.firestore
                .collection("users")
                .whereEqualTo("role", "carpenter")
                .get()
                .await()

            for (doc in usersSnapshot.documents) {
                val userDto = doc.toObject(
                    com.vivicarmonadev.appmovil_carpinteria.data.model.UserDto::class.java
                )
                if (userDto != null) {
                    val user = userDto.toDomain()

                    val profileResult = carpenterRepository.getCarpenterProfileOnce(user.uid)
                    val profile = profileResult.getOrNull()

                    if (profile != null) {
                        carpentersList.add(user to profile)
                    }
                }
            }

            _uiState.update { it.copy(carpenters = carpentersList) }

        } catch (e: Exception) {
            _uiState.update {
                it.copy(errorMessage = "Error al cargar carpinteros: ${e.message}")
            }
        }
    }

    private fun loadRecentPortfolioItems() {
        viewModelScope.launch {
            portfolioRepository.getAllPortfolioItems().collect { items ->
                _uiState.update {
                    it.copy(recentPortfolioItems = items.take(10))
                }
            }
        }
    }

    // HOME DEL CARPINTERO

    private fun loadCarpenterHome(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHome = true) }

            portfolioRepository.getMyPortfolioItems(uid).collect { items ->
                _uiState.update {
                    it.copy(
                        myPortfolioItems = items,
                        isLoadingHome = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

// ESTADO DEL HOME

data class HomeUiState(
    val currentUser: User? = null,
    val isCarpenter: Boolean = false,

    val carpenters: List<Pair<User, CarpenterProfile>> = emptyList(),
    val recentPortfolioItems: List<PortfolioItem> = emptyList(),
    val recentOrders: List<Any> = emptyList(),

    val myPortfolioItems: List<PortfolioItem> = emptyList(),

    val isLoadingHome: Boolean = true,
    val errorMessage: String? = null
) {
    val userName: String
        get() = currentUser?.nombres?.ifBlank { "Usuario" } ?: "Usuario"

    val hasCarpenters: Boolean
        get() = carpenters.isNotEmpty()

    val hasRecentPortfolio: Boolean
        get() = recentPortfolioItems.isNotEmpty()

    val hasRecentOrders: Boolean
        get() = recentOrders.isNotEmpty()

    val hasMyPortfolio: Boolean
        get() = myPortfolioItems.isNotEmpty()
}
