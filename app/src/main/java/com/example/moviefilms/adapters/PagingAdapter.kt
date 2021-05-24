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
import com.example.moviefilms.Constants

interface PagingRvDelegate {
    fun openDetailedMovie(movie: FilmListItem)
}

class PagingAdapter : PagingDataAdapter<FilmListItem, RecyclerView.ViewHolder>(MOVIE_COMPARATOR) {

    private var delegatePaging: PagingRvDelegate? = null

    fun attachRvDelegate(delegatePaging: PagingRvDelegate){
        this.delegatePaging = delegatePaging
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PagingItemViewHolder(itemView = inflater.inflate(R.layout.cell_image, parent, false), delegatePaging = delegatePaging)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val filmItem = getItem(position)
        if (filmItem != null) {
            (holder as PagingItemViewHolder).bind(filmItem)
        }
    }

    class PagingItemViewHolder(itemView: View, val delegatePaging: PagingRvDelegate?): RecyclerView.ViewHolder(itemView){
        private val imgContent: ImageView = itemView.findViewById(R.id.imageSmallPoster)
        fun bind(movie: FilmListItem){
                Glide.with(itemView).load("${Constants.posterPath}${movie.poster_path}")
                        .apply(RequestOptions()
                                .fitCenter()
                                .format(DecodeFormat.PREFER_ARGB_8888)
                                .override(Target.SIZE_ORIGINAL))
                        .error(R.drawable.pic_placeholder)
                        .into(imgContent)

            itemView.setOnClickListener {
                delegatePaging?.openDetailedMovie(movie)
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