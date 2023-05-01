package com.affan.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.affan.storyapp.R
import com.affan.storyapp.adapter.RecyclerAdapter
import com.affan.storyapp.databinding.ActivityMainBinding
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.preferences.LoginPreference
import com.affan.storyapp.viewmodel.LoginFactory
import com.affan.storyapp.viewmodel.LoginViewModel
import com.affan.storyapp.viewmodel.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class MainActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mainViewModel: MainViewModel
    private var binding: ActivityMainBinding? = null
    override fun onResume() {
        super.onResume()

        loginViewModel.getLoginSession().observe(this) { savedToken ->

            if (savedToken != null) {

                mainViewModel.getAllStories(savedToken)
            } else {

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            rvUser.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)

            }
            btnPost.setOnClickListener {
                val intent = Intent(this@MainActivity, PostActivity::class.java)
                startActivity(intent)
            }
        }


        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]
        val pref = LoginPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, LoginFactory(pref))[LoginViewModel::class.java]

        mainViewModel.token.observe(this) {
            if (it != null) {
                loginViewModel.saveSession(it)
            }
        }
        mainViewModel.loading.observe(this) {
            showLoading(it)
        }
        loginViewModel.getLoginSession().observe(this) { savedToken ->

            if (savedToken != null) {
                mainViewModel.getAllStories(savedToken)
            } else {

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        mainViewModel.listStory.observe(this) {

            if (it != null) {
                setData(it)
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            dimmedBackground.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu2 -> {
                loginViewModel.deleteSession()
                true
            }
            R.id.setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> true
        }
    }

    private fun setData(userStories: List<ListStoryItem>) {
        val adapter = RecyclerAdapter(userStories)
        binding?.rvUser?.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}