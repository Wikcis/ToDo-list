package com.example.todolist.management

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todolist.model.CategoryModel
import com.example.todolist.model.TaskModel

class DbManager(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val hideTasks = prefs.getString("hide_tasks", false.toString())
    companion object{
        private const val DB_NAME = "User_Tasks.db"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "Tasks"
        private const val TASK_ID = "task_id"
        private const val TASK_TITLE = "title"
        private const val TASK_DESCRIPTION = "description"
        private const val TASK_CATEGORY = "category"
        private const val TASK_CREATION_DATE = "creation_date"
        private const val TASK_END_DATE = "end_date"
        private const val TASK_ATTACHMENT = "attachment"
        private const val TASK_NOTIFICATIONS = "notifications"
        private const val WHERE_STATEMENT = "WHERE"
        private const val AND_STATEMENT = "AND"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQry = "CREATE TABLE $TABLE_NAME (" +
                "$TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$TASK_TITLE text, " +
                "$TASK_DESCRIPTION text," +
                "$TASK_CATEGORY text," +
                "$TASK_CREATION_DATE text, " +
                "$TASK_END_DATE text, " +
                "$TASK_ATTACHMENT text, " +
                "$TASK_NOTIFICATIONS INTEGER);"
        db?.execSQL(createTableQry)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTask(task: TaskModel): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        putDetails(contentValues, task)

        val res = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return res.toInt() != -1
    }

    fun deleteTask(id: Int): Boolean{
        val db = this.writableDatabase
        val res = db.delete(TABLE_NAME, "$TASK_ID=?", arrayOf(id.toString()))
        db.close()
        return res != -1
    }

    fun updateTask(task: TaskModel): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        putDetails(contentValues, task)

        val res = db.update(TABLE_NAME, contentValues, "$TASK_ID=?", arrayOf(task.id.toString()))
        db.close()
        return res != -1
    }

    private fun getTasks(selectQuery: String, selectionArgs: Array<String>?): ArrayList<TaskModel> {
        val taskList = ArrayList<TaskModel>()
        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery, selectionArgs)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    val task = getTaskRow(cursor)
                    taskList.add(task)
                }while (cursor.moveToNext())
            }
        }
        cursor.close()
        return taskList
    }

    fun getTask(id: Int): TaskModel{
        val tasks = getTasks("SELECT * FROM $TABLE_NAME WHERE $TASK_ID = $id", null)
        return tasks[0]
    }

    fun getTaskWithTitle(title: String): TaskModel? {
        val titleToQuery = "\"$title\""
        val queryHideTasks = makeHideTasksQuery(false)
        val tasks = getTasks("SELECT * FROM $TABLE_NAME WHERE $TASK_TITLE = $titleToQuery LIMIT 1 $queryHideTasks", null)
        return if(tasks.isEmpty()){
            null
        } else
            tasks[0]
    }

    fun getAllTasks(): ArrayList<TaskModel> {
        val queryHideTasks = makeHideTasksQuery(true)
        return getTasks("SELECT * FROM $TABLE_NAME $queryHideTasks", null)
    }

    fun getAllTasksWithTitle(searchTitle: String): ArrayList<TaskModel>{
        val queryHideTasks = makeHideTasksQuery(false)
        return getTasks("SELECT * FROM $TABLE_NAME WHERE $TASK_TITLE LIKE ? COLLATE NOCASE $queryHideTasks", arrayOf("$searchTitle%"))
    }

    fun getAllTasksWithCategory(category: String): ArrayList<TaskModel>{
        val queryHideTasks = makeHideTasksQuery(false)
        return getTasks("SELECT * FROM $TABLE_NAME WHERE $TASK_CATEGORY LIKE ? COLLATE NOCASE $queryHideTasks", arrayOf(category))
    }

    fun sortAllTasksWithTitle(searchTitle: String, category: String, sortType: String): ArrayList<TaskModel>{
        val categorySearch = if(category.isNotEmpty()){
            "$TASK_CATEGORY = \"$category\" $AND_STATEMENT"
        }else ""
        val queryHideTasks = makeHideTasksQuery(false)
        return getTasks(
            "SELECT * FROM $TABLE_NAME WHERE $categorySearch $TASK_TITLE LIKE ? COLLATE NOCASE $queryHideTasks ORDER BY $TASK_END_DATE $sortType ",
            arrayOf("$searchTitle%")
        )
    }

    @SuppressLint("Range")
    private fun getCategories(selectQuery: String): ArrayList<CategoryModel>{
        val categoriesList = ArrayList<CategoryModel>()
        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    val category = CategoryModel(cursor.getString(cursor.getColumnIndex(TASK_CATEGORY)))
                    categoriesList.add(category)
                }while (cursor.moveToNext())
            }
        }
        cursor.close()
        return categoriesList
    }

    fun getAllCategories(): ArrayList<CategoryModel>{
        val queryHideTasks = makeHideTasksQuery(true)
        return getCategories("SELECT DISTINCT $TASK_CATEGORY FROM $TABLE_NAME $queryHideTasks")
    }

    @SuppressLint("Range")
    private fun getTaskRow(cursor: Cursor): TaskModel {
        val id = cursor.getInt(cursor.getColumnIndex(TASK_ID))
        val title = cursor.getString(cursor.getColumnIndex(TASK_TITLE))
        val description = cursor.getString(cursor.getColumnIndex(TASK_DESCRIPTION))
        val category = cursor.getString(cursor.getColumnIndex(TASK_CATEGORY))
        val creationDate = cursor.getString(cursor.getColumnIndex(TASK_CREATION_DATE))
        val endDate = cursor.getString(cursor.getColumnIndex(TASK_END_DATE))
        val attachment = cursor.getString(cursor.getColumnIndex(TASK_ATTACHMENT))
        val notifications = cursor.getInt(cursor.getColumnIndex(TASK_NOTIFICATIONS))

        return TaskModel(
            id,
            title,
            description,
            category,
            creationDate,
            endDate,
            attachment,
            notifications
        )
    }

    private fun putDetails(contentValues: ContentValues, task: TaskModel){
        contentValues.put(TASK_TITLE, task.title)
        contentValues.put(TASK_DESCRIPTION, task.description)
        contentValues.put(TASK_CATEGORY, task.category)
        contentValues.put(TASK_CREATION_DATE, task.creationDate)
        contentValues.put(TASK_END_DATE, task.endDate)
        contentValues.put(TASK_ATTACHMENT, task.attachment)
        contentValues.put(TASK_NOTIFICATIONS, task.notifications)
    }

    private fun makeHideTasksQuery(whereStatement: Boolean): String {
        val statement = if(whereStatement){
            WHERE_STATEMENT
        } else AND_STATEMENT
        val localDate = TimeManager().formatCurrentDate()

        return if(hideTasks.toBoolean()){
            "$statement $TASK_END_DATE > \"$localDate\""
        }else ""
    }
}