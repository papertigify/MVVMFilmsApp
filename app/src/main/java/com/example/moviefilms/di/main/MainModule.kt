package com.example.moviefilms.di.main

import com.example.moviefilms.network.MainMoviesApi
import com.example.moviefilms.repository.MainMoviesRepository
//import com.example.moviefilms.repository.MainMoviesRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    companion object{
        @MainScope
        @Provides
        fun provideMainMoviesApi(retrofit: Retrofit): MainMoviesApi{
            return retrofit.create(MainMoviesApi::class.java)
        }

        @MainScope
        @Provides
        fun provideMoviesRepository(mainMoviesApi: MainMoviesApi): MainMoviesRepository {
            return MainMoviesRepository(mainMoviesApi)
        }
    }
}