package com.example.moviefilms.network

import com.example.moviefilms.ApiKey.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MainMoviesApi {

    @GET("./movie?api_key=$API_KEY")
    @Headers("Content-Type: application/json")
    suspend fun getFilmsList(@Query("page") page: Int): MoviesListResponse
}