package com.example.moviefilms.ui.main.search

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviefilms.R
import com.example.moviefilms.adapters.MyLoadStateAdapter
import com.example.moviefilms.adapters.PagingAdapter
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchMoviesFragment: DaggerFragment(R.layout.search_movies_fragment) {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: MainViewModel
    private var searchJob: Job? = null
    private val mAdapter = PagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.rvSearchMovies)

        recyclerView.apply {
            //layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter.withLoadStateHeaderAndFooter(
                    header = MyLoadStateAdapter { mAdapter.retry() },
                    footer = MyLoadStateAdapter { mAdapter.retry() }
            )
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
    }
}