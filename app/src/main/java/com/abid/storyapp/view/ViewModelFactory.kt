package com.abid.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abid.storyapp.data.UserRepository
import com.abid.storyapp.data.di.Injection
import com.abid.storyapp.view.detail.DetailViewModel
import com.abid.storyapp.view.login.LoginViewModel
import com.abid.storyapp.view.main.MainViewModel
import com.abid.storyapp.view.maps.MapsViewModel
import com.abid.storyapp.view.upload.UploadViewModel

class ViewModelFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class : " + modelClass.name)
        }
    }

//    companion object{
//        @Volatile
//        private var INSTANCE: ViewModelFactory? = null
//
//        @JvmStatic
//        fun getInstance(context: Context): ViewModelFactory{
//            if(INSTANCE == null){
//                synchronized(ViewModelFactory::class.java){
//                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
//                }
//            }
//            return INSTANCE as ViewModelFactory
//        }
//    }
}