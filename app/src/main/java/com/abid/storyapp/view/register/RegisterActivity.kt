package com.abid.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.abid.storyapp.data.response.RegisterResponse
import com.abid.storyapp.data.retrofit.ApiConfig
import com.abid.storyapp.databinding.ActivityRegisterBinding
import com.abid.storyapp.view.login.LoginActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        setMyButtonEnable()
        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        supportActionBar?.hide()
        playAnimation()

        binding.btnRegister.setOnClickListener {
            uploadData()
        }
    }

    private fun uploadData(){
        showLoading(true)
        val token = ""
        val username = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        lifecycleScope.launch {
            try{
                val apiService = ApiConfig.getApiService(token)
                val successResponse = apiService.register(username, email, password)
                successResponse.message?.let { showToast(it) }
                showLoading(false)
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }catch (e: HttpException){
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                errorResponse.message?.let { showToast(it) }
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val registerTitle = ObjectAnimator.ofFloat(binding.registerTitle, View.ALPHA, 1f).setDuration(500)
        val nameTitle = ObjectAnimator.ofFloat(binding.usernameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEd = ObjectAnimator.ofFloat(binding.usernameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTitle = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEd = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passTitle = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passEd = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val regBtn = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply{
            play(nameTitle).after(registerTitle)
            play(nameTitle).with(nameEd)
            play(emailTitle).after(nameTitle)
            play(emailTitle).with(emailEd)
            play(passTitle).after(emailTitle)
            play(passTitle).with(passEd)
            play(regBtn).after(passTitle)
        }.start()
    }

    private fun setMyButtonEnable(){
        val name = binding.edRegisterName.text
        val email = binding.edRegisterEmail.text
        val password = binding.edRegisterPassword.text

        binding.btnRegister.isEnabled = name!=null  && name.toString().isNotEmpty() && email!=null  && email.toString().isNotEmpty() && password!=null  && password.toString().isNotEmpty()
    }
}
