package com.affan.storyapp.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.affan.storyapp.api.ApiConfig
import com.affan.storyapp.entity.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _listStory = MutableLiveData<List<ListStoryItem>?>()
    val listStory: LiveData<List<ListStoryItem>?> = _listStory

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story


    fun registerUser(name: String, email: String, password: String) {
        _loading.value = true
        val client = ApiConfig().getApiService().registerUser(name, email, password)
        client.enqueue(object : Callback<com.affan.storyapp.entity.DataResponse> {
            override fun onResponse(
                call: Call<com.affan.storyapp.entity.DataResponse>,
                dataResponse: Response<com.affan.storyapp.entity.DataResponse>
            ) {
                if (dataResponse.isSuccessful) {
                    val responseBody = dataResponse.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            _loading.value = false
                            _message.value = responseBody.message
                            _error.value = false
                            Log.d("ApiService", responseBody.message)
                            Log.d("ApiService", responseBody.toString())


                        }
                    }
                } else {
                    val errorBody = dataResponse.errorBody()?.string()
                    val errorMessage = JSONObject(errorBody).getString("message")
                    _loading.value = false
                    _message.value = errorMessage
                    _error.value = true
                    Log.d("ApiService", errorMessage)
                }
            }

            override fun onFailure(
                call: Call<com.affan.storyapp.entity.DataResponse>,
                t: Throwable
            ) {
                _loading.value = false
                _error.value = true
                _message.value = t.message
                t.message?.let { Log.d("ApiService", it) }
            }

        })
    }

    fun loginUser(email: String, password: String) {
        _loading.value = true
        val client = ApiConfig().getApiService().loginUser(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            _loading.value = false
                            _message.value = responseBody.message
                            _error.value = false
                            _token.value = responseBody.loginResult.token

                            Log.d("ApiService", responseBody.message)

                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = JSONObject(errorBody).getString("message")
                    _loading.value = false
                    _message.value = errorMessage
                    _error.value = true
                    Log.d("ApiService", errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loading.value = false
                _error.value = true
                _message.value = t.message
                t.message?.let { Log.d("ApiService", it) }
            }

        })
    }

    fun getAllStories(header: String) {
        _loading.value = true
        val client = ApiConfig().getApiService().getAllStories(null, null, 0, "Bearer $header")
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>, response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful) {
                    _loading.value = false
                    _error.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("ListStori", "onResponse:$responseBody ")
                        _listStory.value = responseBody.listStory
                    }
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                _loading.value = false
                _error.value = true

            }
        })
    }

    fun getDetailStory(id: String, header: String) {
        _loading.value = true
        val client = ApiConfig().getApiService().getDetailStory(id, "Bearer $header")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>, response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    _loading.value = false
                    _error.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {

                        _story.value = responseBody.story
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _loading.value = false
                _error.value = true

            }
        })
    }

    fun uploadStory(description: RequestBody, photo: MultipartBody.Part, header: String) {
        _loading.value = true
        val client = ApiConfig().getApiService()
            .uploadStory("Bearer $header", photo, description, null, null)
        client.enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("etail", "etail:$responseBody ")
                    if (responseBody != null && !responseBody.error) {

                        _message.value = responseBody.message
                        _loading.value = false
                        _error.value = false
                    }
                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                _loading.value = false
                _message.value = t.message
                _error.value = true
            }

        })

    }
}

