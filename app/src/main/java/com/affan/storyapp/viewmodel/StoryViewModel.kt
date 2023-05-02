package com.affan.storyapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.affan.storyapp.data.StoryRepository
import com.affan.storyapp.di.Injection
import com.affan.storyapp.entity.ListStoryItem

class StoryViewModel(private val storyRepository: StoryRepository):ViewModel() {
    fun getStory():LiveData<PagingData<ListStoryItem>> {

        var list = storyRepository.getStory().cachedIn(viewModelScope)
        Log.d("TAG", "getStory: ${list.value}")
        return list
    }
}
class ViewModelFactory(private val repo: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(repo) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}
