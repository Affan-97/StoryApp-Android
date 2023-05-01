package com.affan.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.affan.storyapp.databinding.ActivityLoginBinding
import com.affan.storyapp.preferences.LoginPreference
import com.affan.storyapp.viewmodel.LoginFactory
import com.affan.storyapp.viewmodel.LoginViewModel
import com.affan.storyapp.viewmodel.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class LoginActivity : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]
        val pref = LoginPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, LoginFactory(pref)).get(
            LoginViewModel::class.java
        )

        mainViewModel.loading.observe(this) {
            showLoading(it)
        }

        binding?.apply {
            btnLogin.setOnClickListener {


                val email = binding?.edLoginEmail?.text.toString().trim()

                val password = binding?.edLoginPassword?.text.toString().trim()


                mainViewModel.apply {
                    loginUser(email, password)
                    message.observe(this@LoginActivity) {
                        Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                    }
                    error.observe(this@LoginActivity) {
                        if (!it) {
                            token.observe(this@LoginActivity) { token ->

                                loginViewModel.saveSession(token)
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                    }

                }
            }
            textRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
        supportActionBar?.hide()

    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            dimmedBackground.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}