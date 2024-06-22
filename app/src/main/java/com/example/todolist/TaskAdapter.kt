package com.example.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val context: Context, private val tasksList: ArrayList<TaskModel>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.title.text = tasksList[position].title
        holder.description.text = tasksList[position].description
        holder.category.text = tasksList[position].category
        holder.creationDate.text = tasksList[position].creationDate
        holder.endDate.text = tasksList[position].endDate
        if(tasksList[position].attachment){
            holder.attachment.setImageResource(R.drawable.attachment)
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
    }

}