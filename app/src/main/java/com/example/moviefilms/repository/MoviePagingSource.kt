package com.example.moviefilms.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.moviefilms.network.FilmListItem
import com.example.moviefilms.network.MainMoviesApi
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(
    private val moviesApi: MainMoviesApi
): PagingSource<Int, FilmListItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmListItem> {
        val position = params.key ?: START_PAGE
        return try {
            val response = moviesApi.getFilmsList(position)
            val data = response.results
            val nextKey = if (data.isEmpty()) null else position + 1
            val prevKey = if(position == START_PAGE) null else position - 1
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FilmListItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

private const val START_PAGE = 1