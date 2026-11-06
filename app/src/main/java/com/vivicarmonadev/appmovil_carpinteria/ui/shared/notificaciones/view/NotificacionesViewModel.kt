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
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update { it.copy(isLoading = true) }
                    notificacionRepository.getNotificaciones(user.uid).collect { lista ->
                        _uiState.update {
                            it.copy(
                                notificaciones = lista,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(notificaciones = emptyList(), isLoading = false)
                    }
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