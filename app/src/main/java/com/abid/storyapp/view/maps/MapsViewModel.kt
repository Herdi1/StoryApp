package com.abid.storyapp.view.maps

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.abid.storyapp.data.UserRepository
import com.abid.storyapp.data.pref.UserModel
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.data.response.StoryResponse
import com.abid.storyapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val repository: UserRepository): ViewModel() {
    private val _listLocation = MutableLiveData<List<ListStoryItem?>>()
    val listLocation: LiveData<List<ListStoryItem?>> = _listLocation

//     fun getStoryLocation(token: String): Boolean{
//        val client = ApiConfig.getApiService(token).getStoriesWithLocation()
//         client.enqueue(object : Callback<ListStoryItem>{
//             override fun onResponse(call: Call<ListStoryItem>, response: Response<ListStoryItem>) {
//                 if(response.isSuccessful){
//                     val responseBody = response.body()
//                     if (responseBody != null){
//                         val stories = response.body()
//                         _listLocation.value = stories
//                     }
//                 }else{
//                     Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
//                 }
//             }
//
//             override fun onFailure(call: Call<ListStoryItem>, t: Throwable) {
//                 Log.e(ContentValues.TAG, "onFailure: ${t.message}")
//             }
//
//         })
//         return false
//    }

    fun getStoryLocation(): Boolean {
        val client = repository.getStoryLocation()
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                     if (responseBody != null){
                         val stories = response.body()!!.listStory
                         _listLocation.value = stories as List<ListStoryItem>
                     }
                }else{
                     Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                 }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
        return false
    }

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }
}
