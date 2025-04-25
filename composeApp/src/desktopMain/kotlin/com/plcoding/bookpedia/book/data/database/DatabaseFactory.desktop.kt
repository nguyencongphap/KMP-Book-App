package com.plcoding.bookpedia.book.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

// In addition to BookDatabaseConstructor, we need this to create our db
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<FavoriteBookDatabase> {
        val os = System.getProperty("os.name").lowercase()
        // to get access to the specific directory where apps store db file
        // on a specific operating system, we need access to the user home
        val userHome = System.getProperty("user.home")
        val appDataDir = when {
            // specify where to save our db file
            os.contains("win") -> File(System.getenv("APPDATA"), "Bookpedia") // AppData dir path is an env var on windows
            os.contains("mac") -> File(userHome, "Library/Application Support/Bookpedia") // typical app data dir on MacOS
            else -> File(userHome, ".local/share/Bookpedia") // for linux
        }

        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }

        val dbFile = File(appDataDir, FavoriteBookDatabase.DB_NAME)
        return Room.databaseBuilder(dbFile.absolutePath)
    }

}