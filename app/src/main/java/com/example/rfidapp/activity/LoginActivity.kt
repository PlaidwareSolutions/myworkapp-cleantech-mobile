package com.example.rfidapp.activity

import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import com.example.rfidapp.databinding.ActivityLoginBinding
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.viewmodel.LoginState
import com.example.rfidapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ActBase<ActivityLoginBinding>() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun setViewBinding() = ActivityLoginBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                when {
                    username.isEmpty() -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please enter your username",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    password.isEmpty() -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please enter your password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        /*"owner", "123456789"*/
                        loginViewModel.login(username, password)
                    }
                }
            }
        }
    }

    override fun bindMethods() {
        observeLoginState()
    }

    private fun observeLoginState() {
        CoroutineScope(Dispatchers.IO).launch {
            loginViewModel.loginState.collectLatest { state ->
                when (state) {
                    is LoginState.Idle -> {}
                    is LoginState.Loading -> {}
                    is LoginState.Success -> {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Login Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, HomeScreenActivity::class.java))
                            finish()
                        }
                    }

                    is LoginState.Error -> {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, state.message , Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
}