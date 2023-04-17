package com.affan.storyapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.affan.storyapp.R
import com.affan.storyapp.databinding.ActivityPostBinding
import com.affan.storyapp.helper.rotateImage
import com.affan.storyapp.helper.uriToFile
import java.io.File

class PostActivity : AppCompatActivity() {
    private var binding: ActivityPostBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if (!isGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQ_CODE
            )
        }

        binding?.apply {
            btnCamera.setOnClickListener { startCam() }
            btnGallery.setOnClickListener { startGallery() }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type= "image/*"
        val chooser = Intent.createChooser(intent,"Choose a Picture")
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
                binding?.ivItemPhoto?.setImageURI(uri)

            }
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

    companion object {
        const val CAM_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQ_CODE = 10
    }
}