package com.example.moviefilms.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.moviefilms.R
import com.example.moviefilms.network.FilmListItem
import java.io.File

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

        private val TAG = "SavedMoviesAdapter"
        private val imgContent: ImageView = itemView.findViewById(R.id.imageSmallPoster)
        //private val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)

        fun bind(movie: FilmListItem){

            val uri = movie.posterStoragePath?.let {
                Uri.fromFile(File(it))
            }
            Glide.with(itemView).load(uri)
                    .apply(RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .error(R.drawable.pic_placeholder)
                    .into(imgContent)
            //movieTitle.text = movie.title

            itemView.setOnClickListener {
                delegate?.openDetailedMovie(movie)
            }
        }
    }
}