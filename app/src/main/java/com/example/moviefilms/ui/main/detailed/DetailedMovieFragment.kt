package com.example.moviefilms.ui.main.detailed

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.moviefilms.Constants
import com.example.moviefilms.R
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.example.moviefilms.utils.MyFileManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject

class DetailedMovieFragment: DaggerFragment(R.layout.detailed_movie_fragment) {

    private val args: DetailedMovieFragmentArgs by navArgs()

    @Inject
    lateinit var fileManager: MyFileManager

    private lateinit var viewModel: MainViewModel
    private lateinit var title: TextView
    private lateinit var saveButton: FloatingActionButton
    private lateinit var imageView: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById(R.id.detailedMovieTitle)
        saveButton = view.findViewById(R.id.saveButton)
        imageView = view.findViewById(R.id.detailedImageView)
        //safe args
        val movie = args.movie
        title.text = movie.title
        // checking where from this fragment opened, if from SavedMoviesFragment then load image from storage
        if(movie.storageFilePath != null) {
            val uri = movie.storageFilePath?.let {
                Uri.fromFile(File(it))
            }
            Glide.with(view).load(uri)
                    .apply(RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .error(R.drawable.pic_placeholder)
                    .into(imageView)

        } else {
            Glide.with(view)
                    .load("${Constants.posterPath}${movie.poster_path}")
                    .apply(RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .error(R.drawable.pic_placeholder)
                    .into(imageView)
        }

        viewModel = (activity as MainActivity).viewModel

        saveButton.setOnClickListener {
            // saving image to local storage
            val bitmap = imageView.drawable.toBitmap()
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val path = async{ fileManager.saveImage(requireContext().applicationContext, bitmap) }
                movie.storageFilePath = path.await()
                viewModel.insertMovie(movie)
            }
            Snackbar.make(view, "Movie successfully saved", Snackbar.LENGTH_SHORT).show()
        }
    }
}
