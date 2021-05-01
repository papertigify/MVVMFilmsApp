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
import com.example.moviefilms.repository.MainMoviesRepository
import kotlinx.coroutines.flow.*
//import com.example.moviefilms.repository.MainMoviesRepository
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject


class MainViewModel @Inject constructor(private val repository: MainMoviesRepository): ViewModel() {


    private val _allFilms = MutableStateFlow(PagingData.empty<FilmListItem>())
    val allFilms: StateFlow<PagingData<FilmListItem>> = _allFilms.asStateFlow()

    init {
        searchAllFilms()
    }

    private fun getFilmsListFlow(): Flow<PagingData<FilmListItem>> = repository.getAllMoviesPagerFlow()
        .cachedIn(viewModelScope)

    fun getSearchMoviesFlow(query: String): Flow<PagingData<FilmListItem>> = repository.getSearchMoviesPagerFlow(query)
            .cachedIn(viewModelScope)


    private fun searchAllFilms() = viewModelScope.launch {
        getFilmsListFlow().collectLatest {
            _allFilms.value = it
        }
    }
}
