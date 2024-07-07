package com.example.todolist.managers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.util.Locale

class ImageManager(private val context: Context){
    companion object{
        const val PICK_FILE_REQUEST_CODE = 1
    }
    private val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

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
        val fileName = getFileName(uri)

        image.tag = fileName

        if (inputStream != null && fileName != null) {
            try {
                val file = File(documentsDir, fileName)
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                glideImage(file, image)

            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                inputStream.close()
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }

    fun glideImage(uri: File, image: ImageView) {
        Glide.with(context)
            .load(uri)
            .into(image)
    }

    fun getImageFileFromDocuments(imageFileName: String): File {
        return File(documentsDir, imageFileName)
    }

    fun openFile(fileName: String) {
        val file = getImageFileFromDocuments(fileName)
        try {
            val fileUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, getMimeType(fileUri))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch(e: Exception) {
            e.printStackTrace()
            ToastManager(context).showToast(ToastManager.NO_FILE)
        }
    }

    private fun getMimeType(uri: Uri): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase(Locale.getDefault()))
    }
}