package com.affan.storyapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.affan.storyapp.databinding.ActivityPostBinding
import com.affan.storyapp.helper.reduceFileImage
import com.affan.storyapp.helper.rotateImage
import com.affan.storyapp.helper.uriToFile
import com.affan.storyapp.preferences.LoginPreference
import com.affan.storyapp.viewmodel.LoginFactory
import com.affan.storyapp.viewmodel.LoginViewModel
import com.affan.storyapp.viewmodel.MainViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class PostActivity : AppCompatActivity() {
    private var binding: ActivityPostBinding? = null
    private var getFile: File? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]
        val pref = LoginPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, LoginFactory(pref))[LoginViewModel::class.java]
        setContentView(binding?.root)
        if (!isGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQ_CODE
            )
        }

        binding?.apply {
            btnCamera.setOnClickListener { startCam() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener {
                uploadImage()
            }
            btnLoc?.setOnClickListener {
                val intent = Intent(this@PostActivity, LocationActivity::class.java)
              launchMaps.launch(intent)

            }

        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadImage() {

        Log.d("getFile", getFile.toString())
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val desc = binding?.edAddDescription?.text.toString().trim()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            loginViewModel.getLoginSession().observe(this) { savedToken ->

                if (savedToken != null) {
                    mainViewModel.apply {

                        if (latitude!=null && longitude!=null){
                            val lat = latitude.toString()
                                .toRequestBody("text/plain".toMediaType())
                            val lng = longitude.toString()
                                .toRequestBody("text/plain".toMediaType())
                            savedToken.token?.let {
                                uploadStory(desc, imageMultipart,
                                    it, lat, lng)
                            }
                        }else{
                            savedToken.token?.let {
                                uploadStory(desc, imageMultipart,
                                    it, null, null)
                            }

                        }
                        error.observe(this@PostActivity) {
                            if (!it) {
                                val intent = Intent(this@PostActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                        loading.observe(this@PostActivity) {
                            showLoading(it)
                        }
                    }
                } else {

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launchGallery.launch(chooser)
    }

    private fun startCam() {
        val intent = Intent(this, CameraActivity::class.java)
        launchCam.launch(intent)
    }

    private val launchCam =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == CAM_RESULT) {
                val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getSerializableExtra("photo", File::class.java)
                } else {
                    it.data?.getSerializableExtra("photo")
                } as? File
                val isbackCam = it.data?.getBooleanExtra("isBack ", true) as Boolean

                file?.let {
                    rotateImage(file, isbackCam)
                    getFile = file
                    binding?.ivItemPhoto?.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }
        }
    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg = it.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@PostActivity)
                getFile = myFile
                binding?.ivItemPhoto?.setImageURI(uri)

            }
        }
    }
    private val launchMaps = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == LOCATION_ACTIVITY_REQUEST_CODE) {
        val text =
            it.data?.getSerializableExtra("ADDRESS")
            binding?.tvAddress?.text = text.toString()
            latitude = it.data?.getSerializableExtra("EXTRA_LAT") as Double?
            longitude = it.data?.getSerializableExtra("EXTRA_LNG") as Double?

        }
    }

    private fun isGranted() = Companion.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE) {
            if (!isGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
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

    companion object {
        const val CAM_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQ_CODE = 10


        const val LOCATION_ACTIVITY_REQUEST_CODE = 1

    }
}