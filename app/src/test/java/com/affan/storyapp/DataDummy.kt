package com.affan.storyapp

import com.affan.storyapp.entity.ListStoryItem

object DataDummy {
    fun generateStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "date + $i",
                "user $i",
                "user $i",
                0.0,
                "id $i",
                0.0,

            )
            items.add(quote)
        }
        return items
    }
}