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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviefilms.R
import com.example.moviefilms.adapters.MyLoadStateAdapter
import com.example.moviefilms.adapters.PagingAdapter
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchMoviesFragment: DaggerFragment(R.layout.search_movies_fragment) {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var upButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var refreshRetryButton: Button
    private lateinit var viewModel: MainViewModel

    private var searchJob: Job? = null
    private var scrollJob: Job? = null
    private val mAdapter = PagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.searchLoadingProgressBar)
        errorText = view.findViewById(R.id.searchErrorText)
        refreshRetryButton = view.findViewById(R.id.searchRefreshButtonRetry)

        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.rvSearchMovies)
        upButton = view.findViewById(R.id.search_up_button)
        initRecyclerView()
        initUpButton()

        refreshRetryButton.setOnClickListener {
            mAdapter.retry()
        }

        viewModel = (activity as MainActivity).viewModel

        etSearch.addTextChangedListener { editable ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500L)
                editable?.let {
                    if(it.toString().isNotEmpty()) {
                        viewModel.getSearchMoviesFlow(it.toString()).collectLatest { pagingData ->
                            mAdapter.submitData(pagingData)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest { loadState ->
                progressBar.isVisible = loadState.refresh is LoadState.Loading
                errorText.isVisible = loadState.refresh is LoadState.Error
                refreshRetryButton.isVisible = loadState.refresh is LoadState.Error
            }
        }
    }

    private fun initRecyclerView(){
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