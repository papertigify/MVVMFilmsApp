package com.example.moviefilms.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moviefilms.network.FilmListItem

@Database(
        entities = [FilmListItem::class],
        version = 1,
        exportSchema = false
)
abstract class MoviesDatabase: RoomDatabase() {
    abstract fun getMoviesDao(): MoviesDao
}