package com.example.moviefilms.repository

import com.example.moviefilms.network.MainMoviesApi
import javax.inject.Inject


class MainMoviesRepository @Inject constructor(private val mainMoviesApi: MainMoviesApi){

    suspend fun getFilmsList(page: Int){
        mainMoviesApi.getFilmsList(page)
    }
}