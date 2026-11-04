package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestoreCategoriaDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CategoriaMaterial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoriaMaterialRepositoryImpl(
    private val dataSource: FirestoreCategoriaDataSource = FirestoreCategoriaDataSource()
) {
    fun getAllCategorias(): Flow<List<CategoriaMaterial>> {
        return dataSource.getAllCategoriasFlow().map { list -> list.map { it.toDomain() } }
    }

    suspend fun getCategoriaById(id: String): Result<CategoriaMaterial?> {
        return dataSource.getCategoriaById(id).map { it?.toDomain() }
    }

    suspend fun createCategoria(categoria: CategoriaMaterial): Result<CategoriaMaterial> {
        return dataSource.createCategoria(categoria.toDto()).map { it.toDomain() }
    }

    suspend fun updateCategoria(categoria: CategoriaMaterial): Result<Unit> {
        return dataSource.updateCategoria(categoria.toDto())
    }

    suspend fun deleteCategoria(id: String): Result<Unit> {
        return dataSource.deleteCategoria(id)
    }
}