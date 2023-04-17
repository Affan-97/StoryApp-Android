package com.affan.storyapp.ui

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.affan.storyapp.R
import com.affan.storyapp.databinding.ActivityCameraBinding
import com.affan.storyapp.helper.createFile
import java.io.File


class CameraActivity : AppCompatActivity() {
    private var binding: ActivityCameraBinding? = null
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        binding?.apply {
            captureImage.setOnClickListener { takePhoto() }
            switchCamera.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

                startCamera()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val prev = Preview.Builder().build().also {
                it.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            try {

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    prev,
                    imageCapture
                )
            } catch (exc: java.lang.Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    getString(R.string.camera_fail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val filePhoto = createFile(application)

        val outputOPtions = ImageCapture.OutputFileOptions.Builder(filePhoto).build()
        imageCapture.takePicture(
            outputOPtions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra("photo", filePhoto)
                    intent.putExtra("isBack", cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    setResult(PostActivity.CAM_RESULT, intent)
                    finish()
                }
            })

    }
    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


}