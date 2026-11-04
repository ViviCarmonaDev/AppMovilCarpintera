package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.CategoriaMaterialDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreCategoriaDataSource {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val collection = "categorias_materiales"

    fun getAllCategoriasFlow(): Flow<List<CategoriaMaterialDto>> = callbackFlow {
        val listener = firestore.collection(collection)
            .orderBy("nombre", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val categorias = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CategoriaMaterialDto::class.java)
                } ?: emptyList()
                trySend(categorias)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCategoriaById(id: String): Result<CategoriaMaterialDto?> {
        return try {
            val snapshot = firestore.collection(collection).document(id).get().await()
            Result.success(snapshot.toObject(CategoriaMaterialDto::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCategoria(dto: CategoriaMaterialDto): Result<CategoriaMaterialDto> {
        return try {
            val docRef = firestore.collection(collection).document()
            val withId = dto.copy(id = docRef.id)
            docRef.set(withId).await()
            Result.success(withId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategoria(dto: CategoriaMaterialDto): Result<Unit> {
        return try {
            firestore.collection(collection).document(dto.id).set(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategoria(id: String): Result<Unit> {
        return try {
            firestore.collection(collection).document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}