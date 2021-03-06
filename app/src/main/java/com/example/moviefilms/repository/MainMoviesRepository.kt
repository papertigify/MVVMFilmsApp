package com.example.moviefilms.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.moviefilms.Constants.Companion.MAX_SIZE
import com.example.moviefilms.Constants.Companion.PAGE_SIZE
import com.example.moviefilms.db.MoviesDao
import com.example.moviefilms.network.FilmListItem
import com.example.moviefilms.network.MainMoviesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainMoviesRepository @Inject constructor(private val mainMoviesApi: MainMoviesApi, private val moviesDao: MoviesDao){

    fun getAllMoviesPagerFlow(): Flow<PagingData<FilmListItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                maxSize = MAX_SIZE
            ),
            pagingSourceFactory = { AllMoviesPagingSource(mainMoviesApi) }

        ).flow
    }

    fun getSearchMoviesPagerFlow(query: String): Flow<PagingData<FilmListItem>>{
        return Pager(
                config = PagingConfig(
                        pageSize = PAGE_SIZE,
                        enablePlaceholders = false,
                        maxSize = MAX_SIZE
                ),
                pagingSourceFactory = { SearchMoviesPagingSource(mainMoviesApi, query) }

        ).flow
    }

    suspend fun getMovieTrailers(movieId: Int) = mainMoviesApi.getMovieTrailers(movieId)

    // db
    suspend fun insertMovie(movie: FilmListItem){
        moviesDao.insert(movie)
    }

    suspend fun deleteMovie(movie: FilmListItem){
        moviesDao.deleteMovie(movie)
    }

    fun getAllSavedMovies() = moviesDao.getAllSavedMovies()

    suspend fun isMovieInDatabase(movieId: Int): Boolean {
        return moviesDao.getMovie(movieId) != null
    }
}