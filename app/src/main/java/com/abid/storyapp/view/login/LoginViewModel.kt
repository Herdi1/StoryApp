package com.abid.storyapp.view.login

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.abid.storyapp.data.UserRepository
import com.abid.storyapp.data.pref.UserModel
import com.abid.storyapp.data.response.LoginResponse
import com.abid.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserRepository): ViewModel() {
    private val _userDetail = MutableLiveData<LoginResponse>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var errorMessage = ""

    fun saveSession(user: UserModel){
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }

    fun getUser(token: String, email: String, password: String): LiveData<LoginResponse>{
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).login(email, password)
        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if(response.isSuccessful){
                    _isLoading.value = false
                    _userDetail.value = response.body()
                }else{
                    _isLoading.value = false
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
        return _userDetail
    }
}