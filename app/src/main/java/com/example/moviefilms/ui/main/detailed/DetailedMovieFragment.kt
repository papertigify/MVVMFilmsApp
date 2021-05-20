package com.example.moviefilms.ui.main.detailed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.moviefilms.Constants
import com.example.moviefilms.R
import com.example.moviefilms.network.TrailersListResponse
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.example.moviefilms.utils.MyFileManager
import com.example.moviefilms.utils.Resource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutionException
import javax.inject.Inject

class DetailedMovieFragment: DaggerFragment(R.layout.detailed_movie_fragment) {

    private val args: DetailedMovieFragmentArgs by navArgs()

    private var currentTrailerUrl: String? = null
    private val TAG = "DetailedMovieFragment"

    private lateinit var viewModel: MainViewModel
    private lateinit var title: TextView
    private lateinit var saveButton: FloatingActionButton
    private lateinit var imageBackdrop: ImageView
    private lateinit var imageAdult: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var totalVotes: TextView
    private lateinit var buttonTrailer: Button
    private lateinit var genres: TextView
    private lateinit var overviewText: TextView
    private lateinit var rating: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            Class.forName("dalvik.system.CloseGuard")
                    .getMethod("setEnabled", Boolean::class.javaPrimitiveType)
                    .invoke(null, true)
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException(e)
        }
        viewModel = (activity as MainActivity).viewModel

        title = view.findViewById(R.id.detailedMovieTitle)
        saveButton = view.findViewById(R.id.saveButton)
        imageBackdrop = view.findViewById(R.id.detailedImageView)
        imageAdult = view.findViewById(R.id.imageAdult)
        ratingBar = view.findViewById(R.id.ratingBar)
        totalVotes = view.findViewById(R.id.textTotalVotes)
        buttonTrailer = view.findViewById(R.id.buttonTrailer)
        genres = view.findViewById(R.id.textGenres)
        overviewText = view.findViewById(R.id.textOverviewText)
        rating = view.findViewById(R.id.textRating)

        //safe args
        val movie = args.movie.copy()
        title.text = movie.title
        ratingBar.rating = movie.vote_average?.toFloat() ?: 0f
        rating.text = movie.vote_average.toString()
        totalVotes.text = movie.vote_count.toString()
        overviewText.text = movie.overview

        genres.text = viewModel.getGenres(movie.genre_ids)

        imageAdult.isVisible = movie.adult ?: false

        // checking where from this fragment opened, if from SavedMoviesFragment then load image from storage
        if (movie.backdropStoragePath != null) {
            val uri = movie.backdropStoragePath?.let {
                Uri.fromFile(File(it))
            }
            Glide.with(view).load(uri)
                    .apply(RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .error(R.drawable.pic_placeholder)
                    .into(imageBackdrop)

        } else {
            Glide.with(view)
                    .load("${Constants.backDropPath}${movie.backdrop_path}")
                    .apply(RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .error(R.drawable.pic_placeholder)
                    .into(imageBackdrop)
        }

        // getting trailers
        // first time opened this fragment or trailer url was null
        if (currentTrailerUrl == null && movie.trailerUrl == null) {
            Log.e(TAG, "first time")
            viewModel.getMovieTrailers(movie.id)
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.trailers
                        .onEach { response ->
                            when (response.status) {
                                Resource.Status.SUCCESS -> {
                                    movie.trailerUrl = response.data
                                    currentTrailerUrl = response.data
                                    buttonTrailer.setOnClickListener {
                                        if (response.data != null) {
                                            startTrailer(response.data)
                                        } else {
                                            Toast.makeText(context, "Trailer not found(", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                Resource.Status.ERROR -> {
                                    buttonTrailer.setOnClickListener {
                                        Toast.makeText(context, "Trailer not found(", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                Resource.Status.INIT -> null
                            }
                        }
                        .collect()
            }
        } else if (movie.trailerUrl != null) { // opened this fragment from saved movies
            Log.e(TAG, "from saved movies")
            buttonTrailer.setOnClickListener { movie.trailerUrl?.let { startTrailer(it) } }
        } else { // get this fragment from backstack
            Log.e(TAG, "from backstack")
            buttonTrailer.setOnClickListener { currentTrailerUrl?.let { startTrailer(it) } }
        }

        saveButton.setOnClickListener {
            // saving images to local storage
            viewModel.saveImagesToStorage(movie)
            Snackbar.make(view, "Movie successfully saved", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun startTrailer(trailerUrl: String){
        val uri = Uri.parse(trailerUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}

