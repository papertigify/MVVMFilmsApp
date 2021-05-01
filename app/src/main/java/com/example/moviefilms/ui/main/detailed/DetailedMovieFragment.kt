package com.example.moviefilms.ui.main.detailed

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.moviefilms.R
import dagger.android.support.DaggerFragment

class DetailedMovieFragment: DaggerFragment(R.layout.detailed_movie_fragment) {

    private val args: DetailedMovieFragmentArgs by navArgs()

    private lateinit var title: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById(R.id.detailedMovieTitle)

        val movie = args.movie

        title.text = movie.title
    }
}