package com.example.moviefilms.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.moviefilms.R
import com.example.moviefilms.network.FilmListItem

interface SavedMoviesRvDelegate {
    fun openDetailedMovie(movie: FilmListItem)
}

class SavedMoviesAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // delegate
    private var delegate: SavedMoviesRvDelegate? = null

    fun attachRvDelegate(delegate: SavedMoviesRvDelegate){
        this.delegate = delegate
    }

    // differ
    private val DIFFER_CALLBACK = object : DiffUtil.ItemCallback<FilmListItem>(){
        override fun areItemsTheSame(oldItem: FilmListItem, newItem: FilmListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FilmListItem, newItem: FilmListItem): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, DIFFER_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SavedMoviesViewHolder(
                itemView = inflater.inflate(R.layout.cell_image, parent, false),
                delegate = delegate
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = differ.currentList[position]
        (holder as SavedMoviesViewHolder).bind(movie)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class SavedMoviesViewHolder(itemView: View, private val delegate: SavedMoviesRvDelegate?): RecyclerView.ViewHolder(itemView){

        private val imgContent: ImageView = itemView.findViewById(R.id.imageSmallPoster)
        //private val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        private val smallPoster = "w185"
        private val posterBasePath = "https://image.tmdb.org/t/p/"
        fun bind(movie: FilmListItem){
            Glide.with(itemView).load("$posterBasePath$smallPoster${movie.poster_path}")
                    .apply(RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .into(imgContent)
            //movieTitle.text = movie.title

            itemView.setOnClickListener {
                delegate?.openDetailedMovie(movie)
            }
        }
    }
}