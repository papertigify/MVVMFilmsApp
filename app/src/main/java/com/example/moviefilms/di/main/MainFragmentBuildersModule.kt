package com.example.moviefilms.di.main

import com.example.moviefilms.ui.main.all.AllMoviesFragment
import com.example.moviefilms.ui.main.detailed.DetailedMovieFragment
import com.example.moviefilms.ui.main.saved.SavedMoviesFragment
import com.example.moviefilms.ui.main.search.SearchMoviesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeAllMoviesFragment(): AllMoviesFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchMoviesFragment(): SearchMoviesFragment

    @ContributesAndroidInjector
    abstract fun contributeSavedMoviesFragment(): SavedMoviesFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailedMovieFragment(): DetailedMovieFragment

}