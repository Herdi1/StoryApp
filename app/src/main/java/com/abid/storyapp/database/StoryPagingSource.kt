package com.abid.storyapp.database

import android.content.ContentValues.TAG
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.data.response.StoryResponse
import com.abid.storyapp.data.retrofit.ApiConfig
import com.abid.storyapp.data.retrofit.ApiService

class StoryPagingSource(private val token: String): PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let{ anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = ApiConfig.getApiService(token).getStories(position, params.loadSize)

            LoadResult.Page(
                data = responseData as List<ListStoryItem>,
                prevKey = if(position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if(responseData.isNullOrEmpty()) null else position + 1
            )
        }catch (exception: Exception){
            return LoadResult.Error(exception)
        }
    }

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }
}