package com.example.moviefilms.ui.main.search

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchMoviesFragment: DaggerFragment(R.layout.search_movies_fragment) {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var upButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var refreshRetryButton: Button
    private lateinit var nothingFoundTextView: TextView

    private lateinit var viewModel: MainViewModel

    private var searchJob: Job? = null
    private var scrollJob: Job? = null
    private val mAdapter = PagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        progressBar = view.findViewById(R.id.searchLoadingProgressBar)
        errorText = view.findViewById(R.id.searchErrorText)
        refreshRetryButton = view.findViewById(R.id.searchRefreshButtonRetry)
        nothingFoundTextView = view.findViewById(R.id.nothingFoundTextView)

        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.rvSearchMovies)
        upButton = view.findViewById(R.id.upButtonSearch)
        initRecyclerView()
        initUpButton()

        refreshRetryButton.setOnClickListener {
            mAdapter.retry()
        }

        etSearch.addTextChangedListener { editable ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500L)
                editable?.let { query ->
                    if(query.toString().isNotEmpty() && query.toString() != viewModel.currentQuery) {
                        // refresh Rv when new query executes
                        launch {
                            recyclerView.scrollToPosition(0)
                            mAdapter.submitData(PagingData.empty())
                        }
                        viewModel.currentQuery = query.toString()
                        // query
                        viewModel.getSearchMoviesFlow(query.toString()).collectLatest { pagingData ->
                            mAdapter.submitData(pagingData)
                        }
                    }
                    nothingFoundTextView.isVisible = query.toString().isNotEmpty() && query.toString() != viewModel.currentQuery
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest { loadState ->
                progressBar.isVisible = loadState.refresh is LoadState.Loading
                errorText.isVisible = loadState.refresh is LoadState.Error
                refreshRetryButton.isVisible = loadState.refresh is LoadState.Error
                nothingFoundTextView.isVisible = loadState.refresh is LoadState.NotLoading && (mAdapter.itemCount == 0) && etSearch.text.toString().isNotEmpty()
            }
        }
    }

    private fun initRecyclerView(){
        // Rv item click listener stuff
        mAdapter.attachRvDelegate(object: PagingRvDelegate {
            override fun openDetailedMovie(movie: FilmListItem) {
                val bundle = Bundle()
                bundle.putSerializable("movie", movie)
                findNavController().navigate(
                    R.id.action_searchMoviesFragment_to_detailedMovieFragment3,
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
                    } else if(dy < 0){
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