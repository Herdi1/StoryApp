package com.abid.storyapp.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.abid.storyapp.data.UserRepository
import com.abid.storyapp.data.pref.UserModel
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.data.response.UploadResponse
import com.google.gson.Gson
import retrofit2.HttpException

class MapsViewModel(private val repository: UserRepository): ViewModel() {
    private val _listLocation = MutableLiveData<List<ListStoryItem?>>()
    val listLocation: LiveData<List<ListStoryItem?>> = _listLocation

    suspend fun getAllLocation(): Boolean{
        try {
            val successResponse = repository.getLocation()
            _listLocation.value = successResponse
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            Log.d("mapsViewModel", errorResponse.toString())
        }
        return false
    }

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }
}
