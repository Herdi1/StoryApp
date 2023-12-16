package com.abid.storyapp.data.di

import android.content.Context
import com.abid.storyapp.data.UserRepository
import com.abid.storyapp.data.pref.UserPreference
import com.abid.storyapp.data.pref.dataStore
import com.abid.storyapp.data.retrofit.ApiConfig
import com.abid.storyapp.database.StoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref, database)
    }
}