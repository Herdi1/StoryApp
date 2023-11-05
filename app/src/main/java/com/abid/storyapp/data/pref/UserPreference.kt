package com.abid.storyapp.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name="session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveSession(userModel: UserModel){
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = userModel.name
            preferences[TOKEN_KEY] = userModel.token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preference ->
            UserModel(
                preference[NAME_KEY] ?:"",
                preference[TOKEN_KEY] ?: "",
                preference[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logOut(){
        dataStore.edit{ preference ->
            preference.clear()
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference{
            return INSTANCE ?: synchronized(this){
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}