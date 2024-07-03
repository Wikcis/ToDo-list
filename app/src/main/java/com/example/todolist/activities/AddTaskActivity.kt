package com.example.todolist.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.R
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.example.todolist.managers.DbManager
import com.example.todolist.managers.ImageManager
import com.example.todolist.managers.TimeManager
import com.example.todolist.model.TaskModel
import com.example.todolist.objects.ToastMessages

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private var dbManager : DbManager? = null

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
            if (isChecked) showToast(ToastMessages.NOTIFICATIONS_ON)
            else showToast(ToastMessages.NOTIFICATIONS_OFF)

            notifications = 1 - notifications
        }

        binding.saveTaskButton.setOnClickListener {
            if (binding.titleEditText.text.isBlank()) {
                showToast(ToastMessages.NO_TITLE)
            } else if (dbManager!!.getTaskWithTitle(binding.titleEditText.text.toString()) != null) {
                showToast(ToastMessages.TITLE_UNAVAILABLE)
            } else{
                val toastMessage = TimeManager().validateDate(binding.endDateEditText.text.toString())

                if (toastMessage == ToastMessages.SUCCESS) {
                    val title = binding.titleEditText.text.toString()
                    val description = binding.descriptionEditText.text.toString()
                    val category = binding.categoryEditText.text.ifEmpty { "No Category" }.toString()
                    val creationDate = TimeManager().getCurrentDate()
                    val endDate = binding.endDateEditText.text.toString()

                    val attachment = if (binding.attachmentImageView.drawable != null) {
                        binding.attachmentImageView.tag.toString()
                    } else ""

                    val task = TaskModel(
                        0,
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
                } else
                    showToast(toastMessage)
            }
        }

        binding.attachmentButton.setOnClickListener {
            ImageManager(applicationContext).openFilePicker(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageManager.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                ImageManager(applicationContext).handleFileUri(uri, binding.attachmentImageView)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}