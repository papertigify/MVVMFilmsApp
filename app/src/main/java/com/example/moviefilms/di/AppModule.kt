package com.example.moviefilms.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviefilms.db.MoviesDao
import com.example.moviefilms.db.MoviesDatabase
import com.example.moviefilms.utils.MyFileManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    companion object {

        @Singleton
        @Provides
        fun provideRetrofit(): Retrofit {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Singleton
        @Provides
        fun provideRoomDatabase(context: Application): MoviesDatabase{
            return Room.databaseBuilder(
                context.applicationContext,
                MoviesDatabase::class.java,
                "movieDb"
            ).build()
        }

        @Singleton
        @Provides
        fun provideMoviesDao(db: MoviesDatabase): MoviesDao{
            return db.getMoviesDao()
        }

        @Singleton
        @Provides
        fun provideFileManager(): MyFileManager{
            return MyFileManager()
        }
    }
}