package com.example.travelotefapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelotefapp.R
import com.example.travelotefapp.data.model.Tour

class TourAdapter(
    private var tours: List<Tour>,
    private val onTourClick: (Tour) -> Unit,
    private val onFavoriteClick: (Tour) -> Unit
) : RecyclerView.Adapter<TourAdapter.TourViewHolder>() {

    inner class TourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivTourImage)
        val tvTitle:  TextView = itemView.findViewById(R.id.tvTourTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvTourDescription)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)

        fun bind(tour: Tour) {
            tvTitle.text = tour.title
            tvDescription.text = tour.description
            tvDuration.text = "⏱️ ${tour.duration}"
            tvRating.text = "⭐ ${tour.rating}"
            
            // Set favorite icon
            val favoriteIcon = if (tour.isFavorite) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            }
            btnFavorite.setImageResource(favoriteIcon)

            itemView.setOnClickListener { onTourClick(tour) }
            btnFavorite.setOnClickListener { onFavoriteClick(tour) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tour, parent, false)
        return TourViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position:  Int) {
        holder.bind(tours[position])
    }

    override fun getItemCount() = tours.size

    fun updateTours(newTours: List<Tour>) {
        tours = newTours
        notifyDataSetChanged()
    }
}