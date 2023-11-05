package com.abid.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.abid.storyapp.data.pref.UserModel
import com.abid.storyapp.data.pref.UserPreference
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.data.response.StoryResponse
import com.abid.storyapp.data.retrofit.ApiService
import com.abid.storyapp.database.StoryDatabase
import com.abid.storyapp.database.StoryPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Call

class UserRepository private constructor(private val apiService: ApiService, private val userPreference: UserPreference, private val storyDatabase: StoryDatabase){

    suspend fun saveSession(user: UserModel){
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel>{
        return userPreference.getSession()
    }

    suspend fun logout(){
        userPreference.logOut()
    }

    fun getStoryLocation(): Call<StoryResponse> {
        return apiService.getStoriesWithLocation()
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(token)
            }
        ).liveData
    }

    suspend fun insertStory(stories: List<ListStoryItem>){
        storyDatabase.storyDao().insertStory(stories)
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

//     fun getQuote(): List<ListStoryItem> {
//        return apiService.getStories(1, 5)
//    }
}