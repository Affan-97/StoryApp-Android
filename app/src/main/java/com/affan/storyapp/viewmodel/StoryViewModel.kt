package com.affan.storyapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.affan.storyapp.api.ApiConfig
import com.affan.storyapp.data.StoryRepository
import com.affan.storyapp.di.Injection
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.entity.ListStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val storyRepository: StoryRepository):ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>?>()
    val listStory: LiveData<List<ListStoryItem>?> = _listStory
    fun getStory(): LiveData<PagingData<ListStoryItem>> {

        return storyRepository.getStory().cachedIn(viewModelScope)
    }
    fun getStoryLoc(token: String) {
        val header ="Bearer $token"
        Log.d("etail", "$header ")
        val client = ApiConfig.getApiService()
            .getStoriesLocation(1, header)
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("etail", "etail:$responseBody ")
                    if (responseBody != null && !responseBody.error) {

                        _listStory.value = responseBody.listStory

                    }
                } else {
                    Log.d("Error", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                Log.d("Error", "onFailure: ${t.message}")
            }

        })
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
