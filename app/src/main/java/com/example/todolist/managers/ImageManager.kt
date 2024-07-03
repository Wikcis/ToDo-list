package com.example.todolist.managers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream

class ImageManager(private val context: Context){
    companion object{
        const val PICK_FILE_REQUEST_CODE = 1
    }
    private val dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

    fun openFilePicker(activity: Activity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        activity.startActivityForResult(
            Intent.createChooser(intent, "Choose a file"),
            PICK_FILE_REQUEST_CODE
        )
    }

    fun handleFileUri(uri: Uri, image: ImageView) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = createImageFile(image)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        glideImage(file, image)
        Thread.sleep(500)
    }
    private fun createImageFile(image: ImageView): File {
        val imageFileName = "IMG_${System.currentTimeMillis()}.jpg"

        image.tag = imageFileName
        return File(dcimDirectory, imageFileName).apply {
            createNewFile()
        }
    }

    fun getImageFileFromDCIM(imageFileName: String): File {
        return File(dcimDirectory, imageFileName)
    }

    fun glideImage(uri: File, image: ImageView) {
        Glide.with(context)
            .load(uri)
            .into(image)
    }
}