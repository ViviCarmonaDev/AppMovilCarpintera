package com.vivicarmonadev.appmovil_carpinteria.ui.shared.notificaciones.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.NotificacionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.collectLatest

class NotificacionesViewModel(
    private val notificacionRepository: NotificacionRepositoryImpl = NotificacionRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificacionesUiState())
    val uiState: StateFlow<NotificacionesUiState> = _uiState.asStateFlow()

    init {
        cargarNotificaciones()
    }

    private fun cargarNotificaciones() {
        viewModelScope.launch {
            authRepository.currentUser
                .flatMapLatest { user ->
                    if (user != null) {
                        notificacionRepository.getNotificaciones(user.uid)
                    } else {
                        flowOf(emptyList()) // Si no hay usuario, lista vacía
                    }
                }
                .collectLatest { lista ->
                    _uiState.update {
                        it.copy(
                            notificaciones = lista,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun marcarComoLeida(id: String) {
        viewModelScope.launch {
            notificacionRepository.marcarComoLeida(id)
        }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    notificacionRepository.marcarTodasComoLeidas(user.uid)
                }
            }
        }
    }
}