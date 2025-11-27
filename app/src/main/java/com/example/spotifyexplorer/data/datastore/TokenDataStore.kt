package com.example.spotifyexplorer.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Creates DataStore instance using preferencesDataStore
// As the documentation specifies it should be a top-level property
// And give it a name of the file to be stored
// Stores data in key-value format
// Anywhere we have context we can access DataStore
private val Context.dataStore by preferencesDataStore(name = "spotify_token_store")

class TokenDataStore(private val context: Context) {

    // Definition of key-value pairs to be stored
    // companion object = static members of a class
    // so there is no need to create an instance of the class
    // it allows for direct access to the props of the class
    companion object {
        // stringPreferencesKey = key for the string value
        val ACCESS_TOKEN = stringPreferencesKey("access_token")

        // longPreferencesKey = key for the long value
        val EXPIRATION = longPreferencesKey("expiration_time")
    }

    /**
     * Saves the token and its expiration time in DataStore.
     */
    suspend fun saveToken(token: String, expiresInMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = token
            prefs[EXPIRATION] = expiresInMillis
        }
    }

    /**
     * Retrieves the access token from DataStore.
     * Returns a Flow of String?
     * Using Flow (state management) allows us to listen for changes in the data
     * map { it (key) } to return the value of the key
     * it - represents the preferences object
     */
    val accessToken: Flow<String?>
        get() = context.dataStore.data.map { it[ACCESS_TOKEN] }

    val expirationTime: Flow<Long?>
        get() = context.dataStore.data.map { it[EXPIRATION] }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(EXPIRATION)
        }
    }
}