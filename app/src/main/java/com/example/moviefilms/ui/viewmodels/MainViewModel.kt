package com.example.moviefilms.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bumptech.glide.Glide
import com.example.moviefilms.network.FilmListItem
import com.example.moviefilms.network.MainMoviesApi
import com.example.moviefilms.network.TrailersListItem
import com.example.moviefilms.network.TrailersListResponse
import com.example.moviefilms.repository.MainMoviesRepository
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
//import com.example.moviefilms.repository.MainMoviesRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.Executors
import javax.inject.Inject


class MainViewModel @Inject constructor(private val repository: MainMoviesRepository): ViewModel() {


    private val _allFilms = MutableStateFlow(PagingData.empty<FilmListItem>())
    val allFilms: StateFlow<PagingData<FilmListItem>> = _allFilms.asStateFlow()

    private val _trailers = MutableStateFlow(Resource.init<TrailersListResponse>())
    val trailers: StateFlow<Resource<TrailersListResponse>> = _trailers.asStateFlow()

    var currentQuery: String = ""

    init {
        searchAllFilms()
    }

    private fun getFilmsListFlow(): Flow<PagingData<FilmListItem>> = repository.getAllMoviesPagerFlow()
        .cachedIn(viewModelScope)

    private fun searchAllFilms() = viewModelScope.launch {
        getFilmsListFlow().collectLatest {
            _allFilms.value = it
        }
    }

    fun getSearchMoviesFlow(query: String): Flow<PagingData<FilmListItem>> = repository.getSearchMoviesPagerFlow(query)
            .cachedIn(viewModelScope)

    fun getMovieTrailers(movieId: Int) = viewModelScope.launch {
        try {
            val response = repository.getMovieTrailers(movieId)
            _trailers.value = Resource.success(response)
        } catch (e: IOException){
            _trailers.value = Resource.error(e.message)
        } catch (e: HttpException){
            _trailers.value = Resource.error(e.message)
        }
    }

    // db calls
    fun insertMovie(movie: FilmListItem) = viewModelScope.launch {
        repository.insertMovie(movie)
    }

    fun deleteMovie(movie: FilmListItem) = viewModelScope.launch {
        repository.deleteMovie(movie)
    }

    fun getAllSavedMovies() = repository.getAllSavedMovies()
}
