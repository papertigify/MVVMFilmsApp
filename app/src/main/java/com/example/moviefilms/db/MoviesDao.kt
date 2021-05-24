package com.example.moviefilms.db

import androidx.room.*
import com.example.moviefilms.network.FilmListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: FilmListItem)

    @Query("SELECT * FROM movieDb")
    fun getAllSavedMovies(): Flow<List<FilmListItem>>

    @Query("SELECT * FROM movieDb WHERE id = :movieId")
    suspend fun getMovie(movieId: Int): FilmListItem?

    @Delete
    suspend fun deleteMovie(movie: FilmListItem)
}