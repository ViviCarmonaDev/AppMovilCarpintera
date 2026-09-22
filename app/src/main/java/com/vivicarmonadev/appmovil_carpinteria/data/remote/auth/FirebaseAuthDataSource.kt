package com.vivicarmonadev.appmovil_carpinteria.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataSource de Firebase Authentication.
 *
 * Es la única clase que habla DIRECTAMENTE con FirebaseAuth.
 * El Repository la usa para hacer las operaciones de auth sin
 * conocer los detalles de Firebase.
 *
 * No tiene lógica de negocio, solo envuelve las llamadas de Firebase
 * en funciones suspend (usando await()).
 */
@Singleton
class FirebaseAuthDataSource @Inject constructor() {

    private val auth: FirebaseAuth = Firebase.auth

    // Crea un usuario nuevo con email y contraseña en Firebase Auth y Devuelve el FirebaseUser con el uid asignado.

    suspend fun createUserWithEmail(
        email: String,
        password: String,
        displayName: String
    ): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user

        // Actualiza el displayName del perfil de Firebase Auth
        if (firebaseUser != null && displayName.isNotBlank()) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
        }

        return firebaseUser
    }

    // Inicia sesión con email y contraseña.

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    // Cierra la sesión actual.

    fun signOut() {
        auth.signOut()
    }

    // Devuelve el usuario autenticado actualmente, o null si no hay sesión.

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Devuelve el uid del usuario actual, o null si no hay sesión.

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}