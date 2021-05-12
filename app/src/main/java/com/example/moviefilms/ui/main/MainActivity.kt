package com.example.moviefilms.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.moviefilms.R
import com.example.moviefilms.extension.setupWithNavController
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.example.moviefilms.ui.viewmodels.ViewModelProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import java.util.concurrent.Executors
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "MainActivity 1")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(
            navGraphIds = listOf(
                R.navigation.nav_graph_all,
                R.navigation.nav_graph_search,
                R.navigation.nav_graph_saved
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.navHostFragment,
            intent = intent
        )

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)
        Log.e(TAG, "MainActivity 2")
    }
}