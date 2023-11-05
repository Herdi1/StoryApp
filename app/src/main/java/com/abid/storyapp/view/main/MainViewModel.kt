package com.abid.storyapp.view.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abid.storyapp.data.UserRepository
import com.abid.storyapp.data.pref.UserModel
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.data.response.StoryResponse
import com.abid.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: UserRepository): ViewModel() {
    private val _listItem = MutableLiveData<List<ListStoryItem>>()
    val listItem: LiveData<List<ListStoryItem>> = _listItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

//    fun getStory(token: String): Boolean{
//        _isLoading.value =true
//        val client = ApiConfig.getApiService(token).getStories()
//        client.enqueue(object: Callback<StoryResponse> {
//            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
//                _isLoading.value = false
//                if (response.isSuccessful){
//                    val responseBody = response.body()
//                    if(responseBody != null){
//                        val stories = response.body()!!.listStory
//                        _listItem.value = (stories ?: ArrayList()) as List<ListStoryItem>?
//                    }
//                }else{
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message}")
//            }
//
//        })
//        return false
//    }
//fun getQuote() {
//    viewModelScope.launch {
//        _listItem.postValue(repository.getQuote())
//    }
//}
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>>{
        return repository.getStories(token).cachedIn(viewModelScope)
    }

//    val stories: LiveData<PagingData<ListStoryItem>> =
//        repository.getStories().cachedIn(viewModelScope)

    suspend fun insertStory(stories: List<ListStoryItem>){
        repository.insertStory(stories)
    }

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }


}