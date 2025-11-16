package com.example.spotifyexplorer.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "spotify_token_store")

class TokenDataStore(private val context: Context) {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val EXPIRATION = longPreferencesKey("expiration_time")
    }

    suspend fun saveToken(token: String, expiresInMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = token
            prefs[EXPIRATION] = expiresInMillis
        }
    }

    val accessToken: Flow<String?>
        get() = context.dataStore.data.map { it[ACCESS_TOKEN] }

    val expirationTime: Flow<Long?>
        get() = context.dataStore.data.map { it[EXPIRATION] }
}