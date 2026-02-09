package com.travelotef.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.travelotef.app.R
import com.travelotef.app.domain.model.Category

/**
 * RecyclerView Adapter for category chips (horizontal list)
 */
class CategoryAdapter(
    private val onCategoryClick: (Int?) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedCategoryId: Int? = null

    fun setSelectedCategory(categoryId: Int?) {
        val oldSelected = selectedCategoryId
        selectedCategoryId = categoryId
        // Refresh items that changed selection state
        currentList.forEachIndexed { index, category ->
            if (category.id == oldSelected || category.id == categoryId ||
                (oldSelected == null && index == 0) || (categoryId == null && index == 0)) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryCard: MaterialCardView = itemView.findViewById(R.id.categoryCard)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: Category) {
            tvCategoryName.text = category.name

            // Handle "All" category (id = -1)
            val isSelected = if (category.id == -1) {
                selectedCategoryId == null
            } else {
                selectedCategoryId == category.id
            }

            // Selected state
            categoryCard.isChecked = isSelected
            categoryCard.strokeWidth = if (isSelected) 2 else 0
            tvCategoryName.setTextColor(
                itemView.context.getColor(
                    if (isSelected) R.color.primary else R.color.text_primary
                )
            )

            // Click
            itemView.setOnClickListener {
                val clickedId = if (category.id == -1) null else category.id
                onCategoryClick(clickedId)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
