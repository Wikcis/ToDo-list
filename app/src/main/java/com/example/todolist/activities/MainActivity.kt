package com.example.todolist.activities

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
import com.example.todolist.R
import com.example.todolist.adapters.CategoryAdapter
import com.example.todolist.adapters.TaskAdapter
import com.example.todolist.databaseManagement.DbManager
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.interfaces.OnCategoryClickListener
import com.example.todolist.interfaces.OnTaskClickListener
import com.example.todolist.model.CategoryModel
import com.example.todolist.model.TaskModel

/*TODO:
    Ogarnąć status zakonczone/niezakonczone ikonka itp
    Ustawienia aplikacji:
        - Ukrywanie skonczonnych zadan
        - Ile minut pzed czasem wykonania powinno buć powiadomoenie
    Powiadomienia
    Załączniki otwierać z dcim
 */

class MainActivity : AppCompatActivity(), OnTaskClickListener, OnCategoryClickListener {
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
        getAllItemsForLists()

        fetchLists()

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
            } else ""

            tasksList = dbManager!!.sortAllTasksWithTitle(binding.searchBarEditText.text.toString(), categoryName, sortType)
            fetchTasksList()

            sortType = if(sortType == "ASC") "DESC"
            else "ASC"
        }

        binding.searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s.toString() != "") searchTasks(s)
                else tasksList = dbManager!!.getAllTasks()

                fetchTasksList()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun searchTasks(title: CharSequence?): ArrayList<TaskModel>{
        tasksList = dbManager!!.getAllTasksWithTitle(title.toString())
        return tasksList
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchTasksList(){
        taskAdapter = TaskAdapter(applicationContext, tasksList, this)
        binding.tasksRecyclerView.adapter = taskAdapter
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCategoriesList(){
        categoryAdapter = CategoryAdapter(applicationContext, categoriesList,this)
        binding.categoriesRecyclerView.adapter = categoryAdapter
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        categoryAdapter?.notifyDataSetChanged()
    }
    override fun onTaskClick(task: TaskModel) {
        val intent = Intent(this, EditTaskActivity::class.java)

        intent.putExtra("TASK_ID", task.id)

        startActivity(intent)
    }

    override fun onTaskLongClick(task: TaskModel, position: Int) {
        dbManager!!.deleteTask(task.id)
        getAllItemsForLists()

        fetchLists()
    }

    private fun getAllItemsForLists() {
        tasksList = dbManager!!.getAllTasks()
        categoriesList = dbManager!!.getAllCategories()
    }

    override fun onCategoryClick(category: CategoryModel) {
        if(onCategoryClick){
            getAllItemsForLists()
        }else{
            categoriesList.clear()
            categoriesList.add(category)
            tasksList = dbManager!!.getAllTasksWithCategory(category.name)
        }
        onCategoryClick = !onCategoryClick
        fetchLists()
    }

    private fun fetchLists() {
        fetchTasksList()
        fetchCategoriesList()
    }
}