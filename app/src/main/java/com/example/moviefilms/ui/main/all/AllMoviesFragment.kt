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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllMoviesFragment: DaggerFragment(R.layout.all_movies_fragment) {

    private val TAG = "AllMoviesFragment"

    private var searchJob: Job? = null
    private var scrollJob: Job? = null

    private lateinit var viewModel: MainViewModel
    private val mAdapter = PagingAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var upButton: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "AllMoviesFragment 1")
        progressBar = view.findViewById(R.id.loadingProgressBar)
        recyclerView = view.findViewById(R.id.rvAllMovies)
        upButton = view.findViewById(R.id.up_button)
        initRecyclerView()
        initUpButton()

        viewModel = (activity as MainActivity).viewModel

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.allFilms
                .onEach {
                    mAdapter.submitData(it)
                }
                .collect()
        }
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            //layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter.withLoadStateHeaderAndFooter(
                header = MyLoadStateAdapter { mAdapter.retry() },
                footer = MyLoadStateAdapter { mAdapter.retry() }
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(dy > 0){
                        upButton.visibility = View.GONE
                    }
                    else if(dy < 0){
                        upButton.visibility = View.VISIBLE
                    }
                }
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(newState ==  RecyclerView.SCROLL_STATE_IDLE){
                        scrollJob?.cancel()
                        scrollJob = viewLifecycleOwner.lifecycleScope.launch {
                            delay(1700L)
                            upButton.visibility = View.GONE
                        }
                    }
                }
            })
        }
    }

    private fun initUpButton(){
        upButton.apply {
            visibility = View.GONE
            setOnClickListener {
                recyclerView.scrollToPosition(0)
                visibility = View.GONE
            }
        }
    }
}