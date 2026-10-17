package com.vivicarmonadev.appmovil_carpinteria

import android.app.Application
import com.cloudinary.android.MediaManager

class InitCloudinary : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = HashMap<String, String>()

        config["cloud_name"] = "we0qh2xq"
        MediaManager.init(this, config)
    }
}