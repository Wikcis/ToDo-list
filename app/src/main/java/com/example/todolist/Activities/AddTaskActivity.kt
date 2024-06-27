package com.example.todolist.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.DatabaseManagement.DbManager
import com.example.todolist.Model.TaskModel
import com.example.todolist.R
import com.example.todolist.databinding.ActivityAddTaskBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        binding.notificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
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
            }
            else
            {
                val title = binding.titleEditText.text.toString()
                val description = binding.descriptionEditText.text.toString()
                val category = binding.categoryEditText.text.toString()
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd")
                val creationDate = currentDateTime.format(formatter)
                val endDate = binding.endDateEditText.text.toString()
                val attachment = binding.attachmentImageView.toString()

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

    }
}