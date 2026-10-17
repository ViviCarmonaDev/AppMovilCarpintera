package com.vivicarmonadev.appmovil_carpinteria.data.remote.cloudinary

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class CloudinaryDataSource {

    fun subirImagen(
        uriLocal: String,
        carpeta: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        MediaManager.get().upload(uriLocal)
            .unsigned("Merveta")
            .option("folder", carpeta) // SE ESPECIFICA LA CARPETA
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val urlSegura = resultData?.get("secure_url") as? String
                    if (urlSegura != null) {
                        onSuccess(urlSegura)
                    } else {
                        onError("No se pudo obtener la URL de la imagen")
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Error desconocido al subir imagen")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }
}