package com.abid.storyapp.data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.abid.storyapp.data.pref.UserModel
import com.abid.storyapp.data.pref.UserPreference
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.data.response.StoryResponse
import com.abid.storyapp.data.retrofit.ApiConfig
import com.abid.storyapp.data.retrofit.ApiService
import com.abid.storyapp.database.StoryDatabase
import com.abid.storyapp.database.StoryPagingSource
import com.abid.storyapp.database.StoryRemoteMediator
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase){

    suspend fun saveSession(user: UserModel){
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel>{
        return userPreference.getSession()
    }

    suspend fun logout(){
        userPreference.logOut()
    }

    fun getStoryLocation(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(ApiConfig.getApiService(token))
            }
        ).liveData
    }

    fun getStoriesPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, ApiConfig.getApiService(token)),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getLocation(): List<ListStoryItem>{
        return apiService.getStoriesWithLocation().listStory
    }

    companion object{
        @Volatile
        private var instance: UserRepository ?= null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase
        ):UserRepository =
            instance ?: synchronized(this){
                instance ?:UserRepository(apiService, userPreference, storyDatabase)
            }.also { instance = it }
    }
}