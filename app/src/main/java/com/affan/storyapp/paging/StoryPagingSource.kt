package com.affan.storyapp.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.affan.storyapp.api.ApiService
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.preferences.LoginPreference
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val apiService: ApiService, private val pref: LoginPreference) :
    PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPos ->
            val anchorPage = state.closestPageToPosition(anchorPos)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_INDEX
            val token = "Bearer ${pref.getLoginSession().first()}"

            val responseData = apiService.getAllStories(page, params.loadSize, 0, token)


            if (responseData.listStory.isNullOrEmpty()) {

                LoadResult.Error(NullPointerException("Response is empty or null"))
            } else {


                LoadResult.Page(
                    data = responseData.listStory,
                    prevKey = if (page == INITIAL_INDEX) null else page - 1,
                    nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1
                )
            }
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }

    }

    private companion object {
        const val INITIAL_INDEX = 1
    }
}