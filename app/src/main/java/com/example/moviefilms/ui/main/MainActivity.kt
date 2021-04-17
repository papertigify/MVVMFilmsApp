package com.example.moviefilms.ui.main

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moviefilms.R
import com.example.moviefilms.ui.viewmodels.MainViewModel
import com.example.moviefilms.ui.viewmodels.ViewModelProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "MainActivity 1")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)
        Log.e(TAG, "MainActivity 2")

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    }
}