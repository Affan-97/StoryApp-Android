package com.affan.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.affan.storyapp.api.ApiService
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.paging.StoryPagingSource
import com.affan.storyapp.preferences.LoginPreference

class StoryRepository(private val pref: LoginPreference, private val apiService: ApiService) {

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, pref)
            }
        ).liveData
    }


}