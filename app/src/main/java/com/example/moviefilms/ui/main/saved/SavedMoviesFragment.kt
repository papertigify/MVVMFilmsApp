package com.example.moviefilms.ui.main.saved

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.moviefilms.R
import com.example.moviefilms.adapters.SavedMoviesAdapter
import com.example.moviefilms.adapters.SavedMoviesRvDelegate
import com.example.moviefilms.network.FilmListItem
import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.example.moviefilms.utils.MyFileManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class SavedMoviesFragment: DaggerFragment(R.layout.saved_movies_fragment) {

    private val mAdapter = SavedMoviesAdapter()
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var upButton: FloatingActionButton
    private lateinit var nothingFoundTextView: TextView

    private var scrollJob: Job? = null
    @Inject
    lateinit var fileManager: MyFileManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        recyclerView = view.findViewById(R.id.recyclerSaved)
        upButton = view.findViewById(R.id.upButtonSaved)
        nothingFoundTextView = view.findViewById(R.id.nothingFoundTextView2)
        initRecyclerView()
        initUpButton()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllSavedMovies().collectLatest{
                nothingFoundTextView.isVisible = it.isEmpty()
                mAdapter.differ.submitList(it)
            }
        }
    }

    private fun initRecyclerView(){
        // Rv item click listener stuff
        mAdapter.attachRvDelegate(object: SavedMoviesRvDelegate {
            override fun openDetailedMovie(movie: FilmListItem) {
                val bundle = Bundle()
                bundle.putSerializable("movie", movie)
                findNavController().navigate(
                        R.id.action_savedMoviesFragment_to_detailedMovieFragment2,
                        bundle
                )
            }
        })

        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            //layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
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
            createItemTouchHelper().attachToRecyclerView(this)
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

    private fun createItemTouchHelper(): ItemTouchHelper{
        return ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val currentMovie = mAdapter.differ.currentList[position]
                var isDeleted = false
                if (currentMovie != null) {
                    // delete current movie from db
                    viewModel.deleteMovie(currentMovie)
                    isDeleted = true
                }
                view?.let {
                    Snackbar.make(it, "Successfully deleted this movie", Snackbar.LENGTH_LONG)
                            .setAction("Undo"){
                                viewModel.insertMovie(currentMovie)
                                isDeleted = false
                            }
                            .show()
                }
                // delete image from storage
                if(isDeleted) {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        currentMovie.posterStoragePath?.let { fileManager.deleteImageFromInternalStorage(requireContext().applicationContext, it) }
                        currentMovie.backdropStoragePath?.let {fileManager.deleteImageFromInternalStorage(requireContext().applicationContext, it)}
                    }
                }
            }
        })
    }
}