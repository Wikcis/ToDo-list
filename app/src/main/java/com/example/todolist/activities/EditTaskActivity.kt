package com.example.todolist.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.todolist.databaseManagement.DbManager
import com.example.todolist.model.TaskModel
import com.example.todolist.R
import com.example.todolist.databinding.ActivityEditTaskBinding
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private var dbManager: DbManager? = null
    private val PICK_FILE_REQUEST_CODE = 1
    private var addAttachment = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbManager = DbManager(this)

        val taskId = intent.getIntExtra("TASK_ID", -1)
        val task = dbManager!!.getTask(taskId)


        binding.titleEditText.setText(task.title)
        binding.descriptionEditText.setText(task.description)
        binding.categoryEditText.setText(task.category)
        binding.endDateEditText.setText(task.endDate)
        binding.notificationSwitch.isChecked = task.notifications == 1

        if (task.attachment.isNotEmpty()) {
            Glide.with(this)
                .load(task.attachment)
                .into(binding.attachmentImageView)
        }
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
            if (binding.titleEditText.text.isBlank()) {
                Toast.makeText(this, "There is no title!", Toast.LENGTH_SHORT).show()
            }
            else if(validateDate(binding.endDateEditText.text.toString())){
                val title = binding.titleEditText.text.toString()
                val description = binding.descriptionEditText.text.toString()
                val category = binding.categoryEditText.text.toString()
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")
                val creationDate = currentDateTime.format(formatter)
                val endDate = binding.endDateEditText.text.toString()
                val attachment = if (addAttachment) {
                    binding.attachmentImageView.tag.toString()
                } else task.attachment

                val taskTemp = TaskModel(
                    taskId,
                    title,
                    description,
                    category,
                    creationDate,
                    endDate,
                    attachment,
                    notifications
                )

                dbManager!!.updateTask(taskTemp)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.attachmentButton.setOnClickListener {
            openFilePicker()
        }

        binding.removeAttachmentButton.setOnClickListener {
            clearAttachment()
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

    private fun clearAttachment() {
        binding.attachmentImageView.setImageDrawable(null)
        addAttachment = true
        binding.attachmentImageView.tag = ""
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(
            Intent.createChooser(intent, "Choose a file"),
            PICK_FILE_REQUEST_CODE
        )
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

        addAttachment = true

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