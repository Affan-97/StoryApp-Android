package com.affan.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.affan.storyapp.databinding.ItemRowBinding
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.ui.DetailActivity
import com.bumptech.glide.Glide

class RecyclerAdapter :
    PagingDataAdapter<ListStoryItem, RecyclerAdapter.ListViewHolder>(DIFF_CALLBACK) {
    class ListViewHolder(private val binding: ItemRowBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item: ListStoryItem) {
            binding.tvItemName.text = item.name
            Glide.with(itemView).load(item.photoUrl).into(binding.ivItemPhoto)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)

                intent.putExtra(DetailActivity.ID_STORY, item.id)
                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,

                        Pair(binding.ivItemPhoto, "image"),
                        Pair(binding.tvItemName, "name"),

                        )
                itemView.context.startActivity(intent, options.toBundle())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowBinding.inflate(inflater, parent, false)
        return ListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = getItem(position)

        if (list != null) {
        Log.d("TAG", "onBindViewHolder: $list")
            holder.bind(list)
        }else{
        Log.d("TAG", "onBindViewHolder: NULL")

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}