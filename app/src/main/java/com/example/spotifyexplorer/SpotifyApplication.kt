package com.example.spotifyexplorer

import android.app.Application
import com.example.spotifyexplorer.data.worker.scheduleSpotifyTokenRefresh

class SpotifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        scheduleSpotifyTokenRefresh(
            context = this,
            clientId = "03619e7aa0344a45a9fe014a969a62ea",
            clientSecret = "6c994bd714a94ee79d8f6c985716c3a9"
        )
    }
}