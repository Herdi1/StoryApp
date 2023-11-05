package com.abid.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.abid.storyapp.data.pref.UserModel
import com.abid.storyapp.data.response.LoginResponse
import com.abid.storyapp.databinding.ActivityLoginBinding
import com.abid.storyapp.view.ViewModelFactory
import com.abid.storyapp.view.main.MainActivity
import com.abid.storyapp.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = obtainViewModel(this@LoginActivity)

        setMyButtonEnable()
        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
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

        var token = ""
        loginViewModel.getSession().observe(this){ user ->
            token = user.token
        }

        binding.toRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginViewModel.isLoading.observe(this){ isLoading ->
            showLoading(isLoading)
        }

        binding.btnLogin.setOnClickListener {
            loginViewModel.getUser(
                token, binding.edLoginEmail.text.toString(),
                binding.edLoginPassword.text.toString()
            ).observe(this){ user ->
                saveSession(user)
            }
        }

    }

    private fun saveSession(userData: LoginResponse){
        if(userData.error == false){
            val userSession = UserModel(userData.loginResult?.name.toString(), userData.loginResult?.token.toString(), true)

            loginViewModel.saveSession(userSession)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            showToast(loginViewModel.errorMessage)
        }
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory(applicationContext)
        return ViewModelProvider(activity, factory).get(LoginViewModel::class.java)
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleLogin = ObjectAnimator.ofFloat(binding.loginTitle, View.ALPHA, 1f).setDuration(500)
        val emailTitle = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEd = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passTitle = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passEd = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val hintReg = ObjectAnimator.ofFloat(binding.registerHint, View.ALPHA, 1f).setDuration(500)
        val regBtn = ObjectAnimator.ofFloat(binding.toRegister, View.ALPHA, 1f).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply{
            play(emailTitle).after(titleLogin)
            play(emailTitle).with(emailEd)
            play(passTitle).after(emailTitle)
            play(passTitle).with(passEd)
            play(hintReg).after(passTitle)
            play(hintReg).with(regBtn)
            play(loginBtn).after(hintReg)
        }.start()
    }

    private fun setMyButtonEnable(){
        val email = binding.edLoginEmail.text
        val password = binding.edLoginPassword.text

        binding.btnLogin.isEnabled = email!=null  && email.toString().isNotEmpty() && password!=null  && password.toString().isNotEmpty()
    }

}