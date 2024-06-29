package com.example.todolist.interfaces

import com.example.todolist.model.CategoryModel

interface OnCategoryClickListener {
    fun onCategoryClick(category: CategoryModel)
}