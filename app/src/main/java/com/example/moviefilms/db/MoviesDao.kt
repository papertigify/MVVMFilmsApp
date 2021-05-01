package com.example.moviefilms.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moviefilms.network.FilmListItem

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: FilmListItem)

    @Query("SELECT * FROM movieDb")
    fun getAllMovies(): LiveData<List<FilmListItem>>

    @Delete
    fun deleteMovie(movie: FilmListItem)
}