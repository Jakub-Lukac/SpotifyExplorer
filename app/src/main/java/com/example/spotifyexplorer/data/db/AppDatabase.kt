package com.example.spotifyexplorer.data.db

import com.example.spotifyexplorer.data.model.FavoriteTrack
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
/**
 * Room workflow
 * Room entities represent tables in the database.
 * Room DAOs (Data Access Objects) provide methods for CRUD operation on DB
 * Database class is the main access point to the underlying Room DB
 */
@Database(
    entities = [FavoriteTrack::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // abstract because Room creates implementation in behind

    // abstract method to get the DAO, once again the Room creates the implementation
    abstract fun favoriteTrackDao(): FavoriteTrackDao

    // static method to get the database instance
    companion object {
        // Volatile = immediately visible to other threads
        // So changed by one thread are immediately visible to other threads
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // wrapping in synchronized block to prevent multiple threads from accessing the database
            // avoid race conditions
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spotify_explorer.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}