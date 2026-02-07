package com.example.travelotefapp.ui.tourdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.travelotefapp.R
import com.example.travelotefapp.data.model.Tour
import com.google.android.material.chip.Chip

class TourDetailFragment : Fragment() {

    private lateinit var tour: Tour
    
    private lateinit var ivTourImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDescription: TextView
    private lateinit var chipCategory: Chip
    private lateinit var btnBook: Button
    private lateinit var btnFavorite:  ImageButton
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get tour from arguments
        arguments?.let { bundle ->
            tour = Tour(
                id = bundle.getString("tourId") ?: "",
                title = bundle.getString("tourTitle") ?: "",
                description = bundle.getString("tourDescription") ?: "",
                imageUrl = bundle.getString("tourImageUrl") ?: "",
                duration = bundle.getString("tourDuration") ?: "",
                rating = bundle.getFloat("tourRating", 0f),
                category = bundle.getString("tourCategory") ?: "",
                location = bundle.getString("tourLocation") ?: "",
                price = bundle.getDouble("tourPrice", 0.0),
                isFavorite = bundle.getBoolean("tourIsFavorite", false)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle? 
    ): View? {
        return inflater.inflate(R.layout.fragment_tour_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        displayTourDetails()
        setupClickListeners()
    }

    private fun initViews(view:  View) {
        ivTourImage = view.findViewById(R.id.ivTourImage)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvLocation = view.findViewById(R.id.tvLocation)
        tvRating = view.findViewById(R.id.tvRating)
        tvDuration = view.findViewById(R.id.tvDuration)
        tvPrice = view.findViewById(R.id.tvPrice)
        tvDescription = view.findViewById(R.id.tvDescription)
        chipCategory = view.findViewById(R.id.chipCategory)
        btnBook = view.findViewById(R.id.btnBook)
        btnFavorite = view.findViewById(R.id.btnFavorite)
        btnBack = view.findViewById(R.id.btnBack)
    }

    private fun displayTourDetails() {
        tvTitle.text = tour.title
        tvLocation.text = "📍 ${tour.location}"
        tvRating.text = "⭐ ${tour.rating}"
        tvDuration.text = "⏱️ ${tour.duration}"
        tvPrice.text = "₪${tour.price.toInt()}"
        tvDescription.text = tour.description
        chipCategory.text = tour.category
        
        // Set favorite icon
        updateFavoriteIcon()
        
        // TODO: Load image with Glide/Coil if imageUrl exists
        if (tour.imageUrl.isNotEmpty()) {
            // Glide.with(this).load(tour.imageUrl).into(ivTourImage)
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnFavorite.setOnClickListener {
            tour = tour.copy(isFavorite = !tour.isFavorite)
            updateFavoriteIcon()
            
            val message = if (tour.isFavorite) {
                "נוסף למועדפים ❤️"
            } else {
                "הוסר ממועדפים"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            
            // TODO: Update in Firebase
        }

        btnBook.setOnClickListener {
            Toast.makeText(context, "הזמנת טיול:  ${tour.title}", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to booking screen
        }
    }

  private fun updateFavoriteIcon() {
    val icon = if (tour.isFavorite) {
        android.R.drawable.star_big_on
    } else {
        android.R.drawable.star_big_off
    }
    btnFavorite.setImageResource(icon)
}

    companion object {
        fun newInstance(tour: Tour): TourDetailFragment {
            val fragment = TourDetailFragment()
            val bundle = Bundle().apply {
                putString("tourId", tour.id)
                putString("tourTitle", tour.title)
                putString("tourDescription", tour.description)
                putString("tourImageUrl", tour.imageUrl)
                putString("tourDuration", tour.duration)
                putFloat("tourRating", tour.rating)
                putString("tourCategory", tour.category)
                putString("tourLocation", tour.location)
                putDouble("tourPrice", tour.price)
                putBoolean("tourIsFavorite", tour.isFavorite)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}