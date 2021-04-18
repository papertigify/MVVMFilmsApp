package com.example.moviefilms.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviefilms.R

class MyLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<MyLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LoadStateViewHolder(itemView = inflater.inflate(R.layout.load_state_item, parent, false), retry = retry)
    }

    class LoadStateViewHolder(itemView: View, retry: () -> Unit): RecyclerView.ViewHolder(itemView){
        private val textError: TextView = itemView.findViewById(R.id.error_msg)
        private val retryButton: Button = itemView.findViewById(R.id.retry_button)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.loadingProgressBar1)
        init {
            retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                textError.text = loadState.error.localizedMessage
            }
            retryButton.isVisible = loadState !is LoadState.Loading
            textError.isVisible = loadState !is LoadState.Loading
            progressBar.isVisible = loadState is LoadState.Loading

        }
    }
}