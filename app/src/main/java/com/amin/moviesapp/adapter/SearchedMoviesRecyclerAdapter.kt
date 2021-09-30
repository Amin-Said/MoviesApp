package com.amin.moviesapp.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.amin.moviesapp.R
import com.amin.moviesapp.databinding.MovieItemBinding
import com.amin.moviesapp.model.Result
import com.amin.moviesapp.utils.Config
import com.amin.moviesapp.utils.extensions.setImageWithPicasso

class SearchedMoviesRecyclerAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Result>() {

        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchedMoviesViewHolder(
            MovieItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchedMoviesViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Result>) {
        differ.submitList(list)
    }

    class SearchedMoviesViewHolder
    constructor(
        private val binding: MovieItemBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Result) = with(binding.root) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            try {
                binding.posterImage.setImageWithPicasso(Config.POSTER_PATH.trim() + item.poster_path.trim())
            }catch (e:Exception){
                e.printStackTrace()
            }
            binding.movieNameTV.text = item.title
            binding.movieLangTV.text = binding.root.context.getString(R.string.original_lang) +
                    item.original_language


        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Result)
    }
}

