package com.example.moviefilms.ui.main.all

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviefilms.R
import com.example.moviefilms.adapters.MyLoadStateAdapter
import com.example.moviefilms.adapters.PagingAdapter
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.example.moviefilms.ui.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllMoviesFragment: DaggerFragment(R.layout.all_movies_fragment) {

    private val TAG = "AllMoviesFragment"
    private lateinit var viewModel: MainViewModel
    private var searchJob: Job? = null
    private val mAdapter = PagingAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e(TAG, "AllMoviesFragment 1")
        progressBar = view.findViewById(R.id.loadingProgressBar)

        recyclerView = view.findViewById(R.id.rvAllMovies)
        recyclerView.apply {
            //layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter.withLoadStateHeaderAndFooter(
                header = MyLoadStateAdapter { mAdapter.retry() },
                footer = MyLoadStateAdapter { mAdapter.retry() }
            )
        }
        viewModel = (activity as MainActivity).viewModel

        // мигрировать на Flow
        viewModel.allFilms.observe(viewLifecycleOwner){
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                mAdapter.submitData(it)
            }
        }
    }
}