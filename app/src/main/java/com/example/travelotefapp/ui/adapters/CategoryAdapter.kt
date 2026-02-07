package com.example.travelotefapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelotefapp.R
import com.example.travelotefapp.data.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView:  View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: Category) {
            tvIcon.text = category.icon
            tvName.text = category.name
            itemView.setOnClickListener { onCategoryClick(category) }
        }
    }

    override fun onCreateViewHolder(parent:  ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder:  CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
}