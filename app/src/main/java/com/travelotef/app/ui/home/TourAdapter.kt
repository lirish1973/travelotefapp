package com.travelotef.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.travelotef.app.R
import com.travelotef.app.domain.model.Tour

/**
 * RecyclerView Adapter for displaying tours
 */
class TourAdapter(
    private val onTourClick: (Tour) -> Unit,
    private val onFavoriteClick: (Tour) -> Unit
) : ListAdapter<Tour, TourAdapter.TourViewHolder>(TourDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tour, parent, false)
        return TourViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivTourImage: ImageView = itemView.findViewById(R.id.ivTourImage)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvTourTitle: TextView = itemView.findViewById(R.id.tvTourTitle)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)

        fun bind(tour: Tour) {
            // Title
            tvTourTitle.text = tour.name

            // Price
            tvPrice.text = tour.formattedPrice

            // Category
            tvCategory.text = tour.primaryCategory
            tvCategory.visibility = if (tour.primaryCategory.isNotEmpty()) View.VISIBLE else View.GONE

            // Rating
            if (tour.averageRating > 0) {
                tvRating.text = "⭐ ${String.format("%.1f", tour.averageRating)} (${tour.reviewCount})"
                tvRating.visibility = View.VISIBLE
            } else {
                tvRating.visibility = View.GONE
            }

            // Stock
            tvStock.text = if (tour.isInStock) "במלאי" else "אזל"
            tvStock.setTextColor(
                itemView.context.getColor(
                    if (tour.isInStock) R.color.success else R.color.error
                )
            )

            // Image
            if (tour.thumbnailUrl.isNotEmpty()) {
                Glide.with(ivTourImage)
                    .load(tour.thumbnailUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivTourImage)
            }

            // Favorite button
            btnFavorite.setImageResource(
                if (tour.isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )

            // Click listeners
            itemView.setOnClickListener { onTourClick(tour) }
            btnFavorite.setOnClickListener { onFavoriteClick(tour) }
        }
    }

    class TourDiffCallback : DiffUtil.ItemCallback<Tour>() {
        override fun areItemsTheSame(oldItem: Tour, newItem: Tour): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tour, newItem: Tour): Boolean {
            return oldItem == newItem
        }
    }
}
