package com.travelotef.app.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.chip.Chip
import com.travelotef.app.R
import com.travelotef.app.domain.model.Tour
import com.travelotef.app.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TourDetailFragment : Fragment() {

    private val viewModel: TourDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tour_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val btnFavorite: ImageButton = view.findViewById(R.id.btnFavorite)
        val btnBook: Button = view.findViewById(R.id.btnBook)

        btnBack.setOnClickListener { findNavController().navigateUp() }
        btnFavorite.setOnClickListener { viewModel.toggleFavorite() }
        btnBook.setOnClickListener {
            viewModel.getBookingUrl()?.let { url ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }

        observeViewModel(view)
    }

    private fun observeViewModel(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tourState.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> bindTour(view, state.data)
                            is UiState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel.isFavorite.collectLatest { isFav ->
                        view.findViewById<ImageButton>(R.id.btnFavorite).setImageResource(
                            if (isFav) android.R.drawable.btn_star_big_on
                            else android.R.drawable.btn_star_big_off
                        )
                    }
                }
            }
        }
    }

    private fun bindTour(view: View, tour: Tour) {
        val ivTourImage: ImageView = view.findViewById(R.id.ivTourImage)
        val chipCategory: Chip = view.findViewById(R.id.chipCategory)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)

        tvTitle.text = tour.name
        tvPrice.text = tour.formattedPrice
        tvDescription.text = if (tour.description.isNotBlank()) tour.description else tour.shortDescription

        if (tour.averageRating > 0) {
            tvRating.text = "⭐ ${String.format("%.1f", tour.averageRating)}"
        }

        chipCategory.text = tour.primaryCategory
        chipCategory.visibility = if (tour.primaryCategory.isNotEmpty()) View.VISIBLE else View.GONE

        if (tour.imageUrls.isNotEmpty()) {
            Glide.with(this)
                .load(tour.imageUrls.first())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(ivTourImage)
        }
    }
}
