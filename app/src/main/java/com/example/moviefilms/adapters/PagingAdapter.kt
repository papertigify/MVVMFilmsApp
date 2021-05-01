package com.example.moviefilms.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviefilms.R
import com.example.moviefilms.network.FilmListItem
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

interface RvDelegate {
    fun openDetailedMovie(movie: FilmListItem)
}

class PagingAdapter : PagingDataAdapter<FilmListItem, RecyclerView.ViewHolder>(MOVIE_COMPARATOR) {

    private var delegate: RvDelegate? = null

    fun attachRvDelegate(delegate: RvDelegate){
        this.delegate = delegate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PagingItemViewHolder(itemView = inflater.inflate(R.layout.cell_image, parent, false), delegate = delegate)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val filmItem = getItem(position)
        if (filmItem != null) {
            (holder as PagingItemViewHolder).bind(filmItem)
        }
    }

    class PagingItemViewHolder(itemView: View, val delegate: RvDelegate?): RecyclerView.ViewHolder(itemView){
        private val imgContent: ImageView = itemView.findViewById(R.id.imageSmallPoster)
        private val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        private val smallPoster = "w185"
        private val bigPoster = "w780"
        private val posterBasePath = "https://image.tmdb.org/t/p/"
        fun bind(movie: FilmListItem){
            Glide.with(itemView).load("$posterBasePath$smallPoster${movie.poster_path}")
                    .apply(RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .into(imgContent)
            movieTitle.text = movie.title

            itemView.setOnClickListener {
                delegate?.openDetailedMovie(movie)
            }
        }
    }


    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<FilmListItem>() {
            override fun areItemsTheSame(oldItem: FilmListItem, newItem: FilmListItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FilmListItem, newItem: FilmListItem): Boolean =
                oldItem == newItem
        }
    }

}