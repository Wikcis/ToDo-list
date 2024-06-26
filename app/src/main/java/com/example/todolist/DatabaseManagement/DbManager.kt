package com.example.todolist.DatabaseManagement

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todolist.Model.TaskModel

class DbManager(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object{
        private const val DB_NAME = "User_Tasks"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "Tasks"
        private const val TASK_ID = "task_id"
        private const val TASK_TITLE = "title"
        private const val TASK_DESCRIPTION = "description"
        private const val TASK_CATEGORY = "category"
        private const val TASK_CREATION_DATE = "creation_date"
        private const val TASK_END_DATE = "end_date"
        private const val TASK_ATTACHMENT = "attachment"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQry = "CREATE TABLE $TABLE_NAME (" +
                "$TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$TASK_TITLE text, " +
                "$TASK_DESCRIPTION text," +
                "$TASK_CATEGORY text," +
                "$TASK_CREATION_DATE text, " +
                "$TASK_END_DATE text, " +
                "$TASK_ATTACHMENT text);"

        db?.execSQL(createTableQry)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    @SuppressLint("Range")
    fun getAllTasks(): ArrayList<TaskModel>{
        val taskList = ArrayList<TaskModel>()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    val id = cursor.getInt(cursor.getColumnIndex(TASK_ID))
                    val title = cursor.getString(cursor.getColumnIndex(TASK_TITLE))
                    val description = cursor.getString(cursor.getColumnIndex(TASK_DESCRIPTION))
                    val category = cursor.getString(cursor.getColumnIndex(TASK_CATEGORY))
                    val creationDate = cursor.getString(cursor.getColumnIndex(TASK_CREATION_DATE))
                    val endDate = cursor.getString(cursor.getColumnIndex(TASK_END_DATE))
                    val attachment = cursor.getString(cursor.getColumnIndex(TASK_TITLE))

                    val task = TaskModel(id, title, description, category, creationDate, endDate, attachment)
                    taskList.add(task)
                }while (cursor.moveToNext())
            }
        }
        cursor.close()
        return taskList
    }
    fun insertTask(task: TaskModel): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(TASK_TITLE, task.title)
        contentValues.put(TASK_DESCRIPTION, task.description)
        contentValues.put(TASK_CATEGORY, task.category)
        contentValues.put(TASK_CREATION_DATE, task.creationDate)
        contentValues.put(TASK_END_DATE, task.endDate)
        contentValues.put(TASK_ATTACHMENT, task.attachment)

        val res = db.insert(TABLE_NAME, null, contentValues)
        return res.toInt() != -1
    }

    @SuppressLint("Range")
    fun getTask(id: Int): TaskModel{
        val db = writableDatabase

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $TASK_ID = $id"
        val cursor = db.rawQuery(selectQuery, null)

        cursor.moveToFirst()
        val taskId = cursor.getInt(cursor.getColumnIndex(TASK_ID))
        val title = cursor.getString(cursor.getColumnIndex(TASK_TITLE))
        val description = cursor.getString(cursor.getColumnIndex(TASK_DESCRIPTION))
        val category = cursor.getString(cursor.getColumnIndex(TASK_CATEGORY))
        val creationDate = cursor.getString(cursor.getColumnIndex(TASK_CREATION_DATE))
        val endDate = cursor.getString(cursor.getColumnIndex(TASK_END_DATE))
        val attachment = cursor.getString(cursor.getColumnIndex(TASK_TITLE))

        val task = TaskModel(taskId, title, description, category, creationDate, endDate, attachment)
        cursor.close()

        return task
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

        contentValues.put(TASK_TITLE, task.title)
        contentValues.put(TASK_DESCRIPTION, task.description)
        contentValues.put(TASK_CATEGORY, task.category)
        contentValues.put(TASK_CREATION_DATE, task.creationDate)
        contentValues.put(TASK_END_DATE, task.endDate)
        contentValues.put(TASK_ATTACHMENT, task.attachment)

        val res = db.update(TABLE_NAME, contentValues, "$TASK_ID=?", arrayOf(task.id.toString()))
        db.close()
        return res != -1
    }
}