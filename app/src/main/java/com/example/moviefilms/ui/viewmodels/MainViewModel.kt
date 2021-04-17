package com.example.moviefilms.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviefilms.network.MainMoviesApi
import com.example.moviefilms.repository.MainMoviesRepository
//import com.example.moviefilms.repository.MainMoviesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainViewModel @Inject constructor(private val repository: MainMoviesRepository): ViewModel() {

    fun getFilmsList(page: Int) = viewModelScope.launch {
        repository.getFilmsList(page)
    }

    init {
        getFilmsList(1)
    }
}
