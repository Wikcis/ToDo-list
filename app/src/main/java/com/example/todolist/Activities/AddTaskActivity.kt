package com.example.todolist.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.todolist.DatabaseManagement.DbManager
import com.example.todolist.Model.TaskModel
import com.example.todolist.R
import com.example.todolist.databinding.ActivityAddTaskBinding
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private var dbManager : DbManager? = null
    private val PICK_FILE_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbManager = DbManager(this)

        var notifications = 0

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Notifications are ON", Toast.LENGTH_SHORT).show()
                notifications = 1
            } else {
                notifications = 0
                Toast.makeText(this, "Notifications are OFF", Toast.LENGTH_SHORT).show()
            }
        }

        binding.saveTaskButton.setOnClickListener {
            if(binding.titleEditText.text.isBlank()){
                Toast.makeText(this, "There is no title!", Toast.LENGTH_SHORT).show()
            } else if(dbManager!!.getTaskWithTitle(binding.titleEditText.text.toString()) != null){
                Toast.makeText(this, "There is already task with this title!", Toast.LENGTH_SHORT).show()
            }
            else if(validateDate(binding.endDateEditText.text.toString())){
                val title = binding.titleEditText.text.toString()
                val description = binding.descriptionEditText.text.toString()
                val category = binding.categoryEditText.text.toString()
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")
                val creationDate = currentDateTime.format(formatter)
                val endDate = binding.endDateEditText.text.toString()
                val attachment = if(binding.attachmentImageView.drawable != null) {
                    binding.attachmentImageView.tag.toString()
                } else ""

                val task = TaskModel(0,
                    title,
                    description,
                    category,
                    creationDate,
                    endDate,
                    attachment,
                    notifications
                )

                dbManager!!.insertTask(task)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.attachmentButton.setOnClickListener {
            openFilePicker()
        }

    }

    private fun validateDate(date: String): Boolean {
        try{
            if(date.isEmpty()){
                Toast.makeText(this, "You must specify execution date!", Toast.LENGTH_SHORT).show()
                return false
            }

            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")
            val localDate = LocalDateTime.parse(date, formatter)

            if(!formatter.format(localDate).equals(date)){
                Toast.makeText(this, "Execution date is incorrect!", Toast.LENGTH_SHORT).show()
                return false
            }else if(localDate <= LocalDateTime.now()){
                Toast.makeText(this, "Date must be in the future!", Toast.LENGTH_SHORT).show()
                return false
            }

        }catch (e: Exception){
            Toast.makeText(this, "Execution date is incorrect!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Choose a file"), PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                handleFileUri(uri)
            }
        }
    }

    private fun handleFileUri(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(binding.attachmentImageView)

        binding.attachmentImageView.tag = uri

        val inputStream = contentResolver.openInputStream(uri)
        val fileName = getFileName(uri)

        if (inputStream != null && fileName != null) {
            try {
                val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                externalDir.mkdirs()

                val file = File(externalDir, fileName)
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

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
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
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
}