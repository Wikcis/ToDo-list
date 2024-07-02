package com.example.todolist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.interfaces.OnTaskClickListener
import com.example.todolist.management.TimeManager
import com.example.todolist.model.TaskModel

class TaskAdapter(
    private val context: Context,
    private val tasksList: ArrayList<TaskModel>,
    private val taskClickListener: OnTaskClickListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasksList[position]

        holder.title.text = task.title
        holder.description.text = task.description
        holder.category.text = task.category
        holder.creationDate.text = task.creationDate
        holder.endDate.text = task.endDate

        if(TimeManager().isDatePast(task.endDate)){
            holder.statusImageView.setImageResource(R.drawable.tick)
        } else {
            holder.statusImageView.setImageResource(R.drawable.x)
        }

        if (task.notifications == 1)
            holder.notifications.setImageResource(R.drawable.bell)
        else
            holder.notifications.setImageResource(R.drawable.bell_slash)

        if (task.attachment.isNotEmpty())
            holder.attachment.setImageResource(R.drawable.attachment)
        else
            holder.attachment.setImageDrawable(null)

        holder.itemView.setOnClickListener {
            taskClickListener.onTaskClick(task)
        }

        holder.itemView.setOnLongClickListener {
            taskClickListener.onTaskLongClick(task, position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, tasksList.size)
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
        val statusImageView: ImageView = itemView.findViewById(R.id.statusImageView)
    }
}