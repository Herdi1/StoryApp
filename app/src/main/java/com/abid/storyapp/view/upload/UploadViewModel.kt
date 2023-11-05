package com.abid.storyapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.abid.storyapp.data.UserRepository
import com.abid.storyapp.data.pref.UserModel

class UploadViewModel(private val repository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}