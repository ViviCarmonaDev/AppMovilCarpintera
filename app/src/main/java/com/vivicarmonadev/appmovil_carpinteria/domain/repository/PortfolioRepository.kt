package com.vivicarmonadev.appmovil_carpinteria.domain.repository

import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de trabajos del portafolio.
 *
 * Define QUÉ se puede hacer con los trabajos, pero no CÓMO.
 * El "cómo" lo implementa PortfolioRepositoryImpl en la capa data.
 *
 * Permisos:
 *  - Cliente: solo puede LEER trabajos
 *  - Carpintero: puede LEER, CREAR, EDITAR y ELIMINAR sus propios trabajos
 */
interface PortfolioRepository {

     // Flujo reactivo de TODOS los trabajos del portafolio. Se usa para el catálogo general (vista del cliente).

    fun getAllPortfolioItems(): Flow<List<PortfolioItem>>

     // Flujo reactivo de los trabajos de UN carpintero específico. Se usa para que el carpintero vea y gestione sus propios trabajos.

    fun getMyPortfolioItems(uid: String): Flow<List<PortfolioItem>>

     // Lee un trabajo específico por ID. Devuelve null si no existe.

    suspend fun getPortfolioItemById(id: String): Result<PortfolioItem?>

     // Crea un nuevo trabajo. Devuelve el trabajo creado con su ID asignado por Firestore.

    suspend fun createPortfolioItem(item: PortfolioItem): Result<PortfolioItem>

     // Actualiza un trabajo existente. Solo el dueño (uid) puede actualizarlo.

    suspend fun updatePortfolioItem(item: PortfolioItem): Result<PortfolioItem>

     // Elimina un trabajo por ID. Solo el dueño (uid) puede eliminarlo.

    suspend fun deletePortfolioItem(id: String): Result<Unit>

     // Busca trabajos por un criterio de texto. Busca coincidencias en título, categoría o material.

    suspend fun searchPortfolioItems(query: String): Result<List<PortfolioItem>>
}