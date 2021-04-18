package com.example.moviefilms.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.moviefilms.Constants.Companion.MAX_SIZE
import com.example.moviefilms.Constants.Companion.PAGE_SIZE
import com.example.moviefilms.network.FilmListItem
import com.example.moviefilms.network.MainMoviesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MainMoviesRepository @Inject constructor(private val mainMoviesApi: MainMoviesApi){

    fun getPagerFlow(): Flow<PagingData<FilmListItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                maxSize = MAX_SIZE
            ),
            pagingSourceFactory = { MoviePagingSource(mainMoviesApi) }

        ).flow
    }
}