package com.example.adminoffoodymind

import android.app.Application
import com.cloudinary.android.MediaManager

class FoodyMindAdminApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = HashMap<String, String>()
        config["cloud_name"] = "dc3o3bgsz"
        MediaManager.init(this, config)
    }
}
