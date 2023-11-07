package com.abid.storyapp.view.main

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
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<PagingData<ListStoryItem>>()
    val story: LiveData<PagingData<ListStoryItem>> = _listStory

    fun stories(token: String): LiveData<PagingData<ListStoryItem>> {
        return repository.getStoriesPaging(token).cachedIn(viewModelScope)
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