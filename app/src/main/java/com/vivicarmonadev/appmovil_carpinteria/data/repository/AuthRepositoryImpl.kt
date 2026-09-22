package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.UserDto
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.remote.auth.FirebaseAuthDataSource
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestoreUserDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import com.google.firebase.auth.ktx.auth

/**
 * Implementación del AuthRepository usando Firebase.
 *
 * Es el "pegamento" entre la capa de dominio (que solo conoce la interfaz)
 * y la capa de datos (que conoce Firebase).
 *
 * Responsabilidades:
 *  1. Coordinar FirebaseAuthDataSource + FirestoreUserDataSource
 *  2. Convertir entre UserDto (Firestore) y User (dominio)
 *  3. Envolver errores en Result
 */
class AuthRepositoryImpl(
    private val authDataSource: FirebaseAuthDataSource = FirebaseAuthDataSource(),
    private val userDataSource: FirestoreUserDataSource = FirestoreUserDataSource()
) : AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    // USUARIO ACTUAL (reactivo)

    override val currentUser: Flow<User?> = callbackFlow {
        var firestoreListener: ListenerRegistration? = null

        // Listener de Firebase Auth (detecta login/logout)
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser

            // Siempre remover el listener de Firestore al cambiar sesión
            firestoreListener?.remove()
            firestoreListener = null

            if (firebaseUser == null) {
                trySend(null)
            } else {
                // Escuchar el documento del usuario en Firestore en TIEMPO REAL
                firestoreListener = firestore
                    .collection("users")
                    .document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            // Error leyendo el documento → emitir null
                            trySend(null)
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            val userDto = snapshot.toObject(UserDto::class.java)
                            trySend(userDto?.toDomain())
                        } else {
                            // El documento no existe todavía → emitir null
                            trySend(null)
                        }
                    }
            }
        }

        auth.addAuthStateListener(authListener)

        awaitClose {
            auth.removeAuthStateListener(authListener)
            firestoreListener?.remove()
        }
    }

    // REGISTRO

    override suspend fun register(
        nombres: String,
        apellidos: String,
        telefono: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User> {
        return try {
            val displayName = "$nombres $apellidos".trim()
            val firebaseUser = authDataSource.createUserWithEmail(
                email = email,
                password = password,
                displayName = displayName
            )

            if (firebaseUser == null) {
                return Result.failure(Exception("No se pudo crear el usuario en Firebase Auth"))
            }

            val userDto = UserDto(
                uid = firebaseUser.uid,
                nombres = nombres,
                apellidos = apellidos,
                telefono = telefono,
                email = email,
                role = role.toFirestoreValue(),
                photoUrl = null
            )

            val saveResult = userDataSource.saveUser(userDto)

            if (saveResult.isFailure) {
                return Result.failure(
                    saveResult.exceptionOrNull()
                        ?: Exception("Error al guardar en Firestore")
                )
            }

            Result.success(userDto.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LOGIN CON EMAIL

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val firebaseUser = authDataSource.signInWithEmail(email, password)

            if (firebaseUser == null) {
                return Result.failure(Exception("Credenciales incorrectas"))
            }

            val result = userDataSource.getUserById(firebaseUser.uid)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al leer usuario")
                )
            }

            val userDto = result.getOrNull()

            if (userDto == null) {
                return Result.failure(Exception("El usuario no existe en Firestore"))
            }

            Result.success(userDto.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // LOGIN CON GOOGLE
    // ============================================
    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            // 1. Autenticar en Firebase con el idToken de Google
            val firebaseUser = authDataSource.signInWithGoogleIdToken(idToken)

            if (firebaseUser == null) {
                return Result.failure(Exception("No se pudo autenticar con Google"))
            }

            // 2. Buscar el usuario en Firestore
            val result = userDataSource.getUserById(firebaseUser.uid)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al leer usuario")
                )
            }

            val existingUser = result.getOrNull()

            if (existingUser != null) {
                // Usuario ya existía → devolverlo
                Result.success(existingUser.toDomain())
            } else {
                // Primera vez con Google → crear documento básico en Firestore
                val fullName = firebaseUser.displayName ?: ""
                val nameParts = fullName.split(" ").filter { it.isNotBlank() }

                val newUserDto = UserDto(
                    uid = firebaseUser.uid,
                    nombres = nameParts.firstOrNull() ?: "",
                    apellidos = if (nameParts.size > 1)
                        nameParts.drop(1).joinToString(" ") else "",
                    telefono = "",
                    email = firebaseUser.email ?: "",
                    role = UserRole.CLIENT.toFirestoreValue(),
                    photoUrl = firebaseUser.photoUrl?.toString()
                )

                val saveResult = userDataSource.saveUser(newUserDto)

                if (saveResult.isFailure) {
                    return Result.failure(
                        saveResult.exceptionOrNull() ?: Exception("Error al guardar usuario")
                    )
                }

                Result.success(newUserDto.toDomain())
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // LOGOUT
    // ============================================
    override suspend fun logout(): Result<Unit> {
        return try {
            authDataSource.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // LEER USUARIO POR UID
    // ============================================
    override suspend fun getUserById(uid: String): Result<User?> {
        return try {
            val result = userDataSource.getUserById(uid)
            result.map { dto -> dto?.toDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(
        uid: String,
        nombres: String,
        apellidos: String,
        telefono: String
    ): Result<User> {
        return try {
            // 1. Actualizar solo los campos que cambiaron en Firestore
            val fields = mapOf(
                "nombres" to nombres,
                "apellidos" to apellidos,
                "telefono" to telefono
            )

            val updateResult = userDataSource.updateUserFields(uid, fields)

            if (updateResult.isFailure) {
                return Result.failure(
                    updateResult.exceptionOrNull() ?: Exception("Error al actualizar")
                )
            }

            // 2. Leer el usuario actualizado desde Firestore
            val getResult = userDataSource.getUserById(uid)

            if (getResult.isFailure) {
                return Result.failure(
                    getResult.exceptionOrNull() ?: Exception("Error al leer usuario")
                )
            }

            val updatedUser = getResult.getOrNull()

            if (updatedUser == null) {
                return Result.failure(Exception("El usuario no existe"))
            }

            Result.success(updatedUser.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}