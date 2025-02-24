package com.affan.storyapp.helper

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import com.affan.storyapp.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
private const val MAXIMAL_SIZE = 1000000
val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.getString(R.string.app_name)).apply {
            mkdir()
        }
    }
    val output = if (mediaDir != null && mediaDir.exists()) mediaDir else application.filesDir
    return File(output, "$timeStamp.jpg")
}

fun rotateImage(file: File, isBack: Boolean = false) {
    val matrix = Matrix()
    val bitmap = BitmapFactory.decodeFile(file.path)
    val rotate = if (isBack) 90f else -90f
    matrix.postRotate(rotate)
    if (!isBack) {
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    }
    val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

}

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context)
    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } >= 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5


    } while (streamLength > MAXIMAL_SIZE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file

}

fun convertDate(isoDateString: String): String {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val date = inputFormat.parse(isoDateString)
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return outputFormat.format(date)

}