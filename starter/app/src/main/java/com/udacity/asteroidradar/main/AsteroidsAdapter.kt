package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ItemAsteroidBinding

class AsteroidsAdapter(private val clickListenerOn: OnAsteroidClickListener) :
    ListAdapter<Asteroid, AsteroidsAdapter.ViewHolder>(AsteroidDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListenerOn)
    }

    class ViewHolder private constructor(private val binding: ItemAsteroidBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAsteroidBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(asteroid: Asteroid, onClickListener: OnAsteroidClickListener) {
            binding.asteroid = asteroid
            binding.onClickListener = onClickListener
            binding.executePendingBindings()
        }
    }
}

/** Callback for calculating the diff between two non-null items in a list. */
class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }
}

class OnAsteroidClickListener(val clickListener: (artist: Asteroid) -> Unit) {
    fun onClick(clickedAsteroid: Asteroid) = clickListener(clickedAsteroid)
}