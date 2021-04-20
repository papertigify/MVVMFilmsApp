package com.example.moviefilms.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.moviefilms.network.FilmListItem
import com.example.moviefilms.network.MainMoviesApi
import com.example.moviefilms.repository.MainMoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
//import com.example.moviefilms.repository.MainMoviesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainViewModel @Inject constructor(private val repository: MainMoviesRepository): ViewModel() {


    // мигрировать на Flow
    val allFilms: MutableLiveData<PagingData<FilmListItem>> = MutableLiveData()

    init {
        searchAllFilms()
    }

    fun getFilmsListFlow(): Flow<PagingData<FilmListItem>> = repository.getAllMoviesPagerFlow()
        .cachedIn(viewModelScope)

    fun getSearchMoviesFlow(query: String): Flow<PagingData<FilmListItem>> = repository.getSearchMoviesPagerFlow(query)
            .cachedIn(viewModelScope)

    private fun searchAllFilms() = viewModelScope.launch {
        getFilmsListFlow().collectLatest {
            allFilms.postValue(it)
        }
    }
}
