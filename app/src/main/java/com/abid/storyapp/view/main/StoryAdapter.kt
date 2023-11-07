package com.abid.storyapp.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.databinding.ItemStoryCardBinding
import com.abid.storyapp.view.detail.DetailActivity
import com.bumptech.glide.Glide

class StoryAdapter(private val itemClicked: (ListStoryItem) -> Unit): PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {

    inner class MyViewHolder(private val binding: ItemStoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val item = getItem(position)
                    if (item != null) {
                        itemClicked(item)
                    }
                }
            }
        }

        fun bind(stories: ListStoryItem){
            binding.tvItemName.text = stories.name
            binding.tvItemDescription.text = stories.description
            Glide.with(binding.root.context)
                .load(stories.photoUrl)
                .into(binding.ivItemPhoto)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("id", stories.id)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "imageDetail"),
                        Pair(binding.tvItemName, "usernameDetail"),
                        Pair(binding.tvItemDescription, "descriptionDetail")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        val binding = ItemStoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        val storyItem = getItem(position)
        if (storyItem != null) {
            holder.bind(storyItem)
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}