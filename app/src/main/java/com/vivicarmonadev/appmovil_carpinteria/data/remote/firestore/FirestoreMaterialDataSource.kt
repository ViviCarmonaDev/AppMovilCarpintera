package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.MaterialDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreMaterialDataSource {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val collection = "materiales"

    fun getAllMaterialsFlow(): Flow<List<MaterialDto>> = callbackFlow {
        val listener = firestore.collection(collection)
            .orderBy("nombre", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val materiales = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MaterialDto::class.java)
                } ?: emptyList()
                trySend(materiales)
            }
        awaitClose { listener.remove() }
    }

    fun getMaterialesByCategoriaFlow(categoriaId: String): Flow<List<MaterialDto>> = callbackFlow {
        val listener = firestore.collection("materiales")
            .whereEqualTo("categoriaId", categoriaId)
            .orderBy("nombre", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val materiales = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MaterialDto::class.java)
                } ?: emptyList()
                trySend(materiales)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getMaterialById(id: String): Result<MaterialDto?> {
        return try {
            val snapshot = firestore.collection(collection).document(id).get().await()
            Result.success(snapshot.toObject(MaterialDto::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createMaterial(dto: MaterialDto): Result<MaterialDto> {
        return try {
            val docRef = firestore.collection(collection).document()
            val withId = dto.copy(id = docRef.id)
            docRef.set(withId).await()
            Result.success(withId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMaterial(dto: MaterialDto): Result<Unit> {
        return try {
            firestore.collection(collection).document(dto.id).set(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMaterial(id: String): Result<Unit> {
        return try {
            firestore.collection(collection).document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}