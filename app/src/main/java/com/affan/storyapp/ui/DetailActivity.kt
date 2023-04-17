package com.affan.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.affan.storyapp.R
import com.affan.storyapp.databinding.ActivityDetailBinding
import com.affan.storyapp.databinding.ActivityMainBinding
import com.affan.storyapp.preferences.LoginPreference
import com.affan.storyapp.viewmodel.LoginFactory
import com.affan.storyapp.viewmodel.LoginViewModel
import com.affan.storyapp.viewmodel.MainViewModel
import com.bumptech.glide.Glide

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class DetailActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mainViewModel: MainViewModel
    private var binding: ActivityDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val id = intent.getStringExtra(ID_STORY).toString()
        setContentView(binding?.root)




        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]
        val pref = LoginPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, LoginFactory(pref)).get(
            LoginViewModel::class.java
        )

        loginViewModel.getLoginSession().observe(this) { savedToken ->

            if (savedToken != null) {
                mainViewModel.getDetailStory(id,savedToken)
            }else{

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        mainViewModel.story.observe(this){
            binding?.apply {
                tvDetailDescription.text= it.description
                tvDetailName.text =  it.name
                tvDetailDate.text = it.createdAt
                ivDetailPhoto.let { imageView ->
                    Glide.with(this@DetailActivity).load(it.photoUrl).into(
                        imageView
                    )
            }

            }
        }

    }

    companion object {
        const val ID_STORY ="id_story"
    }
}