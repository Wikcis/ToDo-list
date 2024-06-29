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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.Adapter.CategoryAdapter
import com.example.todolist.Adapter.TaskAdapter
import com.example.todolist.DatabaseManagement.DbManager
import com.example.todolist.Model.CategoryModel
import com.example.todolist.Model.TaskModel
import com.example.todolist.R
import com.example.todolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var tasksList = ArrayList<TaskModel>()
    private var categoriesList = ArrayList<CategoryModel>()
    private var dbManager : DbManager? = null
    private var taskAdapter : TaskAdapter? = null
    private var categoryAdapter : CategoryAdapter? = null
    private var onCategoryClick = false
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
        categoriesList = dbManager!!.getAllCategories()

        fetchTasksList(tasksList)
        fetchCategoriesList(categoriesList)

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
            val categoryName = if(onCategoryClick){
                categoriesList[0].name
            } else null

            tasksList = dbManager!!.sortAllTasksWithTitle(binding.searchBarEditText.text.toString(), categoryName!!, sortType)
            fetchTasksList(tasksList)

            sortType = if(sortType == "ASC") "DESC"
            else "ASC"
        }

        binding.searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != ""){
                    searchTasks(s)
                    fetchTasksList(tasksList)
                }
                else{
                    tasksList = dbManager!!.getAllTasks()
                    fetchTasksList(tasksList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun searchTasks(title: CharSequence?): ArrayList<TaskModel>{
        tasksList = dbManager!!.getAllTasksWithTitle(title.toString())
        return tasksList
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchTasksList(tasksList: ArrayList<TaskModel>){
        taskAdapter = TaskAdapter(applicationContext, tasksList)
        binding.tasksRecyclerView.adapter = taskAdapter
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCategoriesList(categoriesList: ArrayList<CategoryModel>){
        categoryAdapter = CategoryAdapter(applicationContext, categoriesList){category ->
            onCategoryItemClick(category)
        }
        binding.categoriesRecyclerView.adapter = categoryAdapter
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        categoryAdapter?.notifyDataSetChanged()
    }

    private fun onCategoryItemClick(category: CategoryModel) {
        if(onCategoryClick){
            categoriesList = dbManager!!.getAllCategories()
            tasksList = dbManager!!.getAllTasks()
            fetchTasksList(tasksList)
            fetchCategoriesList(categoriesList)
            onCategoryClick = false
        }else{
            onCategoryClick = true
            categoriesList.clear()
            categoriesList.add(category)
            tasksList = dbManager!!.getAllTasksWithCategory(category.name)
            fetchTasksList(tasksList)
            fetchCategoriesList(categoriesList)
        }
    }

}