package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun copiarUriACache(context: Context, uri: Uri): String? {
    return try {
        // Abre el flujo de entrada de la URI original
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        // Crea un archivo temporal en la caché de la app
        val archivoTemporal = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")

        // Copia el contenido
        val outputStream = FileOutputStream(archivoTemporal)
        inputStream.copyTo(outputStream)

        // Cierra los flujos
        inputStream.close()
        outputStream.close()

        // Devuelve la ruta absoluta del archivo (que Cloudinary puede leer)
        archivoTemporal.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}