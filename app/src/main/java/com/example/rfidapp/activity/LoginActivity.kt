package com.example.rfidapp.activity

import android.widget.Toast
import com.example.rfidapp.databinding.ActivityLoginBinding
import com.example.rfidapp.util.ActBase

class LoginActivity : ActBase<ActivityLoginBinding>() {

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
                        // logic of api::
                        // Both fields are filled
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun bindMethods() {

    }
}