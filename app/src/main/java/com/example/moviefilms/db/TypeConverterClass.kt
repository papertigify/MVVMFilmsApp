package com.example.moviefilms.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TypeConverterClass {

    @TypeConverter
    fun fromList(genresList: List<Int>?): String?{
        return if(genresList == null){
            null
        } else {
            val gson = Gson()
            val type = object : TypeToken<List<Int>?>() {}.type
            val json = gson.toJson(genresList, type)
            json
        }
    }

    @TypeConverter
    fun toList(genresString: String?): List<Int>?{
        return if(genresString == null){
            null
        } else {
            val gson = Gson()
            val type = object : TypeToken<List<Int>?>() {}.type
            val listGenres: List<Int> = gson.fromJson(genresString, type)
            listGenres
        }
    }
}