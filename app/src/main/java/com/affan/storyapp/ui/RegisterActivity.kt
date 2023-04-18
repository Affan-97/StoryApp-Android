package com.affan.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.affan.storyapp.databinding.ActivityRegisterBinding
import com.affan.storyapp.viewmodel.MainViewModel

class RegisterActivity : AppCompatActivity() {
    private var binding: ActivityRegisterBinding? = null
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]

        mainViewModel.loading.observe(this) {
            showLoading(it)
        }
        binding?.apply {
            btnRegis.setOnClickListener {
                val name = binding?.edRegisterName?.text.toString().trim()

                val email = binding?.edRegisterEmail?.text.toString().trim()

                val password = binding?.edRegisterPassword?.text.toString().trim()


                mainViewModel.apply {
                    registerUser(name, email, password)
                    message.observe(this@RegisterActivity) {
                        Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_SHORT).show()
                    }
                    error.observe(this@RegisterActivity) {
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            tvHaveAccount.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
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