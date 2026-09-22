package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.UserDto
import kotlinx.coroutines.tasks.await


 // DataSource de Firestore para usuarios.

 //Es la única clase que habla DIRECTAMENTE con la colección `users`
 //de Firestore. El Repository la usa para guardar y leer usuarios
 //sin conocer los detalles de Firestore.

class FirestoreUserDataSource {

    private val firestore: FirebaseFirestore = Firebase.firestore

    companion object {
        private const val COLLECTION_USERS = "users"
    }

    // Guarda un usuario en Firestore usando su uid como ID del documento.
    // Si el documento ya existe, lo sobreescribe.

    suspend fun saveUser(user: UserDto): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_USERS)
                .document(user.uid)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lee un usuario por su uid.
    // Devuelve null si el documento no existe.

    suspend fun getUserById(uid: String): Result<UserDto?> {
        return try {
            val snapshot = firestore.collection(COLLECTION_USERS)
                .document(uid)
                .get()
                .await()

            if (snapshot.exists()) {
                val user = snapshot.toObject(UserDto::class.java)
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Actualiza campos específicos de un usuario.
    // Ejemplo: actualizar solo el teléfono sin tocar el resto.

    suspend fun updateUserFields(
        uid: String,
        fields: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_USERS)
                .document(uid)
                .update(fields)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}