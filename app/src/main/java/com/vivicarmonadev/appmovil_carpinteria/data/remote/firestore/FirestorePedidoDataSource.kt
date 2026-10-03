package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.PedidoDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * DataSource de Firestore para pedidos.

 * Es la única clase que habla DIRECTAMENTE con la colección `orders`.
 * El Repository la usa para gestionar los pedidos.
 */
class FirestorePedidoDataSource {
    private val firestore: FirebaseFirestore = Firebase.firestore
    companion object {
        private const val COLLECTION_PEDIDOS = "orders"
    }

    // LECTURA — Pedidos de UN cliente (reactivo)

    fun getPedidosByClientFlow(clientUid: String): Flow<List<PedidoDto>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_PEDIDOS)
            .whereEqualTo("clientUid", clientUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val pedidos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PedidoDto::class.java)
                } ?: emptyList()

                trySend(pedidos)
            }

        awaitClose { listener.remove() }
    }

    // LECTURA — Un pedido por ID

    suspend fun getPedidoById(id: String): Result<PedidoDto?> {
        return try {
            val snapshot = firestore.collection(COLLECTION_PEDIDOS)
                .document(id)
                .get()
                .await()

            if (snapshot.exists()) {
                Result.success(snapshot.toObject(PedidoDto::class.java))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREAR

    suspend fun createPedido(pedido: PedidoDto): Result<PedidoDto> {
        return try {
            val docRef = if (pedido.id.isBlank()) {
                firestore.collection(COLLECTION_PEDIDOS).document()
            } else {
                firestore.collection(COLLECTION_PEDIDOS).document(pedido.id)
            }

            val dtoWithId = pedido.copy(id = docRef.id)
            docRef.set(dtoWithId).await()

            Result.success(dtoWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ACTUALIZAR

    suspend fun updatePedido(pedido: PedidoDto): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_PEDIDOS)
                .document(pedido.id)
                .set(pedido)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ACTUALIZAR SOLO EL ESTADO

    suspend fun updateEstado(
        pedidoId: String,
        nuevoEstado: String
    ): Result<Unit> {
        return try {
            val fields = mapOf(
                "status" to nuevoEstado,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            firestore.collection(COLLECTION_PEDIDOS)
                .document(pedidoId)
                .update(fields)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ELIMINAR

    suspend fun deletePedido(id: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_PEDIDOS)
                .document(id)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}