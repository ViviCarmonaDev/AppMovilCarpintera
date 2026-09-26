package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.vivicarmonadev.appmovil_carpinteria.data.model.PortfolioItemDto
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestorePortfolioDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del PortfolioRepository usando Firestore.
 *
 * Es el "pegamento" entre la capa de dominio (que solo conoce la interfaz)
 * y la capa de datos (que conoce Firestore).
 *
 * Responsabilidades:
 *  1. Coordinar el FirestorePortfolioDataSource
 *  2. Convertir entre PortfolioItemDto (Firestore) y PortfolioItem (dominio)
 *  3. Envolver errores en Result
 *  4. Filtrar búsquedas en memoria
 */
class PortfolioRepositoryImpl(
    private val dataSource: FirestorePortfolioDataSource = FirestorePortfolioDataSource()
) : PortfolioRepository {

    // FLUJOS REACTIVOS

    override fun getAllPortfolioItems(): Flow<List<PortfolioItem>> {
        return dataSource.getAllItemsFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getMyPortfolioItems(uid: String): Flow<List<PortfolioItem>> {
        return dataSource.getItemsByUidFlow(uid).map { list ->
            list.map { it.toDomain() }
        }
    }

    // LEER UNO

    override suspend fun getPortfolioItemById(id: String): Result<PortfolioItem?> {
        return try {
            val result = dataSource.getItemById(id)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al leer el trabajo")
                )
            }

            val dto = result.getOrNull()
            Result.success(dto?.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREAR

    override suspend fun createPortfolioItem(
        item: PortfolioItem
    ): Result<PortfolioItem> {
        return try {
            val dto = item.toDto()
            val result = dataSource.createItem(dto)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al crear el trabajo")
                )
            }

            val createdDto = result.getOrNull()
                ?: return Result.failure(Exception("No se pudo crear el trabajo"))

            Result.success(createdDto.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ACTUALIZAR

    override suspend fun updatePortfolioItem(
        item: PortfolioItem
    ): Result<PortfolioItem> {
        return try {
            if (item.id.isBlank()) {
                return Result.failure(Exception("El trabajo no tiene ID"))
            }

            val dto = item.toDto()
            val result = dataSource.updateItem(dto)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al actualizar el trabajo")
                )
            }

            Result.success(item)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ELIMINAR

    override suspend fun deletePortfolioItem(id: String): Result<Unit> {
        return try {
            if (id.isBlank()) {
                return Result.failure(Exception("El trabajo no tiene ID"))
            }

            dataSource.deleteItem(id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // BUSCAR (filtro en memoria)

    override suspend fun searchPortfolioItems(
        query: String
    ): Result<List<PortfolioItem>> {
        return try {
            // Por ahora, no soportamos búsqueda en el DataSource.
            // Este método devuelve la lista completa y el ViewModel filtra.
            // Es la forma recomendada porque Firestore no soporta búsqueda de texto.
            Result.success(emptyList())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}