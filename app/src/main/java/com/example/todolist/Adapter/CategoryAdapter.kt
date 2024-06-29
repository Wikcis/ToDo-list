package com.example.todolist.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.Model.CategoryModel
import com.example.todolist.R

class CategoryAdapter(
    private val context: Context,
    private val categoriesList: ArrayList<CategoryModel>,
    private val itemClickListener: (CategoryModel) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoriesList[position]

        holder.name.text = category.name

        holder.itemView.setOnClickListener {
            itemClickListener(category)
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameTextView)
    }
}