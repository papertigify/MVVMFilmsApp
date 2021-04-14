package com.example.moviefilms.di

import com.example.moviefilms.ui.main.MainActivity
import com.example.moviefilms.di.main.MainFragmentBuildersModule
import com.example.moviefilms.di.main.MainModule
import com.example.moviefilms.di.main.MainScope
import com.example.moviefilms.di.main.MainViewModelsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(
        modules = [
            MainModule::class,
            MainFragmentBuildersModule::class,
            MainViewModelsModule::class
        ]
    )
    abstract fun contributeMainActivity(): MainActivity

}