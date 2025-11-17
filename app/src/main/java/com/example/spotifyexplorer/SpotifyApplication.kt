package com.example.spotifyexplorer

import android.app.Application
import android.util.Log
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import com.example.spotifyexplorer.data.worker.scheduleSpotifyTokenRefresh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpotifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        scheduleSpotifyTokenRefresh(
            context = this,
            clientId = "03619e7aa0344a45a9fe014a969a62ea",
            clientSecret = "6c994bd714a94ee79d8f6c985716c3a9"
        )

        /*val tokenStore = TokenDataStore(this)

        // Launch a coroutine to clear token on app start
        CoroutineScope(Dispatchers.IO).launch {
            tokenStore.clearToken()
            Log.d("TokenDataStore", "Access token and expiration cleared on app launch")
        }*/
    }
}