package com.affan.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.affan.storyapp.databinding.ItemRowBinding
import com.affan.storyapp.entity.ListStoryItem
import com.bumptech.glide.Glide

class RecyclerAdapter(
    private val listStory:List<ListStoryItem>,
    private val clickListener:(ListStoryItem)->Unit
) :RecyclerView.Adapter<RecyclerAdapter.ListViewHolder>(){
    class ListViewHolder(private val binding: ItemRowBinding, private val clickListener:(ListStoryItem)->Unit):RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item:ListStoryItem){
            binding.tvItemName.text = item.name
            Glide.with(itemView).load(item.photoUrl).into(binding.ivItemPhoto)
            binding.cardView.setOnClickListener {
                clickListener(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowBinding.inflate(inflater, parent, false)
        return ListViewHolder(binding, clickListener)
    }

    override fun getItemCount(): Int =listStory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
       val list = listStory[position]
        holder.bind(list)
    }


}