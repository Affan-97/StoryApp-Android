package com.affan.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.affan.storyapp.databinding.ItemRowBinding
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.ui.DetailActivity
import com.bumptech.glide.Glide

class RecyclerAdapter(
    private val listStory: List<ListStoryItem>
) : RecyclerView.Adapter<RecyclerAdapter.ListViewHolder>() {
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

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = listStory[position]
        holder.bind(list)
    }


}