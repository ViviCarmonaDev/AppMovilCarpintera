package com.vivicarmonadev.appmovil_carpinteria.ui.shared.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CarpenterRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PedidoRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.UserDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl(),
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl(),
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUid: String = ""
    private var currentRole: UserRole = UserRole.CLIENT

    // Jobs para poder cancelar los Flows
    private var pedidosJob: Job? = null

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    val uidChanged = currentUid != user.uid
                    currentUid = user.uid
                    currentRole = user.role

                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            isCarpenter = user.role == UserRole.CARPENTER
                        )
                    }

                    if (uidChanged) {
                        when (user.role) {
                            UserRole.CLIENT -> loadClientHome(user.uid)
                            UserRole.CARPENTER -> loadCarpenterHome(user.uid)
                        }
                    }
                }
            }
        }
    }

    // HOME DEL CLIENTE
    private fun loadClientHome(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHome = true) }

            loadAllCarpenters()
            loadRecentPortfolioItems()
            loadRecentOrders(uid)

            _uiState.update { it.copy(isLoadingHome = false) }
        }
    }

    private suspend fun loadAllCarpenters() {
        try {
            val carpentersList = mutableListOf<Pair<User, CarpenterProfile>>()

            val usersSnapshot = Firebase.firestore
                .collection("users")
                .whereEqualTo("role", "carpenter")
                .get()
                .await()

            for (doc in usersSnapshot.documents) {
                val userDto = doc.toObject(
                    UserDto::class.java
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

    private fun loadRecentOrders(uid: String) {
        pedidosJob?.cancel()
        pedidosJob = viewModelScope.launch {
            pedidoRepository.getMyPedidos(uid).collect { pedidos ->
                _uiState.update {
                    it.copy(recentOrders = pedidos.take(2))   // solo los primeros 2
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

        // Pedidos asignados + libres
        viewModelScope.launch {
            combine(
                pedidoRepository.getPedidosAsignadosAMi(uid),
                pedidoRepository.getPedidosLibres()
            ) { asignados, libres ->
                // Combinamos: primero los asignados, luego los libres (sin duplicados)
                (asignados + libres).distinctBy { it.id }
            }.collect { pedidos ->
                _uiState.update {
                    it.copy(recentOrders = pedidos)
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
    val recentOrders: List<Pedido> = emptyList(),      // ← Ahora tipado como Pedido

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