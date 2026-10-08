package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FieldValue
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
 */
class FirestorePedidoDataSource {

    private val firestore: FirebaseFirestore = Firebase.firestore

    companion object {
        private const val COLLECTION_PEDIDOS = "orders"
        private const val COLLECTION_COUNTERS = "counters"
        private const val DOC_COUNTER_PEDIDOS = "pedidos"
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

    // LECTURA — Pedidos de UN carpintero (reactivo)

    fun getPedidosByCarpenterFlow(carpenterUid: String): Flow<List<PedidoDto>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_PEDIDOS)
            .whereEqualTo("carpenterUid", carpenterUid)
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

    // LECTURA — Pedidos LIBRES (reactivo)

    fun getPedidosLibresFlow(): Flow<List<PedidoDto>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_PEDIDOS)
            .whereEqualTo("carpenterUid", null)
            .whereEqualTo("status", "PENDIENTE")
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
                "updatedAt" to FieldValue.serverTimestamp()
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

    // ASIGNAR CARPINTERO (tomar pedido libre)

    suspend fun asignarCarpintero(
        pedidoId: String,
        carpenterUid: String,
        carpinteroNombre: String
    ): Result<Unit> {
        return try {
            val fields = mapOf(
                "carpenterUid" to carpenterUid,
                "carpinteroNombre" to carpinteroNombre,
                "updatedAt" to FieldValue.serverTimestamp()
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

    // NÚMERO SECUENCIAL (contador global)

    suspend fun getNextNumeroSecuencial(): Result<Long> {
        return try {
            val counterRef = firestore
                .collection(COLLECTION_COUNTERS)
                .document(DOC_COUNTER_PEDIDOS)

            val newValue = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(counterRef)
                val current = snapshot.getLong("ultimoNumero") ?: 0L
                val next = current + 1
                transaction.set(counterRef, mapOf("ultimoNumero" to next))
                next
            }.await()

            Result.success(newValue)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}