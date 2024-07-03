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
import com.example.todolist.databinding.ActivityEditTaskBinding
import com.example.todolist.managers.DbManager
import com.example.todolist.managers.ImageManager
import com.example.todolist.managers.TimeManager
import com.example.todolist.model.TaskModel
import com.example.todolist.objects.ToastMessages

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private var dbManager: DbManager? = null
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
            val file = ImageManager(applicationContext).getImageFileFromDCIM(task.attachment)

            ImageManager(applicationContext).glideImage(file, binding.attachmentImageView)
        } else binding.attachmentImageView.tag = ""
        var notifications = task.notifications

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            notifications = if (isChecked){
                showToast(ToastMessages.NOTIFICATIONS_ON)
                1
            } else {
                showToast(ToastMessages.NOTIFICATIONS_OFF)
                0
            }
        }

        binding.saveTaskButton.setOnClickListener {
            if(ifTaskIsTheSame(task, notifications)){
                showToast(ToastMessages.TASK_NOT_CHANGED)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else {
                if (binding.titleEditText.text.isBlank()) showToast(ToastMessages.NO_TITLE)

                val toastMessage =
                    TimeManager().validateDate(binding.endDateEditText.text.toString())

                if (toastMessage == ToastMessages.SUCCESS) {
                    val title = binding.titleEditText.text.toString()
                    val description = binding.descriptionEditText.text.toString()
                    val category = binding.categoryEditText.text.toString()
                    val endDate = binding.endDateEditText.text.toString()
                    val attachment = if (addAttachment) binding.attachmentImageView.tag.toString()
                    else task.attachment

                    val taskTemp = TaskModel(
                        taskId,
                        title,
                        description,
                        category,
                        task.creationDate,
                        endDate,
                        attachment,
                        notifications
                    )

                    dbManager!!.updateTask(taskTemp)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else
                    showToast(toastMessage)
            }
        }

        binding.attachmentButton.setOnClickListener {
            ImageManager(applicationContext).openFilePicker(this)
        }

        binding.removeAttachmentButton.setOnClickListener {
            clearAttachment()
        }
    }

    private fun ifTaskIsTheSame(task: TaskModel, notifications: Int): Boolean {
        val taskTemp = TaskModel(
            task.id,
            binding.titleEditText.text.toString(),
            binding.descriptionEditText.text.toString(),
            binding.categoryEditText.text.toString(),
            task.creationDate,
            binding.endDateEditText.text.toString(),
            binding.attachmentImageView.tag.toString(),
            notifications
        )
        return task == taskTemp
    }

    private fun clearAttachment() {
        binding.attachmentImageView.setImageDrawable(null)
        binding.attachmentImageView.tag = ""
        addAttachment = true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        addAttachment = true
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