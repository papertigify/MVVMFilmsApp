package com.example.moviefilms.ui.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.moviefilms.Constants
import com.example.moviefilms.R
import com.example.moviefilms.network.FilmListItem
import com.example.moviefilms.network.MainMoviesApi
import com.example.moviefilms.network.TrailersListItem
import com.example.moviefilms.network.TrailersListResponse
import com.example.moviefilms.repository.MainMoviesRepository
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.utils.MyFileManager
import com.example.moviefilms.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
//import com.example.moviefilms.repository.MainMoviesRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import javax.inject.Inject


class MainViewModel @Inject constructor(private val repository: MainMoviesRepository, private val fileManager: MyFileManager, application: Application): AndroidViewModel(application) {

    private val TAG = "MainViewModel"
    private val _allFilms = MutableStateFlow(PagingData.empty<FilmListItem>())
    val allFilms: StateFlow<PagingData<FilmListItem>> = _allFilms.asStateFlow()

    private val _trailers = MutableStateFlow(Resource.init<String?>())
    val trailers: StateFlow<Resource<String?>> = _trailers.asStateFlow()

    var currentQuery: String = ""

    init {
        searchAllFilms()
    }

    // all movies
    private fun getFilmsListFlow(): Flow<PagingData<FilmListItem>> = repository.getAllMoviesPagerFlow()
        .cachedIn(viewModelScope)

    private fun searchAllFilms() = viewModelScope.launch {
        getFilmsListFlow().collectLatest {
            _allFilms.value = it
        }
    }

    // search movies
    fun getSearchMoviesFlow(query: String): Flow<PagingData<FilmListItem>> = repository.getSearchMoviesPagerFlow(query)
            .cachedIn(viewModelScope)


    // getting trailer
    fun getMovieTrailers(movieId: Int) = viewModelScope.launch {
        try {
            val response = repository.getMovieTrailers(movieId)
            val url = getTrailerUrl(response)
            _trailers.value = Resource.success(url)
        } catch (e: IOException){
            _trailers.value = Resource.error(e.message)
        } catch (e: HttpException){
            _trailers.value = Resource.error(e.message)
        }
    }

    private fun getTrailerUrl(response: TrailersListResponse?): String?{
        return if(response != null) {
            val item = response.results.find { it.site == "YouTube" }
            val url = item?.key?.let{"https://www.youtube.com/watch?v=${item.key}"}
            url
        } else {
            null
        }
    }

    // db calls
    fun insertMovie(movie: FilmListItem) = viewModelScope.launch {
        repository.insertMovie(movie)
    }

    fun deleteMovie(movie: FilmListItem) = viewModelScope.launch {
        repository.deleteMovie(movie)
    }

    private suspend fun isMovieInDatabase(movieId: Int): Boolean{
        return repository.isMovieInDatabase(movieId)
    }

    fun getAllSavedMovies() = repository.getAllSavedMovies()

    // some other work
    fun saveImagesToStorage(movie: FilmListItem) = viewModelScope.launch {
        if (movie.posterStoragePath == null && movie.backdropStoragePath == null && !isMovieInDatabase(movie.id)) {

            withContext(Dispatchers.IO) {
                val bitmapPoster = try {
                    loadWithGlide(Constants.posterPath, movie.poster_path)
                } catch (e: ExecutionException) {
                    loadWithGlideErrorPic()
                }
                val bitmapBackdrop = try {
                    loadWithGlide(Constants.backDropPath, movie.backdrop_path)
                } catch (e: ExecutionException) {
                    loadWithGlideErrorPic()
                }

                val posterPath = fileManager.saveImage(getApplication() as Application, bitmapPoster)
                val backdropPath = fileManager.saveImage(getApplication() as Application, bitmapBackdrop)
                movie.posterStoragePath = posterPath
                movie.backdropStoragePath = backdropPath
            }
            insertMovie(movie)
            Log.e(TAG, "Saved movie images")
        } else {
            Log.e(TAG, "Already saved")
        }
    }

    private fun loadWithGlide(basePath: String, path: String?): Bitmap{
        return Glide.with(getApplication() as Application)
                .asBitmap()
                .load("$basePath$path")
                .apply(RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL)
                )
                .submit().get()
    }
    private fun loadWithGlideErrorPic(): Bitmap {
        return Glide.with(getApplication() as Application)
                .asBitmap()
                .load(R.drawable.pic_placeholder)
                .apply(RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL)
                )
                .submit().get()
    }

    fun getGenres(listGenreIds: List<Int>?): String{
        val genresList = listGenreIds?.map { Constants.genres[it] }
        var result = ""
        genresList?.forEach { result += "$it, "}
        return result.dropLast(2)
    }

}
