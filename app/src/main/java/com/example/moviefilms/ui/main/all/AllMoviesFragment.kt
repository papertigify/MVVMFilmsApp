package com.example.moviefilms.ui.main.all

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviefilms.R
import com.example.moviefilms.adapters.MyLoadStateAdapter
import com.example.moviefilms.adapters.PagingAdapter
import com.example.moviefilms.adapters.PagingRvDelegate
import com.example.moviefilms.network.FilmListItem
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AllMoviesFragment: DaggerFragment(R.layout.all_movies_fragment) {

    private val TAG = "AllMoviesFragment"

    private var scrollJob: Job? = null

    private lateinit var viewModel: MainViewModel
    private val mAdapter = PagingAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var upButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var errorText2: TextView
    private lateinit var refreshRetryButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        progressBar = view.findViewById(R.id.loadingProgressBar)
        errorText = view.findViewById(R.id.errorText)
        errorText2 = view.findViewById(R.id.errorText2)
        refreshRetryButton = view.findViewById(R.id.refreshButtonRetry)
        recyclerView = view.findViewById(R.id.rvAllMovies)
        upButton = view.findViewById(R.id.upButtonAll)
        initRecyclerView()
        initUpButton()

        refreshRetryButton.setOnClickListener {
            mAdapter.retry()
        }

        // updating Rv from Api
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.allFilms
                .onEach {
                    mAdapter.submitData(it)
                }
                .collect()
        }
        // Handling situation when adapter is refreshing (progress bar or error text)
        viewLifecycleOwner.lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest { loadState ->
                progressBar.isVisible = loadState.refresh is LoadState.Loading
                errorText.isVisible = loadState.refresh is LoadState.Error
                errorText2.isVisible = loadState.refresh is LoadState.Error
                refreshRetryButton.isVisible = loadState.refresh is LoadState.Error
            }
        }
    }

    private fun initRecyclerView() {
        // Rv item click listener stuff
        mAdapter.attachRvDelegate(object: PagingRvDelegate {
            override fun openDetailedMovie(movie: FilmListItem) {
                val bundle = Bundle()
                bundle.putSerializable("movie", movie)
                findNavController().navigate(
                    R.id.action_allMoviesFragment_to_detailedMovieFragment,
                    bundle
                )
            }
        })

        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            //layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
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