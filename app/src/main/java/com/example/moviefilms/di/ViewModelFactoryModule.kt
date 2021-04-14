package com.example.moviefilms.di

import androidx.lifecycle.ViewModelProvider
import com.example.moviefilms.ui.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

// dependency for ViewModelFactory
@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}