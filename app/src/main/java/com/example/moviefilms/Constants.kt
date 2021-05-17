package com.example.moviefilms

class Constants {
    companion object{
        const val PAGE_SIZE = 40
        const val MAX_SIZE = 1000
        const val posterPath = "https://image.tmdb.org/t/p/w185"
        const val backDropPath = "https://image.tmdb.org/t/p/w780"
        val genres = hashMapOf(
                28 to "Action", 12 to "Adventure", 16 to "Animation", 35 to "Comedy",
                80 to "Crime", 99 to "Documentary", 18 to "Drama", 10751 to "Family",
                14 to "Fantasy", 36 to "History", 27 to "Horror", 10402 to "Music",
                9648 to "Mystery", 10749 to "Romance", 878 to "Science Fiction",
                10770 to "TV Movie", 53 to "Thriller", 10752 to "War",
                37 to "Western"
        )
       /* {"genres":[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"},{"id":16,"name":"Animation"},{"id":35,"name":"Comedy"},{"id":80,"name":"Crime"},{"id":99,"name":"Documentary"},
            {"id":18,"name":"Drama"},{"id":10751,"name":"Family"},{"id":14,"name":"Fantasy"},{"id":36,"name":"History"},{"id":27,"name":"Horror"},{"id":10402,"name":"Music"},{"id":9648,"name":"Mystery"},
            {"id":10749,"name":"Romance"},{"id":878,"name":"Science Fiction"},{"id":10770,"name":"TV Movie"},{"id":53,"name":"Thriller"},{"id":10752,"name":"War"},{"id":37,"name":"Western"}]}*/
    }
}