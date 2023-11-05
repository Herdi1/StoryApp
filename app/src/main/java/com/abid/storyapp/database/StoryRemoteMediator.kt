package com.abid.storyapp.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.data.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, ListStoryItem>(){

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = INITIAL_PAGE_INDEX

        try {
            val responseData = apiService.getStories(page, state.config.pageSize)

            val endOfPagingReached = responseData.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.storyDao().deleteAll()
                }
                database.storyDao().insertStory(responseData)
            }
            return MediatorResult.Success(endOfPagingReached)
        }catch (exception: Exception){
            return MediatorResult.Error(exception)
        }
    }

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }
}