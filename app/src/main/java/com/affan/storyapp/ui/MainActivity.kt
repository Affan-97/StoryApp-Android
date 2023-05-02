package com.affan.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.affan.storyapp.R
import com.affan.storyapp.databinding.ActivityMainBinding
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.preferences.LoginPreference
import com.affan.storyapp.ui.fragment.HomeFragment
import com.affan.storyapp.viewmodel.LoginFactory
import com.affan.storyapp.viewmodel.LoginViewModel
import com.affan.storyapp.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        mainViewModel.listStory.observe(this) { item ->
            mainViewModel.loading.observe(this) {
                if (item != null) {
                    if (it != null) {
                Log.d("Tokenb", "onViewCreated: $it $item")

                    }
                }
            }
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


        val navView: BottomNavigationView? = binding?.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_maps,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView?.setupWithNavController(navController)


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


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}