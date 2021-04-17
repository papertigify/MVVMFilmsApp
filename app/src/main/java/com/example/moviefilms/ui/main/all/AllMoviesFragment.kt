package com.example.moviefilms.ui.main.all

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.moviefilms.R
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.example.moviefilms.ui.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class AllMoviesFragment: DaggerFragment(R.layout.all_movies_fragment) {

    private val TAG = "AllMoviesFragment"
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "AllMoviesFragment 1")
        viewModel = (activity as MainActivity).viewModel
    }
}