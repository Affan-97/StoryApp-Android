package com.affan.storyapp.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.affan.storyapp.adapter.LoadingStateAdapter
import com.affan.storyapp.adapter.RecyclerAdapter
import com.affan.storyapp.api.ApiConfig
import com.affan.storyapp.databinding.FragmentHomeBinding
import com.affan.storyapp.preferences.LoginPreference
import com.affan.storyapp.ui.PostActivity
import com.affan.storyapp.viewmodel.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class HomeFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var factory: ViewModelFactory
    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = requireContext().applicationContext.dataStore
        val pref = LoginPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, LoginFactory(pref))[LoginViewModel::class.java]

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        factory = ViewModelFactory.getInstance(requireActivity().applicationContext)
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
        getData()

        binding?.apply {

            rvUser.layoutManager = LinearLayoutManager(requireContext())
            rvUser.setHasFixedSize(true)

            btnPost.setOnClickListener {
                startActivity(Intent(requireContext(), PostActivity::class.java))
            }
        }


    }

    private fun getData() {
        val adapter = RecyclerAdapter()
        binding?.rvUser?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        storyViewModel.getStory().observe(viewLifecycleOwner) {
            Log.d("TAG", "getData: $it.")
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
