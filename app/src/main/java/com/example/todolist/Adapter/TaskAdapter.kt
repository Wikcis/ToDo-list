package com.example.todolist.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.Activities.EditTaskActivity
import com.example.todolist.DatabaseManagement.DbManager
import com.example.todolist.Model.TaskModel
import com.example.todolist.R

class TaskAdapter(
    private val context: Context,
    private val tasksList: ArrayList<TaskModel>,
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var dbManager : DbManager? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        dbManager = DbManager(context)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasksList[position]

        holder.title.text = task.title
        holder.description.text = task.description
        holder.category.text = task.category
        holder.creationDate.text = task.creationDate
        holder.endDate.text = task.endDate

        if (task.notifications == 1) {
            holder.notifications.setImageResource(R.drawable.bell)
        } else {
            holder.notifications.setImageResource(R.drawable.bell_slash)
        }

        if (task.attachment.isNotEmpty()) {
            holder.attachment.setImageResource(R.drawable.attachment)
        } else {
            holder.attachment.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, EditTaskActivity::class.java).apply {
                putExtra("TASK_ID", task.id)
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            removeTask(task.id, position)
            true
        }

    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
        val category: TextView = itemView.findViewById(R.id.categoryTextView)
        val creationDate: TextView = itemView.findViewById(R.id.creationDateTextView)
        val endDate: TextView = itemView.findViewById(R.id.endDateTextView)
        val attachment: ImageView = itemView.findViewById(R.id.attachmentImageView)
        val notifications: ImageView = itemView.findViewById(R.id.bellImageView)
    }
    private fun removeTask(id: Int, position: Int) {
        dbManager!!.deleteTask(id)
        tasksList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasksList.size)
    }

}