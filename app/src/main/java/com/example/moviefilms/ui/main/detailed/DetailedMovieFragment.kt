package com.example.moviefilms.ui.main.detailed

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.moviefilms.R
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment

class DetailedMovieFragment: DaggerFragment(R.layout.detailed_movie_fragment) {

    private val args: DetailedMovieFragmentArgs by navArgs()

    private lateinit var viewModel: MainViewModel
    private lateinit var title: TextView
    private lateinit var saveButton: FloatingActionButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById(R.id.detailedMovieTitle)
        saveButton = view.findViewById(R.id.saveButton)
        //safe args
        val movie = args.movie
        title.text = movie.title

        viewModel = (activity as MainActivity).viewModel

        saveButton.setOnClickListener { 
            viewModel.insertMovie(movie)
            Snackbar.make(view, "Movie successfully saved", Snackbar.LENGTH_SHORT).show()
        }
    }
}