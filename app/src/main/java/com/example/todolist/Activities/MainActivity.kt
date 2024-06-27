package com.example.todolist.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.Adapter.TaskAdapter
import com.example.todolist.DatabaseManagement.DbManager
import com.example.todolist.Model.TaskModel
import com.example.todolist.R
import com.example.todolist.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var tasksList = ArrayList<TaskModel>()
    private var dbManager : DbManager? = null
    private var adapter : TaskAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbManager = DbManager(this)
        tasksList = dbManager!!.getAllTasks()
        fetchList(tasksList)

        binding.addTaskButton.setOnClickListener {
            val intent = Intent(applicationContext, AddTaskActivity::class.java)
            startActivity(intent)
        }

        binding.settingsButton.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }

        var sortType = "ASC"
        binding.sortButton.setOnClickListener {

            tasksList = dbManager!!.sortAllTasksWithTitle(binding.searchBarEditText.text.toString(), sortType)
            fetchList(tasksList)

            sortType = if(sortType == "ASC") "DESC"
            else "ASC"
        }

        binding.searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch {
                    if(s.toString() != ""){
                        searchTasks(s)
                        fetchList(tasksList)
                    }
                    else{
                        tasksList = dbManager!!.getAllTasks()
                        fetchList(tasksList)
                    }

                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private suspend fun searchTasks(title: CharSequence?): ArrayList<TaskModel>{
        return withContext(Dispatchers.IO) {
            tasksList = dbManager!!.getAllTasksWithTitle(title.toString())
            tasksList
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchList(tasksList: ArrayList<TaskModel>){
        adapter = TaskAdapter(applicationContext, tasksList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter?.notifyDataSetChanged()
    }

}